/*
 * Copyright 2016 National Bank of Belgium
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
package ec.util.list.swing;

import ec.util.various.swing.FontAwesome;
import static ec.util.various.swing.FontAwesome.FA_ANGLE_DOUBLE_DOWN;
import static ec.util.various.swing.FontAwesome.FA_ANGLE_DOUBLE_LEFT;
import static ec.util.various.swing.FontAwesome.FA_ANGLE_DOUBLE_RIGHT;
import static ec.util.various.swing.FontAwesome.FA_ANGLE_DOUBLE_UP;
import static ec.util.various.swing.FontAwesome.FA_ANGLE_DOWN;
import static ec.util.various.swing.FontAwesome.FA_ANGLE_LEFT;
import static ec.util.various.swing.FontAwesome.FA_ANGLE_RIGHT;
import static ec.util.various.swing.FontAwesome.FA_ANGLE_UP;
import ec.util.various.swing.JCommand;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Philippe Charles
 * @param <E>
 */
public final class JListSelection<E> extends JComponent {

    public static final String SOURCE_MODEL_PROPERTY = "sourceModel";
    public static final String SOURCE_FOOTER_PROPERTY = "sourceFooter";
    public static final String SOURCE_HEADER_PROPERTY = "sourceHeader";
    public static final String TARGET_MODEL_PROPERTY = "targetModel";
    public static final String TARGET_FOOTER_PROPERTY = "targetFooter";
    public static final String TARGET_HEADER_PROPERTY = "targetHeader";
    public static final String CELL_RENDERER_PROPERTY = "cellRenderer";
    public static final String ORIENTATION_PROPERTY = "orientation";

    public static final String SELECT_ACTION = "select";
    public static final String UNSELECT_ACTION = "unselect";
    public static final String SELECT_ALL_ACTION = "selectAll";
    public static final String UNSELECT_ALL_ACTION = "unselectAll";
    public static final String APPLY_HORIZONTAL_ACTION = "applyHorizontal";
    public static final String APPLY_VERTICAL_ACTION = "applyVertical";

    private final JPanel sourcePanel;
    private final JList<E> sourceList;
    private final JPanel targetPanel;
    private final JList<E> targetList;
    private final JToolBar toolBar;

    private DefaultListModel<E> sourceModel;
    private Component sourceFooter;
    private Component sourceHeader;
    private DefaultListModel<E> targetModel;
    private Component targetFooter;
    private Component targetHeader;
    private ListCellRenderer<? super E> cellRenderer;
    private int orientation;

    public JListSelection() {
        this.sourcePanel = new JPanel();
        this.sourceList = new JList<>();
        this.targetPanel = new JPanel();
        this.targetList = new JList<>();
        this.toolBar = new JToolBar();

        this.sourceModel = new DefaultListModel<>();
        this.sourceFooter = null;
        this.sourceHeader = null;
        this.targetModel = new DefaultListModel<>();
        this.targetFooter = null;
        this.targetHeader = null;
        this.cellRenderer = new DefaultListCellRenderer();
        this.orientation = SwingConstants.HORIZONTAL;

        initComponents();
        enableProperties();
        enableSelectionOnDoubleClick();
    }

