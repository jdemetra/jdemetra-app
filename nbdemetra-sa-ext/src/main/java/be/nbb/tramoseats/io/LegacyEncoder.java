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
package be.nbb.tramoseats.io;

import be.nbb.tramoseats.io.IDecoder.Document;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.data.DataBlock;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.tstoolkit.modelling.ComponentType;
import ec.tstoolkit.modelling.TsVariableDescriptor;
import ec.tstoolkit.modelling.TsVariableDescriptor.UserComponentType;
import ec.tstoolkit.modelling.arima.tramo.CalendarSpec;
import ec.tstoolkit.modelling.arima.tramo.OutlierSpec;
import ec.tstoolkit.modelling.arima.tramo.TradingDaysSpec;
import ec.tstoolkit.modelling.arima.tramo.TransformSpec;
import ec.tstoolkit.timeseries.PeriodSelectorType;
import ec.tstoolkit.timeseries.calendars.IGregorianCalendarProvider;
import ec.tstoolkit.timeseries.calendars.LengthOfPeriodType;
import ec.tstoolkit.timeseries.regression.ITsVariable;
import ec.tstoolkit.timeseries.regression.InterventionVariable;
import ec.tstoolkit.timeseries.regression.LeapYearVariable;
import ec.tstoolkit.timeseries.regression.OutlierDefinition;
import ec.tstoolkit.timeseries.regression.Ramp;
import ec.tstoolkit.timeseries.regression.Sequence;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author Jean Palate
 */
public class LegacyEncoder extends AbstractEncoder {

    public static final double MISSING = -99999;
    public static final String INPUT = "$INPUT", REG = "$REG", INPUT_ = "INPUT", REG_ = "REG";
    private static final char sep = ' ';
    protected boolean m_closed;
    private boolean batch;
    private final ProcessingContext context;

    private TsDomain domain;

    public LegacyEncoder(ProcessingContext context) {
        this.domain = null;
        if (context == null) {
            this.context = ProcessingContext.getActiveContext();
        } else {
            this.context = context;
        }
    }

    public LegacyEncoder(final TsDomain domain, final ProcessingContext context) {
        this.domain = domain;
        if (context == null) {
            this.context = ProcessingContext.getActiveContext();
        } else {
            this.context = context;
        }
    }

    public String encode(String name, TsData s, TramoSeatsSpecification spec) {
        domain = s.getDomain();
        batch = false;
        return encode(name, s) + encode(spec);
    }

    public String encode(String name, TsData s) {
        StringBuilder builder = new StringBuilder();
        builder.append(name == null ? "Series" : name).append(NL);
        builder.append(s.getLength()).append(sep).append(s.getStart().getYear())
                .append(sep).append(s.getStart().getPosition() + 1).append(sep)
                .append(s.getFrequency().intValue()).append(NL);
        for (int i = 0; i < s.getLength(); ++i) {
            if (s.getValues().isMissing(i)) {
                builder.append(MISSING);
            } else {
                builder.append(s.get(i));
            }
            builder.append(NL);
        }
        return builder.toString();
    }

    @Override
    protected void openDocument() {
        m_closed = false;
        super.openDocument();
    }

    @Override
    protected void openSpecSection() {
        m_closed = false;
        m_builder.append(INPUT);
        if (batch) {
            write(Item.ITER, 3);
            batch = false;
        }
    }

    protected void openFreeSection() {
        m_closed = false;
    }

    @Override
    protected void openRegSection() {
        if (!m_closed) {
            m_builder.append("$END");
        }
        m_closed = false;
        m_builder.append(REG);
    }

    @Override
    protected void closeSection() {
        if (!m_closed) {
            m_builder.append("$END");
        }
        m_builder.append(NL);
        m_closed = true;
    }

    @Override
    protected void addSeparator() {
        m_builder.append(sep);
    }

