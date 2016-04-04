/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.IPool;
import ec.tstoolkit.utilities.Pools;
import ec.ui.interfaces.IDisposable;
import ec.ui.view.MarginView;
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

    private final IPool<MarginView> pool = Pools.on(MarginView.class, 10);

    @Override
    public JComponent getView(V host, Information information) {
        if (information.original == null) {
            return null;
        }
        final MarginView view = pool.getOrCreate();
        view.setData(information.original.update(information.fcasts), information.lfcasts, information.ufcasts, information.markers);
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
            TsData e = ef.times(c);
            lfcasts = TsData.subtract(f, e);
            ufcasts = TsData.add(f, e);
        }

        final TsData original, fcasts, lfcasts, ufcasts;

        public Date[] markers;
    }

    private static abstract class JDisposable extends JComponent implements IDisposable {

        JDisposable(Component c) {
            setLayout(new BorderLayout());
            add(c, BorderLayout.CENTER);
        }
    }
}
