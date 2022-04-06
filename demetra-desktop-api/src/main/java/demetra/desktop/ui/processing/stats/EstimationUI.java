/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing.stats;

import demetra.desktop.interfaces.Disposable;
import demetra.desktop.ui.JMarginView;
import demetra.desktop.ui.processing.ItemUI;
import demetra.desktop.util.Pools;
import demetra.timeseries.TsData;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JComponent;
import demetra.desktop.util.Pool;
import java.time.LocalDateTime;

/**
 *
 * @author Jean Palate
 * @param <V>
 */
public class EstimationUI implements ItemUI<EstimationUI.Information> {

    private final Pool<JMarginView> pool = Pools.on(JMarginView.class, 10);

    @Override
    public JComponent getView(Information information) {
        if (information.original == null) {
            return null;
        }
        final JMarginView view = pool.getOrCreate();
        view.setData(TsData.concatenate(information.original, information.fcasts), information.lfcasts, information.ufcasts, information.markers);
        return new JDisposable(view) {
            @Override
            public void dispose() {
                pool.recycle(view);
            }
        };
    }

    public static class Information {

        public Information(TsData o, TsData f, TsData l, TsData u) {
            original = o;
            fcasts = f;
            lfcasts = l;
            ufcasts = u;
        }

        public Information(TsData o, TsData f, TsData ef, double c) {
            original = o;
            fcasts = f;
            TsData e = ef.multiply(c);
            lfcasts = f.fastFn(ef, (a,b)->a-b*c);
            ufcasts = f.fastFn(ef, (a,b)->a+b*c);
        }

        final TsData original, fcasts, lfcasts, ufcasts;

        public LocalDateTime[] markers;
    }

    private static abstract class JDisposable extends JComponent implements Disposable {

        JDisposable(Component c) {
            setLayout(new BorderLayout());
            add(c, BorderLayout.CENTER);
        }
    }
}
