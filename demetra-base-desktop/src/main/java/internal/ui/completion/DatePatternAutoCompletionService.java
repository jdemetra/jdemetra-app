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
package internal.ui.completion;

import demetra.ui.completion.JAutoCompletionService;
import ec.util.completion.AutoCompletionSource;
import ec.util.completion.ExtAutoCompletionSource;
import ec.util.completion.swing.CustomListCellRenderer;
import ec.util.completion.swing.JAutoCompletion;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 * @since 1.3.2
 */
@ServiceProvider(service = JAutoCompletionService.class, path = JAutoCompletionService.DATE_PATTERN_PATH)
public final class DatePatternAutoCompletionService implements JAutoCompletionService {

    private final AutoCompletionSource source = datePatternLetterSource();
    private final ListCellRenderer renderer = new DatePatternLetterRenderer();

    @Override
    public JAutoCompletion bind(JTextComponent textComponent) {
        JAutoCompletion result = new JAutoCompletion(textComponent);
        result.setMinLength(0);
        result.setSeparator(" ");
        result.setSource(source);
        result.getList().setCellRenderer(renderer);
        return result;
    }

    // http://docs.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
    private enum DatePatternLetter {

        G("Era designator", DatePatternPresentation.TEXT),
        y("Year", DatePatternPresentation.YEAR),
        M("Month in year", DatePatternPresentation.MONTH),
        w("Week in year", DatePatternPresentation.NUMBER),
        W("Week in month", DatePatternPresentation.NUMBER),
        D("Day in year", DatePatternPresentation.NUMBER),
        d("Day in month", DatePatternPresentation.NUMBER),
        F("Day of week in month", DatePatternPresentation.NUMBER),
        E("Day in week", DatePatternPresentation.TEXT),
        a("Am/pm marker", DatePatternPresentation.TEXT),
        H("Hour in day (0-23)", DatePatternPresentation.NUMBER),
        k("Hour in day (1-24)", DatePatternPresentation.NUMBER),
        K("Hour in am/pm (0-11)", DatePatternPresentation.NUMBER),
        h("Hour in am/pm (1-12)", DatePatternPresentation.NUMBER),
        m("Minute in hour", DatePatternPresentation.NUMBER),
        s("Second in minute", DatePatternPresentation.NUMBER),
        S("Millisecond", DatePatternPresentation.NUMBER),
        z("Time zone", DatePatternPresentation.OTHER),
        Z("Time zone", DatePatternPresentation.OTHER);
        //
        final String dateComponent;
        final DatePatternPresentation presentation;

        private DatePatternLetter(String dateComponent, DatePatternPresentation presentation) {
            this.dateComponent = dateComponent;
            this.presentation = presentation;
        }

        @Override
        public String toString() {
            return name();
        }
    }

    private enum DatePatternPresentation {

        TEXT("<html>For <b>formatting</b>, if the number of pattern letters is 4 or more, the full form is used; otherwise a short or abbreviated form is used if available.<br>For <b>parsing</b>, both forms are accepted, independent of the number of pattern letters."),
        YEAR("<html>For <b>formatting</b>, if the number of pattern letters is 2, the year is truncated to 2 digits; otherwise it is interpreted as a <b>number</b>.<br>For <b>parsing</b>, if the number of pattern letters is more than 2, the year is interpreted literally, regardless of the number of digits."),
        MONTH("<html>If the number of pattern letters is 3 or more, the month is interpreted as <b>text</b>; otherwise, it is interpreted as a <b>number</b>."),
        NUMBER("<html>For <b>formatting</b>, the number of pattern letters is the minimum number of digits, and shorter numbers are zero-padded to this amount.<br>For <b>parsing</b>, the number of pattern letters is ignored unless it's needed to separate two adjacent fields."),
        OTHER("");
        final String description;

        private DatePatternPresentation(String description) {
            this.description = description;
        }
    }

    private static AutoCompletionSource datePatternLetterSource() {
        return ExtAutoCompletionSource
                .builder(DatePatternAutoCompletionService::getDatePatternLetters)
                .behavior(AutoCompletionSource.Behavior.SYNC)
                .postProcessor(DatePatternAutoCompletionService::getDatePatternLetter)
                .build();
    }

    private static List<DatePatternLetter> getDatePatternLetters() {
        return Arrays.asList(DatePatternLetter.values());
    }

    private static List<DatePatternLetter> getDatePatternLetter(List<DatePatternLetter> allValues, String term) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return allValues.stream()
                .filter(o -> filter.test(o.name()) || filter.test(o.dateComponent))
                .sorted()
                .collect(Collectors.toList());
    }

    private static class DatePatternLetterRenderer extends CustomListCellRenderer<DatePatternLetter> {

        @Override
        protected String getValueAsString(DatePatternLetter value) {
            return "(" + String.valueOf(value.name()) + ") " + value.dateComponent;
        }

        @Override
        protected String toToolTipText(String term, JList list, DatePatternLetter value, int index, boolean isSelected, boolean cellHasFocus) {
            return value.presentation.description;
        }
    }
}
