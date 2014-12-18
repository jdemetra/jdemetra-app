/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.star;

import com.google.common.base.Optional;
import ec.nbdemetra.core.InstallerStep;
import static ec.nbdemetra.core.InstallerStep.tryGet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceLoader;
import ec.tss.tsproviders.TsProviders;
import ec.tss.tsproviders.utils.Formatters;
import ec.tss.tsproviders.utils.Parsers;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Philippe Charles
 */
public class StarHelper extends InstallerStep {

    static final Logger LOGGER = LoggerFactory.getLogger(StarHelper.class);
    static final String DATASOURCE_PROPERTY = "StarDataSource";
    final Preferences prefs = NbPreferences.forModule(StarHelper.class).node("Star");

    @Override
    public void restore() {
        StarList.getInstance().clear();

        Parsers.Parser<DataSource> parser = DataSource.xmlParser();

        try {
            for (String i : prefs.childrenNames()) {
                Optional<DataSource> dataSource = tryGet(prefs.node(i), DATASOURCE_PROPERTY, parser);
                if (dataSource.isPresent()) {
                    StarList.getInstance().toggle(dataSource.get());
                }
            }
        } catch (BackingStoreException ex) {
            LOGGER.warn("Can't get node list", ex);
        }

        for (DataSource o : StarList.getInstance()) {
            Optional<IDataSourceLoader> loader = TsProviders.lookup(IDataSourceLoader.class, o);
            if (loader.isPresent()) {
                loader.get().open(o);
            }
        }
    }

    @Override
    public void close() {
        // clear the backing store
        try {
            for (String i : prefs.childrenNames()) {
                prefs.node(i).removeNode();
            }
        } catch (BackingStoreException ex) {
            LOGGER.warn("Can't clear storage", ex);
        }

        Formatters.Formatter<DataSource> formatter = DataSource.xmlFormatter(false);

        int i = 0;
        for (DataSource o : StarList.getInstance()) {
            Preferences node = prefs.node(String.valueOf(i++));
            tryPut(node, DATASOURCE_PROPERTY, formatter, o);
        }
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            LOGGER.warn("Can't flush storage", ex);
        }
    }
}
