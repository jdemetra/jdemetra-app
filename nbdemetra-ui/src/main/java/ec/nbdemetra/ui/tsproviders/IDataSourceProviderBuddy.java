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
import ec.tss.TsMoniker;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tstoolkit.design.ServiceDefinition;
import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openide.nodes.Sheet;

/**
 *
 * @author Philippe Charles
 * @since 1.0.0
 */
@ServiceDefinition
public interface IDataSourceProviderBuddy {

    @Nonnull
    String getProviderName();

    @Nullable
    default Image getIcon(int type, boolean opened) {
        return null;
    }

    @Nullable
    default Image getIcon(@Nonnull DataSource dataSource, int type, boolean opened) {
        return getIcon(type, opened);
    }

    @Nullable
    default Image getIcon(@Nonnull DataSet dataSet, int type, boolean opened) {
        return getIcon(dataSet.getDataSource(), type, opened);
    }

    @Nullable
    default Image getIcon(@Nonnull IOException ex, int type, boolean opened) {
        return null;
    }

    @Nullable
    default Image getIcon(@Nonnull TsMoniker moniker, int type, boolean opened) {
        return getIcon(type, opened);
    }

    @Nonnull
    default Sheet createSheet() {
        List<Sheet.Set> result = ProvidersUtil.sheetSetsOfProvider(getProviderName());
        return ProvidersUtil.sheetOf(result);
    }

    @Nonnull
    default Sheet createSheet(@Nonnull DataSource dataSource) {
        List<Sheet.Set> result = ProvidersUtil.sheetSetsOfDataSource(dataSource);
        return ProvidersUtil.sheetOf(result);
    }

    @Nonnull
    default Sheet createSheet(@Nonnull DataSet dataSet) {
        List<Sheet.Set> result = ProvidersUtil.sheetSetsOfDataSet(dataSet);
        return ProvidersUtil.sheetOf(result);
    }

    @Nonnull
    default Sheet createSheet(@Nonnull IOException ex) {
        List<Sheet.Set> result = ProvidersUtil.sheetSetsOfException(ex);
        return ProvidersUtil.sheetOf(result);
    }

    default boolean editBean(@Nonnull String title, @Nonnull Object bean) throws IntrospectionException {
        return new PropertySheetDialogBuilder()
                .title(title)
                .icon(getIcon(BeanInfo.ICON_COLOR_16x16, false))
                .editBean(bean);
    }
}
