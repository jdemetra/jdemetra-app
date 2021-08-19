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

import demetra.timeseries.TsMoniker;
import demetra.tsprovider.DataSet;
import demetra.tsprovider.DataSource;
import demetra.ui.properties.PropertySheetDialogBuilder;
import demetra.ui.util.NetBeansServiceBackend;
import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.List;
import nbbrd.service.Quantifier;
import nbbrd.service.ServiceDefinition;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.nodes.Sheet;

/**
 *
 * @author Philippe Charles
 * @since 1.0.0
 */
@ServiceDefinition(
        quantifier = Quantifier.MULTIPLE,
        backend = NetBeansServiceBackend.class,
        singleton = true
)
public interface IDataSourceProviderBuddy {

    @NonNull
    String getProviderName();

    @Nullable
    default Image getIcon(int type, boolean opened) {
        return null;
    }

    @Nullable
    default Image getIcon(@NonNull DataSource dataSource, int type, boolean opened) {
        return getIcon(type, opened);
    }

    @Nullable
    default Image getIcon(@NonNull DataSet dataSet, int type, boolean opened) {
        return getIcon(dataSet.getDataSource(), type, opened);
    }

    @Nullable
    default Image getIcon(@NonNull IOException ex, int type, boolean opened) {
        return null;
    }

    @Nullable
    default Image getIcon(@NonNull TsMoniker moniker, int type, boolean opened) {
        return getIcon(type, opened);
    }

    @NonNull
    default Sheet createSheet() {
        List<Sheet.Set> result = ProvidersUtil.sheetSetsOfProvider(getProviderName());
        return ProvidersUtil.sheetOf(result);
    }

    @NonNull
    default Sheet createSheet(@NonNull DataSource dataSource) {
        List<Sheet.Set> result = ProvidersUtil.sheetSetsOfDataSource(dataSource);
        return ProvidersUtil.sheetOf(result);
    }

    @NonNull
    default Sheet createSheet(@NonNull DataSet dataSet) {
        List<Sheet.Set> result = ProvidersUtil.sheetSetsOfDataSet(dataSet);
        return ProvidersUtil.sheetOf(result);
    }

    @NonNull
    default Sheet createSheet(@NonNull IOException ex) {
        List<Sheet.Set> result = ProvidersUtil.sheetSetsOfException(ex);
        return ProvidersUtil.sheetOf(result);
    }

    default boolean editBean(@NonNull String title, @NonNull Object bean) throws IntrospectionException {
        return new PropertySheetDialogBuilder()
                .title(title)
                .icon(getIcon(BeanInfo.ICON_COLOR_16x16, false))
                .editBean(bean);
    }
}
