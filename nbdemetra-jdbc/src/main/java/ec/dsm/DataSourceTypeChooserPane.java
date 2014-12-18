package ec.dsm;

import ec.tss.tsproviders.jdbc.dsm.datasource.DataSourceManager;
import ec.tss.tsproviders.jdbc.dsm.datasource.DefaultManagedDataSource;
import ec.tss.tsproviders.jdbc.dsm.datasource.interfaces.IManagedDataSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 *
 * @author Demortier Jeremy
 */
@Deprecated
public class DataSourceTypeChooserPane extends JPanel {

    public DataSourceTypeChooserPane() {
        super();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                build();

                SwingUtilities.getWindowAncestor(DataSourceTypeChooserPane.this).pack();
            }
        });

    }

    private void build() {
        setLayout(new MigLayout(new LC().fill(), new AC().fill(), new AC().fill()));

        add(new JLabel("Data Source Type:"));

        final JComboBox comboType = new JComboBox(DataSourceManager.INSTANCE.listDataSourceProviders().toArray());
        add(comboType, new CC().wrap());
        add(new JLabel(), new CC().growY().pushY().wrap("20px"));

        final JButton bOk = new JButton("Create new data source");
        add(bOk, new CC().skip().wrap());
        bOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.getWindowAncestor(bOk).setVisible(false);

                String providerQualifier = comboType.getSelectedItem().toString();
                IManagedDataSource mds = new DefaultManagedDataSource(providerQualifier);

                Object innerPane = new DataSourceConfigurationPane(mds, DataSourceManager.INSTANCE.listDataSourceProperties(providerQualifier));
                DialogDescriptor d = new DialogDescriptor(innerPane, "Data Source Properties");
                DialogDisplayer.getDefault().createDialog(d).setVisible(true);
            }
        });

        final JButton bCancel = new JButton("Cancel");
        add(bCancel, new CC().skip());
        bCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.getWindowAncestor(bCancel).setVisible(false);
            }
        });
    }
}
