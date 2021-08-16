package internal.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

public enum NetBeansLookup implements Function<Class, Iterable> {

    INSTANCE;

    @Override
    public Iterable apply(Class type) {
        return new NetBeansLookupResult(type);
    }

    private static final class NetBeansLookupResult implements Iterable, LookupListener {

        private final Lookup.Result result;
        private final AtomicReference<Collection> instances;

        private NetBeansLookupResult(Class type) {
            this.result = Lookup.getDefault().lookupResult(type);
            this.instances = new AtomicReference<>(result.allInstances());
        }

        @Override
        public Iterator iterator() {
            return instances.get().iterator();
        }

        @Override
        public void resultChanged(LookupEvent event) {
            if (event.getSource().equals(result)) {
                instances.set(result.allInstances());
            }
        }
    }
}
