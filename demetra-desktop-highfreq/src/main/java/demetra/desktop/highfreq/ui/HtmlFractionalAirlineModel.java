/*
 * Copyright 2023 National Bank of Belgium
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
package demetra.desktop.highfreq.ui;

import demetra.data.DoubleSeq;
import demetra.data.DoubleSeqCursor;
import demetra.html.AbstractHtmlElement;
import demetra.html.Bootstrap4;
import demetra.html.HtmlStream;
import demetra.html.HtmlTable;
import demetra.html.HtmlTableCell;
import demetra.html.HtmlTag;
import demetra.html.modelling.HtmlLikelihood;
import demetra.math.matrices.Matrix;
import demetra.timeseries.TsDomain;
import demetra.timeseries.regression.HolidaysVariable;
import demetra.timeseries.regression.IOutlier;
import demetra.timeseries.regression.ITsVariable;
import demetra.timeseries.regression.Variable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jdplus.arima.ArimaModel;
import jdplus.dstats.T;
import jdplus.highfreq.extendedairline.ExtendedAirlineDescription;
import jdplus.highfreq.regarima.HighFreqRegArimaModel;
import jdplus.modelling.GeneralLinearModel;
import jdplus.modelling.regression.RegressionDesc;
import jdplus.stats.likelihood.LikelihoodStatistics;

/**
 *
 * @author PALATEJ
 */
public class HtmlFractionalAirlineModel extends AbstractHtmlElement {

    private final HighFreqRegArimaModel<ArimaModel, ExtendedAirlineDescription> model;
    private final boolean summary;

    public HtmlFractionalAirlineModel(final HighFreqRegArimaModel model, boolean summary) {
        this.model = model;
        this.summary = summary;
    }

    @Override
    public void write(HtmlStream stream) throws IOException {
        writeSummary(stream);
        if (summary) {
            return;
        }
        writeDetails(stream);
    }

    private void writeSummary(HtmlStream stream) throws IOException {
        TsDomain edom = model.getEstimation().getDomain();
        stream.write(HtmlTag.HEADER1, "Summary").newLine();
        stream.write("Estimation span: [").write(edom.getStartPeriod().display());
        stream.write(" - ").write(edom.getLastPeriod().display()).write(']').newLine();
        if (model.getDescription().isLogTransformation()) {
            stream.write("Series has been log-transformed").newLine();
        }
        model.getDescription().getVariables();
        int no = countVariables(IOutlier.class, false);
        if (no > 1) {
            stream.write(Integer.toString(no)).write(" detected outliers").newLine();
        } else if (no == 1) {
            stream.write(Integer.toString(no)).write(" detected outlier").newLine();
        }
    }

    private void writeDetails(HtmlStream stream) throws IOException {

        GeneralLinearModel.Estimation estimation = model.getEstimation();

        stream.write(HtmlTag.HEADER1, "Final model");
        stream.newLine();
        stream.write(HtmlTag.HEADER2, "Likelihood statistics");
        stream.write(new HtmlLikelihood(estimation.getStatistics()));
        writeScore(stream);
        stream.write(HtmlTag.LINEBREAK);
        stream.write(HtmlTag.HEADER2, "Extended airline model");
        writeModel(stream);
        stream.write(HtmlTag.LINEBREAK);
        stream.write(HtmlTag.HEADER2, "Regression");
        writeHolidays(stream);
        writeOutliers(stream);
    }

