/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package internal.uihelpers;


/**
 *
 * @author Jean Palate
 */
public class FrequencyInformationProvider {


     public static ContinuousDisplayDomain getDisplayDomain(final int period, final int npoints) {
        double beg = 0;
        double end = Math.PI;
         if (period == 0) {
            return new ContinuousDisplayDomain(beg, end, npoints);
        } else {
            return new ContinuousDisplayDomain(beg, end, adjust(period, npoints));
        }

    }

    public static ContinuousDisplayDomain getDisplayDomain(final int period, final double beg, final double end, final int npoints) {
        if (period == 0) {
            return new ContinuousDisplayDomain(beg, end, npoints);
        } else {
            // adjust beg...
            double begc = beg, endc = end;
            if (begc < 0) {
                begc = 0;
            }
            if (endc > Math.PI) {
                endc = Math.PI;
            }
            
            // computes the corresponding number of points for the all range:
            int n=(int) (npoints * Math.PI/(endc-begc));
            double step=Math.PI/(n-1);
            int ibeg=(int)(begc/step);
            begc=ibeg*step;
            n=adjust(period, npoints);
            endc=begc+(n-1)*step;

            return new ContinuousDisplayDomain(begc, endc, n);
        }

    }

    private static int adjust(final int period, int npoints) {
        // adjust the beg, end and npoints...
        int n;
        int ires = npoints % period;
         switch (ires) {
             case 0:
                 n = npoints + 1;
                 break;
             case 1:
                 n = npoints;
                 break;
             default:
                 n = (npoints / period + 1) * period + 1;
                 break;
         }
        return n;
    }
}
