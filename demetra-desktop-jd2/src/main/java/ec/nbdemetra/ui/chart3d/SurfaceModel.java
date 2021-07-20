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

/**
 * The model used to display any surface in JSurface
 *
 * @see Eric aro
 * @see Yanto Suryono <yanto@fedu.uec.ac.jp>
 * @author Mats Maggi
 */
public interface SurfaceModel {

    public enum PlotType {

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
    };

    public enum PlotColor {

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
    };

    public void addPropertyChangeListener(java.beans.PropertyChangeListener listener);

    public void addPropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener);

    public void removePropertyChangeListener(java.beans.PropertyChangeListener listener);

    public void removePropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener);

    public void addChangeListener(javax.swing.event.ChangeListener listener);

    public void removeChangeListener(javax.swing.event.ChangeListener listener);

    public SurfaceVertex[] getSurfaceVertex();

    public Projector getProjector();

    public boolean isAutoScaleZ();

    public PlotType getPlotType();

    public PlotColor getPlotColor();

    public int getCalcDivisions();

    public int getContourLines();

    public int getDispDivisions();

    public float getXMin();

    public float getYMin();

    public float getZMin();

    public float getXMax();

    public float getYMax();

    public float getZMax();

    public SurfaceColor getColorModel(); // not the right place, but JSurface does not work with any colorset, should be removed lately

    /**
     * Determines whether the delay regeneration checkbox is checked.
     *
     * @return <code>true</code> if the checkbox is checked, <code>false</code>
     * otherwise
     */
    public boolean isExpectDelay();

    /**
     * Determines whether to show bounding box.
     *
     * @return <code>true</code> if to show bounding box
     */
    public boolean isBoxed();

    /**
     * Determines whether to show x-y mesh.
     *
     * @return <code>true</code> if to show x-y mesh
     */
    public boolean isMesh();

    /**
     * Determines whether to scale axes and bounding box.
     *
     * @return <code>true</code> if to scale bounding box
     */
    public boolean isScaleBox();

    /**
     * Determines whether to show x-y ticks.
     *
     * @return <code>true</code> if to show x-y ticks
     */
    public boolean isDisplayXY();

    /**
     * Determines whether to show z ticks.
     *
     * @return <code>true</code> if to show z ticks
     */
    public boolean isDisplayZ();

    /**
     * Determines whether to show face grids.
     *
     * @return <code>true</code> if to show face grids
     */
    public boolean isDisplayGrids();

    /**
     * Determines whether the first function is selected.
     *
     * @return <code>true</code> if the first function is checked,
     * <code>false</code> otherwise
     */
    public boolean isPlotFunction1();

    /**
     * Determines whether the first function is selected.
     *
     * @return <code>true</code> if the first function is checked,
     * <code>false</code> otherwise
     */
    public boolean isPlotFunction2();

    /**
     * Sets data availability flag
     */
    public boolean isDataAvailable();
    
    public int getNbDecimals();
    
    public void setNbDecimals(int nb);
}
