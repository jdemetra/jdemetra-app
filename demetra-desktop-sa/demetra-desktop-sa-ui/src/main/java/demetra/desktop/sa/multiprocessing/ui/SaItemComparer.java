/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.ui;

import demetra.sa.SaItem;
import java.util.Comparator;

/**
 *
 * @author Kristof Bayens
 */
public enum SaItemComparer implements Comparator<SaItem> {

    Name {
                @Override
                public int compare(SaItem x, SaItem y) {
                    String nx = x.getName();
                    String ny = y.getName();
                    return nx.compareTo(ny);
                }
            },
    Method {

                @Override
                public int compare(SaItem x, SaItem y) {
                    String mx = x.getDefinition().getDomainSpec().display();
                    String my = y.getDefinition().getDomainSpec().display();
                    return mx.compareTo(my);
                }
            },
    Quality {

                @Override
                public int compare(SaItem x, SaItem y) {
                    return Integer.compare(x.getEstimation() == null ? -1 : x.getEstimation().getQuality().intValue(), 
                            y.getEstimation() == null ? -1 : y.getEstimation().getQuality().intValue());
                }
            },
    Priority {

                @Override
                public int compare(SaItem x, SaItem y) {
                    return Integer.compare(x.getPriority(), y.getPriority());
                }
            }
}
