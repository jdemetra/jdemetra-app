/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.mru;

import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.utils.IFormatter;
import ec.tss.tsproviders.utils.Parsers;
import java.util.Comparator;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Philippe Charles
 */
enum MruPreferences {

    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(MruPreferences.class);
    private static final String DATASOURCE_PROPERTY = "MruDataSource";
    private static final String LABEL_PROPERTY = "MruLabel";

    public void load(Preferences prefs, MruList list) {
        Parsers.Parser<DataSource> parser = DataSource.xmlParser();
        try {
            Stream.of(prefs.childrenNames())
                    .sorted(Comparator.reverseOrder())
                    .forEach(o -> {
                        Preferences node = prefs.node(o);
                        String tmp = node.get(DATASOURCE_PROPERTY, null);
                        if (tmp == null) {
                            return;
                        }
                        Optional<DataSource> dataSource = parser.parseValue(tmp);
                        if (!dataSource.isPresent()) {
                            return;
                        }
                        String label = node.get(LABEL_PROPERTY, null);
                        if (label == null) {
                            return;
                        }
                        list.add(new SourceId(dataSource.get(), label));
                    });
        } catch (BackingStoreException ex) {
            LOGGER.warn("Can't get node list", ex);
        }
    }

    public void store(Preferences prefs, MruList list) {
        // clear the backing store
        clear(prefs);
        IFormatter<DataSource> formatter = DataSource.xmlFormatter(false);
        int i = 0;
        for (SourceId o : list) {
            Preferences node = prefs.node(String.valueOf(i++));
            node.put(DATASOURCE_PROPERTY, formatter.formatValueAsString(o.getDataSource()).get());
            node.put(LABEL_PROPERTY, o.getLabel());
        }
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            LOGGER.warn("Can't flush storage", ex);
        }
    }

    public void clear(Preferences prefs) {
        try {
            prefs.clear();
            for (String i : prefs.childrenNames()) {
                prefs.node(i).removeNode();
            }
        } catch (BackingStoreException ex) {
            LOGGER.warn("Can't clear storage", ex);
        }
    }
}
