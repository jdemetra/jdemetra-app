/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package internal.uihelpers;

/**
 *
 * @author Jean Palate
 */
public interface DiscreteInformationProvider {

    String getInformation();
    String[] getComponents();

    DiscreteDisplayDomain getDiscreteDisplayDomain(int npoints);
    DiscreteDisplayDomain getDiscreteDisplayDomain(int lower, int upper);

    boolean isDefined(int cmp);

    double[] getDataArray(int cmp, DiscreteDisplayDomain domain);
    double getData(int cmp, int x);

}
