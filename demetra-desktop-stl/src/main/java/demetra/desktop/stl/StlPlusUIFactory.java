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
import demetra.stl.MStlSpec;
import java.awt.Color;
import javax.swing.Icon;
import demetra.stl.StlSpec;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author PALATEJ
 */
@ServiceProvider(service = DocumentUIServices.class)
public class StlPlusUIFactory implements DocumentUIServices<MStlSpec, StlPlusDocument> {

//    public static StlPlusUIFactory INSTANCE=new StlPlusUIFactory();
    @Override
    public IProcDocumentView<StlPlusDocument> getDocumentView(StlPlusDocument document) {
        return StlPlusViewFactory.getDefault().create(document);
    }

    @Override
    public IObjectDescriptor<MStlSpec> getSpecificationDescriptor(MStlSpec spec) {
        return new StlPlusSpecUI(spec, false);
    }

    @Override
    public Class<StlPlusDocument> getDocumentType() {
        return StlPlusDocument.class;
    }

    @Override
    public Class<MStlSpec> getSpecType() {
        return MStlSpec.class;
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
