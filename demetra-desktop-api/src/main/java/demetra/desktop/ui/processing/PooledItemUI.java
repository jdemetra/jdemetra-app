/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing;

import demetra.desktop.interfaces.Disposable;
import demetra.desktop.ui.Disposables;
import demetra.desktop.util.Pools;

import javax.swing.*;
import java.awt.*;
import demetra.desktop.util.Pool;

/**
 * @author Philippe Charles
 * @param <D>
 * @param <C>
 */
public abstract class PooledItemUI<D, C extends JComponent> implements ItemUI<D>, Pool.Factory<C> {

    final Class<? extends C> clazz;
    final Pool<C> pool;

    public PooledItemUI(Class<? extends C> clazz) {
        this.clazz = clazz;
        this.pool = Pools.on(this, 10);
    }

    @Override
    public JComponent getView(D document) {
        final C result = pool.getOrCreate();
        init(result, document);

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
            throw new RuntimeException(ex);
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

    abstract protected void init(C c, D document);

    private static abstract class JDisposable extends JComponent implements Disposable {

        JDisposable(Component c) {
            setLayout(new BorderLayout());
            add(c, BorderLayout.CENTER);
        }
    }
}
