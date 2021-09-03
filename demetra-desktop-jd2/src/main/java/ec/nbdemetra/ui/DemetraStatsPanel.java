/*
 * Copyright 2016 National Bank of Belgium
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

import demetra.desktop.actions.Configurable;
import ec.nbdemetra.sa.output.INbOutputFactory;
import demetra.desktop.nodes.AbstractNodeBuilder;
import demetra.desktop.nodes.NamedServiceNode;
import ec.nbdemetra.ws.ui.JSpecSelectionComponent;
import ec.satoolkit.ISaSpecification;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tss.sa.EstimationPolicyType;
import ec.tss.sa.SaManager;
import ec.tss.sa.output.BasicConfiguration;
import ec.util.list.swing.JListSelection;
import java.awt.Dimension;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.DropDownButtonFactory;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import demetra.desktop.actions.Resetable;
import ec.nbdemetra.ui.sa.SaDiagnosticsFactoryBuddyLoader;

/**
 *
 * @author Mats Maggi
 */
final class DemetraStatsPanel extends javax.swing.JPanel {

    private final DemetraStatsOptionsPanelController controller;
    private final JPopupMenu specPopup = new JPopupMenu();
    private final JSpecSelectionComponent specComponent = new JSpecSelectionComponent(true);

    private final JListSelection<String> fieldSelectionComponent = new JListSelection<>();

    private List<String> selectedDiagFields = new ArrayList<>();
    private final List<String> allDiagFields = new ArrayList<>();

    private List<String> selectedSeriesFields = new ArrayList<>();
    private final List<String> allSeriesFields = new ArrayList<>();

    private final EstimationPolicyType[] types = {EstimationPolicyType.Complete,
        EstimationPolicyType.FreeParameters,
        EstimationPolicyType.None};

