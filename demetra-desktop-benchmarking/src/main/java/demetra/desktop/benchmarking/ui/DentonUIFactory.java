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

import demetra.benchmarking.univariate.DentonSpec;
import demetra.desktop.benchmarking.descriptors.DentonSpecUI;
import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.desktop.workspace.WorkspaceItem;
import java.awt.Color;
import javax.swing.Icon;
import jdplus.benchmarking.univariate.DentonDocument;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author PALATEJ
 */
@ServiceProvider(service = DocumentUIServices.class)
public class DentonUIFactory implements DocumentUIServices<DentonSpec, DentonDocument> {

    @Override
    public IProcDocumentView<DentonDocument> getDocumentView(DentonDocument document) {
        return DentonViewFactory.getDefault().create(document);
    }

    @Override
    public IObjectDescriptor<DentonSpec> getSpecificationDescriptor(DentonSpec spec) {
        return new DentonSpecUI(spec);
    }

    @Override
    public Class<DentonDocument> getDocumentType() {
        return DentonDocument.class;
    }

    @Override
    public Class<DentonSpec> getSpecType() {
        return DentonSpec.class;
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
    public void showDocument(WorkspaceItem<DentonDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            DentonTopComponent view = new DentonTopComponent(item);
            view.open();
            view.requestActive();
        }
    }

}
