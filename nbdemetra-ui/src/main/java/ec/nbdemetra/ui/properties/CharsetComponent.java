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
package ec.nbdemetra.ui.properties;

import ec.util.completion.AutoCompletionSources;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 *
 * @author Philippe Charles
 */
@Deprecated
public class CharsetComponent extends AutoCompletedComboBox<Charset> {

    public CharsetComponent() {
        setAutoCompletion(AutoCompletionSources.of(false, charsets()));
    }

    @Override
    public Charset getValue() {
        return Charset.forName(this.textComponent.getText());
    }

    @Override
    public void setValue(Charset value) {
        this.textComponent.setText(value.name());
    }

    static String[] charsets() {
        Charset[] charsets = {StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII, StandardCharsets.UTF_16, StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE, StandardCharsets.UTF_8};

        String[] result = new String[charsets.length + 1];
        for (int i = 0; i < result.length - 1; i++) {
            result[i] = charsets[i].name();
        }
        result[result.length - 1] = Charset.defaultCharset().name();
        Arrays.sort(result);
        return result;
    }
}
