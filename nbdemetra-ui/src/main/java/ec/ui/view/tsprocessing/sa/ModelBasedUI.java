/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing.sa;

import ec.tstoolkit.modelling.ComponentInformation;
import ec.satoolkit.ISeriesDecomposition;
import ec.tss.html.HtmlFragment;
import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlWienerKolmogorovDiagnostics;
import ec.tstoolkit.modelling.ComponentType;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.ucarima.UcarimaModel;
import ec.tstoolkit.ucarima.WienerKolmogorovDiagnostics;
import ec.ui.view.tsprocessing.HtmlItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;

/**
 *
 * @author pcuser
 */
public class ModelBasedUI< V extends IProcDocumentView<?>> extends HtmlItemUI<V, ModelBasedUI.Information> {

    @Override
    protected IHtmlElement getHtmlElement(V host, Information information) {
        try {
            ISeriesDecomposition decomposition = information.decomposition;
            UcarimaModel ucm = information.ucm;
            if (ucm.getComponentsCount() > 3) {
                ucm = ucm.clone();
                ucm.compact(2, 2);
            }
            String[] desc = new String[]{"Trend", "Seasonally adjusted", "Seasonal", "Irregular"};
            int[] cmps = new int[]{1, -2, 2, 3};
            boolean[] signals = new boolean[]{true, false, true, true};
            double err = information.err;
            TsData t = decomposition.getSeries(ComponentType.Trend, ComponentInformation.Value);
            TsData s = decomposition.getSeries(ComponentType.Seasonal, ComponentInformation.Value);
            TsData i = decomposition.getSeries(ComponentType.Irregular, ComponentInformation.Value);
            TsData sa = decomposition.getSeries(ComponentType.SeasonallyAdjusted, ComponentInformation.Value);

            double[][] data = new double[][]{
                t == null ? null : t.getValues().internalStorage(),
                sa == null ? null : sa.getValues().internalStorage(),
                s == null ? null : s.getValues().internalStorage(),
                i == null ? null : i.getValues().internalStorage()
            };
            WienerKolmogorovDiagnostics diags = WienerKolmogorovDiagnostics.make(ucm, err, data, cmps);
            if (diags != null) {
                return new HtmlWienerKolmogorovDiagnostics(diags, desc, signals, t.getFrequency().intValue());
            }
        } catch (Exception err) {
        }
        return new HtmlFragment("Unable to compute model-based diagnostics");
    }

    public static class Information {

        public ISeriesDecomposition decomposition;
        public UcarimaModel ucm;
        public double err;
    }
}
