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
package demetra.ui.components;

import demetra.tsprovider.util.ObsFormat;
import demetra.ui.completion.AutoCompletion;
import demetra.ui.design.SwingComponent;
import demetra.ui.design.SwingProperty;
import ec.util.completion.swing.XPopup;
import ec.util.various.swing.TextPrompt;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import nbbrd.io.text.Parser;

/**
 *
 * @author Philippe Charles
 * @since 1.3.2
 */
@SwingComponent
public final class JObsFormatComponent extends JComponent {

    @SwingProperty
    public static final String OBS_FORMAT_PROPERTY = "obsFormat";

    @SwingProperty
    public static final String PREVIEW_VISIBLE_PROPERTY = "previewVisible";

    private static final ObsFormat DEFAULT_OBS_FORMAT = ObsFormat.DEFAULT;
    private static final boolean DEFAULT_PREVIEW_VISIBLE = true;

    private final JTextComponent locale;
    private final JTextComponent datePattern;
    private final JTextComponent numberPattern;
    private final Listener listener;
    private final LocalDateTime dateSample;
    private final CustomPreview datePatternPreview;
    private final Number numberSample;
    private final CustomPreview numberPatternPreview;
    private ObsFormat dataFormat;
    private boolean previewVisible;

    public JObsFormatComponent() {
        this.locale = new JTextField();
        this.datePattern = new JTextField();
        this.numberPattern = new JTextField();
        this.listener = new Listener();

        this.dateSample = LocalDateTime.now();
        this.datePatternPreview = new CustomPreview(datePattern);
        this.numberSample = 1234.5;
        this.numberPatternPreview = new CustomPreview(numberPattern);

        this.dataFormat = DEFAULT_OBS_FORMAT;
        this.previewVisible = DEFAULT_PREVIEW_VISIBLE;

        initComponents();
        enableProperties();
    }

    //<editor-fold defaultstate="collapsed" desc="Initialization">
    private void initComponents() {
        AutoCompletion.getDefault().bind(Locale.class, locale);
        AutoCompletion.getDefault().bind(LocalDate.class, datePattern);

        new TextPrompt("locale", locale).setEnabled(false);
        new TextPrompt("date pattern", datePattern).setEnabled(false);
        new TextPrompt("number pattern", numberPattern).setEnabled(false);

        onObsFormatChange();

        locale.getDocument().addDocumentListener(listener);
        datePattern.getDocument().addDocumentListener(listener);
        numberPattern.getDocument().addDocumentListener(listener);

        GridLayout layout = new GridLayout(1, 3);
        layout.setHgap(3);
        setLayout(layout);
        add(locale);
        add(datePattern);
        add(numberPattern);
    }

    private void enableProperties() {
        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case OBS_FORMAT_PROPERTY:
                    onObsFormatChange();
                    break;
                case PREVIEW_VISIBLE_PROPERTY:
                    onPreviewVisibleChange();
                    break;
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
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Events handlers">
    private void refreshPreviews() {
        datePatternPreview.setText(dataFormat.dateTimeFormatter().formatValueAsString(dateSample).orElse("\u203C "));
        numberPatternPreview.setText(dataFormat.numberFormatter().formatValueAsString(numberSample).orElse("\u203C "));
    }

    private void onObsFormatChange() {
        if (listener.enabled) {
            listener.enabled = false;
            Locale tmp = dataFormat.getLocale();
            locale.setText(tmp != null ? tmp.toString() : null);
            datePattern.setText(dataFormat.getDateTimePattern());
            numberPattern.setText(dataFormat.getNumberPattern());
            listener.enabled = true;
        }
        refreshPreviews();
    }

    private void onPreviewVisibleChange() {
        datePatternPreview.setVisible(previewVisible);
        numberPatternPreview.setVisible(previewVisible);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    @NonNull
    public ObsFormat getObsFormat() {
        return dataFormat;
    }

    public void setObsFormat(@Nullable ObsFormat dataFormat) {
        ObsFormat old = this.dataFormat;
        this.dataFormat = dataFormat != null ? dataFormat : DEFAULT_OBS_FORMAT;
        firePropertyChange(OBS_FORMAT_PROPERTY, old, this.dataFormat);
    }

    public boolean isPreviewVisible() {
        return previewVisible;
    }

    public void setPreviewVisible(boolean previewVisible) {
        boolean old = this.previewVisible;
        this.previewVisible = previewVisible;
        firePropertyChange(PREVIEW_VISIBLE_PROPERTY, old, this.previewVisible);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    private final class Listener implements DocumentListener {

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
                setObsFormat(ObsFormat.of(Parser.onLocale().parse(locale.getText()), datePattern.getText(), numberPattern.getText()));
                enabled = true;
            }
        }
    }

    private static final class CustomPreview {

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
    //</editor-fold>
}
