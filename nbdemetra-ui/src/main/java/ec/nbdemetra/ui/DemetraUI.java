/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package ec.nbdemetra.ui;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import ec.nbdemetra.core.GlobalService;
import ec.nbdemetra.ui.awt.ListenableBean;
import ec.nbdemetra.ui.properties.l2fprod.OutlierDefinitionsEditor.PrespecificiedOutliersEditor;
import ec.nbdemetra.ui.tsaction.ChartGridTsAction;
import ec.nbdemetra.ui.tsaction.ITsAction;
import ec.nbdemetra.ui.tssave.ITsSave;
import ec.satoolkit.ISaSpecification;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.satoolkit.x13.X13Specification;
import ec.tss.sa.EstimationPolicyType;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;
import ec.tstoolkit.utilities.ThreadPoolSize;
import ec.tstoolkit.utilities.ThreadPriority;
import ec.ui.ATsGrowthChart;
import ec.ui.view.AutoRegressiveSpectrumView;
import ec.util.chart.ColorScheme;
import ec.util.chart.impl.SmartColorScheme;
import ec.util.various.swing.FontAwesome;
import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.swing.Icon;
import org.netbeans.api.options.OptionsDisplayer;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 * @author Mats Maggi
 */
@GlobalService
@ServiceProvider(service = DemetraUI.class)
public class DemetraUI extends ListenableBean implements IConfigurable {

    @Nonnull
    public static DemetraUI getDefault() {
        return Lookup.getDefault().lookup(DemetraUI.class);
    }

    @Deprecated
    @Nonnull
    public static DemetraUI getInstance() {
        return getDefault();
    }

    // PROPERTIES DEFINITION
    public static final String COLOR_SCHEME_NAME_PROPERTY = "colorSchemeName";
    public static final String DATA_FORMAT_PROPERTY = "dataFormat";
    public static final String SHOW_UNAVAILABLE_TSPROVIDER_PROPERTY = "showUnavailableTsProviders";
    public static final String SHOW_TSPROVIDER_NODES_PROPERTY = "showTsProviderNodes";
    public static final String TS_ACTION_NAME_PROPERTY = "tsActionName";
    public static final String PERSIST_TOOLS_CONTENT_PROPERTY = "persistToolsContent";
    public static final String PERSIST_OPENED_DATASOURCES_PROPERTY = "persistOpenDataSources";
    public static final String BATCH_POOL_SIZE_PROPERTY = "batchPoolSize";
    public static final String BATCH_PRIORITY_PROPERTY = "batchPriority";
    public static final String GROWTH_CHART_LENGTH_PROPERTY = "growthChartLength";
    public static final String SPECTRAL_YEARS_PROPERTY = "spectralLastYears";
    public static final String STABILITY_YEARS_PROPERTY = "stabilityLastYears";
    public static final String ESTIMATION_POLICY_PROPERTY = "estimationPolicyType";
    public static final String DEFAULT_SA_SPEC_PROPERTY = "defaultSASpec";
    public static final String POPUP_MENU_ICONS_VISIBLE_PROPERTY = "menuIconsVisibility";
    public static final String PRESPECIFIED_OUTLIERS_EDITOR_PROPERTY = "prespecifiedOutliersEditor";

