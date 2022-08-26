/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.stl.ui;

import demetra.desktop.ui.properties.l2fprod.ArrayRenderer;
import demetra.desktop.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import demetra.desktop.ui.properties.l2fprod.CustomPropertyRendererFactory;
import demetra.stl.SeasonalSpec;
import demetra.stl.StlSpec;



/**
 *
 * @author Jean Palate
 */
@lombok.Getter
@lombok.AllArgsConstructor
public class StlPlusSpecRoot  {
    
    static{
        CustomPropertyEditorRegistry.INSTANCE.register(SeasonalSpec[].class, new SeasonalSpecsEditor());
        CustomPropertyRendererFactory.INSTANCE.getRegistry().registerRenderer(SeasonalSpec[].class, new ArrayRenderer());
    }

    StlSpec core;
    boolean ro;
}
