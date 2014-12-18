/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.output;

import ec.tss.sa.output.SpreadsheetOutputConfiguration;
import ec.tss.sa.output.SpreadsheetOutputFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = INbOutputFactory.class, position = 1200)
public class ExcelFactory implements INbOutputFactory{
    
    private SpreadsheetOutputConfiguration config=new SpreadsheetOutputConfiguration();

    @Override
    public AbstractOutputNode createNode() {
        return new ExcelNode(config);
    }

    @Override
    public String getName() {
        return SpreadsheetOutputFactory.NAME;
    }

    @Override
    public AbstractOutputNode createNodeFor(Object properties) {
        if (properties instanceof SpreadsheetOutputConfiguration)
            return new ExcelNode((SpreadsheetOutputConfiguration) properties);
        else
            return null;
    }

}
