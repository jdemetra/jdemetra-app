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

import demetra.ui.TsActions;
import demetra.ui.concurrent.ThreadPoolSize;
import demetra.ui.concurrent.ThreadPriority;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Mats Maggi
 */
final class DemetraBehaviourPanel extends javax.swing.JPanel implements ItemListener {

    private final DemetraBehaviourOptionsPanelController controller;

    /**
     * Creates new form DemetraBehaviourPanel
     */
    DemetraBehaviourPanel(DemetraBehaviourOptionsPanelController controller) {
        this.controller = controller;
        initComponents();

        showUnavailableCheckBox.addItemListener(this);
        persistToolsContent.addItemListener(this);
        persistOpenDataSources.addItemListener(this);
        batchPoolSizeCombo.addItemListener(this);
        batchPriorityCombo.addItemListener(this);
        tsActionChoicePanel.getComboBox().addItemListener(this);
    }

    void load() {
        DemetraUI demetraUI = DemetraUI.getDefault();
        showUnavailableCheckBox.setSelected(demetraUI.isShowUnavailableTsProviders());
        persistToolsContent.setSelected(demetraUI.isPersistToolsContent());
        persistOpenDataSources.setSelected(demetraUI.isPersistOpenedDataSources());

        tsActionChoicePanel.setContent(TsActions.getDefault().getOpenActions());
        tsActionChoicePanel.setSelectedServiceName(demetraUI.getTsActionName());

        batchPoolSizeCombo.setModel(new DefaultComboBoxModel(ThreadPoolSize.values()));
        batchPoolSizeCombo.setSelectedItem(demetraUI.getBatchPoolSize());
        batchPriorityCombo.setModel(new DefaultComboBoxModel(ThreadPriority.values()));
        batchPriorityCombo.setSelectedItem(demetraUI.getBatchPriority());
    }

