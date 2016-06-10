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
package ec.util.various.swing;

import java.awt.Color;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 * http://alvinalexander.com/java/java-uimanager-color-keys-list
 * http://nadeausoftware.com/articles/2008/11/all_ui_defaults_names_common_java_look_and_feels_windows_mac_os_x_and_linux
 *
 * @author Philippe Charles
 */
public enum StandardSwingColor {

    TABLE_HEADER_BACKGROUND("TableHeader.background"),
    TABLE_HEADER_FOREGROUND("TableHeader.foreground"),
    TABLE_BACKGROUND("Table.background"),
    TABLE_FOREGROUND("Table.foreground"),
    TABLE_SELECTION_BACKGROUND("Table.selectionBackground"),
    TABLE_SELECTION_FOREGROUND("Table.selectionForeground"),
    TEXT_FIELD_INACTIVE_BACKGROUND("TextField.inactiveBackground"),
    TEXT_FIELD_INACTIVE_FOREGROUND("TextField.inactiveForeground"),
    CONTROL("control");

    private static final JTable TABLE = new JTable();
    private static final JTextField TEXT_FIELD = new JTextField();
    private static final JPanel PANEL = new JPanel();

    private final String key;

    private StandardSwingColor(String key) {
        this.key = key;
    }

    @Nonnull
    public String key() {
        return key;
    }

    @Nullable
    public Color value() {
        Color result = null;
        switch (this) {
            case TABLE_HEADER_BACKGROUND:
                result = TABLE.getTableHeader().getBackground();
                break;
            case TABLE_HEADER_FOREGROUND:
                result = TABLE.getTableHeader().getForeground();
                break;
            case TABLE_BACKGROUND:
                result = TABLE.getBackground();
                break;
            case TABLE_FOREGROUND:
                result = TABLE.getForeground();
                break;
            case TABLE_SELECTION_BACKGROUND:
                result = TABLE.getSelectionBackground();
                break;
            case TABLE_SELECTION_FOREGROUND:
                result = TABLE.getSelectionForeground();
                break;
            case TEXT_FIELD_INACTIVE_BACKGROUND:
                break;
            case TEXT_FIELD_INACTIVE_FOREGROUND:
                result = TEXT_FIELD.getDisabledTextColor();
                break;
            case CONTROL:
                result = PANEL.getBackground();
                break;
        }
        return result != null ? result : UIManager.getColor(key);
    }

    @Nonnull
    public Color or(@Nonnull Color fallback) {
        Objects.requireNonNull(fallback);
        Color result = value();
        return result != null ? result : fallback;
    }
}
