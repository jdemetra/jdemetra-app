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

import demetra.ui.Config;
import com.google.common.base.Preconditions;
import demetra.ui.GlobalService;
import demetra.ui.Persistable;
import demetra.ui.actions.Configurable;
import demetra.ui.beans.PropertyChangeSource;
import ec.nbdemetra.ui.properties.l2fprod.OutlierDefinitionsEditor.PrespecificiedOutliersEditor;
import internal.ui.ChartGridTsAction;
import ec.satoolkit.ISaSpecification;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.satoolkit.x13.X13Specification;
import ec.tss.sa.EstimationPolicyType;
import ec.tss.sa.output.BasicConfiguration;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tstoolkit.utilities.Jdk6.Collections;
import ec.tstoolkit.utilities.ThreadPoolSize;
import ec.tstoolkit.utilities.ThreadPriority;
import demetra.ui.components.JTsGrowthChart;
import ec.ui.view.AutoRegressiveSpectrumView;
import ec.util.chart.ColorScheme;
import ec.util.chart.impl.SmartColorScheme;
import ec.util.various.swing.FontAwesome;
import java.awt.Color;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.Icon;
import nbbrd.io.text.BooleanProperty;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.IntProperty;
import nbbrd.io.text.Parser;
import nbbrd.io.text.Property;
import org.netbeans.api.options.OptionsDisplayer;
import org.openide.util.Lookup;

/**
 *
 * @author Philippe Charles
 * @author Mats Maggi
 */
@GlobalService
public final class DemetraUI implements PropertyChangeSource, Configurable, Persistable {

    private static final DemetraUI INSTANCE = new DemetraUI();

    @NonNull
    public static DemetraUI getDefault() {
        return INSTANCE;
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
    public static final String SELECTED_DIAG_FIELDS_PROPERTY = "selectedDiagnosticsFields";
    public static final String SELECTED_SERIES_FIELDS_PROPERTY = "selectedSeriesFields";
    public static final String HTML_ZOOM_RATIO_PROPERTY = "htmlZoomRatio";

    // DEFAULT PROPERTIES
    static final Property<String> COLOR_SCHEME_NAME = Property.of(COLOR_SCHEME_NAME_PROPERTY, SmartColorScheme.NAME, Parser.onString(), Formatter.onString());
    static final DataFormatParam DATA_FORMAT = new DataFormatParam(new DataFormat(null, "yyyy-MM", null), "locale", "datePattern", "numberPattern");
    static final BooleanProperty SHOW_UNAVAILABLE = BooleanProperty.of(SHOW_UNAVAILABLE_TSPROVIDER_PROPERTY, false);
    static final BooleanProperty SHOW_PROVIDER_NODES = BooleanProperty.of(SHOW_TSPROVIDER_NODES_PROPERTY, true);
    static final Property<String> TS_ACTION_NAME = Property.of(TS_ACTION_NAME_PROPERTY, ChartGridTsAction.NAME, Parser.onString(), Formatter.onString());
    static final BooleanProperty PERSIST_TOOLS_CONTENT = BooleanProperty.of(PERSIST_TOOLS_CONTENT_PROPERTY, false);
    static final BooleanProperty PERSIST_OPENED_DATASOURCES = BooleanProperty.of(PERSIST_OPENED_DATASOURCES_PROPERTY, false);
    static final Property<ThreadPoolSize> BATCH_POOL_SIZE = Property.of(BATCH_POOL_SIZE_PROPERTY, ThreadPoolSize.ALL_BUT_ONE, Parser.onEnum(ThreadPoolSize.class), Formatter.onEnum());
    static final Property<ThreadPriority> BATCH_PRIORITY = Property.of(BATCH_PRIORITY_PROPERTY, ThreadPriority.NORMAL, Parser.onEnum(ThreadPriority.class), Formatter.onEnum());
    static final IntProperty GROWTH_LAST_YEARS = IntProperty.of(GROWTH_CHART_LENGTH_PROPERTY, JTsGrowthChart.DEFAULT_LAST_YEARS);
    static final IntProperty SPECTRAL_LAST_YEARS = IntProperty.of(SPECTRAL_YEARS_PROPERTY, AutoRegressiveSpectrumView.DEFAULT_LAST);
    static final IntProperty STABILITY_LENGTH = IntProperty.of(STABILITY_YEARS_PROPERTY, 8);
    static final Property<EstimationPolicyType> ESTIMATION_POLICY_TYPE = Property.of(ESTIMATION_POLICY_PROPERTY, EstimationPolicyType.FreeParameters, Parser.onEnum(EstimationPolicyType.class), Formatter.onEnum());
    static final Property<String> DEFAULT_SA_SPEC = Property.of(DEFAULT_SA_SPEC_PROPERTY, "tramoseats." + TramoSeatsSpecification.RSAfull.toString(), Parser.onString(), Formatter.onString());
    static final BooleanProperty POPUP_MENU_ICONS_VISIBLE = BooleanProperty.of(POPUP_MENU_ICONS_VISIBLE_PROPERTY, false);
    static final Property<PrespecificiedOutliersEditor> PRESPECIFIED_OUTLIERS_EDITOR = Property.of(PRESPECIFIED_OUTLIERS_EDITOR_PROPERTY, PrespecificiedOutliersEditor.CALENDAR_GRID, Parser.onEnum(PrespecificiedOutliersEditor.class), Formatter.onEnum());
    static final Property<String[]> SELECTED_DIAG_FIELDS = Property.of(SELECTED_DIAG_FIELDS_PROPERTY,
            BasicConfiguration.allSingleSaDetails(false).stream().toArray(String[]::new), Parser.onStringArray(), Formatter.onStringArray());
    static final Property<String[]> SELECTED_SERIES_FIELDS = Property.of(SELECTED_SERIES_FIELDS_PROPERTY,
            BasicConfiguration.allSaSeries(false).stream().toArray(String[]::new), Parser.onStringArray(), Formatter.onStringArray());
    static final IntProperty HTML_ZOOM_RATIO = IntProperty.of(HTML_ZOOM_RATIO_PROPERTY, 100);

    @lombok.experimental.Delegate(types = PropertyChangeSource.class)
    private final PropertyChangeSupport broadcaster = new PropertyChangeSupport(this);

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
        this.properties.colorSchemeName = colorSchemeName != null ? colorSchemeName : COLOR_SCHEME_NAME.getDefaultValue();
        broadcaster.firePropertyChange(COLOR_SCHEME_NAME_PROPERTY, old, this.properties.colorSchemeName);
    }

