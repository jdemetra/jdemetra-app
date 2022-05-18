/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.regarima.ui;

import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.regarima.descriptors.RegArimaSpecUI;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.regarima.RegArimaSpec;
import java.awt.Color;
import javax.swing.Icon;
import jdplus.x13.regarima.RegArimaDocument;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author PALATEJ
 */
@ServiceProvider(service = DocumentUIServices.class)
public class RegArimaUIFactory implements DocumentUIServices<RegArimaSpec, RegArimaDocument> {
    
 //   public static RegArimaUIFactory INSTANCE=new RegArimaUIFactory();

    @Override
    public IProcDocumentView<RegArimaDocument> getDocumentView(RegArimaDocument document) {
        return RegArimaViewFactory.getDefault().create(document);
    }

    @Override
    public IObjectDescriptor<RegArimaSpec> getSpecificationDescriptor(RegArimaDocument doc) {
        return new RegArimaSpecUI(doc.getSpecification(), false);
    }

    @Override
    public Class<RegArimaDocument> getDocumentType() {
        return RegArimaDocument.class; 
    }

    @Override
    public Class<RegArimaSpec> getSpecType() {
        return RegArimaSpec.class; 
    }

    @Override
    public Color getColor() {
        return Color.MAGENTA; 
    }

    @Override
    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("demetra/desktop/regarima/ui/tangent_magenta.png", false);
    }

    @Override
    public void showDocument(WorkspaceItem<RegArimaDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            RegArimaTopComponent view = new RegArimaTopComponent(item);
            view.open();
            view.requestActive();
        }
    }
}
