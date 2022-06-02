/*
 * Copyright 2021 National Bank of Belgium
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
package demetra.desktop.components;

import demetra.desktop.design.GlobalService;
import demetra.desktop.util.CollectionSupplier;
import demetra.desktop.util.LazyGlobalService;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;
import java.util.Objects;

/**
 *
 * @author Philippe Charles
 */
@GlobalService
public final class ComponentBackendManager {

    public static ComponentBackendManager get() {
        return LazyGlobalService.get(ComponentBackendManager.class, ComponentBackendManager::new);
    }

    private ComponentBackendManager() {
    }

    private final CollectionSupplier<ComponentBackendSpi> providers = ComponentBackendSpiLoader::get;

    public <T extends JComponent> void install(@NonNull T component) {
        Objects.requireNonNull(component);
        providers.stream()
                .filter(provider -> provider.handles(component.getClass()))
                .findFirst()
                .ifPresent(provider -> provider.install(component));
    }
}
