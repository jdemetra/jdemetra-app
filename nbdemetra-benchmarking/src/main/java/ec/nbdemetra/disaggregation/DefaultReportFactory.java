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
package ec.nbdemetra.disaggregation;

import ec.benchmarking.simplets.TsDisaggregation;
import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ui.mru.SourceId;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.tss.Ts;
import ec.tss.disaggregation.documents.DisaggregationResults;
import ec.tss.disaggregation.documents.DisaggregationSpecification;
import ec.tss.disaggregation.documents.ITsDisaggregationReportFactory;
import ec.tss.disaggregation.documents.TsDisaggregationModelDocument;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.dstats.T;
import ec.tstoolkit.eco.DiffuseConcentratedLikelihood;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.utilities.Paths;
import java.awt.Dimension;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JTextPane;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ITsDisaggregationReportFactory.class,
        position = 10)
public class DefaultReportFactory implements ITsDisaggregationReportFactory {
    
    public static class ReportItem {
        
        public double regQuality;
    }
    public static final String NAME = "Default report";
    private static final String NL = "\r\n", NL2 = "\r\n\r\n";
    private static final DecimalFormat pc2 = new DecimalFormat();
    private static final DecimalFormat d2 = new DecimalFormat(), d3 = new DecimalFormat();
    private final FileChooserBuilder fileChooserBuilder;
    public final List<ReportItem> items = new ArrayList<>();
    
    static {
        pc2.setMultiplier(100);
        pc2.setMaximumFractionDigits(2);
        pc2.setPositiveSuffix("%");
        d2.setMaximumFractionDigits(2);
        d2.setMaximumFractionDigits(3);
    }
    
