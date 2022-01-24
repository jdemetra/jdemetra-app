/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.tramo.ui;

import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.tramo.descriptors.TramoSpecUI;
import demetra.desktop.ui.processing.DocumentUIServices;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.tramo.TramoSpec;
import java.awt.Color;
import javax.swing.Icon;
import jdplus.tramo.TramoDocument;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author PALATEJ
 */
@ServiceProvider(service = DocumentUIServices.class)
public class TramoUIFactory implements DocumentUIServices<TramoSpec, TramoDocument> {
    
//    public static TramoUIFactory INSTANCE=new TramoUIFactory();

    @Override
    public IProcDocumentView<TramoDocument> getDocumentView(TramoDocument document) {
        return TramoViewFactory.getDefault().create(document);
    }

    @Override
    public IObjectDescriptor<TramoSpec> getSpecificationDescriptor(TramoDocument doc) {
        return new TramoSpecUI(doc.getSpecification(), false);
    }

    @Override
    public Class<TramoDocument> getDocumentType() {
        return TramoDocument.class; 
    }

    @Override
    public Class<TramoSpec> getSpecType() {
        return TramoSpec.class; 
    }

    @Override
    public Color getColor() {
        return Color.BLUE; 
    }

    @Override
    public Icon getIcon() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
