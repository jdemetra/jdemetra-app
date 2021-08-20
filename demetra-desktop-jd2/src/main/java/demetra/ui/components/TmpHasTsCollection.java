package demetra.ui.components;

import demetra.ui.DemetraOptions;
import demetra.ui.components.parts.HasTsCollection;
import internal.ui.components.HasTsCollectionCommands;
import javax.swing.JMenu;

/**
 *
 */
public class TmpHasTsCollection {

    @Deprecated
    public static JMenu newDefaultMenu(HasTsCollection col, DemetraOptions demetraUI) {
        return HasTsCollectionCommands.newDefaultMenu(col, demetraUI);
    }
}
