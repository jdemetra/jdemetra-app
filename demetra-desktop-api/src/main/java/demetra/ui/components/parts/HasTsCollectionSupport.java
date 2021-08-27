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
package demetra.ui.components.parts;

import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsInformationType;
import demetra.timeseries.TsMoniker;
import demetra.tsprovider.DataSourceProvider;
import demetra.ui.*;
import demetra.ui.actions.Actions;
import demetra.ui.beans.PropertyChangeBroadcaster;
import demetra.ui.components.ComponentCommand;
import demetra.ui.components.TsSelectionBridge;
import demetra.ui.datatransfer.DataTransfer;
import demetra.ui.datatransfer.DataTransfers;
import demetra.ui.datatransfer.LocalObjectDataTransfer;
import demetra.ui.util.KeyStrokes;
import ec.util.list.swing.JLists;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static demetra.ui.components.parts.HasTsCollection.TS_COLLECTION_PROPERTY;
import static demetra.ui.components.parts.HasTsCollection.TS_UPDATE_MODE_PROPERTY;

/**
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class HasTsCollectionSupport {

    @NonNull
    public static HasTsCollection of(@NonNull PropertyChangeBroadcaster broadcaster) {
        return new HasTsCollectionImpl(broadcaster).watch(TsManager.getDefault());
    }

    @NonNull
    public static TransferHandler newTransferHandler(@NonNull HasTsCollection component) {
        return new HasTsCollectionTransferHandler(component, DataTransfer.getDefault());
    }

    @NonNull
    public static DropTargetListener newDropTargetListener(@NonNull HasTsCollection component, @NonNull DropTarget dropTarget) {
        return new HasTsCollectionDropTargetListener(component, DataTransfer.getDefault()).watch(dropTarget);
    }

    public static void registerActions(HasTsCollection component, ActionMap am) {
        am.put(HasTsCollection.FREEZE_ACTION, FreezeCommand.INSTANCE.toAction(component));
        am.put(HasTsCollection.COPY_ACTION, CopyCommand.INSTANCE.toAction(component));
        am.put(HasTsCollection.COPY_ALL_ACTION, CopyAllCommand.INSTANCE.toAction(component));
        am.put(HasTsCollection.DELETE_ACTION, DeleteCommand.INSTANCE.toAction(component));
        am.put(HasTsCollection.CLEAR_ACTION, ClearCommand.INSTANCE.toAction(component));
        am.put(HasTsCollection.PASTE_ACTION, PasteCommand.INSTANCE.toAction(component));
        am.put(HasTsCollection.OPEN_ACTION, OpenCommand.INSTANCE.toAction(component));
        am.put(HasTsCollection.SELECT_ALL_ACTION, SelectAllCommand.INSTANCE.toAction(component));
        am.put(HasTsCollection.RENAME_ACTION, RenameCommand.INSTANCE.toAction(component));
//        am.put(SPLIT_ACTION, HasChartCommands.splitIntoYearlyComponents().toAction(view));
//        if (this instanceof HasColorScheme) {
//            am.put(HasColorScheme.DEFAULT_COLOR_SCHEME_ACTION, HasColorScheme.commandOf(null).toAction((HasColorScheme) this));
//        }
    }

    public static void registerInputs(InputMap im) {
        KeyStrokes.putAll(im, KeyStrokes.COPY, HasTsCollection.COPY_ACTION);
        KeyStrokes.putAll(im, KeyStrokes.PASTE, HasTsCollection.PASTE_ACTION);
        KeyStrokes.putAll(im, KeyStrokes.DELETE, HasTsCollection.DELETE_ACTION);
        KeyStrokes.putAll(im, KeyStrokes.SELECT_ALL, HasTsCollection.SELECT_ALL_ACTION);
        KeyStrokes.putAll(im, KeyStrokes.OPEN, HasTsCollection.OPEN_ACTION);
        KeyStrokes.putAll(im, KeyStrokes.CLEAR, HasTsCollection.CLEAR_ACTION);
    }

    public static <C extends JComponent & HasTsCollection> JMenu newDefaultMenu(C component) {
        JMenu result = new JMenu();

        result.add(newOpenMenu(component));
        result.add(newOpenWithMenu(component));

        JMenu menu = newSaveMenu(component);
        if (menu.getSubElements().length > 0) {
            result.add(menu);
        }

        result.add(newRenameMenu(component));
        result.add(newFreezeMenu(component));
        result.add(newCopyMenu(component));
        result.add(newPasteMenu(component));
        result.add(newDeleteMenu(component));
        result.addSeparator();
        result.add(newSelectAllMenu(component));
        result.add(newClearMenu(component));

        return result;
    }

    public static <C extends JComponent & HasTsCollection> JMenuItem newRenameMenu(C component) {
        JMenuItem result = new JMenuItem(component.getActionMap().get(HasTsCollection.RENAME_ACTION));
        result.setText("Rename");
        result.setIcon(DemetraOptions.getDefault().getPopupMenuIcon(FontAwesome.FA_PENCIL_SQUARE_O));
        Actions.hideWhenDisabled(result);
        return result;
    }

    public static <C extends JComponent & HasTsCollection> JMenuItem newOpenMenu(C component) {
        JMenuItem result = new JMenuItem(component.getActionMap().get(HasTsCollection.OPEN_ACTION));
        result.setText("Open");
        result.setIcon(DemetraOptions.getDefault().getPopupMenuIcon(FontAwesome.FA_FOLDER_OPEN_O));
        Actions.hideWhenDisabled(result);
        result.setAccelerator(KeyStrokes.OPEN.get(0));
        result.setFont(result.getFont().deriveFont(Font.BOLD));
        return result;
    }

    public static <C extends JComponent & HasTsCollection> JMenu newOpenWithMenu(C component) {
        JMenu result = new JMenu(new MainOpenWithCommand().toAction(component));
        result.setText("Open with");
        result.setIcon(DemetraOptions.getDefault().getPopupMenuIcon(FontAwesome.FA_BAR_CHART_O));
        Actions.hideWhenDisabled(result);

        for (NamedService o : TsActions.getDefault().getOpenActions()) {
            JMenuItem item = new JMenuItem(((JCommand<HasTsCollection>) new OpenWithCommand(o.getName())).toAction(component));
            item.setName(o.getName());
            item.setText(o.getDisplayName());
            Image image = o.getIcon(BeanInfo.ICON_COLOR_16x16, false);
            if (image != null) {
                item.setIcon(DemetraOptions.getDefault().getPopupMenuIcon(ImageUtilities.image2Icon(image)));
            }
            result.add(item);
        }

        return result;
    }

    public static <C extends JComponent & HasTsCollection> JMenu newSaveMenu(C component) {
        JMenu result = new JMenu(new MainSaveCommand().toAction(component));
        result.setText("Save");
        Actions.hideWhenDisabled(result);
        for (NamedService o : TsActions.getDefault().getSaveActions()) {
            JMenuItem item = new JMenuItem(((JCommand<HasTsCollection>) new SaveCommand(o)).toAction(component));
            item.setName(o.getName());
            item.setText(o.getDisplayName());
            Image image = o.getIcon(BeanInfo.ICON_COLOR_16x16, false);
            if (image != null) {
                item.setIcon(DemetraOptions.getDefault().getPopupMenuIcon(ImageUtilities.image2Icon(image)));
            }
            result.add(item);
        }
        return result;
    }

    public static <C extends JComponent & HasTsCollection> JMenuItem newCopyMenu(C component) {
        JMenuItem result = new JMenuItem(component.getActionMap().get(HasTsCollection.COPY_ACTION));
        result.setText("Copy");
        result.setIcon(DemetraOptions.getDefault().getPopupMenuIcon(FontAwesome.FA_FILES_O));
        result.setAccelerator(KeyStrokes.COPY.get(0));
        Actions.hideWhenDisabled(result);
        return result;
    }

    public static <C extends JComponent & HasTsCollection> JMenuItem newPasteMenu(C component) {
        JMenuItem result = new JMenuItem(component.getActionMap().get(HasTsCollection.PASTE_ACTION));
        result.setText("Paste");
        result.setIcon(DemetraOptions.getDefault().getPopupMenuIcon(FontAwesome.FA_CLIPBOARD));
        result.setAccelerator(KeyStrokes.PASTE.get(0));
//        ExtAction.hideWhenDisabled(item);
        return result;
    }

    public static <C extends JComponent & HasTsCollection> JMenuItem newDeleteMenu(C component) {
        JMenuItem result = new JMenuItem(component.getActionMap().get(HasTsCollection.DELETE_ACTION));
        result.setText("Remove");
        result.setIcon(DemetraOptions.getDefault().getPopupMenuIcon(FontAwesome.FA_TRASH_O));
        result.setAccelerator(KeyStrokes.DELETE.get(0));
        Actions.hideWhenDisabled(result);
        return result;
    }

    public static <C extends JComponent & HasTsCollection> JMenuItem newClearMenu(C component) {
        JMenuItem result = new JMenuItem(component.getActionMap().get(HasTsCollection.CLEAR_ACTION));
        result.setText("Clear");
        result.setIcon(DemetraOptions.getDefault().getPopupMenuIcon(FontAwesome.FA_ERASER));
        result.setAccelerator(KeyStrokes.CLEAR.get(0));
        return result;
    }

    public static <C extends JComponent & HasTsCollection> JMenuItem newSelectAllMenu(C component) {
        JMenuItem result = new JMenuItem(component.getActionMap().get(HasTsCollection.SELECT_ALL_ACTION));
        result.setText("Select all");
        result.setIcon(DemetraOptions.getDefault().getPopupMenuIcon(FontAwesome.FA_ASTERISK));
        result.setAccelerator(KeyStrokes.SELECT_ALL.get(0));
        return result;
    }

    public static <C extends JComponent & HasTsCollection> JMenuItem newFreezeMenu(C component) {
        JMenuItem result = new JMenuItem(component.getActionMap().get(HasTsCollection.FREEZE_ACTION));
        result.setText("Freeze");
        result.setIcon(DemetraOptions.getDefault().getPopupMenuIcon(FontAwesome.FA_LOCK));
        Actions.hideWhenDisabled(result);
        return result;
    }

    public static <C extends JComponent & HasTsCollection> JMenuItem newSplitMenu(C component) {
        JMenuItem item = new JMenuItem(component.getActionMap().get(HasTsCollection.SPLIT_ACTION));
        item.setText("Split into yearly components");
        item.setIcon(DemetraOptions.getDefault().getPopupMenuIcon(FontAwesome.FA_CHAIN_BROKEN));
        Actions.hideWhenDisabled(item);
        return item;
    }

    //<editor-fold defaultstate="collapsed" desc="Commands">
    private static final class CopyAllCommand extends ComponentCommand<HasTsCollection> {

        public static final CopyAllCommand INSTANCE = new CopyAllCommand();

        public CopyAllCommand() {
            super(HasTsCollection.TS_COLLECTION_PROPERTY);
        }

        @Override
        public boolean isEnabled(HasTsCollection component) {
            return !component.getTsCollection().isEmpty();
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

        protected demetra.timeseries.Ts getSingleTs(HasTsCollection c) {
            return c.getTsCollection().get(c.getTsSelectionModel().getMinSelectionIndex());
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
            final demetra.timeseries.Ts ts = getSingleTs(c);
            NotifyDescriptor.InputLine descriptor = new NotifyDescriptor.InputLine("New name:", "Rename time series");
            descriptor.setInputText(ts.getName());
            if (ts.getMoniker().isProvided()) {
                descriptor.setAdditionalOptions(new Object[]{new JButton(new AbstractAction("Restore") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Optional<DataSourceProvider> provider = TsManager.getDefault().getProvider(DataSourceProvider.class, ts.getMoniker());
                        if (provider.isPresent()) {
                            demetra.tsprovider.DataSet dataSet = provider.get().toDataSet(ts.getMoniker()).orElse(null);
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

        private void rename(HasTsCollection c, demetra.timeseries.Ts ts, String newName) {
            List<demetra.timeseries.Ts> tmp = c.getTsCollection().toList();
            tmp.set(tmp.indexOf(ts), ts.withName(newName));
            c.setTsCollection(TsCollection.of(tmp));
        }
    }

    private static final class OpenCommand extends SingleSelectionCommand {

        public static final OpenCommand INSTANCE = new OpenCommand();

        @Override
        public void execute(HasTsCollection c) throws Exception {
            if (c instanceof HasTsAction) {
                String actionName = ((HasTsAction) c).getTsAction();
                if (actionName == null) {
                    actionName = DemetraOptions.getDefault().getTsActionName();
                }
                TsActions.getDefault().openWith(getSingleTs(c), actionName);
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
            TsActions.getDefault().openWith(getSingleTs(c), tsAction);
        }
    }

    @lombok.AllArgsConstructor
    private static final class SaveCommand extends AnySelectionCommand {

        private final NamedService tsSave;

        @Override
        public void execute(HasTsCollection c) throws Exception {
            TsCollection selection = JLists.getSelectionIndexStream(c.getTsSelectionModel())
                    .mapToObj(c.getTsCollection()::get)
                    .collect(TsCollection.toTsCollection());
            if (!selection.isEmpty()) {
                List<TsCollection> data = Collections.singletonList(selection);
                TsActions.getDefault().saveWith(data, tsSave.getName());
            }
        }
    }

    private static final class CopyCommand extends AnySelectionCommand {

        public static final CopyCommand INSTANCE = new CopyCommand();

        @Override
        public void execute(HasTsCollection c) throws Exception {
            TsCollection data = c.getTsSelectionStream().collect(TsCollection.toTsCollection());
            Transferable transferable = DataTransfer.getDefault().fromTsCollection(data);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
        }
    }

    private static final class PasteCommand extends JCommand<HasTsCollection> {

        public static final PasteCommand INSTANCE = new PasteCommand();

        @Override
        public boolean isEnabled(HasTsCollection c) {
            return !c.getTsUpdateMode().isReadOnly()
                    && DataTransfer.getDefault().isValidClipboard();
        }

        @Override
        public void execute(HasTsCollection c) throws Exception {
            HasTsCollectionTransferHandler.importData(c, DataTransfer.getDefault(), DataTransfers::systemClipboardAsTransferable);
        }

        @Override
        public ActionAdapter toAction(HasTsCollection c) {
            final ActionAdapter result = super.toAction(c);
            if (c instanceof Component) {
                result.withWeakPropertyChangeListener((Component) c, TS_UPDATE_MODE_PROPERTY);
            }
            DataTransfer source = DataTransfer.getDefault();
            PropertyChangeListener realListener = evt -> result.refreshActionState();
            result.putValue("TssTransferSupport", realListener);
            source.addWeakPropertyChangeListener(DataTransfer.VALID_CLIPBOARD_PROPERTY, realListener);
            return result;
        }
    }

    private static final class DeleteCommand extends ComponentCommand<HasTsCollection> {

        public static final DeleteCommand INSTANCE = new DeleteCommand();

        public DeleteCommand() {
            super(TsSelectionBridge.TS_SELECTION_PROPERTY, TS_UPDATE_MODE_PROPERTY);
        }

        @Override
        public boolean isEnabled(HasTsCollection component) {
            return !component.getTsUpdateMode().isReadOnly()
                    && !component.getTsSelectionModel().isSelectionEmpty();
        }

        @Override
        public void execute(HasTsCollection c) throws Exception {
            Set<demetra.timeseries.Ts> selection = c.getTsSelectionStream().collect(Collectors.toSet());
            TsCollection result = c.getTsCollection()
                    .stream()
                    .filter(ts -> !selection.contains(ts))
                    .collect(TsCollection.toTsCollection());
            c.setTsCollection(result);
        }
    }

    private static final class ClearCommand extends ComponentCommand<HasTsCollection> {

        public static final ClearCommand INSTANCE = new ClearCommand();

        public ClearCommand() {
            super(HasTsCollection.TS_COLLECTION_PROPERTY, TS_UPDATE_MODE_PROPERTY);
        }

        @Override
        public boolean isEnabled(HasTsCollection component) {
            return !component.getTsUpdateMode().isReadOnly() && !component.getTsCollection().isEmpty();
        }

        @Override
        public void execute(HasTsCollection component) throws Exception {
            component.setTsCollection(demetra.timeseries.TsCollection.EMPTY);
        }
    }

    private static final class SelectAllCommand extends ComponentCommand<HasTsCollection> {

        public static final SelectAllCommand INSTANCE = new SelectAllCommand();

        public SelectAllCommand() {
            super(TsSelectionBridge.TS_SELECTION_PROPERTY, TS_COLLECTION_PROPERTY);
        }

        @Override
        public boolean isEnabled(HasTsCollection c) {
            return JLists.getSelectionIndexSize(c.getTsSelectionModel()) != c.getTsCollection().size();
        }

        @Override
        public void execute(HasTsCollection c) throws Exception {
            c.getTsSelectionModel().setSelectionInterval(0, c.getTsCollection().size());
        }
    }

    private static final class FreezeCommand extends ComponentCommand<HasTsCollection> {

        private static final FreezeCommand INSTANCE = new FreezeCommand();

        public FreezeCommand() {
            super(TsSelectionBridge.TS_SELECTION_PROPERTY, TS_UPDATE_MODE_PROPERTY);
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
                    .ifPresent(i -> {
                        demetra.timeseries.TsCollection tmp = c.getTsCollection();
                        List<demetra.timeseries.Ts> list = tmp.toList();
                        list.add(list.get(i).freeze());
                        c.setTsCollection(tmp.toBuilder().items(list).build());
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

    /**
     * @author Philippe Charles
     */
    @lombok.RequiredArgsConstructor
    private static final class HasTsCollectionTransferHandler extends TransferHandler {

        @lombok.NonNull
        private final HasTsCollection delegate;

        @lombok.NonNull
        private final DataTransfer dataTransfer;

        @Override
        public int getSourceActions(JComponent c) {
            //            TsDragRenderer r = selection.length < 10 ? TsDragRenderer.asChart() : TsDragRenderer.asCount();
            //            Image image = r.getTsDragRendererImage(Arrays.asList(selection));
            //            setDragImage(image);
            return COPY;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            TsCollection data = delegate.getTsSelectionStream().collect(TsCollection.toTsCollection());
            return dataTransfer.fromTsCollection(data);
        }

        @Override
        public boolean canImport(TransferSupport support) {
            if (canImport(delegate, dataTransfer, support::getTransferable)) {
                if (support.isDrop()) {
                    support.setDropAction(COPY);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean importData(TransferSupport support) {
            return importData(delegate, dataTransfer, support::getTransferable);
        }

        public static boolean canImport(@NonNull HasTsCollection view, @NonNull DataTransfer tssSupport, @NonNull Supplier<Transferable> toData) {
            if (!view.getTsUpdateMode().isReadOnly()) {
                Transferable t = toData.get();
                return tssSupport.canImport(t) && TransferChange.of(t, view.getTsCollection()).mayChangeContent();
            }
            return false;
        }

        public static boolean importData(@NonNull HasTsCollection view, @NonNull DataTransfer tssSupport, @NonNull Supplier<Transferable> toData) {
            if (!view.getTsUpdateMode().isReadOnly()) {
                return tssSupport.toTsCollectionStream(toData.get())
                        .peek(col -> importData(view, col))
                        .count() > 0;
            }
            return false;
        }

        private static void importData(HasTsCollection view, TsCollection data) {
            if (view.isFreezeOnImport()) {
                TsCollection latest = TsManager.getDefault().makeTsCollection(data.getMoniker(), TsInformationType.All);
                view.setTsCollection(update(view.getTsUpdateMode(), view.getTsCollection(), freezedCopyOf(latest)));
            } else {
                if (TransferChange.isNotYetLoaded(data)) {
                    // TODO: put load in a separate thread
                    data = data.load(TsInformationType.Definition, TsManager.getDefault());
                }
                if (!data.isEmpty()) {
                    view.setTsCollection(update(view.getTsUpdateMode(), view.getTsCollection(), data));
                    TsManager.getDefault().loadAsync(data, TsInformationType.All, view::replaceTsCollection);
                }
            }
        }

        private static TsCollection update(HasTsCollection.TsUpdateMode mode, TsCollection first, TsCollection second) {
            switch (mode) {
                case None:
                    return first;
                case Single:
                    return TsCollection.of(second.get(0));
                case Replace:
                    return second;
                case Append:
                    Set<TsMoniker> firstMonikers = first.stream().map(Ts::getMoniker).collect(Collectors.toSet());
                    Predicate<TsMoniker> filter = moniker -> !moniker.isProvided() || !firstMonikers.contains(moniker);
                    return Stream.concat(first.stream(), second.stream().filter(internal.ui.Collections2.compose(filter, Ts::getMoniker))).collect(TsCollection.toTsCollection());
            }
            return first;
        }

        private static TsCollection freezedCopyOf(TsCollection input) {
            return input.stream().map(Ts::freeze).collect(TsCollection.toTsCollection());
        }

        private enum TransferChange {
            YES, NO, MAYBE;

            public boolean mayChangeContent() {
                return this != NO;
            }

            public static TransferChange of(Transferable source, TsCollection target) {
                LocalObjectDataTransfer handler = Lookup.getDefault().lookup(LocalObjectDataTransfer.class);
                return handler != null ? of(handler, source, target) : MAYBE;
            }

            private static TransferChange of(LocalObjectDataTransfer handler, Transferable source, TsCollection target) {
                return DataTransfers.getMultiTransferables(source)
                        .map(handler::peekTsCollection)
                        .map(col -> of(col, target))
                        .filter(TransferChange::mayChangeContent)
                        .findFirst()
                        .orElse(NO);
            }

            private static TransferChange of(@Nullable TsCollection source, @NonNull TsCollection target) {
                if (source == null) {
                    return MAYBE;
                }
                if (isNotYetLoaded(source)) {
                    return MAYBE;
                }
                if (!source.stream().allMatch(target.getItems()::contains)) {
                    return YES;
                }
                return NO;
            }

            public static boolean isNotYetLoaded(TsCollection o) {
                return o.getMoniker().isProvided() && o.isEmpty();
            }
        }
    }

    /**
     * @author Philippe Charles
     */
    @lombok.RequiredArgsConstructor
    private static final class HasTsCollectionDropTargetListener implements DropTargetListener {

        @lombok.NonNull
        private final HasTsCollection target;

        @lombok.NonNull
        private final DataTransfer transferSupport;

        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
            if (!target.getTsUpdateMode().isReadOnly()) {
                Transferable t = dtde.getTransferable();
                if (transferSupport.canImport(t)) {
                    TsCollection dropContent = transferSupport
                            .toTsCollectionStream(t)
                            .flatMap(TsCollection::stream)
                            .collect(TsCollection.toTsCollection());
                    target.setDropContent(dropContent);
                }
            }
        }

        @Override
        public void dragOver(DropTargetDragEvent dtde) {
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent dtde) {
        }

        @Override
        public void dragExit(DropTargetEvent dte) {
            target.setDropContent(TsCollection.EMPTY);
        }

        @Override
        public void drop(DropTargetDropEvent dtde) {
            dragExit(dtde);
        }

        public HasTsCollectionDropTargetListener watch(DropTarget dropTarget) {
            try {
                dropTarget.addDropTargetListener(this);
            } catch (TooManyListenersException ex) {
                Exceptions.printStackTrace(ex);
            }
            return this;
        }
    }

    /**
     * @author Philippe Charles
     */
    @lombok.RequiredArgsConstructor
    private static final class HasTsCollectionImpl implements HasTsCollection, TsListener {

        @lombok.NonNull
        private final PropertyChangeBroadcaster broadcaster;

        private TsCollection tsCollection = TsCollection.EMPTY;
        private ListSelectionModel selectionModel = new DefaultListSelectionModel();
        private static final TsUpdateMode DEFAULT_TS_UPDATE_MODE = TsUpdateMode.Append;
        private TsUpdateMode updateMode = DEFAULT_TS_UPDATE_MODE;
        private static final boolean DEFAULT_FREEZE_ON_IMPORT = false;
        private boolean freezeOnImport = DEFAULT_FREEZE_ON_IMPORT;
        private TsCollection dropContent = TsCollection.EMPTY;

        public HasTsCollectionImpl watch(TsManager manager) {
            manager.addWeakListener(this);
            return this;
        }

        @Override
        public TsCollection getTsCollection() {
            return tsCollection;
        }

        @Override
        public void setTsCollection(TsCollection tsCollection) {
            Objects.requireNonNull(tsCollection);
            TsCollection old = this.tsCollection;
            this.tsCollection = tsCollection;
            broadcaster.firePropertyChange(TS_COLLECTION_PROPERTY, old, this.tsCollection);
        }

        @Override
        public ListSelectionModel getTsSelectionModel() {
            return selectionModel;
        }

        @Override
        public void setTsSelectionModel(ListSelectionModel selectionModel) {
            ListSelectionModel old = this.selectionModel;
            this.selectionModel = selectionModel != null ? selectionModel : new DefaultListSelectionModel();
            broadcaster.firePropertyChange(TS_SELECTION_MODEL_PROPERTY, old, this.selectionModel);
        }

        @Override
        public TsUpdateMode getTsUpdateMode() {
            return updateMode;
        }

        @Override
        public void setTsUpdateMode(TsUpdateMode updateMode) {
            TsUpdateMode old = this.updateMode;
            this.updateMode = updateMode != null ? updateMode : DEFAULT_TS_UPDATE_MODE;
            broadcaster.firePropertyChange(TS_UPDATE_MODE_PROPERTY, old, this.updateMode);
        }

        @Override
        public boolean isFreezeOnImport() {
            return freezeOnImport;
        }

        @Override
        public void setFreezeOnImport(boolean freezeOnImport) {
            boolean old = this.freezeOnImport;
            this.freezeOnImport = freezeOnImport;
            broadcaster.firePropertyChange(FREEZE_ON_IMPORT_PROPERTY, old, this.freezeOnImport);
        }

        @Override
        public TsCollection getDropContent() {
            return dropContent;
        }

        @Override
        public void setDropContent(TsCollection dropContent) {
            Objects.requireNonNull(dropContent);
            TsCollection old = this.dropContent;
            this.dropContent = dropContent;
            broadcaster.firePropertyChange(DROP_CONTENT_PROPERTY, old, this.dropContent);
        }

        @Override
        public void tsUpdated(TsEvent event) {
            TsCollection currentData = getTsCollection();
            if (isEventRelatedToCollection(event, currentData)) {
                TsCollection newData = event.getSource().makeTsCollection(event.getMoniker(), currentData.getType());
                setTsCollection(newData);
            } else {
                boolean requireChange = false;
                TsCollection.Builder newData = currentData.toBuilder().clearItems();
                for (Ts ts : currentData) {
                    if (event.getRelated().test(ts.getMoniker())) {
                        requireChange = true;
                        newData.item(event.getSource().makeTs(ts.getMoniker(), TsInformationType.All));
                    } else {
                        newData.item(ts);
                    }
                }
                if (requireChange) {
                    setTsCollection(newData.build());
                }
            }
        }

        private static boolean isEventRelatedToCollection(TsEvent event, TsCollection col) {
            return !col.getMoniker().isNull() && col.getMoniker().equals(event.getMoniker());
        }
    }
}
