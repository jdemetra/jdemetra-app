/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui;

import demetra.desktop.Config;
import com.google.common.base.Strings;
import ec.tss.tsproviders.utils.DataFormat;
import java.util.Locale;
import java.util.Objects;
import nbbrd.io.text.Parser;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 *
 */
@lombok.AllArgsConstructor
public final class DataFormatParam implements Config.Converter<DataFormat> {

    @lombok.NonNull
    private final DataFormat defaultValue;
    @lombok.NonNull
    private final String localeKey;
    @lombok.NonNull
    private final String datePatternKey;
    @lombok.NonNull
    private final String numberPatternKey;

    private boolean isValid(String locale, String datePattern) {
        return locale != null && datePattern != null;
    }

    @Nullable
    private Locale parseLocale(@NonNull String locale) {
        // Fix behavior change in Parser#onLocale()
        Locale result = Parser.onLocale().parse(locale);
        return Locale.ROOT.equals(result) && locale.isEmpty() ? null : result;
    }

    @Override
    public DataFormat getDefaultValue() {
        return defaultValue;
    }

    @Override
    public DataFormat get(Config config) {
        String locale = config.getParameter(localeKey);
        String datePattern = config.getParameter(datePatternKey);
        String numberPattern = config.getParameter(numberPatternKey);
        return isValid(locale, datePattern) ? DataFormat.of(parseLocale(locale), datePattern, numberPattern) : defaultValue;
    }

    @Override
    public void set(Config.Builder builder, DataFormat value) {
        Objects.requireNonNull(builder);
        if (!defaultValue.equals(value)) {
            builder.parameter(localeKey, getLocaleValue(value.getLocale()));
            builder.parameter(datePatternKey, getDateTimePatternValue(value.getDatePattern()));
            builder.parameter(numberPatternKey, getNumberPatternValue(value.getNumberPattern()));
        }
    }

    private static String getLocaleValue(Locale locale) {
        return locale != null ? locale.toString() : "";
    }

    private static String getDateTimePatternValue(String dateTimePattern) {
        return Strings.nullToEmpty(dateTimePattern);
    }

    private static String getNumberPatternValue(String numberPattern) {
        return Strings.nullToEmpty(numberPattern);
    }
}
