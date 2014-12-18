/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.output;

import ec.tss.sa.output.CsvOutputConfiguration;
import ec.tss.sa.output.CsvOutputFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = INbOutputFactory.class,
position = 1000)
public class CsvFactory implements INbOutputFactory{
    
    private CsvOutputConfiguration config=new CsvOutputConfiguration();

    @Override
    public AbstractOutputNode createNode() {
        return new CsvNode(config);
    }

    @Override
    public String getName() {
        return CsvOutputFactory.NAME;
    }

    @Override
    public AbstractOutputNode createNodeFor(Object properties) {
        if (properties instanceof CsvOutputConfiguration)
            return new CsvNode((CsvOutputConfiguration) properties);
        else
            return null;
    }
}
