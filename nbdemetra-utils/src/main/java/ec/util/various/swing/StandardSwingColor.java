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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JTable;
import javax.swing.UIManager;

/**
 *
 * @author Philippe Charles
 */
public enum StandardSwingColor {

    TABLE_HEADER_BACKGROUND("TableHeader.background"),
    TABLE_HEADER_FOREGROUND("TableHeader.foreground"),
    TABLE_BACKGROUND("Table.background"),
    TABLE_FOREGROUND("Table.foreground"),
    TABLE_SELECTION_BACKGROUND("Table.selectionBackground"),
    TABLE_SELECTION_FOREGROUND("Table.selectionForeground");

    private static final JTable TABLE = new JTable();

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
        }
        return result != null ? result : UIManager.getColor(key);
    }
}
