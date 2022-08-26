/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.stl;

import demetra.timeseries.AbstractTsDocument;
import demetra.timeseries.TsData;
import jdplus.stl.StlPlusResults;
import demetra.stl.StlSpec;
import jdplus.stl.StlPlusKernel;

/**
 *
 * @author PALATEJ
 */
public class StlPlusDocument extends AbstractTsDocument<StlSpec, StlPlusResults> {

    public StlPlusDocument() {
        super(StlSpec.DEFAULT);
    }

    @Override
    protected StlPlusResults internalProcess(StlSpec spec, TsData data) {
        return StlPlusKernel.process(data, spec);
    }

}
