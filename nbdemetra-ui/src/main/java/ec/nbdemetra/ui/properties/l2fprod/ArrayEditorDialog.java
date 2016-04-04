package ec.nbdemetra.ui.properties.l2fprod;

import com.google.common.collect.Lists;
import com.l2fprod.common.propertysheet.*;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.NbComponents;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Demortier Jeremy
 */
public class ArrayEditorDialog<T> extends JDialog {

    private Class<T> c_;
    private ArrayList<T> elementsList_;
    private T cur_;
    private boolean dirty_;

    public List<T> getElements() {
        return elementsList_;
    }

    public boolean isDirty() {
        return dirty_;
    }

    public ArrayEditorDialog(final Window owner, T[] elements, Class<T> c) {
        super(owner);
        c_ = c;
        elementsList_ = Lists.newArrayList(elements);

        final JPanel pane = new JPanel(new BorderLayout());
        pane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        final JList list = new JList(new ArrayEditorListModel(elementsList_));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setPreferredSize(new Dimension(150, 200));
        pane.add(NbComponents.newJScrollPane(list), BorderLayout.WEST);

        final PropertySheetTableModel model = new PropertySheetTableModel();
        final PropertySheetPanel psp = new PropertySheetPanel(new PropertySheetTable(model));
        psp.setToolBarVisible(false);
        psp.setEditorFactory(CustomPropertyEditorRegistry.INSTANCE.getRegistry());
        psp.setRendererFactory(CustomPropertyRendererFactory.INSTANCE.getRegistry());
        psp.setDescriptionVisible(true);
        psp.setMode(PropertySheet.VIEW_AS_CATEGORIES);
        pane.add(psp, BorderLayout.CENTER);
        psp.setPreferredSize(new Dimension(250, 200));
        psp.setBorder(BorderFactory.createEtchedBorder());

        list.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (list.getSelectedValue() != null) {
                    model.setProperties(PropertiesPanelFactory.INSTANCE.createProperties(list.getSelectedValue()));
                    cur_ = (T) list.getSelectedValue();
                } else {
                    cur_ = null;
                    model.setProperties(new Property[]{});
                }
            }
        });

        psp.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (dirty_) {
                    list.setModel(new ArrayEditorListModel(elementsList_));
                    list.invalidate();
                }
            }
        });

        model.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                dirty_ = true;
                try {
                    Object o = list.getSelectedValue();
                    if (o != null) {
                        model.setProperties(PropertiesPanelFactory.INSTANCE.createProperties(o));
                    }
                } catch (RuntimeException err) {
                    String msg = err.getMessage();
                } finally {
                    model.fireTableStructureChanged();
                }
            }
        });

        final JPanel buttonPane = new JPanel();
        BoxLayout layout = new BoxLayout(buttonPane, BoxLayout.LINE_AXIS);
        buttonPane.setLayout(layout);
        final JButton addButton = new JButton(DemetraUiIcon.LIST_ADD_16);
        addButton.setPreferredSize(new Dimension(30, 30));
        addButton.setFocusPainted(false);
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dirty_ = true;
                try {
                    Constructor<T> constructor = c_.getConstructor(new Class[]{});
                    final T o = constructor.newInstance(new Object[]{});
                    elementsList_.add(o);
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            list.setModel(new ArrayEditorListModel(elementsList_));
                            list.setSelectedValue(o, true);
                            list.invalidate();
                        }
                    });

                } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });
        buttonPane.add(addButton);
        final JButton deleteButton = new JButton(DemetraUiIcon.LIST_REMOVE_16);
        deleteButton.setPreferredSize(new Dimension(30, 30));
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (cur_ == null) {
                        return;
                    }
                    dirty_ = true;
                    elementsList_.remove(cur_);
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            list.setModel(new ArrayEditorListModel(elementsList_));
                            list.invalidate();
                        }
                    });

                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        });
        buttonPane.add(deleteButton);
        buttonPane.add(Box.createGlue());
        final JButton okButton = new JButton("Done");
        okButton.setPreferredSize(new Dimension(60, 27));
        okButton.setFocusPainted(false);
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayEditorDialog.this.setVisible(false);
            }
        });
        buttonPane.add(okButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        pane.add(buttonPane, BorderLayout.SOUTH);
        pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        setMinimumSize(new Dimension(400, 200));
        setContentPane(pane);
        pack();
        setModal(true);
    }
}

class ArrayEditorListModel<T> extends DefaultListModel {

    private List<T> elementsList_;

    public ArrayEditorListModel(List<T> elements) {
        super();
        elementsList_ = elements;
    }

    @Override
    public int getSize() {
        return elementsList_.size();
    }

    @Override
    public Object getElementAt(int index) {
        return elementsList_.get(index);
    }
}
