/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq.ui;

import demetra.arima.SarimaSpec;
import demetra.data.DoubleSeq;
import demetra.data.DoubleSeqCursor;
import demetra.data.Parameter;
import demetra.desktop.highfreq.FractionalAirlineDecompositionDocument;
import demetra.desktop.highfreq.FractionalAirlineDocument;
import demetra.highfreq.FractionalAirlineSpec;
import demetra.html.AbstractHtmlElement;
import demetra.html.Bootstrap4;
import demetra.html.HtmlStream;
import demetra.html.HtmlTable;
import demetra.html.HtmlTableCell;
import demetra.html.HtmlTag;
import demetra.html.modelling.HtmlLikelihood;
import demetra.math.matrices.Matrix;
import demetra.modelling.OutlierDescriptor;
import demetra.timeseries.Ts;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDomain;
import demetra.timeseries.regression.IOutlier;
import demetra.timeseries.regression.ITsVariable;
import demetra.timeseries.regression.MissingValueEstimation;
import demetra.timeseries.regression.ModellingUtility;
import demetra.timeseries.regression.Variable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jdplus.dstats.T;
import jdplus.highfreq.FractionalAirlineEstimation;
import jdplus.modelling.GeneralLinearModel;
import jdplus.regsarima.regular.RegSarimaModel;
import jdplus.stats.likelihood.LikelihoodStatistics;

/**
 *
 * @author PALATEJ
 */
public class HtmlFractionalAirlineModel extends AbstractHtmlElement {

    private final Ts series;
    private final FractionalAirlineEstimation estimation;
    private final FractionalAirlineSpec spec;
    private final boolean summary;

    public HtmlFractionalAirlineModel(final FractionalAirlineDocument document, boolean summary) {
        this.series = document.getInput();
        this.estimation = document.getResult();
        this.spec = document.getSpecification();
        this.summary = summary;
    }

    public HtmlFractionalAirlineModel(final FractionalAirlineDecompositionDocument document, boolean summary) {
        this.series = document.getInput();
        this.estimation = document.getResult();
        this.spec = document.getSpecification();
        this.summary = summary;
    }

    public HtmlFractionalAirlineModel(final Ts series, final FractionalAirlineEstimation estimation, FractionalAirlineSpec spec, boolean summary) {
        this.series = series;
        this.estimation = estimation;
        this.spec = spec;
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
        TsDomain edom = series.getData().getDomain();
        stream.write(HtmlTag.HEADER1, "Summary").newLine();
        stream.write("Estimation span: [").write(edom.getStartPeriod().display());
        stream.write(" - ").write(edom.getLastPeriod().display()).write(']').newLine();
        if (spec.isLog()) {
            stream.write("Series has been log-transformed").newLine();
        }

        int no = estimation.getOutliers().length;
        if (no > 1) {
            stream.write(Integer.toString(no)).write(" detected outliers").newLine();
        } else if (no == 1) {
            stream.write(Integer.toString(no)).write(" detected outlier").newLine();
        }
    }

