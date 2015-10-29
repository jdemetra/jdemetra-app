/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.output;

import ec.nbdemetra.ui.properties.ListSelectionEditor;
import ec.tss.sa.output.BasicConfiguration;
import java.util.Arrays;

/**
 *
 * @author Jean Palate
 */
public class Series extends ListSelectionEditor<String> {

    public Series() {
        super(Arrays.asList(BasicConfiguration.allSeries));
    }
}
