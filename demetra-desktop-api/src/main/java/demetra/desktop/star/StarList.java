package demetra.desktop.star;

import demetra.desktop.design.GlobalService;
import demetra.tsprovider.DataSource;
import demetra.ui.util.LazyGlobalService;
import ec.util.various.swing.OnEDT;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@GlobalService
public final class StarList implements Iterable<DataSource> {

    @NonNull
    public static StarList getDefault() {
        return LazyGlobalService.get(StarList.class, StarList::new);
    }

    private final Set<DataSource> list;

    private StarList() {
        list = new HashSet<>();
    }

    @OnEDT
    public void clear() {
        list.clear();
    }

    @OnEDT
    public void toggle(DataSource item) {
        if (list.contains(item))
            list.remove(item);
        else
            list.add(item);
    }

    @OnEDT
    @Override
    public Iterator<DataSource> iterator() {
        return list.iterator();
    }

    @OnEDT
    public boolean isStarred(DataSource dataSource) {
        return list.contains(dataSource);
    }
}
