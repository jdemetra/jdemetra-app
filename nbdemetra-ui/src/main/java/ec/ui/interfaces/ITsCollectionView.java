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
package ec.ui.interfaces;

import ec.tss.Ts;
import ec.tss.TsCollection;

/**
 *
 * @author Demortier Jeremy
 */
public interface ITsCollectionView extends ITsControl, ITsCollectionAble, ITsActionAble {

    public static final String SELECTION_PROPERTY = "selection";
    public static final String UDPATE_MODE_PROPERTY = "tsUpdateMode";
    public static final String FREEZE_ON_IMPORT_PROPERTY = "freezeOnImport";

    Ts[] getSelection();

    void setSelection(Ts[] tss);

    int getSelectionSize();

    TsUpdateMode getTsUpdateMode();

    void setTsUpdateMode(TsUpdateMode updateMode);

    public enum TsUpdateMode {

        None {
            @Override
            public void update(TsCollection main, TsCollection col) {
                // do nothing
            }
        }, Single {
            @Override
            public void update(TsCollection main, TsCollection col) {
                main.replace(col.get(0));
            }
        }, Replace {
            @Override
            public void update(TsCollection main, TsCollection col) {
                main.replace(col);
            }
        }, Append {
            @Override
            public void update(TsCollection main, TsCollection col) {
                main.append(col);
            }
        };

        public boolean isReadOnly() {
            return this == None;
        }

        abstract public void update(TsCollection main, TsCollection col);
    }

    default boolean isFreezeOnImport() {
        return false;
    }

    default void setFreezeOnImport(boolean freezeOnImport) {
    }
}
