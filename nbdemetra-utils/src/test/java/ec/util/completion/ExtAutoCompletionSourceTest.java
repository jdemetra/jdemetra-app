/*
 * Copyright 2016 National Bank of Belgium
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
package ec.util.completion;

import ec.util.completion.AutoCompletionSource.Behavior;
import static ec.util.completion.AutoCompletionSource.Behavior.NONE;
import static ec.util.completion.AutoCompletionSource.Behavior.SYNC;
import ec.util.completion.ExtAutoCompletionSource.Loader;
import ec.util.completion.ExtAutoCompletionSource.Request;
import static ec.util.completion.ExtAutoCompletionSource.basicFilter;
import static ec.util.completion.ExtAutoCompletionSource.builder;
import static ec.util.completion.ExtAutoCompletionSource.wrap;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.Test;

/**
 *
 * @author Philippe Charles
 */
public class ExtAutoCompletionSourceTest {

    private static List<?> callUnchecked(Request request) {
        try {
            return request.call();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static final AutoCompletionSource DATA = new AutoCompletionSource() {
        @Override
        public Behavior getBehavior(String term) {
            return Behavior.SYNC;
        }

        @Override
        public String toString(Object value) {
            return value.toString();
        }

        @Override
        public List<?> getValues(String term) throws Exception {
            return Stream.of("Hello", "World")
                    .filter(o -> o.contains(term))
                    .sorted()
                    .collect(Collectors.toList());
        }
    };

    @Test
    @SuppressWarnings("null")
    public void testBasicFilter() {
        Predicate<String> filter = basicFilter("hëlLô");
        assertThat(filter.test("hello")).isTrue();
        assertThat(filter.test("helloworld")).isTrue();
        assertThat(filter.test("worldhello")).isTrue();
        assertThat(filter.test("helworld")).isFalse();
        assertThat(filter.test("")).isFalse();

        assertThatThrownBy(() -> basicFilter(null)).isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    @SuppressWarnings("null")
    public void testWrap() {
        assertThat(wrap(DATA, "ll"))
                .extracting(Request::getTerm, Request::getBehavior, ExtAutoCompletionSourceTest::callUnchecked)
                .containsExactly("ll", SYNC, Arrays.asList("Hello"));

        assertThat(wrap(DATA, ""))
                .extracting(Request::getTerm, Request::getBehavior, ExtAutoCompletionSourceTest::callUnchecked)
                .containsExactly("", SYNC, Arrays.asList("Hello", "World"));

        assertThatThrownBy(() -> wrap(null, "ll")).isExactlyInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> wrap(AutoCompletionSources.empty(), null)).isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    @SuppressWarnings("null")
    public void testBuilder() throws Exception {
        Loader<String> loader = o -> Arrays.asList("hello", "world");

        assertThatThrownBy(() -> builder(loader).behavior((Behavior) null)).isExactlyInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> builder(loader).behavior((Function<String, Behavior>) null)).isExactlyInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> builder(loader).postProcessor(null)).isExactlyInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> builder(loader).valueToString(null)).isExactlyInstanceOf(NullPointerException.class);

        ExtAutoCompletionSource source;

        source = builder(loader)
                .behavior(o -> o.isEmpty() ? NONE : SYNC)
                .valueToString(String::toUpperCase)
                .postProcessor((values, term) -> values.stream().filter(basicFilter(term)).collect(Collectors.toList()))
                .build();

        assertThat(source.toString("value")).isEqualTo("VALUE");
        assertThat(source.getBehavior("")).isEqualTo(NONE);
        assertThat(source.getBehavior("ll")).isEqualTo(SYNC);
        assertThat(source.getValues("ll")).containsExactly("hello");
        assertThat(source.getRequest("ll").getBehavior()).isEqualTo(SYNC);
        assertThat(source.getRequest("ll").getTerm()).isEqualTo("ll");
        assertThat(source.getRequest("ll").call()).containsExactly("hello");
    }
}
