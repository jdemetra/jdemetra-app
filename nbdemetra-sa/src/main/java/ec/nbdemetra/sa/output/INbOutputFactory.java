/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.output;

import ec.tstoolkit.design.ServiceDefinition;

/**
 *
 * @author Jean Palate
 */
@ServiceDefinition
public interface INbOutputFactory {

    String getName();

    AbstractOutputNode createNode();

    AbstractOutputNode createNodeFor(Object properties);
}
