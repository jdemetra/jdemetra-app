/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tss.Ts;
import ec.tss.html.IHtmlElement;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public interface ITsViewToolkit {

    JComponent getGrid(Ts series);

    JComponent getGrid(Iterable<Ts> series);

    JComponent getChart(Iterable<Ts> series);

    JComponent getGrowthChart(Iterable<Ts> series);

    JComponent getHtmlViewer(IHtmlElement html);
    
    JComponent getMessageViewer(String msg);
}
