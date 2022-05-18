/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.stl;

import demetra.timeseries.AbstractTsDocument;
import demetra.timeseries.TsData;
import jdplus.stl.StlPlusResults;
import demetra.stl.StlSpecification;
import jdplus.stl.StlPlusKernel;

/**
 *
 * @author PALATEJ
 */
public class StlPlusDocument extends AbstractTsDocument<StlSpecification, StlPlusResults> {

    public StlPlusDocument() {
        super(StlSpecification.DEFAULT);
    }

    @Override
    protected StlPlusResults internalProcess(StlSpecification spec, TsData data) {
        return new StlPlusKernel(spec).process(data);
    }

}
