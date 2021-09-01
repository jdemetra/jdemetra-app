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
package demetra.desktop.core.interchange;

import demetra.desktop.interchange.Exportable;
import demetra.desktop.interchange.Importable;
import ec.util.desktop.Desktop;
import ec.util.desktop.DesktopManager;
import ec.util.desktop.MailtoBuilder;
import java.io.IOException;
import java.util.List;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;
import org.checkerframework.checker.nullness.qual.NonNull;
import demetra.desktop.interchange.InterchangeSpi;

/**
 *
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider
public final class MailBroker implements InterchangeSpi {

    @Override
    public int getPosition() {
        return 200;
    }

    @Override
    public String getName() {
        return "Mail";
    }

    private Desktop getDesktop() {
        return DesktopManager.get();
    }

    @Override
    public boolean canExport(List<? extends Exportable> exportables) {
        return !exportables.isEmpty() && getDesktop().isSupported(Desktop.Action.MAIL);
    }

    @Override
    public void performExport(List<? extends Exportable> exportables) throws IOException {
        Configs configs = Configs.fromExportables(exportables);
        store(getDesktop(), configs);
    }

    private static void store(@NonNull Desktop desktop, @NonNull Configs configs) throws IOException {
        String subject = configs.getItems().size() == 1 ? configs.getItems().get(0).getName() : "Configs";
        String body = Configs.xmlFormatter(true).formatToString(configs);
        desktop.mail(new MailtoBuilder().subject(subject).body(body).build());
    }

    @Override
    public boolean canImport(List<? extends Importable> importables) {
        return false;
    }

    @Override
    public void performImport(List<? extends Importable> importables) throws IOException, IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
