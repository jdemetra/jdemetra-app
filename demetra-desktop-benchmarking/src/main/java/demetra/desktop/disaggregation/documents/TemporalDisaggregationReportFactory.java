/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.disaggregation.documents;

import java.util.Map;
import jdplus.tempdisagg.univariate.TemporalDisaggregationDocument;

/**
 *
 * @author Jean
 */
public interface TemporalDisaggregationReportFactory {
    String getReportName();
    String getReportDescription();
    boolean createReport(Map<String, TemporalDisaggregationDocument> processing);
}
