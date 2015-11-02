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
package ec.util.completion.swing;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.swing.AbstractListModel;

@SuppressWarnings("serial")
public class CustomListModel extends AbstractListModel {

    private String term = "";
    private List<?> data = Collections.emptyList();

    public void setData(@Nonnull String term, @Nonnull List<?> data) {
        this.term = term;
        this.data = data;
        fireContentsChanged(this, 0, data.size());
    }

    @Nonnull
    public String getTerm() {
        return term;
    }

    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public Object getElementAt(int index) {
        return data.get(index);
    }
}
