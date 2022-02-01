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
public enum SaNodeComparer implements Comparator<SaNode> {

    Name {
                @Override
                public int compare(SaNode x, SaNode y) {
                    String nx = x.getName();
                    String ny = y.getName();
                    return nx.compareTo(ny);
                }
            },
    Method {

                @Override
                public int compare(SaNode x, SaNode y) {
                    String mx = x.getSpec().display();
                    String my = y.getSpec().display();
                    return mx.compareTo(my);
                }
            },
    Quality {

                @Override
                public int compare(SaNode x, SaNode y) {
                    return Integer.compare(x.results() == null ? -1 : x.results().getQuality().intValue(), 
                            y.results() == null ? -1 : y.results().getQuality().intValue());
                }
            },
    Priority {

                @Override
                public int compare(SaNode x, SaNode y) {
                    return Integer.compare(x.getOutput() == null ? 0 : x.getOutput().getPriority(), 
                            y.getOutput() == null ? 0 : y.getOutput().getPriority());
                }
            }
}
