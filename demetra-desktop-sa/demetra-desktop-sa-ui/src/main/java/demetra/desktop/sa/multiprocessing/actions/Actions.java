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
package demetra.desktop.sa.multiprocessing.actions;

import demetra.desktop.sa.multiprocessing.ui.MultiProcessingManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;

@lombok.experimental.UtilityClass
public class Actions {

    @ActionID(category = "Edit",
            id = "demetra.desktop.workspace.nodes.RenameAction")
    @ActionReferences({
        @ActionReference(path = MultiProcessingManager.ITEMPATH, position = 1050)
    })
    public static demetra.desktop.workspace.nodes.RenameAction renameAction() {
        return new demetra.desktop.workspace.nodes.RenameAction();
    }

    @ActionID(category = "Edit",
            id = "demetra.desktop.workspace.nodes.CommentAction")
    @ActionReferences({
        @ActionReference(path = MultiProcessingManager.ITEMPATH, position = 1150)
    })
    public static demetra.desktop.workspace.nodes.CommentAction commentAction() {
        return new demetra.desktop.workspace.nodes.CommentAction();
    }

    @ActionID(category = "Edit",
            id = "demetra.desktop.workspace.nodes.DeleteAction")
    @ActionReferences({
        @ActionReference(path = MultiProcessingManager.ITEMPATH, position = 1100)
    })
    public static demetra.desktop.workspace.nodes.DeleteAction deleteAction() {
        return new demetra.desktop.workspace.nodes.DeleteAction();
    }
    
    @ActionID(category = "Edit",
            id = "demetra.desktop.workspace.nodes.NewAction")
    @ActionReferences({
        @ActionReference(path = MultiProcessingManager.PATH, position = 1000)
    })
    public static demetra.desktop.workspace.nodes.NewAction newAction() {
        return new demetra.desktop.workspace.nodes.NewAction();
    }
    
}
