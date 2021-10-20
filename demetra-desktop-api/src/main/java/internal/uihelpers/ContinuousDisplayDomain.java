/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package internal.uihelpers;

/**
 *
 * @author Jean Palate
 */
public class ContinuousDisplayDomain {

    public ContinuousDisplayDomain(double beg, double end, int npoints)
    {
        this.beg=beg;
        this.end=end;
        this.npoints=npoints;
        this.step=(end-beg)/(npoints-1);
    }

    public final double step;
    public final double beg, end;
    public final int npoints;

    public double x(int i)
    {
        return beg+step*i;
    }
}
