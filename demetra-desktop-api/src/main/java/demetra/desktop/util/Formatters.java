/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.util;

import demetra.tsprovider.util.ObsFormat;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 *
 * @author PALATEJ
 */
@lombok.experimental.UtilityClass
public class Formatters {

    public DateFormat dateFormatOf(ObsFormat fmt) {
        String datePattern = fmt.getDateTimePattern();
        Locale locale = fmt.getLocale();
        DateFormat result = datePattern == null
                ? new SimpleDateFormat(datePattern, locale == null ? Locale.getDefault() : locale)
                : SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, locale == null ? Locale.getDefault() : locale);
        result.setLenient(datePattern == null && locale == null);
        return result;
    }

    public NumberFormat numberFormatOf(ObsFormat fmt) {
        String numberPattern = fmt.getNumberPattern();
        Locale locale = fmt.getLocale();
        if (locale == null) {
            locale = Locale.getDefault();
        }
        NumberFormat result = numberPattern != null
                ? new DecimalFormat(numberPattern, DecimalFormatSymbols.getInstance())
                : NumberFormat.getInstance(locale);
        return result;
    }

}
