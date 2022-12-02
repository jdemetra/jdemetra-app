/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.mru;

import demetra.tsprovider.DataSource;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.Parser;

import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

/**
 * @author Philippe Charles
 */
@lombok.extern.java.Log
enum MruPreferences {

    INSTANCE;

    private static final String DATASOURCE_PROPERTY = "MruDataSource";
    private static final String LABEL_PROPERTY = "MruLabel";

    public void load(Preferences prefs, MruList list) {
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
            log.log(Level.WARNING, "Can't get node list", ex);
        }
    }

    public void store(Preferences prefs, MruList list) {
        // clear the backing store
        clear(prefs);
        Formatter<DataSource> formatter = Formatter.of(DataSource::toString);
        int i = 0;
        for (SourceId o : list) {
            Preferences node = prefs.node(String.valueOf(i++));
            node.put(DATASOURCE_PROPERTY, formatter.formatValueAsString(o.getDataSource()).get());
            node.put(LABEL_PROPERTY, o.getLabel());
        }
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            log.log(Level.WARNING, "Can't flush storage", ex);
        }
    }

    public void clear(Preferences prefs) {
        try {
            prefs.clear();
            for (String i : prefs.childrenNames()) {
                prefs.node(i).removeNode();
            }
        } catch (BackingStoreException ex) {
            log.log(Level.WARNING, "Can't clear storage", ex);
        }
    }
}
