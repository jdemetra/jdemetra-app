/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing;

import demetra.timeseries.Ts;
import demetra.timeseries.TsData;
import demetra.timeseries.TsPeriod;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class StaticChartUI implements ItemUI< List<Ts>> {
    
    public static enum Transformation{
        NONE, INDEX
    }

    private final Transformation transformation;

    public StaticChartUI(Transformation transformation){
        this.transformation=transformation;
    }
    
    private List<Ts> transform(List<Ts> ts){
        return switch (transformation) {
            case INDEX -> index(ts);
            default -> ts;
        };
    }
    
    private List<Ts> index(List<Ts> ts){
        if (ts.isEmpty())
            return ts;
        int year=-1;
        for (Ts s : ts){
            TsPeriod start = s.getData().getStart();
            int nyear = start.year();
            if (start.annualPosition()>0)
                ++nyear;
            if (nyear > year) {
                year = nyear;
            }
        }
        
        LocalDateTime ref=LocalDate.of(year, Month.JANUARY, 1).atStartOfDay();
        
        List<Ts> items = new ArrayList<>();
        for (Ts s : ts){
            TsData data = s.getData();
            int idx = data.getDomain().indexOf(ref);
            if (idx>=0){
                double scale=100/data.getValue(idx);
                if (Double.isFinite(scale)){
                    data=data.multiply(scale);
                    items.add(Ts.of(s.getName(), data));
                }
            }
        }
        return items;
    }


    @Override
    public JComponent getView(List<Ts> ts) {

        return TsViewToolkit.getChart(transform(ts));
    }

 }
