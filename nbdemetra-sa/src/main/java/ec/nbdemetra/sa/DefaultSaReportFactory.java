/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ui.mru.SourceId;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.satoolkit.DecompositionMode;
import ec.tss.sa.EstimationPolicyType;
import ec.tss.sa.SaItem;
import ec.tss.sa.SaProcessing;
import ec.tstoolkit.MetaData;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.algorithm.ProcQuality;
import ec.tstoolkit.information.RegressionItem;
import ec.tstoolkit.sarima.SarimaModel;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.Paths;
import java.awt.Dimension;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.JTextPane;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = ISaReportFactory.class,
        position = 10)
public class DefaultSaReportFactory implements ISaReportFactory {

    public static final String NAME = "Default Text Report";
    private static final String NL = "\r\n", NL2 = "\r\n\r\n";
    private static final DecimalFormat pc2 = new DecimalFormat();
    private static final DecimalFormat d2 = new DecimalFormat();
    private final FileChooserBuilder fileChooserBuilder;
    private int nseries = 0;

    static {
        pc2.setMultiplier(100);
        pc2.setMaximumFractionDigits(2);
        pc2.setPositiveSuffix("%");
        d2.setMaximumFractionDigits(2);
    }

    public DefaultSaReportFactory() {
        this.fileChooserBuilder = new FileChooserBuilder(DefaultSaReportFactory.class);
        fileChooserBuilder.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text files", "txt"));
    }

    @Override
    public String getReportName() {
        return NAME;
    }

    @Override
    public String getReportDescription() {
        return NAME;
    }

