/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import com.google.common.primitives.Ints;
import ec.tss.sa.SaItem;
import java.util.Comparator;

/**
 *
 * @author Kristof Bayens
 */
public enum SaItemComparer implements Comparator<SaItem> {

    Key {

        @Override
        public int compare(SaItem x, SaItem y) {
            return Ints.compare(x.getKey(), y.getKey());
        }
    },
    Name {

        @Override
        public int compare(SaItem x, SaItem y) {
            String nx = x.getTs().getName();
            String ny = y.getTs().getName();
            if (nx == null && ny == null) {
                return 0;
            } else if (nx == null) {
                return -1;
            } else if (ny == null) {
                return 1;
            } else {
                return nx.compareTo(ny);
            }
        }
    },
    Method {

        @Override
        public int compare(SaItem x, SaItem y) {
            String mx = x.getDomainSpecification().toString();
            String my = y.getDomainSpecification().toString();
            return mx.compareTo(my);
        }
    },
    Quality {

        @Override
        public int compare(SaItem x, SaItem y) {
            return Ints.compare(x.getQuality().intValue(), y.getQuality().intValue());
        }
    },
    Priority {

        @Override
        public int compare(SaItem x, SaItem y) {
            return Ints.compare(x.getPriority(), y.getPriority());
        }
    },
    Status {

        @Override
        public int compare(SaItem o1, SaItem o2) {
            return o1.getStatus().compareTo(o2.getStatus());
        }        
    };
}
