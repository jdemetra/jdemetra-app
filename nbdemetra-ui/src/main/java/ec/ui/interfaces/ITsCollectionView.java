package ec.ui.interfaces;

import ec.nbdemetra.ui.tsaction.ITsAction;
import ec.tss.Ts;
import ec.tss.TsCollection;

public interface ITsCollectionView extends ITsControl {

    public static final String COLLECTION_PROPERTY = "tsCollection";
    public static final String SELECTION_PROPERTY = "selection";
    public static final String UDPATE_MODE_PROPERTY = "tsUpdateMode";
    public static final String TS_ACTION_PROPERTY = "tsAction";

    TsCollection getTsCollection();

    void setTsCollection(TsCollection collection);

    Ts[] getSelection();

    void setSelection(Ts[] tss);

    int getSelectionSize();
    
    TsUpdateMode getTsUpdateMode();

    void setTsUpdateMode(TsUpdateMode updateMode);

    void setTsAction(ITsAction tsAction);

    ITsAction getTsAction();

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
}
