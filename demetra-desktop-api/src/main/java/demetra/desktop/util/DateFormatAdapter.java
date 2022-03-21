package demetra.desktop.util;

import demetra.tsprovider.util.ObsFormat;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import nbbrd.io.text.Formatter;

public final class DateFormatAdapter extends DateFormat {

    private final Formatter<Date> dateFormatter;

    public DateFormatAdapter(ObsFormat obsFormat) {
        dateFormatter = obsFormat.calendarFormatter();
    }

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        return toAppendTo.append(dateFormatter.format(date));
    }

    @Override
    public Date parse(String source, ParsePosition pos) {
        return null;
    }
}
