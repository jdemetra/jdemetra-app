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
package demetra.desktop.tsproviders;

import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.properties.PropertySheetDialogBuilder;
import demetra.tsprovider.DataSet;
import demetra.tsprovider.DataSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;

import java.awt.*;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.Collections;
import java.util.List;

/**
 * @author Philippe Charles
 */
public abstract class AbstractDataSourceProviderBuddy implements DataSourceProviderBuddy {

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
        Image image = getIconOrNull(BeanInfo.ICON_COLOR_16x16, false);
        return new PropertySheetDialogBuilder().title(title).icon(image).editSheet(sheet);
    }

    @NonNull
    protected List<Set> createSheetSets() {
        return ProvidersUtil.sheetSetsOfProvider(getProviderName());
    }

    @NonNull
    protected List<Set> createSheetSets(@NonNull DataSource dataSource) {
        return ProvidersUtil.sheetSetsOfDataSource(dataSource, ProvidersUtil.usingErrorManager(this::createSheetSets, Collections::emptyList));
    }

    @NonNull
    protected List<Set> createSheetSets(@NonNull DataSet dataSet) {
        return ProvidersUtil.sheetSetsOfDataSet(dataSet, this::createSheetSets, this::fillParamProperties);
    }

    protected void fillParamProperties(@NonNull NodePropertySetBuilder b, @NonNull DataSet dataSet) {
        ProvidersUtil.fillParamProperties(b, dataSet);
    }

    @NonNull
    protected List<Set> createSheetSets(@NonNull Object bean) throws IntrospectionException {
        return ProvidersUtil.sheetSetsOfBean(bean);
    }

    private static Sheet createSheet(List<Set> sets) {
        return ProvidersUtil.sheetOf(sets);
    }
}
