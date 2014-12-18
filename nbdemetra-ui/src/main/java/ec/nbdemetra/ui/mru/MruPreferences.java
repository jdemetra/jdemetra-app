/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.mru;

import com.google.common.base.Optional;
import com.google.common.collect.Ordering;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.utils.Formatters;
import ec.tss.tsproviders.utils.Parsers;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Philippe Charles
 */
enum MruPreferences {

    INSTANCE;
    //
    private static final Logger LOGGER = LoggerFactory.getLogger(MruPreferences.class);
    private static final String DATASOURCE_PROPERTY = "MruDataSource";
    private static final String LABEL_PROPERTY = "MruLabel";

    public void load(Preferences prefs, MruList list) {
        Parsers.Parser<DataSource> parser = DataSource.xmlParser();
        try {
            for (String o : Ordering.natural().immutableSortedCopy(Arrays.asList(prefs.childrenNames())).reverse()) {
                Preferences node = prefs.node(o);
                String tmp = node.get(DATASOURCE_PROPERTY, null);
                if (tmp == null) {
                    continue;
                }
                Optional<DataSource> dataSource = parser.tryParse(tmp);
                if (!dataSource.isPresent()) {
                    continue;
                }
                String label = node.get(LABEL_PROPERTY, null);
                if (label == null) {
                    continue;
                }
                list.add(new SourceId(dataSource.get(), label));
            }
        } catch (BackingStoreException ex) {
            LOGGER.warn("Can't get node list", ex);
        }
    }

    public void store(Preferences prefs, MruList list) {
        // clear the backing store
        clear(prefs);
        Formatters.Formatter<DataSource> formatter = DataSource.xmlFormatter(false);
        int i = 0;
        for (SourceId o : list) {
            Preferences node = prefs.node(String.valueOf(i++));
            node.put(DATASOURCE_PROPERTY, formatter.tryFormatAsString(o.getDataSource()).get());
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
            for (String i : prefs.childrenNames()){
                prefs.node(i).removeNode();
            }
        } catch (BackingStoreException ex) {
            LOGGER.warn("Can't clear storage", ex);
        }
    }
}
