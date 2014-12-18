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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The DesktopManager is a utility class that allows you to retrieve a suitable
 * Desktop implementation.<p>By default, it uses {@link ServiceLoader} to get
 * the right implementation but you can override this behavior by calling
 * {@link DesktopManager#set(ec.util.desktop.Desktop)}.<br>If no implementation
 * is found, a no-operation implementation will be returned.<p>A Desktop
 * implementation is created by a {@link Factory}. If several factories are
 * available at runtime, this utility will choose the one that offers the best
 * support.
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
    public static synchronized void set(Desktop newDesktop) {
        if (newDesktop == null) {
            throw new NullPointerException("desktop");
        }
        DESKTOP = newDesktop;
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
    public static Desktop load(Iterable<? extends Factory> factories) {
        if (factories == null) {
            throw new NullPointerException("factories");
        }

        String osArch = System.getProperty("os.arch");
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");

        List<Entry<SupportType, Factory>> list = new ArrayList<>();
        for (Factory o : factories) {
            list.add(new AbstractMap.SimpleEntry<>(o.getSupportType(osArch, osName, osVersion), o));
        }

        if (list.isEmpty()) {
            LOGGER.info("No factories found");
            return new NoOpDesktop();
        }

        Collections.sort(list, DesktopManager.BestFactoryComparator.INSTANCE);
        Entry<SupportType, Factory> bestFactory = list.get(0);

        if (bestFactory.getKey() == SupportType.NONE) {
            LOGGER.info("No support for this OS");
            return new NoOpDesktop();
        }

        LOGGER.log(Level.INFO, "Using factory ''{0}''", bestFactory.getValue().getClass().getName());
        return bestFactory.getValue().create(osArch, osName, osVersion);
    }

    private DesktopManager() {
        // static class
    }

    /**
     * The comparator used to select the best factory through their level of
     * support.
     */
    private enum BestFactoryComparator implements Comparator<Entry<SupportType, Factory>> {

        INSTANCE;

        @Override
        public int compare(Entry<SupportType, Factory> l, Entry<SupportType, Factory> r) {
            return r.getKey().compareTo(l.getKey());
        }
    }
}
