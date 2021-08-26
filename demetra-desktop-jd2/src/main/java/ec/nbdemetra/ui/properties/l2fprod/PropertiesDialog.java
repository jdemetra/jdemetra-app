package ec.nbdemetra.ui.properties.l2fprod;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author Demortier Jeremy
 */
public class PropertiesDialog extends JDialog {

    private JButton bApply_;
    private JButton bCancel_;
    private final Object clone_;
    private final Action applyAction_;

    public PropertiesDialog(Frame owner, boolean modal, Object clone, Action applyAction) {
        super(owner, modal);

        clone_ = clone;
        applyAction_ = applyAction;
        buildUI();
        pack();
    }

    private void buildUI() {
        this.setLocationByPlatform(true);
        setLayout(new BorderLayout());

        add(PropertiesPanelFactory.INSTANCE.createPanel(clone_), BorderLayout.CENTER);

        bApply_ = new JButton(applyAction_);
        bApply_.addActionListener(event -> setVisible(false));

        bCancel_ = new JButton(new AbstractAction(isModal() ? "Cancel" : "Close") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        JPanel buttonsPane = new JPanel();
        BoxLayout boxes = new BoxLayout(buttonsPane, BoxLayout.LINE_AXIS);
        buttonsPane.setLayout(boxes);
        buttonsPane.add(Box.createHorizontalGlue());
        buttonsPane.add(bApply_);
        buttonsPane.add(bCancel_);
        buttonsPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(buttonsPane, BorderLayout.SOUTH);
    }
}
