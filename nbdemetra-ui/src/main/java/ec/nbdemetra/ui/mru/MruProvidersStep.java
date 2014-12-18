/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.mru;

import ec.nbdemetra.core.InstallerStep;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.tss.tsproviders.TsProviders;
import ec.tss.tsproviders.utils.DataSourceAdapter;
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
        for (IDataSourceProvider o : TsProviders.all().filter(IDataSourceProvider.class)) {
            o.addDataSourceListener(listener);
        }
    }

    @Override
    protected void onRestore(Lookup.Result<IDataSourceProvider> lookup) {
        MruPreferences.INSTANCE.load(prefs, MruList.getProvidersInstance());
        for (IDataSourceProvider o : TsProviders.all().filter(IDataSourceProvider.class)) {
            o.addDataSourceListener(listener);
        }
    }

    @Override
    protected void onClose(Lookup.Result<IDataSourceProvider> lookup) {
        for (IDataSourceProvider o : TsProviders.all().filter(IDataSourceProvider.class)) {
            o.removeDataSourceListener(listener);
        }
        MruPreferences.INSTANCE.store(prefs, MruList.getProvidersInstance());
    }

    static class Listener extends DataSourceAdapter {

        @Override
        public void opened(DataSource dataSource) {
            IDataSourceProvider provider = TsProviders.lookup(IDataSourceProvider.class, dataSource).get();
            MruList.getProvidersInstance().add(new SourceId(dataSource, provider.getDisplayName(dataSource)));
        }
    }
}
