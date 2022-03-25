/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq;

import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.highfreq.ui.FractionalAirlineSpecUI;
import demetra.desktop.ui.processing.DocumentUIServices;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.highfreq.ExtendedAirlineModellingSpec;
import demetra.highfreq.ExtendedAirlineSpec;
import java.awt.Color;
import javax.swing.Icon;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author PALATEJ
 */
@ServiceProvider(service = DocumentUIServices.class)
public class FractionalAirlineDecompositionUIFactory implements DocumentUIServices<ExtendedAirlineModellingSpec, FractionalAirlineDecompositionDocument> {
    
//    public static FractionalAirlineUIFactory INSTANCE=new FractionalAirlineUIFactory();

    @Override
    public IProcDocumentView<FractionalAirlineDecompositionDocument> getDocumentView(FractionalAirlineDecompositionDocument document) {
        return FractionalAirlineDecompositionViewFactory.getDefault().create(document);
    }

    @Override
    public IObjectDescriptor<ExtendedAirlineModellingSpec> getSpecificationDescriptor(FractionalAirlineDecompositionDocument doc) {
        return new FractionalAirlineSpecUI(doc.getSpecification(), false);
    }

    @Override
    public Class<FractionalAirlineDecompositionDocument> getDocumentType() {
        return FractionalAirlineDecompositionDocument.class; 
    }

    @Override
    public Class<ExtendedAirlineModellingSpec> getSpecType() {
        return ExtendedAirlineModellingSpec.class; 
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