    /**
     * Creates new form DemetraBehaviourPanel
     */
    DemetraStatsPanel(DemetraStatsOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        initSpecButton();

        diagnosticsView.getOutline().setRootVisible(false);
        editDiagnostic.setEnabled(false);
        resetDiagnostic.setEnabled(false);

        allDiagFields.addAll(BasicConfiguration.allSingleSaDetails(false));
        allSeriesFields.addAll(BasicConfiguration.allSeries(false, SaManager.instance.getProcessors()));

        selectedDiagFields = new ArrayList<>(allDiagFields);
        selectedDiagLabel.setText(String.format("%s selected", selectedDiagFields == null ? 0 : selectedDiagFields.size()));

        selectedSeriesFields = new ArrayList<>(selectedSeriesFields);
        selectedSeriesLabel.setText(String.format("%s selected", selectedSeriesFields == null ? 0 : selectedSeriesFields.size()));

        getDiagnosticsExplorerManager().addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                Node[] nodes = (Node[]) evt.getNewValue();
                editDiagnostic.setEnabled(nodes.length == 1 && nodes[0].getLookup().lookup(Configurable.class) != null);
                resetDiagnostic.setEnabled(nodes.length == 1 && nodes[0].getLookup().lookup(Resetable.class) != null);
            }
        });
        
        fieldSelectionComponent.setSourceHeader(new JLabel("Available items :"));
        fieldSelectionComponent.setTargetHeader(new JLabel("Selected items :"));
        fieldSelectionComponent.setBorder(new EmptyBorder(10, 10, 10, 10));
        fieldSelectionComponent.setPreferredSize(new Dimension(400, 300));
    }

    void load() {
        DemetraUI demetraUI = DemetraUI.getDefault();
        spectralLastYears.setValue(demetraUI.getSpectralLastYears());

        estimationPolicyComboBox.setModel(new DefaultComboBoxModel(types));
        estimationPolicyComboBox.setSelectedItem(demetraUI.getEstimationPolicyType());

        stabilityLength.setValue(demetraUI.getStabilityLength());

        specComponent.setSpecification(demetraUI.getDefaultSASpecInstance());
        selectedSpecLabel.setText(demetraUI.getDefaultSASpecInstance().toLongString());

        selectedDiagFields = demetraUI.getSelectedDiagFields();
        selectedSeriesFields = demetraUI.getSelectedSeriesFields();

        selectedDiagLabel.setText(String.format("%s selected", selectedDiagFields == null ? 0 : selectedDiagFields.size()));
        selectedSeriesLabel.setText(String.format("%s selected", selectedSeriesFields == null ? 0 : selectedSeriesFields.size()));

        AbstractNodeBuilder root = new AbstractNodeBuilder();
        root.add(new AbstractNodeBuilder().name("Diagnostics")
                .add(SaDiagnosticsFactoryBuddyLoader.get().stream().map(NamedServiceNode::new)).build());
        root.add(new AbstractNodeBuilder().name("Outputs")
                .add(Lookup.getDefault().lookupAll(INbOutputFactory.class).stream().map(NamedServiceNode::new)).build());

        getDiagnosticsExplorerManager().setRootContext(root.build());
    }

    void store() {
        DemetraUI demetraUI = DemetraUI.getDefault();
        demetraUI.setSpectralLastYears((Integer) spectralLastYears.getValue());
        demetraUI.setEstimationPolicyType((EstimationPolicyType) estimationPolicyComboBox.getSelectedItem());
        demetraUI.setStabilityLength((Integer) stabilityLength.getValue());
        demetraUI.setSelectedDiagFields(selectedDiagFields);
        demetraUI.setSelectedSeriesFields(selectedSeriesFields);

        if (specComponent.getSpecification() instanceof TramoSeatsSpecification) {
            demetraUI.setDefaultSaSpec("tramoseats." + specComponent.getSpecification());
        } else {
            demetraUI.setDefaultSaSpec("x13." + specComponent.getSpecification().toString());
        }

        NamedServiceNode.storeAll(getDiagnosticsExplorerManager());
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lastYearsPanel = new javax.swing.JPanel();
        spectralLastYears = new javax.swing.JSpinner();
        spectralLabel = new javax.swing.JLabel();
        stabilityLabel = new javax.swing.JLabel();
        stabilityLength = new javax.swing.JSpinner();
        saPanel = new javax.swing.JPanel();
        defaultSpecLabel = new javax.swing.JLabel();
        specButton = DropDownButtonFactory.createDropDownButton(DemetraUiIcon.BLOG_16, specPopup);
        selectedSpecLabel = new javax.swing.JLabel();
        revisionHistoryPanel = new javax.swing.JPanel();
        estimationLabel = new javax.swing.JLabel();
        estimationPolicyComboBox = new javax.swing.JComboBox();
        diagnosticsPanel = new ExtPanel();
        jToolBar1 = new javax.swing.JToolBar();
        editDiagnostic = new javax.swing.JButton();
        resetDiagnostic = new javax.swing.JButton();
        diagnosticsView = new org.openide.explorer.view.OutlineView("Diagnostics");
        componentsPanel = new javax.swing.JPanel();
        seriesSubPanel1 = new javax.swing.JPanel();
        selectedSeriesLabel = new javax.swing.JLabel();
        selectedSeriesButton = new javax.swing.JButton();
        estimationLabel6 = new javax.swing.JLabel();
        seriesSubPanel = new javax.swing.JPanel();
        selectedDiagLabel = new javax.swing.JLabel();
        selectedDiagButton = new javax.swing.JButton();
        estimationLabel1 = new javax.swing.JLabel();

        lastYearsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.lastYearsPanel.border.title"))); // NOI18N

        spectralLastYears.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        org.openide.awt.Mnemonics.setLocalizedText(spectralLabel, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.spectralLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(stabilityLabel, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.stabilityLabel.text")); // NOI18N

        stabilityLength.setModel(new javax.swing.SpinnerNumberModel(8, 1, null, 1));

        javax.swing.GroupLayout lastYearsPanelLayout = new javax.swing.GroupLayout(lastYearsPanel);
        lastYearsPanel.setLayout(lastYearsPanelLayout);
        lastYearsPanelLayout.setHorizontalGroup(
            lastYearsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lastYearsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lastYearsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(stabilityLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spectralLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lastYearsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spectralLastYears, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stabilityLength, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        lastYearsPanelLayout.setVerticalGroup(
            lastYearsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lastYearsPanelLayout.createSequentialGroup()
                .addGroup(lastYearsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spectralLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(lastYearsPanelLayout.createSequentialGroup()
                        .addComponent(spectralLastYears, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lastYearsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stabilityLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(stabilityLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        saPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.saPanel.border.title"))); // NOI18N
        saPanel.setLayout(new javax.swing.BoxLayout(saPanel, javax.swing.BoxLayout.LINE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(defaultSpecLabel, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.defaultSpecLabel.text")); // NOI18N
        defaultSpecLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 20));
        saPanel.add(defaultSpecLabel);

        org.openide.awt.Mnemonics.setLocalizedText(specButton, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.specButton.text")); // NOI18N
        saPanel.add(specButton);

        org.openide.awt.Mnemonics.setLocalizedText(selectedSpecLabel, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.selectedSpecLabel.text")); // NOI18N
        selectedSpecLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 20, 1, 1));
        saPanel.add(selectedSpecLabel);

        revisionHistoryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.revisionHistoryPanel.border.title"))); // NOI18N
        revisionHistoryPanel.setLayout(new javax.swing.BoxLayout(revisionHistoryPanel, javax.swing.BoxLayout.LINE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(estimationLabel, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.estimationLabel.text")); // NOI18N
        estimationLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 20));
        revisionHistoryPanel.add(estimationLabel);

        revisionHistoryPanel.add(estimationPolicyComboBox);

        diagnosticsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.diagnosticsPanel.border.title"))); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);

        editDiagnostic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ec/nbdemetra/ui/preferences-system_16x16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(editDiagnostic, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.editDiagnostic.text")); // NOI18N
        editDiagnostic.setFocusable(false);
        editDiagnostic.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editDiagnostic.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        editDiagnostic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editDiagnosticActionPerformed(evt);
            }
        });
        jToolBar1.add(editDiagnostic);

        resetDiagnostic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ec/nbdemetra/ui/reset_16x16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(resetDiagnostic, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.resetDiagnostic.text")); // NOI18N
        resetDiagnostic.setFocusable(false);
        resetDiagnostic.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resetDiagnostic.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        resetDiagnostic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetDiagnosticActionPerformed(evt);
            }
        });
        jToolBar1.add(resetDiagnostic);

        javax.swing.GroupLayout diagnosticsPanelLayout = new javax.swing.GroupLayout(diagnosticsPanel);
        diagnosticsPanel.setLayout(diagnosticsPanelLayout);
        diagnosticsPanelLayout.setHorizontalGroup(
            diagnosticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(diagnosticsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(diagnosticsView, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        diagnosticsPanelLayout.setVerticalGroup(
            diagnosticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(diagnosticsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(diagnosticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(diagnosticsPanelLayout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 10, Short.MAX_VALUE))
                    .addComponent(diagnosticsView, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        componentsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.componentsPanel.border.title"))); // NOI18N
        componentsPanel.setLayout(new javax.swing.BoxLayout(componentsPanel, javax.swing.BoxLayout.PAGE_AXIS));

        seriesSubPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 5, 1));
        seriesSubPanel1.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(selectedSeriesLabel, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.selectedSeriesLabel.text")); // NOI18N
        seriesSubPanel1.add(selectedSeriesLabel, java.awt.BorderLayout.CENTER);

        org.openide.awt.Mnemonics.setLocalizedText(selectedSeriesButton, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.selectedSeriesButton.text")); // NOI18N
        selectedSeriesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectedSeriesButtonActionPerformed(evt);
            }
        });
        seriesSubPanel1.add(selectedSeriesButton, java.awt.BorderLayout.EAST);

        org.openide.awt.Mnemonics.setLocalizedText(estimationLabel6, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.estimationLabel6.text")); // NOI18N
        estimationLabel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 10));
        seriesSubPanel1.add(estimationLabel6, java.awt.BorderLayout.WEST);

        componentsPanel.add(seriesSubPanel1);

        seriesSubPanel.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(selectedDiagLabel, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.selectedDiagLabel.text")); // NOI18N
        seriesSubPanel.add(selectedDiagLabel, java.awt.BorderLayout.CENTER);

        org.openide.awt.Mnemonics.setLocalizedText(selectedDiagButton, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.selectedDiagButton.text")); // NOI18N
        selectedDiagButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectedDiagButtonActionPerformed(evt);
            }
        });
        seriesSubPanel.add(selectedDiagButton, java.awt.BorderLayout.EAST);

        org.openide.awt.Mnemonics.setLocalizedText(estimationLabel1, org.openide.util.NbBundle.getMessage(DemetraStatsPanel.class, "DemetraStatsPanel.estimationLabel1.text")); // NOI18N
        estimationLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 10));
        seriesSubPanel.add(estimationLabel1, java.awt.BorderLayout.WEST);

        componentsPanel.add(seriesSubPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(diagnosticsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(componentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(revisionHistoryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(saPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
            .addComponent(lastYearsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lastYearsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(revisionHistoryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(componentsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(diagnosticsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void editDiagnosticActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editDiagnosticActionPerformed
        if (getDiagnosticsExplorerManager().getSelectedNodes() != null && getDiagnosticsExplorerManager().getSelectedNodes().length != 0) {
            getDiagnosticsExplorerManager().getSelectedNodes()[0].getPreferredAction().actionPerformed(evt);
        }
    }//GEN-LAST:event_editDiagnosticActionPerformed

    private void resetDiagnosticActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetDiagnosticActionPerformed
        if (getDiagnosticsExplorerManager().getSelectedNodes() != null && getDiagnosticsExplorerManager().getSelectedNodes().length != 0) {
            NotifyDescriptor d = new NotifyDescriptor.Confirmation("Would you like to reset to default values ?", "Reset", NotifyDescriptor.YES_NO_OPTION);
            if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.YES_OPTION) {
                Node node = getDiagnosticsExplorerManager().getSelectedNodes()[0];
                Resetable r = node.getLookup().lookup(Resetable.class);
                r.reset();
            }
        }
    }//GEN-LAST:event_resetDiagnosticActionPerformed

    private void selectedSeriesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectedSeriesButtonActionPerformed
        fieldSelectionComponent.getSourceModel().clear();
        fieldSelectionComponent.getTargetModel().clear();
        List<String> tmpAvailable = new ArrayList<>(allSeriesFields);
        tmpAvailable.removeAll(selectedSeriesFields);

        tmpAvailable.forEach(fieldSelectionComponent.getSourceModel()::addElement);
        selectedSeriesFields.forEach(fieldSelectionComponent.getTargetModel()::addElement);

        NotifyDescriptor d = new NotifyDescriptor(fieldSelectionComponent, "Select fields",
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE,
                null,
                NotifyDescriptor.OK_OPTION);
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
            selectedSeriesFields = fieldSelectionComponent.getSelectedValues();
            selectedSeriesLabel.setText(String.format("%s selected", selectedSeriesFields == null ? 0 : selectedSeriesFields.size()));
            controller.changed();
        }
    }//GEN-LAST:event_selectedSeriesButtonActionPerformed

    private void selectedDiagButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectedDiagButtonActionPerformed
        fieldSelectionComponent.getSourceModel().clear();
        fieldSelectionComponent.getTargetModel().clear();
        List<String> tmpAvailable = new ArrayList<>(allDiagFields);
        tmpAvailable.removeAll(selectedDiagFields);
        
        tmpAvailable.forEach(fieldSelectionComponent.getSourceModel()::addElement);
        selectedDiagFields.forEach(fieldSelectionComponent.getTargetModel()::addElement);

        NotifyDescriptor d = new NotifyDescriptor(fieldSelectionComponent, "Select fields",
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE,
                null,
                NotifyDescriptor.OK_OPTION);
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
            selectedDiagFields = fieldSelectionComponent.getSelectedValues();
            selectedDiagLabel.setText(String.format("%s selected", selectedDiagFields == null ? 0 : selectedDiagFields.size()));
            controller.changed();
        }
    }//GEN-LAST:event_selectedDiagButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel componentsPanel;
    private javax.swing.JLabel defaultSpecLabel;
    private javax.swing.JPanel diagnosticsPanel;
    private org.openide.explorer.view.OutlineView diagnosticsView;
    private javax.swing.JButton editDiagnostic;
    private javax.swing.JLabel estimationLabel;
    private javax.swing.JLabel estimationLabel1;
    private javax.swing.JLabel estimationLabel6;
    private javax.swing.JComboBox estimationPolicyComboBox;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel lastYearsPanel;
    private javax.swing.JButton resetDiagnostic;
    private javax.swing.JPanel revisionHistoryPanel;
    private javax.swing.JPanel saPanel;
    private javax.swing.JButton selectedDiagButton;
    private javax.swing.JLabel selectedDiagLabel;
    private javax.swing.JButton selectedSeriesButton;
    private javax.swing.JLabel selectedSeriesLabel;
    private javax.swing.JLabel selectedSpecLabel;
    private javax.swing.JPanel seriesSubPanel;
    private javax.swing.JPanel seriesSubPanel1;
    private javax.swing.JButton specButton;
    private javax.swing.JLabel spectralLabel;
    private javax.swing.JSpinner spectralLastYears;
    private javax.swing.JLabel stabilityLabel;
    private javax.swing.JSpinner stabilityLength;
    // End of variables declaration//GEN-END:variables

    private void initSpecButton() {
        specPopup.add(specComponent);
        specComponent.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            String p = evt.getPropertyName();
            if (p.equals(JSpecSelectionComponent.SPECIFICATION_PROPERTY) && evt.getNewValue() != null) {
                selectedSpecLabel.setText(((ISaSpecification) evt.getNewValue()).toLongString());
            } else if (p.equals(JSpecSelectionComponent.ICON_PROPERTY) && evt.getNewValue() != null) {
                specButton.setIcon(ImageUtilities.image2Icon((Image) evt.getNewValue()));
            }
        });
    }

    private ExplorerManager getDiagnosticsExplorerManager() {
        return ((ExplorerManager.Provider) diagnosticsPanel).getExplorerManager();

    }

    private static final class ExtPanel extends JPanel implements ExplorerManager.Provider {

        private final ExplorerManager em = new ExplorerManager();

        @Override
        public ExplorerManager getExplorerManager() {
            return em;
        }
    }
}