    // DEFAULT PROPERTIES
    static final IParam<Config, String> COLOR_SCHEME_NAME = Params.onString(SmartColorScheme.NAME, COLOR_SCHEME_NAME_PROPERTY);
    static final IParam<Config, DataFormat> DATA_FORMAT = Params.onDataFormat(new DataFormat(null, "yyyy-MM", null), "locale", "datePattern", "numberPattern");
    static final IParam<Config, Boolean> SHOW_UNAVAILABLE = Params.onBoolean(false, SHOW_UNAVAILABLE_TSPROVIDER_PROPERTY);
    static final IParam<Config, Boolean> SHOW_PROVIDER_NODES = Params.onBoolean(true, SHOW_TSPROVIDER_NODES_PROPERTY);
    static final IParam<Config, String> TS_ACTION_NAME = Params.onString(ChartGridTsAction.NAME, TS_ACTION_NAME_PROPERTY);
    static final IParam<Config, Boolean> PERSIST_TOOLS_CONTENT = Params.onBoolean(false, PERSIST_TOOLS_CONTENT_PROPERTY);
    static final IParam<Config, Boolean> PERSIST_OPENED_DATASOURCES = Params.onBoolean(false, PERSIST_OPENED_DATASOURCES_PROPERTY);
    static final IParam<Config, ThreadPoolSize> BATCH_POOL_SIZE = Params.onEnum(ThreadPoolSize.ALL_BUT_ONE, BATCH_POOL_SIZE_PROPERTY);
    static final IParam<Config, ThreadPriority> BATCH_PRIORITY = Params.onEnum(ThreadPriority.NORMAL, BATCH_PRIORITY_PROPERTY);
    static final IParam<Config, Integer> GROWTH_LAST_YEARS = Params.onInteger(ATsGrowthChart.DEFAULT_LAST_YEARS, GROWTH_CHART_LENGTH_PROPERTY);
    static final IParam<Config, Integer> SPECTRAL_LAST_YEARS = Params.onInteger(AutoRegressiveSpectrumView.DEFAULT_LAST, SPECTRAL_YEARS_PROPERTY);
    static final IParam<Config, Integer> STABILITY_LENGTH = Params.onInteger(8, STABILITY_YEARS_PROPERTY);
    static final IParam<Config, EstimationPolicyType> ESTIMATION_POLICY_TYPE = Params.onEnum(EstimationPolicyType.FreeParameters, ESTIMATION_POLICY_PROPERTY);
    static final IParam<Config, String> DEFAULT_SA_SPEC = Params.onString("tramoseats." + TramoSeatsSpecification.RSAfull.toString(), DEFAULT_SA_SPEC_PROPERTY);
    static final IParam<Config, Boolean> POPUP_MENU_ICONS_VISIBLE = Params.onBoolean(false, POPUP_MENU_ICONS_VISIBLE_PROPERTY);
    static final IParam<Config, PrespecificiedOutliersEditor> PRESPECIFIED_OUTLIERS_EDITOR = Params.onEnum(PrespecificiedOutliersEditor.CALENDAR_GRID, PRESPECIFIED_OUTLIERS_EDITOR_PROPERTY);
    // INTERNAL STUFF
    private static final Ordering<ColorScheme> COLOR_SCHEME_ORDERING = Ordering.natural().onResultOf(o -> o.getDisplayName());
    private static final Ordering<? super ITsAction> TS_ACTION_ORDERING = Ordering.natural().onResultOf(o -> o.getDisplayName());
    private static final Ordering<? super ITsSave> TS_SAVE_ORDERING = Ordering.natural().onResultOf(o -> o.getDisplayName());
    // PROPERTIES
    private final ConfigBean properties;

    public DemetraUI() {
        this.properties = new ConfigBean();
    }

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public String getColorSchemeName() {
        return properties.colorSchemeName;
    }

    public void setColorSchemeName(String colorSchemeName) {
        String old = this.properties.colorSchemeName;
        this.properties.colorSchemeName = colorSchemeName != null ? colorSchemeName : COLOR_SCHEME_NAME.defaultValue();
        firePropertyChange(COLOR_SCHEME_NAME_PROPERTY, old, this.properties.colorSchemeName);
    }

    public DataFormat getDataFormat() {
        return properties.dataFormat;
    }

    public void setDataFormat(DataFormat dataFormat) {
        DataFormat old = this.properties.dataFormat;
        this.properties.dataFormat = dataFormat != null ? dataFormat : DATA_FORMAT.defaultValue();
        firePropertyChange(DATA_FORMAT_PROPERTY, old, this.properties.dataFormat);
    }

    public boolean isShowUnavailableTsProviders() {
        return properties.showUnavailableTsProviders;
    }

    public void setShowUnavailableTsProviders(boolean show) {
        boolean old = this.properties.showUnavailableTsProviders;
        this.properties.showUnavailableTsProviders = show;
        firePropertyChange(SHOW_UNAVAILABLE_TSPROVIDER_PROPERTY, old, this.properties.showUnavailableTsProviders);
    }

    public boolean isShowTsProviderNodes() {
        return properties.showTsProviderNodes;
    }

    public void setShowTsProviderNodes(boolean show) {
        boolean old = this.properties.showTsProviderNodes;
        this.properties.showTsProviderNodes = show;
        firePropertyChange(SHOW_TSPROVIDER_NODES_PROPERTY, old, this.properties.showTsProviderNodes);
    }

    public String getTsActionName() {
        return properties.tsActionName;
    }

    public void setTsActionName(String tsActionName) {
        String old = this.properties.tsActionName;
        this.properties.tsActionName = tsActionName != null ? tsActionName : TS_ACTION_NAME.defaultValue();
        firePropertyChange(TS_ACTION_NAME_PROPERTY, old, this.properties.tsActionName);
    }

    public boolean isPersistToolsContent() {
        return properties.persistToolsContent;
    }

