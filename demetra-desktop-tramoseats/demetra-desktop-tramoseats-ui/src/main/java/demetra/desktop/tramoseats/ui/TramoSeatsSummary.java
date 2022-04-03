/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.tramoseats.ui;

import demetra.desktop.ui.processing.ItemUI;
import javax.swing.JComponent;
import jdplus.tramoseats.TramoSeatsDocument;

/**
 *
 * @author PALATEJ
 */
public class TramoSeatsSummary implements ItemUI<TramoSeatsDocument>{

    @Override
    public JComponent getView(TramoSeatsDocument document) {
        JTramoSeatsSummary view=new JTramoSeatsSummary();
        view.set(document);
        return view;
    }
    
}
