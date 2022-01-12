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

/**
 * The model used to display any surface in JSurface
 *
 * @see Eric aro
 * @see Yanto Suryono <yanto@fedu.uec.ac.jp>
 * @author Mats Maggi
 */
public interface SurfaceModel {

    enum PlotType {

        SURFACE("3D Surface"),
        WIREFRAME("3D Wireframe"),
        DENSITY("2D Density"),
        CONTOUR("2D Contour");
        
        final String att;

        PlotType(String att) {
            this.att = att;
        }

        public String getPropertyName() {
            return att;
        }
    }

    enum PlotColor {

        OPAQUE("Hidden Surface"),
        SPECTRUM("Color Spectrum"),
        DUALSHADE("Dual Shade"),
        GRAYSCALE("Gray Scale"),
        FOG("Fog");
        final String att;

        PlotColor(String att) {
            this.att = att;
        }

        public String getPropertyName() {
            return att;
        }
    }

    void addPropertyChangeListener(java.beans.PropertyChangeListener listener);

    void addPropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener);

    void removePropertyChangeListener(java.beans.PropertyChangeListener listener);

    void removePropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener);

    void addChangeListener(javax.swing.event.ChangeListener listener);

    void removeChangeListener(javax.swing.event.ChangeListener listener);

    SurfaceVertex[] getSurfaceVertex();

    Projector getProjector();

    boolean isAutoScaleZ();

    PlotType getPlotType();

    PlotColor getPlotColor();

    int getCalcDivisions();

    int getContourLines();

    int getDispDivisions();

    float getXMin();

    float getYMin();

    float getZMin();

    float getXMax();

    float getYMax();

    float getZMax();

    SurfaceColor getColorModel(); // not the right place, but JSurface does not work with any colorset, should be removed lately

    /**
     * Determines whether the delay regeneration checkbox is checked.
     *
     * @return <code>true</code> if the checkbox is checked, <code>false</code>
     * otherwise
     */
    boolean isExpectDelay();

    /**
     * Determines whether to show bounding box.
     *
     * @return <code>true</code> if to show bounding box
     */
    boolean isBoxed();

    /**
     * Determines whether to show x-y mesh.
     *
     * @return <code>true</code> if to show x-y mesh
     */
    boolean isMesh();

    /**
     * Determines whether to scale axes and bounding box.
     *
     * @return <code>true</code> if to scale bounding box
     */
    boolean isScaleBox();

    /**
     * Determines whether to show x-y ticks.
     *
     * @return <code>true</code> if to show x-y ticks
     */
    boolean isDisplayXY();

    /**
     * Determines whether to show z ticks.
     *
     * @return <code>true</code> if to show z ticks
     */
    boolean isDisplayZ();

    /**
     * Determines whether to show face grids.
     *
     * @return <code>true</code> if to show face grids
     */
    boolean isDisplayGrids();

    /**
     * Determines whether the first function is selected.
     *
     * @return <code>true</code> if the first function is checked,
     * <code>false</code> otherwise
     */
    boolean isPlotFunction1();

    /**
     * Determines whether the first function is selected.
     *
     * @return <code>true</code> if the first function is checked,
     * <code>false</code> otherwise
     */
    boolean isPlotFunction2();

    /**
     * Sets data availability flag
     */
    boolean isDataAvailable();
    
    int getNbDecimals();
    
    void setNbDecimals(int nb);
}
