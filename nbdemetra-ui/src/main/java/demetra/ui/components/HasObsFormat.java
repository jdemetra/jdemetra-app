/*
 * Copyright 2017 National Bank of Belgium
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
package demetra.ui.components;

import demetra.ui.beans.PropertyChangeSource;
import ec.tss.tsproviders.utils.DataFormat;
import internal.ui.components.HasObsFormatImpl;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 *
 * @author Philippe Charles
 */
public interface HasObsFormat {

    static final String DATA_FORMAT_PROPERTY = "dataFormat";

    @Nullable
    DataFormat getDataFormat();

    void setDataFormat(@Nullable DataFormat dataFormat);

    @NonNull
    static HasObsFormat of(PropertyChangeSource.@NonNull Broadcaster broadcaster) {
        return new HasObsFormatImpl(broadcaster);
    }
}
