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

import demetra.desktop.Config;
import demetra.desktop.TsManager;
import demetra.desktop.core.actions.OpenNodeAction;
import demetra.desktop.core.interchange.ImportNodeAction;
import demetra.desktop.datatransfer.DataSourceTransfer;
import demetra.desktop.interchange.Importable;
import demetra.desktop.nodes.Nodes;
import demetra.desktop.tsproviders.DataSourceProviderBuddySupport;
import demetra.desktop.tsproviders.ProvidersUtil;
import demetra.tsprovider.DataSource;
import demetra.tsprovider.DataSourceListener;
import demetra.tsprovider.DataSourceLoader;
import demetra.tsprovider.DataSourceProvider;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.netbeans.api.actions.Openable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.nodes.*;
import org.openide.util.Exceptions;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static demetra.desktop.tsproviders.TsProviderNodes.PROVIDER_ACTION_PATH;

/**
 * A node that represents a IDataSourceProvider.
 *
 * @author Philippe Charles
 */
@ActionReferences({
        @ActionReference(path = PROVIDER_ACTION_PATH, separatorBefore = 300, position = 310, id = @ActionID(category = "File", id = OpenNodeAction.ID)),
        @ActionReference(path = PROVIDER_ACTION_PATH, separatorBefore = 400, position = 420, id = @ActionID(category = "Edit", id = PasteProviderNodeAction.ID)),
        @ActionReference(path = PROVIDER_ACTION_PATH, separatorBefore = 400, position = 430, id = @ActionID(category = "File", id = ImportNodeAction.ID)),
        @ActionReference(path = PROVIDER_ACTION_PATH, separatorBefore = 500, position = 520, id = @ActionID(category = "File", id = "ec.nbdemetra.ui.actions.ConfigureAction"))
})
public final class ProviderNode extends AbstractNode {

    public ProviderNode(@NonNull DataSourceProvider provider) {
        this(provider, new InstanceContent());
    }

    private ProviderNode(DataSourceProvider provider, InstanceContent abilities) {
        // 1. Children and lookup
        super(Children.create(new ProviderChildFactory(provider), true),
                new ProxyLookup(Lookups.singleton(provider), new AbstractLookup(abilities)));
        // 2. Abilities
        {
            if (provider instanceof DataSourceLoader) {
                abilities.add(new OpenableImpl());
                abilities.add(new ImportableDataSource());
            }
            DataSourceProviderBuddySupport.getDefault()
                    .getConfigurable(provider.getSource())
                    .ifPresent(abilities::add);
        }
        // 3. Name and display name
        setName(provider.getSource());
        setDisplayName(provider.getDisplayName());
    }

    @Override
    public Action[] getActions(boolean context) {
        return Nodes.actionsForPath(PROVIDER_ACTION_PATH);
    }

    private Image lookupIcon(int type, boolean opened) {
        DataSourceProvider o = getLookup().lookup(DataSourceProvider.class);
        return DataSourceProviderBuddySupport.getDefault().getImage(o.getSource(), type, opened);
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
        DataSourceProvider o = getLookup().lookup(DataSourceProvider.class);
        return DataSourceProviderBuddySupport.getDefault().createSheet(o.getSource());
    }

    @Override
    public PasteType getDropType(Transferable t, int action, int index) {
        DataSourceLoader loader = getLookup().lookup(DataSourceLoader.class);
        if (loader != null && DataSourceTransfer.getDefault().canHandle(t, loader.getSource())) {
            return new PasteTypeImpl(t, loader);
        }
        return null;
    }

    public void paste(DataSource dataSource) {
        DataSourceLoader loader = getLookup().lookup(DataSourceLoader.class);
        if (loader == null) {
            throw new IllegalArgumentException();
        }
        if (!dataSource.getProviderName().equals(loader.getSource())) {
            throw new IllegalArgumentException();
        }
        loader.open(dataSource);
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    private static final class ProviderChildFactory extends ChildFactory.Detachable<DataSource> implements DataSourceListener {

        private final DataSourceProvider provider;

        public ProviderChildFactory(DataSourceProvider provider) {
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
            provider.getDataSources().stream()
                    .sorted(Comparator.comparing(Object::toString))
                    .forEach(list::add);
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
            DataSourceLoader loader = getLookup().lookup(DataSourceLoader.class);
            Object bean = loader.newBean();
            if (DataSourceProviderBuddySupport.getDefault().getBeanEditor(loader.getSource(), "Open data source").editBean(bean, Exceptions::printStackTrace)) {
                loader.open(loader.encodeBean(bean));
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
            TsManager.getDefault()
                    .getProvider(DataSourceLoader.class, dataSource)
                    .ifPresent(provider -> {
                        provider.open(dataSource);
                        ProvidersNode.findNode(dataSource, ProviderNode.this)
                                .ifPresent(node -> node.setDisplayName(config.getName()));
                    });
        }
    }

    private static final class PasteTypeImpl extends PasteType {

        private final Transferable t;
        private final DataSourceLoader loader;

        public PasteTypeImpl(Transferable t, DataSourceLoader loader) {
            this.t = t;
            this.loader = loader;
        }

        @Override
        public Transferable paste() throws IOException {
            DataSourceTransfer.getDefault().getDataSource(t, loader.getSource())
                    .ifPresent(loader::open);
            return null;
        }
    }
}
