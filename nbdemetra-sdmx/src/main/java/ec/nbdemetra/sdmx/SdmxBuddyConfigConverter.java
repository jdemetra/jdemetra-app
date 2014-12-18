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

import com.google.common.base.Converter;
import ec.nbdemetra.ui.Config;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;

/**
 *
 * @author Philippe Charles
 */
final class SdmxBuddyConfigConverter extends Converter<SdmxBuddyConfig, Config> {

    private final IParam<Config, Boolean> COMPACT_NAMING = Params.onBoolean(false, "compactNaming");
    private final IParam<Config, Boolean> KEYS_IN_META_DATA = Params.onBoolean(false, "keysInMetaData");

    @Override
    protected Config doForward(SdmxBuddyConfig a) {
        Config.Builder result = Config.builder(SdmxBuddyConfig.class.getName(), "INSTANCE", "20141710");
        COMPACT_NAMING.set(result, a.isCompactNaming());
        KEYS_IN_META_DATA.set(result, a.isKeysInMetaData());
        return result.build();
    }

    @Override
    protected SdmxBuddyConfig doBackward(Config b) {
        SdmxBuddyConfig result = new SdmxBuddyConfig();
        result.setCompactNaming(COMPACT_NAMING.get(b));
        result.setKeysInMetaData(KEYS_IN_META_DATA.get(b));
        return result;
    }
}