    private void writeDetails(HtmlStream stream) throws IOException {
        stream.write(HtmlTag.HEADER1, "Final model");
        stream.newLine();
        stream.write(HtmlTag.HEADER2, "Likelihood statistics");
        stream.write(new HtmlLikelihood(estimation.getLikelihood()));
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
        LikelihoodStatistics ll = estimation.getLikelihood();
        int nhp = estimation.getParameters().length();

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
        DoubleSeqCursor p = estimation.getParameters().cursor();
        DoubleSeqCursor vars = estimation.getParametersCovariance().diagonal().cursor();
        double ndf = nobs - nparams;
        T t = new T(ndf - nhp);
        double vcorr = (ndf - nhp) / ndf;
        List<String> headers = new ArrayList<>();
        String header;
        stream.open(HtmlTag.TABLEROW);
        if (spec.isAr()) {
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

        double[] f = estimation.getModel().getPeriodicities();
        for (int j = 1, k = 0; j < nhp; ++j) {
            stream.open(HtmlTag.TABLEROW);
            if (spec.isAdjustToInt()) {
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

        Matrix pcov = estimation.getParametersCovariance();
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
        DoubleSeq score = estimation.getScore();
        if (score.isEmpty()) {
            return;
        }
        stream.newLine();
        stream.write(HtmlTag.HEADER3, "Scores at the solution");
        stream.write(DoubleSeq.format(score, df6));
    }

    private void writeOutliers(HtmlStream stream) throws IOException {
        OutlierDescriptor[] outliers = estimation.getOutliers();
        if (outliers.length == 0) {
            return;
        }

        String header = "Outliers";

        DoubleSeq c = estimation.getCoefficients();
        DoubleSeq var = estimation.getCoefficientsCovariance().diagonal();

        TsDomain domain = series.getData().getDomain();
        stream.write(HtmlTag.HEADER3, header);

        int start = spec.isMeanCorrection() ? 1 : 0;
        if (spec.getX() != null) {
            start += spec.getX().getColumnsCount();
        }

        LikelihoodStatistics ll = estimation.getLikelihood();
        int nhp = estimation.getParameters().length();
        int nobs = ll.getEffectiveObservationsCount(), nparams = ll.getEstimatedParametersCount();
        T t = new T(nobs - nparams - nhp);

        stream.open(new HtmlTable().withWidth(400));
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableCell("").withWidth(100));
        stream.write(new HtmlTableCell("Coefficients").withWidth(100).withClass(Bootstrap4.FONT_WEIGHT_BOLD));
        stream.write(new HtmlTableCell("T-Stat").withWidth(100).withClass(Bootstrap4.FONT_WEIGHT_BOLD));
        stream.write(new HtmlTableCell("P[|T| &gt t]").withWidth(100).withClass(Bootstrap4.FONT_WEIGHT_BOLD));
        stream.close(HtmlTag.TABLEROW);
        for (OutlierDescriptor o : outliers) {
            double b = c.get(start), e = Math.sqrt(var.get(start)), tval = b / e;
            ++start;
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell(o.getCode() + " - " + domain.get(o.getPosition()).display()).withWidth(100));
            stream.write(new HtmlTableCell(df4.format(b)).withWidth(100));
            stream.write(new HtmlTableCell(formatT(tval)).withWidth(100));
            stream.write(new HtmlTableCell(df4.format(1 - t.getProbabilityForInterval(-tval, tval))).withWidth(100));
            stream.close(HtmlTag.TABLEROW);
        }
        stream.close(HtmlTag.TABLE);
        stream.newLine();
    }

    private void writeHolidays(HtmlStream stream) throws IOException {
        
        if (spec.getX() == null)
            return;

        String header = "Variables";

        DoubleSeq c = estimation.getCoefficients();
        DoubleSeq var = estimation.getCoefficientsCovariance().diagonal();

        stream.write(HtmlTag.HEADER3, header);

        int start = spec.isMeanCorrection() ? 1 : 0;
        String[] vars = spec.getXnames();
        int nx=vars.length;
        LikelihoodStatistics ll = estimation.getLikelihood();
        int nhp = estimation.getParameters().length();
        int nobs = ll.getEffectiveObservationsCount(), nparams = ll.getEstimatedParametersCount();
        T t = new T(nobs - nparams - nhp);

        stream.open(new HtmlTable().withWidth(400));
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableCell("").withWidth(100));
        stream.write(new HtmlTableCell("Coefficients").withWidth(100).withClass(Bootstrap4.FONT_WEIGHT_BOLD));
        stream.write(new HtmlTableCell("T-Stat").withWidth(100).withClass(Bootstrap4.FONT_WEIGHT_BOLD));
        stream.write(new HtmlTableCell("P[|T| &gt t]").withWidth(100).withClass(Bootstrap4.FONT_WEIGHT_BOLD));
        stream.close(HtmlTag.TABLEROW);
        for (int i=0; i< nx; ++i) {
            double b = c.get(start+i), e = Math.sqrt(var.get(start+i)), tval = b / e;
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell(vars[i]).withWidth(100));
            stream.write(new HtmlTableCell(df4.format(b)).withWidth(100));
            stream.write(new HtmlTableCell(formatT(tval)).withWidth(100));
            stream.write(new HtmlTableCell(df4.format(1 - t.getProbabilityForInterval(-tval, tval))).withWidth(100));
            stream.close(HtmlTag.TABLEROW);
        }
        stream.close(HtmlTag.TABLE);
        stream.newLine();
    }
}
