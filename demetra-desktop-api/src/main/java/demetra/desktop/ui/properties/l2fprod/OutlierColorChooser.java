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
package demetra.desktop.ui.properties.l2fprod;

import demetra.html.HtmlClass;
import static ec.util.chart.swing.SwingColorSchemeSupport.toHex;
import java.awt.Color;

/**
 *
 * @author Mats Maggi
 */
public class OutlierColorChooser {

    private static final Color CUSTOMGREEN = new Color(191, 255, 25);
    private static final Color CUSTOMRED = new Color(255, 77, 61);
    private static final Color CUSTOMBLUE = new Color(87, 155, 204);
    private static final Color CUSTOMPINK = new Color(255, 204, 204);
    private static final Color DARK = new Color(50, 50, 50);
    private static final Color LIGHT = new Color(230, 230, 230);

    public static Color getColor(String type) {
        switch (type) {
            case "AO":
                return CUSTOMGREEN;
            case "LS":
                return CUSTOMRED;
            case "TC":
                return CUSTOMBLUE;
            case "SO":
                return CUSTOMPINK;
            case "WO":
                return CUSTOMPINK;
            default:
                return Color.white;
        }
    }

    public static Color getForeColor(String type) {
        switch (type) {
            case "AO":
            case "WO":
                return DARK;
            case "LS":
            case "TC":
                return LIGHT;
            default:
                return Color.black;
        }
    }

    public static String getBgHexColor(String type) {
        return toHex(getColor(type));
    }

    public static HtmlClass getCodeClass(String code) {
        return HtmlClass.of("outlier-" + code.toLowerCase());
    }
}
