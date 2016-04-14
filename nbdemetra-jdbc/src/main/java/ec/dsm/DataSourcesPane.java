package ec.dsm;

import ec.nbdemetra.ui.NbComponents;
import ec.tss.tsproviders.jdbc.dsm.datasource.DataSourceManager;
import ec.tss.tsproviders.jdbc.dsm.identification.AccountManager;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 * Panel presenting the different databases available in the manager; made to be
 * displayed in a dialog.
 *
 * @author Demortier Jeremy
 */
@Deprecated
public class DataSourcesPane extends JPanel {

    public DataSourcesPane() {
        super();

        SwingUtilities.invokeLater(() -> {
            build();
            
            Dimension size = new Dimension(400, 500);
            SwingUtilities.getWindowAncestor(DataSourcesPane.this).setPreferredSize(size);
            SwingUtilities.getWindowAncestor(DataSourcesPane.this).setSize(size);
            
            SwingUtilities.getWindowAncestor(DataSourcesPane.this).addWindowListener(new WindowAdapter() {
                
                @Override
                public void windowClosing(WindowEvent e) {
                    DataSourceManager.INSTANCE.save();
                    AccountManager.INSTANCE.save();
                }
            });
        });

    }

    private void build() {
        setLayout(new MigLayout(new LC().fill(), new AC().fill(), new AC().fill()));

        final JTable table = new JTable();
        table.setFillsViewportHeight(true);
        add(NbComponents.newJScrollPane(table), new CC().growX().pushX().spanY(4));
        table.setModel(new DataSourceTableModel());

        final JButton bAdd = new JButton("Add");
        bAdd.addActionListener(event -> {
            DialogDescriptor d = new DialogDescriptor(new DataSourceTypeChooserPane(), "Choose data source type");
            DialogDisplayer.getDefault().createDialog(d).setVisible(true);
            table.setModel(new DataSourceTableModel());
        });
        add(bAdd, new CC().wrap());

        JButton bRemove = new JButton("Remove");
        add(bRemove, new CC().skip().wrap());
        bRemove.addActionListener(event -> {
            if (table.getSelectedRow() >= 0) {
                Object[] options = {"Remove the information from the manager.", "Keep the information!"};
                int n = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(bAdd),
                        "Removing this information from the manager may prevent you from being able "
                                + "to consult the database.\n\nRemove information anyway?",
                        "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                if (n == JOptionPane.YES_OPTION) {
                    String provider = table.getModel().getValueAt(
                            table.convertRowIndexToModel(table.getSelectedRow()),
                            table.convertColumnIndexToView(1)).toString();
                    String dbName = table.getModel().getValueAt(
                            table.convertRowIndexToModel(table.getSelectedRow()),
                            table.convertColumnIndexToView(0)).toString();
                    
                    DataSourceManager.INSTANCE.remove(provider, dbName);
                    AccountManager.INSTANCE.removeAccount(provider, dbName);
                    
                    table.setModel(new DataSourceTableModel());
                }
            }
        });

        JButton bEdit = new JButton("Edit");
        add(bEdit, new CC().skip().wrap());
        bEdit.addActionListener(event -> {
            if (table.getSelectedRow() >= 0) {
                String provider = table.getModel().getValueAt(
                        table.convertRowIndexToModel(table.getSelectedRow()),
                        table.convertColumnIndexToView(1)).toString();
                String dbName = table.getModel().getValueAt(
                        table.convertRowIndexToModel(table.getSelectedRow()),
                        table.convertColumnIndexToView(0)).toString();
                
                DialogDescriptor d = new DialogDescriptor(new DataSourceConfigurationPane(DataSourceManager.INSTANCE.getManagedDataSource(provider, dbName)), "Edit information");
                DialogDisplayer.getDefault().createDialog(d).setVisible(true);
            }
            table.setModel(new DataSourceTableModel());
        });

        add(new JLabel(), new CC().growY().pushY().skip());
    }
}
