/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import com.google.common.base.Objects;
import ec.tstoolkit.algorithm.IProcDocument;
import ec.tstoolkit.utilities.InformationExtractor;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Jean Palate
 */
public abstract class DocumentInformationExtractor<D extends IProcDocument<?,?, ?>, I> implements InformationExtractor<D, I> {

    private static final AtomicInteger gid_ = new AtomicInteger(0);
    private final int id_;

    protected DocumentInformationExtractor() {
        id_ = gid_.incrementAndGet();
    }

    @Override
    public I retrieve(D source) {
        long doc = source.getKey();
        Key key = new Key(doc, id_);
        I info = null;
        synchronized (cache_) {
            info = (I) cache_.get(key);
            if (info == null) {
                info = buildInfo(source);
                cache_.put(key, info);
            }
        }
        return info;
    }

    @Override
    public void flush(D source) {
        long doc = source.getKey();
        synchronized (cache_) {
            cache_.remove(new Key(doc, id_));
        }
    }

    protected abstract I buildInfo(D source);
    private static final HashMap<Key, Object> cache_ = new HashMap<>();

    private static class Key implements Comparable<Key> {

        Key(long doc, int id) {
            this.doc = doc;
            this.id = id;
        }
        long doc;
        int id;

        @Override
        public int compareTo(Key o) {
            if (id == o.id) {
                if (doc == o.doc) {
                    return 0;
                }
                else if (doc < o.doc) {
                    return -1;
                }
                else {
                    return 1;
                }
            }
            else if (id < o.id) {
                return -1;
            }
            else {
                return 1;
            }
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || (obj instanceof Key && equals((Key) obj));
        }

        private boolean equals(Key other) {
            return other.id == id && other.doc == doc;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.doc, this.id);
        }
    }
}