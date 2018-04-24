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
package ec.nbdemetra.ui;

import ec.nbdemetra.core.GlobalService;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import ec.tss.TsMoniker;
import java.beans.BeanInfo;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * TODO: improve this API
 *
 * @author Philippe Charles
 */
@GlobalService
@ServiceProvider(service = MonikerUI.class)
public class MonikerUI {

    @Nonnull
    public static MonikerUI getDefault() {
        return Lookup.getDefault().lookup(MonikerUI.class);
    }

    @Nullable
    public Icon getIcon(@Nonnull TsMoniker moniker) {
        return DataSourceProviderBuddySupport.getDefault()
                .getIcon(moniker, BeanInfo.ICON_COLOR_16x16, false)
                .map(ImageUtilities::image2Icon)
                .orElse(null);
    }
}
