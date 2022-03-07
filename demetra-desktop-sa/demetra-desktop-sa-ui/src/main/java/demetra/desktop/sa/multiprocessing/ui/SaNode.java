/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.sa.multiprocessing.ui;

import demetra.sa.EstimationPolicyType;
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

    public static SaNode of(int id, Ts ts, SaSpecification spec) {
        SaNode node = new SaNode(id, ts.getName(), ts.getMoniker(), spec);
        if (ts.getType().encompass(TsInformationType.Data)) {
            node.setOutput(SaItem.of(ts, spec));
        }
        return node;
    }

    public static SaNode of(int id, SaItem item) {
        SaDefinition definition = item.getDefinition();
        SaNode node = new SaNode(id, item.getName(), definition.getTs().getMoniker(), definition.activeSpecification());
        node.output = item;
        return node;
    }

    public SaNode with(SaSpecification nspec) {
        SaItem item = output;
        if (item == null) {
            return new SaNode(id, name, moniker, nspec);
        } else {
            SaDefinition definition = item.getDefinition();
            SaDefinition ndefinition = definition.toBuilder()
                    .domainSpec(definition.getDomainSpec())
                    .estimationSpec(nspec)
                    .policy(EstimationPolicyType.Interactive)
                    .build();
            SaItem nitem=SaItem.builder()
                    .definition(ndefinition)
                    .comment(item.getComment())
                    .name(item.getName())
                    .meta(item.getMeta())
                    .priority(item.getPriority())
                    .build();
            return SaNode.of(id, nitem);
        }
    }

    public void process() {
        if (output == null) {
            Ts ts = TsFactory.getDefault().makeTs(moniker, TsInformationType.Data);
            output = SaItem.of(ts, spec);
        }
    }

    public SaEstimation results() {
        return output == null ? null : output.getEstimation();
    }

    public boolean isProcessed() {
        SaItem o = output;
        return o != null && o.isProcessed();
    }

    public SaSpecification domainSpec() {
        if (output != null) {
            return output.getDefinition().getDomainSpec();
        } else {
            return spec;
        }
    }
}
