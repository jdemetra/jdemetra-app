/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import ec.util.list.swing.JLists;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import org.openide.windows.WindowManager;

/**
 *
 * @author Jean Palate
 */
public class ReportSelectionDialog extends JDialog {

    private String report;
    private JList<String> list;

    private void fillList() {
        List<ISaReportFactory> factories = SaReportManager.getDefault().getFactories();
        list.setModel(JLists.modelOf(factories.stream().map(ISaReportFactory::getReportName).toArray(String[]::new)));
    }

    public ReportSelectionDialog() {
        super(WindowManager.getDefault().getMainWindow(), true);
        setTitle("Choose report");

        final JButton btnOK_ = new JButton("OK");
        btnOK_.addActionListener(event -> setVisible(false));
        btnOK_.setEnabled(false);

        list = new JList<>();
        list.setPreferredSize(new Dimension(200, 200));
        list.addListSelectionListener(event -> {
            if (event.getLastIndex() < 0) {
                report = null;
                btnOK_.setEnabled(false);
            } else {
                report = list.getModel().getElementAt(event.getLastIndex()).toString();
                btnOK_.setEnabled(true);
            }
        });

        Box bnbox = Box.createHorizontalBox();
        bnbox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        bnbox.add(Box.createHorizontalGlue());
        bnbox.add(btnOK_);

        setLayout(new BorderLayout());
        add(list, BorderLayout.CENTER);
        add(bnbox, BorderLayout.SOUTH);
        this.setBounds(100, 100, 200, 300);
        fillList();

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                report = null;
            }
        });
    }

    public ISaReportFactory getReportFactory() {
        if (report == null) {
            return null;
        }
        List<ISaReportFactory> factories = SaReportManager.getDefault().getFactories();
        for (ISaReportFactory item : factories) {
            if (item.getReportName().equals(report)) {
                return item;
            }
        }
        return null;
    }
}