    public DataFormat getDataFormat() {
        return properties.dataFormat;
    }

    public void setDataFormat(DataFormat dataFormat) {
        DataFormat old = this.properties.dataFormat;
        this.properties.dataFormat = dataFormat != null ? dataFormat : DATA_FORMAT.getDefaultValue();
        broadcaster.firePropertyChange(DATA_FORMAT_PROPERTY, old, this.properties.dataFormat);
    }

    public boolean isShowUnavailableTsProviders() {
        return properties.showUnavailableTsProviders;
    }

    public void setShowUnavailableTsProviders(boolean show) {
        boolean old = this.properties.showUnavailableTsProviders;
        this.properties.showUnavailableTsProviders = show;
        broadcaster.firePropertyChange(SHOW_UNAVAILABLE_TSPROVIDER_PROPERTY, old, this.properties.showUnavailableTsProviders.booleanValue());
    }

    public boolean isShowTsProviderNodes() {
        return properties.showTsProviderNodes;
    }

    public void setShowTsProviderNodes(boolean show) {
        boolean old = this.properties.showTsProviderNodes;
        this.properties.showTsProviderNodes = show;
        broadcaster.firePropertyChange(SHOW_TSPROVIDER_NODES_PROPERTY, old, this.properties.showTsProviderNodes.booleanValue());
    }

    public String getTsActionName() {
        return properties.tsActionName;
    }

    public void setTsActionName(String tsActionName) {
        String old = this.properties.tsActionName;
        this.properties.tsActionName = tsActionName != null ? tsActionName : TS_ACTION_NAME.getDefaultValue();
        broadcaster.firePropertyChange(TS_ACTION_NAME_PROPERTY, old, this.properties.tsActionName);
    }

    public boolean isPersistToolsContent() {
        return properties.persistToolsContent;
    }

    public void setPersistToolsContent(boolean persistToolsContent) {
        boolean old = this.properties.persistToolsContent;
        this.properties.persistToolsContent = persistToolsContent;
        broadcaster.firePropertyChange(PERSIST_TOOLS_CONTENT_PROPERTY, old, this.properties.persistToolsContent);
    }

