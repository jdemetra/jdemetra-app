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
package internal.extra.sdmx.web;

import demetra.desktop.completion.AutoCompletionSpi;
import demetra.tsp.extra.sdmx.web.SdmxWebProvider;
import sdmxdl.web.SdmxWebManager;
import ec.util.completion.swing.JAutoCompletion;
import internal.extra.sdmx.SdmxAutoCompletion;
import java.util.Optional;
import javax.swing.text.JTextComponent;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;
import org.openide.util.Lookup;
import sdmxdl.web.SdmxWebSource;

/**
 *
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider
public final class SdmxWebSourceAutoCompletionService implements AutoCompletionSpi {

    @Override
    public String getPath() {
        return SdmxWebSource.class.getName();
    }

    @Override
    public JAutoCompletion bind(JTextComponent textComponent) {
        JAutoCompletion result = new JAutoCompletion(textComponent);
        result.setMinLength(0);
        lookupManager().ifPresent(manager -> {
            result.setSource(SdmxAutoCompletion.onSources(manager));
            result.getList().setCellRenderer(SdmxAutoCompletion.getSourceRenderer(manager));
        });
        return result;
    }

    private Optional<SdmxWebManager> lookupManager() {
        return Optional.ofNullable(Lookup.getDefault().lookup(SdmxWebProvider.class))
                .map(SdmxWebProvider::getSdmxManager);
    }
}