    @Override
    public boolean createReport(SaProcessing processing) {
        StringWriter out = new StringWriter();
        if (createReport(out, processing)) {
            final JTextPane panel = new JTextPane();
            panel.setPreferredSize(new Dimension(300, 400));
            final DialogDescriptor dd = new DialogDescriptor(NbComponents.newJScrollPane(panel), "Report");
            String report = out.toString();
            panel.setText(report);
            if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
                java.io.File file = fileChooserBuilder.showSaveDialog();
                if (file != null) {
                    try {
                        String sfile = file.getAbsolutePath();
                        sfile = Paths.changeExtension(sfile, "txt");
                        try (Writer writer = Files.newBufferedWriter(java.nio.file.Paths.get(sfile))) {
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

    public synchronized boolean createReport(Writer out, SaProcessing processing) {
        nseries = 0;
        try {
            writeHeader(out);
            writeProcessingHeader(out, processing);
            out.write(NL2);
            writeSummary(out, processing);
            //out.write(NL2);
            //writeDetails(out, processing);

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

    private void writeHeader(Writer out) throws IOException {
        out.write("Software\t");
        out.write("JDemetra+" + NL2);
    }

    private void writeProcessingHeader(Writer out, SaProcessing processing) throws IOException {
        out.write("JDemetra+ specific information\t");
        out.write(NL);

        SourceId sourceId = WorkspaceFactory.getInstance().getActiveWorkspace().getSourceId();
        out.write("Workspace\t");
        if (sourceId != null) {
            out.write(sourceId.getLabel());
            for (Entry<String, String> p : sourceId.getDataSource().getParams().entrySet()) {
                out.write('\t');
                out.write(p.getKey());
                out.write('=');
                out.write(p.getValue());
            }
        }
        out.write(NL);
        out.write("Processing\t" + processing.getDocumentId());
        out.write(NL);
        MetaData metaData = processing.getMetaData();
        if (metaData != null) {
            for (String key : metaData.keySet()) {
                out.write(key + '\t' + metaData.get(key));
                out.write(NL);
            }
        }

        for (SaItem item : processing) {
            CompositeResults rslt = item.process();
            if (rslt == null) {
                continue;
            }
            if (rslt.getData("sa", TsData.class) != null) {
                ++nseries;
            }
        }

        out.write(NL);
        out.write("Number of series\t");
        out.write(Integer.toString(processing.size()) + NL);
        out.write("Number of successful estimations\t");
        out.write(Integer.toString(nseries) + NL2);
        writeMethods(out, processing);
    }

    private void writeSummary(Writer out, SaProcessing processing) throws IOException {
        out.write("Summary");
        out.write(NL2);
        writeTransformation(out, processing);
        writeCalendar(out, processing);
        writeEaster(out, processing);
        writeOutliers(out, processing);
        writeArima(out, processing);
        writeDecomposition(out, processing);
        writeDiagnostics(out, processing);
    }

    private void writeTransformation(Writer out, SaProcessing processing) throws IOException {
        int nadj = 0, nlog = 0;
        for (SaItem item : processing) {
            CompositeResults rslt = item.process();
            if (rslt == null) {
                continue;
            }
            Boolean badj = rslt.getData("adjust", Boolean.class);
            if (badj != null && badj) {
                ++nadj;
            }
            Boolean blog = rslt.getData("log", Boolean.class);
            if (blog != null && blog) {
                ++nlog;
            }
        }
        if (nlog == 0 && nadj == 0) {
            return;
        }
        out.write("Transformation" + NL);

        if (nlog > 0) {
            out.write("Log\t" + pc2.format(nlog / ((double) nseries)));
            out.write(NL);
        }
        if (nadj > 0) {
            out.write("Adjust\t" + pc2.format(nadj / ((double) nseries)));
            out.write(NL);
        }
        out.write(NL);
    }

    private void writeDetails(Writer out, SaProcessing processing) throws IOException {
        out.write("Details");
        out.write(NL2);
    }

    private void writeCalendar(Writer out, SaProcessing processing) throws IOException {
        SortedMap<Integer, Integer> ntd = new TreeMap<>();
        for (SaItem item : processing) {
            CompositeResults rslt = item.process();
            if (rslt == null) {
                continue;
            }
            Integer n = rslt.getData("regression.ntd", Integer.class);
            if (n == null) {
                n = 0;
            }
            Integer c = ntd.get(n);
            if (c == null) {
                ntd.put(n, 1);
            } else {
                ntd.put(n, c + 1);
            }
        }
        out.write("Trading days" + NL);
        for (Entry<Integer, Integer> entry : ntd.entrySet()) {
            int n = entry.getKey();
            if (n <= 1) {
                out.write(n + " var\t");
            } else {
                out.write(n + " vars\t");
            }
            out.write(pc2.format(entry.getValue() / ((double) nseries)));
            out.write(NL);
        }
        out.write(NL);
    }

    private void writeEaster(Writer out, SaProcessing processing) throws IOException {
        int neaster = 0;
        for (SaItem item : processing) {
            CompositeResults rslt = item.process();
            if (rslt == null) {
                continue;
            }

            RegressionItem easter = rslt.getData("regression.easter", RegressionItem.class);
            if (easter != null) {
                ++neaster;
            }
        }
        out.write("Easter\t");
        out.write(pc2.format(neaster / ((double) nseries)));
        out.write(NL2);
    }

    private void writeOutliers(Writer out, SaProcessing processing) throws IOException {
        int ntot = 0, nao = 0, nls = 0, ntc = 0, nso = 0;
        for (SaItem item : processing) {
            CompositeResults rslt = item.process();
            if (rslt == null) {
                continue;
            }
            Integer n = rslt.getData("regression.nout", Integer.class);
            if (n != null) {
                ntot += n;
            }
            n = rslt.getData("regression.noutao", Integer.class);
            if (n != null) {
                nao += n;
            }
            n = rslt.getData("regression.noutls", Integer.class);
            if (n != null) {
                nls += n;
            }
            n = rslt.getData("regression.nouttc", Integer.class);
            if (n != null) {
                ntc += n;
            }
            n = rslt.getData("regression.noutso", Integer.class);
            if (n != null) {
                nso += n;
            }
        }
        if (ntot == 0) {
            return;
        }
        double dsize = nseries;
        double dtot = ntot;
        out.write("Outliers" + NL);
        out.write("Average number by series" + NL);
        out.write("all\t");
        out.write(d2.format(ntot / dsize));
        out.write(NL);

        if (nao > 0) {
            out.write("ao\t");
            out.write(d2.format(nao / dsize));
            out.write(NL);
        }
        if (nls > 0) {
            out.write("ls\t");
            out.write(d2.format(nls / dsize));
            out.write(NL);
        }
        if (ntc > 0) {
            out.write("tc\t");
            out.write(d2.format(ntc / dsize));
            out.write(NL);
        }
        if (nso > 0) {
            out.write("so\t");
            out.write(d2.format(nso / dsize));
            out.write(NL);
        }
        out.write("Relative part" + NL);

        if (nao > 0) {
            out.write("ao\t");
            out.write(pc2.format(nao / dtot));
            out.write(NL);
        }
        if (nls > 0) {
            out.write("ls\t");
            out.write(pc2.format(nls / dtot));
            out.write(NL);
        }
        if (ntc > 0) {
            out.write("tc\t");
            out.write(pc2.format(ntc / dtot));
            out.write(NL);
        }
        if (nso > 0) {
            out.write("so\t");
            out.write(pc2.format(nso / dtot));
            out.write(NL);
        }
        out.write(NL);
    }

    private void writeArima(Writer out, SaProcessing processing) throws IOException {
        SortedMap<String, Integer> nmodel = new TreeMap<>();
        double size = 0;
        for (SaItem item : processing) {
            CompositeResults rslt = item.process();
            if (rslt == null) {
                continue;
            }
            SarimaModel m = rslt.getData("arima", SarimaModel.class);
            if (m != null) {
                ++size;
                String n = m.getSpecification().toString();
                Integer c = nmodel.get(n);
                if (c == null) {
                    nmodel.put(n, 1);
                } else {
                    nmodel.put(n, c + 1);
                }
            }
        }
        if (size == 0) {
            return;
        }
        out.write("Arima model" + NL);
        for (Entry<String, Integer> entry : nmodel.entrySet()) {
            out.write(entry.getKey());
            out.write('\t');
            out.write(pc2.format(entry.getValue() / size));
            out.write(NL);
        }
        out.write(NL);
    }

    private void writeDiagnostics(Writer out, SaProcessing processing) throws IOException {

        EnumMap<ProcQuality, Integer> qmap = new EnumMap<>(ProcQuality.class);
        double size = 0;
        for (SaItem item : processing) {
            CompositeResults rslt = item.process();
            if (rslt == null) {
                continue;
            }
            //ProcQuality q = rslt.getData("diagnostics.quality", ProcQuality.class);
            ProcQuality q = item.getQuality();
            if (q != null) {
                ++size;
                Integer c = qmap.get(q);
                if (c == null) {
                    qmap.put(q, 1);
                } else {
                    qmap.put(q, c + 1);
                }
            }
        }
        if (size == 0) {
            return;
        }
        out.write("Diagnostics" + NL);

        for (ProcQuality q : ProcQuality.values()) {
            out.write(q.name());
            out.write('\t');
            Integer n = qmap.get(q);
            out.write(pc2.format((n != null ? n : 0) / size));
            out.write(NL);
        }
        out.write(NL);
    }

    private void writeDecomposition(Writer out, SaProcessing processing) throws IOException {
        int nseas = 0;
        EnumMap<DecompositionMode, Integer> mmap = new EnumMap<>(DecompositionMode.class);
        for (SaItem item : processing) {
            CompositeResults rslt = item.process();
            if (rslt == null) {
                continue;
            }
            Boolean bseas = rslt.getData("decomposition.seasonality", Boolean.class);
            if (bseas != null && bseas) {
                ++nseas;
            }

            DecompositionMode mode = rslt.getData("decomposition.mode", DecompositionMode.class);
            if (mode != null) {
                Integer c = mmap.get(mode);
                if (c == null) {
                    mmap.put(mode, 1);
                } else {
                    mmap.put(mode, c + 1);
                }
            }
        }

        out.write("Decomposition" + NL);

        out.write("Seas. present\t" + pc2.format(nseas / ((double) nseries)));
        out.write(NL);
        out.write("Mode");
        out.write(NL);

        for (DecompositionMode m : DecompositionMode.values()) {
            out.write(m.name());
            out.write('\t');
            Integer n = mmap.get(m);
            out.write(pc2.format((n != null ? n : 0) / ((double) nseries)));
            out.write(NL);
        }

        out.write(NL);
    }

    private void writeMethods(Writer out, SaProcessing processing) throws IOException {
        writeSpecs(out, processing);
        writePolicy(out, processing);
    }

    private void writeSpecs(Writer out, SaProcessing processing) throws IOException {
        HashMap<String, Integer> mmap = new HashMap<>();
        for (SaItem item : processing) {
            String m = item.getDomainSpecification().toLongString();
            Integer c = mmap.get(m);
            if (c == null) {
                mmap.put(m, 1);
            } else {
                mmap.put(m, c + 1);
            }
        }
        double sz = processing.size();
        out.write("Specification" + NL);
        for (Map.Entry<String, Integer> spec : mmap.entrySet()) {
            out.write(spec.getKey());
            out.write('\t');
            out.write(pc2.format(spec.getValue() / sz));
            out.write(NL);
        }
        out.write(NL);
    }

    private void writePolicy(Writer out, SaProcessing processing) throws IOException {
        EnumMap<EstimationPolicyType, Integer> mmap = new EnumMap<>(EstimationPolicyType.class);
        for (SaItem item : processing) {
            EstimationPolicyType policy = item.getEstimationPolicy();
            Integer c = mmap.get(policy);
            if (c == null) {
                mmap.put(policy, 1);
            } else {
                mmap.put(policy, c + 1);
            }
        }
        double sz = processing.size();
        out.write("Estimation policy" + NL);
        for (EstimationPolicyType m : EstimationPolicyType.values()) {
            Integer n = mmap.get(m);
            if (n != null) {
                out.write(m.name());
                out.write('\t');
                out.write(pc2.format(n / sz));
                out.write(NL);
            }
        }
        out.write(NL);
    }
}
