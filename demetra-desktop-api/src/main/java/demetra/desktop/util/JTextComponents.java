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
package demetra.desktop.util;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.*;
import java.util.Optional;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 *
 * @author Philippe Charles
 * @since 2.2.0
 */
public final class JTextComponents {

    private JTextComponents() {
        // static class
    }

    public static void enableDecimalMappingOnNumpad(JFormattedTextField component) {
        enableDecimalMappingOnNumpad(component, () -> {
            JFormattedTextField.AbstractFormatter af = component.getFormatter();
            if (af instanceof NumberFormatter) {
                Format format = ((NumberFormatter) af).getFormat();
                if (format instanceof DecimalFormat) {
                    return ((DecimalFormat) format).getDecimalFormatSymbols().getDecimalSeparator();
                }
            }
            return '.';
        });
    }

    public static void enableDecimalMappingOnNumpad(JTextComponent component, IntSupplier decimalSeparator) {
        component.addKeyListener(new KeyAdapter() {
            private boolean mappingRequested = false;

            private boolean isDecimalKeyOnNumpad(KeyEvent e) {
                return e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD
                        && e.getKeyCode() == KeyEvent.VK_DECIMAL;
            }

            private boolean isDecimalSeparator(KeyEvent e) {
                return e.getKeyChar() == decimalSeparator.getAsInt();
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (mappingRequested) {
                    e.setKeyChar((char) decimalSeparator.getAsInt());
                    mappingRequested = false;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                mappingRequested = false;
            }

            @Override
            public void keyPressed(KeyEvent e) {
                mappingRequested = isDecimalKeyOnNumpad(e) && !isDecimalSeparator(e);
            }
        });
    }

    public static void enableValidationFeedback(JTextComponent component, Predicate<? super String> validator) {
        enableValidationFeedback(component, validator, JTextComponents::getInvalidForeground);
    }

    public static void enableValidationFeedback(JTextComponent component, Predicate<? super String> validator, Supplier<Color> feedbackColor) {
        component.getDocument().addDocumentListener(new DocumentListener() {
            private final Color valid = component.getForeground();

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateForeground();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateForeground();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            private void updateForeground() {
                component.setForeground(validator.test(component.getText()) ? valid : feedbackColor.get());
            }
        });
    }

    public static boolean isDouble(NumberFormat format, String input) {
        ParsePosition pos = new ParsePosition(0);
        return format.parse(input, pos) != null
                && !(pos.getIndex() != input.length() || pos.getErrorIndex() != -1);
    }

    public static void fixMaxDecimals(NumberFormat format) {
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(64);
    }

    public static Optional peekValue(JFormattedTextField component) {
        try {
            return Optional.ofNullable(component.getFormatter().stringToValue(component.getText()));
        } catch (ParseException ex) {
            return Optional.empty();
        }
    }

    public static Color getInvalidForeground() {
        return Color.RED;
    }
}
