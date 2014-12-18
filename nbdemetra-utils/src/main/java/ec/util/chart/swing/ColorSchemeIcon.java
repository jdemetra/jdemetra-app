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
package ec.util.chart.swing;

import ec.util.chart.ColorScheme;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.swing.Icon;

/**
 * Displays a color scheme as an icon.
 *
 * @author Philippe Charles
 */
public final class ColorSchemeIcon implements Icon {

    private static final Dimension DEFAULT_SIZE = new Dimension(17, 16);
    private static final int DEFAULT_BORDER_THICKNESS = 1;
    private static final int DEFAULT_COLOR_COUNT = 3;

    private final Dimension size;
    private final int borderThickness;
    private final SwingColorSchemeSupport support;
    private final int colorCount;
    // computed values
    private final int colorWidth;
    private final int colorHeight;

    public ColorSchemeIcon(@Nonnull ColorScheme colorScheme) {
        this(DEFAULT_SIZE, DEFAULT_BORDER_THICKNESS, SwingColorSchemeSupport.from(colorScheme), DEFAULT_COLOR_COUNT);
    }

    private ColorSchemeIcon(@Nonnull Dimension size, int borderThickness, @Nonnull SwingColorSchemeSupport support, int colorCount) {
        this.size = Objects.requireNonNull(size, "size");
        if (borderThickness < 0) {
            throw new IllegalArgumentException("Border thickness must be >= 0");
        }
        this.borderThickness = borderThickness;
        this.support = Objects.requireNonNull(support, "colorSchemeSupport");
        if (colorCount <= 0) {
            throw new IllegalArgumentException("Color count must be > 0");
        }
        this.colorCount = colorCount;
        //
        this.colorWidth = (size.width - borderThickness * 2) / colorCount;
        this.colorHeight = size.height - borderThickness * 2;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Color savedColor = g.getColor();
        g.setColor(support.getPlotColor());
        g.fillRect(x, y, size.width, size.height);
        for (int i = 0; i < colorCount; i++) {
            g.setColor(support.getLineColor(i));
            g.fillRect(x + borderThickness + colorWidth * i, y + borderThickness, colorWidth, colorHeight);
        }
        g.setColor(savedColor);
    }

    @Override
    public int getIconWidth() {
        return size.width;
    }

    @Override
    public int getIconHeight() {
        return size.height;
    }
}
