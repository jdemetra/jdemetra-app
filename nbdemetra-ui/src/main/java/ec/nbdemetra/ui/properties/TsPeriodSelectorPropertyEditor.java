/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.nbdemetra.ui.properties;

import ec.tstoolkit.timeseries.TsPeriodSelector;
import java.awt.Component;
import java.beans.PropertyEditorSupport;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.PropertyEditorRegistration;

/**
 *
 * @author Philippe Charles
 */
@PropertyEditorRegistration(targetType = TsPeriodSelector.class)
public final class TsPeriodSelectorPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor {

    private PropertyEnv env = null;

    @Override
    public void attachEnv(PropertyEnv env) {
        this.env = env;
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        TsPeriodSelectorComponent result = new TsPeriodSelectorComponent();
        result.setTsPeriodSelector((TsPeriodSelector) getValue());
        applyMagicGlue(result);
        return result;
    }

    @Override
    public String getAsText() {
        TsPeriodSelector value = (TsPeriodSelector) getValue();
        return value != null ? value.toString() : "null";
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        // do nothing!
    }

    private void applyMagicGlue(final TsPeriodSelectorComponent c) {
        env.setState(PropertyEnv.STATE_NEEDS_VALIDATION);
        env.addPropertyChangeListener(evt -> {
            if (PropertyEnv.PROP_STATE.equals(evt.getPropertyName()) && evt.getNewValue() == PropertyEnv.STATE_VALID) {
                setValue(c.getTsPeriodSelector());
            }
        });
    }
}
