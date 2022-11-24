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
import demetra.timeseries.regression.ModellingContext;

/**
 *
 * @author PALATEJ
 */
@lombok.Data
public class SaNode {

    /**
     * Status of the processing
     */
    public static enum Status {

        Unprocessed,
        NoData,
        Pending,
        Valid,
        Invalid;

        public boolean isError() {
            return this == NoData || this == Invalid;
        }

        public boolean isProcessed() {
            return this != Unprocessed && this != Pending;
        }
    }
    
    final int id;
    final String name;
    final TsMoniker moniker;
    final SaSpecification spec;

    volatile SaItem output;
    volatile Status status=Status.Unprocessed;

    public static SaNode of(int id, Ts ts, SaSpecification spec) {
        SaNode node = new SaNode(id, ts.getName(), ts.getMoniker(), spec);
        if (ts.getType().encompass(TsInformationType.Data)) {
            node.setOutput(SaItem.of(ts, spec));
            node.status=Status.Unprocessed;
        }
        return node;
    }
    
    private static Status status(SaItem item){
        SaEstimation estimation = item.getEstimation();
        if (estimation == null)
            return Status.Unprocessed;
        if (estimation.getResults() != null)
            return Status.Valid;
        return Status.Unprocessed; // Invalid should be captured elsewhere
    }

    public static SaNode of(int id, SaItem item) {
        SaDefinition definition = item.getDefinition();
        SaNode node = new SaNode(id, item.getName(), definition.getTs().getMoniker(), definition.activeSpecification());
        node.output = item;
        node.status = status(item);
        return node;
    }
    
    void prepare(){
        if (output == null) {
            Ts ts = TsFactory.getDefault().makeTs(moniker, TsInformationType.Data);
            output = SaItem.of(ts, spec);
            status=Status.Unprocessed;
        }
    }

    public void process(ModellingContext context, boolean verbose) {
        if (status.isProcessed())
            return;
        if (output == null) {
            Ts ts = TsFactory.getDefault().makeTs(moniker, TsInformationType.Data);
            output = SaItem.of(ts, spec);
        }
        status = output.process(context, verbose) ? Status.Valid : Status.Invalid;
    }

    public SaNode with(SaSpecification nspec) {
        SaItem item = output;
        if (item == null) {
            return new SaNode(id, name, moniker, nspec);
        } else {
            SaDefinition definition = item.getDefinition();
            SaDefinition ndefinition = definition.toBuilder()
                    .domainSpec(nspec)
                    .policy(EstimationPolicyType.None)
                    .build();
            SaItem nitem=SaItem.builder()
                    .definition(ndefinition)
                    .name(item.getName())
                    .meta(item.getMeta())
                    .priority(item.getPriority())
                    .build();
            return SaNode.of(id, nitem);
        }
    }

    public SaEstimation results() {
        return output == null ? null : output.getEstimation();
    }

    public boolean isProcessed() {
        return status.isProcessed();
    }

    public SaSpecification domainSpec() {
        if (output != null) {
            return output.getDefinition().getDomainSpec();
        } else {
            return spec;
        }
    }
}
