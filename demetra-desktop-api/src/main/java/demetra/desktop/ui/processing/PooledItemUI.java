/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing;

import com.google.common.base.Throwables;
import demetra.desktop.interfaces.Disposable;
import demetra.desktop.ui.Disposables;
import demetra.desktop.util.IPool;
import demetra.desktop.util.Pools;

import javax.swing.*;
import java.awt.*;

/**
 * @author Philippe Charles
 */
public abstract class PooledItemUI<H, I, C extends JComponent> extends DefaultItemUI<H, I> implements IPool.Factory<C> {

    final Class<? extends C> clazz;
    final IPool<C> pool;

    public PooledItemUI(Class<? extends C> clazz) {
        this.clazz = clazz;
        this.pool = Pools.on(this, 10);
    }

    @Override
    public JComponent getView(H host, I information) {
        final C result = pool.getOrCreate();
        init(result, host, information);

        return new JDisposable(result) {
            @Override
            public void dispose() {
                pool.recycle(result);
            }
        };
    }

    @Override
    public C create() {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw Throwables.propagate(ex);
        }
    }

    @Override
    public void reset(C o) {
        // do nothing
    }

    @Override
    public void destroy(C o) {
        Disposables.dispose(o);
    }

    abstract protected void init(C c, H host, I information);

    private static abstract class JDisposable extends JComponent implements Disposable {

        JDisposable(Component c) {
            setLayout(new BorderLayout());
            add(c, BorderLayout.CENTER);
        }
    }
}
