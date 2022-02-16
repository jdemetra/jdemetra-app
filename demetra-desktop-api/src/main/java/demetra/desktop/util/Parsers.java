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
package demetra.desktop.util;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import demetra.information.InformationSet;
import demetra.toolkit.io.xml.information.XmlInformationSet;
import java.io.File;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.StreamSupport;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import nbbrd.io.xml.bind.Jaxb;

/**
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public final class Parsers {

    @Nullable
    public <T> T parseFirstNotNull(@NonNull CharSequence input, @NonNull Iterable<? extends Parser<T>> parsers) {
        Objects.requireNonNull(input); // if parsers is empty
        for (Parser<T> o : parsers) {
            T result = o.parse(input);
            if (result != null) {
                return result;
            }
        }
        return null;

    }

    /**
     *
     * @param <T>
     * @param parsers
     * @return
     * @see Parsers#using(java.lang.Iterable)
     */
    @NonNull
    public <T> Parser<T> firstNotNull(Parser<T>... parsers) {
        return firstNotNull(ImmutableList.copyOf(parsers));
    }

    @NonNull
    public <T> Parser<T> firstNotNull(@NonNull ImmutableList<? extends Parser<T>> parsers) {
        return new Parser<T>() {
            @Override
            public T parse(CharSequence input) throws NullPointerException {
                return parseFirstNotNull(input, parsers);
            }
        };
    }

    /**
     * Creates a new parser using
     * {@link JAXBContext#newInstance(java.lang.Class[])}.
     * <p>
     * Note that "<i>{@link JAXBContext} is thread-safe and should only be
     * created once and reused to avoid the cost of initializing the metadata
     * multiple times. {@link Marshaller} and {@link Unmarshaller} are not
     * thread-safe, but are lightweight to create and could be created per
     * operation (<a
     * href="http://stackoverflow.com/a/7400735">http://stackoverflow.com/a/7400735</a>)".</i>
     *
     * @param <T>
     * @param classToBeParsed
     * @return
     */
    @NonNull
    public <T> Parser<T> onJAXB(@NonNull Class<T> classToBeParsed) {
        try {
            return onJAXB(JAXBContext.newInstance(classToBeParsed));
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
    }

    @NonNull
    public <T> Parser<T> onJAXB(@NonNull JAXBContext context) {
        try {
            return onJAXB(context.createUnmarshaller());
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
    }

    @NonNull
    public  <T> Parser<T> onJAXB(@NonNull Unmarshaller unmarshaller) {
        Jaxb.Parser<T> p = Jaxb.Parser.<T>builder().factory(() -> unmarshaller).build();
        return new FailSafeParser<T>() {
            @Override
            protected T doParse(CharSequence input) throws Exception {
                return p.parseChars(input);
            }
        };
    }

    @NonNull
    public Parser<Date> onDateFormat(@NonNull DateFormat dateFormat) {
        return new Adapter<>(nbbrd.io.text.Parser.onDateFormat(dateFormat));
    }

    @NonNull
    public Parser<Number> onNumberFormat(@NonNull NumberFormat numberFormat) {
        return new Adapter<>(nbbrd.io.text.Parser.onNumberFormat(numberFormat));
    }

    @NonNull
    public <T> Parser<T> onNull() {
        return new Adapter<>(nbbrd.io.text.Parser.onNull());
    }

    @NonNull
    public <T> Parser<T> ofInstance(@Nullable T instance) {
        return new Adapter<>(nbbrd.io.text.Parser.onConstant(instance));
    }

    @NonNull
    public Parser<File> fileParser() {
        return FILE_PARSER;
    }

    /**
     * Create a {@link Parser} that delegates its parsing to
     * {@link Integer#valueOf(java.lang.String)}.
     *
     * @return a non-null parser
     */
    @NonNull
    public Parser<Integer> intParser() {
        return INT_PARSER;
    }

    @NonNull
    public Parser<Long> longParser() {
        return LONG_PARSER;
    }

    @NonNull
    public Parser<Boolean> boolParser() {
        return BOOL_PARSER;
    }

    @NonNull
    public Parser<Character> charParser() {
        return CHAR_PARSER;
    }

    /**
     * Create a {@link Parser} that delegates its parsing to
     * {@link Double#valueOf(java.lang.String)}.
     *
     * @return a non-null parser
     */
    @NonNull
    public Parser<Double> doubleParser() {
        return DOUBLE_PARSER;
    }

    @NonNull
    public Parser<Charset> charsetParser() {
        return CHARSET_PARSER;
    }

    @NonNull
    public <T extends Enum<T>> Parser<T> enumParser(@NonNull Class<T> enumClass) {
        return new Adapter<>(nbbrd.io.text.Parser.onEnum(enumClass));
    }

    @NonNull
    public Parser<String> stringParser() {
        return STRING_PARSER;
    }

    @NonNull
    public Parser<double[]> doubleArrayParser() {
        return DOUBLE_ARRAY_PARSER;
    }

    @NonNull
    public Parser<String[]> stringArrayParser() {
        return STRING_ARRAY_PARSER;
    }

    @NonNull
    public Parser<Locale> localeParser() {
        return LOCALE_PARSER;
    }

    @NonNull
    public <X, Y> Parser<Y> compose(@NonNull Parser<X> parser, @NonNull Function<X, Y> after) {
        return new Parser<Y>() {
            @Override
            public Y parse(CharSequence input) throws NullPointerException {
                X tmp = parser.parse(input);
                return tmp != null ? after.apply(tmp) : null;
            }
        };
    }

    @NonNull
    public Parser<List<String>> onSplitter(@NonNull Splitter splitter) {
        Objects.requireNonNull(splitter);
        return new Adapter<>(nbbrd.io.text.Parser.onStringList(chars -> StreamSupport.stream(splitter.split(chars).spliterator(), false)));
    }

    /**
     *
     * @param <T>
     * @param parser
     * @return
     * @since 2.2.0
     */
    @NonNull
    public <T> Parser<T> wrap(@NonNull Parser<T> parser) {
        return parser instanceof Parser ? (Parser<T>) parser : new Wrapper<>(parser);
    }

    public static abstract class FailSafeParser<T> implements Parser<T> {

        @Override
        public T parse(CharSequence input) throws NullPointerException {
            Objects.requireNonNull(input);
            try {
                return doParse(input);
            } catch (Exception ex) {
                return null;
            }
        }

        @Nullable
        abstract protected T doParse(@NonNull CharSequence input) throws Exception;
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    @lombok.RequiredArgsConstructor
    private static final class Adapter<T> implements Parser<T> {

        private final nbbrd.io.text.Parser<T> parser;

        @Override
        public T parse(CharSequence input) {
            Objects.requireNonNull(input);
            return parser.parse(input);
        }

        @Override
        public java.util.Optional<T> parseValue(CharSequence input) {
            Objects.requireNonNull(input);
            return parser.parseValue(input);
        }
    }

    @lombok.RequiredArgsConstructor
    private static final class Wrapper<T> implements Parser<T> {

        private final Parser<T> parser;

        @Override
        public T parse(CharSequence input) {
            return parser.parse(input);
        }

        @Override
        public java.util.Optional<T> parseValue(CharSequence input) {
            return parser.parseValue(input);
        }

        @Override
        public Parser<T> orElse(Parser<T> other) {
            return parser.orElse(other);
        }

        @Override
        public <X> Parser<X> andThen(java.util.function.Function<? super T, ? extends X> after) {
            return parser.andThen(after);
        }
    }

    private static final Parser<File> FILE_PARSER = new Adapter<>(nbbrd.io.text.Parser.onFile());
    private static final Parser<Integer> INT_PARSER = new Adapter<>(nbbrd.io.text.Parser.onInteger());
    private static final Parser<Long> LONG_PARSER = new Adapter<>(nbbrd.io.text.Parser.onLong());
    private static final Parser<Double> DOUBLE_PARSER = new Adapter<>(nbbrd.io.text.Parser.onDouble());
    private static final Parser<Boolean> BOOL_PARSER = new Adapter<>(nbbrd.io.text.Parser.onBoolean());
    private static final Parser<Character> CHAR_PARSER = new Adapter<>(nbbrd.io.text.Parser.onCharacter());
    private static final Parser<Charset> CHARSET_PARSER = new Adapter<>(nbbrd.io.text.Parser.onCharset());
    private static final Parser<String> STRING_PARSER = new Adapter<>(nbbrd.io.text.Parser.onString());
    private static final Parser<double[]> DOUBLE_ARRAY_PARSER = new Adapter<>(nbbrd.io.text.Parser.onDoubleArray());
    private static final Parser<String[]> STRING_ARRAY_PARSER = new Adapter<>(nbbrd.io.text.Parser.onStringArray());
    private static final Parser<Locale> LOCALE_PARSER = new Adapter<>(nbbrd.io.text.Parser.onLocale());
    //</editor-fold>
    
    private final Parser<XmlInformationSet> INFORMATIONPARSER;

    static {
        INFORMATIONPARSER = Parsers.onJAXB(XmlInformationSet.class);
    }

    public InformationSet parseAsInformationSet(String stream){
        XmlInformationSet xml = INFORMATIONPARSER.parse(stream);
        return xml.create();
    }

}
