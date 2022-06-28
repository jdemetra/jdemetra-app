/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.stl;

import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.stl.ui.StlPlusSpecUI;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.desktop.workspace.WorkspaceItem;
import java.awt.Color;
import javax.swing.Icon;
import demetra.stl.StlSpecification;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author PALATEJ
 */
@ServiceProvider(service = DocumentUIServices.class)
public class StlPlusUIFactory implements DocumentUIServices<StlSpecification, StlPlusDocument> {

//    public static StlPlusUIFactory INSTANCE=new StlPlusUIFactory();
    @Override
    public IProcDocumentView<StlPlusDocument> getDocumentView(StlPlusDocument document) {
        return StlPlusViewFactory.getDefault().create(document);
    }

    @Override
    public IObjectDescriptor<StlSpecification> getSpecificationDescriptor(StlPlusDocument doc) {
        return new StlPlusSpecUI(doc.getSpecification(), false);
    }

    @Override
    public Class<StlPlusDocument> getDocumentType() {
        return StlPlusDocument.class;
    }

    @Override
    public Class<StlSpecification> getSpecType() {
        return StlSpecification.class;
    }

    @Override
    public Color getColor() {
        return Color.RED;
    }

    @Override
    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("demetra/desktop/stl/tangent_red.png", false);
    }

    @Override
    public void showDocument(WorkspaceItem<StlPlusDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            StlPlusTopComponent view = new StlPlusTopComponent(item);
            view.open();
            view.requestActive();
        }
    }

}