    void store() {
        DemetraUI demetraUI = DemetraUI.getDefault();
        demetraUI.setShowUnavailableTsProviders(showUnavailableCheckBox.isSelected());
        demetraUI.setPersistToolsContent(persistToolsContent.isSelected());
        demetraUI.setPersistOpenedDataSources(persistOpenDataSources.isSelected());

        demetraUI.setTsActionName(tsActionChoicePanel.getSelectedServiceName());

        demetraUI.setBatchPriority((ThreadPriority) batchPriorityCombo.getSelectedItem());
        demetraUI.setBatchPoolSize((ThreadPoolSize) batchPoolSizeCombo.getSelectedItem());
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

        threadingPanel = new javax.swing.JPanel();
        batchPoolLabel = new javax.swing.JLabel();
        batchPriorityCombo = new javax.swing.JComboBox();
        batchPriorityLabel = new javax.swing.JLabel();
        batchPoolSizeCombo = new javax.swing.JComboBox();
        persistencePanel = new javax.swing.JPanel();
        persistToolsContent = new javax.swing.JCheckBox();
        persistOpenDataSources = new javax.swing.JCheckBox();
        providersPanel = new javax.swing.JPanel();
        showUnavailableCheckBox = new javax.swing.JCheckBox();
        tsPanel = new javax.swing.JPanel();
        doubleClickLabel = new javax.swing.JLabel();
        tsActionChoicePanel = new ec.nbdemetra.ui.ns.NamedServiceChoicePanel();

        threadingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DemetraBehaviourPanel.class, "DemetraBehaviourPanel.threadingPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(batchPoolLabel, org.openide.util.NbBundle.getMessage(DemetraBehaviourPanel.class, "DemetraBehaviourPanel.batchPoolLabel.text")); // NOI18N

        batchPriorityCombo.setOpaque(false);

        org.openide.awt.Mnemonics.setLocalizedText(batchPriorityLabel, org.openide.util.NbBundle.getMessage(DemetraBehaviourPanel.class, "DemetraBehaviourPanel.batchPriorityLabel.text")); // NOI18N

        batchPoolSizeCombo.setOpaque(false);

        javax.swing.GroupLayout threadingPanelLayout = new javax.swing.GroupLayout(threadingPanel);
        threadingPanel.setLayout(threadingPanelLayout);
        threadingPanelLayout.setHorizontalGroup(
            threadingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(threadingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(threadingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(batchPoolLabel)
                    .addComponent(batchPriorityLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(threadingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(batchPriorityCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(batchPoolSizeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        threadingPanelLayout.setVerticalGroup(
            threadingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(threadingPanelLayout.createSequentialGroup()
                .addGroup(threadingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(batchPoolLabel)
                    .addComponent(batchPoolSizeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(threadingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(batchPriorityLabel)
                    .addComponent(batchPriorityCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        persistencePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DemetraBehaviourPanel.class, "DemetraBehaviourPanel.persistencePanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(persistToolsContent, org.openide.util.NbBundle.getMessage(DemetraBehaviourPanel.class, "DemetraBehaviourPanel.persistToolsContent.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(persistOpenDataSources, org.openide.util.NbBundle.getMessage(DemetraBehaviourPanel.class, "DemetraBehaviourPanel.persistOpenDataSources.text")); // NOI18N

        javax.swing.GroupLayout persistencePanelLayout = new javax.swing.GroupLayout(persistencePanel);
        persistencePanel.setLayout(persistencePanelLayout);
        persistencePanelLayout.setHorizontalGroup(
            persistencePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(persistencePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(persistencePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(persistToolsContent)
                    .addComponent(persistOpenDataSources))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        persistencePanelLayout.setVerticalGroup(
            persistencePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(persistencePanelLayout.createSequentialGroup()
                .addComponent(persistOpenDataSources)
                .addGap(0, 0, 0)
                .addComponent(persistToolsContent))
        );

        providersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DemetraBehaviourPanel.class, "DemetraBehaviourPanel.providersPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(showUnavailableCheckBox, org.openide.util.NbBundle.getMessage(DemetraBehaviourPanel.class, "DemetraBehaviourPanel.showUnavailableCheckBox.text")); // NOI18N

        javax.swing.GroupLayout providersPanelLayout = new javax.swing.GroupLayout(providersPanel);
        providersPanel.setLayout(providersPanelLayout);
        providersPanelLayout.setHorizontalGroup(
            providersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(providersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(showUnavailableCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        providersPanelLayout.setVerticalGroup(
            providersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(showUnavailableCheckBox)
        );

        tsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DemetraBehaviourPanel.class, "DemetraBehaviourPanel.tsPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(doubleClickLabel, org.openide.util.NbBundle.getMessage(DemetraBehaviourPanel.class, "DemetraBehaviourPanel.doubleClickLabel.text")); // NOI18N

        tsActionChoicePanel.setOpaque(false);

        javax.swing.GroupLayout tsPanelLayout = new javax.swing.GroupLayout(tsPanel);
        tsPanel.setLayout(tsPanelLayout);
        tsPanelLayout.setHorizontalGroup(
            tsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(doubleClickLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tsActionChoicePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(70, Short.MAX_VALUE))
        );
        tsPanelLayout.setVerticalGroup(
            tsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(doubleClickLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tsActionChoicePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(providersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(persistencePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(threadingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(providersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(persistencePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(threadingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel batchPoolLabel;
    private javax.swing.JComboBox batchPoolSizeCombo;
    private javax.swing.JComboBox batchPriorityCombo;
    private javax.swing.JLabel batchPriorityLabel;
    private javax.swing.JLabel doubleClickLabel;
    private javax.swing.JCheckBox persistOpenDataSources;
    private javax.swing.JCheckBox persistToolsContent;
    private javax.swing.JPanel persistencePanel;
    private javax.swing.JPanel providersPanel;
    private javax.swing.JCheckBox showUnavailableCheckBox;
    private javax.swing.JPanel threadingPanel;
    private ec.nbdemetra.ui.ns.NamedServiceChoicePanel tsActionChoicePanel;
    private javax.swing.JPanel tsPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void itemStateChanged(ItemEvent e) {
        switch (e.getStateChange()) {
            case 1: 
            case 2:
                controller.changed();
        }
    }
}