/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
