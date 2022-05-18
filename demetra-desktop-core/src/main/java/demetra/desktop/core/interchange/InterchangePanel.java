/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package demetra.desktop.core.interchange;

import demetra.desktop.actions.Configurable;
import demetra.desktop.interchange.Interchange;
import demetra.desktop.nodes.AbstractNodeBuilder;
import demetra.desktop.nodes.NamedServiceNode;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;

import java.util.stream.Stream;

final class InterchangePanel extends javax.swing.JPanel implements ExplorerManager.Provider {

    private final InterchangeOptionsPanelController controller;
    private final ExplorerManager em;

    InterchangePanel(InterchangeOptionsPanelController controller) {
        this.controller = controller;
        this.em = new ExplorerManager();
        initComponents();
        editButton.setEnabled(false);
        em.addVetoableChangeListener(evt -> {
            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                Node[] nodes = (Node[]) evt.getNewValue();
                editButton.setEnabled(nodes.length == 1 && nodes[0].getLookup().lookup(Configurable.class) != null);
            }
        });
        outlineView2.getOutline().setRootVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        editButton = new javax.swing.JButton();
        outlineView2 = new org.openide.explorer.view.OutlineView("Interchange broker");

        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);

        editButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/demetra/desktop/core/preferences-system_16x16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(editButton, org.openide.util.NbBundle.getMessage(InterchangePanel.class, "InterchangePanel.editButton.text")); // NOI18N
        editButton.setToolTipText(org.openide.util.NbBundle.getMessage(InterchangePanel.class, "InterchangePanel.editButton.toolTipText")); // NOI18N
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(editButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(outlineView2, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addGap(112, 112, 112))
            .addComponent(outlineView2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        em.getSelectedNodes()[0].getPreferredAction().actionPerformed(evt);
    }//GEN-LAST:event_editButtonActionPerformed

    void load() {
        Stream<NamedServiceNode> nodes = Interchange.getDefault().all().stream().map(NamedServiceNode::new);
        em.setRootContext(new AbstractNodeBuilder().add(nodes).name("Interchange broker").build());
    }

    void store() {
        for (Node o : em.getRootContext().getChildren().getNodes()) {
            if (o instanceof NamedServiceNode) {
                ((NamedServiceNode) o).applyConfig();
            }
        }
    }

    boolean valid() {
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton editButton;
    private javax.swing.JToolBar jToolBar1;
    private org.openide.explorer.view.OutlineView outlineView2;
    // End of variables declaration//GEN-END:variables

    @Override
    public ExplorerManager getExplorerManager() {
        return em;
    }
}
