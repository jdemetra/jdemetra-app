/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.tramo.ui;

import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.tramo.descriptors.TramoSpecUI;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.tramo.TramoSpec;
import java.awt.Color;
import javax.swing.Icon;
import jdplus.tramo.TramoDocument;
import org.openide.util.ImageUtilities;
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
    public IObjectDescriptor<TramoSpec> getSpecificationDescriptor(TramoSpec spec) {
        return new TramoSpecUI(spec, false);
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
        return ImageUtilities.loadImageIcon("demetra/desktop/tramo/ui/tangent_blue.png", false);
    }

    @Override
    public void showDocument(WorkspaceItem<TramoDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            TramoTopComponent view = new TramoTopComponent(item);
            view.open();
            view.requestActive();
        }
    }

}