    public void writeModel(HtmlStream stream) throws IOException {
        GeneralLinearModel.Estimation estimation = model.getEstimation();
        GeneralLinearModel.Description<ExtendedAirlineDescription> description = model.getDescription();
        LikelihoodStatistics ll = estimation.getStatistics();
        int nhp = estimation.getParameters().getValues().length();

        if (nhp == 0) {
            return;
        }
        stream.open(new HtmlTable().withWidth(400));
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableCell("").withWidth(100));
        stream.write(new HtmlTableCell("Coefficients").withWidth(100).withClass(Bootstrap4.FONT_WEIGHT_BOLD));
        stream.write(new HtmlTableCell("T-Stat").withWidth(100).withClass(Bootstrap4.FONT_WEIGHT_BOLD));
        stream.write(new HtmlTableCell("P[|T| &gt t]").withWidth(100).withClass(Bootstrap4.FONT_WEIGHT_BOLD));
        stream.close(HtmlTag.TABLEROW);
        int nobs = ll.getEffectiveObservationsCount(), nparams = ll.getEstimatedParametersCount();
        DoubleSeqCursor p = estimation.getParameters().getValues().cursor();
        DoubleSeqCursor vars = estimation.getParameters().getCovariance().diagonal().cursor();
        double ndf = nobs - nparams;
        T t = new T(ndf - nhp);
        double vcorr = (ndf - nhp) / ndf;
        List<String> headers = new ArrayList<>();
        String header;
        stream.open(HtmlTag.TABLEROW);
        if (description.getStochasticComponent().getSpec().hasAr()) {
            header = "Phi(1)";
        } else {
            header = "Theta(1)";
        }
        stream.write(new HtmlTableCell(header).withWidth(100));
        double val = p.getAndNext();
        stream.write(new HtmlTableCell(df4.format(val)).withWidth(100));
        double stde = Math.sqrt(vars.getAndNext() * vcorr);
        headers.add(header);
        double tval = val / stde;
        stream.write(new HtmlTableCell(formatT(tval)).withWidth(100));
        double prob = 1 - t.getProbabilityForInterval(-tval, tval);
        stream.write(new HtmlTableCell(df4.format(prob)).withWidth(100));
        stream.close(HtmlTag.TABLEROW);

        double[] f = description.getStochasticComponent().getSpec().getPeriodicities();
        for (int j = 1, k = 0; j < nhp; ++j) {
            stream.open(HtmlTag.TABLEROW);
            if (description.getStochasticComponent().getSpec().isAdjustToInt()) {
                header = "Theta(" + ((int) f[k++]) + ")";
            } else {
                header = "Theta(" + df2.format(f[k++]) + ")";
            }
            stream.write(new HtmlTableCell(header).withWidth(100));
            val = p.getAndNext();
            stream.write(new HtmlTableCell(df4.format(val)).withWidth(100));
            stde = Math.sqrt(vars.getAndNext() * vcorr);
            headers.add(header);
            tval = val / stde;
            stream.write(new HtmlTableCell(formatT(tval)).withWidth(100));
            prob = 1 - t.getProbabilityForInterval(-tval, tval);
            stream.write(new HtmlTableCell(df4.format(prob)).withWidth(100));

            stream.close(HtmlTag.TABLEROW);
        }
        stream.close(HtmlTag.TABLE);

        Matrix pcov = estimation.getParameters().getCovariance();
        if (!pcov.isEmpty()) {
            int size = pcov.getColumnsCount();
            stream.newLines(2);
            stream.write(HtmlTag.HEADER3, "Correlation of the estimates").newLine();
            stream.open(HtmlTag.TABLE);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("").withWidth(100));

            for (int i = 0; i < size; ++i) {
                stream.write(new HtmlTableCell(headers.get(i)).withWidth(100));
            }
            stream.close(HtmlTag.TABLEROW);

