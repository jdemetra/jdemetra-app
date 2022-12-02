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
package demetra.desktop.disaggregation.ui;

import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.disaggregation.descriptors.TemporalDisaggregationSpecUI;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.tempdisagg.univariate.TemporalDisaggregationSpec;
import java.awt.Color;
import javax.swing.Icon;
import jdplus.tempdisagg.univariate.TemporalDisaggregationDocument;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author PALATEJ
 */
@ServiceProvider(service = DocumentUIServices.class)
public class TemporalDisaggregationUIFactory implements DocumentUIServices<TemporalDisaggregationSpec, TemporalDisaggregationDocument> {

    @Override
    public IProcDocumentView<TemporalDisaggregationDocument> getDocumentView(TemporalDisaggregationDocument document) {
        return TemporalDisaggregationViewFactory.getDefault().create(document);
    }

    @Override
    public IObjectDescriptor<TemporalDisaggregationSpec> getSpecificationDescriptor(TemporalDisaggregationSpec spec) {
        return new TemporalDisaggregationSpecUI(spec, false);
    }

    @Override
    public Class<TemporalDisaggregationDocument> getDocumentType() {
        return TemporalDisaggregationDocument.class;
    }

    @Override
    public Class<TemporalDisaggregationSpec> getSpecType() {
        return TemporalDisaggregationSpec.class;
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
    public void showDocument(WorkspaceItem<TemporalDisaggregationDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            TemporalDisaggregationDocumentTopComponent view = new TemporalDisaggregationDocumentTopComponent(item);
            view.open();
            view.requestActive();
        }
    }

}
