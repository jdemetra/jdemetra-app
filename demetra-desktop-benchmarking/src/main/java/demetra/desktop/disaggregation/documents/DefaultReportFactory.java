/*
 * Copyright 2022 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.disaggregation.documents;

import demetra.data.AggregationType;
import demetra.data.DoubleSeq;
import demetra.desktop.ui.mru.SourceId;
import demetra.desktop.util.NbComponents;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.tempdisagg.univariate.TemporalDisaggregationSpec;
import demetra.timeseries.Ts;
import demetra.timeseries.TsData;
import demetra.timeseries.TsUnit;
import demetra.util.Paths;
import java.awt.Dimension;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JTextPane;
import jdplus.dstats.T;
import jdplus.stats.DescriptiveStatistics;
import jdplus.stats.likelihood.DiffuseConcentratedLikelihood;
import jdplus.stats.likelihood.LikelihoodStatistics;
import jdplus.tempdisagg.univariate.TemporalDisaggregationDocument;
import jdplus.tempdisagg.univariate.TemporalDisaggregationResults;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = TemporalDisaggregationReportFactory.class,
        position = 10)
public class DefaultReportFactory implements TemporalDisaggregationReportFactory {
    
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
    public boolean createReport(Map<String, TemporalDisaggregationDocument> processing) {
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
                        try (FileWriter writer = new FileWriter(sfile, Charset.defaultCharset())) {
                            writer.append(report);
                        }
                    } catch (IOException ex) {
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
    
    public synchronized boolean createReport(Writer out, Map<String, TemporalDisaggregationDocument> processing) {
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
    
    private void writeProcessingHeader(Writer out, Map<String, TemporalDisaggregationDocument> processing) throws IOException {
        
        SourceId sourceId = WorkspaceFactory.getInstance().getActiveWorkspace().getSourceId();
        out.write("Workspace\t");
        if (sourceId != null) {
            out.write(sourceId.getLabel());
            for (Entry<String, String> p : sourceId.getDataSource().getParameters().entrySet()) {
                out.write('\t');
                out.write(p.getKey());
                out.write('=');
                out.write(p.getValue());
            }
        }
        out.write(NL);
    }
    
    private void writeDetails(Writer out, Map<String, TemporalDisaggregationDocument> processing) throws IOException {
        for (Map.Entry<String, TemporalDisaggregationDocument> item : processing.entrySet()) {
            out.write(item.getKey());
            out.write(NL);
            writeDetail(out, item.getValue());
            out.write(NL);
        }
    }
    
    private void writeDetail(Writer out, TemporalDisaggregationDocument doc) throws IOException {
        ReportItem cur = new ReportItem();
        writeModel(out, doc);
        TemporalDisaggregationResults results = doc.getResult();
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
    
    private double regSignificance(TemporalDisaggregationResults results) {
        TsData reg = results.getRegressionEffects();
        if (reg == null) {
            return 0;
        }
        TsData smooth = TsData.subtract(results.getOriginalSeries(), reg);
        reg = reg.aggregate(TsUnit.YEAR, AggregationType.Sum, true);
        smooth = smooth.aggregate(TsUnit.YEAR, AggregationType.Sum, true);
        DescriptiveStatistics rstats = DescriptiveStatistics.of(reg.getValues());
        DescriptiveStatistics sstats = DescriptiveStatistics.of(smooth.getValues());
        
        double varT = rstats.getVar() + sstats.getVar();
        return rstats.getVar() / varT;
    }
    
    private void writeModel(Writer out, TemporalDisaggregationDocument doc) throws IOException {
        TemporalDisaggregationResults results = doc.getResult();
        TemporalDisaggregationSpec specification = doc.getSpecification();
        out.write(specification.toString());
        if (specification.getResidualsModel().hasParameter()) {
            if (results != null) {
                out.write("\trho=");
                out.write(Double.toString(results.getMaximum().getParameters()[0]));
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
            List<Ts> input = doc.getInput();
            for (int j = 1; j < input.size(); ++j) {
                out.write(input.get(j).getName());
                out.write(NL);
            }
            out.write(NL);
        } else {
            DiffuseConcentratedLikelihood ll = results.getLikelihood();
            DoubleSeq b = ll.coefficients();
            if (b.isEmpty())
                return;
            int nhp = results.getHyperParametersCount();
            T t = new T(ll.degreesOfFreedom()-nhp);
            
            int idx = 0;
//            if (specification.getOption() != TsDisaggregation.SsfOption.DKF) {
//                idx = results.getEstimatedSsf().getNonStationaryDim();
//            }
            if (specification.isConstant()) {
                out.write("Constant");
                out.write('\t');
                out.write(Double.toString(b.get(idx)));
                out.write('\t');
                double tval = ll.tstat(idx, nhp, true);
                double prob = 1 - t.getProbabilityForInterval(-tval, tval);
                out.write(d3.format(prob));
                out.write(NL);
                ++idx;
            }
            if (specification.isTrend()) {
                out.write("Trend");
                out.write('\t');
                out.write(Double.toString(b.get(idx)));
                out.write('\t');
                double tval = ll.tstat(idx, nhp, true);
                double prob = 1 - t.getProbabilityForInterval(-tval, tval);
                out.write(d3.format(prob));
                out.write(NL);
                ++idx;
            }
            List<Ts> input = doc.getInput();
            for (int j = 1; j < input.size(); ++j, ++idx) {
                out.write(input.get(j).getName());
                out.write('\t');
                out.write(Double.toString(b.get(idx)));
                out.write('\t');
                double tval = ll.tstat(idx, nhp, true);
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
        DescriptiveStatistics stats = DescriptiveStatistics.ofInternal(q);
        
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