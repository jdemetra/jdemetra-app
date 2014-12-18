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
package ec.nbdemetra.ui;

import ec.util.various.swing.ModernUI;
import java.awt.Component;
import java.awt.Toolkit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.Border;
import org.openide.windows.TopComponent;

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
        return isXP == null ? false : isXP.booleanValue();
    }

    @Nonnull
    public static JToolBar newInnerToolbar() {
        JToolBar result = new JToolBar();
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

    @Nonnull
    public static JScrollPane newJScrollPane() {
        return ModernUI.withEmptyBorders(new JScrollPane());
    }

    @Nonnull
    public static JScrollPane newJScrollPane(@Nonnull Component view) {
        return ModernUI.withEmptyBorders(new JScrollPane(view));
    }

    @Nonnull
    public static JSplitPane newJSplitPane(int orientation) {
        return ModernUI.withEmptyBorders(new JSplitPane(orientation));
    }

    @Nonnull
    public static JSplitPane newJSplitPane(int orientation, @Nonnull Component left, @Nonnull Component right) {
        return ModernUI.withEmptyBorders(new JSplitPane(orientation, left, right));
    }

    @Nonnull
    public static JSplitPane newJSplitPane(int orientation, boolean continuousLayout, @Nonnull Component left, @Nonnull Component right) {
        return ModernUI.withEmptyBorders(new JSplitPane(orientation, continuousLayout, left, right));
    }

    @Nullable
    public static <T extends TopComponent> T findTopComponentByNameAndClass(@Nonnull String name, @Nonnull Class<T> clazz) {
        for (TopComponent o : TopComponent.getRegistry().getOpened()) {
            if (o.getName().equals(name) && clazz.isInstance(o)) {
                return (T) o;
            }
        }
        return null;
    }

    @Nullable
    public static TopComponent findTopComponentByName(@Nonnull String name) {
        return findTopComponentByNameAndClass(name, TopComponent.class);
    }
}
