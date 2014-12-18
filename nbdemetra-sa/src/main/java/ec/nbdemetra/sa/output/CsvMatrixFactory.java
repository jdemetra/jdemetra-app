/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.output;

import ec.tss.sa.output.CsvMatrixOutputConfiguration;
import ec.tss.sa.output.CsvMatrixOutputFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = INbOutputFactory.class,
position = 1100)
public class CsvMatrixFactory implements INbOutputFactory{
    
    private CsvMatrixOutputConfiguration config=new CsvMatrixOutputConfiguration();

 
    @Override
    public AbstractOutputNode createNode() {
        return new CsvMatrixNode(config);
    }

    @Override
    public String getName() {
        return CsvMatrixOutputFactory.NAME;
    }
    
    @Override
    public AbstractOutputNode createNodeFor(Object properties) {
        if (properties instanceof CsvMatrixOutputConfiguration)
            return new CsvMatrixNode((CsvMatrixOutputConfiguration) properties);
        else
            return null;
    }

}
