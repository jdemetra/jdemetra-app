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

import com.google.common.base.Strings;
import ec.util.completion.AutoCompletionSource;
import java.util.List;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

@Deprecated
public class AutoCompleteDocument extends PlainDocument {

    public static AutoCompleteDocument on(JTextComponent textComponent, AutoCompletionSource autoCompletion, String separator) {
        AutoCompleteDocument doc = new AutoCompleteDocument(textComponent, autoCompletion, separator);
        textComponent.setDocument(doc);
        return doc;
    }
    //
    final JTextComponent textComponent;
    AutoCompletionSource autoCompletion;
    String separator;

    public AutoCompleteDocument(JTextComponent textComponent, AutoCompletionSource autoCompletion, String separator) {
        this.textComponent = textComponent;
        this.autoCompletion = autoCompletion;
        this.separator = separator;
    }

    public void setAutoCompletion(AutoCompletionSource autoCompletion) {
        this.autoCompletion = autoCompletion;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        super.insertString(offs, str, a);

        String all = getText(0, getLength());

        int start = Strings.isNullOrEmpty(separator) ? 0 : all.lastIndexOf(separator) + separator.length();

        //System.out.println(offs + " " + str + " * " + all.substring(start));
        String target = all.substring(start);
        try {
            List<?> values = autoCompletion.getValues(target);
            for (Object o : values) {
                String tmp = autoCompletion.toString(o);
                if (tmp.startsWith(target)) {
                    String word = tmp.substring(target.length());
                    super.insertString(offs + str.length(), word, a);
                    textComponent.setCaretPosition(offs + str.length());
                    textComponent.moveCaretPosition(getLength());
                    break;
                }
            }
        } catch (Exception ex) {
            // do nothing?
        }
    }
}