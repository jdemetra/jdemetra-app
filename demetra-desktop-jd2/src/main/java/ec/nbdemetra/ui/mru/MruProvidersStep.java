/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.mru;

import demetra.tsprovider.DataSourceListener;
import demetra.tsprovider.DataSourceLoader;
import demetra.tsprovider.DataSourceProvider;
import demetra.desktop.TsManager;
import ec.nbdemetra.core.InstallerStep;
import ec.tss.tsproviders.IDataSourceProvider;
import java.util.prefs.Preferences;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

/**
 *
 * @author Philippe Charles
 */
public class MruProvidersStep extends InstallerStep.LookupStep<IDataSourceProvider> {

    final Preferences prefs = NbPreferences.forModule(MruWorkspacesStep.class).node("Mru");
    final Listener listener;

    public MruProvidersStep() {
        super(IDataSourceProvider.class);
        this.listener = new Listener();
    }

    @Override
    protected void onResultChanged(Lookup.Result<IDataSourceProvider> lookup) {
        // won't be added twice
        TsManager.getDefault().getProviders()
                .filter(DataSourceLoader.class::isInstance)
                .map(DataSourceLoader.class::cast)
                .forEach(o -> o.addDataSourceListener(listener));
    }

    @Override
    protected void onRestore(Lookup.Result<IDataSourceProvider> lookup) {
        MruPreferences.INSTANCE.load(prefs, MruList.getProvidersInstance());
        TsManager.getDefault().getProviders()
                .filter(DataSourceLoader.class::isInstance)
                .map(DataSourceLoader.class::cast)
                .forEach(o -> o.addDataSourceListener(listener));
    }

    @Override
    protected void onClose(Lookup.Result<IDataSourceProvider> lookup) {
        TsManager.getDefault().getProviders()
                .filter(DataSourceLoader.class::isInstance)
                .map(DataSourceLoader.class::cast)
                .forEach(o -> o.removeDataSourceListener(listener));
        MruPreferences.INSTANCE.store(prefs, MruList.getProvidersInstance());
    }

    static class Listener implements DataSourceListener {

        @Override
        public void opened(demetra.tsprovider.DataSource dataSource) {
            TsManager.getDefault()
                    .getProvider(DataSourceProvider.class, dataSource)
                    .ifPresent(provider -> MruList.getProvidersInstance().add(new SourceId(dataSource, provider.getDisplayName(dataSource))));
        }

        @Override
        public void closed(demetra.tsprovider.DataSource ds) {
        }

        @Override
        public void changed(demetra.tsprovider.DataSource ds) {
        }

        @Override
        public void allClosed(String string) {
        }
    }
}
