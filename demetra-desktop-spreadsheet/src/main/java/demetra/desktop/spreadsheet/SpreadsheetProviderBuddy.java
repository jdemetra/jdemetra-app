/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package demetra.desktop.spreadsheet;

import demetra.desktop.TsManager;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.tsproviders.DataSourceProviderBuddy;
import demetra.desktop.tsproviders.DataSourceProviderBuddyUtil;
import static demetra.desktop.tsproviders.TsProviderProperties.addFile;
import static demetra.desktop.tsproviders.TsProviderProperties.addObsFormat;
import static demetra.desktop.tsproviders.TsProviderProperties.addObsGathering;
import demetra.spreadsheet.SpreadSheetBean;
import demetra.tsprovider.DataSet;
import demetra.tsprovider.DataSource;
import demetra.tsprovider.FileLoader;
import nbbrd.service.ServiceProvider;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;

import java.awt.*;
import java.beans.IntrospectionException;
import nbbrd.design.DirectImpl;
import org.openide.nodes.Sheet;

/**
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider(DataSourceProviderBuddy.class)
public final class SpreadsheetProviderBuddy implements DataSourceProviderBuddy {

    private static final String SOURCE = "XCLPRVDR";

    @Override
    public String getProviderName() {
        return SOURCE;
    }

    @Override
    public Image getIconOrNull(int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/document-table.png", true);
    }

    @Override
    public Image getIconOrNull(DataSource dataSource, int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/tables.png", true);
    }

    @Override
    public Image getIconOrNull(DataSet dataSet, int type, boolean opened) {
        switch (dataSet.getKind()) {
            case COLLECTION:
                return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/table-sheet.png", true);
            case SERIES:
                return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/table-select-row.png", true);
        }
        return null;
    }

    @Override
    public Sheet getSheetOfBeanOrNull(Object bean) throws IntrospectionException {
        return bean instanceof SpreadSheetBean
                ? createSheetSets((SpreadSheetBean) bean)
                : DataSourceProviderBuddy.super.getSheetOfBeanOrNull(bean);
    }

    private Sheet createSheetSets(SpreadSheetBean bean) {
        NodePropertySetBuilder b = new NodePropertySetBuilder();
        return DataSourceProviderBuddyUtil.sheetOf(
                createSource(b, bean),
                createOptions(b, bean)
        );
    }

    @Messages({
        "bean.source.display=Source",
        "bean.source.description="
    })
    private Sheet.Set createSource(NodePropertySetBuilder b, SpreadSheetBean bean) {
        b.reset("source")
                .display(Bundle.bean_source_display())
                .description(Bundle.bean_source_description());
        TsManager.get()
                .getProvider(FileLoader.class, SOURCE)
                .ifPresent(o -> addFile(b, o, bean));
        return b.build();
    }

    @Messages({
        "bean.options.display=Options",
        "bean.options.description="
    })
    private Sheet.Set createOptions(NodePropertySetBuilder b, SpreadSheetBean bean) {
        b.reset("options")
                .display(Bundle.bean_options_display())
                .description(Bundle.bean_options_description());
        addObsFormat(b, bean::getFormat, bean::setFormat);
        addObsGathering(b, bean::getGathering, bean::setGathering);
        return b.build();
    }
}
