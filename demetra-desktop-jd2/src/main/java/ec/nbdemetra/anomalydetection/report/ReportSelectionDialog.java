/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.nbdemetra.anomalydetection.report;

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
    private final JList<ICheckLastReportFactory> list;

    private void fillList() {
        list.setModel(JLists.modelOf(CheckLastReportManager.getDefault().getFactories()));
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

    public ICheckLastReportFactory getReportFactory() {
        if (report == null) {
            return null;
        }
        List<ICheckLastReportFactory> factories = CheckLastReportManager.getDefault().getFactories();
        for (ICheckLastReportFactory item : factories) {
            if (item.getReportName().equals(report))
                return item;
        }
        return null;
    }
}
