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
package ec.nbdemetra.ui.completion;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import ec.util.completion.AutoCompletionSource;
import ec.util.completion.ExtAutoCompletionSource;
import ec.util.completion.swing.CustomListCellRenderer;
import ec.util.completion.swing.JAutoCompletion;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 * @since 1.3.2
 */
@ServiceProvider(service = JAutoCompletionService.class, path = JAutoCompletionService.CHARSET_PATH)
public class CharsetAutoCompletionService extends JAutoCompletionService {

    private final AutoCompletionSource source = charsetSource();
    private final ListCellRenderer renderer = new CharsetRenderer();

    @Override
    public JAutoCompletion bind(JTextComponent textComponent) {
        JAutoCompletion result = new JAutoCompletion(textComponent);
        result.setMinLength(0);
        result.setSource(source);
        result.getList().setCellRenderer(renderer);
        return result;
    }

    private static AutoCompletionSource charsetSource() {
        return ExtAutoCompletionSource
                .builder(Suppliers.memoize(CharsetAutoCompletionService::getCharsets)::get)
                .behavior(AutoCompletionSource.Behavior.SYNC)
                .postProcessor(CharsetAutoCompletionService::getCharsets)
                .build();
    }

    private static List<Charset> getCharsets() {
        return ImmutableList.copyOf(Charset.availableCharsets().values());
    }

    private static List<Charset> getCharsets(List<Charset> allValues, String term) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return allValues.stream()
                .filter(o -> filter.test(o.name()))
                .sorted()
                .collect(Collectors.toList());
    }

    private static final class CharsetRenderer extends CustomListCellRenderer<Charset> {

        @Override
        protected String getValueAsString(Charset value) {
            return value.name();
        }
    }
}