    //<editor-fold defaultstate="collapsed" desc="Initialization">
    private void initComponents() {
        ActionMap am = getActionMap();
        am.put(SELECT_ACTION, new SelectCommand().toAction(this));
        am.put(UNSELECT_ACTION, new UnselectCommand().toAction(this));
        am.put(SELECT_ALL_ACTION, new SelectAllCommand().toAction(this));
        am.put(UNSELECT_ALL_ACTION, new UnselectAllCommand().toAction(this));
        am.put(APPLY_HORIZONTAL_ACTION, new ApplyHorizontalCommand().toAction(this));
        am.put(APPLY_VERTICAL_ACTION, new ApplyVerticalCommand().toAction(this));

        TransferHandler transferHandler = new CustomTransferHandler(sourceList, targetList);
        KeyStroke enterKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

        sourceList.setDragEnabled(true);
        sourceList.setDropMode(DropMode.INSERT);
        sourceList.setTransferHandler(transferHandler);
        sourceList.getInputMap().put(enterKeyStroke, SELECT_ACTION);
        sourceList.getActionMap().put(SELECT_ACTION, am.get(SELECT_ACTION));

        sourcePanel.setLayout(new BorderLayout());
        sourcePanel.add(new JScrollPane(sourceList), BorderLayout.CENTER);
        sourcePanel.setPreferredSize(new Dimension(10, 10));

        targetList.setDragEnabled(true);
        targetList.setDropMode(DropMode.INSERT);
        targetList.setTransferHandler(transferHandler);
        targetList.getInputMap().put(enterKeyStroke, UNSELECT_ACTION);
        targetList.getActionMap().put(UNSELECT_ACTION, am.get(UNSELECT_ACTION));

        targetPanel.setLayout(new BorderLayout());
        targetPanel.add(new JScrollPane(targetList), BorderLayout.CENTER);
        targetPanel.setPreferredSize(new Dimension(10, 10));

        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        toolBar.setFloatable(false);

        onSourceModelChange();
        onTargetModelChange();
        onCellRendererChange();
        onOrientationChange();
    }

