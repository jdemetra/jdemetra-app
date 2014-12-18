package ec.ui.interfaces;

import ec.tss.Ts;

/**
 *
 * @author Demortier Jeremy
 */
public interface ITsView extends ITsControl {

    public static final String TS_PROPERTY = "ts";
    
    Ts getTs();

    void setTs(Ts ts);
}
