/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties.l2fprod;

import ec.nbdemetra.ui.properties.ListSelection;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author Jean Palate
 */
public class ListSelectionDialog<T> extends JDialog {

    private ListSelection<T> list;

    public ListSelectionDialog(final Window owner) {
        super(owner);

        final JPanel pane = new JPanel(new BorderLayout());
        pane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        list = new ListSelection<>();
        list.setPreferredSize(new Dimension(150, 200));
        pane.add(list, BorderLayout.NORTH);

        final JPanel buttonPane = new JPanel();
        BoxLayout layout = new BoxLayout(buttonPane, BoxLayout.LINE_AXIS);
        buttonPane.setLayout(layout);
        buttonPane.add(Box.createGlue());
        final JButton okButton = new JButton("Done");
        okButton.setPreferredSize(new Dimension(60, 27));
        okButton.setFocusPainted(false);
        okButton.addActionListener(event -> setVisible(false));
        buttonPane.add(okButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        pane.add(buttonPane, BorderLayout.SOUTH);
        pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        setMinimumSize(new Dimension(400, 200));
        setContentPane(pane);
        pack();
        setModal(true);
    }

    public List<T> getSelection() {
        return list.getSelection();
    }

    public void set(List<T> input) {
        list.set(input);
    }

    public void set(List<T> input, List<T> sel) {
        list.set(input, sel);
    }
}
