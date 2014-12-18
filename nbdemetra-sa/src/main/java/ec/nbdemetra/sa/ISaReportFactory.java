/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import ec.tss.sa.SaProcessing;

/**
 *
 * @author Jean Palate
 */
public interface ISaReportFactory {
    
    String getReportName();
    String getReportDescription();
    boolean createReport(SaProcessing processing);
}
