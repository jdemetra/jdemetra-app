/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.properties.l2fprod;

import demetra.desktop.descriptors.*;
import demetra.sa.ComponentType;
import demetra.timeseries.regression.ITsVariable;
import demetra.timeseries.regression.Variable;
import demetra.desktop.ui.properties.l2fprod.VariableDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 *
 * @author Jean Palate
 * @param <T>
 */
public abstract class SaVariableDescriptor<T extends ITsVariable> extends VariableDescriptor<T> {

    private ComponentType regressionEffect;

    protected abstract ComponentType regressionEffect();

    protected SaVariableDescriptor() {
        regressionEffect=null;
    }
    
    protected SaVariableDescriptor(Variable<T> var) {
        super(var);
        this.regressionEffect=null;
    }

    protected SaVariableDescriptor(SaVariableDescriptor<T> desc) {
        super(desc);
        this.regressionEffect = desc.regressionEffect;
    }

   public ComponentType getRegressionEffect() {
        return regressionEffect == null ? regressionEffect() : regressionEffect;
    }

    public void setRegressionEffect(ComponentType effect) {
        if ( effect != regressionEffect()) {
            this.regressionEffect=effect;
        }else
            this.regressionEffect=null;
    }


    private static final int REGEFFECT_ID = 5;

    protected EnhancedPropertyDescriptor regDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("regressionEffect", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, REGEFFECT_ID);
            desc.setDisplayName("Regression effect");
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

}
