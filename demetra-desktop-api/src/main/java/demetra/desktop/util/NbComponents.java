/*
 * Copyright 2013 National Bank of Belgium
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

import ec.util.various.swing.ModernUI;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.awt.Toolbar;
import org.openide.windows.TopComponent;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Optional;

/**
 * TODO: move somewhere else?
 *
 * @author Philippe Charles
 */
public final class NbComponents {

    private NbComponents() {
        // static class
    }

    private static final boolean AQUA = "Aqua".equals(UIManager.getLookAndFeel().getID()); //NOI18N

    public static boolean isXPTheme() {
        Boolean isXP = (Boolean) Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.themeActive"); //NOI18N
        return isXP != null && isXP;
    }

    @NonNull
    public static JToolBar newInnerToolbar() {
        JToolBar result = new Toolbar();
        Border b = (Border) UIManager.get("Nb.Editor.Toolbar.border"); //NOI18N
        result.setBorder(b);
        result.setFloatable(false);
        result.setFocusable(true);
        if ("Windows".equals(UIManager.getLookAndFeel().getID()) && !isXPTheme()) {
            result.setRollover(true);
        } else if (AQUA) {
            result.setBackground(UIManager.getColor("NbExplorerView.background"));
        }
        return result;
    }

    @NonNull
    public static JScrollPane newJScrollPane() {
        return ModernUI.withEmptyBorders(new JScrollPane());
    }

    @NonNull
    public static JScrollPane newJScrollPane(@NonNull Component view) {
        return ModernUI.withEmptyBorders(new JScrollPane(view));
    }

    @NonNull
    public static JSplitPane newJSplitPane(int orientation) {
        return ModernUI.withEmptyBorders(new JSplitPane(orientation));
    }

    @NonNull
    public static JSplitPane newJSplitPane(int orientation, @NonNull Component left, @NonNull Component right) {
        return ModernUI.withEmptyBorders(new JSplitPane(orientation, left, right));
    }

    @NonNull
    public static JSplitPane newJSplitPane(int orientation, boolean continuousLayout, @NonNull Component left, @NonNull Component right) {
        return ModernUI.withEmptyBorders(new JSplitPane(orientation, continuousLayout, left, right));
    }

    @NonNull
    public static <T extends TopComponent> Optional<T> findTopComponentByNameAndClass(@NonNull String name, @NonNull Class<T> type) {
        return TopComponent.getRegistry()
                .getOpened()
                .stream()
                .filter(o -> o.getName().equals(name) && type.isInstance(o))
                .map(type::cast)
                .findFirst();
    }

    @NonNull
    public static Optional<TopComponent> findTopComponentByName(@NonNull String name) {
        return findTopComponentByNameAndClass(name, TopComponent.class);
    }
}