    public void setPersistToolsContent(boolean persistToolsContent) {
        boolean old = this.properties.persistToolsContent;
        this.properties.persistToolsContent = persistToolsContent;
        firePropertyChange(PERSIST_TOOLS_CONTENT_PROPERTY, old, this.properties.persistToolsContent);
    }

    public boolean isPersistOpenedDataSources() {
        return properties.persistOpenedDataSources;
    }

    public void setPersistOpenedDataSources(boolean persistOpenedDataSources) {
        boolean old = this.properties.persistOpenedDataSources;
        this.properties.persistOpenedDataSources = persistOpenedDataSources;
        firePropertyChange(PERSIST_OPENED_DATASOURCES_PROPERTY, old, this.properties.persistOpenedDataSources);
    }

    public ThreadPoolSize getBatchPoolSize() {
        return properties.batchPoolSize;
    }

    public void setBatchPoolSize(ThreadPoolSize batchPoolSize) {
        ThreadPoolSize old = this.properties.batchPoolSize;
        this.properties.batchPoolSize = batchPoolSize != null ? batchPoolSize : BATCH_POOL_SIZE.defaultValue();
        firePropertyChange(BATCH_POOL_SIZE_PROPERTY, old, this.properties.batchPoolSize);
    }

    public ThreadPriority getBatchPriority() {
        return properties.batchPriority;
    }

    public void setBatchPriority(ThreadPriority batchPriority) {
        ThreadPriority old = this.properties.batchPriority;
        this.properties.batchPriority = batchPriority != null ? batchPriority : BATCH_PRIORITY.defaultValue();
        firePropertyChange(BATCH_PRIORITY_PROPERTY, old, this.properties.batchPriority);
    }

    public Integer getGrowthLastYears() {
        return properties.growthLastYears;
    }

    public void setGrowthLastYears(Integer lastYears) {
        Integer old = this.properties.growthLastYears;
        properties.growthLastYears = lastYears != null ? lastYears : GROWTH_LAST_YEARS.defaultValue();
        firePropertyChange(GROWTH_CHART_LENGTH_PROPERTY, old, properties.growthLastYears);
    }

    public Integer getSpectralLastYears() {
        return properties.spectralLastYears;
    }

    public void setSpectralLastYears(Integer lastYears) {
        Integer old = this.properties.spectralLastYears;
        properties.spectralLastYears = lastYears != null ? lastYears : SPECTRAL_LAST_YEARS.defaultValue();
        firePropertyChange(SPECTRAL_YEARS_PROPERTY, old, properties.spectralLastYears);
    }

    public Integer getStabilityLength() {
        return properties.stabilityLength;
    }

    public void setStabilityLength(Integer length) {
        Integer old = this.properties.stabilityLength;
        properties.stabilityLength = length != null ? length : STABILITY_LENGTH.defaultValue();
        firePropertyChange(STABILITY_YEARS_PROPERTY, old, properties.stabilityLength);
    }

    public EstimationPolicyType getEstimationPolicyType() {
        return properties.estimationPolicyType;
    }

    public void setEstimationPolicyType(EstimationPolicyType type) {
        EstimationPolicyType old = this.properties.estimationPolicyType;
        this.properties.estimationPolicyType = type != null ? type : ESTIMATION_POLICY_TYPE.defaultValue();
        firePropertyChange(ESTIMATION_POLICY_PROPERTY, old, this.properties.estimationPolicyType);
    }

    public ISaSpecification getDefaultSASpec() {
        switch (properties.defaultSASpec) {
            case "tramoseats.RSA0":
                return TramoSeatsSpecification.RSA0;
            case "tramoseats.RSA1":
                return TramoSeatsSpecification.RSA1;
            case "tramoseats.RSA2":
                return TramoSeatsSpecification.RSA2;
            case "tramoseats.RSA3":
                return TramoSeatsSpecification.RSA3;
            case "tramoseats.RSA4":
                return TramoSeatsSpecification.RSA4;
            case "tramoseats.RSA5":
                return TramoSeatsSpecification.RSA5;
            case "tramoseats.RSAfull":
                return TramoSeatsSpecification.RSAfull;
            case "x13.X11":
                return X13Specification.RSAX11;
            case "x13.RSA0":
                return X13Specification.RSA0;
            case "x13.RSA1":
                return X13Specification.RSA1;
            case "x13.RSA2c":
                return X13Specification.RSA2;
            case "x13.RSA3":
                return X13Specification.RSA3;
            case "x13.RSA4c":
                return X13Specification.RSA4;
            case "x13.RSA5c":
                return X13Specification.RSA5;
        }
        return null;
    }

