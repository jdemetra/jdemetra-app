/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties;

import java.beans.IntrospectionException;

/**
 *
 * @author Philippe Charles
 */
public interface IBeanEditor {
    
    boolean editBean(Object bean) throws IntrospectionException;
    
}
