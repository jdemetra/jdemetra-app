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
package demetra.desktop.ui.calendar.actions;

import demetra.desktop.Config;
import demetra.desktop.Converter;
import demetra.desktop.util.Formatters;
import demetra.desktop.util.Parser;
import demetra.desktop.util.Parsers;
import demetra.timeseries.calendars.Calendar;
import demetra.timeseries.calendars.CalendarDefinition;
import demetra.timeseries.calendars.CalendarManager;
import demetra.timeseries.calendars.ChainedCalendar;
import demetra.timeseries.calendars.CompositeCalendar;
import demetra.timeseries.regression.ModellingContext;
import internal.workspace.file.xml.util.AbstractXmlCalendar;
import internal.workspace.file.xml.util.XmlChainedCalendar;
import internal.workspace.file.xml.util.XmlCompositeCalendar;
import internal.workspace.file.xml.util.XmlNationalCalendar;
import nbbrd.io.xml.bind.Jaxb;
import demetra.desktop.util.Formatter;

/**
 *
 * @author Philippe Charles
 */
final class CalendarConfig implements Converter<CalendarDefinition, Config> {

    public static final String DOMAIN = AbstractXmlCalendar.class.getName();

    private final Formatter<XmlNationalCalendar> nationalFormatter = Formatters.onJAXB(XmlNationalCalendar.class, false);
    private final Formatter<XmlChainedCalendar> chainedFormatter = Formatters.onJAXB(XmlChainedCalendar.class, false);
    private final Formatter<XmlCompositeCalendar> compositeFormatter = Formatters.onJAXB(XmlCompositeCalendar.class, false);

    private final Parser<XmlNationalCalendar> nationalParser = Parsers.onJAXB(XmlNationalCalendar.class);
    private final Parser<XmlChainedCalendar> chainedParser = Parsers.onJAXB(XmlChainedCalendar.class);
    private final Parser<XmlCompositeCalendar> compositeParser = Parsers.onJAXB(XmlCompositeCalendar.class);

    @Override
    public Config doForward(CalendarDefinition cal) {
        CalendarManager manager = ModellingContext.getActiveContext().getCalendars();
        Config.Builder result = Config.builder(DOMAIN, manager.get(cal), "");
        String code = manager.get(cal);
        result.parameter("type", cal.getClass().getName());
        String xml = format(cal, code, manager);
        if (xml == null) {
            throw new RuntimeException("Cannot format calendar");
        }
        result.parameter("xml", xml);
        return result.build();
    }

    private String format(CalendarDefinition cal, String code, CalendarManager manager) {
        if (cal instanceof Calendar) {
            return nationalFormatter.formatAsString(XmlNationalCalendar.create(code, manager));
        } else if (cal instanceof ChainedCalendar) {
            return chainedFormatter.formatAsString(XmlChainedCalendar.create(code, manager));
        } else if (cal instanceof CompositeCalendar) {
            return compositeFormatter.formatAsString(XmlCompositeCalendar.create(code, manager));
        }
        return null;
    }

    @Override
    public CalendarDefinition doBackward(Config config) {
        CalendarManager manager = ModellingContext.getActiveContext().getCalendars();
        AbstractXmlCalendar xmlCal = parse(config.getParameter("type"), config.getParameter("xml"));
        if (xmlCal != null) {
            if (xmlCal.addTo(manager)) {
                return manager.get(xmlCal.name);
            }
            throw new IllegalArgumentException("Cannot add calendar to manager");
        }
        throw new IllegalArgumentException("Cannot parse config");
    }

    private AbstractXmlCalendar parse(String type, String xml) {
        if (Calendar.class.getName().equals(type)) {
            return nationalParser.parse(xml);
        } else if (ChainedCalendar.class.getName().equals(type)) {
            return chainedParser.parse(xml);
        } else if (CompositeCalendar.class.getName().equals(type)) {
            return compositeParser.parse(xml);
        } else {
            return null;
        }
    }
}
