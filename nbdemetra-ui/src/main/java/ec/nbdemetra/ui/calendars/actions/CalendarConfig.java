/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package ec.nbdemetra.ui.calendars.actions;

import com.google.common.base.Converter;
import ec.nbdemetra.ui.Config;
import ec.tss.tsproviders.utils.Formatters;
import ec.tss.tsproviders.utils.Parsers;
import ec.tss.xml.calendar.AbstractXmlCalendar;
import ec.tss.xml.calendar.XmlChainedCalendar;
import ec.tss.xml.calendar.XmlCompositeCalendar;
import ec.tss.xml.calendar.XmlNationalCalendar;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.timeseries.calendars.ChainedGregorianCalendarProvider;
import ec.tstoolkit.timeseries.calendars.CompositeGregorianCalendarProvider;
import ec.tstoolkit.timeseries.calendars.GregorianCalendarManager;
import ec.tstoolkit.timeseries.calendars.IGregorianCalendarProvider;
import ec.tstoolkit.timeseries.calendars.NationalCalendarProvider;

/**
 *
 * @author Philippe Charles
 */
final class CalendarConfig extends Converter<IGregorianCalendarProvider, Config> {

    public static final String DOMAIN = AbstractXmlCalendar.class.getName();

    private final Formatters.Formatter<XmlNationalCalendar> nationalFormatter = Formatters.onJAXB(XmlNationalCalendar.class, false);
    private final Formatters.Formatter<XmlChainedCalendar> chainedFormatter = Formatters.onJAXB(XmlChainedCalendar.class, false);
    private final Formatters.Formatter<XmlCompositeCalendar> compositeFormatter = Formatters.onJAXB(XmlCompositeCalendar.class, false);

    private final Parsers.Parser<XmlNationalCalendar> nationalParser = Parsers.onJAXB(XmlNationalCalendar.class);
    private final Parsers.Parser<XmlChainedCalendar> chainedParser = Parsers.onJAXB(XmlChainedCalendar.class);
    private final Parsers.Parser<XmlCompositeCalendar> compositeParser = Parsers.onJAXB(XmlCompositeCalendar.class);

    @Override
    protected Config doForward(IGregorianCalendarProvider cal) {
        GregorianCalendarManager manager = ProcessingContext.getActiveContext().getGregorianCalendars();
        Config.Builder result = Config.builder(DOMAIN, manager.get(cal), "");
        String code = manager.get(cal);
        result.put("type", cal.getClass().getName());
        String xml = format(cal, code, manager);
        if (xml == null) {
            throw new RuntimeException("Cannot format calendar");
        }
        result.put("xml", xml);
        return result.build();
    }

    private String format(IGregorianCalendarProvider cal, String code, GregorianCalendarManager manager) {
        if (cal instanceof NationalCalendarProvider) {
            return nationalFormatter.formatAsString(XmlNationalCalendar.create(code, manager));
        } else if (cal instanceof ChainedGregorianCalendarProvider) {
            return chainedFormatter.formatAsString(XmlChainedCalendar.create(code, manager));
        } else if (cal instanceof CompositeGregorianCalendarProvider) {
            return compositeFormatter.formatAsString(XmlCompositeCalendar.create(code, manager));
        }
        return null;
    }

    @Override
    protected IGregorianCalendarProvider doBackward(Config config) {
        GregorianCalendarManager manager = ProcessingContext.getActiveContext().getGregorianCalendars();
        AbstractXmlCalendar xmlCal = parse(config.get("type"), config.get("xml"));
        if (xmlCal != null) {
            if (xmlCal.addTo(manager)) {
                return manager.get(xmlCal.name);
            }
            throw new IllegalArgumentException("Cannot add calendar to manager");
        }
        throw new IllegalArgumentException("Cannot parse config");
    }

    private AbstractXmlCalendar parse(String type, String xml) {
        if (NationalCalendarProvider.class.getName().equals(type)) {
            return nationalParser.parse(xml);
        } else if (ChainedGregorianCalendarProvider.class.getName().equals(type)) {
            return chainedParser.parse(xml);
        } else if (CompositeGregorianCalendarProvider.class.getName().equals(type)) {
            return compositeParser.parse(xml);
        } else {
            return null;
        }
    }
}
