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
import ec.util.various.swing.FontAwesome;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import javax.swing.Icon;
import java.awt.*;

/**
 * TODO: improve this API
 *
 * @author Philippe Charles
 */
@GlobalService
public final class IconManager {

    @NonNull
    public static IconManager getDefault() {
        return LazyGlobalService.get(IconManager.class, IconManager::new);
    }

    private IconManager() {
    }

    @Nullable
    public Icon getIcon(@NonNull TsMoniker moniker) {
        return IconManagerSpiLoader.get()
                .map(provider -> provider.getIconOrNull(moniker))
                .orElse(null);
    }

    @Deprecated
    public @Nullable Icon getPopupMenuIcon(@Nullable Icon icon) {
        return DemetraOptions.getDefault().isPopupMenuIconsVisible() ? icon : null;
    }

    @Deprecated
    public Icon getPopupMenuIcon(FontAwesome icon) {
        return DemetraOptions.getDefault().isPopupMenuIconsVisible() ? icon.getIcon(Color.BLACK, 13f) : null;
    }
}
