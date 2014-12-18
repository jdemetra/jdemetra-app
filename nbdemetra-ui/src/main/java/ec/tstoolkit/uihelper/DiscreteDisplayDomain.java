/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.tstoolkit.uihelper;

/**
 *
 * @author pcuser
 */
public class DiscreteDisplayDomain {
    public DiscreteDisplayDomain(int beg, int end)
    {
        this.beg=beg;
        this.end=end;
    }

    public final int beg, end;

    public int x(int i)
    {
        return beg+i;
    }

    public int getLength()
    {
        return end-beg+1;
    }


}