    public boolean isPersistOpenedDataSources() {
        return properties.persistOpenedDataSources;
    }

    public void setPersistOpenedDataSources(boolean persistOpenedDataSources) {
        boolean old = this.properties.persistOpenedDataSources;
        this.properties.persistOpenedDataSources = persistOpenedDataSources;
        broadcaster.firePropertyChange(PERSIST_OPENED_DATASOURCES_PROPERTY, old, this.properties.persistOpenedDataSources);
    }

    public ThreadPoolSize getBatchPoolSize() {
        return properties.batchPoolSize;
    }

    public void setBatchPoolSize(ThreadPoolSize batchPoolSize) {
        ThreadPoolSize old = this.properties.batchPoolSize;
        this.properties.batchPoolSize = batchPoolSize != null ? batchPoolSize : BATCH_POOL_SIZE.getDefaultValue();
        broadcaster.firePropertyChange(BATCH_POOL_SIZE_PROPERTY, old, this.properties.batchPoolSize);
    }

    public ThreadPriority getBatchPriority() {
        return properties.batchPriority;
    }

    public void setBatchPriority(ThreadPriority batchPriority) {
        ThreadPriority old = this.properties.batchPriority;
        this.properties.batchPriority = batchPriority != null ? batchPriority : BATCH_PRIORITY.getDefaultValue();
        broadcaster.firePropertyChange(BATCH_PRIORITY_PROPERTY, old, this.properties.batchPriority);
    }

    public Integer getGrowthLastYears() {
        return properties.growthLastYears;
    }

    public void setGrowthLastYears(Integer lastYears) {
        Integer old = this.properties.growthLastYears;
        properties.growthLastYears = lastYears != null ? lastYears : GROWTH_LAST_YEARS.getDefaultValue();
        broadcaster.firePropertyChange(GROWTH_CHART_LENGTH_PROPERTY, old, properties.growthLastYears);
    }

    public Integer getSpectralLastYears() {
        return properties.spectralLastYears;
    }

    public void setSpectralLastYears(Integer lastYears) {
        Integer old = this.properties.spectralLastYears;
        properties.spectralLastYears = lastYears != null ? lastYears : SPECTRAL_LAST_YEARS.getDefaultValue();
        broadcaster.firePropertyChange(SPECTRAL_YEARS_PROPERTY, old, properties.spectralLastYears);
    }

    public Integer getStabilityLength() {
        return properties.stabilityLength;
    }

    public void setStabilityLength(Integer length) {
        Integer old = this.properties.stabilityLength;
        properties.stabilityLength = length != null ? length : STABILITY_LENGTH.getDefaultValue();
        broadcaster.firePropertyChange(STABILITY_YEARS_PROPERTY, old, properties.stabilityLength);
    }

    public EstimationPolicyType getEstimationPolicyType() {
        return properties.estimationPolicyType;
    }

    public void setEstimationPolicyType(EstimationPolicyType type) {
        EstimationPolicyType old = this.properties.estimationPolicyType;
        this.properties.estimationPolicyType = type != null ? type : ESTIMATION_POLICY_TYPE.getDefaultValue();
        broadcaster.firePropertyChange(ESTIMATION_POLICY_PROPERTY, old, this.properties.estimationPolicyType);
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
        this.properties.defaultSASpec = spec != null ? spec : DEFAULT_SA_SPEC.getDefaultValue();
        broadcaster.firePropertyChange(DEFAULT_SA_SPEC_PROPERTY, old, this.properties.defaultSASpec);
    }

    public boolean getPopupMenuIconsVisible() {
        return properties.popupMenuIconsVisible;
    }

    public void setPopupMenuIconsVisible(boolean visible) {
        boolean old = this.properties.popupMenuIconsVisible;
        this.properties.popupMenuIconsVisible = visible;
        broadcaster.firePropertyChange(POPUP_MENU_ICONS_VISIBLE_PROPERTY, old, this.properties.popupMenuIconsVisible);
    }

    public PrespecificiedOutliersEditor getPrespecifiedOutliersEditor() {
        return properties.prespecifiedOutliersEditor;
    }

