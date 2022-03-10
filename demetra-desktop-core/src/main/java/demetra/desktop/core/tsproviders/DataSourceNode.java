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
import demetra.desktop.TsCollectable;
import demetra.desktop.TsManager;
import demetra.desktop.actions.Reloadable;
import demetra.desktop.actions.Renameable;
import demetra.desktop.core.actions.CloseNodeAction;
import demetra.desktop.core.actions.ReloadNodeAction;
import demetra.desktop.core.actions.RenameNodeAction;
import demetra.desktop.core.actions.TsSaveNodeAction;
import demetra.desktop.core.interchange.ExportNodeAction;
import demetra.desktop.core.star.StarAction;
import demetra.desktop.datatransfer.DataTransfer;
import demetra.desktop.datatransfer.DataTransfers;
import demetra.desktop.interchange.Exportable;
import demetra.desktop.nodes.FailSafeChildFactory;
import demetra.desktop.nodes.NodeAnnotator;
import demetra.desktop.nodes.Nodes;
import demetra.desktop.star.StarList;
import demetra.desktop.tsproviders.DataSourceProviderBuddySupport;
import demetra.desktop.tsproviders.DataSourceProviderBuddyUtil;
import demetra.timeseries.TsInformationType;
import demetra.tsprovider.DataSet;
import demetra.tsprovider.DataSource;
import demetra.tsprovider.DataSourceLoader;
import demetra.tsprovider.DataSourceProvider;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.netbeans.api.actions.Closable;
import org.netbeans.api.actions.Editable;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static demetra.desktop.actions.Actions.COPY_NODE_ACTION_ID;
import static demetra.desktop.tsproviders.TsProviderNodes.SOURCE_ACTION_PATH;

/**
 * A node that represents a DataSource.
 *
 * @author Philippe Charles
 */
@ActionReferences({
        @ActionReference(path = SOURCE_ACTION_PATH, position = 210, separatorBefore = 200, id = @ActionID(category = "File", id = StarAction.ID)),
        @ActionReference(path = SOURCE_ACTION_PATH, position = 310, separatorBefore = 300, id = @ActionID(category = "Edit", id = EditSourceNodeAction.ID)),
        @ActionReference(path = SOURCE_ACTION_PATH, position = 320, separatorBefore = 300, id = @ActionID(category = "Edit", id = CloneSourceNodeAction.ID)),
        @ActionReference(path = SOURCE_ACTION_PATH, position = 330, separatorBefore = 300, id = @ActionID(category = "File", id = CloseNodeAction.ID)),
        @ActionReference(path = SOURCE_ACTION_PATH, position = 340, separatorBefore = 300, id = @ActionID(category = "File", id = ReloadNodeAction.ID)),
        @ActionReference(path = SOURCE_ACTION_PATH, position = 350, separatorBefore = 300, id = @ActionID(category = "File", id = RenameNodeAction.ID)),
        @ActionReference(path = SOURCE_ACTION_PATH, position = 410, separatorBefore = 400, id = @ActionID(category = "Edit", id = COPY_NODE_ACTION_ID)),
        @ActionReference(path = SOURCE_ACTION_PATH, position = 420, separatorBefore = 400, id = @ActionID(category = "File", id = TsSaveNodeAction.ID)),
        @ActionReference(path = SOURCE_ACTION_PATH, position = 430, separatorBefore = 400, id = @ActionID(category = "File", id = ExportNodeAction.ID)),
        @ActionReference(path = SOURCE_ACTION_PATH, position = 710, separatorBefore = 700, id = @ActionID(category = "Edit", id = ShowInFolderNodeAction.ID))
})
public final class DataSourceNode extends AbstractNode {

    public DataSourceNode(@NonNull DataSource dataSource) {
        this(dataSource, new InstanceContent());
    }

    private DataSourceNode(DataSource dataSource, InstanceContent abilities) {
        // 1. Children and lookup
        super(Children.create(new DataSourceChildFactory(dataSource), true),
                new ProxyLookup(Lookups.singleton(dataSource), new AbstractLookup(abilities)));
        // 2. Abilities
        {
            abilities.add(new NameableImpl());
            abilities.add(new TsCollectableImpl());
            abilities.add(new ReloadableImpl());
            if (TsManager.getDefault().getProvider(DataSourceLoader.class, dataSource).isPresent()) {
                abilities.add(new EditableImpl());
                abilities.add(new ClosableImpl());
                abilities.add(new ExportableAsXmlImpl());
            }
        }
        // 3. Name and display name
        TsManager.getDefault()
                .getProvider(DataSourceProvider.class, dataSource)
                .ifPresent(provider -> setDisplayName(provider.getDisplayName(dataSource)));
    }

    @Override
    public Action[] getActions(boolean context) {
        return Nodes.actionsForPath(SOURCE_ACTION_PATH);
    }

    private Image lookupIcon(int type, boolean opened) {
        DataSource o = getLookup().lookup(DataSource.class);
        return DataSourceProviderBuddySupport.getDefault().getImage(o, type, opened);
    }

    @Override
    public Image getIcon(int type) {
        Image image = lookupIcon(type, false);
        return NodeAnnotator.getDefault().annotateIcon(this, image);
    }

    @Override
    public Image getOpenedIcon(int type) {
        Image image = lookupIcon(type, true);
        return NodeAnnotator.getDefault().annotateIcon(this, image);
    }

    @Override
    protected Sheet createSheet() {
        DataSource o = getLookup().lookup(DataSource.class);
        return DataSourceProviderBuddySupport.getDefault().getSheet(o);
    }

