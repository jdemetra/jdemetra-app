/*
 * Copyright 2015 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved
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
package demetra.ui.components.parts;

import demetra.timeseries.TsData;
import demetra.ui.beans.PropertyChangeBroadcaster;
import demetra.desktop.design.SwingProperty;
import internal.ui.components.parts.HasTsDataImpl;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 *
 * @author Philippe Charles
 */
public interface HasTsData {

    @SwingProperty
    String TS_DATA_PROPERTY = "tsData";

    @Nullable
    TsData getTsData();

    void setTsData(@Nullable TsData tsData);

    @NonNull
    static HasTsData of(@NonNull PropertyChangeBroadcaster support) {
        return new HasTsDataImpl(support);
    }
}