    public void setPrespecifiedOutliersEditor(PrespecificiedOutliersEditor prespecifiedOutliersEditor) {
        PrespecificiedOutliersEditor old = this.properties.prespecifiedOutliersEditor;
        this.properties.prespecifiedOutliersEditor = prespecifiedOutliersEditor != null ? prespecifiedOutliersEditor : PRESPECIFIED_OUTLIERS_EDITOR.getDefaultValue();
        broadcaster.firePropertyChange(PRESPECIFIED_OUTLIERS_EDITOR_PROPERTY, old, this.properties.prespecifiedOutliersEditor);
    }

    public List<String> getSelectedDiagFields() {
        return this.properties.selectedDiagFields;
    }

    public void setSelectedDiagFields(List<String> fields) {
        List<String> old = this.properties.selectedDiagFields;
        this.properties.selectedDiagFields = fields;
        broadcaster.firePropertyChange(SELECTED_DIAG_FIELDS_PROPERTY, old, this.properties.selectedDiagFields);
    }

    public List<String> getSelectedSeriesFields() {
        return this.properties.selectedSeriesFields;
    }

    public void setSelectedSeriesFields(List<String> fields) {
        List<String> old = this.properties.selectedSeriesFields;
        this.properties.selectedSeriesFields = fields;
        broadcaster.firePropertyChange(SELECTED_SERIES_FIELDS_PROPERTY, old, this.properties.selectedSeriesFields);
    }

    public int getHtmlZoomRatio() {
        return this.properties.htmlZoomRatio;
    }

