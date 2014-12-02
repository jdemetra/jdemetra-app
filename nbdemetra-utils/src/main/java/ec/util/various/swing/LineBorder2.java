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
package ec.util.various.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import javax.swing.border.Border;

/**
 *
 * @author Philippe Charles
 */
public final class LineBorder2 implements Border {

    private final Color color;
    private final int left, right, top, bottom;

    public LineBorder2(Color color, int top, int left, int bottom, int right) {
        this.color = color;
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;

            Color oldColor = g2d.getColor();
            g2d.setColor(color);

            Path2D path = new Path2D.Float(Path2D.WIND_EVEN_ODD);
            path.append(new Rectangle2D.Float(x, y, width, height), false);
            path.append(new Rectangle2D.Float(x + left, y + top, width - left - right, height - top - bottom), false);
            g2d.fill(path);

            g2d.setColor(oldColor);
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(top, left, bottom, right);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}
