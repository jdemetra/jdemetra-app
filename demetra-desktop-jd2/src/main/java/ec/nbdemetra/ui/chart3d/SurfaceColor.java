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

import java.awt.Color;

/**
 * Interface used by JSurface for every color. Warning, some color are not
 * suitable for some drawing, be careful to sync it with the SurfaceModel
 *
 * @see Eric aro
 * @see Yanto Suryono <yanto@fedu.uec.ac.jp>
 * @author Mats Maggi
 *
 */
public interface SurfaceColor {

    public Color getBackgroundColor();

    public Color getLineBoxColor();

    public Color getBoxColor();

    public Color getLineColor();

    public Color getTextColor();

    public Color getLineColor(int curve, float z);

    public Color getPolygonColor(int curve, float z);

    public Color getFirstPolygonColor(float z);

    public Color getSecondPolygonColor(float z);
}