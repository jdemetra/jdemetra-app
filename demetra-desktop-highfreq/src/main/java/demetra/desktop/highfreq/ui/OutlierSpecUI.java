/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq.ui;

import demetra.desktop.sa.descriptors.highfreq.AbstractOutlierSpecUI;
import demetra.desktop.sa.descriptors.highfreq.HighFreqSpecUI;
import demetra.modelling.highfreq.OutlierSpec;

/**
 *
 * @author PALATEJ
 */
public class OutlierSpecUI extends AbstractOutlierSpecUI {

    private final FractionalAirlineSpecRoot root;

    public OutlierSpecUI(FractionalAirlineSpecRoot root) {
        this.root = root;
    }

    @Override
    protected OutlierSpec spec() {
        return root.getCore().getOutlier();
    }

    @Override
    protected HighFreqSpecUI root() {
        return root;
    }
}
