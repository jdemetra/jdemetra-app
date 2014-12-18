/*
 * Copyright 2013-2014 National Bank of Belgium
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
package ec.nbdemetra.sa.advanced.ui;

import ec.tss.Ts;
import ec.tss.html.AbstractHtmlElement;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlStyle;
import ec.tss.html.HtmlTable;
import ec.tss.html.HtmlTableCell;
import ec.tss.html.HtmlTag;
import ec.tss.html.implementation.HtmlArima;
import ec.tss.html.implementation.HtmlLikelihood;
import ec.tss.sa.documents.MixedFrequenciesArimaDocument;
import ec.tss.sa.processors.MixedFrequenciesArimaProcessor;
import ec.tstoolkit.Parameter;
import ec.tstoolkit.arima.special.mixedfrequencies.MixedFrequenciesModelEstimation;
import ec.tstoolkit.arima.special.mixedfrequencies.MixedFrequenciesSpecification;
import ec.tstoolkit.dstats.T;
import ec.tstoolkit.eco.ConcentratedLikelihood;
import ec.tstoolkit.sarima.SarimaComponent;
import ec.tstoolkit.sarima.SarimaModel;
import ec.tstoolkit.sarima.SarimaSpecification;
import ec.tstoolkit.timeseries.regression.ITsVariable;
import ec.tstoolkit.timeseries.regression.TsVariableList;
import java.io.IOException;

/**
 *
 * @author Jean Palate
 */
public class MixedFrequenciesArimaModelSummary extends AbstractHtmlElement {

    //private 
    private final MixedFrequenciesModelEstimation results;
    private final Ts[] input;
    private final MixedFrequenciesSpecification specification;

    public MixedFrequenciesArimaModelSummary(MixedFrequenciesArimaDocument doc) {
        this.results = doc.getResults().get(MixedFrequenciesArimaProcessor.PROCESSING, MixedFrequenciesModelEstimation.class);
        this.input = doc.getInput();
        this.specification = doc.getSpecification();
    }

    @Override
    public void write(HtmlStream stream) throws IOException {
        writeHeader(stream);
        writeLikelihood(stream);
        stream.write(HtmlTag.LINEBREAK);
        stream.write(HtmlTag.HEADER2, h2, "Arima model");
        writeArima(stream);
        //writeVariance(stream);
        stream.write(HtmlTag.LINEBREAK);
        stream.write(HtmlTag.HEADER2, h2, "Regression");
        writeRegression(stream);
    }

    private void writeLikelihood(HtmlStream stream) throws IOException {
        stream.write(HtmlTag.HEADER2, h2, "Likelihood statistics");
        HtmlLikelihood ll = new HtmlLikelihood(results.getLikelihoodStatistics());
        stream.write(ll);
    }

    private void writeHeader(HtmlStream stream) throws IOException {
        String model;
        switch (specification.getBasic().getDataType()) {
            case Flow:
                model = "Flow";
                break;
            case Stock:
                model = "Stock";
                break;
            default:
                model = "";
                break;
        }
        stream.write(HtmlTag.HEADER1, h1, model);
        stream.newLine();

    }

    public void writeArima(HtmlStream stream) throws IOException {
        SarimaModel arima = results.getArima();
        SarimaSpecification sspec = arima.getSpecification();
        stream.write('[').write(sspec.toString()).write(']').newLines(2);
        stream.open(new HtmlTable(0, 200));
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableCell("", 100));
        stream.write(new HtmlTableCell("Coefficients", 100, HtmlStyle.Bold));
        stream.close(HtmlTag.TABLEROW);
        int P = sspec.getP();
        for (int j = 0; j < P; ++j) {
            stream.open(HtmlTag.TABLEROW);
            StringBuilder header = new StringBuilder();
            header.append("Phi(").append(j + 1).append(')');
            stream.write(new HtmlTableCell(header.toString(), 100));
            double val = arima.phi(j + 1);
            stream.write(new HtmlTableCell(df4.format(val), 100));
            stream.close(HtmlTag.TABLEROW);
        }
        int Q = sspec.getQ();
        for (int j = 0; j < Q; ++j) {
            stream.open(HtmlTag.TABLEROW);
            StringBuilder header = new StringBuilder();
            header.append("Theta(").append(j + 1).append(')');
            stream.write(new HtmlTableCell(header.toString(), 100));
            double val = arima.theta(j + 1);
            stream.write(new HtmlTableCell(df4.format(val), 100));
            stream.close(HtmlTag.TABLEROW);
        }
        int BP = sspec.getBP();
        for (int j = 0; j < BP; ++j) {
            stream.open(HtmlTag.TABLEROW);
            StringBuilder header = new StringBuilder();
            header.append("BPhi(").append(j + 1).append(')');
            stream.write(new HtmlTableCell(header.toString(), 100));
            double val = arima.bphi(j + 1);
            stream.write(new HtmlTableCell(df4.format(val), 100));
            stream.close(HtmlTag.TABLEROW);
        }
        int BQ = sspec.getBQ();
        for (int j = 0; j < BQ; ++j) {
            stream.open(HtmlTag.TABLEROW);
            StringBuilder header = new StringBuilder();
            header.append("BTheta(").append(j + 1).append(')');
            stream.write(new HtmlTableCell(header.toString(), 100));
            double val = arima.btheta(j + 1);
            stream.write(new HtmlTableCell(df4.format(val), 100));
            stream.close(HtmlTag.TABLEROW);
        }

