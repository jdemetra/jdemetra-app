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

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import ec.nbdemetra.db.DbIcon;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.awt.SimpleHtmlListCellRenderer;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.tsproviders.IDataSourceProviderBuddy;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.TsProviders;
import ec.tss.tsproviders.db.DbBean;
import ec.tss.tsproviders.jdbc.ConnectionSupplier;
import ec.tss.tsproviders.jdbc.JdbcBean;
import ec.tss.tsproviders.jdbc.jndi.JndiJdbcProvider;
import ec.tss.tsproviders.utils.Formatters;
import ec.tss.tsproviders.utils.Parsers;
import static ec.util.chart.impl.TangoColorScheme.DARK_ORANGE;
import static ec.util.chart.impl.TangoColorScheme.DARK_SCARLET_RED;
import static ec.util.chart.swing.SwingColorSchemeSupport.rgbToColor;
import ec.util.completion.AutoCompletionSource;
import ec.util.completion.ExtAutoCompletionSource;
import ec.util.jdbc.ForwardingConnection;
import ec.util.various.swing.FontAwesome;
import java.awt.EventQueue;
import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.db.explorer.DatabaseException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = IDataSourceProviderBuddy.class)
public class JndiJdbcProviderBuddy extends JdbcProviderBuddy<JdbcBean> implements IConfigurable {

    private final static Config EMPTY = Config.builder("", "", "").build();
    private final AutoCompletionSource dbSource;
    private final ListCellRenderer dbRenderer;
    private final Image warningBadge;
    private final Image errorBadge;

    public JndiJdbcProviderBuddy() {
        super(new DbExplorerConnectionSupplier());
        this.dbSource = dbExplorerSource();
        this.dbRenderer = new SimpleHtmlListCellRenderer<>((DatabaseConnection o) -> "<html><b>" + o.getDisplayName() + "</b> - <i>" + o.getName() + "</i>");
        this.warningBadge = FontAwesome.FA_EXCLAMATION_TRIANGLE.getImage(rgbToColor(DARK_ORANGE), 8f);
        this.errorBadge = FontAwesome.FA_EXCLAMATION_CIRCLE.getImage(rgbToColor(DARK_SCARLET_RED), 8f);
        overrideDefaultConnectionSupplier();
    }

    // this overrides default connection supplier since we don't have JNDI in JavaSE
    private void overrideDefaultConnectionSupplier() {
        Optional<JndiJdbcProvider> provider = TsProviders.lookup(JndiJdbcProvider.class, JndiJdbcProvider.SOURCE);
        if (provider.isPresent()) {
            provider.get().setConnectionSupplier(supplier);
        }
    }

    @Override
    public String getProviderName() {
        return JndiJdbcProvider.SOURCE;
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return DbIcon.DATABASE.getImageIcon().getImage();
    }

    @Override
    public Image getIcon(DataSource dataSource, int type, boolean opened) {
        Image image = super.getIcon(dataSource, type, opened);
        String dbName = DbBean.X_DBNAME.get(dataSource);
        switch (DbConnStatus.lookupByDisplayName(dbName)) {
            case DISCONNECTED:
                return ImageUtilities.mergeImages(image, warningBadge, 8, 8);
            case MISSING:
                return ImageUtilities.mergeImages(image, errorBadge, 8, 8);
        }
        return image;
    }

    @Override
    protected List<Sheet.Set> createSheetSets(DataSource dataSource) {
        List<Sheet.Set> result = super.createSheetSets(dataSource);
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("Connection");
        b.add(new DbConnStatusProperty(DbBean.X_DBNAME.get(dataSource)));
        result.add(b.build());
        return result;
    }

    @Override
    protected boolean isFile() {
        return false;
    }

    @Override
    protected AutoCompletionSource getDbSource(JdbcBean bean) {
        return dbSource;
    }

    @Override
    protected ListCellRenderer getDbRenderer(JdbcBean bean) {
        return dbRenderer;
    }

    //<editor-fold defaultstate="collapsed" desc="Config methods">
    @Override
    public Config getConfig() {
        return EMPTY;
    }

    @Override
    public void setConfig(Config config) throws IllegalArgumentException {
        Preconditions.checkArgument(config.equals(EMPTY));
    }

    @Override
    public Config editConfig(Config config) throws IllegalArgumentException {
        final Action openServicesTab = FileUtil.getConfigObject("Actions/Window/org-netbeans-core-ide-ServicesTabAction.instance", Action.class);
        if (openServicesTab != null) {
            EventQueue.invokeLater(() -> openServicesTab.actionPerformed(null));
        }
        return EMPTY;
    }
    //</editor-fold>

    private enum DbConnStatus {

        CONNECTED,
        DISCONNECTED,
        MISSING;

        public static DbConnStatus lookupByDisplayName(String dbName) {
            Optional<DatabaseConnection> conn = DbExplorerUtil.getConnectionByDisplayName(dbName);
            if (conn.isPresent()) {
                return DbExplorerUtil.isConnected(conn.get()) ? CONNECTED : DISCONNECTED;
            }
            return MISSING;
        }
    }

    private static AutoCompletionSource dbExplorerSource() {
        return ExtAutoCompletionSource
                .builder(JndiJdbcProviderBuddy::getDatabaseConnections)
                .behavior(AutoCompletionSource.Behavior.ASYNC)
                .postProcessor(JndiJdbcProviderBuddy::getDatabaseConnections)
                .valueToString(DatabaseConnection::getDisplayName)
                .build();
    }

