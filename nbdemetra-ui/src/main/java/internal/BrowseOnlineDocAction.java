package internal;

import ec.nbdemetra.ui.notification.NotifyUtil;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

@ActionID(category = "Help", id = BrowseOnlineDocAction.ID)
@ActionRegistration(displayName = "#BrowseOnlineDocAction", lazy = false)
@ActionReference(path = "Menu/Help", position = 200)
@NbBundle.Messages("BrowseOnlineDocAction=Online Docs")
public final class BrowseOnlineDocAction extends NodeAction {

//    @ClassNameConstant
    public static final String ID = "internal.BrowseOnlineDocAction";

    @Override
    public String getName() {
        return Bundle.BrowseOnlineDocAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    protected void performAction(Node[] nodes) {
        try {
            Desktop.getDesktop().browse(URI.create("https://jdemetra-new-documentation.netlify.app/"));
        } catch (IOException ex) {
            NotifyUtil.error("Desktop", "Cannot browse uri", ex);
        }
    }

    @Override
    protected boolean enable(Node[] nodes) {
        return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
    }
}
