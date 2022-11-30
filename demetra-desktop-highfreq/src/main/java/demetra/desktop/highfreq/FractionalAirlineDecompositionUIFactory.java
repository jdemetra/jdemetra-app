/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq;

import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.highfreq.ui.FractionalAirlineDecompositionSpecUI;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.highfreq.ExtendedAirlineDecompositionSpec;
import java.awt.Color;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author PALATEJ
 */
@ServiceProvider(service = DocumentUIServices.class)
public class FractionalAirlineDecompositionUIFactory implements DocumentUIServices<ExtendedAirlineDecompositionSpec, FractionalAirlineDecompositionDocument> {
    
//    public static FractionalAirlineUIFactory INSTANCE=new FractionalAirlineUIFactory();

    @Override
    public IProcDocumentView<FractionalAirlineDecompositionDocument> getDocumentView(FractionalAirlineDecompositionDocument document) {
        return FractionalAirlineDecompositionViewFactory.getDefault().create(document);
    }

    @Override
    public IObjectDescriptor<ExtendedAirlineDecompositionSpec> getSpecificationDescriptor(ExtendedAirlineDecompositionSpec spec) {
        return new FractionalAirlineDecompositionSpecUI(spec, false);
    }

    @Override
    public Class<FractionalAirlineDecompositionDocument> getDocumentType() {
        return FractionalAirlineDecompositionDocument.class; 
    }

    @Override
    public Class<ExtendedAirlineDecompositionSpec> getSpecType() {
        return ExtendedAirlineDecompositionSpec.class; 
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
    public void showDocument(WorkspaceItem<FractionalAirlineDecompositionDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            FractionalAirlineDecompositionTopComponent view = new FractionalAirlineDecompositionTopComponent(item);
            view.open();
            view.requestActive();
        }
    }

}