    public void setDefaultSASpec(String spec) {
        String old = this.properties.defaultSASpec;
        this.properties.defaultSASpec = spec != null ? spec : DEFAULT_SA_SPEC.defaultValue();
        firePropertyChange(DEFAULT_SA_SPEC_PROPERTY, old, this.properties.defaultSASpec);
    }

    public boolean getPopupMenuIconsVisible() {
        return properties.popupMenuIconsVisible;
    }

    public void setPopupMenuIconsVisible(boolean visible) {
        boolean old = this.properties.popupMenuIconsVisible;
        this.properties.popupMenuIconsVisible = visible;
        firePropertyChange(POPUP_MENU_ICONS_VISIBLE_PROPERTY, old, this.properties.popupMenuIconsVisible);
    }

    public PrespecificiedOutliersEditor getPrespecifiedOutliersEditor() {
        return properties.prespecifiedOutliersEditor;
    }

    public void setPrespecifiedOutliersEditor(PrespecificiedOutliersEditor prespecifiedOutliersEditor) {
        PrespecificiedOutliersEditor old = this.properties.prespecifiedOutliersEditor;
        this.properties.prespecifiedOutliersEditor = prespecifiedOutliersEditor != null ? prespecifiedOutliersEditor : PRESPECIFIED_OUTLIERS_EDITOR.defaultValue();
        firePropertyChange(PRESPECIFIED_OUTLIERS_EDITOR_PROPERTY, old, this.properties.prespecifiedOutliersEditor);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Utils">
    public ITsAction getTsAction() {
        return find(ITsAction.class, o -> o.getName(), properties.tsActionName, TS_ACTION_NAME.defaultValue());
    }

    public List<? extends ITsAction> getTsActions() {
        return TS_ACTION_ORDERING.sortedCopy(Lookup.getDefault().lookupAll(ITsAction.class));
    }

    public List<? extends ITsSave> getTsSave() {
        return TS_SAVE_ORDERING.sortedCopy(Lookup.getDefault().lookupAll(ITsSave.class));
    }

    public ColorScheme getColorScheme() {
        return find(ColorScheme.class, o -> o.getName(), properties.colorSchemeName, COLOR_SCHEME_NAME.defaultValue());
    }

    public List<? extends ColorScheme> getColorSchemes() {
        return COLOR_SCHEME_ORDERING.sortedCopy(Lookup.getDefault().lookupAll(ColorScheme.class));
    }

    public Icon getPopupMenuIcon(Icon icon) {
        return properties.popupMenuIconsVisible ? icon : null;
    }

    public Icon getPopupMenuIcon(FontAwesome icon) {
        return properties.popupMenuIconsVisible ? icon.getIcon(Color.BLACK, 13f) : null;
    }

    private static <X, Y> X find(Class<X> clazz, Function<? super X, Y> toName, Y... names) {
        Collection<? extends X> items = Lookup.getDefault().lookupAll(clazz);
        for (Y o : names) {
            Optional<? extends X> result = Iterables.tryFind(items, x -> o.equals(toName.apply(x)));
            if (result.isPresent()) {
                return result.get();
            }
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Configurable">
    @Override
    public Config getConfig() {
        return properties.toConfig();
    }

    @Override
    public void setConfig(Config config) {
        ConfigBean bean = new ConfigBean(config);
        setColorSchemeName(bean.colorSchemeName);
        setDataFormat(bean.dataFormat);
        setShowUnavailableTsProviders(bean.showUnavailableTsProviders);
        setShowTsProviderNodes(bean.showTsProviderNodes);
        setTsActionName(bean.tsActionName);
        setPersistToolsContent(bean.persistToolsContent);
        setPersistOpenedDataSources(bean.persistOpenedDataSources);
        setBatchPoolSize(bean.batchPoolSize);
        setBatchPriority(bean.batchPriority);
        setGrowthLastYears(bean.growthLastYears);
        setSpectralLastYears(bean.spectralLastYears);
        setEstimationPolicyType(bean.estimationPolicyType);
        setStabilityLength(bean.stabilityLength);
        setDefaultSASpec(bean.defaultSASpec);
        setPopupMenuIconsVisible(bean.popupMenuIconsVisible);
        setPrespecifiedOutliersEditor(bean.prespecifiedOutliersEditor);
    }

    @Override
    public Config editConfig(Config config) {
        OptionsDisplayer.getDefault().open(DemetraUIOptionsPanelController.ID);
        return getConfig();
    }

    private static class ConfigBean {

        static final String DOMAIN = DemetraUI.class.getName(), NAME = "INSTANCE", VERSION = "";
        String colorSchemeName;
        DataFormat dataFormat;
        Boolean showUnavailableTsProviders;
        Boolean showTsProviderNodes;
        String tsActionName;
        boolean persistToolsContent;
        boolean persistOpenedDataSources;
        ThreadPoolSize batchPoolSize;
        ThreadPriority batchPriority;
        Integer growthLastYears;
        Integer spectralLastYears;
        EstimationPolicyType estimationPolicyType;
        Integer stabilityLength;
        String defaultSASpec;
        boolean popupMenuIconsVisible;
        PrespecificiedOutliersEditor prespecifiedOutliersEditor;

        ConfigBean() {
            colorSchemeName = COLOR_SCHEME_NAME.defaultValue();
            dataFormat = DATA_FORMAT.defaultValue();
            showUnavailableTsProviders = SHOW_UNAVAILABLE.defaultValue();
            showTsProviderNodes = SHOW_PROVIDER_NODES.defaultValue();
            tsActionName = TS_ACTION_NAME.defaultValue();
            persistToolsContent = PERSIST_TOOLS_CONTENT.defaultValue();
            persistOpenedDataSources = PERSIST_OPENED_DATASOURCES.defaultValue();
            batchPoolSize = BATCH_POOL_SIZE.defaultValue();
            batchPriority = BATCH_PRIORITY.defaultValue();
            growthLastYears = GROWTH_LAST_YEARS.defaultValue();
            spectralLastYears = SPECTRAL_LAST_YEARS.defaultValue();
            estimationPolicyType = ESTIMATION_POLICY_TYPE.defaultValue();
            stabilityLength = STABILITY_LENGTH.defaultValue();
            defaultSASpec = DEFAULT_SA_SPEC.defaultValue();
            popupMenuIconsVisible = POPUP_MENU_ICONS_VISIBLE.defaultValue();
            prespecifiedOutliersEditor = PRESPECIFIED_OUTLIERS_EDITOR.defaultValue();
        }

        ConfigBean(Config config) {
            Preconditions.checkArgument(DOMAIN.equals(config.getDomain()), "Not produced here");
            colorSchemeName = COLOR_SCHEME_NAME.get(config);
            dataFormat = DATA_FORMAT.get(config);
            showUnavailableTsProviders = SHOW_UNAVAILABLE.get(config);
            showTsProviderNodes = SHOW_PROVIDER_NODES.get(config);
            tsActionName = TS_ACTION_NAME.get(config);
            persistToolsContent = PERSIST_TOOLS_CONTENT.get(config);
            persistOpenedDataSources = PERSIST_OPENED_DATASOURCES.get(config);
            batchPoolSize = BATCH_POOL_SIZE.get(config);
            batchPriority = BATCH_PRIORITY.get(config);
            growthLastYears = GROWTH_LAST_YEARS.get(config);
            spectralLastYears = SPECTRAL_LAST_YEARS.get(config);
            estimationPolicyType = ESTIMATION_POLICY_TYPE.get(config);
            stabilityLength = STABILITY_LENGTH.get(config);
            defaultSASpec = DEFAULT_SA_SPEC.get(config);
            popupMenuIconsVisible = POPUP_MENU_ICONS_VISIBLE.get(config);
            prespecifiedOutliersEditor = PRESPECIFIED_OUTLIERS_EDITOR.get(config);
        }

        Config toConfig() {
            Config.Builder b = Config.builder(DOMAIN, NAME, VERSION);
            COLOR_SCHEME_NAME.set(b, colorSchemeName);
            DATA_FORMAT.set(b, dataFormat);
            SHOW_UNAVAILABLE.set(b, showUnavailableTsProviders);
            SHOW_PROVIDER_NODES.set(b, showTsProviderNodes);
            TS_ACTION_NAME.set(b, tsActionName);
            PERSIST_TOOLS_CONTENT.set(b, persistToolsContent);
            PERSIST_OPENED_DATASOURCES.set(b, persistOpenedDataSources);
            BATCH_POOL_SIZE.set(b, batchPoolSize);
            BATCH_PRIORITY.set(b, batchPriority);
            GROWTH_LAST_YEARS.set(b, growthLastYears);
            SPECTRAL_LAST_YEARS.set(b, spectralLastYears);
            ESTIMATION_POLICY_TYPE.set(b, estimationPolicyType);
            STABILITY_LENGTH.set(b, stabilityLength);
            DEFAULT_SA_SPEC.set(b, defaultSASpec);
            POPUP_MENU_ICONS_VISIBLE.set(b, popupMenuIconsVisible);
            PRESPECIFIED_OUTLIERS_EDITOR.set(b, prespecifiedOutliersEditor);
            return b.build();
        }
    }
    //</editor-fold>
}
