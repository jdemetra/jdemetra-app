/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.modelling;

import demetra.desktop.interfaces.Disposable;
import demetra.desktop.ui.JMarginView;
import demetra.desktop.ui.processing.DefaultItemUI;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.util.IPool;
import demetra.desktop.util.Pools;
import demetra.timeseries.TsData;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Date;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 * @param <V>
 */
public class EstimationUI<V extends IProcDocumentView<?>> extends DefaultItemUI<V, EstimationUI.Information> {

    private final IPool<JMarginView> pool = Pools.on(JMarginView.class, 10);

    @Override
    public JComponent getView(V host, Information information) {
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

        public Date[] markers;
    }

    private static abstract class JDisposable extends JComponent implements Disposable {

        JDisposable(Component c) {
            setLayout(new BorderLayout());
            add(c, BorderLayout.CENTER);
        }
    }
}
