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
package demetra.desktop.disaggregation.ui;

import demetra.data.AggregationType;
import demetra.data.DoubleSeq;
import demetra.data.Parameter;
import demetra.html.AbstractHtmlElement;
import demetra.html.HtmlStream;
import demetra.html.HtmlTable;
import demetra.html.HtmlTableCell;
import demetra.html.HtmlTag;
import demetra.html.modelling.HtmlDiffuseLikelihood;
import demetra.math.matrices.Matrix;
import demetra.tempdisagg.univariate.TemporalDisaggregationSpec;
import demetra.timeseries.Ts;
import demetra.timeseries.TsData;
import demetra.timeseries.TsUnit;
import java.io.IOException;
import java.util.List;
import jdplus.dstats.T;
import jdplus.stats.DescriptiveStatistics;
import jdplus.stats.likelihood.DiffuseConcentratedLikelihood;
import jdplus.tempdisagg.univariate.TemporalDisaggregationDocument;
import jdplus.tempdisagg.univariate.TemporalDisaggregationResults;

/**
 *
 * @author Jean Palate
 */
public class ModelSummary extends AbstractHtmlElement {

    //private 
    private final TemporalDisaggregationResults results;
    private final List<Ts> input;
    private final TemporalDisaggregationSpec specification;

    public ModelSummary(TemporalDisaggregationDocument doc) {
        this.results = doc.getResult();
        this.input = doc.getInput();
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
        HtmlDiffuseLikelihood ll = new HtmlDiffuseLikelihood(results.getStats());
        stream.write(ll);
    }

    private void writeHeader(HtmlStream stream) throws IOException {
        String model;
        model = switch (specification.getResidualsModel()) {
            case Wn ->
                "OLS";
            case Ar1 ->
                "Chow-Lin";
            case RwAr1 ->
                "Litterman";
            default ->
                "Fernandez";
        };
        stream.write(HtmlTag.HEADER1, h1, model);
        stream.newLine();

    }

    private void writeModel(HtmlStream stream) throws IOException {
        if (!specification.getResidualsModel().hasParameter()) {
            return;
        }
        stream.write(HtmlTag.LINEBREAK);
        stream.write(HtmlTag.HEADER2, h2, "Model");
        stream.write("Rho = ");
        Parameter p = specification.getParameter();

        if (!p.isFixed()) {
            double[] parameters = results.getMaximum().getParameters();
            p = Parameter.estimated(parameters[0]);
        }

        stream.write(p.getValue());
        if (!p.isFixed()) {
            Matrix hessian = results.getMaximum().getHessian();
            double scale = results.getLikelihood().ssq() / (results.getLikelihood().degreesOfFreedom() - 1);
            stream.write(" [");
            stream.write(df4.format(Math.sqrt(hessian.get(0, 0) * scale)));
            stream.write("]");
        }
    }

    private void writeVariance(HtmlStream stream) throws IOException {
        TsData bench = results.getDisaggregatedSeries();
        TsData reg = results.getRegressionEffects();
        TsData smooth = TsData.subtract(bench, reg);
        if (reg == null) {
            return;
        }
        TsData bencha = bench.aggregate(TsUnit.YEAR, AggregationType.Sum, true);
        TsData rega = reg.aggregate(TsUnit.YEAR, AggregationType.Sum, true);
        TsData smootha = smooth.aggregate(TsUnit.YEAR, AggregationType.Sum, true);
        DescriptiveStatistics bstats = DescriptiveStatistics.of(bencha.getValues());
        DescriptiveStatistics rstats = DescriptiveStatistics.of(rega.getValues());
        DescriptiveStatistics sstats = DescriptiveStatistics.of(smootha.getValues());

        double varT = rstats.getVar() + sstats.getVar();

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
        stream.write(new HtmlTableCell(df2.format(100.0 * rstats.getVar() / varT), 100));
        stream.close(HtmlTag.TABLEROW);
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableCell("Smoothing", 100));
        stream.write(new HtmlTableCell(format(sstats.getVar()), 100));
        stream.write(new HtmlTableCell(df2.format(100.0 * sstats.getVar() / varT), 100));
        stream.close(HtmlTag.TABLEROW);
        stream.close(HtmlTag.TABLE);
    }

    private void writeRegression(HtmlStream stream) throws IOException {
        DiffuseConcentratedLikelihood ll = results.getLikelihood();
        DoubleSeq b = ll.coefficients();
        if (b.isEmpty()) {
            return;
        }
        int nhp = specification.getResidualsModel().getParametersCount();
        T t = new T(ll.degreesOfFreedom() - nhp);

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
//        if (specification.getOption() != TsDisaggregation.SsfOption.DKF) {
//            idx = results.getEstimatedSsf().getNonStationaryDim();
//        }
        if (specification.isConstant()) {
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("Constant", 100));
            stream.write(new HtmlTableCell(format(b.get(idx)), 100));
            double tval = ll.tstat(idx, nhp, true);
            stream.write(new HtmlTableCell(formatT(tval), 100));
            double prob = 1 - t.getProbabilityForInterval(-tval, tval);
            stream.write(new HtmlTableCell(df4.format(prob), 100));
            stream.close(HtmlTag.TABLEROW);
            ++idx;
        }

        if (specification.isTrend()) {
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("Trend", 100));
            stream.write(new HtmlTableCell(format(b.get(idx)), 100));
            double tval = ll.tstat(idx, nhp, true);
            stream.write(new HtmlTableCell(formatT(tval), 100));
            double prob = 1 - t.getProbabilityForInterval(-tval, tval);
            stream.write(new HtmlTableCell(df4.format(prob), 100));
            stream.close(HtmlTag.TABLEROW);
            ++idx;
        }
        for (int j = 1; j < input.size(); ++j, ++idx) {
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell(input.get(j).getName(), 100));
            stream.write(new HtmlTableCell(format(b.get(idx)), 100));
            double tval = ll.tstat(idx, nhp, true);
            stream.write(new HtmlTableCell(formatT(tval), 100));
            double prob = 1 - t.getProbabilityForInterval(-tval, tval);
            stream.write(new HtmlTableCell(df4.format(prob), 100));
            stream.close(HtmlTag.TABLEROW);
        }
        stream.close(HtmlTag.TABLE);
    }

}
