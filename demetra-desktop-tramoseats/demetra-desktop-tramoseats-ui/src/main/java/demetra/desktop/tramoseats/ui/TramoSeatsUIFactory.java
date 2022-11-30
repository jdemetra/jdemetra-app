/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.tramoseats.ui;

import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.tramoseats.descriptors.TramoSeatsSpecUI;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.tramoseats.TramoSeatsSpec;
import java.awt.Color;
import javax.swing.Icon;
import jdplus.tramoseats.TramoSeatsDocument;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author PALATEJ
 */
@ServiceProvider(service = DocumentUIServices.class)
public class TramoSeatsUIFactory implements DocumentUIServices<TramoSeatsSpec, TramoSeatsDocument> {

    @Override
    public IProcDocumentView<TramoSeatsDocument> getDocumentView(TramoSeatsDocument document) {
        return TramoSeatsViewFactory.getDefault().create(document);
    }

    @Override
    public IObjectDescriptor<TramoSeatsSpec> getSpecificationDescriptor(TramoSeatsSpec spec) {
        return new TramoSeatsSpecUI(spec, false);
    }

    @Override
    public Class<TramoSeatsDocument> getDocumentType() {
        return TramoSeatsDocument.class;
    }

    @Override
    public Class<TramoSeatsSpec> getSpecType() {
        return TramoSeatsSpec.class;
    }

    @Override
    public Color getColor() {
        return Color.BLUE;
    }

    @Override
    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("demetra/desktop/tramoseats/ui/tangent_blue.png", false);
    }

    @Override
    public void showDocument(WorkspaceItem<TramoSeatsDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            TramoSeatsTopComponent view = new TramoSeatsTopComponent(item);
            view.open();
            view.requestActive();
        }
    }

}
