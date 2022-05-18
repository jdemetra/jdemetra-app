/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.sa.output;

import demetra.math.matrices.Matrix;
import demetra.sa.SaProcessingFactory;
import demetra.timeseries.TsData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author PALATEJ
 */
@lombok.experimental.UtilityClass
public class OutputSelection {

    public List<String> items(List<SaProcessingFactory> fac, Predicate<Class> selector) {
        if (fac.isEmpty())
            return Collections.emptyList();
        if (fac.size() == 1) {
            return fac.get(0).outputDictionary().entries().
                    filter(entry->selector.test(entry.getOutputClass())).
                    map(entry->entry.fullName()).
                    collect(Collectors.toList());
        }else{
            List<String> all=new ArrayList<>();
            Set<String> tmp=new HashSet<>();
            fac.forEach(processor->fillDictionary(processor, all, tmp, selector));
            return all;
        }
    }
    
    private void fillDictionary(SaProcessingFactory processor, List<String> all, Set<String> tmp, Predicate<Class> selector) {
        processor.outputDictionary().entries().filter(entry->selector.test(entry.getOutputClass())).forEachOrdered(entry->{
            String n=entry.fullName();
            if (!tmp.contains(n)){
                tmp.add(n);
                all.add(n);
            }
        });
    }
    
    public List<String> seriesItems(List<SaProcessingFactory> fac){
        return items(fac, OutputSelection::acceptSeries);
    }
    
    public List<String> matrixItems(List<SaProcessingFactory> fac){
        return items(fac, OutputSelection::acceptMatrix);
    }
    
    private boolean acceptMatrix(Class C){
        if (C.equals(TsData.class))
            return false;
        if (C.isArray())
            return false;
        if (Matrix.class.isAssignableFrom(C))
            return false;
        // Additional selection criteria ?
        return true;
    }

    private boolean acceptSeries(Class C){
        return TsData.class.isAssignableFrom(C);
    }
    
}
