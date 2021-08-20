/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import demetra.ui.util.NetBeansServiceBackend;
import ec.tss.sa.SaProcessing;
import nbbrd.service.Quantifier;
import nbbrd.service.ServiceDefinition;

/**
 *
 * @author Jean Palate
 */
@ServiceDefinition(
        quantifier = Quantifier.MULTIPLE,
        backend = NetBeansServiceBackend.class,
        singleton = true
)
public interface ISaReportFactory {

    String getReportName();

    String getReportDescription();

    boolean createReport(SaProcessing processing);
}