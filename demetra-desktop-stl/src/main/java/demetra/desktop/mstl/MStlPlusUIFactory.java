/*
 * Copyright 2023 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.mstl;

import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.mstl.ui.MStlPlusSpecUI;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.stl.MStlSpec;
import java.awt.Color;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author PALATEJ
 */
@ServiceProvider(service = DocumentUIServices.class)
public class MStlPlusUIFactory implements DocumentUIServices<MStlSpec, MStlPlusDocument> {

//    public static StlPlusUIFactory INSTANCE=new StlPlusUIFactory();
    @Override
    public IProcDocumentView<MStlPlusDocument> getDocumentView(MStlPlusDocument document) {
        return MStlPlusViewFactory.getDefault().create(document);
    }

    @Override
    public IObjectDescriptor<MStlSpec> getSpecificationDescriptor(MStlSpec spec) {
        return new MStlPlusSpecUI(spec, false);
    }

    @Override
    public Class<MStlPlusDocument> getDocumentType() {
        return MStlPlusDocument.class;
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
    public void showDocument(WorkspaceItem<MStlPlusDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            MStlPlusTopComponent view = new MStlPlusTopComponent(item);
            view.open();
            view.requestActive();
        }
    }

}
