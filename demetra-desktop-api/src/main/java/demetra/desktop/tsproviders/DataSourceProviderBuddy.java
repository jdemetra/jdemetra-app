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

import demetra.desktop.beans.BeanEditor;
import demetra.desktop.util.NetBeansServiceBackend;
import demetra.timeseries.TsMoniker;
import demetra.tsprovider.DataSet;
import demetra.tsprovider.DataSource;
import nbbrd.service.Quantifier;
import nbbrd.service.ServiceDefinition;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.nodes.Sheet;

import java.awt.*;
import java.beans.IntrospectionException;
import java.io.IOException;

/**
 * @author Philippe Charles
 * @since 1.0.0
 */
@ServiceDefinition(
        quantifier = Quantifier.MULTIPLE,
        backend = NetBeansServiceBackend.class,
        singleton = true
)
public interface DataSourceProviderBuddy {

    @NonNull
    String getProviderName();

    @Nullable
    default Image getIconOrNull(int type, boolean opened) {
        return null;
    }

    @Nullable
    default Image getIconOrNull(@NonNull DataSource dataSource, int type, boolean opened) {
        return null;
    }

    @Nullable
    default Image getIconOrNull(@NonNull DataSet dataSet, int type, boolean opened) {
        return null;
    }

    @Nullable
    default Image getIconOrNull(@NonNull IOException ex, int type, boolean opened) {
        return null;
    }

    @Nullable
    default Image getIconOrNull(@NonNull TsMoniker moniker, int type, boolean opened) {
        return null;
    }

    @Nullable
    default Sheet getSheetOrNull() {
        return null;
    }

    @Nullable
    default Sheet getSheetOrNull(@NonNull DataSource dataSource) {
        return null;
    }

    @Nullable
    default Sheet getSheetOrNull(@NonNull DataSet dataSet) {
        return null;
    }

    @Nullable
    default Sheet getSheetOrNull(@NonNull IOException ex) {
        return null;
    }

    @Nullable
    default Sheet getSheetOfBeanOrNull(@NonNull Object bean) throws IntrospectionException {
        return null;
    }

    @Nullable
    default BeanEditor getBeanEditorOrNull(@NonNull String title) {
        return null;
    }
}
