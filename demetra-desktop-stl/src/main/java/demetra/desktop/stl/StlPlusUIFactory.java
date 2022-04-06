/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.stl;

import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.ui.processing.DocumentUIServices;
import demetra.desktop.ui.processing.IProcDocumentView;
import java.awt.Color;
import javax.swing.Icon;
import jdplus.stl.StlPlusSpecification;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author PALATEJ
 */
@ServiceProvider(service = DocumentUIServices.class)
public class StlPlusUIFactory implements DocumentUIServices<StlPlusSpecification, StlPlusDocument> {
    
//    public static StlPlusUIFactory INSTANCE=new StlPlusUIFactory();

    @Override
    public IProcDocumentView<StlPlusDocument> getDocumentView(StlPlusDocument document) {
        return StlPlusViewFactory.getDefault().create(document);
    }

    @Override
    public IObjectDescriptor<StlPlusSpecification> getSpecificationDescriptor(StlPlusDocument doc) {
        return null;
    }

    @Override
    public Class<StlPlusDocument> getDocumentType() {
        return StlPlusDocument.class; 
    }

    @Override
    public Class<StlPlusSpecification> getSpecType() {
        return StlPlusSpecification.class; 
    }

    @Override
    public Color getColor() {
        return Color.GRAY; 
    }

    @Override
    public Icon getIcon() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
