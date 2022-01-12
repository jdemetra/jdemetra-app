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
package demetra.desktop.ui.chart3d;

import java.awt.Point;

/**
 * The class
 * <code>SurfaceVertex</code> represents a surfaceVertex in 3D space.
 *
 * @see Eric aro
 * @see Yanto Suryono <yanto@fedu.uec.ac.jp>
 * @author Mats Maggi
 */
public final class SurfaceVertex {

    private Point projection;
    private int project_index;
    private static int master_project_index = 0;     // over 4 billion times to reset
    /**
     * The x coordinate
     */
    public float x;
    /**
     * The y coordinate
     */
    public float y;
    /**
     * The z coordinate
     */
    public float z;

    /**
     * The constructor of
     * <code>SurfaceVertex</code>. The x and y coordinated must be in normalized
     * form, i.e: in the range -10 .. +10.
     *
     * @param ix the x coordinate
     * @param iy the y coordinate
     * @param iz the z coordinate
     */
    public SurfaceVertex(float ix, float iy, float iz) {
        x = ix;
        y = iy;
        z = iz;
        project_index = master_project_index - 1;
    }

    /**
     * Determines whether this surfaceVertex is invalid, i.e has invalid
     * coordinates value.
     *
     * @return <code>true</code> if this surfaceVertex is invalid
     */
    public final boolean isInvalid() {
        return Float.isNaN(z);
    }

    /**
     * Gets the 2D projection of the surfaceVertex.
     *
     * @return the 2D projection
     */
    public final Point projection(Projector projector) {
        if (project_index != master_project_index) {
            projection = projector.project(x, y, (z - projector.zmin) * projector.zfactor - 10);
            project_index = master_project_index;
        }
        return projection;
    }

    /**
     * Transforms coordinate values to fit the scaling factor of the projector.
     * This routine is only used for transforming center of projection in
     * Surface Plotter.
     */
    public final void transform(Projector projector) {
        x = x / projector.getXScaling();
        y = y / projector.getYScaling();
        z = (projector.zmax - projector.zmin) * (z / projector.getZScaling() + 10) / 20 + projector.zmin;
    }

    /**
     * Invalidates all vertices. This will force the projector to recalculate
     * surfaceVertex projection.
     */
    public static void invalidate() {
        master_project_index++;
    }

    @Override
    public String toString() {
        return "SurfaceVertex{" + x + ", " + y + ", " + z + '}';
    }
}
