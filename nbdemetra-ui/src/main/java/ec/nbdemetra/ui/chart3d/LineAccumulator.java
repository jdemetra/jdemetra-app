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
package ec.nbdemetra.ui.chart3d;

import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;


/**
 * The class
 * <code>LineAccumulator</code> accumulates line drawing information and then
 * draws all accumulated lines together. It is used as contour lines accumulator
 * in Surface Plotter.
 *
 * @see Eric aro
 * @see Yanto Suryono <yanto@fedu.uec.ac.jp>
 * @author Mats Maggi
 */
public class LineAccumulator {

    private List<LineRecord> accumulator;

    /**
     * The constructor of
     * <code>LineAccumulator</code>
     */
    LineAccumulator() {
        accumulator = new LinkedList<LineRecord>();
    }

    /**
     * Adds a line to the accumulator.
     *
     * @param x1 the first point's x coordinate
     * @param y1 the first point's y coordinate
     * @param x2 the second point's x coordinate
     * @param y2 the second point's y coordinate
     */
    public void addLine(int x1, int y1, int x2, int y2) {
        if (x1 <= 0 || y1 <= 0 || x2 <= 0 || y2 <= 0) {
            return;
        }
        accumulator.add(new LineRecord(x1, y1, x2, y2));
    }

    /**
     * Clears accumulator.
     */
    public void clearAccumulator() {
        accumulator.clear();
    }

    /**
     * Draws all accumulated lines.
     *
     * @param g the graphics context to draw
     */
    public void drawAll(Graphics g) {
        for (LineRecord line : accumulator) {
            g.drawLine(line.x1, line.y1, line.x2, line.y2);
        }
    }
}
/**
 * Represents a stright line. Used by
 * <code>LineAccumulator</code> class.
 *
 * @see LineAccumulator
 */
class LineRecord {

    /**
     * @param x1 the first point's x coordinate
     */
    public final int x1;
    /**
     * @param y1 the first point's y coordinate
     */
    public final int y1;
    /**
     * @param x2 the second point's x coordinate
     */
    public final int x2;
    /**
     * @param y2 the second point's y coordinate
     */
    public final int y2;

    /**
     * The constructor of
     * <code>LineRecord</code>
     *
     * @param x1 the first point's x coordinate
     * @param y1 the first point's y coordinate
     * @param x2 the second point's x coordinate
     * @param y2 the second point's y coordinate
     */
    LineRecord(int x1, int y1, int x2, int y2) {
        super();
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
}
