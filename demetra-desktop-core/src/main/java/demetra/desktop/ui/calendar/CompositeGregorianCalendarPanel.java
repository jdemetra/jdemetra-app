/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.calendar;

import com.google.common.collect.ImmutableList;
import demetra.desktop.design.SwingProperty;
import demetra.desktop.beans.PropertyChangeSource;
import demetra.desktop.DemetraIcons;
import demetra.desktop.util.IDialogDescriptorProvider;
import demetra.desktop.util.ListenerState;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.timeseries.calendars.CalendarManager;
import demetra.timeseries.regression.ModellingContext;
import demetra.util.Arrays2;
import demetra.util.Constraint;
import demetra.util.WeightedItem;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.DialogDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.nodes.Sheet;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Philippe Charles
 */
public class CompositeGregorianCalendarPanel extends JPanel implements ExplorerManager.Provider, IDialogDescriptorProvider {

    // PROPERTIES DEFINITION
    public static final String CALENDAR_NAME_PROPERTY = "calendarName";
    public static final String WEIGHTED_ITEMS_PROPERTY = "weightedItems";
    // PROPERTIES
    private String calendarName;
    private ImmutableList<WeightedItem<String>> weightedItems;
    // OTHER
    final ExplorerManager em;
    final NameTextFieldListener nameTextFieldListener;
    final ListOfWeightedItem childFactory;
    final String initialCalendarName;