    public void setHtmlZoomRatio(int htmlZoomRatio) {
        int old = this.properties.htmlZoomRatio;
        this.properties.htmlZoomRatio = htmlZoomRatio >= 10 && htmlZoomRatio <= 200 ? htmlZoomRatio : 100;
        broadcaster.firePropertyChange(HTML_ZOOM_RATIO_PROPERTY, old, this.properties.htmlZoomRatio);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Utils">
    public ColorScheme getColorScheme() {
        return find(ColorScheme.class, ColorScheme::getName, properties.colorSchemeName, COLOR_SCHEME_NAME.getDefaultValue());
    }

    public List<? extends ColorScheme> getColorSchemes() {
        return Lookup.getDefault().lookupAll(ColorScheme.class)
                .stream()
                .sorted(Comparator.comparing(ColorScheme::getDisplayName))
                .collect(Collectors.toList());
    }

    public Icon getPopupMenuIcon(Icon icon) {
        return properties.popupMenuIconsVisible ? icon : null;
    }

    public Icon getPopupMenuIcon(FontAwesome icon) {
        return properties.popupMenuIconsVisible ? icon.getIcon(Color.BLACK, 13f) : null;
    }

    private static <X, Y> X find(Class<X> clazz, Function<? super X, Y> toName, Y... names) {
        Collection<? extends X> items = Lookup.getDefault().lookupAll(clazz);
        return Stream.of(names)
                .flatMap(o -> items.stream().filter(x -> o.equals(toName.apply(x))))
                .findFirst()
                .orElse(null);
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
        setSelectedDiagFields(bean.selectedDiagFields);
        setSelectedSeriesFields(bean.selectedSeriesFields);
        setHtmlZoomRatio(bean.htmlZoomRatio);
    }

    @Override
    public void configure() {
        OptionsDisplayer.getDefault().open(DemetraUIOptionsPanelController.ID);
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
        List<String> selectedDiagFields;
        List<String> selectedSeriesFields;
        int htmlZoomRatio;

        ConfigBean() {
            colorSchemeName = COLOR_SCHEME_NAME.getDefaultValue();
            dataFormat = DATA_FORMAT.getDefaultValue();
            showUnavailableTsProviders = SHOW_UNAVAILABLE.isDefaultValue();
            showTsProviderNodes = SHOW_PROVIDER_NODES.isDefaultValue();
            tsActionName = TS_ACTION_NAME.getDefaultValue();
            persistToolsContent = PERSIST_TOOLS_CONTENT.isDefaultValue();
            persistOpenedDataSources = PERSIST_OPENED_DATASOURCES.isDefaultValue();
            batchPoolSize = BATCH_POOL_SIZE.getDefaultValue();
            batchPriority = BATCH_PRIORITY.getDefaultValue();
            growthLastYears = GROWTH_LAST_YEARS.getDefaultValue();
            spectralLastYears = SPECTRAL_LAST_YEARS.getDefaultValue();
            estimationPolicyType = ESTIMATION_POLICY_TYPE.getDefaultValue();
            stabilityLength = STABILITY_LENGTH.getDefaultValue();
            defaultSASpec = DEFAULT_SA_SPEC.getDefaultValue();
            popupMenuIconsVisible = POPUP_MENU_ICONS_VISIBLE.isDefaultValue();
            prespecifiedOutliersEditor = PRESPECIFIED_OUTLIERS_EDITOR.getDefaultValue();
            selectedDiagFields = Arrays.asList(SELECTED_DIAG_FIELDS.getDefaultValue());
            selectedSeriesFields = Arrays.asList(SELECTED_SERIES_FIELDS.getDefaultValue());
            htmlZoomRatio = HTML_ZOOM_RATIO.getDefaultValue();
        }

        ConfigBean(Config config) {
            Preconditions.checkArgument(DOMAIN.equals(config.getDomain()), "Not produced here");
            colorSchemeName = COLOR_SCHEME_NAME.get(config::getParameter);
            dataFormat = DATA_FORMAT.get(config);
            showUnavailableTsProviders = SHOW_UNAVAILABLE.get(config::getParameter);
            showTsProviderNodes = SHOW_PROVIDER_NODES.get(config::getParameter);
            tsActionName = TS_ACTION_NAME.get(config::getParameter);
            persistToolsContent = PERSIST_TOOLS_CONTENT.get(config::getParameter);
            persistOpenedDataSources = PERSIST_OPENED_DATASOURCES.get(config::getParameter);
            batchPoolSize = BATCH_POOL_SIZE.get(config::getParameter);
            batchPriority = BATCH_PRIORITY.get(config::getParameter);
            growthLastYears = GROWTH_LAST_YEARS.get(config::getParameter);
            spectralLastYears = SPECTRAL_LAST_YEARS.get(config::getParameter);
            estimationPolicyType = ESTIMATION_POLICY_TYPE.get(config::getParameter);
            stabilityLength = STABILITY_LENGTH.get(config::getParameter);
            defaultSASpec = DEFAULT_SA_SPEC.get(config::getParameter);
            popupMenuIconsVisible = POPUP_MENU_ICONS_VISIBLE.get(config::getParameter);
            prespecifiedOutliersEditor = PRESPECIFIED_OUTLIERS_EDITOR.get(config::getParameter);
            selectedDiagFields = Arrays.asList(SELECTED_DIAG_FIELDS.get(config::getParameter));
            selectedSeriesFields = Arrays.asList(SELECTED_SERIES_FIELDS.get(config::getParameter));
            htmlZoomRatio = HTML_ZOOM_RATIO.get(config::getParameter);
        }

        Config toConfig() {
            Config.Builder b = Config.builder(DOMAIN, NAME, VERSION);
            COLOR_SCHEME_NAME.set(b::parameter, colorSchemeName);
            DATA_FORMAT.set(b, dataFormat);
            SHOW_UNAVAILABLE.set(b::parameter, showUnavailableTsProviders);
            SHOW_PROVIDER_NODES.set(b::parameter, showTsProviderNodes);
            TS_ACTION_NAME.set(b::parameter, tsActionName);
            PERSIST_TOOLS_CONTENT.set(b::parameter, persistToolsContent);
            PERSIST_OPENED_DATASOURCES.set(b::parameter, persistOpenedDataSources);
            BATCH_POOL_SIZE.set(b::parameter, batchPoolSize);
            BATCH_PRIORITY.set(b::parameter, batchPriority);
            GROWTH_LAST_YEARS.set(b::parameter, growthLastYears);
            SPECTRAL_LAST_YEARS.set(b::parameter, spectralLastYears);
            ESTIMATION_POLICY_TYPE.set(b::parameter, estimationPolicyType);
            STABILITY_LENGTH.set(b::parameter, stabilityLength);
            DEFAULT_SA_SPEC.set(b::parameter, defaultSASpec);
            POPUP_MENU_ICONS_VISIBLE.set(b::parameter, popupMenuIconsVisible);
            PRESPECIFIED_OUTLIERS_EDITOR.set(b::parameter, prespecifiedOutliersEditor);
            SELECTED_DIAG_FIELDS.set(b::parameter, Collections.toArray(selectedDiagFields, String.class));
            SELECTED_SERIES_FIELDS.set(b::parameter, Collections.toArray(selectedSeriesFields, String.class));
            HTML_ZOOM_RATIO.set(b::parameter, htmlZoomRatio);
            return b.build();
        }
    }
    //</editor-fold>
}