    @Override
    protected void writeCalendarRegs(TradingDaysSpec spec) {
        if (spec.getHolidays() != null) {
            IGregorianCalendarProvider cal = context.getGregorianCalendars().get(spec.getHolidays());
            if (cal != null) {
                int ntd = spec.getTradingDaysType().getVariablesCount();
                if (spec.isLeapYear()) {
                    ++ntd;
                }
                TsDomain xdom = domain.extend(0, 3 * domain.getFrequency().intValue());
                Matrix M = new Matrix(xdom.getLength(), ntd);
                List<DataBlock> cols = M.columnList();
                cal.calendarData(spec.getTradingDaysType(), xdom, cols, 0);
                if (spec.isLeapYear()) {
                    new LeapYearVariable(LengthOfPeriodType.LeapYear).data(domain.getStart(), cols.get(ntd - 1));
                }
                for (int i = 0; i < M.getColumnsCount(); ++i) {
                    openRegSection();
                    write(Item.IUSER, 1);
                    write(Item.ILONG, xdom.getLength());
                    write(Item.NSER, 1);
                    write(Item.REGEFF, 2);
                    closeSection();
                    openFreeSection();
                    write(M.column(i));
                    closeSection();
                }
            }
        } else if (spec.getUserVariables() != null) {
            String[] vars = spec.getUserVariables();
            TsDomain xdom = domain.extend(0, 3 * domain.getFrequency().intValue());

            for (int i = 0; i < vars.length; ++i) {
                ITsVariable var = context.getTsVariable(vars[i]);
                if (var.getDim() == 1) {
                    DataBlock data = new DataBlock(xdom.getLength());
                    var.data(xdom, Collections.singletonList(data));
                    openRegSection();
                    write(Item.IUSER, 1);
                    write(Item.ILONG, xdom.getLength());
                    write(Item.NSER, 1);
                    write(Item.REGEFF, 2);
                    closeSection();
                    openFreeSection();
                    write(data);
                    closeSection();
                }
            }
        }
    }

    @Override
    protected void writeInterventionRegs(InterventionVariable[] spec) {
        if (spec == null) {
            return;
        }
        for (int i = 0; i < spec.length; ++i) {
            openRegSection();
            int nseq = spec[i].getCount();
            write(Item.ISEQ, nseq);
            double d = spec[i].getDelta();
            if (d != 0) {
                write(Item.DELTA, d);
            }
            double ds = spec[i].getDeltaS();
            if (ds != 0) {
                write(Item.DELTAS, ds);
            }
            if (spec[i].getD1DS()) {
                write(Item.ID1DS, 1);
            }
            closeSection();
            openFreeSection();
            for (int j = 0; j < nseq; ++j) {
                Sequence seq = spec[i].getSequence(j);
                int start = domain.search(seq.start), end = domain.search(seq.end);
                write(start + 1);
                write(end - start + 1);
            }
            closeSection();
        }
    }

    @Override
    protected void writeUserRegs(TsVariableDescriptor[] spec) {
        if (spec == null) {
            return;
        }
        TsDomain xdom = domain.extend(0, 3 * domain.getFrequency().intValue());
        for (int i = 0; i < spec.length; ++i) {
            ITsVariable var = spec[i].toTsVariable(context);
            Matrix M = new Matrix(xdom.getLength(), var.getDim());
            List<DataBlock> cols = M.columnList();
            var.data(xdom, cols);
            for (int j = 0; j < M.getColumnsCount(); ++j) {
                openRegSection();
                write(Item.IUSER, 1);
                write(Item.NSER, 1);
                write(Item.ILONG, xdom.getLength());
                write(Item.REGEFF, convert(spec[i].getEffect()));
                closeSection();
                openFreeSection();
                write(M.column(i));
                closeSection();
            }
        }
    }

    static int convert(UserComponentType cmp) {
        switch (cmp) {
            case Trend:
                return 1;
            case Seasonal:
                return 2;
            case Irregular:
                return 3;
            default:
                return 0;
        }
    }

    @Override
    protected void writeOutlierRegs(OutlierDefinition[] spec) {
        if (spec == null || spec.length == 0 || domain == null) {
            return;
        }
        openRegSection();
        write(Item.IUSER, 2);
        write(Item.NSER, spec.length);
        closeSection();
        openFreeSection();
        for (int i = 0; i < spec.length; ++i) {
            TsPeriod p = new TsPeriod(domain.getFrequency());
            p.set(spec[i].position);
            m_builder.append(sep).append(p.minus(domain.getStart()) + 1).
                    append(sep).append(spec[i].type.name());
        }
        closeSection();
    }

    @Override
    protected void writeRampRegs(Ramp[] spec) {
        //TODO
    }

    @Override
    protected void writeEstimateSpan(TransformSpec spec) {
        // TODO
    }

    @Override
    protected void writeOutliersSpan(OutlierSpec spec) {
        if (domain != null && spec.getSpan().getType() != PeriodSelectorType.All) {
            TsDomain ndom = domain.select(spec.getSpan());
            if (ndom.getLength() == 0) {
                write(Item.INT1, domain.getLength() + 1);
            } else {
                write(Item.INT1, 1 + (ndom.getStart().minus(domain.getStart())));
                write(Item.INT1, ndom.getEnd().minus(domain.getStart()));
            }
        }
    }

    public void encodeMultiDocument(FileWriter writer, List<Document> docs) {
        batch = true;
        for (Document doc : docs){
            domain=doc.series.getDomain();
            String ts=encode(doc.name, doc.series);
            String spec=encode(doc.spec);
            try {
                writer.append(ts);
                writer.append(spec);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

}
