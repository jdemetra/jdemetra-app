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
package ec.nbdemetra.ui.tsproviders;

import demetra.ui.properties.PropertySheetDialogBuilder;
import demetra.ui.properties.NodePropertySetBuilder;
import ec.tss.tsproviders.*;
import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Philippe Charles
 */
public abstract class AbstractDataSourceProviderBuddy implements IDataSourceProviderBuddy {

    private static final String DEFAULT_PROVIDER_ICON = "ec/nbdemetra/ui/nodes/document.png";
    private static final String DEFAULT_COLLECTION_ICON = "ec/nbdemetra/ui/nodes/folder.png";
    private static final String DEFAULT_SERIES_ICON = "ec/nbdemetra/ui/nodes/chart_line.png";
    private static final String DEFAULT_EXCEPTION_ICON = "ec/nbdemetra/ui/nodes/exclamation-red.png";

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.loadImage(DEFAULT_PROVIDER_ICON, true);
    }

    @Override
    public Image getIcon(DataSet dataSet, int type, boolean opened) {
        switch (dataSet.getKind()) {
            case COLLECTION:
                return ImageUtilities.loadImage(DEFAULT_COLLECTION_ICON, true);
            case SERIES:
                return ImageUtilities.loadImage(DEFAULT_SERIES_ICON, true);
            case DUMMY:
                return null;
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Image getIcon(IOException ex, int type, boolean opened) {
        return ImageUtilities.loadImage(DEFAULT_EXCEPTION_ICON, true);
    }

    @Override
    public Sheet createSheet() {
        return createSheet(createSheetSets());
    }

    @Override
    public Sheet createSheet(DataSource dataSource) {
        return createSheet(createSheetSets(dataSource));
    }

    @Override
    public Sheet createSheet(DataSet dataSet) {
        return createSheet(createSheetSets(dataSet));
    }

    @Override
    public boolean editBean(String title, Object bean) throws IntrospectionException {
        Sheet sheet = createSheet(createSheetSets(bean));
        Image image = getIcon(BeanInfo.ICON_COLOR_16x16, false);
        return new PropertySheetDialogBuilder().title(title).icon(image).editSheet(sheet);
    }

    @Nonnull
    protected List<Set> createSheetSets() {
        return ProvidersUtil.sheetSetsOfProvider(getProviderName());
    }

    @Nonnull
    protected List<Set> createSheetSets(@Nonnull DataSource dataSource) {
        return ProvidersUtil.sheetSetsOfDataSource(dataSource, ProvidersUtil.usingErrorManager(this::createSheetSets, Collections::emptyList));
    }

    @Nonnull
    protected List<Set> createSheetSets(@Nonnull DataSet dataSet) {
        return ProvidersUtil.sheetSetsOfDataSet(dataSet, this::createSheetSets, this::fillParamProperties);
    }

    @Nonnull
    protected void fillParamProperties(@Nonnull NodePropertySetBuilder b, @Nonnull DataSet dataSet) {
        ProvidersUtil.fillParamProperties(b, dataSet);
    }

    @Nonnull
    protected List<Set> createSheetSets(@Nonnull Object bean) throws IntrospectionException {
        return ProvidersUtil.sheetSetsOfBean(bean);
    }

    static Sheet createSheet(List<Set> sets) {
        return ProvidersUtil.sheetOf(sets);
    }
}
