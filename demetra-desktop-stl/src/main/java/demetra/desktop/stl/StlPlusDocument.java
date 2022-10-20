/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.stl;

import demetra.timeseries.AbstractTsDocument;
import demetra.timeseries.TsData;
import jdplus.stl.StlPlusResults;
import demetra.stl.MStlSpec;
import jdplus.stl.StlPlusKernel;

/**
 *
 * @author PALATEJ
 */
public class StlPlusDocument extends AbstractTsDocument<MStlSpec, StlPlusResults> {

    public StlPlusDocument() {
        super(MStlSpec.DEFAULT);
    }

    @Override
    protected StlPlusResults internalProcess(MStlSpec spec, TsData data) {
        return StlPlusKernel.process(data, spec);
    }

}
