/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.x13.ui;

import demetra.desktop.ui.processing.ItemUI;
import javax.swing.JComponent;
import jdplus.x13.X13Document;

/**
 *
 * @author Jean Palate <jean.palate@nbb.be>
 */
public class X13Summary implements ItemUI<X13Document>{

    @Override
    public JComponent getView(X13Document document) {
        JX13Summary view=new JX13Summary();
        view.set(document);
        return view;
    }
    
}