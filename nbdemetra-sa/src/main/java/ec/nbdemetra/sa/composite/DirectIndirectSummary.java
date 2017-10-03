/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.composite;

import ec.tss.TsCollection;
import ec.tss.html.*;
import ec.tss.sa.composite.MultiSaSpecification;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.timeseries.simplets.TsData;
import java.io.IOException;

/**
 *
 * @author Jean Palate
 */
public class DirectIndirectSummary extends AbstractHtmlElement {

    private final TsCollection input, sa, bsa;
    private final MultiSaSpecification spec;

    public DirectIndirectSummary(TsCollection input, TsCollection sa, TsCollection bsa, MultiSaSpecification spec) {
        this.input = input;
        this.sa = sa;
        this.bsa = bsa;
        this.spec = spec;
    }

    @Override
    public void write(HtmlStream stream) throws IOException {
        writeConstraintsCheck(stream);
        writeBenchDifferences(stream);
    }

    private void writeConstraintsCheck(HtmlStream stream) {
    }

    private void writeBenchDifferences(HtmlStream stream) throws IOException {
        int n = input.getCount();
        if (bsa.getCount() != n || sa.getCount() != n) {
            return;
        }
        stream.write(HtmlTag.HEADER2, "Relatives differences between benchmarked and original sa series");
        stream.open(new HtmlTable().withWidth(600));
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableCell("Series").withWidth(200));
        stream.write(new HtmlTableCell("Average").withWidth(100));
        stream.write(new HtmlTableCell("Stdev").withWidth(100));
        stream.write(new HtmlTableCell("Min").withWidth(100));
        stream.write(new HtmlTableCell("Max").withWidth(100));
        stream.close(HtmlTag.TABLEROW);
        stream.open(HtmlTag.TABLEROW);
        for (int i = 0; i < n; ++i) {
            String name = input.get(i).getName();
            TsData del=TsData.subtract(sa.get(i).getTsData(), bsa.get(i).getTsData());
            DescriptiveStatistics tmp=new DescriptiveStatistics(sa.get(i).getTsData());
            del.getValues().div(tmp.getRmse());
            DescriptiveStatistics stats=new DescriptiveStatistics(del);
            stream.write(new HtmlTableCell(name).withWidth(200));
            stream.write(new HtmlTableCell(df4.format(stats.getAverage())).withWidth(100));
            stream.write(new HtmlTableCell(df4.format(stats.getStdev())).withWidth(100));
            stream.write(new HtmlTableCell(df4.format(stats.getMin())).withWidth(100));
            stream.write(new HtmlTableCell(df4.format(stats.getMax())).withWidth(100));
            stream.close(HtmlTag.TABLEROW);
        }
        stream.close(HtmlTag.TABLE);
    }
}
