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
package demetra.desktop.sa.ui;

import demetra.desktop.Config;
import demetra.desktop.design.GlobalService;
import demetra.desktop.design.SwingProperty;
import demetra.desktop.Persistable;
import demetra.desktop.actions.Configurable;
import demetra.desktop.beans.PropertyChangeSource;
import demetra.desktop.util.LazyGlobalService;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.sa.EstimationPolicyType;
import demetra.sa.SaSpecification;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
public final class DemetraSaUI implements PropertyChangeSource.WithWeakListeners, Persistable, Configurable {

    @NonNull
    public static DemetraSaUI get() {
        return LazyGlobalService.get(DemetraSaUI.class, DemetraSaUI::new);
    }

    private DemetraSaUI() {
    }

    @lombok.experimental.Delegate(types = PropertyChangeSource.class)
    protected final PropertyChangeSupport broadcaster = new PropertyChangeSupport(this);

    @SwingProperty
    public static final String SPECTRAL_LAST_YEARS_PROPERTY = "spectralLastYears";
    private static final int DEFAULT_SPECTRAL_LAST_YEARS = 0;
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
    public static final String SEASONALITY_LENGTH_PROPERTY = "seasonalityLength";
    private static final int DEFAULT_SEASONALITY_LENGTH = 10;
    private Integer seasonalityLength = DEFAULT_SEASONALITY_LENGTH;

    public Integer getSeasonalityLength() {
        return seasonalityLength;
    }

    public void setSeasonalityLength(Integer lastYears) {
        Integer old = this.seasonalityLength;
        seasonalityLength = lastYears != null ? lastYears : DEFAULT_SPECTRAL_LAST_YEARS;
        broadcaster.firePropertyChange(SEASONALITY_LENGTH_PROPERTY, old, seasonalityLength);
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
    private SaSpecification defaultSaSpec = null;

    public SaSpecification getDefaultSaSpec() {
        return defaultSaSpec;
    }

    public void setDefaultSaSpec(@NonNull SaSpecification spec) {
        SaSpecification old = this.defaultSaSpec;
        this.defaultSaSpec = spec;
        broadcaster.firePropertyChange(DEFAULT_SA_SPEC_PROPERTY, old, this.defaultSaSpec);
    }

    @SwingProperty
    public static final String SELECTED_DIAG_FIELDS_PROPERTY = "selectedDiagFields";
    private static final List<String> DEFAULT_SELECTED_DIAG_FIELDS = Collections.emptyList();
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
    private static final List<String> DEFAULT_SELECTED_SERIES_FIELDS = Collections.emptyList();
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
    private static final IntProperty SEASONALITY_LENGTH_CONFIG = IntProperty.of(SEASONALITY_LENGTH_PROPERTY, DEFAULT_SEASONALITY_LENGTH);
    private static final IntProperty STABILITY_LENGTH_CONFIG = IntProperty.of(STABILITY_LENGTH_PROPERTY, DEFAULT_STABILITY_LENGTH);
    private static final Property<EstimationPolicyType> ESTIMATION_POLICY_TYPE_CONFIG = Property.of(ESTIMATION_POLICY_TYPE_PROPERTY, DEFAULT_ESTIMATION_POLICY, Parser.onEnum(EstimationPolicyType.class), Formatter.onEnum());
    private static final Property<String> DEFAULT_SA_SPEC_CONFIG = Property.of(DEFAULT_SA_SPEC_PROPERTY, "", Parser.onString(), Formatter.onString());
    private static final Property<String[]> SELECTED_DIAG_FIELDS_CONFIG = Property.of(SELECTED_DIAG_FIELDS_PROPERTY, DEFAULT_SELECTED_DIAG_FIELDS.stream().toArray(String[]::new), Parser.onStringArray(), Formatter.onStringArray());
    private static final Property<String[]> SELECTED_SERIES_FIELDS_CONFIG = Property.of(SELECTED_SERIES_FIELDS_PROPERTY, DEFAULT_SELECTED_SERIES_FIELDS.stream().toArray(String[]::new), Parser.onStringArray(), Formatter.onStringArray());

    private static final String DOMAIN = DemetraSaUI.class.getName(), NAME = "demetra-sa", VERSION = "3.0.0";

    @Override
    public Config getConfig() {
        Config.Builder b = Config.builder(DOMAIN, NAME, VERSION);
        SPECTRAL_LAST_YEARS_CONFIG.set(b::parameter, getSpectralLastYears());
        SEASONALITY_LENGTH_CONFIG.set(b::parameter, getSeasonalityLength());
        STABILITY_LENGTH_CONFIG.set(b::parameter, getStabilityLength());
        ESTIMATION_POLICY_TYPE_CONFIG.set(b::parameter, getEstimationPolicyType());
        DEFAULT_SA_SPEC_CONFIG.set(b::parameter, idOf(getDefaultSaSpec()));
        SELECTED_DIAG_FIELDS_CONFIG.set(b::parameter, getSelectedDiagFields().toArray(n->new String[n]));
        SELECTED_SERIES_FIELDS_CONFIG.set(b::parameter, getSelectedSeriesFields().toArray(n->new String[n]));
        return b.build();
    }

    @Override
    public void setConfig(Config config) {
        Config.checkDomain(config, DOMAIN);
        setSpectralLastYears(SPECTRAL_LAST_YEARS_CONFIG.get(config::getParameter));
        setSeasonalityLength(SEASONALITY_LENGTH_CONFIG.get(config::getParameter));
        setStabilityLength(STABILITY_LENGTH_CONFIG.get(config::getParameter));
        setEstimationPolicyType(ESTIMATION_POLICY_TYPE_CONFIG.get(config::getParameter));
        setDefaultSaSpec(specOf(DEFAULT_SA_SPEC_CONFIG.get(config::getParameter)));
        setSelectedDiagFields(Arrays.asList(SELECTED_DIAG_FIELDS_CONFIG.get(config::getParameter)));
        setSelectedSeriesFields(Arrays.asList(SELECTED_SERIES_FIELDS_CONFIG.get(config::getParameter)));
    }

    @Override
    public void configure() {
        OptionsDisplayer.getDefault().open(GeneralOptionsPanelController.ID);
    }

    private SaSpecification specOf(String id) {
        if (id == null || id.length() == 0) {
            return null;
        }
        
        List<WorkspaceItem<SaSpecification>> items = WorkspaceFactory.getInstance().getActiveWorkspace().searchDocuments(SaSpecification.class);
        Optional<WorkspaceItem<SaSpecification>> fspec = items.stream().filter(c -> c.getId().toString().equals(id)).findFirst();
        return fspec.isPresent() ? fspec.get().getElement() : null;
    }

    private String idOf(SaSpecification spec) {
        // walk through the current workspace
        WorkspaceItem<SaSpecification> item = WorkspaceFactory.getInstance().getActiveWorkspace().searchDocumentByElement(spec);
        if (item == null) {
            return "";
        } else {
            return item.getId().toString();
        }
    }

}
