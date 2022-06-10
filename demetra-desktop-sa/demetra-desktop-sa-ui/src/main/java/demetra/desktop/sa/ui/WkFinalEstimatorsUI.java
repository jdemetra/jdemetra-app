/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.ui;

import demetra.desktop.ui.processing.ItemUI;
import demetra.desktop.ui.processing.TsViewToolkit;
import demetra.html.HtmlFragment;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class WkFinalEstimatorsUI implements ItemUI<WkInformation> {

    @Override
    public JComponent getView(WkInformation information) {
        try {
            return new FinalEstimatorsView(information.getEstimators(), information.getDescriptors(), information.getFrequency());
        } catch (Exception err) {
            return TsViewToolkit.getHtmlViewer(new HtmlFragment("Unable to compute the final estimators"));
        }
    }

}
