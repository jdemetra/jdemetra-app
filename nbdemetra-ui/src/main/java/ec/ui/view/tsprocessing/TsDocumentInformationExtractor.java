/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tss.documents.TsDocument;
import ec.tstoolkit.utilities.InformationExtractor;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Jean Palate
 * @param <D>
 * @param <I>
 */
public abstract class TsDocumentInformationExtractor<D extends TsDocument, I> implements InformationExtractor<D, I> {

    private static final AtomicInteger gid_ = new AtomicInteger(0);
    
    private final int id_;
    
    protected TsDocumentInformationExtractor(){
        id_ = gid_.incrementAndGet();
    }
    
    @Override
    public I retrieve(D source) {
        String doc = source.getDocumentId();
        Key key=new Key(doc, id_);
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
        String doc = source.getDocumentId();
        synchronized (cache_) {
            cache_.remove(new Key(doc, id_));
        }
    }

    protected abstract I buildInfo(D source);
    private static final HashMap<Key, Object> cache_ = new HashMap<>();
    
    private static class Key implements Comparable<Key>{
        
        Key(String doc, int id){
            this.doc=doc;
            this.id=id;
        }
        
        String doc;
        int id; 

        @Override
        public int compareTo(Key o) {
            if (id == o.id)
                return doc.compareTo(o.doc);
            else if (id < o.id)
                return -1;
            else
                return 1;            
        }
        
        @Override
        public boolean equals(Object obj) {
            return this == obj || (obj instanceof Key && equals((Key) obj));
        }
        
        private boolean equals(Key other) {
           return other.id == id && other.doc.equals(doc);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 89 * hash + Objects.hashCode(this.doc);
            hash = 89 * hash + this.id;
            return hash;
        }
        
    }
    
}