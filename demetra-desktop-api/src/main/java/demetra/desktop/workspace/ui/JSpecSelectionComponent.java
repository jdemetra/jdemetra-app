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
package demetra.desktop.workspace.ui;

import demetra.desktop.design.SwingComponent;
import demetra.desktop.design.SwingProperty;
import demetra.desktop.util.IDialogDescriptorProvider;
import demetra.desktop.nodes.DecoratedNode;
import demetra.desktop.ui.CustomDialogDescriptor;
import demetra.util.Constraint;
import demetra.util.LinearId;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.ItemWsNode;
import demetra.desktop.workspace.nodes.DummyWsNode;
import demetra.processing.ProcSpecification;
import demetra.util.Arrays2;
import org.openide.DialogDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.Optional;
import java.util.function.Predicate;

/**
 *
 * @author Philippe Charles
 */
@SwingComponent
public final class JSpecSelectionComponent extends JComponent implements ExplorerManager.Provider, IDialogDescriptorProvider {

    @SwingProperty
    public static final String SPECIFICATION_PROPERTY = "specification";

    @SwingProperty
    public static final String ICON_PROPERTY = "icon";

    private final BeanTreeView tree;
    private final ExplorerManager em;
    private final SelectionListener selectionListener;

    private ProcSpecification specification;
    private Image icon;
    private boolean systemOnly;

    public JSpecSelectionComponent() {
        this(false);
    }

    public JSpecSelectionComponent(boolean showSystemOnly) {
        this.systemOnly = showSystemOnly;
        this.tree = new BeanTreeView();
        this.em = new ExplorerManager();
        this.selectionListener = new SelectionListener();
        this.specification = null;
        this.icon = null;

        tree.setRootVisible(false);
        tree.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        setLayout(new BorderLayout());
        add(tree, BorderLayout.CENTER);
        setPreferredSize(new Dimension(225, 300));

        em.addVetoableChangeListener(selectionListener);
        addPropertyChangeListener(evt -> {
            String p = evt.getPropertyName();
            if (p.equals(SPECIFICATION_PROPERTY)) {
                onSpecificationChange();
            }
        });
    }

    public void setFamily(String family) {
        DecoratedNode root = new DecoratedNode(new DummyWsNode(WorkspaceFactory.getInstance().getActiveWorkspace(), new LinearId(family, WorkspaceFactory.SPECIFICATIONS)), systemOnly ? ItemWsNodeFilter.SYSTEM_ONLY : (o -> true));
        root.breadthFirstStream().forEach(o -> o.setPreferredActionDecorator(DecoratedNode.PreferredAction.DO_NOTHING));
        em.setRootContext(root);
    }

    boolean isCurrentSpecificationNode(Node o) {
        return o instanceof ItemWsNode && ((ItemWsNode) o).getItem().getElement().equals(specification);
    }

    class SelectionListener implements VetoableChangeListener {

        boolean enable = true;

        @Override
        public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
            if (enable && ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                Node[] nodes = (Node[]) evt.getNewValue();
                if (nodes.length > 0 && ((DecoratedNode) nodes[0]).getOriginal() instanceof ItemWsNode) {
                    ItemWsNode node = (ItemWsNode) ((DecoratedNode) nodes[0]).getOriginal();
                    setSpecification((ProcSpecification) node.getItem().getElement());
                    setIcon(node.getIcon(BeanInfo.ICON_COLOR_16x16));
                } else {
                    setSpecification(null);
                    setIcon(null);
                }
            }
        }
    }

    protected void onSpecificationChange() {
        selectionListener.enable = false;
//        for (Node o : (Node[]) em.getSelectedNodes()) {
//            ((DecoratedNode) o).setHtmlDecorator(null);
//        }
        Node root = em.getRootContext();
        if (root instanceof DecoratedNode droot) {
            Optional<DecoratedNode> node = droot.breadthFirstStream().filter(o -> isCurrentSpecificationNode(o.getOriginal())).findFirst();
            if (node.isPresent()) {
//            node.get().setHtmlDecorator(DecoratedNode.Html.BOLD);
                try {
                    em.setSelectedNodes(new Node[]{node.get()});
                } catch (PropertyVetoException ex) {
                    // do nothing?
                }
            }
        }
        selectionListener.enable = true;

    }

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    @Override
    public ExplorerManager getExplorerManager() {
        return em;
    }

    public ProcSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(ProcSpecification specification) {
        ProcSpecification old = this.specification;
        this.specification = specification;
        firePropertyChange(SPECIFICATION_PROPERTY, old, this.specification);
    }

    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        Image old = this.icon;
        this.icon = icon;
        firePropertyChange(ICON_PROPERTY, old, this.icon);
    }
    //</editor-fold>

    @Override
    public DialogDescriptor createDialogDescriptor(String title) {
        return new SpecSelectionDialogDescriptor(this, title);
    }

    private static class SpecSelectionDialogDescriptor extends CustomDialogDescriptor<JSpecSelectionComponent> {

        SpecSelectionDialogDescriptor(JSpecSelectionComponent p, String title) {
            super(p, title, p);
            validate(SpecSelectionConstraints.values());
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String p = evt.getPropertyName();
            if (p.equals(JSpecSelectionComponent.SPECIFICATION_PROPERTY)) {
                validate(SpecSelectionConstraints.values());
            }
        }
    }

    private enum SpecSelectionConstraints implements Constraint<JSpecSelectionComponent> {

        SELECTION;

        @Override
        public String check(JSpecSelectionComponent t) {
            return t.getSpecification() == null ? "Specification not selected" : null;
        }
    }

    private enum ItemWsNodeFilter implements Predicate<Node> {

        SYSTEM_ONLY;

        @Override
        public boolean test(Node input) {
            return !(input instanceof ItemWsNode)
                    || ((ItemWsNode) input).getItem().getStatus() == WorkspaceItem.Status.System;
        }
    }

    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (!Arrays2.arrayEquals(oldValue, newValue)) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
}