    @Override
    public boolean canCopy() {
        return true;
    }

    private Transferable getData(TsInformationType type) throws IOException {
        DataSource dataSource = getLookup().lookup(DataSource.class);
        return TsManager.getDefault()
                .getTsCollection(dataSource, type)
                .map(DataTransfer.getDefault()::fromTsCollection)
                .orElseThrow(() -> new IOException("Cannot create the TS collection '" + getDisplayName() + "'; check the logs for further details."));
    }

    @Override
    public Transferable clipboardCopy() throws IOException {
        return getData(TsEventHelper.SHOULD_BE_NONE);
    }

    @Override
    public Transferable drag() throws IOException {
        ExTransferable data = ExTransferable.create(getData(TsEventHelper.SHOULD_BE_NONE));

        DataSource dataSource = getLookup().lookup(DataSource.class);
        TsManager.getDefault()
                .getFile(dataSource)
                .ifPresent(o -> data.put(new LocalFileTransferable(o)));
        return data;
    }

    private static void notifyMissingProvider(String providerName) {
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Cannot find provider '" + providerName + "'"));
    }

    private static final class DataSourceChildFactory extends FailSafeChildFactory {

        private final DataSource dataSource;

        public DataSourceChildFactory(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        protected boolean tryCreateKeys(List<Object> list) throws Exception {
            Optional<DataSourceProvider> provider = TsManager.getDefault().getProvider(DataSourceProvider.class, dataSource);
            if (provider.isPresent()) {
                list.addAll(provider.get()
                        .children(dataSource));
            }
            return true;
        }

        @Override
        protected Node tryCreateNodeForKey(Object key) throws Exception {
            return DataSetNode.create((DataSet) key);
        }

        @Override
        protected Node createExceptionNode(Exception ex) {
            if (ex instanceof IOException) {
                return new ProviderExceptionNode((IOException) ex, dataSource.getProviderName());
            }
            return super.createExceptionNode(ex);
        }
    }

    private final class ReloadableImpl implements Reloadable {

        @Override
        public void reload() {
            DataSource dataSource = getLookup().lookup(DataSource.class);
            Optional<DataSourceProvider> provider = TsManager.getDefault().getProvider(DataSourceProvider.class, dataSource);
            if (provider.isPresent()) {
                provider.get().reload(dataSource);
                setChildren(Children.create(new DataSourceChildFactory(dataSource), true));
            } else {
                notifyMissingProvider(dataSource.getProviderName());
            }
        }
    }

    private final class NameableImpl implements Renameable {

        @Override
        public void rename() {
            final NotifyDescriptor.InputLine descriptor = new NotifyDescriptor.InputLine("New name:", "Rename DataSource");
            descriptor.setInputText(getDisplayName());
            descriptor.setAdditionalOptions(new Object[]{new JButton(new AbstractAction("Restore") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DataSource dataSource = getLookup().lookup(DataSource.class);
                    TsManager.getDefault()
                            .getProvider(DataSourceProvider.class, dataSource)
                            .ifPresent(provider -> setDisplayName(provider.getDisplayName(dataSource)));
                }
            })});
            if (DialogDisplayer.getDefault().notify(descriptor) == NotifyDescriptor.OK_OPTION) {
                setDisplayName(descriptor.getInputText());
            }
        }
    }

    private final class ClosableImpl implements Closable {

        @Override
        public boolean close() {
            DataSource dataSource = getLookup().lookup(DataSource.class);
            return TsManager.getDefault()
                    .getProvider(DataSourceLoader.class, dataSource)
                    .map(provider -> provider.close(dataSource))
                    .orElse(false);
        }
    }

    private final class EditableImpl implements Editable {

        @Override
        public void edit() {
            DataSource dataSource = getLookup().lookup(DataSource.class);
            DataSourceLoader loader = TsManager.getDefault().getProvider(DataSourceLoader.class, dataSource).get();
            Object bean = loader.decodeBean(dataSource);
            if (DataSourceProviderBuddySupport.getDefault().getBeanEditor(loader.getSource(), "Edit data source").editBean(bean, Exceptions::printStackTrace)) {
                loader.close(dataSource);
                DataSource editedDataSource = loader.encodeBean(bean);
                loader.open(editedDataSource);
                StarList starList = StarList.getDefault();
                if (starList.isStarred(dataSource)) {
                    starList.toggle(dataSource);
                    starList.toggle(editedDataSource);
                }
            }
        }
    }

    private final class ExportableAsXmlImpl implements Exportable {

        @Override
        public Config exportConfig() {
            DataSource dataSource = getLookup().lookup(DataSource.class);
            return DataSourceProviderBuddyUtil.getConfig(dataSource, getDisplayName());
        }
    }

    private final class TsCollectableImpl implements TsCollectable {

        @Override
        public demetra.timeseries.TsCollection getTsCollection() {
            DataSource dataSource = getLookup().lookup(DataSource.class);
            return TsManager.getDefault()
                    .getTsCollection(dataSource, TsEventHelper.SHOULD_BE_NONE)
                    .orElse(demetra.timeseries.TsCollection.EMPTY);
        }
    }

    private static final class LocalFileTransferable extends ExTransferable.Single {

        private static final DataFlavor LOCAL_FILE_DATA_FLAVOR = DataTransfers.newLocalObjectDataFlavor(File.class);
        private final File file;

        public LocalFileTransferable(@NonNull File file) {
            super(LOCAL_FILE_DATA_FLAVOR);
            this.file = file;
        }

        @Override
        protected Object getData() throws IOException, UnsupportedFlavorException {
            return file;
        }
    }
}
