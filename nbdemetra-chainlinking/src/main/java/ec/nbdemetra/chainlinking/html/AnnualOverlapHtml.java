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
package ec.nbdemetra.chainlinking.html;

import ec.tss.html.AbstractHtmlElement;
import ec.tss.html.Bootstrap4;
import static ec.tss.html.Bootstrap4.FONT_ITALIC;
import static ec.tss.html.Bootstrap4.FONT_WEIGHT_BOLD;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlTable;
import ec.tss.html.HtmlTableCell;
import ec.tss.html.HtmlTableHeader;
import ec.tss.html.HtmlTag;
import ec.tss.html.IHtmlElement;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDataBlock;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.tstoolkit.timeseries.simplets.YearIterator;
import ec.tstoolkit.timeseries.simplets.chainlinking.AnnualOverlap;
import java.io.IOException;
import static ec.tss.html.Bootstrap4.TEXT_CENTER;
import ec.tss.html.HtmlClass;

/**
 * Html document displaying results of an Annual Overlap Chain Linking
 *
 * @author Mats Maggi
 */
public class AnnualOverlapHtml extends AbstractHtmlElement implements IHtmlElement {

    private final InformationSet results;
    private final boolean detailed;

    public AnnualOverlapHtml(InformationSet results, boolean detailed) {
        this.results = results;
        this.detailed = detailed;
    }

    @Override
    public void write(HtmlStream stream) throws IOException {
        if (results == null) {
            return;
        }

        stream.write(HtmlTag.HEADER1, "Annual Overlap").newLine();

        Integer refYear = results.get(AnnualOverlap.REF_YEAR, Integer.class);
        stream.write(HtmlTag.HEADER2, "Reference Year : " + refYear).newLine();

        if (detailed) {
            writeDetailed(stream, refYear);
        } else {
            writeResults(stream, refYear);
        }
    }

