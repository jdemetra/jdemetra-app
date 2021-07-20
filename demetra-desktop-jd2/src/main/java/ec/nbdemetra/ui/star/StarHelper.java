/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.star;

import demetra.bridge.TsConverter;
import demetra.tsprovider.DataSource;
import demetra.tsprovider.DataSourceLoader;
import demetra.ui.TsManager;
import ec.nbdemetra.core.InstallerStep;
import static ec.nbdemetra.core.InstallerStep.tryGet;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.Parser;
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

        Parser<DataSource> parser = ec.tss.tsproviders.DataSource.xmlParser().andThen(TsConverter::toDataSource)::parse;

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
            TsManager.getDefault()
                    .getProvider(DataSourceLoader.class, o)
                    .ifPresent(x -> x.open(o));
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

        Formatter<DataSource> formatter = ec.tss.tsproviders.DataSource.xmlFormatter(false).compose(TsConverter::fromDataSource)::format;

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
