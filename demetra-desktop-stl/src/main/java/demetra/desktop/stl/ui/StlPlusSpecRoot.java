/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.stl.ui;

import demetra.desktop.ui.properties.l2fprod.ArrayRenderer;
import demetra.desktop.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import demetra.desktop.ui.properties.l2fprod.CustomPropertyRendererFactory;
import demetra.stl.SeasonalSpecification;
import demetra.stl.StlSpecification;



/**
 *
 * @author Jean Palate
 */
@lombok.Getter
@lombok.AllArgsConstructor
public class StlPlusSpecRoot  {
    
    static{
        CustomPropertyEditorRegistry.INSTANCE.register(SeasonalSpecification[].class, new SeasonalSpecsEditor());
        CustomPropertyRendererFactory.INSTANCE.getRegistry().registerRenderer(SeasonalSpecification[].class, new ArrayRenderer());
    }
    
    StlSpecification core;
    boolean ro;
}