    private void enableProperties() {
        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case SOURCE_MODEL_PROPERTY:
                    onSourceModelChange();
                    break;
                case TARGET_MODEL_PROPERTY:
                    onTargetModelChange();
                    break;
                case CELL_RENDERER_PROPERTY:
                    onCellRendererChange();
                    break;
                case ORIENTATION_PROPERTY:
                    onOrientationChange();
                    break;
                case SOURCE_FOOTER_PROPERTY:
                    onSourcePanelChange();
                    break;
                case SOURCE_HEADER_PROPERTY:
                    onSourcePanelChange();
                    break;
                case TARGET_FOOTER_PROPERTY:
                    onTargetPanelChange();
                    break;
                case TARGET_HEADER_PROPERTY:
                    onTargetPanelChange();
                    break;
                case "componentPopupMenu":
                    onComponentPopupMenuChange();
                    break;
            }
        });
    }

    private void enableSelectionOnDoubleClick() {
        sourceList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    getActionMap().get(SELECT_ACTION).actionPerformed(null);
                }
            }
        });
        targetList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    getActionMap().get(UNSELECT_ACTION).actionPerformed(null);
                }
            }
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    private void onSourceModelChange() {
        sourceList.setModel(sourceModel);
    }

    private void onTargetModelChange() {
        targetList.setModel(targetModel);
    }

    private void onCellRendererChange() {
        sourceList.setCellRenderer(cellRenderer);
        targetList.setCellRenderer(cellRenderer);
    }

    private void onOrientationChange() {
        removeAll();

        boolean horizontal = orientation == SwingConstants.HORIZONTAL;

        toolBar.removeAll();
        toolBar.add(newButton(SELECT_ACTION, horizontal ? FA_ANGLE_RIGHT : FA_ANGLE_DOWN));
        toolBar.add(newButton(UNSELECT_ACTION, horizontal ? FA_ANGLE_LEFT : FA_ANGLE_UP));
        toolBar.add(newButton(SELECT_ALL_ACTION, horizontal ? FA_ANGLE_DOUBLE_RIGHT : FA_ANGLE_DOUBLE_DOWN));
        toolBar.add(newButton(UNSELECT_ALL_ACTION, horizontal ? FA_ANGLE_DOUBLE_LEFT : FA_ANGLE_DOUBLE_UP));
        toolBar.setOrientation(horizontal ? SwingConstants.VERTICAL : SwingConstants.HORIZONTAL);

        setLayout(new BoxLayout(this, horizontal ? BoxLayout.X_AXIS : BoxLayout.Y_AXIS));
        Stream.of(sourcePanel, toolBar, targetPanel).forEach(this::add);
        validate();
    }

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = getComponentPopupMenu();
        Stream.of(sourcePanel, sourceList, targetPanel, targetList)
                .forEach(o -> o.setComponentPopupMenu(popupMenu));
    }

    private void onSourcePanelChange() {
        sourcePanel.removeAll();
        sourcePanel.add(new JScrollPane(sourceList), BorderLayout.CENTER);
        if (sourceFooter != null) {
            sourcePanel.add(sourceFooter, BorderLayout.SOUTH);
        }
        if (sourceHeader != null) {
            sourcePanel.add(sourceHeader, BorderLayout.NORTH);
        }
        sourcePanel.validate();
    }

    private void onTargetPanelChange() {
        targetPanel.removeAll();
        targetPanel.add(new JScrollPane(targetList), BorderLayout.CENTER);
        if (targetFooter != null) {
            targetPanel.add(targetFooter, BorderLayout.SOUTH);
        }
        if (targetHeader != null) {
            targetPanel.add(targetHeader, BorderLayout.NORTH);
        }
        targetPanel.validate();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    @Nonnull
    public DefaultListModel<E> getSourceModel() {
        return sourceModel;
    }

    public void setSourceModel(@Nullable DefaultListModel<E> sourceModel) {
        DefaultListModel<E> old = this.sourceModel;
        this.sourceModel = sourceModel != null ? sourceModel : new DefaultListModel<>();
        firePropertyChange(SOURCE_MODEL_PROPERTY, old, this.sourceModel);
    }

    @Nonnull
    public DefaultListModel<E> getTargetModel() {
        return targetModel;
    }

    public void setTargetModel(@Nullable DefaultListModel<E> targetModel) {
        DefaultListModel<E> old = this.targetModel;
        this.targetModel = targetModel != null ? targetModel : new DefaultListModel<>();
        firePropertyChange(SOURCE_MODEL_PROPERTY, old, this.targetModel);
    }

    @Nonnull
    public ListCellRenderer<? super E> getCellRenderer() {
        return cellRenderer;
    }

    public void setCellRenderer(@Nullable ListCellRenderer<? super E> cellRenderer) {
        ListCellRenderer<? super E> old = this.cellRenderer;
        this.cellRenderer = cellRenderer != null ? cellRenderer : new DefaultListCellRenderer();
        firePropertyChange(CELL_RENDERER_PROPERTY, old, this.cellRenderer);
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        if (!(orientation == SwingConstants.HORIZONTAL || orientation == SwingConstants.VERTICAL)) {
            throw new IllegalArgumentException();
        }
        int old = this.orientation;
        this.orientation = orientation;
        firePropertyChange(ORIENTATION_PROPERTY, old, this.orientation);
    }

    @Nullable
    public Component getSourceFooter() {
        return sourceFooter;
    }

    public void setSourceFooter(@Nullable Component sourceFooter) {
        Component old = this.sourceFooter;
        this.sourceFooter = sourceFooter;
        firePropertyChange(SOURCE_FOOTER_PROPERTY, old, this.sourceFooter);
    }

    @Nullable
    public Component getSourceHeader() {
        return sourceHeader;
    }

    public void setSourceHeader(@Nullable Component sourceHeader) {
        Component old = this.sourceHeader;
        this.sourceHeader = sourceHeader;
        firePropertyChange(SOURCE_HEADER_PROPERTY, old, this.sourceHeader);
    }

    @Nullable
    public Component getTargetFooter() {
        return targetFooter;
    }

    public void setTargetFooter(@Nullable Component targetFooter) {
        Component old = this.targetFooter;
        this.targetFooter = targetFooter;
        firePropertyChange(TARGET_FOOTER_PROPERTY, old, this.targetFooter);
    }

    @Nullable
    public Component getTargetHeader() {
        return targetHeader;
    }

    public void setTargetHeader(@Nullable Component targetHeader) {
        Component old = this.targetHeader;
        this.targetHeader = targetHeader;
        firePropertyChange(TARGET_HEADER_PROPERTY, old, this.targetHeader);
    }
    //</editor-fold>

    @Nonnull
    public List<E> getSelectedValues() {
        return JLists.stream(targetModel).collect(Collectors.toList());
    }

    private JButton newButton(String actionKey, FontAwesome icon) {
        JButton result = new JButton(getActionMap().get(actionKey));
        result.setIcon(icon.getIcon(result.getForeground(), 16f));
        return result;
    }

    //<editor-fold defaultstate="collapsed" desc="DataTransfer">
    private static final class CustomTransferHandler extends TransferHandler {

        private static final DataFlavor DATA_FLAVOR = new ActivationDataFlavor(JList.class, DataFlavor.javaJVMLocalObjectMimeType, "List");

        private final JList<?> sourceList;
        private final JList<?> targetList;

        public CustomTransferHandler(JList<?> sourceList, JList<?> targetList) {
            this.sourceList = sourceList;
            this.targetList = targetList;
        }

        private boolean isValidComponent(Component c) {
            return c == sourceList || c == targetList;
        }

        private static JList<?> getData(TransferSupport support) {
            try {
                return (JList<?>) support.getTransferable().getTransferData(DATA_FLAVOR);
            } catch (UnsupportedFlavorException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDrop()
                    && support.isDataFlavorSupported(DATA_FLAVOR)
                    && isValidComponent(support.getComponent())
                    && isValidComponent(getData(support));
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (canImport(support)) {
                JList<?> from = getData(support);
                JList<?> to = (JList<?>) support.getComponent();

                int dropIndex = ((JList.DropLocation) support.getDropLocation()).getIndex();
                int[] selection = JLists.getSelectionIndexStream(from.getSelectionModel()).toArray();
                if (from == to && selection[0] < dropIndex) {
                    dropIndex = dropIndex - selection.length;
                }

                JLists.move((DefaultListModel) from.getModel(), (DefaultListModel) to.getModel(), selection, dropIndex);
                to.getSelectionModel().setSelectionInterval(dropIndex, dropIndex + selection.length - 1);

                return true;
            }
            return false;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return new DataHandler(c, DATA_FLAVOR.getMimeType());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Commands">
    private static final class SelectCommand<T> extends JCommand<JListSelection<T>> {

        @Override
        public void execute(JListSelection<T> c) throws Exception {
            int[] selection = JLists.getSelectionIndexStream(c.sourceList.getSelectionModel()).toArray();
            c.sourceList.getSelectionModel().clearSelection();
            c.targetList.getSelectionModel().clearSelection();
            int position = c.targetModel.size();
            for (int i = selection.length - 1; i >= 0; i--) {
                c.targetModel.insertElementAt(c.sourceModel.remove(selection[i]), position);
            }
            c.targetList.getSelectionModel().setSelectionInterval(c.targetModel.getSize() - selection.length, c.targetModel.getSize() - 1);
        }

        @Override
        public boolean isEnabled(JListSelection<T> c) {
            return !c.sourceList.getSelectionModel().isSelectionEmpty();
        }

        @Override
        public ActionAdapter toAction(JListSelection<T> c) {
            return super.toAction(c).withWeakListSelectionListener(c.sourceList.getSelectionModel());
        }
    }

    private static final class UnselectCommand<T> extends JCommand<JListSelection<T>> {

        @Override
        public void execute(JListSelection<T> c) throws Exception {
            int[] selection = JLists.getSelectionIndexStream(c.targetList.getSelectionModel()).toArray();
            c.sourceList.getSelectionModel().clearSelection();
            c.targetList.getSelectionModel().clearSelection();
            int position = c.sourceModel.size();
            for (int i = selection.length - 1; i >= 0; i--) {
                c.sourceModel.insertElementAt(c.targetModel.remove(selection[i]), position);
            }
            c.sourceList.getSelectionModel().setSelectionInterval(c.sourceModel.getSize() - selection.length, c.sourceModel.getSize() - 1);
        }

        @Override
        public boolean isEnabled(JListSelection<T> c) {
            return !c.targetList.getSelectionModel().isSelectionEmpty();
        }

        @Override
        public ActionAdapter toAction(JListSelection<T> c) {
            return super.toAction(c).withWeakListSelectionListener(c.targetList.getSelectionModel());
        }
    }

    private static void addListDataListener(JCommand.ActionAdapter action, JList<?> list) {
        ListDataListener listener = JLists.dataListenerOf(o -> action.refreshActionState());
        list.getModel().addListDataListener(listener);
        list.addPropertyChangeListener("model", evt -> {
            ((ListModel) evt.getOldValue()).removeListDataListener(listener);
            ((ListModel) evt.getNewValue()).addListDataListener(listener);
            action.refreshActionState();
        });
    }

    private static final class SelectAllCommand<T> extends JCommand<JListSelection<T>> {

        @Override
        public void execute(JListSelection<T> c) throws Exception {
            c.sourceList.getSelectionModel().clearSelection();
            c.targetList.getSelectionModel().clearSelection();
            while (!c.sourceModel.isEmpty()) {
                c.targetModel.addElement(c.sourceModel.remove(0));
            }
        }

        @Override
        public boolean isEnabled(JListSelection<T> c) {
            return !c.sourceModel.isEmpty();
        }

        @Override
        public ActionAdapter toAction(JListSelection<T> c) {
            ActionAdapter result = super.toAction(c);
            addListDataListener(result, c.sourceList);
            return result;
        }
    }

    private static final class UnselectAllCommand<T> extends JCommand<JListSelection<T>> {

        @Override
        public void execute(JListSelection<T> c) throws Exception {
            c.sourceList.getSelectionModel().clearSelection();
            c.targetList.getSelectionModel().clearSelection();
            while (!c.targetModel.isEmpty()) {
                c.sourceModel.addElement(c.targetModel.remove(0));
            }
        }

        @Override
        public boolean isEnabled(JListSelection<T> c) {
            return !c.targetModel.isEmpty();
        }

        @Override
        public ActionAdapter toAction(JListSelection<T> c) {
            ActionAdapter result = super.toAction(c);
            addListDataListener(result, c.targetList);
            return result;
        }
    }

    private static final class ApplyHorizontalCommand extends JCommand<JListSelection<?>> {

        @Override
        public void execute(JListSelection<?> c) throws Exception {
            c.setOrientation(c.getOrientation() != SwingConstants.HORIZONTAL ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL);
        }

        @Override
        public boolean isSelected(JListSelection<?> c) {
            return c.getOrientation() == SwingConstants.HORIZONTAL;
        }

        @Override
        public ActionAdapter toAction(JListSelection<?> c) {
            return super.toAction(c).withWeakPropertyChangeListener(c, ORIENTATION_PROPERTY);
        }
    }

    private static final class ApplyVerticalCommand extends JCommand<JListSelection<?>> {

        @Override
        public void execute(JListSelection<?> c) throws Exception {
            c.setOrientation(c.getOrientation() != SwingConstants.VERTICAL ? SwingConstants.VERTICAL : SwingConstants.HORIZONTAL);
        }

        @Override
        public boolean isSelected(JListSelection<?> c) {
            return c.getOrientation() == SwingConstants.VERTICAL;
        }

        @Override
        public ActionAdapter toAction(JListSelection<?> c) {
            return super.toAction(c).withWeakPropertyChangeListener(c, ORIENTATION_PROPERTY);
        }
    }
    //</editor-fold>
}
