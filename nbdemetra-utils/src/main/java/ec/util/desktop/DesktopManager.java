/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package ec.util.desktop;

import ec.util.desktop.Desktop.Factory;
import ec.util.desktop.Desktop.Factory.SupportType;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;

/**
 * The DesktopManager is a utility class that allows you to retrieve a suitable
 * Desktop implementation.<p>
 * By default, it uses {@link ServiceLoader} to get the right implementation but
 * you can override this behavior by calling
 * {@link DesktopManager#set(ec.util.desktop.Desktop)}.<br>If no implementation
 * is found, a no-operation implementation will be returned.<p>
 * A Desktop implementation is created by a {@link Factory}. If several
 * factories are available at runtime, this utility will choose the one that
 * offers the best support.
 *
 * @see ServiceLoader
 * @see Factory
 * @see SupportType
 * @author Philippe Charles
 */
public final class DesktopManager {

    private static final Logger LOGGER = Logger.getLogger(DesktopManager.class.getName());
    private static Desktop DESKTOP;

    /**
     * Gets the current Desktop implementation. It is loaded at first call.
     *
     * @return the non-null current Desktop
     */
    @Nonnull
    public static synchronized Desktop get() {
        if (DESKTOP == null) {
            try {
                DESKTOP = load(ServiceLoader.load(Factory.class));
            } catch (java.util.ServiceConfigurationError ex) {
                LOGGER.log(Level.SEVERE, "While loading factories", ex);
                DESKTOP = new NoOpDesktop();
            }
        }
        return DESKTOP;
    }

    /**
     * Sets the current Desktop implementation.
     *
     * @param newDesktop a non-null new Desktop
     * @throws NullPointerException if the parameter is null
     */
    public static synchronized void set(@Nonnull Desktop newDesktop) {
        DESKTOP = Objects.requireNonNull(newDesktop, "desktop");
    }

    /**
     * Loads the best Desktop implementation from some factories.<br>Note that
     * it returns a no-operation implementation if there is no factory or if the
     * factories don't support the current platform.
     *
     * @param factories a non-null list of factories
     * @return a non-null Desktop implementation
     * @throws NullPointerException if the parameter is null
     */
    @Nonnull
    public static Desktop load(@Nonnull Iterable<? extends Factory> factories) {
        Objects.requireNonNull(factories, "factories");

        String osArch = System.getProperty("os.arch");
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");

        Optional<AbstractMap.SimpleEntry<SupportType, Factory>> bestFactory
                = StreamSupport.stream(factories.spliterator(), false)
                .map(o -> new AbstractMap.SimpleEntry<>(o.getSupportType(osArch, osName, osVersion), o))
                .sorted(DesktopManager::compareByLevelOfSupport)
                .findFirst();

        if (!bestFactory.isPresent()) {
            LOGGER.info("No factories found");
            return new NoOpDesktop();
        }

        if (bestFactory.get().getKey() == SupportType.NONE) {
            LOGGER.info("No support for this OS");
            return new NoOpDesktop();
        }

        LOGGER.log(Level.INFO, "Using factory ''{0}''", bestFactory.get().getValue().getClass().getName());
        return bestFactory.get().getValue().create(osArch, osName, osVersion);
    }

    private DesktopManager() {
        // static class
    }

    /**
     * The comparator used to select the best factory through their level of
     * support.
     */
    private static int compareByLevelOfSupport(Entry<SupportType, Factory> l, Entry<SupportType, Factory> r) {
        return r.getKey().compareTo(l.getKey());
    }
}
