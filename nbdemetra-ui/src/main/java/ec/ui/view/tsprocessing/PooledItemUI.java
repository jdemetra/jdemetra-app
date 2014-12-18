/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import com.google.common.base.Throwables;
import ec.tstoolkit.utilities.IPool;
import ec.tstoolkit.utilities.Pools;
import ec.ui.Disposables;
import javax.swing.JComponent;

/**
 *
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

        return new TsViewToolkit.JDisposable(result) {
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
}
