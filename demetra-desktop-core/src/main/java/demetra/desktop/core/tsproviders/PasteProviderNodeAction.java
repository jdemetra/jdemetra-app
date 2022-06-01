/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.core.tsproviders;

import demetra.desktop.actions.AbilityNodeAction;
import demetra.desktop.datatransfer.DataSourceTransferManager;
import demetra.desktop.datatransfer.DataTransfers;
import demetra.tsprovider.DataSourceLoader;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

import java.util.stream.Stream;

@ActionID(category = "Edit", id = PasteProviderNodeAction.ID)
@ActionRegistration(displayName = "#CTL_PasteProviderAction", lazy = false)
@Messages("CTL_PasteProviderAction=Paste")
public final class PasteProviderNodeAction extends AbilityNodeAction<DataSourceLoader> {

    public static final String ID = "demetra.desktop.core.tsproviders.PasteProviderAction";

    public PasteProviderNodeAction() {
        super(DataSourceLoader.class, true);
    }

    @Override
    protected void performAction(Stream<DataSourceLoader> items) {
        items.forEach(item -> {
            DataSourceTransferManager.get()
                    .getDataSource(DataTransfers.systemClipboardAsTransferable(), item.getSource())
                    .ifPresent(item::open);
        });
    }

    @Override
    protected boolean enable(Stream<DataSourceLoader> items) {
        return items.anyMatch(item -> DataSourceTransferManager.get().canHandle(DataTransfers.systemClipboardAsTransferable(), item.getSource()));
    }

    @Override
    public String getName() {
        return Bundle.CTL_PasteProviderAction();
    }
}
