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
import demetra.desktop.design.GlobalService;
import demetra.desktop.design.SwingProperty;
import demetra.ui.Persistable;
import demetra.ui.actions.Configurable;
import demetra.ui.beans.PropertyChangeSource;
import demetra.ui.util.LazyGlobalService;
import ec.nbdemetra.ui.properties.l2fprod.OutlierDefinitionsEditor.PrespecificiedOutliersEditor;
import ec.satoolkit.ISaSpecification;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.satoolkit.x13.X13Specification;
import ec.tss.sa.EstimationPolicyType;
import ec.tss.sa.output.BasicConfiguration;
import ec.tstoolkit.utilities.Jdk6.Collections;
import ec.ui.view.AutoRegressiveSpectrumView;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.List;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.IntProperty;
import nbbrd.io.text.Parser;
import nbbrd.io.text.Property;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.netbeans.api.options.OptionsDisplayer;

/**
 *
 * @author Philippe Charles
 * @author Mats Maggi
 */
@GlobalService
public final class DemetraUI implements PropertyChangeSource.WithWeakListeners, Persistable, Configurable {

    @NonNull
    public static DemetraUI getDefault() {
        return LazyGlobalService.get(DemetraUI.class, DemetraUI::new);
    }

    private DemetraUI() {
    }

    @lombok.experimental.Delegate(types = PropertyChangeSource.class)
    protected final PropertyChangeSupport broadcaster = new PropertyChangeSupport(this);

    @SwingProperty
    public static final String SPECTRAL_LAST_YEARS_PROPERTY = "spectralLastYears";
    private static final int DEFAULT_SPECTRAL_LAST_YEARS = AutoRegressiveSpectrumView.DEFAULT_LAST;
    private Integer spectralLastYears = DEFAULT_SPECTRAL_LAST_YEARS;

    public Integer getSpectralLastYears() {
        return spectralLastYears;
    }

    public void setSpectralLastYears(Integer lastYears) {
        Integer old = this.spectralLastYears;
        spectralLastYears = lastYears != null ? lastYears : DEFAULT_SPECTRAL_LAST_YEARS;
        broadcaster.firePropertyChange(SPECTRAL_LAST_YEARS_PROPERTY, old, spectralLastYears);
    }

    @SwingProperty
    public static final String STABILITY_LENGTH_PROPERTY = "stabilityLength";
    private static final int DEFAULT_STABILITY_LENGTH = 8;
    private Integer stabilityLength = DEFAULT_STABILITY_LENGTH;

    public Integer getStabilityLength() {
        return stabilityLength;
    }

    public void setStabilityLength(Integer length) {
        Integer old = this.stabilityLength;
        stabilityLength = length != null ? length : DEFAULT_STABILITY_LENGTH;
        broadcaster.firePropertyChange(STABILITY_LENGTH_PROPERTY, old, stabilityLength);
    }

    @SwingProperty
    public static final String ESTIMATION_POLICY_TYPE_PROPERTY = "estimationPolicyType";
    private static final EstimationPolicyType DEFAULT_ESTIMATION_POLICY = EstimationPolicyType.FreeParameters;
    private EstimationPolicyType estimationPolicyType = DEFAULT_ESTIMATION_POLICY;

    public EstimationPolicyType getEstimationPolicyType() {
        return estimationPolicyType;
    }

    public void setEstimationPolicyType(EstimationPolicyType type) {
        EstimationPolicyType old = this.estimationPolicyType;
        this.estimationPolicyType = type != null ? type : DEFAULT_ESTIMATION_POLICY;
        broadcaster.firePropertyChange(ESTIMATION_POLICY_TYPE_PROPERTY, old, this.estimationPolicyType);
    }

    @SwingProperty
    public static final String DEFAULT_SA_SPEC_PROPERTY = "defaultSaSpec";
    private static final String DEFAULT_DETAULT_SA_SPEC = "tramoseats." + TramoSeatsSpecification.RSAfull;
    private String defaultSaSpec = DEFAULT_DETAULT_SA_SPEC;

    public String getDefaultSaSpec() {
        return defaultSaSpec;
    }

    public void setDefaultSaSpec(String spec) {
        String old = this.defaultSaSpec;
        this.defaultSaSpec = spec != null ? spec : DEFAULT_DETAULT_SA_SPEC;
        broadcaster.firePropertyChange(DEFAULT_SA_SPEC_PROPERTY, old, this.defaultSaSpec);
    }

    @SwingProperty
    public static final String PRESPECIFIED_OUTLIERS_EDITOR_PROPERTY = "prespecifiedOutliersEditor";
    private static final PrespecificiedOutliersEditor DEFAULT_PRESPECIFIED_OUTLIERS_EDITOR = PrespecificiedOutliersEditor.CALENDAR_GRID;
    private PrespecificiedOutliersEditor prespecifiedOutliersEditor = DEFAULT_PRESPECIFIED_OUTLIERS_EDITOR;

    public PrespecificiedOutliersEditor getPrespecifiedOutliersEditor() {
        return prespecifiedOutliersEditor;
    }

    public void setPrespecifiedOutliersEditor(PrespecificiedOutliersEditor prespecifiedOutliersEditor) {
        PrespecificiedOutliersEditor old = this.prespecifiedOutliersEditor;
        this.prespecifiedOutliersEditor = prespecifiedOutliersEditor != null ? prespecifiedOutliersEditor : DEFAULT_PRESPECIFIED_OUTLIERS_EDITOR;
        broadcaster.firePropertyChange(PRESPECIFIED_OUTLIERS_EDITOR_PROPERTY, old, this.prespecifiedOutliersEditor);
    }

