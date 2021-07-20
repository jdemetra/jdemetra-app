package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author Demortier Jeremy
 */
public class MultiEnumPropertyEditor<T extends Class> extends AbstractPropertyEditor {

  public MultiEnumPropertyEditor(T clazz) {
    editor = new MultiEnumEditor<>(clazz);

  }

  @Override
  public Object getValue() {
    MultiEnumEditor mee = (MultiEnumEditor) editor;
    return mee.getValue();
  }

  @Override
  public void setValue(Object value) {
    if (null != value) {
      MultiEnumEditor mee = (MultiEnumEditor) editor;
      mee.setValue(value);
    }
  }
}

class MultiEnumEditor<T extends Class> extends JPanel {

  private List<Object> selection_;
  private Object[] enumValues_;
  private JCheckBox[] checkBoxes_;

  public Object getValue() {
    return selection_;
  }

  public void setValue(Object value) {
    if (value instanceof ArrayList) {
      selection_ = (List<Object>) value;
    }
  }

  public MultiEnumEditor(final T clazz) {
    setLayout(new BorderLayout());

    final JTextField textField = new JTextField();
    textField.setEditable(false);
    textField.setText(getValue() != null ? getValue().toString() : "");
    add(textField, BorderLayout.CENTER);

    final JButton button = new JButton("...");
    button.addActionListener(event -> {
        final JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(button));
        JPanel panel = new JPanel(new BorderLayout());
        JPanel choicesPanel = new JPanel();
        BoxLayout layout = new BoxLayout(choicesPanel, BoxLayout.PAGE_AXIS);
        choicesPanel.setLayout(layout);

        enumValues_ = clazz.getEnumConstants();
        if (null != enumValues_) {
            checkBoxes_ = new JCheckBox[enumValues_.length];
            
            for (int i = 0; i < enumValues_.length; i++) {
                checkBoxes_[i] = new JCheckBox(enumValues_[i].toString());
                choicesPanel.add(checkBoxes_[i]);
                if (null != selection_ && selection_.contains(enumValues_[i])) {
                    checkBoxes_[i].setSelected(true);
                }
            }
            
            final JButton applyButton = new JButton(new AbstractAction("OK") {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    selection_ = new ArrayList<>();
                    for (int j = 0; j < checkBoxes_.length; j++) {
                        if (checkBoxes_[j].isSelected()) {
                            selection_.add(enumValues_[j]);
                        }
                    }
                    dialog.setVisible(false);
                }
            });
            
            panel.add(choicesPanel, BorderLayout.CENTER);
            panel.add(applyButton, BorderLayout.SOUTH);
            
            dialog.setContentPane(panel);
            dialog.setModal(true);
            dialog.pack();
            dialog.setVisible(true);
        }
    });
    add(button, BorderLayout.EAST);
  }
}
