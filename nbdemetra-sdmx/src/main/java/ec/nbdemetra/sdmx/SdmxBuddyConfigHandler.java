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
package ec.nbdemetra.sdmx;

import com.google.common.base.Optional;
import ec.nbdemetra.ui.BeanHandler;
import ec.tss.tsproviders.TsProviders;
import ec.tss.tsproviders.sdmx.SdmxProvider;

/**
 *
 * @author Philippe Charles
 */
final class SdmxBuddyConfigHandler extends BeanHandler<SdmxBuddyConfig, SdmxProviderBuddy> {

    @Override
    public SdmxBuddyConfig loadBean(SdmxProviderBuddy resource) {
        SdmxBuddyConfig result = new SdmxBuddyConfig();
        Optional<SdmxProvider> loader = TsProviders.lookup(SdmxProvider.class, SdmxProvider.SOURCE);
        if (loader.isPresent()) {
            result.setCompactNaming(loader.get().isCompactNaming());
            result.setKeysInMetaData(loader.get().isKeysInMetaData());
        }
        return result;
    }

    @Override
    public void storeBean(SdmxProviderBuddy resource, SdmxBuddyConfig bean) {
        Optional<SdmxProvider> loader = TsProviders.lookup(SdmxProvider.class, SdmxProvider.SOURCE);
        if (loader.isPresent()) {
            loader.get().setCompactNaming(bean.isCompactNaming());
            loader.get().setKeysInMetaData(bean.isKeysInMetaData());
        }
    }
}
