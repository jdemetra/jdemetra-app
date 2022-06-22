/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.star;

import demetra.desktop.util.InstallerStep;
import demetra.timeseries.TsFactory;
import demetra.timeseries.TsProvider;
import demetra.tsprovider.DataSource;
import demetra.tsprovider.DataSourceLoader;
import java.util.Comparator;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Stream;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.Parser;
import org.openide.util.NbPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Philippe Charles
 */
public class StarStep extends InstallerStep {

    static final Logger LOGGER = LoggerFactory.getLogger(StarStep.class);
    static final String DATASOURCE_PROPERTY = "StarDataSource";
    final Preferences prefs = NbPreferences.forModule(StarStep.class).node("Star");

    @Override
    public void restore() {
        StarListManager.get().clear();
        Parser<DataSource> parser = Parser.of(DataSource::parse);
        try {
            Stream.of(prefs.childrenNames())
                    .sorted(Comparator.reverseOrder())
                    .forEach(o -> {
                        Preferences node = prefs.node(o);
                        String tmp = node.get(DATASOURCE_PROPERTY, null);
                        if (tmp == null) {
                            return;
                        }
                        java.util.Optional<DataSource> dataSource = parser.parseValue(tmp);
                        if (dataSource.isPresent()) {
                            StarListManager.get().toggle(dataSource.get());
                        }
                    });
        } catch (BackingStoreException ex) {
            LOGGER.warn("Can't get star items", ex);
        }

        for (DataSource o : StarListManager.get()) {
            java.util.Optional<TsProvider> provider = TsFactory.getDefault().getProvider(o.getProviderName());
            if (provider.isPresent()) {
                DataSourceLoader loader = (DataSourceLoader) provider.get();
                loader.open(o);
            }
        }
    }

    @Override
    public void close() {
        // clear the backing store
        Formatter<DataSource> formatter = Formatter.of(DataSource::toString);
        int i = 0;
        for (DataSource o : StarListManager.get()) {
            Preferences node = prefs.node(String.valueOf(i++));
            node.put(DATASOURCE_PROPERTY, formatter.formatValueAsString(o).get());
        }
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            LOGGER.warn("Can't flush storage", ex);
        }
    }
}
