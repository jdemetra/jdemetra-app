/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.interfaces;

import ec.util.chart.ColorScheme;


/**
 *
 * @author Philippe Charles
 */
public interface IColorSchemeAble {

    public static final String COLOR_SCHEME_PROPERTY = "colorScheme";

    void setColorScheme(ColorScheme colorScheme);

    ColorScheme getColorScheme();
}
