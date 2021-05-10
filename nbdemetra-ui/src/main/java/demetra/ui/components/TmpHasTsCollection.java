package demetra.ui.components;

import demetra.ui.components.parts.HasTsCollection;
import ec.nbdemetra.ui.DemetraUI;
import internal.ui.components.HasTsCollectionCommands;
import javax.swing.JMenu;

/**
 *
 */
public class TmpHasTsCollection {

    @Deprecated
    public static JMenu newDefaultMenu(HasTsCollection col, DemetraUI demetraUI) {
        return HasTsCollectionCommands.newDefaultMenu(col, demetraUI);
    }
}
