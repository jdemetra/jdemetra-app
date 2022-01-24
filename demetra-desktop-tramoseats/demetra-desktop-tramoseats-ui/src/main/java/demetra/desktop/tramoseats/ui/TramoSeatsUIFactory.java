/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.tramoseats.ui;

import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.tramoseats.descriptors.TramoSeatsSpecUI;
import demetra.desktop.ui.processing.DocumentUIServices;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.tramoseats.TramoSeatsSpec;
import java.awt.Color;
import javax.swing.Icon;
import jdplus.tramoseats.TramoSeatsDocument;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author PALATEJ
 */
@ServiceProvider(service = DocumentUIServices.class)
public class TramoSeatsUIFactory implements DocumentUIServices<TramoSeatsSpec, TramoSeatsDocument> {
    
//    public static TramoSeatsUIFactory INSTANCE=new TramoSeatsUIFactory();

    @Override
    public IProcDocumentView<TramoSeatsDocument> getDocumentView(TramoSeatsDocument document) {
        return TramoSeatsViewFactory.getDefault().create(document);
    }

    @Override
    public IObjectDescriptor<TramoSeatsSpec> getSpecificationDescriptor(TramoSeatsDocument doc) {
        return new TramoSeatsSpecUI(doc.getSpecification(), false);
    }

    @Override
    public Class<TramoSeatsDocument> getDocumentType() {
        return TramoSeatsDocument.class; 
    }

    @Override
    public Class<TramoSeatsSpec> getSpecType() {
        return TramoSeatsSpec.class; 
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
