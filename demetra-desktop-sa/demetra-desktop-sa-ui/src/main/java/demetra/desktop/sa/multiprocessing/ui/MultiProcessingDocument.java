/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.ui;

import demetra.sa.EstimationPolicy;
import demetra.sa.SaDefinition;
import demetra.sa.SaItem;
import demetra.sa.SaItems;
import demetra.sa.SaSpecification;
import demetra.timeseries.Ts;
import demetra.timeseries.TsInformationType;
import demetra.util.Documented;
import java.util.Collections;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Jean Palate
 */
public class MultiProcessingDocument implements Documented {

    private Map<String, String> metadata = Collections.emptyMap();

    private SaItems current;
    private final SaItems initial;

    private MultiProcessingDocument(SaItems initial) {
        this.initial = initial;
        this.current = initial;
    }

    public SaItems getCurrent() {
        return current;
    }

    public SaItems getInitial() {
        return initial;
    }

    public static MultiProcessingDocument createNew() {
        MultiProcessingDocument doc = new MultiProcessingDocument(SaItems.empty());
        return doc;
    }

    public static MultiProcessingDocument open(SaItems initial) {
        MultiProcessingDocument doc = new MultiProcessingDocument(initial);
        return doc;
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }

    public boolean isNew() {
        return initial.isEmpty();
    }

    public void refresh(EstimationPolicy policy) {
        current = initial.refresh(policy, TsInformationType.Data);
    }

    public void updateMetadata(Map<String, String> nmeta) {
        current = current.withMetadata(nmeta);
    }

    public void add(@NonNull SaSpecification spec, Ts... nts) {
        SaItems.Builder builder = current.toBuilder();
        for (Ts ts : nts) {
            builder.item(
                    SaItem.builder().definition(
                            SaDefinition.builder()
                                    .domainSpec(spec)
                                    .ts(ts)
                                    .build())
                            .build());
        }
        current = builder.build();
    }

    public void add(SaItem... nitems) {
        current = current.addItems(nitems);
    }

    public void replace(int pos, SaItem nitem) {
        current = current.withItem(pos, nitem);
    }

    public void replace(SaItem oitem, SaItem nitem) {
        current = current.replaceItem(oitem, nitem);
    }
    
    public void replace(Predicate<SaItem> test, UnaryOperator<SaItem> op){
        current = current.replaceItems(test, op);
    }

    public void remove(SaItem... nitems) {
        current = current.removeItems(nitems);
    }
    
    public void reset(){
        this.current=initial;
    }
}
