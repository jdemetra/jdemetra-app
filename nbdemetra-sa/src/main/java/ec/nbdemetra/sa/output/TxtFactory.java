/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.output;

import ec.tss.sa.output.TxtOutputConfiguration;
import ec.tss.sa.output.TxtOutputFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = INbOutputFactory.class,
position = 1500)
public class TxtFactory implements INbOutputFactory{
    
    private TxtOutputConfiguration config=new TxtOutputConfiguration();

    @Override
    public AbstractOutputNode createNode() {
        return new TxtNode(config);
    }

    @Override
    public String getName() {
        return TxtOutputFactory.NAME;
    }

    @Override
    public AbstractOutputNode createNodeFor(Object properties) {
        if (properties instanceof TxtOutputConfiguration)
            return new TxtNode((TxtOutputConfiguration) properties);
        else
            return null;
    }

}
