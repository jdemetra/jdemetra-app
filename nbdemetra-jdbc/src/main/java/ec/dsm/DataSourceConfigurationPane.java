package ec.dsm;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import ec.tss.tsproviders.jdbc.dsm.datasource.DataSourceManager;
import ec.tss.tsproviders.jdbc.dsm.datasource.interfaces.IManagedDataSource;
import ec.tss.tsproviders.jdbc.dsm.identification.Account;
import ec.tss.tsproviders.jdbc.dsm.identification.AccountManager;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.util.List;
import javax.swing.JPasswordField;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * Panel used to configure the connection information to a database. It adapts itself
 * to the database type, presenting its properties. To be used in a dialog.
 * @author Demortier Jeremy
 */
@Deprecated
public class DataSourceConfigurationPane extends JPanel {

  private final IManagedDataSource dataSource_;
  private final HashMap<String, JTextField> propFields_;
  private final JButton bSave_;
  private final JPasswordField txtPwd_, txtPwd2_;

  public DataSourceConfigurationPane(final IManagedDataSource mds) {
    this(mds, mds.listProperties());
  }

  public DataSourceConfigurationPane(final IManagedDataSource mds, final List<String> properties) {
    dataSource_ = mds;
    propFields_ = new HashMap<>();
    bSave_ = new JButton();
    txtPwd_ = new JPasswordField();
    txtPwd2_ = new JPasswordField();

    SwingUtilities.invokeLater(() -> {
        build(properties);

        Dimension size = new Dimension(400, 350);
        SwingUtilities.getWindowAncestor(DataSourceConfigurationPane.this).setPreferredSize(size);
        SwingUtilities.getWindowAncestor(DataSourceConfigurationPane.this).setSize(size);
        SwingUtilities.getWindowAncestor(DataSourceConfigurationPane.this).setMinimumSize(size);
    });
  }

  private void build(List<String> properties) {
    setLayout(new MigLayout(new LC().fill(), new AC().fill(), new AC().fill()));

    // Display datasource type (read only)
    add(new JLabel("Data Source Type:"));
    add(new JLabel(dataSource_.getSourceType()), new CC().gap("20px").growX().pushX().wrap());

    // Display datasource name
    add(new JLabel("Data Source Name:"));
    final JTextField txtName = new JTextField();
    add(txtName, new CC().gap("20px").growX().pushX().wrap());

    if (dataSource_.getName() != null) {
      txtName.setText(dataSource_.getName());
    }

    // Display datasource properties
    for (String property : properties) {
      add(new JLabel(String.format("%s:", property)));
      JTextField txt = new JTextField();
      add(txt, new CC().gap("20px").growX().pushX().wrap());
      propFields_.put(property, txt);

      if (dataSource_.getProperty(property) != null) {
        txt.setText(dataSource_.getProperty(property));
      }
    }

    add(new JLabel(), new CC().wrap("15px"));

    // Security information
    add(new JLabel("User:"));
    final JTextField txtUser = new JTextField();
    add(txtUser, new CC().gap("20px").growX().pushX().wrap());

    add(new JLabel("Password:"));
    add(txtPwd_, new CC().gap("20px").growX().pushX().wrap());
    txtPwd_.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
        checkValidity();
      }
    });

    add(new JLabel("Confirm Password:"));
    add(txtPwd2_, new CC().gap("20px").growX().pushX().wrap());
    txtPwd2_.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
        checkValidity();
      }
    });

    Account acc = AccountManager.INSTANCE.getAccount(dataSource_.getSourceType(), dataSource_.getName());
    if (acc != null) {
      txtUser.setText(acc.getLogin());
      txtPwd_.setText(acc.getPassword());
      txtPwd2_.setText(acc.getPassword());
      checkValidity();
    }

    add(new JLabel(), new CC().growY().pushY().wrap("30px"));

    // Buttons
    bSave_.setText("Save");
    bSave_.addActionListener(event -> {
        // Remove old version if there was one
        DataSourceManager.INSTANCE.remove(dataSource_.getSourceType(), dataSource_.getName());
        AccountManager.INSTANCE.removeAccount(dataSource_.getSourceType(), dataSource_.getName());

        // Update information
        dataSource_.setName(txtName.getText());
        for (String property : propFields_.keySet()) {
            dataSource_.setProperty(property, propFields_.get(property).getText());
        }

        // Save new version
        DataSourceManager.INSTANCE.add(dataSource_.getSourceType(), dataSource_.getName(), dataSource_);
        AccountManager.INSTANCE.addAccount(dataSource_.getSourceType(), dataSource_.getName(),
                new Account(txtUser.getText(), new String(txtPwd_.getPassword())));

        SwingUtilities.getWindowAncestor(bSave_).setVisible(false);
    });

    add(bSave_, new CC().skip().gap("20px").growX().pushX().wrap());

    final JButton bCancel = new JButton("Cancel");
    bCancel.addActionListener(event -> {
        SwingUtilities.getWindowAncestor(bCancel).setVisible(false);
    });
    add(bCancel, new CC().skip().gap("20px").growX().pushX());

    checkValidity();
  }

  private void checkValidity() {
    bSave_.setEnabled(hasValidPassword());
  }

  private boolean hasValidPassword() {
    return txtPwd_.getPassword().length != 0
            && new String(txtPwd_.getPassword()).equals(new String(txtPwd2_.getPassword()));
  }
}
