/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws;

import ec.tstoolkit.algorithm.AlgorithmDescriptor;
import ec.tstoolkit.algorithm.IProcSpecification;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public interface IStatisticalMethodFactory {
    
    int getPriority();
   
    AlgorithmDescriptor getDescriptor();
    
    List<IProcSpecification> getDefaultSpecifications(); 
}
