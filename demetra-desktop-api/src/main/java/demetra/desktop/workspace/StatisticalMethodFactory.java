/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace;

import demetra.processing.AlgorithmDescriptor;
import demetra.processing.ProcSpecification;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public interface StatisticalMethodFactory {
    
    int getPriority();
   
    AlgorithmDescriptor getDescriptor();
    
    List<ProcSpecification> getDefaultSpecifications(); 
}
