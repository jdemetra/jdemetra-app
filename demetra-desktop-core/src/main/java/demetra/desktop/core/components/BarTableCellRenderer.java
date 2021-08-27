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
package demetra.desktop.core.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Philippe Charles
 */
class BarTableCellRenderer extends DefaultTableCellRenderer {

    private final boolean horizontalBar;
    private double a, b = 0;

    public BarTableCellRenderer(boolean horizontalBar) {
        this.horizontalBar = true;
    }

    public void setBarValues(double min, double max, double val) {
        double diff = max - min;
        if (diff == 0) {
            a = b = 0;
        } else {
            double zero = min > 0 ? min : max < 0 ? max : 0;
            if (val >= zero) {
                a = (zero - min) / diff;
                b = (val - min) / diff;
            } else {
                a = (val - min) / diff;
                b = (zero - min) / diff;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (b - a > 0) {
            Graphics2D g2 = (Graphics2D) g;

            Rectangle oldClip = g2.getClipBounds();
            Color oldForeground = getForeground();
            Color oldBackground = getBackground();

            double d = b - a;
            if (horizontalBar) {
                g2.clipRect((int) (a * getWidth()), 0, (int) (d * getWidth()), getHeight());
            } else {
                g2.clipRect(0, (int) ((1 - b) * getHeight()), getWidth(), (int) (d * getHeight()));
            }
            setForeground(oldBackground);
            setBackground(oldForeground);

            super.paintComponent(g);

            setBackground(oldBackground);
            setForeground(oldForeground);
            g2.setClip(oldClip);
        }
    }
}
