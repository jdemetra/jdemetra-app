/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/template_mypluginPanel.java to edit this template
 */
package demetra.desktop.x13.ui;

import demetra.desktop.actions.Configurable;
import demetra.desktop.actions.Resetable;
import demetra.desktop.nodes.AbstractNodeBuilder;
import demetra.desktop.nodes.NamedServiceNode;
import demetra.desktop.x13.diagnostics.X13DiagnosticsFactoryBuddy;
import java.beans.PropertyChangeEvent;
import javax.swing.JPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

final class X13Panel extends javax.swing.JPanel {

    private final X13OptionsPanelController controller;

    X13Panel(X13OptionsPanelController controller) {
        this.controller = controller;
        initComponents();
       getDiagnosticsExplorerManager().addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                Node[] nodes = (Node[]) evt.getNewValue();
                editDiagnostic.setEnabled(nodes.length == 1 && nodes[0].getLookup().lookup(Configurable.class) != null);
                resetDiagnostic.setEnabled(nodes.length == 1 && nodes[0].getLookup().lookup(Resetable.class) != null);
            }
        });
    }

    private ExplorerManager getDiagnosticsExplorerManager() {
        return ((ExplorerManager.Provider) diagnosticsPanel).getExplorerManager();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        diagnosticsPanel = new ExtPanel();
        jToolBar1 = new javax.swing.JToolBar();
        editDiagnostic = new javax.swing.JButton();
        resetDiagnostic = new javax.swing.JButton();
        diagnosticsView = new org.openide.explorer.view.OutlineView("Diagnostics");
        outputPanel = new ExtPanel();
        jToolBar2 = new javax.swing.JToolBar();
        editOutput = new javax.swing.JButton();
        outputView = new org.openide.explorer.view.OutlineView("Diagnostics");
        componentsPanel = new javax.swing.JPanel();
        seriesSubPanel1 = new javax.swing.JPanel();
        selectedSeriesLabel = new javax.swing.JLabel();
        selectedSeriesButton = new javax.swing.JButton();
        estimationLabel6 = new javax.swing.JLabel();
        seriesSubPanel = new javax.swing.JPanel();
        selectedDiagLabel = new javax.swing.JLabel();
        selectedDiagButton = new javax.swing.JButton();
        estimationLabel1 = new javax.swing.JLabel();

        diagnosticsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(X13Panel.class, "X13Panel.diagnosticsPanel.border.title"))); // NOI18N

        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(editDiagnostic, org.openide.util.NbBundle.getMessage(X13Panel.class, "X13Panel.editDiagnostic.text")); // NOI18N
        editDiagnostic.setFocusable(false);
        editDiagnostic.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editDiagnostic.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        editDiagnostic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editDiagnosticActionPerformed(evt);
            }
        });
        jToolBar1.add(editDiagnostic);

        org.openide.awt.Mnemonics.setLocalizedText(resetDiagnostic, org.openide.util.NbBundle.getMessage(X13Panel.class, "X13Panel.resetDiagnostic.text")); // NOI18N
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(diagnosticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(diagnosticsView, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        outputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(X13Panel.class, "X13Panel.outputPanel.border.title"))); // NOI18N

        jToolBar2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar2.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(editOutput, org.openide.util.NbBundle.getMessage(X13Panel.class, "X13Panel.editOutput.text")); // NOI18N
        editOutput.setFocusable(false);
        editOutput.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editOutput.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        editOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editOutputActionPerformed(evt);
            }
        });
        jToolBar2.add(editOutput);

        javax.swing.GroupLayout outputPanelLayout = new javax.swing.GroupLayout(outputPanel);
        outputPanel.setLayout(outputPanelLayout);
        outputPanelLayout.setHorizontalGroup(
            outputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(outputView, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        outputPanelLayout.setVerticalGroup(
            outputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(outputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outputView, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        componentsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(X13Panel.class, "X13Panel.componentsPanel.border.title"))); // NOI18N
        componentsPanel.setLayout(new javax.swing.BoxLayout(componentsPanel, javax.swing.BoxLayout.PAGE_AXIS));

        seriesSubPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 5, 1));
        seriesSubPanel1.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(selectedSeriesLabel, org.openide.util.NbBundle.getMessage(X13Panel.class, "X13Panel.selectedSeriesLabel.text")); // NOI18N
        seriesSubPanel1.add(selectedSeriesLabel, java.awt.BorderLayout.CENTER);

        org.openide.awt.Mnemonics.setLocalizedText(selectedSeriesButton, org.openide.util.NbBundle.getMessage(X13Panel.class, "X13Panel.selectedSeriesButton.text")); // NOI18N
        selectedSeriesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectedSeriesButtonActionPerformed(evt);
            }
        });
        seriesSubPanel1.add(selectedSeriesButton, java.awt.BorderLayout.EAST);

        org.openide.awt.Mnemonics.setLocalizedText(estimationLabel6, org.openide.util.NbBundle.getMessage(X13Panel.class, "X13Panel.estimationLabel6.text")); // NOI18N
        estimationLabel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 10));
        seriesSubPanel1.add(estimationLabel6, java.awt.BorderLayout.WEST);

        componentsPanel.add(seriesSubPanel1);

        seriesSubPanel.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(selectedDiagLabel, org.openide.util.NbBundle.getMessage(X13Panel.class, "X13Panel.selectedDiagLabel.text")); // NOI18N
        seriesSubPanel.add(selectedDiagLabel, java.awt.BorderLayout.CENTER);

        org.openide.awt.Mnemonics.setLocalizedText(selectedDiagButton, org.openide.util.NbBundle.getMessage(X13Panel.class, "X13Panel.selectedDiagButton.text")); // NOI18N
        selectedDiagButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectedDiagButtonActionPerformed(evt);
            }
        });
        seriesSubPanel.add(selectedDiagButton, java.awt.BorderLayout.EAST);

        org.openide.awt.Mnemonics.setLocalizedText(estimationLabel1, org.openide.util.NbBundle.getMessage(X13Panel.class, "X13Panel.estimationLabel1.text")); // NOI18N
        estimationLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 10));
        seriesSubPanel.add(estimationLabel1, java.awt.BorderLayout.WEST);

        componentsPanel.add(seriesSubPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(componentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(diagnosticsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(outputPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(componentsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(diagnosticsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void editDiagnosticActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editDiagnosticActionPerformed
        if (getDiagnosticsExplorerManager().getSelectedNodes() != null && getDiagnosticsExplorerManager().getSelectedNodes().length != 0) {
            getDiagnosticsExplorerManager().getSelectedNodes()[0].getPreferredAction().actionPerformed(evt);
        }
    }//GEN-LAST:event_editDiagnosticActionPerformed

    private void resetDiagnosticActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetDiagnosticActionPerformed
//        if (getDiagnosticsExplorerManager().getSelectedNodes() != null && getDiagnosticsExplorerManager().getSelectedNodes().length != 0) {
//            NotifyDescriptor d = new NotifyDescriptor.Confirmation("Would you like to reset to default values ?", "Reset", NotifyDescriptor.YES_NO_OPTION);
//            if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.YES_OPTION) {
//                Node node = getDiagnosticsExplorerManager().getSelectedNodes()[0];
//                IResetable r = node.getLookup().lookup(IResetable.class);
//                r.reset();
//            }
//        }
    }//GEN-LAST:event_resetDiagnosticActionPerformed

    private void editOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editOutputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editOutputActionPerformed

    private void selectedSeriesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectedSeriesButtonActionPerformed
//        fieldSelectionComponent.getSourceModel().clear();
//        fieldSelectionComponent.getTargetModel().clear();
//        List<String> tmpAvailable = new ArrayList<>(allSeriesFields);
//        tmpAvailable.removeAll(selectedSeriesFields);
//
//        tmpAvailable.forEach(fieldSelectionComponent.getSourceModel()::addElement);
//        selectedSeriesFields.forEach(fieldSelectionComponent.getTargetModel()::addElement);
//
//        NotifyDescriptor d = new NotifyDescriptor(fieldSelectionComponent, "Select fields",
//            NotifyDescriptor.OK_CANCEL_OPTION,
//            NotifyDescriptor.PLAIN_MESSAGE,
//            null,
//            NotifyDescriptor.OK_OPTION);
//        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
//            selectedSeriesFields = fieldSelectionComponent.getSelectedValues();
//            selectedSeriesLabel.setText(String.format("%s selected", selectedSeriesFields == null ? 0 : selectedSeriesFields.size()));
//            controller.changed();
//        }
    }//GEN-LAST:event_selectedSeriesButtonActionPerformed

    private void selectedDiagButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectedDiagButtonActionPerformed
//        fieldSelectionComponent.getSourceModel().clear();
//        fieldSelectionComponent.getTargetModel().clear();
//        List<String> tmpAvailable = new ArrayList<>(allDiagFields);
//        tmpAvailable.removeAll(selectedDiagFields);
//
//        tmpAvailable.forEach(fieldSelectionComponent.getSourceModel()::addElement);
//        selectedDiagFields.forEach(fieldSelectionComponent.getTargetModel()::addElement);
//
//        NotifyDescriptor d = new NotifyDescriptor(fieldSelectionComponent, "Select fields",
//            NotifyDescriptor.OK_CANCEL_OPTION,
//            NotifyDescriptor.PLAIN_MESSAGE,
//            null,
//            NotifyDescriptor.OK_OPTION);
//        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
//            selectedDiagFields = fieldSelectionComponent.getSelectedValues();
//            selectedDiagLabel.setText(String.format("%s selected", selectedDiagFields == null ? 0 : selectedDiagFields.size()));
//            controller.changed();
//        }
    }//GEN-LAST:event_selectedDiagButtonActionPerformed

    void load() {
        AbstractNodeBuilder root = new AbstractNodeBuilder();
        root.add(new AbstractNodeBuilder().name("Diagnostics")
                .add(Lookup.getDefault().lookupAll(X13DiagnosticsFactoryBuddy.class).stream().map(NamedServiceNode::new)).build());
        getDiagnosticsExplorerManager().setRootContext(root.build());
    }

    void store() {
        // TODO store modified settings
        // Example:
        // Preferences.userNodeForPackage(X13Panel.class).putBoolean("someFlag", someCheckBox.isSelected());
        // or for org.openide.util with API spec. version >= 7.4:
        // NbPreferences.forModule(X13Panel.class).putBoolean("someFlag", someCheckBox.isSelected());
        // or:
        // SomeSystemOption.getDefault().setSomeStringProperty(someTextField.getText());
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel componentsPanel;
    private javax.swing.JPanel diagnosticsPanel;
    private org.openide.explorer.view.OutlineView diagnosticsView;
    private javax.swing.JButton editDiagnostic;
    private javax.swing.JButton editOutput;
    private javax.swing.JLabel estimationLabel1;
    private javax.swing.JLabel estimationLabel6;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JPanel outputPanel;
    private org.openide.explorer.view.OutlineView outputView;
    private javax.swing.JButton resetDiagnostic;
    private javax.swing.JButton selectedDiagButton;
    private javax.swing.JLabel selectedDiagLabel;
    private javax.swing.JButton selectedSeriesButton;
    private javax.swing.JLabel selectedSeriesLabel;
    private javax.swing.JPanel seriesSubPanel;
    private javax.swing.JPanel seriesSubPanel1;
    // End of variables declaration//GEN-END:variables

    private static final class ExtPanel extends JPanel implements ExplorerManager.Provider {

        private final ExplorerManager em = new ExplorerManager();

        @Override
        public ExplorerManager getExplorerManager() {
            return em;
        }
    }

}
