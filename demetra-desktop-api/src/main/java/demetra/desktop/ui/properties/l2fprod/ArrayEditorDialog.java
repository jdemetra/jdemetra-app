package demetra.desktop.ui.properties.l2fprod;

import com.l2fprod.common.propertysheet.*;
import demetra.desktop.DemetraIcons;
import demetra.desktop.util.NbComponents;
import ec.util.list.swing.JLists;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.swing.*;

/**
 *
 * @author Demortier Jeremy
 * @author Mats Maggi
 * @param <T>
 */
public class ArrayEditorDialog<T> extends JDialog {

    private final Supplier<T> newItem;
    ;
    private final T[] initialItems;
    private List<T> currentList;
    private T current;
    private boolean dirty;

    public List<T> getElements() {
        return currentList;
    }

    public boolean isDirty() {
        return dirty;
    }

    public ArrayEditorDialog(final Window owner, T[] elements, Supplier<T> newItem, Function<T, T> duplicate) {
        super(owner);
        this.newItem = newItem;
        initialItems = elements;
        if (duplicate == null) {
            currentList = Arrays.<T>asList(elements);
        } else {
            currentList = Arrays.stream(elements).map(t -> duplicate.apply(t)).collect(Collectors.toCollection(ArrayList<T>::new));
        }

        final JPanel pane = new JPanel(new BorderLayout());
        pane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        final JList list = new JList(JLists.modelOf(currentList));
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

        psp.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (dirty) {
                    list.setModel(JLists.modelOf(currentList));
                    list.invalidate();
                }
            }
        });

        model.addPropertyChangeListener(evt -> {
            dirty = true;
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
        });

        final JPanel buttonPane = new JPanel();
        BoxLayout layout = new BoxLayout(buttonPane, BoxLayout.LINE_AXIS);
        buttonPane.setLayout(layout);
        final JButton addButton = new JButton(DemetraIcons.LIST_ADD_16);
        addButton.setPreferredSize(new Dimension(30, 30));
        addButton.setFocusPainted(false);
        addButton.addActionListener(event -> {
            dirty = true;
            final T o = newItem.get();
            currentList.add(o);
            SwingUtilities.invokeLater(() -> {
                list.setModel(JLists.modelOf(currentList));
                list.setSelectedValue(o, true);
                list.invalidate();
            });

        });
        buttonPane.add(addButton);
        final JButton deleteButton = new JButton(DemetraIcons.LIST_REMOVE_16);
        deleteButton.setPreferredSize(new Dimension(30, 30));
        deleteButton.setFocusPainted(false);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(event -> {
            try {
                if (current == null) {
                    return;
                }
                dirty = true;
                currentList.remove(current);
                SwingUtilities.invokeLater(() -> {
                    list.setModel(JLists.modelOf(currentList));
                    list.invalidate();
                });

            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        });
        buttonPane.add(deleteButton);

        final JButton clearButton = new JButton(DemetraIcons.BROOM);
        clearButton.setToolTipText("Clear");
        clearButton.setPreferredSize(new Dimension(30, 30));
        clearButton.setFocusPainted(false);
        clearButton.setEnabled(false);
        clearButton.addActionListener(event -> {
            try {
                if (list.getModel() != null) {
                    dirty = true;
                    currentList.clear();
                    SwingUtilities.invokeLater(() -> {
                        list.setModel(JLists.modelOf(currentList));
                        list.invalidate();
                    });
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        });
        buttonPane.add(clearButton);

        buttonPane.add(Box.createGlue());
        final JButton okButton = new JButton("Ok");
        okButton.setPreferredSize(new Dimension(80, 27));
        okButton.addActionListener(event -> ArrayEditorDialog.this.setVisible(false));
        okButton.setFocusPainted(false);
        buttonPane.add(okButton);
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(80, 27));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(event -> {
            currentList = Arrays.asList(initialItems);
            ArrayEditorDialog.this.setVisible(false);
        });
        buttonPane.add(cancelButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        pane.add(buttonPane, BorderLayout.SOUTH);
        pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        list.addListSelectionListener(event -> {
            if (list.getSelectedValue() != null) {
                deleteButton.setEnabled(true);
                model.setProperties(PropertiesPanelFactory.INSTANCE.createProperties(list.getSelectedValue()));
                current = (T) list.getSelectedValue();
            } else {
                deleteButton.setEnabled(false);
                current = null;
                model.setProperties(new Property[]{});
            }
        });

        list.addPropertyChangeListener(evt -> clearButton.setEnabled(list.getModel() != null && list.getModel().getSize() > 0));

//        addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent we) {
//                super.windowClosing(we);
//                elementsList_ = new ArrayList<>(Arrays.asList(elements));
//            }
//        });
//
        setMinimumSize(new Dimension(400, 200));
        setContentPane(pane);
        pack();
        setLocationRelativeTo(owner);
        setModal(true);
    }
}
