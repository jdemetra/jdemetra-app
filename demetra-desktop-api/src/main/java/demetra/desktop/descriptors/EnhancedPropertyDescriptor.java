/*
* Copyright 2013 National Bank of Belgium
*
* Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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

package demetra.desktop.descriptors;

import java.beans.PropertyDescriptor;

/**
 *
 * @author Jean Palate
 */
public class EnhancedPropertyDescriptor {

    public static enum Refresh {

        None, All, Children
    }
    private Refresh refresh_ = Refresh.None;
    private boolean readonly_;
    private final int pos_;
    private final PropertyDescriptor descriptor_;
    private String category_;

    public Refresh getRefreshMode() {
        return refresh_;
    }

    public void setRefreshMode(Refresh mode) {
        refresh_ = mode;
    }

    public boolean isReadOnly() {
        return readonly_;
    }

    public void setReadOnly(boolean ro) {
        readonly_ = ro;
    }

    public void setCategory(String category) {
        category_ = category;
    }

    public String getCategory() {
        return category_;
    }

    public PropertyDescriptor getDescriptor() {
        return descriptor_;
    }

    public EnhancedPropertyDescriptor(PropertyDescriptor descriptor, int pos) {
        descriptor_ = descriptor;
        pos_ = pos;
    }

    public int getPosition() {
        return pos_;
    }
}
