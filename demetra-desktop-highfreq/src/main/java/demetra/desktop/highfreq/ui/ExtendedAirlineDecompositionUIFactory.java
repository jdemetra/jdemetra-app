/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq.ui;

import demetra.desktop.descriptors.IObjectDescriptor;
import jdplus.highfreq.extendedairline.decomposiiton.ExtendedAirlineDecompositionDocument;
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
public class ExtendedAirlineDecompositionUIFactory implements DocumentUIServices<ExtendedAirlineDecompositionSpec, ExtendedAirlineDecompositionDocument> {
    
//    public static FractionalAirlineUIFactory INSTANCE=new FractionalAirlineUIFactory();

    @Override
    public IProcDocumentView<ExtendedAirlineDecompositionDocument> getDocumentView(ExtendedAirlineDecompositionDocument document) {
        return ExtendedAirlineDecompositionViewFactory.getDefault().create(document);
    }

    @Override
    public IObjectDescriptor<ExtendedAirlineDecompositionSpec> getSpecificationDescriptor(ExtendedAirlineDecompositionSpec spec) {
        return new ExtendedAirlineDecompositionSpecUI(spec, false);
    }

    @Override
    public Class<ExtendedAirlineDecompositionDocument> getDocumentType() {
        return ExtendedAirlineDecompositionDocument.class; 
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
    public void showDocument(WorkspaceItem<ExtendedAirlineDecompositionDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            ExtendedAirlineDecompositionTopComponent view = new ExtendedAirlineDecompositionTopComponent(item);
            view.open();
            view.requestActive();
        }
    }

}
