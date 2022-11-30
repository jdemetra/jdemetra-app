/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq;

import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.highfreq.ui.FractionalAirlineSpecUI;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.highfreq.ExtendedAirlineModellingSpec;
import java.awt.Color;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author PALATEJ
 */
@ServiceProvider(service = DocumentUIServices.class)
public class FractionalAirlineUIFactory implements DocumentUIServices<ExtendedAirlineModellingSpec, FractionalAirlineDocument> {
    
//    public static FractionalAirlineUIFactory INSTANCE=new FractionalAirlineUIFactory();

    @Override
    public IProcDocumentView<FractionalAirlineDocument> getDocumentView(FractionalAirlineDocument document) {
        return FractionalAirlineViewFactory.getDefault().create(document);
    }

    @Override
    public IObjectDescriptor<ExtendedAirlineModellingSpec> getSpecificationDescriptor(ExtendedAirlineModellingSpec spec) {
        return new FractionalAirlineSpecUI(spec, false);
    }

    @Override
    public Class<FractionalAirlineDocument> getDocumentType() {
        return FractionalAirlineDocument.class; 
    }

    @Override
    public Class<ExtendedAirlineModellingSpec> getSpecType() {
        return ExtendedAirlineModellingSpec.class; 
    }

    @Override
    public Color getColor() {
        return Color.GREEN; 
    }

    @Override
    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("demetra/desktop/highfreq/tangent_green.png", false);
    }

    @Override
    public void showDocument(WorkspaceItem<FractionalAirlineDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            FractionalAirlineTopComponent view = new FractionalAirlineTopComponent(item);
            view.open();
            view.requestActive();
        }
    }

}
