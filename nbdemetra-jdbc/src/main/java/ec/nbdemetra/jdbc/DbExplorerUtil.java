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
package ec.nbdemetra.jdbc;

import java.util.Optional;
import java.util.Properties;
import javax.annotation.Nonnull;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.api.db.explorer.JDBCDriver;
import org.netbeans.api.db.explorer.JDBCDriverManager;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * http://code.metager.de/source/xref/netbeans/db/src/org/netbeans/modules/db/resources/mf-layer.xml
 *
 * @author Philippe Charles
 */
public final class DbExplorerUtil {

    private DbExplorerUtil() {
        // static class
    }

    static boolean isConnected(@Nonnull DatabaseConnection conn) {
        return conn.getJDBCConnection() != null;
    }

    static boolean isTableOrView(@Nonnull Node node) {
        return lookupContains(node.getLookup(),
                "org.netbeans.modules.db.explorer.node.TableNode",
                "org.netbeans.modules.db.explorer.node.ViewNode");
    }

    // some part of the db api is private; we need to cheat a bit
    private static boolean lookupContains(Lookup lookup, String... classNames) {
        for (Lookup.Item<Object> o : lookup.lookupResult(Object.class).allItems()) {
            for (String className : classNames) {
                if (o.getType().getName().equals(className)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nonnull
    static Optional<DatabaseConnection> findConnection(@Nonnull Node node) {
        DatabaseConnection result = null;
        Node current = node;
        while (current != null && (result = current.getLookup().lookup(DatabaseConnection.class)) == null) {
            current = current.getParentNode();
        }
        return Optional.ofNullable(result);
    }

    @Nonnull
    static Optional<JDBCDriver> getDriverByClass(@Nonnull String driverClass) {
        for (JDBCDriver o : JDBCDriverManager.getDefault().getDrivers(driverClass)) {
            return Optional.of(o);
        }
        return Optional.empty();
    }

    @Nonnull
    static Optional<DatabaseConnection> getConnectionByDisplayName(@Nonnull String displayName) {
        for (DatabaseConnection o : ConnectionManager.getDefault().getConnections()) {
            if (o.getDisplayName().equals(displayName)) {
                return Optional.of(o);
            }
        }
        return Optional.empty();
    }

    public static void importConnection(@Nonnull DriverBasedConfig config) {
        Optional<JDBCDriver> driver = getDriverByClass(config.getDriverClass());
        if (!driver.isPresent()) {
            String msg = "Cannot find driver '" + config.getDriverClass() + "'";
            DialogDisplayer.getDefault().notify(new DialogDescriptor.Message(msg, DialogDescriptor.ERROR_MESSAGE));
            return;
        }
        Optional<DatabaseConnection> conn = getConnectionByDisplayName(config.getDisplayName());
        if (conn.isPresent()) {
            String msg = "A connection with the same name already exist: '" + config.getDisplayName() + "'";
            DialogDisplayer.getDefault().notify(new DialogDescriptor.Message(msg, DialogDescriptor.ERROR_MESSAGE));
            return;
        }
        Properties properties = new Properties();
        config.forEach(properties::put);
        DatabaseConnection newConn = DatabaseConnection.create(driver.get(), config.getDatabaseUrl(), "", config.getSchema(), "", false, config.getDisplayName(), properties);
        try {
            ConnectionManager.getDefault().addConnection(newConn);
        } catch (DatabaseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Nonnull
    public static DriverBasedConfig exportConnection(@Nonnull DatabaseConnection conn) {
        DriverBasedConfig.Builder b = DriverBasedConfig.builder(conn.getDriverClass(), conn.getDatabaseURL(), conn.getSchema(), conn.getDisplayName());
        Properties properties = conn.getConnectionProperties();
        for (String o : properties.stringPropertyNames()) {
            b.put(o, properties.getProperty(o));
        }
        return b.build();
    }
}
