/*
 * Copyright 2015 National Bank of Belgium
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
package internal.extra.sdmx;

import demetra.desktop.TsManager;
import demetra.desktop.completion.AutoCompletionSpi;
import demetra.tsp.extra.sdmx.web.SdmxWebProvider;
import ec.util.completion.swing.JAutoCompletion;
import javax.swing.text.JTextComponent;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;
import sdmxdl.web.SdmxWebSource;

/**
 *
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider
public final class SdmxWebSourceService implements AutoCompletionSpi {

    @Override
    public String getPath() {
        return SdmxWebSource.class.getName();
    }

    @Override
    public JAutoCompletion bind(JTextComponent textComponent) {
        JAutoCompletion result = new JAutoCompletion(textComponent);
        result.setMinLength(0);
        TsManager.get()
                .getProvider(SdmxWebProvider.class)
                .ifPresent(provider -> {
                    SdmxAutoCompletion c = SdmxAutoCompletion.onWebSource(provider.getSdmxManager());
                    result.setSource(c.getSource());
                    result.getList().setCellRenderer(c.getRenderer());
                });
        return result;
    }
}
