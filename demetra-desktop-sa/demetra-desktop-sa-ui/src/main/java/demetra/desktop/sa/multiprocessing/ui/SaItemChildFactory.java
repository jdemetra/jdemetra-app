/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;

/**
 *
 * @author Thomas Witthohn
 */
public class SaItemChildFactory extends ChildFactory.Detachable<String> {

    private ChangeListener listener;

    @Override
    protected void addNotify() {
        SaItemNotifier.addChangeListener(listener = ev -> refresh(true));
    }

    @Override
    protected void removeNotify() {
        if (listener != null) {
            SaItemNotifier.removeChangeListener(listener);
            listener = null;
        }
    }

    @Override
    protected boolean createKeys(List<String> toPopulate) {
        List<String> keys = new ArrayList<>();
        for (Object prop : System.getProperties().keySet()) {
            keys.add((String) prop);
        }
        Collections.sort(keys);
        toPopulate.addAll(keys);
        return true;
    }

}