    public DefaultReportFactory() {
        this.fileChooserBuilder = new FileChooserBuilder(DefaultReportFactory.class);
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
    public boolean createReport(Map<String, TsDisaggregationModelDocument> processing) {
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
    
    public synchronized boolean createReport(Writer out, Map<String, TsDisaggregationModelDocument> processing) {
        try {
            items.clear();
            writeHeader(out);
            writeProcessingHeader(out, processing);
            out.write(NL2);
            writeDetails(out, processing);
            writeSummary(out);
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
        out.write(new Date().toString() + NL2);
    }
    
    private void writeProcessingHeader(Writer out, Map<String, TsDisaggregationModelDocument> processing) throws IOException {
        
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
    }
    
    private void writeDetails(Writer out, Map<String, TsDisaggregationModelDocument> processing) throws IOException {
        for (Map.Entry<String, TsDisaggregationModelDocument> item : processing.entrySet()) {
            out.write(item.getKey());
            out.write(NL);
            writeDetail(out, item.getValue());
            out.write(NL);
        }
    }
    
    private void writeDetail(Writer out, TsDisaggregationModelDocument doc) throws IOException {
        ReportItem cur = new ReportItem();
        writeModel(out, doc);
        DisaggregationResults results = doc.getResults();
        if (results != null) {
            out.write("Regressor significance\t");
            cur.regQuality = regSignificance(results);
            out.write(pc2.format(cur.regQuality));
            out.write(NL);
        } else {
            out.write("NOT PROCESSED");
            out.write(NL);
        }
        items.add(cur);
    }
    
    private double regSignificance(DisaggregationResults results) {
        TsData reg = results.getData(DisaggregationResults.REGEFFECT, TsData.class);
        if (reg == null) {
            return 0;
        }
        TsData smooth = results.getData(DisaggregationResults.SMOOTHING, TsData.class);
        reg = reg.changeFrequency(TsFrequency.Yearly, TsAggregationType.Sum, true);
        smooth = smooth.changeFrequency(TsFrequency.Yearly, TsAggregationType.Sum, true);
        DescriptiveStatistics rstats = new DescriptiveStatistics(reg);
        DescriptiveStatistics sstats = new DescriptiveStatistics(smooth);
        
        double varT = rstats.getVar() + sstats.getVar();
        return rstats.getVar() / varT;
    }
    
    private void writeModel(Writer out, TsDisaggregationModelDocument doc) throws IOException {
        DisaggregationResults results = doc.getResults();
        DisaggregationSpecification specification = doc.getSpecification();
        out.write(specification.toString());
        if (specification.getModel().hasParameter()) {
            if (results != null) {
                out.write("\trho=");
                out.write(results.getEstimatedParameter().toString());
            } else if (specification.getParameter().isFixed()) {
                out.write("\trho=");
                out.write(specification.getParameter().toString());
            }
        }
        out.write(NL2);
        // write regression variables
        if (results == null) {
            if (specification.isConstant()) {
                out.write("Constant");
                out.write(NL);
            }
            if (specification.isTrend()) {
                out.write("Trend");
                out.write(NL);
            }
            Ts[] input = doc.getTs();
            for (int j = 1; j < input.length; ++j) {
                out.write(input[j].getName());
                out.write(NL);
            }
            out.write(NL);
        } else {
            DiffuseConcentratedLikelihood ll = results.getLikelihood();
            double[] b = ll.getB();
            if (b == null) {
                return;
            }
            T t = new T();
            int nhp = specification.getModel().getParametersCount();
            t.setDegreesofFreedom(ll.getDegreesOfFreedom(true, nhp));
            
            int idx = 0;
            if (specification.getOption() != TsDisaggregation.SsfOption.DKF) {
                idx = results.getEstimatedSsf().getNonStationaryDim();
            }
            if (specification.isConstant()) {
                out.write("Constant");
                out.write('\t');
                out.write(Double.toString(b[idx]));
                out.write('\t');
                double tval = ll.getTStat(idx, true, nhp);
                double prob = 1 - t.getProbabilityForInterval(-tval, tval);
                out.write(d3.format(prob));
                out.write(NL);
                ++idx;
            }
            if (specification.isTrend()) {
                out.write("Trend");
                out.write('\t');
                out.write(Double.toString(b[idx]));
                out.write('\t');
                double tval = ll.getTStat(idx, true, nhp);
                double prob = 1 - t.getProbabilityForInterval(-tval, tval);
                out.write(d3.format(prob));
                out.write(NL);
                ++idx;
            }
            Ts[] input = doc.getInput();
            for (int j = 1; j < input.length; ++j, ++idx) {
                out.write(input[j].getName());
                out.write('\t');
                out.write(Double.toString(b[idx]));
                out.write('\t');
                double tval = ll.getTStat(idx, true, nhp);
                double prob = 1 - t.getProbabilityForInterval(-tval, tval);
                out.write(d3.format(prob));
                out.write(NL);
            }
            out.write(NL);
        }
    }
    
    private void writeSummary(Writer out) throws IOException {
        out.write(NL);
        out.write("Summary");
        out.write(NL2);
        out.write("Quality");
        out.write(NL2);
        double[] q = new double[items.size()];
        int i = 0;
        for (ReportItem item : items) {
            q[i++] = item.regQuality;
        }
        double n=q.length;        
        DescriptiveStatistics stats = new DescriptiveStatistics(q);
        
        out.write("Average: ");
        out.write(pc2.format(stats.getAverage()));
        out.write(NL);
        out.write("High: ");
        out.write(pc2.format(stats.countBetween(.9, 1)/n));
        out.write(NL);
        out.write("Acceptable: ");
        out.write(pc2.format(stats.countBetween(.8, .9)/n));
        out.write(NL);
        out.write("Low: ");
        out.write(pc2.format(stats.countBetween(.7, .8)/n));
        out.write(NL);
        out.write("Very low: ");
        out.write(pc2.format(stats.countBetween(.5, .7)/n));
        out.write(NL);
        out.write("Rejected (or missing) indicator: ");
        out.write(pc2.format(stats.countBetween(0, .5)/n));
        out.write(NL);
    }
}