    /**
     * Creates new form NationalCalendarPanel
     */
    public CompositeGregorianCalendarPanel(String initialCalendarName) {
        this.initialCalendarName = initialCalendarName;
        this.calendarName = initialCalendarName != null ? initialCalendarName : "";
        this.weightedItems = ImmutableList.of();

        this.em = new ExplorerManager();

        initComponents();

        childFactory = new ListOfWeightedItem();

        em.setRootContext(new AbstractNode(Children.create(childFactory, false)));

        this.nameTextFieldListener = new NameTextFieldListener();

        treeTableView1.setRootVisible(false);
        treeTableView1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        WeightedItemNode template = new WeightedItemNode(new WeightedItemBean(""));
        treeTableView1.setProperties(template.getPropertySets()[0].getProperties());

        nameTextField.setText(calendarName);
        nameTextField.getDocument().addDocumentListener(nameTextFieldListener);

        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case CompositeGregorianCalendarPanel.CALENDAR_NAME_PROPERTY:
                    onCalendarNameChange();
                    break;
                case CompositeGregorianCalendarPanel.WEIGHTED_ITEMS_PROPERTY:
                    onWeightedItemsChange();
                    break;
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        treeTableView1 = new org.openide.explorer.view.TreeTableView();

        jLabel1.setText("Name:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(treeTableView1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, nameTextField)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jLabel1)
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(4, 4, 4)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(nameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(treeTableView1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 270, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField nameTextField;
    private org.openide.explorer.view.TreeTableView treeTableView1;
    // End of variables declaration//GEN-END:variables

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    protected void onCalendarNameChange() {
        if (nameTextFieldListener.state == ListenerState.READY) {
            nameTextFieldListener.state = ListenerState.SUSPENDED;
            nameTextField.setText(calendarName);
            nameTextFieldListener.state = ListenerState.READY;
        }
    }

    protected void onWeightedItemsChange() {
        if (childFactory.state == ListenerState.READY) {
            childFactory.state = ListenerState.SUSPENDED;
            Map<String, Double> tmp = new HashMap<>();
            for (WeightedItem<String> o : weightedItems) {
                tmp.put(o.getItem(), o.getWeight());
            }
            for (WeightedItemBean o : childFactory.beans) {
                Double weight = tmp.get(o.getName());
                if (weight != null) {
                    o.setUsed(true);
                    o.setWeight(weight);
                } else {
                    o.setUsed(false);
                    o.setWeight(0);
                }
            }
            childFactory.refreshData();
            childFactory.state = ListenerState.READY;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        String old = this.calendarName;
        this.calendarName = calendarName != null ? calendarName : "";
        firePropertyChange(CALENDAR_NAME_PROPERTY, old, this.calendarName);
    }

    public ImmutableList<WeightedItem<String>> getWeightedItems() {
        return weightedItems;
    }

    public void setWeightedItems(ImmutableList<WeightedItem<String>> weightedItems) {
        ImmutableList<WeightedItem<String>> old = this.weightedItems;
        this.weightedItems = weightedItems != null ? weightedItems : ImmutableList.of();
        firePropertyChange(WEIGHTED_ITEMS_PROPERTY, old, this.weightedItems);
    }
    //</editor-fold>

    @Override
    public ExplorerManager getExplorerManager() {
        return em;
    }

    public static class WeightedItemBean implements PropertyChangeSource.WithWeakListeners {

        // PROPERTIES DEFINITIONS
        @SwingProperty
        public static final String NAME_PROPERTY = "name";

        @SwingProperty
        public static final String USED_PROPERTY = "used";

        @SwingProperty
        public static final String WEIGHT_PROPERTY = "weight";

        @lombok.experimental.Delegate(types = PropertyChangeSource.class)
        private final PropertyChangeSupport broadcaster = new PropertyChangeSupport(this);

        // PROPERTIES
        private String name;
        private boolean used;
        private double weight;

        public WeightedItemBean(String name) {
            this.name = name;
            this.used = false;
            this.weight = 0d;
        }

        //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
        public String getName() {
            return name;
        }

        public void setName(String name) {
            String old = this.name;
            this.name = name;
            broadcaster.firePropertyChange(NAME_PROPERTY, old, this.name);
        }

        public boolean isUsed() {
            return used;
        }

        public void setUsed(boolean used) {
            boolean old = this.used;
            this.used = used;
            broadcaster.firePropertyChange(USED_PROPERTY, old, this.used);
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            double old = this.weight;
            this.weight = weight;
            broadcaster.firePropertyChange(WEIGHT_PROPERTY, old, this.weight);
        }
        //</editor-fold>

        public WeightedItem<String> toItem() {
            return new WeightedItem<>(name, weight);
        }
    }

    static class WeightedItemNode extends AbstractNode implements PropertyChangeListener {

        public WeightedItemNode(WeightedItemBean bean) {
            super(Children.LEAF, Lookups.singleton(bean));
            setName(bean.getName());
            bean.addWeakPropertyChangeListener(this);
        }

        @Override
        public Image getIcon(int type) {
            return DemetraIcons.CALENDAR_16.getImageIcon().getImage();
        }

        @Override
        public String getHtmlDisplayName() {
            WeightedItemBean bean = getLookup().lookup(WeightedItemBean.class);
            return bean.isUsed() ? ("<b>" + getDisplayName() + "</b>") : getDisplayName();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            fireDisplayNameChange(null, getDisplayName());
        }

        @Override
        protected Sheet createSheet() {
            WeightedItemBean bean = getLookup().lookup(WeightedItemBean.class);
            Sheet result = super.createSheet();
            NodePropertySetBuilder b = new NodePropertySetBuilder();
            b.withBoolean()
                    .select(bean, "used")
                    .display("Used")
                    .add();
            b.withDouble()
                    .select(bean, "weight")
                    .min(0)
                    .display("Weight")
                    .add();
            result.put(b.build());
            return result;
        }
    }

    class ListOfWeightedItem extends ChildFactory<WeightedItemBean> implements NodeListener {

        public final List<WeightedItemBean> beans = new ArrayList<>();
        ListenerState state = ListenerState.READY;

        public ListOfWeightedItem() {
            for (String o : ModellingContext.getActiveContext().getCalendars().getNames()) {
                if (!o.equals(initialCalendarName)) {
                    beans.add(new WeightedItemBean(o));
                }
            }
        }

        public void refreshData() {
            refresh(true);
            fireDataChange();
        }

        void fireDataChange() {
            if (state == ListenerState.READY) {
                state = ListenerState.SENDING;
                ImmutableList.Builder<WeightedItem<String>> tmp = ImmutableList.builder();
                for (WeightedItemBean o : beans) {
                    if (o.isUsed()) {
                        tmp.add(o.toItem());
                    }
                }
                setWeightedItems(tmp.build());
                state = ListenerState.READY;
            }
        }

        @Override
        protected boolean createKeys(List<WeightedItemBean> toPopulate) {
            toPopulate.addAll(beans);
            return true;
        }

        @Override
        protected Node createNodeForKey(WeightedItemBean key) {
            Node result = new WeightedItemNode(key);
            result.addNodeListener(WeakListeners.create(NodeListener.class, this, result));
            return result;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String p = evt.getPropertyName();
            if (p.equals(Node.PROP_DISPLAY_NAME)) {
                fireDataChange();
            }
        }

        @Override
        public void childrenAdded(NodeMemberEvent ev) {
        }

        @Override
        public void childrenRemoved(NodeMemberEvent ev) {
        }

        @Override
        public void childrenReordered(NodeReorderEvent ev) {
        }

        @Override
        public void nodeDestroyed(NodeEvent ev) {
        }
    }

    private class NameTextFieldListener implements DocumentListener {

        ListenerState state = ListenerState.READY;

        void update() {
            if (state == ListenerState.READY) {
                state = ListenerState.SENDING;
                setCalendarName(nameTextField.getText());
                state = ListenerState.READY;
            }
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    }

    @Override
    public DialogDescriptor createDialogDescriptor(String title) {
        return new CompositeDialogDescriptor(this, title);
    }

    private static class CompositeDialogDescriptor extends CustomDialogDescriptor<CompositeConstraintData> {

        CompositeDialogDescriptor(CompositeGregorianCalendarPanel p, String title) {
            super(p, title, new CompositeConstraintData(p, p.getCalendarName()));
            validate(CompositeConstraints.values());
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName()) {
                case CALENDAR_NAME_PROPERTY:
                    validate(CompositeConstraints.CALENDAR_NAME);
                    break;
                case WEIGHTED_ITEMS_PROPERTY:
                    break;
            }
        }
    }

    private static class CompositeConstraintData {

        final CompositeGregorianCalendarPanel panel;
        final String originalName;
        final CalendarManager manager;

        CompositeConstraintData(CompositeGregorianCalendarPanel panel, String originalName) {
            this.panel = panel;
            this.originalName = originalName;
            this.manager = ModellingContext.getActiveContext().getCalendars();
        }
    }

    private enum CompositeConstraints implements Constraint<CompositeConstraintData> {

        CALENDAR_NAME {
            @Override
            public String check(CompositeConstraintData t) {
                String name = t.panel.getCalendarName();
                if (name.isEmpty()) {
                    return "The name of the calendar cannot be empty";
                }
                if (!t.originalName.equals(name) && t.manager.contains(name)) {
                    return "The name of the calendar is already used";
                }
                return null;
            }
        }
    }

    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (!Arrays2.arrayEquals(oldValue, newValue)) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
}