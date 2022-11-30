/*
 * Copyright 2022 National Bank of Belgium
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
package demetra.desktop.benchmarking.ui;

import demetra.benchmarking.univariate.CholetteSpec;
import demetra.desktop.benchmarking.descriptors.CholetteSpecUI;
import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.desktop.workspace.WorkspaceItem;
import java.awt.Color;
import javax.swing.Icon;
import jdplus.benchmarking.univariate.CholetteDocument;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author PALATEJ
 */
@ServiceProvider(service = DocumentUIServices.class)
public class CholetteUIFactory implements DocumentUIServices<CholetteSpec, CholetteDocument> {

    @Override
    public IProcDocumentView<CholetteDocument> getDocumentView(CholetteDocument document) {
        return CholetteViewFactory.getDefault().create(document);
    }

    @Override
    public IObjectDescriptor<CholetteSpec> getSpecificationDescriptor(CholetteSpec spec) {
        return new CholetteSpecUI(spec);
    }

    @Override
    public Class<CholetteDocument> getDocumentType() {
        return CholetteDocument.class;
    }

    @Override
    public Class<CholetteSpec> getSpecType() {
        return CholetteSpec.class;
    }

    @Override
    public Color getColor() {
        return Color.BLUE;
    }

    @Override
    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("demetra/desktop/benchmarking/resource-monitor_16x16.png", false);
    }

    @Override
    public void showDocument(WorkspaceItem<CholetteDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            CholetteTopComponent view = new CholetteTopComponent(item);
            view.open();
            view.requestActive();
        }
    }

}
