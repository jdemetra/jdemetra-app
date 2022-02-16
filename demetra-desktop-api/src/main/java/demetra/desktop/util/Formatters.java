/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.util;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import demetra.information.InformationSet;
import demetra.toolkit.io.xml.information.XmlInformationSet;
import demetra.tsprovider.util.ObsFormat;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 *
 * @author PALATEJ
 */
@lombok.experimental.UtilityClass
public class Formatters {

    public DateFormat dateFormatOf(ObsFormat fmt) {
        String datePattern = fmt.getDateTimePattern();
        Locale locale = fmt.getLocale();
        DateFormat result = datePattern != null
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

    @Nullable
    public <T> CharSequence formatFirstNotNull(@NonNull T value, @NonNull Iterable<? extends Formatter<T>> formatters) {
        Objects.requireNonNull(value); // if formatters is empty
        for (Formatter<T> o : formatters) {
            CharSequence result = o.format(value);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @NonNull
    public <T> Formatter<T> firstNotNull(@NonNull Formatter<T>... formatters) {
        return firstNotNull(ImmutableList.copyOf(formatters));
    }

    @NonNull
    public <T> Formatter<T> firstNotNull(@NonNull ImmutableList<? extends Formatter<T>> formatters) {
        return new Wrapper<>(o -> formatFirstNotNull(o, formatters));
    }

    /**
     * Creates a new formatter using {@link JAXBContext#newInstance(java.lang.Class[])
     * }.
     * <p>
     * Note that "<i>{@link JAXBContext} is thread-safe and should only be
     * created once and reused to avoid the cost of initializing the metadata
     * multiple times. {@link Marshaller} and {@link Unmarshaller} are not
     * thread-safe, but are lightweight to create and could be created per
     * operation (<a
     * href="http://stackoverflow.com/a/7400735">http://stackoverflow.com/a/7400735</a>)".</i>
     *
     * @param <T>
     * @param classToBeFormatted
     * @param formattedOutput
     * @return
     */
    @NonNull
    public <T> Formatter<T> onJAXB(@NonNull Class<T> classToBeFormatted, boolean formattedOutput) {
        try {
            return onJAXB(JAXBContext.newInstance(classToBeFormatted), formattedOutput);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
    }

    @NonNull
    public <T> Formatter<T> onJAXB(@NonNull JAXBContext context, boolean formattedOutput) {
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formattedOutput);
            return onJAXB(marshaller);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
    }

    @NonNull
    public <T> Formatter<T> onJAXB(@NonNull Marshaller marshaller) {
        return new FailSafeFormatter<T>() {
            @Override
            protected CharSequence doFormat(T value) throws Exception {
                StringWriter result = new StringWriter();
                marshaller.marshal(value, result);
                return result.toString();
            }
        };
    }

    @NonNull
    public Formatter<Date> onDateFormat(@NonNull DateFormat dateFormat) {
        return new Adapter<>(nbbrd.io.text.Formatter.onDateFormat(dateFormat));
    }

    @NonNull
    public Formatter<Number> onNumberFormat(@NonNull NumberFormat numberFormat) {
        return new Adapter<>(nbbrd.io.text.Formatter.onNumberFormat(numberFormat));
    }

    @NonNull
    public <T> Formatter<T> onNull() {
        return new Adapter<>(nbbrd.io.text.Formatter.onNull());
    }

    @NonNull
    @SuppressWarnings("null")
    public <T> Formatter<T> ofInstance(@Nullable CharSequence instance) {
        return new Adapter<>(nbbrd.io.text.Formatter.onConstant(instance));
    }

    @NonNull
    public Formatter<File> fileFormatter() {
        return FILE_FORMATTER;
    }

    @NonNull
    public Formatter<Integer> intFormatter() {
        return (Formatter<Integer>) OBJECT_TO_STRING_FORMATTER;
    }

    @NonNull
    public Formatter<Long> longFormatter() {
        return (Formatter<Long>) OBJECT_TO_STRING_FORMATTER;
    }

    @NonNull
    public Formatter<Double> doubleFormatter() {
        return (Formatter<Double>) OBJECT_TO_STRING_FORMATTER;
    }

    @NonNull
    public Formatter<Boolean> boolFormatter() {
        return (Formatter<Boolean>) OBJECT_TO_STRING_FORMATTER;
    }

    @NonNull
    public Formatter<Character> charFormatter() {
        return (Formatter<Character>) OBJECT_TO_STRING_FORMATTER;
    }

    @NonNull
    public Formatter<Charset> charsetFormatter() {
        return CHARSET_FORMATTER;
    }

    @NonNull
    public <T extends Enum<T>> Formatter<T> enumFormatter() {
        return new Adapter<T>(nbbrd.io.text.Formatter.onEnum());
    }

    @NonNull
    public Formatter<String> stringFormatter() {
        return (Formatter<String>) OBJECT_TO_STRING_FORMATTER;
    }

    @NonNull
    public Formatter<double[]> doubleArrayFormatter() {
        return DOUBLE_ARRAY_FORMATTER;
    }

    @NonNull
    public Formatter<String[]> stringArrayFormatter() {
        return STRING_ARRAY_FORMATTER;
    }

    @NonNull
    @SuppressWarnings("null")
    public <X, Y> Formatter<Y> compose(@NonNull Formatter<X> formatter, @NonNull Function<? super Y, ? extends X> before) {
        return new Wrapper<>(o -> {
            X tmp = before.apply(o);
            return tmp != null ? formatter.format(tmp) : null;
        });
    }

    @NonNull
    public Formatter<Object> usingToString() {
        return (Formatter<Object>) OBJECT_TO_STRING_FORMATTER;
    }

    @NonNull
    public Formatter<List<String>> onJoiner(@NonNull Joiner joiner) {
        Objects.requireNonNull(joiner);
        return new Adapter<>(nbbrd.io.text.Formatter.onStringList(stream -> joiner.join(stream.iterator())));
    }

    /**
     *
     * @param <T>
     * @param formatter
     * @return
     * @since 2.2.0
     */
    @NonNull
    public <T> Formatter<T> wrap(@NonNull Formatter<T> formatter) {
        return formatter instanceof Formatter ? (Formatter<T>) formatter : new Wrapper(Objects.requireNonNull(formatter));
    }

    /**
     * An abstract formatter that swallows any exception thrown and returns
     * <code>null</code> instead.
     *
     * @param <T>
     */
    public static abstract class FailSafeFormatter<T> implements Formatter<T> {

        @Override
        public CharSequence format(T value) throws NullPointerException {
            Objects.requireNonNull(value);
            try {
                return doFormat(value);
            } catch (Exception ex) {
                return null;
            }
        }

        @Nullable
        abstract protected CharSequence doFormat(@NonNull T value) throws Exception;
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    @lombok.RequiredArgsConstructor
    private static final class Adapter<T> implements Formatter<T> {

        private final nbbrd.io.text.Formatter<T> formatter;

        @Override
        public CharSequence format(T value) {
            Objects.requireNonNull(value);
            return formatter.format(value);
        }

        @Override
        public String formatAsString(T value) {
            Objects.requireNonNull(value);
            return formatter.formatAsString(value);
        }

        @Override
        public java.util.Optional<CharSequence> formatValue(T value) {
            Objects.requireNonNull(value);
            return formatter.formatValue(value);
        }

        @Override
        public java.util.Optional<String> formatValueAsString(T value) {
            Objects.requireNonNull(value);
            return formatter.formatValueAsString(value);
        }
    }

    private static final class Wrapper<T> implements Formatter<T> {

        private final Formatter<T> formatter;

        private Wrapper(Formatter<T> formatter) {
            this.formatter = formatter;
        }

        @Override
        public CharSequence format(T value) {
            return formatter.format(value);
        }

        @Override
        public String formatAsString(T value) {
            return formatter.formatAsString(value);
        }

        @Override
        public java.util.Optional<CharSequence> formatValue(T value) {
            return formatter.formatValue(value);
        }

        @Override
        public java.util.Optional<String> formatValueAsString(T value) {
            return formatter.formatValueAsString(value);
        }
    }

    private final Formatter<File> FILE_FORMATTER = new Adapter<>(nbbrd.io.text.Formatter.onFile());
    private final Formatter<Charset> CHARSET_FORMATTER = new Adapter<>(nbbrd.io.text.Formatter.onCharset());
    private final Formatter<double[]> DOUBLE_ARRAY_FORMATTER = new Adapter<>(nbbrd.io.text.Formatter.onDoubleArray());
    private final Formatter<?> OBJECT_TO_STRING_FORMATTER = new Adapter<>(nbbrd.io.text.Formatter.onObjectToString());
    private final Formatter<String[]> STRING_ARRAY_FORMATTER = new Adapter<>(nbbrd.io.text.Formatter.onStringArray());

    private final Formatter<XmlInformationSet> INFORMATIONFORMATTER;

    static {
        INFORMATIONFORMATTER = Formatters.onJAXB(XmlInformationSet.class, true);
    }

    public String formatAsString(InformationSet info) {
        XmlInformationSet xmlSet = new XmlInformationSet();
        xmlSet.copy(info);
        return INFORMATIONFORMATTER.formatAsString(xmlSet);
    }
    
}
