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
package demetra.ui.util;

import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.OnEDT;
import java.awt.Color;
import java.awt.Image;
import java.beans.BeanInfo;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.Icon;

/**
 *
 * @author Philippe Charles
 */
public final class FontAwesomeUtils {

    private FontAwesomeUtils() {
        // static class
    }

    @OnEDT
    public static float toSize(int type) throws IllegalArgumentException {
        switch (type) {
            case BeanInfo.ICON_COLOR_16x16:
            case BeanInfo.ICON_MONO_16x16:
                return 14f;
            case BeanInfo.ICON_COLOR_32x32:
            case BeanInfo.ICON_MONO_32x32:
                return 28f;
        }
        throw new IllegalArgumentException("Not a valid type");
    }

    @OnEDT
    @NonNull
    public static Color toColor(int type) throws IllegalArgumentException {
        switch (type) {
            case BeanInfo.ICON_COLOR_16x16:
            case BeanInfo.ICON_MONO_16x16:
            case BeanInfo.ICON_COLOR_32x32:
            case BeanInfo.ICON_MONO_32x32:
                return Color.DARK_GRAY;
        }
        throw new IllegalArgumentException("Not a valid type");
    }

    @OnEDT
    @NonNull
    public static Icon getIcon(@NonNull FontAwesome fa, int type) throws IllegalArgumentException {
        return getIcon(fa, toColor(type), toSize(type));
    }

    @OnEDT
    @NonNull
    public static Image getImage(@NonNull FontAwesome fa, int type) throws IllegalArgumentException {
        return getImage(fa, toColor(type), toSize(type));
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    private static Icon getIcon(FontAwesome fa, Color color, float size) {
        String key = asKey(fa, color, size);
        Icon value = icons.get(key);
        if (value == null) {
            value = fa.getIcon(color, size);
            icons.put(key, value);
        }
        return value;
    }

    private static Image getImage(FontAwesome fa, Color color, float size) {
        String key = asKey(fa, color, size);
        Image value = images.get(key);
        if (value == null) {
            value = fa.getImage(color, size);
            images.put(key, value);
        }
        return value;
    }

    private static final Map<String, Icon> icons = new HashMap<>();
    private static final Map<String, Image> images = new HashMap<>();

    private static String asKey(FontAwesome fa, Color color, float size) {
        return fa.name() + "/" + color.getRGB() + "/" + size;
    }
    //</editor-fold>
}
