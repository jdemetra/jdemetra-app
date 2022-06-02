package demetra.desktop.extra.sdmx.web;

import demetra.desktop.TsManager;
import demetra.desktop.actions.AbilityNodeAction;
import demetra.desktop.actions.Actions;
import static demetra.desktop.tsproviders.TsProviderNodes.SOURCE_ACTION_PATH;
import demetra.tsp.extra.sdmx.web.SdmxWebProvider;
import demetra.tsprovider.DataSource;
import ec.util.desktop.Desktop;
import ec.util.desktop.DesktopManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.stream.Stream;
import javax.swing.JMenuItem;
import org.openide.awt.ActionReference;
import org.openide.util.actions.Presenter;
import sdmxdl.web.SdmxWebSource;

@ActionID(category = "Edit", id = OpenWebsiteAction.ID)
@ActionRegistration(displayName = "#CTL_OpenWebsiteAction", lazy = false)
@Messages("CTL_OpenWebsiteAction=Open web site")
@ActionReference(path = SOURCE_ACTION_PATH, position = 720, separatorBefore = 700, id = @ActionID(category = "Edit", id = OpenWebsiteAction.ID))
public final class OpenWebsiteAction extends AbilityNodeAction<DataSource> implements Presenter.Popup {

    public static final String ID = "demetra.desktop.extra.sdmx.web.OpenWebsiteAction";

    public OpenWebsiteAction() {
        super(DataSource.class, true);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return Actions.hideWhenDisabled(new JMenuItem(this));
    }

    @Override
    protected void performAction(Stream<DataSource> items) {
        items.forEach(item -> {
            try {
                DesktopManager.get().browse(getWebsite(item).toURI());
            } catch (IOException | URISyntaxException ex) {
                Exceptions.printStackTrace(ex);
            }
        });
    }

    @Override
    protected boolean enable(Stream<DataSource> items) {
        return DesktopManager.get().isSupported(Desktop.Action.BROWSE)
                && items.anyMatch(item -> getWebsite(item) != null);
    }

    private URL getWebsite(DataSource dataSource) {
        return TsManager.get()
                .getProvider(SdmxWebProvider.class, dataSource)
                .map(provider -> getWebsite(provider, dataSource))
                .orElse(null);
    }

    private URL getWebsite(SdmxWebProvider provider, DataSource dataSource) {
        SdmxWebSource source = provider.getSdmxManager().getSources().get(provider.decodeBean(dataSource).getSource());
        return source != null ? source.getWebsite() : null;
    }

    @Override
    public String getName() {
        return Bundle.CTL_OpenWebsiteAction();
    }
}
