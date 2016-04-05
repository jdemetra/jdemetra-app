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
package ec.nbdemetra.ui.tsproviders;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Ordering;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.nodes.Nodes;
import ec.nbdemetra.ui.interchange.Importable;
import static ec.nbdemetra.ui.tsproviders.ProviderNode.ACTION_PATH;
import ec.tss.datatransfer.DataSourceTransferSupport;
import ec.tss.tsproviders.*;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import javax.swing.Action;
import org.netbeans.api.actions.Openable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 * A node that represents a IDataSourceProvider.
 *
 * @author Philippe Charles
 */
@ActionReferences({
    @ActionReference(path = ACTION_PATH, position = 1310, separatorBefore = 1300, id = @ActionID(category = "File", id = "ec.nbdemetra.ui.actions.OpenAction")),
    @ActionReference(path = ACTION_PATH, position = 1420, separatorBefore = 1400, id = @ActionID(category = "Edit", id = "ec.nbdemetra.ui.nodes.actions.PasteProviderAction")),
    @ActionReference(path = ACTION_PATH, position = 1430, separatorBefore = 1400, id = @ActionID(category = "File", id = "ec.nbdemetra.ui.interchange.ImportAction")),
    @ActionReference(path = ACTION_PATH, position = 1520, separatorBefore = 1500, id = @ActionID(category = "File", id = "ec.nbdemetra.ui.actions.ConfigureAction"))
})
public final class ProviderNode extends AbstractNode {

    public static final String ACTION_PATH = "ProviderNode";

    public ProviderNode(@Nonnull IDataSourceProvider provider) {
        this(provider, new InstanceContent());
    }

    private ProviderNode(IDataSourceProvider provider, InstanceContent abilities) {
        // 1. Children and lookup
        super(Children.create(new ProviderChildFactory(provider), true),
                new ProxyLookup(Lookups.singleton(provider), new AbstractLookup(abilities)));
        // 2. Abilities
        {
            abilities.add(DataSourceProviderBuddySupport.getDefault().get(provider));
            if (provider instanceof IDataSourceLoader) {
                abilities.add(new OpenableImpl());
                abilities.add(new ImportableDataSource());
            }
        }
        // 3. Name and display name
        setName(provider.getSource());
        setDisplayName(provider.getDisplayName());
    }

    @Override
    public Action[] getActions(boolean context) {
        return Nodes.actionsForPath(ACTION_PATH);
    }

    @Override
    public Image getIcon(int type) {
        return getLookup().lookup(IDataSourceProviderBuddy.class).getIcon(type, false);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getLookup().lookup(IDataSourceProviderBuddy.class).getIcon(type, true);
    }

    @Override
    protected Sheet createSheet() {
        return getLookup().lookup(IDataSourceProviderBuddy.class).createSheet();
    }

    @Override
    public PasteType getDropType(Transferable t, int action, int index) {
        IDataSourceLoader loader = getLookup().lookup(IDataSourceLoader.class);
        if (loader != null && DataSourceTransferSupport.getDefault().canHandle(t, loader.getSource())) {
            return new PasteTypeImpl(t, loader);
        }
        return null;
    }

    public void paste(DataSource dataSource) {
        IDataSourceLoader loader = getLookup().lookup(IDataSourceLoader.class);
        Preconditions.checkArgument(loader != null);
        Preconditions.checkArgument(dataSource.getProviderName().equals(loader.getSource()));
        loader.open(dataSource);
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    private static final class ProviderChildFactory extends ChildFactory.Detachable<DataSource> implements IDataSourceListener {

        private final IDataSourceProvider provider;

        public ProviderChildFactory(IDataSourceProvider provider) {
            this.provider = provider;
        }

        @Override
        protected void addNotify() {
            provider.addDataSourceListener(this);
        }

        @Override
        protected void removeNotify() {
            provider.removeDataSourceListener(this);
        }

        @Override
        protected boolean createKeys(List<DataSource> list) {
            list.addAll(ON_TO_STRING.sortedCopy(provider.getDataSources()));
            return true;
        }

        @Override
        protected Node createNodeForKey(DataSource key) {
            return new DataSourceNode(key);
        }

        @Override
        public void opened(DataSource dataSource) {
            refresh(true);
        }

        @Override
        public void closed(DataSource dataSource) {
            refresh(true);
        }

        @Override
        public void changed(DataSource dataSource) {
            refresh(true);
        }

        @Override
        public void allClosed(String providerName) {
            refresh(true);
        }
    }

    private final class OpenableImpl implements Openable {

        @Override
        public void open() {
            IDataSourceLoader loader = getLookup().lookup(IDataSourceLoader.class);
            Object bean = loader.newBean();
            try {
                if (getLookup().lookup(IDataSourceProviderBuddy.class).editBean("Open data source", bean)) {
                    loader.open(loader.encodeBean(bean));
                }
            } catch (IntrospectionException ex) {
                throw Throwables.propagate(ex);
            }
        }
    }

    private final class ImportableDataSource implements Importable {

        @Override
        public String getDomain() {
            return ProvidersUtil.getDataSourceDomain();
        }

        @Override
        public void importConfig(Config config) throws IllegalArgumentException {
            DataSource dataSource = ProvidersUtil.getDataSource(config);
            Optional<IDataSourceLoader> loader = TsProviders.lookup(IDataSourceLoader.class, dataSource);
            if (loader.isPresent()) {
                loader.get().open(dataSource);
                Optional<Node> node = ProvidersUtil.findNode(dataSource, ProviderNode.this);
                if (node.isPresent()) {
                    node.get().setDisplayName(config.getName());
                }
            }
        }
    }

    private static final class PasteTypeImpl extends PasteType {

        private final Transferable t;
        private final IDataSourceLoader loader;

        public PasteTypeImpl(Transferable t, IDataSourceLoader loader) {
            this.t = t;
            this.loader = loader;
        }

        @Override
        public Transferable paste() throws IOException {
            Optional<DataSource> dataSource = DataSourceTransferSupport.getDefault().getDataSource(t, loader.getSource());
            if (dataSource.isPresent()) {
                loader.open(dataSource.get());
            }
            return null;
        }
    }

    private static final Ordering<DataSource> ON_TO_STRING = Ordering.natural().onResultOf(o -> o.toString());
}