    private void writeHeaders(HtmlStream stream, int nbYears, int refYear) throws IOException {
        if (detailed) {
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableHeader("").withColSpan(1).withRowSpan(3).withClass(TEXT_CENTER));
            stream.write(new HtmlTableHeader("Total at current prices").withColSpan(1).withRowSpan(3).withClass(TEXT_CENTER));
            stream.write(new HtmlTableHeader("At constant prices of :").withColSpan(nbYears * 2).withRowSpan(1).withClass(TEXT_CENTER));
            stream.write(new HtmlTableHeader("Chain-Linked Index").withColSpan(2).withRowSpan(2).withClass(TEXT_CENTER));
            stream.close(HtmlTag.TABLEROW);
            stream.open(HtmlTag.TABLEROW);
            for (int y = 0; y < nbYears; y++) {
                stream.write(new HtmlTableHeader(String.valueOf(refYear + y)).withColSpan(2).withRowSpan(1).withClass(TEXT_CENTER));
            }
            stream.close(HtmlTag.TABLEROW);
            stream.open(HtmlTag.TABLEROW);
            for (int y = 0; y < nbYears; y++) {
                stream.write(new HtmlTableHeader("Level").withColSpan(1).withRowSpan(1).withClass(TEXT_CENTER).withClass(FONT_ITALIC));
                stream.write(new HtmlTableHeader("Index " + (refYear + y)).withColSpan(1).withRowSpan(1).withClass(TEXT_CENTER).withClass(FONT_ITALIC));
            }
            stream.write(new HtmlTableHeader("Level").withColSpan(1).withRowSpan(1).withClass(TEXT_CENTER).withClass(FONT_ITALIC));
            stream.write(new HtmlTableHeader("Rate of Change").withColSpan(1).withRowSpan(1).withClass(TEXT_CENTER).withClass(FONT_ITALIC));
            stream.close(HtmlTag.TABLEROW);
        } else {
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableHeader("").withColSpan(1).withRowSpan(2).withClass(TEXT_CENTER));
            stream.write(new HtmlTableHeader("Total at current prices").withColSpan(1).withRowSpan(2).withClass(TEXT_CENTER));
            stream.write(new HtmlTableHeader("Chain-Linked Index").withColSpan(2).withRowSpan(1).withClass(TEXT_CENTER));
            stream.close(HtmlTag.TABLEROW);
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableHeader("Level").withColSpan(1).withRowSpan(1).withClass(TEXT_CENTER).withClass(FONT_ITALIC));
            stream.write(new HtmlTableHeader("Rate of Change").withColSpan(1).withRowSpan(1).withClass(TEXT_CENTER).withClass(FONT_ITALIC));
            stream.close(HtmlTag.TABLEROW);
        }
    }

    private void writeDetailed(HtmlStream stream, int refYear) throws IOException {
        TsData tsQ = results.get(AnnualOverlap.TOTAL_VALUES, TsData.class);
        if (tsQ != null) {
            TsData tsY = tsQ.changeFrequency(TsFrequency.Yearly, TsAggregationType.Sum, true);
            TsData prices = results.get(AnnualOverlap.VALUES_PREVIOUS_PRICE, TsData.class);
            TsData percPrice = results.get(AnnualOverlap.CHAIN_LINKED_INDEXES, TsData.class);
            TsData percPriceAvg = percPrice.changeFrequency(TsFrequency.Yearly, TsAggregationType.Average, true);

            int nbYears = tsY.getLength() - 1;

            stream.open(new HtmlTable().withWidth(200 + (120 * nbYears)));

            // Headers
            writeHeaders(stream, nbYears, refYear);

            YearIterator it = new YearIterator(tsQ);
            YearIterator itY = new YearIterator(tsY);
            int year = 0;
            while (it.hasMoreElements()) {
                TsDataBlock b = it.nextElement();
                TsDataBlock bY = itY.nextElement();

                for (int i = 0; i < b.data.getLength(); i++) {
                    stream.open(HtmlTag.TABLEROW);
                    stream.write(new HtmlTableCell(b.period(i).getPeriodString()).withWidth(20).withClass(TEXT_CENTER));
                    stream.write(new HtmlTableCell(df2.format(b.data.get(i))).withWidth(60).withClass(TEXT_CENTER));
                    if (b.period(i).getYear() == refYear) {
                        stream.write(new HtmlTableCell("").withWidth(60).withClass(TEXT_CENTER));
                        stream.write(new HtmlTableCell("").withWidth(60).withClass(TEXT_CENTER));
                        stream.write(new HtmlTableCell("").withWidth(60).withClass(TEXT_CENTER));
                    } else {
                        TsPeriod prev = TsPeriod.year(b.start.getYear() - 1);
                        TsPeriod curr = b.period(i);

                        for (int y = 1; y <= nbYears; y++) {
                            if (y == year) {
                                stream.write(new HtmlTableCell(df2.format(prices.get(curr))).withWidth(60).withClass(TEXT_CENTER));
                                stream.write(new HtmlTableCell(df2.format((prices.get(curr) / (tsY.get(prev) / 4.0)) * 100.0)).withWidth(60).withClass(TEXT_CENTER));
                            } else {
                                stream.write(new HtmlTableCell("").withWidth(60).withClass(TEXT_CENTER));
                                stream.write(new HtmlTableCell("").withWidth(60).withClass(TEXT_CENTER));
                            }
                        }

                        stream.write(new HtmlTableCell(df2.format(percPrice.get(curr))).withWidth(60).withClass(TEXT_CENTER));
                        double value = percPrice.get(curr.minus(1));
                        double rateOfChange = percPrice.get(curr) - (Double.isNaN(value) ? 100.0 : value);
                        stream.write(new HtmlTableCell(df2.format(rateOfChange) + "%").withWidth(60).withClass(TEXT_CENTER).withClass(getRateColor(rateOfChange)));
                    }
                    stream.close(HtmlTag.TABLEROW);
                }

                // Annual Values row
                stream.open(HtmlTag.TABLEROW);
                stream.write(new HtmlTableCell(String.valueOf(bY.start.getYear())).withWidth(20).withClass(TEXT_CENTER).withClass(FONT_WEIGHT_BOLD));
                stream.write(new HtmlTableCell(df2.format(bY.data.get(0))).withWidth(60).withClass(TEXT_CENTER).withClass(FONT_WEIGHT_BOLD));

                TsData pricesY = prices.changeFrequency(TsFrequency.Yearly, TsAggregationType.Sum, true);
                if (bY.start.getYear() == refYear) {
                    stream.write(new HtmlTableCell(df2.format(bY.data.get(0))).withWidth(60).withClass(TEXT_CENTER).withClass(FONT_WEIGHT_BOLD));
                    stream.write(new HtmlTableCell("100,00").withWidth(60).withClass(TEXT_CENTER).withClass(FONT_WEIGHT_BOLD));
                    for (int y = 0; y < nbYears - 1; y++) {
                        stream.write(new HtmlTableCell("").withWidth(60).withClass(TEXT_CENTER));
                        stream.write(new HtmlTableCell("").withWidth(60).withClass(TEXT_CENTER));
                    }
                    stream.write(new HtmlTableCell("100,00").withWidth(60).withClass(TEXT_CENTER));
                } else {
                    TsPeriod curYear = TsPeriod.year(bY.start.getYear());
                    for (int y = 0; y <= nbYears; y++) {
                        if (y + 1 == year) {
                            stream.write(new HtmlTableCell(df2.format(pricesY.get(curYear))).withWidth(60).withClass(TEXT_CENTER).withClass(FONT_WEIGHT_BOLD));
                            stream.write(new HtmlTableCell(df2.format((pricesY.get(curYear) / tsY.get(curYear.minus(1)) * 100.0))).withWidth(60).withClass(TEXT_CENTER).withClass(FONT_WEIGHT_BOLD));
                        } else if (y == year) {
                            if (tsY.getLastPeriod().getYear() != curYear.getYear()) {
                                stream.write(new HtmlTableCell(df2.format(tsY.get(curYear))).withWidth(60).withClass(TEXT_CENTER).withClass(FONT_WEIGHT_BOLD));
                                stream.write(new HtmlTableCell("100,00").withWidth(60).withClass(TEXT_CENTER));
                            } else {
                                stream.write(new HtmlTableCell(df2.format(percPriceAvg.get(curYear))).withWidth(60).withClass(TEXT_CENTER).withClass(FONT_WEIGHT_BOLD));

                                double value = percPrice.get(percPrice.getLastPeriod());
                                double rateOfChange = percPriceAvg.get(curYear) - value;

                                stream.write(new HtmlTableCell(df2.format(rateOfChange) + "%").withWidth(60).withClass(TEXT_CENTER).withClass(FONT_WEIGHT_BOLD).withClass(getRateColor(rateOfChange)));
                            }
                        } else if (y + 1 < year) {
                            stream.write(new HtmlTableCell("").withWidth(60).withClass(TEXT_CENTER));
                            stream.write(new HtmlTableCell("").withWidth(60).withClass(TEXT_CENTER));
                        } else if (y + 1 > year) {
                            if (y == nbYears) {
                                stream.write(new HtmlTableCell(df2.format(percPriceAvg.get(curYear))).withWidth(60).withClass(TEXT_CENTER).withClass(FONT_WEIGHT_BOLD));
                                stream.write(new HtmlTableCell("").withWidth(60).withClass(TEXT_CENTER));
                            } else {
                                stream.write(new HtmlTableCell("").withWidth(60).withClass(TEXT_CENTER));
                                stream.write(new HtmlTableCell("").withWidth(60).withClass(TEXT_CENTER));
                            }
                        }
                    }
                }

                year++;

                stream.close(HtmlTag.TABLEROW);
            }
        }
    }

    private void writeResults(HtmlStream stream, int refYear) throws IOException {
        TsData tsQ = results.get(AnnualOverlap.TOTAL_VALUES, TsData.class);
        if (tsQ != null) {
            TsData tsY = tsQ.changeFrequency(TsFrequency.Yearly, TsAggregationType.Sum, true);
            TsData percPrice = results.get(AnnualOverlap.CHAIN_LINKED_INDEXES, TsData.class);
            TsData percPriceAvg = percPrice.changeFrequency(TsFrequency.Yearly, TsAggregationType.Average, true);

            int nbYears = tsY.getLength() - 1;

            stream.open(new HtmlTable().withWidth(250));

            // Headers
            writeHeaders(stream, nbYears, refYear);

            YearIterator it = new YearIterator(tsQ);
            YearIterator itY = new YearIterator(tsY);
            int year = 1;
            while (it.hasMoreElements()) {
                TsDataBlock b = it.nextElement();
                TsDataBlock bY = itY.nextElement();
                for (int i = 0; i < b.data.getLength(); i++) {
                    stream.open(HtmlTag.TABLEROW);
                    stream.write(new HtmlTableCell(b.period(i).getPeriodString()).withWidth(20).withClass(TEXT_CENTER));
                    stream.write(new HtmlTableCell(df2.format(b.data.get(i))).withWidth(60).withClass(TEXT_CENTER));
                    if (b.period(i).getYear() != refYear) {
                        TsPeriod curr = b.period(i);
                        stream.write(new HtmlTableCell(df2.format(percPrice.get(curr))).withWidth(60).withClass(TEXT_CENTER));
                        double value = percPrice.get(curr.minus(1));
                        double rateOfChange = percPrice.get(curr) - (Double.isNaN(value) ? 100.0 : value);
                        stream.write(new HtmlTableCell(df2.format(rateOfChange) + "%").withWidth(60).withClass(TEXT_CENTER).withClass(getRateColor(rateOfChange)));
                    }
                    stream.close(HtmlTag.TABLEROW);
                }

                // Annual Values row
                stream.open(HtmlTag.TABLEROW);
                stream.write(new HtmlTableCell(String.valueOf(bY.start.getYear())).withWidth(20).withClass(TEXT_CENTER).withClass(FONT_WEIGHT_BOLD));
                stream.write(new HtmlTableCell(df2.format(bY.data.get(0))).withWidth(60).withClass(TEXT_CENTER).withClass(FONT_WEIGHT_BOLD));

                if (bY.start.getYear() == refYear) {
                    stream.write(new HtmlTableCell("100,00").withWidth(60).withClass(TEXT_CENTER));
                } else {
                    TsPeriod curYear = TsPeriod.year(bY.start.getYear());
                    stream.write(new HtmlTableCell(df2.format(percPriceAvg.get(curYear))).withWidth(60).withClass(TEXT_CENTER).withClass(FONT_WEIGHT_BOLD));

                    if (year > nbYears) {
                        double value = percPrice.get(percPrice.getLastPeriod());
                        double rateOfChange = percPriceAvg.get(curYear) - value;
                        stream.write(new HtmlTableCell(df2.format(rateOfChange) + "%").withWidth(60).withClass(TEXT_CENTER).withClass(FONT_WEIGHT_BOLD).withClass(getRateColor(rateOfChange)));
                    }
                }

                year++;

                stream.close(HtmlTag.TABLEROW);
            }
        }
    }

    private HtmlClass getRateColor(double value) {
        if (value < 0) {
            return Bootstrap4.TEXT_DANGER;
        } else {
            return Bootstrap4.TEXT_SUCCESS;
        }
    }
}
