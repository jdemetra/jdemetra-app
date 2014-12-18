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
package ec.nbdemetra.disaggregation.ui;

import ec.benchmarking.simplets.TsDisaggregation;
import ec.tss.Ts;
import ec.tss.disaggregation.documents.DisaggregationResults;
import ec.tss.disaggregation.documents.DisaggregationSpecification;
import ec.tss.disaggregation.documents.TsDisaggregationModelDocument;
import ec.tss.html.AbstractHtmlElement;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlTable;
import ec.tss.html.HtmlTableCell;
import ec.tss.html.HtmlTag;
import ec.tss.html.implementation.HtmlLikelihood;
import ec.tstoolkit.Parameter;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.dstats.T;
import ec.tstoolkit.eco.DiffuseConcentratedLikelihood;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import java.io.IOException;

/**
 *
 * @author Jean Palate
 */
public class ModelSummary extends AbstractHtmlElement {

    //private 
    private final DisaggregationResults results;
    private final Ts[] input;
    private final DisaggregationSpecification specification;

    public ModelSummary(TsDisaggregationModelDocument doc) {
        this.results = doc.getResults();
        this.input = doc.getTs();
        this.specification = doc.getSpecification();
    }

    @Override
    public void write(HtmlStream stream) throws IOException {
        writeHeader(stream);
        writeLikelihood(stream);
        writeVariance(stream);
        writeModel(stream);
        writeRegression(stream);
    }

    private void writeLikelihood(HtmlStream stream) throws IOException {
        stream.write(HtmlTag.HEADER2, h2, "Likelihood statistics");
        HtmlLikelihood ll = new HtmlLikelihood(results.getLikelihoodStatistics());
        stream.write(ll);
    }

    private void writeHeader(HtmlStream stream) throws IOException {
        String model;
        switch (specification.getModel()) {
            case Wn:
                model = "OLS";
                break;
            case Ar1:
                model = "Chow-Lin";
                break;
            case RwAr1:
                model = "Litterman";
                break;
            default:
                model = "Fernandez";
                break;
        }
        stream.write(HtmlTag.HEADER1, h1, model);
        stream.newLine();

    }

    private void writeModel(HtmlStream stream) throws IOException {
        if (!specification.getModel().hasParameter()) {
            return;
        }
        stream.write(HtmlTag.LINEBREAK);
        stream.write(HtmlTag.HEADER2, h2, "Model");
        stream.write("Rho = ");
        Parameter p = specification.getParameter();
        if (!p.isFixed()) {
            p = results.getEstimatedParameter();
        }

        stream.write(p.getValue());
        if (!p.isFixed()) {
            stream.write(" [");
            stream.write(df4.format(p.getStde()));
            stream.write("]");
        }
    }

    private void writeVariance(HtmlStream stream) throws IOException {
        TsData bench = results.getData(DisaggregationResults.DISAGGREGATION, TsData.class);
        TsData reg = results.getData(DisaggregationResults.REGEFFECT, TsData.class);
        TsData smooth = results.getData(DisaggregationResults.SMOOTHING, TsData.class);
        if (reg == null)
            return;
        bench=bench.changeFrequency(TsFrequency.Yearly, TsAggregationType.Sum, true);
        reg=reg.changeFrequency(TsFrequency.Yearly, TsAggregationType.Sum, true);
        smooth=smooth.changeFrequency(TsFrequency.Yearly, TsAggregationType.Sum, true);
        DescriptiveStatistics bstats = new DescriptiveStatistics(bench);
        DescriptiveStatistics rstats = new DescriptiveStatistics(reg);
        DescriptiveStatistics sstats = new DescriptiveStatistics(smooth);
        
        double varT=rstats.getVar()+sstats.getVar();
        
        stream.write(HtmlTag.LINEBREAK);
        stream.write(HtmlTag.HEADER2, h2, "Variance");
        stream.open(new HtmlTable(0, 300));
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableCell("Component", 100));
        stream.write(new HtmlTableCell("Variance", 100));
        stream.write(new HtmlTableCell("% variance", 100));
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableCell("Indicators", 100));
        stream.write(new HtmlTableCell(format(rstats.getVar()), 100));
        stream.write(new HtmlTableCell(df2.format(100.0 * rstats.getVar() /varT), 100));
        stream.close(HtmlTag.TABLEROW);
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableCell("Smoothing", 100));
        stream.write(new HtmlTableCell(format(sstats.getVar()), 100));
        stream.write(new HtmlTableCell(df2.format(100.0 * sstats.getVar() /varT), 100));
        stream.close(HtmlTag.TABLEROW);
        stream.close(HtmlTag.TABLE);
    }

    private void writeRegression(HtmlStream stream) throws IOException {
        DiffuseConcentratedLikelihood ll = results.getLikelihood();
        double[] b = ll.getB();
        if (b == null) {
            return;
        }
        T t = new T();
        int nhp = specification.getModel().getParametersCount();
        t.setDegreesofFreedom(ll.getDegreesOfFreedom(true, nhp));

        stream.write(HtmlTag.LINEBREAK);
        stream.write(HtmlTag.HEADER2, h2, "Regression model");
        stream.open(new HtmlTable(0, 400));
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableCell("", 100));
        stream.write(new HtmlTableCell("Coefficients", 100));
        stream.write(new HtmlTableCell("T-Stat", 100));
        stream.write(new HtmlTableCell("P[|T| &gt t]", 100));
        stream.close(HtmlTag.TABLEROW);

        int idx = 0;
        if (specification.getOption() != TsDisaggregation.SsfOption.DKF) {
            idx = results.getEstimatedSsf().getNonStationaryDim();
        }
        if (specification.isConstant()) {
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("Constant", 100));
            stream.write(new HtmlTableCell(format(b[idx]), 100));
            double tval = ll.getTStat(idx, true, nhp);
            stream.write(new HtmlTableCell(formatT(tval), 100));
            double prob = 1 - t.getProbabilityForInterval(-tval, tval);
            stream.write(new HtmlTableCell(df4.format(prob), 100));
            stream.close(HtmlTag.TABLEROW);
            ++idx;
        }

        if (specification.isTrend()) {
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("Trend", 100));
            stream.write(new HtmlTableCell(format(b[idx]), 100));
            double tval = ll.getTStat(idx, true, nhp);
            stream.write(new HtmlTableCell(formatT(tval), 100));
            double prob = 1 - t.getProbabilityForInterval(-tval, tval);
            stream.write(new HtmlTableCell(df4.format(prob), 100));
            stream.close(HtmlTag.TABLEROW);
            ++idx;
        }
        for (int j = 1; j < input.length; ++j, ++idx) {
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell(input[j].getName(), 100));
            stream.write(new HtmlTableCell(format(b[idx]), 100));
            double tval = ll.getTStat(idx, true, nhp);
            stream.write(new HtmlTableCell(formatT(tval), 100));
            double prob = 1 - t.getProbabilityForInterval(-tval, tval);
            stream.write(new HtmlTableCell(df4.format(prob), 100));
            stream.close(HtmlTag.TABLEROW);
        }
        stream.close(HtmlTag.TABLE);
    }

}
