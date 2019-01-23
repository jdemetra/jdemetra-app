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
package internal.ui.components;

import demetra.bridge.TsConverter;
import demetra.ui.NamedService;
import demetra.ui.TsAction;
import demetra.ui.components.TsSelectionBridge;
import demetra.ui.TsManager;
import demetra.ui.components.HasTsAction;
import demetra.ui.components.HasTsCollection;
import static demetra.ui.components.HasTsCollection.TS_COLLECTION_PROPERTY;
import static demetra.ui.components.HasTsCollection.UDPATE_MODE_PROPERTY;
import ec.nbdemetra.ui.DemetraUI;
import demetra.ui.util.KeyStrokes;
import ec.nbdemetra.ui.tssave.ITsSave;
import ec.tss.Ts;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.ui.commands.ComponentCommand;
import demetra.ui.actions.Actions;
import ec.util.list.swing.JLists;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;
import java.awt.Image;
import demetra.ui.OldDataTransfer;
import demetra.ui.datatransfer.DataTransfer;
import demetra.ui.datatransfer.DataTransfers;

/**
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class HasTsCollectionCommands {

    public static JMenu newDefaultMenu(HasTsCollection col, DemetraUI demetraUI) {
        ActionMap am = ((JComponent) col).getActionMap();

        JMenu result = new JMenu();

        result.add(HasTsCollectionCommands.newOpenMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newOpenWithMenu(col, demetraUI));

        JMenu menu = HasTsCollectionCommands.newSaveMenu(col, demetraUI);
        if (menu.getSubElements().length > 0) {
            result.add(menu);
        }

        result.add(HasTsCollectionCommands.newRenameMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newFreezeMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newCopyMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newPasteMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newDeleteMenu(am, demetraUI));
        result.addSeparator();
        result.add(HasTsCollectionCommands.newSelectAllMenu(am, demetraUI));
        result.add(HasTsCollectionCommands.newClearMenu(am, demetraUI));

        return result;
    }

    public static final String COPY_ALL_ACTION = "copyAll";

    @Nonnull
    static JCommand<HasTsCollection> copyAll() {
        return HasTsCollectionCommands.CopyAllCommand.INSTANCE;
    }

    public static final String RENAME_ACTION = "rename";

    @Nonnull
    public static JCommand<HasTsCollection> rename() {
        return HasTsCollectionCommands.RenameCommand.INSTANCE;
    }

    public static JMenuItem newRenameMenu(ActionMap am, DemetraUI demetraUI) {
        JMenuItem result = new JMenuItem(am.get(HasTsCollectionCommands.RENAME_ACTION));
        result.setText("Rename");
        result.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_PENCIL_SQUARE_O));
        Actions.hideWhenDisabled(result);
        return result;
    }

    public static final String OPEN_ACTION = "open";

    @Nonnull
    public static JCommand<HasTsCollection> open() {
        return HasTsCollectionCommands.OpenCommand.INSTANCE;
    }

    public static JMenuItem newOpenMenu(ActionMap am, DemetraUI demetraUI) {
        JMenuItem result = new JMenuItem(am.get(HasTsCollectionCommands.OPEN_ACTION));
        result.setText("Open");
        result.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_FOLDER_OPEN_O));
        Actions.hideWhenDisabled(result);
        result.setAccelerator(KeyStrokes.OPEN.get(0));
        result.setFont(result.getFont().deriveFont(Font.BOLD));
        return result;
    }

    @Nonnull
    public static JCommand<HasTsCollection> openWith(@Nonnull String tsAction) {
        return new HasTsCollectionCommands.OpenWithCommand(tsAction);
    }

    public static JMenu newOpenWithMenu(HasTsCollection c, DemetraUI demetraUI) {
        JMenu result = new JMenu(new MainOpenWithCommand().toAction(c));
        result.setText("Open with");
        result.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_BAR_CHART_O));
        Actions.hideWhenDisabled(result);

        for (NamedService o : TsAction.getDefault().getTsActions()) {
            JMenuItem item = new JMenuItem(HasTsCollectionCommands.openWith(o.getName()).toAction(c));
            item.setName(o.getName());
            item.setText(o.getDisplayName());
            Image image = o.getIcon(BeanInfo.ICON_COLOR_16x16, false);
            if (image != null) {
                item.setIcon(demetraUI.getPopupMenuIcon(ImageUtilities.image2Icon(image)));
            }
            result.add(item);
        }

        return result;
    }

    @Nonnull
    public static JCommand<HasTsCollection> save(@Nonnull ITsSave tsSave) {
        return new HasTsCollectionCommands.SaveCommand(tsSave);
    }

    public static JMenu newSaveMenu(HasTsCollection c, DemetraUI demetraUI) {
        JMenu result = new JMenu(new MainSaveCommand().toAction(c));
        result.setText("Save");
        Actions.hideWhenDisabled(result);
        for (ITsSave o : demetraUI.getTsSave()) {
            JMenuItem item = new JMenuItem(HasTsCollectionCommands.save(o).toAction(c));
            item.setName(o.getName());
            item.setText(o.getDisplayName());
            Image image = o.getIcon(BeanInfo.ICON_COLOR_16x16, false);
            if (image != null) {
                item.setIcon(demetraUI.getPopupMenuIcon(ImageUtilities.image2Icon(image)));
            }
            result.add(item);
        }
        return result;
    }

    public static final String COPY_ACTION = "copy";

    @Nonnull
    public static JCommand<HasTsCollection> copy() {
        return HasTsCollectionCommands.CopyCommand.INSTANCE;
    }

    public static JMenuItem newCopyMenu(ActionMap am, DemetraUI demetraUI) {
        JMenuItem result = new JMenuItem(am.get(HasTsCollectionCommands.COPY_ACTION));
        result.setText("Copy");
        result.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_FILES_O));
        result.setAccelerator(KeyStrokes.COPY.get(0));
        Actions.hideWhenDisabled(result);
        return result;
    }

    public static final String PASTE_ACTION = "paste";

    @Nonnull
    public static JCommand<HasTsCollection> paste() {
        return HasTsCollectionCommands.PasteCommand.INSTANCE;
    }

    public static JMenuItem newPasteMenu(ActionMap am, DemetraUI demetraUI) {
        JMenuItem result = new JMenuItem(am.get(HasTsCollectionCommands.PASTE_ACTION));
        result.setText("Paste");
        result.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_CLIPBOARD));
        result.setAccelerator(KeyStrokes.PASTE.get(0));
//        ExtAction.hideWhenDisabled(item);
        return result;
    }

    public static final String DELETE_ACTION = "delete";

    @Nonnull
    public static JCommand<HasTsCollection> delete() {
        return HasTsCollectionCommands.DeleteCommand.INSTANCE;
    }

    public static JMenuItem newDeleteMenu(ActionMap am, DemetraUI demetraUI) {
        JMenuItem result = new JMenuItem(am.get(HasTsCollectionCommands.DELETE_ACTION));
        result.setText("Remove");
        result.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_TRASH_O));
        result.setAccelerator(KeyStrokes.DELETE.get(0));
        Actions.hideWhenDisabled(result);
        return result;
    }

    public static final String CLEAR_ACTION = "clear";

    @Nonnull
    public static JCommand<HasTsCollection> clear() {
        return HasTsCollectionCommands.ClearCommand.INSTANCE;
    }

    public static JMenuItem newClearMenu(ActionMap am, DemetraUI demetraUI) {
        JMenuItem result = new JMenuItem(am.get(HasTsCollectionCommands.CLEAR_ACTION));
        result.setText("Clear");
        result.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_ERASER));
        result.setAccelerator(KeyStrokes.CLEAR.get(0));
        return result;
    }

    public static final String SELECT_ALL_ACTION = "selectAll";

    @Nonnull
    public static JCommand<HasTsCollection> selectAll() {
        return HasTsCollectionCommands.SelectAllCommand.INSTANCE;
    }

    public static JMenuItem newSelectAllMenu(ActionMap am, DemetraUI demetraUI) {
        JMenuItem result = new JMenuItem(am.get(HasTsCollectionCommands.SELECT_ALL_ACTION));
        result.setText("Select all");
        result.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_ASTERISK));
        result.setAccelerator(KeyStrokes.SELECT_ALL.get(0));
        return result;
    }

    public static final String FREEZE_ACTION = "freeze";

    @Nonnull
    public static JCommand<HasTsCollection> freeze() {
        return HasTsCollectionCommands.FreezeCommand.INSTANCE;
    }

    public static JMenuItem newFreezeMenu(ActionMap am, DemetraUI demetraUI) {
        JMenuItem result = new JMenuItem(am.get(HasTsCollectionCommands.FREEZE_ACTION));
        result.setText("Freeze");
        result.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_LOCK));
        Actions.hideWhenDisabled(result);
        return result;
    }

    public static final String SPLIT_ACTION = "splitIntoYearlyComponents";

    public static JMenuItem newSplitMenu(ActionMap am, DemetraUI demetraUI) {
        JMenuItem item = new JMenuItem(am.get(SPLIT_ACTION));
        item.setText("Split into yearly components");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_CHAIN_BROKEN));
        Actions.hideWhenDisabled(item);
        return item;
    }

    public static void registerActions(HasTsCollection view, ActionMap am) {
        am.put(FREEZE_ACTION, freeze().toAction(view));
        am.put(COPY_ACTION, copy().toAction(view));
        am.put(COPY_ALL_ACTION, copyAll().toAction(view));
        am.put(DELETE_ACTION, delete().toAction(view));
        am.put(CLEAR_ACTION, clear().toAction(view));
        am.put(PASTE_ACTION, paste().toAction(view));
        am.put(OPEN_ACTION, open().toAction(view));
        am.put(SELECT_ALL_ACTION, selectAll().toAction(view));
        am.put(RENAME_ACTION, rename().toAction(view));
        am.put(SPLIT_ACTION, HasChartCommands.splitIntoYearlyComponents().toAction(view));
//        if (this instanceof HasColorScheme) {
//            am.put(HasColorScheme.DEFAULT_COLOR_SCHEME_ACTION, HasColorScheme.commandOf(null).toAction((HasColorScheme) this));
//        }
    }

    public static void registerInputs(InputMap im) {
        KeyStrokes.putAll(im, KeyStrokes.COPY, COPY_ACTION);
        KeyStrokes.putAll(im, KeyStrokes.PASTE, PASTE_ACTION);
        KeyStrokes.putAll(im, KeyStrokes.DELETE, DELETE_ACTION);
        KeyStrokes.putAll(im, KeyStrokes.SELECT_ALL, SELECT_ALL_ACTION);
        KeyStrokes.putAll(im, KeyStrokes.OPEN, OPEN_ACTION);
        KeyStrokes.putAll(im, KeyStrokes.CLEAR, CLEAR_ACTION);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static final class CopyAllCommand extends ComponentCommand<HasTsCollection> {

        public static final CopyAllCommand INSTANCE = new CopyAllCommand();

        public CopyAllCommand() {
            super(HasTsCollection.TS_COLLECTION_PROPERTY);
        }

        @Override
        public boolean isEnabled(HasTsCollection component) {
            return component.getTsCollection().getData().size() > 0;
        }

        @Override
        public void execute(HasTsCollection c) throws Exception {
            Transferable transferable = DataTransfer.getDefault().fromTsCollection(c.getTsCollection());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
        }
    }

    private static abstract class SingleSelectionCommand extends ComponentCommand<HasTsCollection> {

        public SingleSelectionCommand() {
            super(TsSelectionBridge.TS_SELECTION_PROPERTY);
        }

        @Override
        public boolean isEnabled(HasTsCollection c) {
            return JLists.isSingleSelectionIndex(c.getTsSelectionModel());
        }

        protected Ts getSingleTs(HasTsCollection c) {
            return TsConverter.fromTs(c.getTsCollection().getData().get(c.getTsSelectionModel().getMinSelectionIndex()));
        }
    }

    private static abstract class AnySelectionCommand extends ComponentCommand<HasTsCollection> {

        public AnySelectionCommand() {
            super(TsSelectionBridge.TS_SELECTION_PROPERTY);
        }

        @Override
        public boolean isEnabled(HasTsCollection component) {
            return !component.getTsSelectionModel().isSelectionEmpty();
        }
    }

    private static final class RenameCommand extends SingleSelectionCommand {

        public static final RenameCommand INSTANCE = new RenameCommand();

        @Override
        public void execute(final HasTsCollection c) throws Exception {
            final Ts ts = getSingleTs(c);
            NotifyDescriptor.InputLine descriptor = new NotifyDescriptor.InputLine("New name:", "Rename time series");
            descriptor.setInputText(ts.getName());
            if (!ts.getMoniker().isAnonymous()) {
                descriptor.setAdditionalOptions(new Object[]{new JButton(new AbstractAction("Restore") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Optional<IDataSourceProvider> provider = TsManager.getDefault().lookup(IDataSourceProvider.class, ts.getMoniker());
                        if (provider.isPresent()) {
                            DataSet dataSet = provider.get().toDataSet(ts.getMoniker());
                            if (dataSet != null) {
                                rename(c, ts, provider.get().getDisplayName(dataSet));
                            }
                        }
                    }
                })});
            }
            if (DialogDisplayer.getDefault().notify(descriptor) == NotifyDescriptor.OK_OPTION) {
                rename(c, ts, descriptor.getInputText());
            }
        }

        private void rename(HasTsCollection c, Ts ts, String newName) {
            List<Ts> tmp = c.getTsCollection().getData().stream().map(TsConverter::fromTs).collect(Collectors.toList());
            tmp.set(tmp.indexOf(ts), ts.rename(newName));
            demetra.tsprovider.TsCollection.Builder result = demetra.tsprovider.TsCollection.builder();
            tmp.forEach(o -> result.data(TsConverter.toTs(o)));
            c.setTsCollection(result.build());
        }
    }

    private static final class OpenCommand extends SingleSelectionCommand {

        public static final OpenCommand INSTANCE = new OpenCommand();

        @Override
        public void execute(HasTsCollection c) throws Exception {
            if (c instanceof HasTsAction) {
                TsAction.getDefault().openWith(TsConverter.toTs(getSingleTs(c)), ((HasTsAction) c).getTsAction());
            }
        }
    }

    private static final class MainOpenWithCommand extends ComponentCommand<HasTsCollection> {

        public MainOpenWithCommand() {
            super(TsSelectionBridge.TS_SELECTION_PROPERTY);
        }

        @Override
        public void execute(HasTsCollection component) throws Exception {
            // do nothing
        }

        @Override
        public boolean isEnabled(HasTsCollection component) {
            return JLists.isSingleSelectionIndex(component.getTsSelectionModel());
        }
    }

    @lombok.AllArgsConstructor
    private static final class OpenWithCommand extends SingleSelectionCommand {

        private final String tsAction;

        @Override
        public void execute(HasTsCollection c) throws Exception {
            TsAction.getDefault().openWith(TsConverter.toTs(getSingleTs(c)), tsAction);
        }
    }

    private static final class SaveCommand extends AnySelectionCommand {

        private final ITsSave tsSave;

        SaveCommand(@Nonnull ITsSave tsSave) {
            this.tsSave = tsSave;
        }

        @Override
        public void execute(HasTsCollection c) throws Exception {
            Ts[] selection = JLists.getSelectionIndexStream(c.getTsSelectionModel())
                    .mapToObj(c.getTsCollection().getData()::get)
                    .map(TsConverter::fromTs)
                    .toArray(Ts[]::new);
            if (selection.length > 0) {
                tsSave.save(selection);
            }
        }
    }

    private static final class CopyCommand extends AnySelectionCommand {

        public static final CopyCommand INSTANCE = new CopyCommand();

        @Override
        public void execute(HasTsCollection c) throws Exception {
            demetra.tsprovider.TsCollection.Builder col = demetra.tsprovider.TsCollection.builder();
            c.getTsSelectionStream().forEach(col::data);
            Transferable transferable = DataTransfer.getDefault().fromTsCollection(col.build());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
        }
    }

    private static final class PasteCommand extends JCommand<HasTsCollection> {

        public static final PasteCommand INSTANCE = new PasteCommand();

        @Override
        public boolean isEnabled(HasTsCollection c) {
            return !c.getTsUpdateMode().isReadOnly()
                    && OldDataTransfer.getDefault().isValidClipboard();
        }

        @Override
        public void execute(HasTsCollection c) throws Exception {
            HasTsCollectionTransferHandler.importData(c, DataTransfer.getDefault(), DataTransfers::systemClipboardAsTransferable);
        }

        @Override
        public ActionAdapter toAction(HasTsCollection c) {
            final ActionAdapter result = super.toAction(c);
            if (c instanceof Component) {
                result.withWeakPropertyChangeListener((Component) c, UDPATE_MODE_PROPERTY);
            }
            OldDataTransfer source = OldDataTransfer.getDefault();
            PropertyChangeListener realListener = evt -> result.refreshActionState();
            result.putValue("TssTransferSupport", realListener);
            source.addPropertyChangeListener(OldDataTransfer.VALID_CLIPBOARD_PROPERTY, WeakListeners.propertyChange(realListener, source));
            return result;
        }
    }

    private static final class DeleteCommand extends ComponentCommand<HasTsCollection> {

        public static final DeleteCommand INSTANCE = new DeleteCommand();

        public DeleteCommand() {
            super(TsSelectionBridge.TS_SELECTION_PROPERTY, UDPATE_MODE_PROPERTY);
        }

        @Override
        public boolean isEnabled(HasTsCollection component) {
            return !component.getTsUpdateMode().isReadOnly()
                    && !component.getTsSelectionModel().isSelectionEmpty();
        }

        @Override
        public void execute(HasTsCollection c) throws Exception {
            Set<demetra.tsprovider.Ts> selection = c.getTsSelectionStream().collect(Collectors.toSet());
            demetra.tsprovider.TsCollection.Builder result = demetra.tsprovider.TsCollection.builder();
            c.getTsCollection().getData().stream().filter(o -> !selection.contains(o)).forEach(result::data);
            c.setTsCollection(result.build());
        }
    }

    private static final class ClearCommand extends ComponentCommand<HasTsCollection> {

        public static final ClearCommand INSTANCE = new ClearCommand();

        public ClearCommand() {
            super(HasTsCollection.TS_COLLECTION_PROPERTY, UDPATE_MODE_PROPERTY);
        }

        @Override
        public boolean isEnabled(HasTsCollection component) {
            return !component.getTsUpdateMode().isReadOnly() && component.getTsCollection().getData().size() > 0;
        }

        @Override
        public void execute(HasTsCollection component) throws Exception {
            component.setTsCollection(demetra.tsprovider.TsCollection.EMPTY);
        }
    }

    private static final class SelectAllCommand extends ComponentCommand<HasTsCollection> {

        public static final SelectAllCommand INSTANCE = new SelectAllCommand();

        public SelectAllCommand() {
            super(TsSelectionBridge.TS_SELECTION_PROPERTY, TS_COLLECTION_PROPERTY);
        }

        @Override
        public boolean isEnabled(HasTsCollection c) {
            return JLists.getSelectionIndexSize(c.getTsSelectionModel()) != c.getTsCollection().getData().size();
        }

        @Override
        public void execute(HasTsCollection c) throws Exception {
            c.getTsSelectionModel().setSelectionInterval(0, c.getTsCollection().getData().size());
        }
    }

    private static final class FreezeCommand extends ComponentCommand<HasTsCollection> {

        private static final FreezeCommand INSTANCE = new FreezeCommand();

        public FreezeCommand() {
            super(TsSelectionBridge.TS_SELECTION_PROPERTY, UDPATE_MODE_PROPERTY);
        }

        @Override
        public boolean isEnabled(HasTsCollection component) {
            return JLists.isSingleSelectionIndex(component.getTsSelectionModel())
                    && !component.getTsUpdateMode().isReadOnly();
        }

        @Override
        public void execute(HasTsCollection c) throws Exception {
            JLists.getSelectionIndexStream(c.getTsSelectionModel())
                    .findFirst()
                    .ifPresent(o -> {
                        demetra.tsprovider.TsCollection tmp = c.getTsCollection();
                        c.setTsCollection(tmp.toBuilder().data(TsConverter.toTs(TsConverter.fromTs(tmp.getData().get(0)).freeze())).build());
                    });
        }
    }

    private static final class MainSaveCommand extends ComponentCommand<HasTsCollection> {

        public MainSaveCommand() {
            super(TsSelectionBridge.TS_SELECTION_PROPERTY);
        }

        @Override
        public void execute(HasTsCollection component) throws Exception {
            // do nothing
        }

        @Override
        public boolean isEnabled(HasTsCollection component) {
            return !component.getTsSelectionModel().isSelectionEmpty();
        }
    }
    //</editor-fold>
}
