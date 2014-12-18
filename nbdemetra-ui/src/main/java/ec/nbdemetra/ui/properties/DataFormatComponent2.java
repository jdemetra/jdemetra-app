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

import ec.nbdemetra.ui.completion.JAutoCompletionService;
import ec.tss.tsproviders.utils.DataFormat;
import ec.util.completion.swing.XPopup;
import ec.util.various.swing.TextPrompt;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Philippe Charles
 * @since 1.3.2
 */
public final class DataFormatComponent2 extends JComponent {

    public static final String DATA_FORMAT_PROPERTY = "dataFormat";
    public static final String PREVIEW_VISIBLE_PROPERTY = "previewVisible";
    private static final DataFormat DEFAULT_DATA_FORMAT = DataFormat.DEFAULT;
    private static final boolean DEFAULT_PREVIEW_VISIBLE = true;
    //
    private final JTextComponent locale;
    private final JTextComponent datePattern;
    private final JTextComponent numberPattern;
    private final Listener listener;
    private final Date dateSample;
    private final CustomPreview datePatternPreview;
    private final Number numberSample;
    private final CustomPreview numberPatternPreview;
    private DataFormat dataFormat;
    private boolean previewVisible;

    public DataFormatComponent2() {
        this.locale = new JTextField();
        this.datePattern = new JTextField();
        this.numberPattern = new JTextField();
        this.listener = new Listener();

        this.dateSample = new Date();
        this.datePatternPreview = new CustomPreview(datePattern);
        this.numberSample = 1234.5;
        this.numberPatternPreview = new CustomPreview(numberPattern);

        this.dataFormat = DEFAULT_DATA_FORMAT;
        this.previewVisible = DEFAULT_PREVIEW_VISIBLE;

        JAutoCompletionService.forPathBind(JAutoCompletionService.LOCALE_PATH, locale);
        JAutoCompletionService.forPathBind(JAutoCompletionService.DATE_PATTERN_PATH, datePattern);

        new TextPrompt("locale", locale).setEnabled(false);
        new TextPrompt("date pattern", datePattern).setEnabled(false);
        new TextPrompt("number pattern", numberPattern).setEnabled(false);

        onDataFormatChange();

        locale.getDocument().addDocumentListener(listener);
        datePattern.getDocument().addDocumentListener(listener);
        numberPattern.getDocument().addDocumentListener(listener);

        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case DATA_FORMAT_PROPERTY:
                        onDataFormatChange();
                        break;
                    case PREVIEW_VISIBLE_PROPERTY:
                        onPreviewVisibleChange();
                        break;
                }
            }
        });
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                refreshPreviews();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                refreshPreviews();
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
                refreshPreviews();
            }
        });

        GridLayout layout = new GridLayout(1, 3);
        layout.setHgap(3);
        setLayout(layout);
        add(locale);
        add(datePattern);
        add(numberPattern);
    }

    private void refreshPreviews() {
        datePatternPreview.setText(dataFormat.dateFormatter().tryFormatAsString(dateSample).or("\u203C "));
        numberPatternPreview.setText(dataFormat.numberFormatter().tryFormatAsString(numberSample).or("\u203C "));
    }

    private void onDataFormatChange() {
        if (listener.enabled) {
            listener.enabled = false;
            locale.setText(dataFormat.getLocaleString());
            datePattern.setText(dataFormat.getDatePattern());
            numberPattern.setText(dataFormat.getNumberPattern());
            listener.enabled = true;
        }
        refreshPreviews();
    }

    private void onPreviewVisibleChange() {
        datePatternPreview.setVisible(previewVisible);
        numberPatternPreview.setVisible(previewVisible);
    }

    public DataFormat getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(DataFormat dataFormat) {
        DataFormat old = this.dataFormat;
        this.dataFormat = dataFormat != null ? dataFormat : DEFAULT_DATA_FORMAT;
        firePropertyChange(DATA_FORMAT_PROPERTY, old, this.dataFormat);
    }

    public boolean isPreviewVisible() {
        return previewVisible;
    }

    public void setPreviewVisible(boolean previewVisible) {
        boolean old = this.previewVisible;
        this.previewVisible = previewVisible;
        firePropertyChange(PREVIEW_VISIBLE_PROPERTY, old, this.previewVisible);
    }

    private class Listener implements DocumentListener {

        boolean enabled = true;

        @Override
        public void removeUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            if (enabled) {
                enabled = false;
                setDataFormat(DataFormat.create(locale.getText(), datePattern.getText(), numberPattern.getText()));
                enabled = true;
            }
        }
    }

    private static class CustomPreview {

        final JTextComponent target;
        final XPopup popup;
        final JLabel label;
        boolean visible;

        public CustomPreview(JTextComponent target) {
            this.target = target;
            this.popup = new XPopup();
            this.label = new JLabel();
            this.visible = true;
        }

        public void setText(String value) {
            popup.hide();
            label.setText(value);
            if (visible) {
                popup.show(target, label, XPopup.Anchor.TOP_LEADING, new Dimension());
            }
        }

        private void setVisible(boolean visible) {
            this.visible = visible;
            if (!visible) {
                popup.hide();
            }
        }
    }
}
