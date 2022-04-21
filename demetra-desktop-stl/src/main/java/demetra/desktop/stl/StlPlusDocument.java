/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.stl;

import demetra.timeseries.AbstractTsDocument;
import demetra.timeseries.TsData;
import jdplus.stl.StlPlusResults;
import demetra.stl.StlPlusSpecification;

/**
 *
 * @author PALATEJ
 */
public class StlPlusDocument extends AbstractTsDocument<StlPlusSpecification, StlPlusResults> {

    public StlPlusDocument() {
        super(StlPlusSpecification.DEFAULT);
    }

    @Override
    protected StlPlusResults internalProcess(StlPlusSpecification spec, TsData data) {
        return null;
    }

}
