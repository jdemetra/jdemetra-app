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

import demetra.ui.util.NbComponents;
import ec.tss.tsproviders.utils.MultiLineNameUtil;
import ec.tstoolkit.utilities.Paths;
import java.awt.Dimension;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import javax.swing.JTextPane;
import nbbrd.service.ServiceProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.Exceptions;

/**
 *
 * @author Mats Maggi
 */
@ServiceProvider
public final class DefaultCheckLastReport implements ICheckLastReportFactory {

    private final FileChooserBuilder fileChooserBuilder;
    private static final String NL = "\r\n", NL2 = "\r\n\r\n";

    public DefaultCheckLastReport() {
        this.fileChooserBuilder = new FileChooserBuilder(DefaultCheckLastReport.class);
        fileChooserBuilder.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text files", "txt"));
    }

    @Override
    public String getReportName() {
        return "Text report";
    }

    @Override
    public String getReportDescription() {
        return "Simple text version of the Check Last processing";
    }

    @Override
    public boolean createReport(Map parameters) {
        StringWriter out = new StringWriter();
        if (createReport(out, parameters)) {
            final JTextPane panel = new JTextPane();
            panel.setPreferredSize(new Dimension(500, 400));
            final DialogDescriptor dd = new DialogDescriptor(NbComponents.newJScrollPane(panel), "Report");
            String report = out.toString();
            panel.setText(report);
            if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
                java.io.File file = fileChooserBuilder.showSaveDialog();
                if (file != null) {
                    try {
                        String sfile = file.getAbsolutePath();
                        sfile = Paths.changeExtension(sfile, "txt");
                        try (FileWriter writer = new FileWriter(sfile)) {
                            writer.append(report);
                        }
                    } catch (Exception ex) {
                        return false;
                    }
                } else {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private boolean createReport(StringWriter out, Map parameters) {
        try {
            writeHeader(out, parameters);
            writeAnomalies(out, parameters);
            writeInvalid(out, parameters);
            writeEmpty(out, parameters);
            
            return true;
        } catch (IOException ex) {
            return false;
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void writeHeader(Writer out, Map parameters) throws IOException {
        out.write("Software\tJDemetra+" + NL2);
        out.write("Check Last processing" + NL2);
        out.write("Number of series : " + String.valueOf(parameters.get("_NB_OF_SERIES")) + NL);
        out.write("Number of anomalies : " + String.valueOf(parameters.get("_NB_ANOMALY")) + NL);
        out.write("Number of last periods checked : " + String.valueOf(parameters.get("_NB_CHECK_LAST")) + NL);
        out.write("Specification used : " + String.valueOf(parameters.get("_SPECIFICATION")) + NL2);
        out.write("Sensitivity :" + NL);
        out.write("Orange cells from : " + String.valueOf(parameters.get("_ORANGE_CELLS")) + NL);
        out.write("Red cells from : " + String.valueOf(parameters.get("_RED_CELLS")) + NL);
        out.write("Series sorted by : " + String.valueOf(parameters.get("_SORTING")) + NL);
    }
    
    private void writeAnomalies(Writer out, Map parameters) throws IOException {
        out.write(NL);
        out.write("List of Anomalies" + NL2);
        
        List<AnomalyPojo> valid = (List<AnomalyPojo>)parameters.get("_VALID");
        
        if (valid.isEmpty()) {
            out.write("No anomalies detected..." + NL);
        } else {
            out.write("Name \t\t\t Period  \t Abs error \t Rel error" + NL);
            for (AnomalyPojo p : valid) {
                out.write(p.toString() + NL);
            }
        }
    }
    
    private void writeInvalid(Writer out, Map parameters) throws IOException {
        out.write(NL);
        out.write("Invalid Series" + NL2);
        
        List<AnomalyPojo> invalid = (List<AnomalyPojo>)parameters.get("_INVALID");
        
        if (invalid.isEmpty()) {
            out.write("No invalid series..." + NL);
        } else {
            for (AnomalyPojo p : invalid) {
                out.write(MultiLineNameUtil.join(p.getTsName()) + NL);
            }
        }
    }
    
    private void writeEmpty(Writer out, Map parameters) throws IOException {
        out.write(NL);
        out.write("Empty Series" + NL2);
        
        List<AnomalyPojo> empty = (List<AnomalyPojo>)parameters.get("_EMPTY");
        
        if (empty.isEmpty()) {
            out.write("No empty series..." + NL);
        } else {
            for (AnomalyPojo p : empty) {
                out.write(MultiLineNameUtil.join(p.getTsName()) + NL);
            }
        }
    }
    
    @Override
    public String toString() {
        return getReportName();
    }
}