    @SwingProperty
    public static final String SELECTED_DIAG_FIELDS_PROPERTY = "selectedDiagFields";
    private static final List<String> DEFAULT_SELECTED_DIAG_FIELDS = BasicConfiguration.allSingleSaDetails(false);
    private List<String> selectedDiagFields = DEFAULT_SELECTED_DIAG_FIELDS;

    public List<String> getSelectedDiagFields() {
        return this.selectedDiagFields;
    }

    public void setSelectedDiagFields(List<String> fields) {
        List<String> old = this.selectedDiagFields;
        this.selectedDiagFields = fields;
        broadcaster.firePropertyChange(SELECTED_DIAG_FIELDS_PROPERTY, old, this.selectedDiagFields);
    }

    @SwingProperty
    public static final String SELECTED_SERIES_FIELDS_PROPERTY = "selectedSeriesFields";
    private static final List<String> DEFAULT_SELECTED_SERIES_FIELDS = BasicConfiguration.allSaSeries(false);
    private List<String> selectedSeriesFields = DEFAULT_SELECTED_SERIES_FIELDS;

    public List<String> getSelectedSeriesFields() {
        return this.selectedSeriesFields;
    }

    public void setSelectedSeriesFields(List<String> fields) {
        List<String> old = this.selectedSeriesFields;
        this.selectedSeriesFields = fields;
        broadcaster.firePropertyChange(SELECTED_SERIES_FIELDS_PROPERTY, old, this.selectedSeriesFields);
    }

    private static final IntProperty SPECTRAL_LAST_YEARS_CONFIG = IntProperty.of(SPECTRAL_LAST_YEARS_PROPERTY, DEFAULT_SPECTRAL_LAST_YEARS);
    private static final IntProperty STABILITY_LENGTH_CONFIG = IntProperty.of(STABILITY_LENGTH_PROPERTY, DEFAULT_STABILITY_LENGTH);
    private static final Property<EstimationPolicyType> ESTIMATION_POLICY_TYPE_CONFIG = Property.of(ESTIMATION_POLICY_TYPE_PROPERTY, DEFAULT_ESTIMATION_POLICY, Parser.onEnum(EstimationPolicyType.class), Formatter.onEnum());
    private static final Property<String> DEFAULT_SA_SPEC_CONFIG = Property.of(DEFAULT_SA_SPEC_PROPERTY, DEFAULT_DETAULT_SA_SPEC, Parser.onString(), Formatter.onString());
    private static final Property<PrespecificiedOutliersEditor> PRESPECIFIED_OUTLIERS_EDITOR_CONFIG = Property.of(PRESPECIFIED_OUTLIERS_EDITOR_PROPERTY, DEFAULT_PRESPECIFIED_OUTLIERS_EDITOR, Parser.onEnum(PrespecificiedOutliersEditor.class), Formatter.onEnum());
    private static final Property<String[]> SELECTED_DIAG_FIELDS_CONFIG = Property.of(SELECTED_DIAG_FIELDS_PROPERTY, DEFAULT_SELECTED_DIAG_FIELDS.stream().toArray(String[]::new), Parser.onStringArray(), Formatter.onStringArray());
    private static final Property<String[]> SELECTED_SERIES_FIELDS_CONFIG = Property.of(SELECTED_SERIES_FIELDS_PROPERTY, DEFAULT_SELECTED_SERIES_FIELDS.stream().toArray(String[]::new), Parser.onStringArray(), Formatter.onStringArray());

    @Override
    public Config getConfig() {
        Config.Builder b = Config.builder(DOMAIN, NAME, VERSION);
        SPECTRAL_LAST_YEARS_CONFIG.set(b::parameter, getSpectralLastYears());
        ESTIMATION_POLICY_TYPE_CONFIG.set(b::parameter, getEstimationPolicyType());
        STABILITY_LENGTH_CONFIG.set(b::parameter, getStabilityLength());
        DEFAULT_SA_SPEC_CONFIG.set(b::parameter, getDefaultSaSpec());
        PRESPECIFIED_OUTLIERS_EDITOR_CONFIG.set(b::parameter, getPrespecifiedOutliersEditor());
        SELECTED_DIAG_FIELDS_CONFIG.set(b::parameter, Collections.toArray(getSelectedDiagFields(), String.class));
        SELECTED_SERIES_FIELDS_CONFIG.set(b::parameter, Collections.toArray(getSelectedSeriesFields(), String.class));
        return b.build();
    }

    @Override
    public void setConfig(Config config) {
        Config.checkDomain(config, DOMAIN);
        setSpectralLastYears(SPECTRAL_LAST_YEARS_CONFIG.get(config::getParameter));
        setEstimationPolicyType(ESTIMATION_POLICY_TYPE_CONFIG.get(config::getParameter));
        setStabilityLength(STABILITY_LENGTH_CONFIG.get(config::getParameter));
        setDefaultSaSpec(DEFAULT_SA_SPEC_CONFIG.get(config::getParameter));
        setPrespecifiedOutliersEditor(PRESPECIFIED_OUTLIERS_EDITOR_CONFIG.get(config::getParameter));
        setSelectedDiagFields(Arrays.asList(SELECTED_DIAG_FIELDS_CONFIG.get(config::getParameter)));
        setSelectedSeriesFields(Arrays.asList(SELECTED_SERIES_FIELDS_CONFIG.get(config::getParameter)));
    }

    @Override
    public void configure() {
        OptionsDisplayer.getDefault().open(DemetraUIOptionsPanelController.ID);
    }

    public ISaSpecification getDefaultSASpecInstance() {
        switch (defaultSaSpec) {
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

    private static final String DOMAIN = DemetraUI.class.getName(), NAME = "INSTANCE", VERSION = "";
}
