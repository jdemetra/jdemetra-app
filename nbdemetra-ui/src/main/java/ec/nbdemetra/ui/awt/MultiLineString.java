/*
 * Copyright 2015 National Bank of Belgium
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
package ec.nbdemetra.ui.awt;

import javax.annotation.Nonnull;

/**
 *
 * @author Philippe Charles
 */
public final class MultiLineString {

    private MultiLineString() {
        // static class
    }

    @Nonnull
    public static String join(@Nonnull String input) {
        return join(input, " \u25b6 ");
    }

    @Nonnull
    public static String join(@Nonnull String input, @Nonnull String separator) {
        return input.replace("\n", separator);
    }

    @Nonnull
    public static String toHtml(@Nonnull String input) {
        return "<html>" + input.replace("\n", "<br>");
    }

    @Nonnull
    public static String last(@Nonnull String input) {
        int index = input.lastIndexOf("\n");
        return index == -1 ? input : input.substring(index + 1);
    }

    @Nonnull
    public static String lastWithMax(@Nonnull String input, int maxLength) {
        String last = last(input);
        return last.length() < maxLength ? last : ("\u2026 " + last.substring(last.length() - maxLength));
    }
}