    private static List<DatabaseConnection> getDatabaseConnections() {
        return Arrays.asList(ConnectionManager.getDefault().getConnections());
    }

    private static List<DatabaseConnection> getDatabaseConnections(List<DatabaseConnection> values, String term) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return values.stream()
                .filter(o -> filter.test(o.getName()) || filter.test(o.getDisplayName()))
                .sorted(Comparator.comparing(DatabaseConnection::getDisplayName))
                .collect(Collectors.toList());
    }

    @NbBundle.Messages({
        "# {0} - dbName",
        "dbexplorer.missingConnection=Cannot find connection named ''{0}''",
        "# {0} - dbName",
        "dbexplorer.noConnection=Not connected to the database ''{0}''",
        "# {0} - dbName",
        "dbexplorer.failedConnection=Failed to connect to the database ''{0}''",
        "# {0} - dbName",
        "dbexplorer.requestConnection=A process request a connection to the database ''{0}''.\nDo you want to open the connection dialog?",
        "dbexplorer.skip=Don't ask again"
    })
    private static final class DbExplorerConnectionSupplier implements ConnectionSupplier {

        private final Properties map = new Properties();

        @Override
        public Connection getConnection(JdbcBean bean) throws SQLException {
            return getConnectionByName(bean.getDbName());
        }

        private Connection getConnectionByName(String dbName) throws SQLException {
            Optional<DatabaseConnection> o = DbExplorerUtil.getConnectionByDisplayName(dbName);
            if (o.isPresent()) {
                return getJDBCConnection(o.get());
            }
            throw new SQLException(Bundle.dbexplorer_missingConnection(dbName));
        }

        private FailSafeConnection getJDBCConnection(DatabaseConnection o) throws SQLException {
            Connection conn = o.getJDBCConnection();
            if (conn != null) {
                return new FailSafeConnection(conn, o.getSchema());
            }
            if (connect(o) || connectWithDialog(o)) {
                return new FailSafeConnection(o.getJDBCConnection(), o.getSchema());
            }
            throw new SQLException(Bundle.dbexplorer_noConnection(o.getDisplayName()));
        }

        private boolean connect(DatabaseConnection o) throws SQLException {
            // let's try to connect without opening any dialog
            // must be done outside EDT
            if (!SwingUtilities.isEventDispatchThread()) {
                try {
                    // currently, the manager returns false if user is empty
                    // this behavior prevents some connections but it might be fixed in further versions
                    return ConnectionManager.getDefault().connect(o);
                } catch (DatabaseException ex) {
                    throw new SQLException(Bundle.dbexplorer_failedConnection(o.getDisplayName()), ex);
                }
            }
            return false;
        }

        private boolean connectWithDialog(final DatabaseConnection o) {
            try {
                return execInEDT(() -> {
                    String dbName = o.getDisplayName();
                    if (!isSkip(dbName)) {
                        JCheckBox checkBox = new JCheckBox(Bundle.dbexplorer_skip(), false);
                        Object[] msg = {Bundle.dbexplorer_requestConnection(dbName), checkBox};
                        Object option = DialogDisplayer.getDefault().notify(new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.YES_NO_OPTION));
                        setSkip(dbName, checkBox.isSelected());
                        if (option == NotifyDescriptor.YES_OPTION) {
                            ConnectionManager.getDefault().showConnectionDialog(o);
                            return o.getJDBCConnection() != null;
                        }
                    }
                    return false;
                });
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            return false;
        }

        boolean isSkip(String dbName) {
            return Parsers.boolParser().parse(map.getProperty(dbName, Boolean.FALSE.toString()));
        }

        void setSkip(String dbName, boolean remember) {
            map.setProperty(dbName, Formatters.boolFormatter().formatAsString(remember));
        }
    }

    private static <T> T execInEDT(Callable<T> callable) throws Exception {
        if (!SwingUtilities.isEventDispatchThread()) {
            RunnableFuture<T> x = new FutureTask(callable);
            SwingUtilities.invokeAndWait(x);
            return x.get();
        } else {
            return callable.call();
        }
    }

    private static final class FailSafeConnection extends ForwardingConnection {

        private final Connection delegate;
        private final String defaultSchema;

        public FailSafeConnection(@Nonnull Connection delegate, String defaultSchema) {
            this.delegate = delegate;
            this.defaultSchema = defaultSchema;
        }

        @Override
        protected Connection getConnection() {
            return delegate;
        }

        @Override
        public String getSchema() throws SQLException {
            try {
                String result = super.getSchema();
                return result != null ? result : defaultSchema;
            } catch (SQLException | AbstractMethodError ex) {
                // occurs when :
                // - method is not yet implemented
                // - driver follows specs older than JDBC4 specs
                return defaultSchema;
            }
        }

        @Override
        public void close() throws SQLException {
            // Calling Connection#close() is forbidden
            // See DatabaseConnection#getJDBCConnection()
        }
    }

    private static final class DbConnStatusProperty extends PropertySupport.ReadOnly<String> {

        private final String dbName;

        public DbConnStatusProperty(String dbName) {
            super("Status", String.class, null, null);
            this.dbName = dbName;
        }

        @Override
        public String getValue() throws IllegalAccessException, InvocationTargetException {
            return DbConnStatus.lookupByDisplayName(dbName).name();
        }
    }
}
