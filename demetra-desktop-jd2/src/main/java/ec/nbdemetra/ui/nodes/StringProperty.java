/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.nodes;

import ec.tstoolkit.MetaData;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.Node;

/**
 *
 * @author Thomas Witthohn
 */
public class StringProperty extends Node.Property<String> {

    private final String key;
    private final MetaData md;

    public StringProperty(String key, MetaData md) {
        super(String.class);
        this.key = key;
        this.md = md;
        setName(key);

    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public String getValue() throws IllegalAccessException, InvocationTargetException {
        return md.get(key);
    }

    @Override
    public boolean canWrite() {
        return true;
    }

    @Override
    public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        md.put(key, val);
    }
}
