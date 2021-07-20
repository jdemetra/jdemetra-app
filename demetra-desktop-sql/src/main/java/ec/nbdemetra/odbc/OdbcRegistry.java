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
package ec.nbdemetra.odbc;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import ec.tss.tsproviders.odbc.registry.IOdbcRegistry;
import ec.tss.tsproviders.odbc.registry.OdbcDataSource;
import ec.tss.tsproviders.odbc.registry.OdbcDriver;
import ec.tstoolkit.design.IntValue;
import ec.tstoolkit.design.VisibleForTesting;
import ec.util.desktop.impl.WinRegistry;
import static ec.util.desktop.impl.WinRegistry.Root.HKEY_CURRENT_USER;
import static ec.util.desktop.impl.WinRegistry.Root.HKEY_LOCAL_MACHINE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = IOdbcRegistry.class)
public final class OdbcRegistry implements IOdbcRegistry {

    private static final String DATA_SOURCES_KEY = "SOFTWARE\\ODBC\\ODBC.INI\\ODBC Data Sources";
    private static final String DATA_SOURCE_KEY = "SOFTWARE\\ODBC\\ODBC.INI";
    private static final String DRIVERS_KEY = "SOFTWARE\\ODBC\\Odbcinst.INI\\ODBC Drivers";
    private static final String DRIVER_KEY = "SOFTWARE\\ODBC\\Odbcinst.INI";

    private final WinRegistry registry;

    public OdbcRegistry() {
        this(WinRegistry.getDefault());
    }

    @VisibleForTesting
    OdbcRegistry(WinRegistry registry) {
        this.registry = registry;
    }

    @Override
    public List<OdbcDataSource> getDataSources(OdbcDataSource.Type... types) throws IOException {
        List<OdbcDataSource> result = new ArrayList<>();
        for (OdbcDataSource.Type type : types) {
            WinRegistry.Root root = getRoot(type);
            if (registry.keyExists(root, DATA_SOURCES_KEY)) {
                for (Entry<String, Object> ds : registry.getValues(root, DATA_SOURCES_KEY).entrySet()) {
                    String dataSourceKey = DATA_SOURCE_KEY + "\\" + ds.getKey();
                    if (registry.keyExists(root, dataSourceKey)) {
                        result.add(newDataSource(type, ds, registry.getValues(root, dataSourceKey)));
                    }
                }
            }
        }
        return result;
    }

    private static OdbcDataSource newDataSource(OdbcDataSource.Type type, Entry<String, Object> master, SortedMap<String, Object> details) {
        return new OdbcDataSource(type, master.getKey(),
                asString(details.get("Description")),
                asString(master.getValue()),
                asString(details.get("Driver")),
                asString(details.get("Server")));
    }

    private static WinRegistry.Root getRoot(OdbcDataSource.Type type) {
        switch (type) {
            case SYSTEM:
                return HKEY_LOCAL_MACHINE;
            case USER:
                return HKEY_CURRENT_USER;
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<OdbcDriver> getDrivers() throws IOException {
        List<OdbcDriver> result = new ArrayList<>();
        if (registry.keyExists(HKEY_LOCAL_MACHINE, DRIVERS_KEY)) {
            for (String name : registry.getValues(HKEY_LOCAL_MACHINE, DRIVERS_KEY).keySet()) {
                String driverKey = DRIVER_KEY + "\\" + name;
                if (registry.keyExists(HKEY_LOCAL_MACHINE, driverKey)) {
                    result.add(newDriver(name, registry.getValues(HKEY_LOCAL_MACHINE, driverKey)));
                }
            }
        }
        return result;
    }

    private static OdbcDriver newDriver(String name, SortedMap<String, Object> details) {
        return new OdbcDriver(name,
                parseEnum(details.get("APILevel"), OdbcDriver.ApiLevel.class, OdbcDriver.ApiLevel.NONE),
                OdbcDriver.ConnectFunctions.valueOf((String) asString(details.get("ConnectFunctions"))),
                asString(details.get("Driver")),
                asString(details.get("DriverOdbcVer")),
                readFileExtns(details.get("FileExtns")),
                parseEnum(details.get("FileUsage"), OdbcDriver.FileUsage.class, OdbcDriver.FileUsage.NONE),
                asString(details.get("Setup")),
                parseEnum(details.get("SQLLevel"), OdbcDriver.SqlLevel.class, OdbcDriver.SqlLevel.SQL_92_ENTRY),
                asInt(details.get("UsageCount"), -1));
    }

    private static String asString(Object o) {
        return o instanceof String ? (String) o : null;
    }

    private static int asInt(Object obj, int defaultValue) {
        return obj instanceof Integer ? (Integer) obj : defaultValue;
    }

    private static <Z extends Enum<Z> & IntValue> Z parseEnum(Object obj, Class<Z> clazz, Z defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        int value = Integer.parseInt(obj.toString());
        for (Z o : clazz.getEnumConstants()) {
            if (o.intValue() == value) {
                return o;
            }
        }
        return defaultValue;
    }

    private static ImmutableList<String> readFileExtns(Object obj) {
        if (obj == null) {
            return ImmutableList.of();
        }
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (String o : EXTNS_SPLITTER.split(obj.toString())) {
            builder.add(Files.getFileExtension(o));
        }
        return builder.build();
    }
    private static final Splitter EXTNS_SPLITTER = Splitter.on(',').omitEmptyStrings();
}