        stream.close(HtmlTag.TABLE);
    }

//    private void writeVariance(HtmlStream stream) throws IOException {
//        TsData bench = results.getData(DisaggregationResults.DISAGGREGATION, TsData.class);
//        TsData reg = results.getData(DisaggregationResults.REGEFFECT, TsData.class);
//        TsData smooth = results.getData(DisaggregationResults.SMOOTHING, TsData.class);
//        if (reg == null)
//            return;
//        bench=bench.changeFrequency(TsFrequency.Yearly, TsAggregationType.Sum, true);
//        reg=reg.changeFrequency(TsFrequency.Yearly, TsAggregationType.Sum, true);
//        smooth=smooth.changeFrequency(TsFrequency.Yearly, TsAggregationType.Sum, true);
//        DescriptiveStatistics bstats = new DescriptiveStatistics(bench);
//        DescriptiveStatistics rstats = new DescriptiveStatistics(reg);
//        DescriptiveStatistics sstats = new DescriptiveStatistics(smooth);
//        
//        double varT=rstats.getVar()+sstats.getVar();
//        
//        stream.write(HtmlTag.LINEBREAK);
//        stream.write(HtmlTag.HEADER2, h2, "Variance");
//        stream.open(new HtmlTable(0, 300));
//        stream.open(HtmlTag.TABLEROW);
//        stream.write(new HtmlTableCell("Component", 100));
//        stream.write(new HtmlTableCell("Variance", 100));
//        stream.write(new HtmlTableCell("% variance", 100));
//        stream.open(HtmlTag.TABLEROW);
//        stream.write(new HtmlTableCell("Indicators", 100));
//        stream.write(new HtmlTableCell(format(rstats.getVar()), 100));
//        stream.write(new HtmlTableCell(df2.format(100.0 * rstats.getVar() /varT), 100));
//        stream.close(HtmlTag.TABLEROW);
//        stream.open(HtmlTag.TABLEROW);
//        stream.write(new HtmlTableCell("Smoothing", 100));
//        stream.write(new HtmlTableCell(format(sstats.getVar()), 100));
//        stream.write(new HtmlTableCell(df2.format(100.0 * sstats.getVar() /varT), 100));
//        stream.close(HtmlTag.TABLEROW);
//        stream.close(HtmlTag.TABLE);
//    }
//
    private void writeRegression(HtmlStream stream) throws IOException {
        ConcentratedLikelihood ll = results.getLikelihood();
        double[] b = ll.getB();
        if (b == null) {
            return;
        }
        T t = new T();
        int nhp = results.getArima().getParametersCount();
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
        TsVariableList var = results.getRegression();
        ITsVariable[] items = var.items();
        for (int j = 0; j < items.length; ++j) {
            for (int k = 0; k < items[j].getDim(); ++k, ++idx) {
                stream.open(HtmlTag.TABLEROW);
                stream.write(new HtmlTableCell(items[j].getItemDescription(k), 100));
                stream.write(new HtmlTableCell(format(b[idx]), 100));
                double tval = ll.getTStat(idx, true, nhp);
                stream.write(new HtmlTableCell(formatT(tval), 100));
                double prob = 1 - t.getProbabilityForInterval(-tval, tval);
                stream.write(new HtmlTableCell(df4.format(prob), 100));
                stream.close(HtmlTag.TABLEROW);
            }
        }
        stream.close(HtmlTag.TABLE);
    }

}
