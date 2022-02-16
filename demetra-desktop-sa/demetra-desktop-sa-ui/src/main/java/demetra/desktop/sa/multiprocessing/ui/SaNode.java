/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.sa.multiprocessing.ui;

import demetra.information.Explorable;
import demetra.sa.SaDefinition;
import demetra.sa.SaEstimation;
import demetra.sa.SaItem;
import demetra.sa.SaSpecification;
import demetra.timeseries.Ts;
import demetra.timeseries.TsFactory;
import demetra.timeseries.TsInformationType;
import demetra.timeseries.TsMoniker;

/**
 *
 * @author PALATEJ
 */
@lombok.Data
public class SaNode {

    final int id;
    final String name;
    final TsMoniker moniker;
    final SaSpecification spec;

    volatile SaItem output;

    static SaNode of(int id, Ts ts, SaSpecification spec) {
        SaNode node = new SaNode(id, ts.getName(), ts.getMoniker(), spec);
        if (ts.getType().encompass(TsInformationType.Data)) {
            node.setOutput(SaItem.of(ts, spec));
        }
        return node;
    }

    static SaNode of(int id, SaItem item) {
        SaDefinition definition = item.getDefinition();
        SaNode node = new SaNode(id, item.getName(), definition.getTs().getMoniker(), definition.getDomainSpec());
        node.output = item;
        return node;
    }

    void process() {
        if (output == null) {
            Ts ts = TsFactory.getDefault().makeTs(moniker, TsInformationType.Data);
            output = SaItem.of(ts, spec);
        }
    }
    
    SaEstimation results(){
        return output == null ? null : output.getEstimation();
    }
    
    boolean isProcessed(){
        SaItem o=output;
        return o != null && o.isProcessed();
    }
}
