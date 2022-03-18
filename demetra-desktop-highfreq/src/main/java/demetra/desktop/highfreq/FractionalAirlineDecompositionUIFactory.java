/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq;

import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.highfreq.ui.FractionalAirlineSpecUI;
import demetra.desktop.ui.processing.DocumentUIServices;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.highfreq.FractionalAirlineSpec;
import java.awt.Color;
import javax.swing.Icon;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author PALATEJ
 */
@ServiceProvider(service = DocumentUIServices.class)
public class FractionalAirlineDecompositionUIFactory implements DocumentUIServices<FractionalAirlineSpec, FractionalAirlineDocument> {
    
//    public static FractionalAirlineUIFactory INSTANCE=new FractionalAirlineUIFactory();

    @Override
    public IProcDocumentView<FractionalAirlineDocument> getDocumentView(FractionalAirlineDocument document) {
        return FractionalAirlineViewFactory.getDefault().create(document);
    }

    @Override
    public IObjectDescriptor<FractionalAirlineSpec> getSpecificationDescriptor(FractionalAirlineDocument doc) {
        return new FractionalAirlineSpecUI(doc.getSpecification(), false);
    }

    @Override
    public Class<FractionalAirlineDocument> getDocumentType() {
        return FractionalAirlineDocument.class; 
    }

    @Override
    public Class<FractionalAirlineSpec> getSpecType() {
        return FractionalAirlineSpec.class; 
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