            for (int i = 0; i < size; ++i) {
                stream.open(HtmlTag.TABLEROW);
                stream.write(new HtmlTableCell(headers.get(i)).withWidth(100));
                for (int j = 0; j < size; ++j) {
                    double vi = pcov.get(i, i), vj = pcov.get(j, j);
                    if (vi != 0 && vj != 0) {
                        val = pcov.get(i, j) / Math.sqrt(vi * vj);
                        stream.write(new HtmlTableCell(df4.format(val)).withWidth(100));
                    } else {
                        stream.write(new HtmlTableCell("-").withWidth(100));
                    }
                }
                stream.close(HtmlTag.TABLEROW);
            }
            stream.close(HtmlTag.TABLE);
            stream.newLine();
        }
    }

    private void writeScore(HtmlStream stream) throws IOException {
        DoubleSeq score = model.getEstimation().getParameters().getScores();
        if (score.isEmpty()) {
            return;
        }
        stream.newLine();
        stream.write(HtmlTag.HEADER3, "Scores at the solution");
        stream.write(DoubleSeq.format(score, df6));
    }

    private void writeOutliers(HtmlStream stream) throws IOException {
        GeneralLinearModel.Description<ExtendedAirlineDescription> description = model.getDescription();
        Set<ITsVariable> outliers = Arrays.stream(description.getVariables())
                .filter(var -> var.getCore() instanceof IOutlier)
                .map(var -> var.getCore()).collect(Collectors.toSet());
        if (outliers.isEmpty()) {
            return;
        }

        String header = "Outliers";

        TsDomain edom = model.getEstimation().getDomain();
        stream.write(HtmlTag.HEADER3, header);
        writeRegressionItems(stream, outliers, edom);
    }

    private void writeHolidays(HtmlStream stream) throws IOException {
        String header = "Holidays";
        GeneralLinearModel.Description<ExtendedAirlineDescription> description = model.getDescription();
        Set<ITsVariable> hol = Arrays.stream(description.getVariables())
                .filter(var -> var.getCore() instanceof HolidaysVariable)
                .map(var -> var.getCore()).collect(Collectors.toSet());
        if (hol.isEmpty()) {
            return;
        }

        TsDomain edom = model.getEstimation().getDomain();
        stream.write(HtmlTag.HEADER3, header);
        writeRegressionItems(stream, hol, edom);
    }

    private <V extends ITsVariable> void writeRegressionItems(HtmlStream stream, Set<ITsVariable> vars, TsDomain context) throws IOException {
        List<RegressionDesc> regs = new ArrayList<>();
        for (RegressionDesc reg : model.getRegressionItems()) {
            if (vars.contains(reg.getCore())) {
                regs.add(reg);
            }
        }
        writeRegressionItems(stream, regs, context);
    }

    private <V extends ITsVariable> void writeRegressionItems(HtmlStream stream, List<RegressionDesc> regs, TsDomain context) throws IOException {

        stream.open(new HtmlTable().withWidth(400));
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableCell("").withWidth(100));
        stream.write(new HtmlTableCell("Coefficients").withWidth(100).withClass(Bootstrap4.FONT_WEIGHT_BOLD));
        stream.write(new HtmlTableCell("T-Stat").withWidth(100).withClass(Bootstrap4.FONT_WEIGHT_BOLD));
        stream.write(new HtmlTableCell("P[|T| &gt t]").withWidth(100).withClass(Bootstrap4.FONT_WEIGHT_BOLD));
        stream.close(HtmlTag.TABLEROW);
        for (RegressionDesc reg : regs) {
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell(reg.getCore().description(reg.getItem(), context)).withWidth(100));
            stream.write(new HtmlTableCell(df4.format(reg.getCoef())).withWidth(100));
            stream.write(new HtmlTableCell(formatT(reg.getTStat())).withWidth(100));
            stream.write(new HtmlTableCell(df4.format(reg.getPvalue())).withWidth(100));
            stream.close(HtmlTag.TABLEROW);
        }
        stream.close(HtmlTag.TABLE);
        stream.newLine();
    }

    private <T extends ITsVariable> int countVariables(Class<T> tclass, boolean fixed) {
        Variable[] variables = model.getDescription().getVariables();
        if (fixed) {
            return Arrays.stream(variables).filter(var -> tclass.isInstance(var.getCore())).mapToInt(var -> var.fixedCoefficientsCount()).sum();
        } else {
            return Arrays.stream(variables).filter(var -> tclass.isInstance(var.getCore())).mapToInt(var -> var.freeCoefficientsCount()).sum();
        }
    }

}
