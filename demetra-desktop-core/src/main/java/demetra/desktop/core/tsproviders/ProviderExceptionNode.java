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
package demetra.desktop.core.tsproviders;

import demetra.desktop.TsManager;
import demetra.desktop.nodes.ExceptionNode;
import demetra.desktop.tsproviders.DataSourceManager;
import demetra.tsprovider.DataSourceProvider;
import org.openide.nodes.Sheet;

import java.awt.*;
import java.io.IOException;

/**
 * @author Philippe Charles
 */
final class ProviderExceptionNode extends ExceptionNode {

    private final String providerName;

    public ProviderExceptionNode(IOException ex, String providerName) {
        super(ex);
        this.providerName = providerName;
    }

    @Override
    public String getHtmlDisplayName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        IOException ex = getLookup().lookup(IOException.class);
        return TsManager.get().getProvider(DataSourceProvider.class, providerName).get().getDisplayName(ex);
    }

    private Image lookupIcon(int type, boolean opened) {
        IOException o = getLookup().lookup(IOException.class);
        return DataSourceManager.get().getImage(providerName, o, type, opened);
    }

    @Override
    public Image getIcon(int type) {
        return lookupIcon(type, false);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return lookupIcon(type, true);
    }

    @Override
    protected Sheet createSheet() {
        IOException ex = getLookup().lookup(IOException.class);
        return DataSourceManager.get().getSheet(providerName, ex);
    }
}
