/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.x13.ui;

import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.ui.processing.DocumentUIServices;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.x13.descriptors.X13SpecUI;
import demetra.x13.X13Spec;
import java.awt.Color;
import javax.swing.Icon;
import jdplus.x13.X13Document;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author PALATEJ
 */
@ServiceProvider(service = DocumentUIServices.class)
public class X13UIFactory implements DocumentUIServices<X13Spec, X13Document> {
    
 //   public static X13UIFactory INSTANCE=new X13UIFactory();

    @Override
    public IProcDocumentView<X13Document> getDocumentView(X13Document document) {
        return X13ViewFactory.getDefault().create(document);
    }

    @Override
    public IObjectDescriptor<X13Spec> getSpecificationDescriptor(X13Document doc) {
        return new X13SpecUI(doc.getSpecification(), false);
    }

    @Override
    public Class<X13Document> getDocumentType() {
        return X13Document.class; 
    }

    @Override
    public Class<X13Spec> getSpecType() {
        return X13Spec.class; 
    }

    @Override
    public Color getColor() {
        return Color.MAGENTA; 
    }

    @Override
    public Icon getIcon() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
