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
package demetra.ui;

import demetra.desktop.design.GlobalService;
import demetra.timeseries.TsMoniker;
import demetra.ui.util.LazyGlobalService;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import javax.swing.Icon;

/**
 * TODO: improve this API
 *
 * @author Philippe Charles
 */
@GlobalService
public final class TsMonikerUI {

    @NonNull
    public static TsMonikerUI getDefault() {
        return LazyGlobalService.get(TsMonikerUI.class, TsMonikerUI::new);
    }

    private TsMonikerUI() {
    }

    @Nullable
    public Icon getIcon(@NonNull TsMoniker moniker) {
        return TsMonikerUISpiLoader.get()
                .map(provider -> provider.getIconOrNull(moniker))
                .orElse(null);
    }
}
