/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.tstoolkit.uihelper;

import ec.tstoolkit.timeseries.simplets.TsFrequency;

/**
 *
 * @author pcuser
 */
public class FrequencyInformationProvider {


     public static ContinuousDisplayDomain getDisplayDomain(final TsFrequency freq, final int npoints) {
        double beg = 0;
        double end = Math.PI;
        int ifreq = freq.intValue();
        if (ifreq == 0) {
            return new ContinuousDisplayDomain(beg, end, npoints);
        } else {
            return new ContinuousDisplayDomain(beg, end, adjust(freq, npoints));
        }

    }

    public static ContinuousDisplayDomain getDisplayDomain(final TsFrequency freq, final double beg, final double end, final int npoints) {
        int ifreq = freq.intValue();
        if (ifreq == 0) {
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
            n=adjust(freq, npoints);
            endc=begc+(n-1)*step;

            return new ContinuousDisplayDomain(begc, endc, n);
        }

    }

    private static int adjust(final TsFrequency freq, int npoints) {
        // adjust the beg, end and npoints...
        int n;
        int ifreq = freq.intValue();
        int ires = npoints % ifreq;
        if (ires == 0) {
            n = npoints + 1;
        } else if (ires == 1) {
            n = npoints;
        } else {
            n = (npoints / ifreq + 1) * ifreq + 1;
        }
        return n;
    }
}
