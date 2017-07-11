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
package ec.util.various.swing;

import static ec.util.chart.swing.SwingColorSchemeSupport.withAlpha;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;

/**
 *
 * @author Philippe Charles
 */
public final class BasicFileViewer extends JPanel {

    //<editor-fold defaultstate="collapsed" desc="API definition">
    public static final String STATE_PROPERTY = "state";
    public static final String FILE_HANDLER_PROPERTY = "fileHandler";
    public static final String FILE_PROPERTY = "file";
    public static final String START_RENDERER_PROPERTY = "startRenderer";
    public static final String DRAG_RENDERER_PROPERTY = "dragRenderer";
    public static final String LOAD_RENDERER_PROPERTY = "loadRenderer";
    public static final String FAILURE_RENDERER_PROPERTY = "failureRenderer";

    public enum State {

        READY, LOADING, LOADED, FAILED, DRAGGING
    }

    public interface BasicFileHandler extends FileFilter {

        Object asyncLoad(File file, ProgressCallback progress) throws Exception;

        boolean isViewer(Component c);

        Component borrowViewer(Object data);

        void recycleViewer(Component c);
    }

    public interface ProgressCallback {

        void setProgress(int min, int max, int value);
    }

    public interface StartRenderer {

        Component getStartComponent();
    }

    public interface DragRenderer {

        Component getDragComponent(DropTargetDragEvent dtde);
    }

    public interface LoadRenderer {

        static final int NO_PROGRESS = -1;

        Component getLoadComponent(File file, int progress);
    }

    public interface FailureRenderer {

        Component getFailureComponent(File file, Throwable cause);
    }
    //</editor-fold>

    private Component viewer;
    private State state;
    //
    private BasicFileHandler fileHandler;
    private File file;
    private StartRenderer startRenderer;
    private DragRenderer dragRenderer;
    private LoadRenderer loadRenderer;
    private FailureRenderer failureRenderer;
    //
    private DropTargetDragEvent dragEvent;
    private Throwable cause;

    public BasicFileViewer() {
        this.viewer = null;
        this.state = State.READY;
        this.fileHandler = null;
        this.file = null;
        this.startRenderer = DefaultStartRenderer.INSTANCE;
        this.dragRenderer = DefaultDragRenderer.INSTANCE;
        this.loadRenderer = DefaultLoadRenderer.INSTANCE;
        this.failureRenderer = DefaultFailureRenderer.INSTANCE;

        setTransferHandler(new FileTransferHandler());
        try {
            getDropTarget().addDropTargetListener(new DropTargetAdapter() {
                State saved;

                @Override
                public void dragEnter(DropTargetDragEvent dtde) {
                    if (state != State.LOADING) {
                        dragEvent = dtde;
                        saved = state;
                        setState(State.DRAGGING);
                    }
                }

                @Override
                public void dragExit(DropTargetEvent dte) {
                    if (state != State.LOADING) {
                        setState(saved);
                    }
                }

                @Override
                public void drop(DropTargetDropEvent dtde) {
                    dragExit(dtde);
                }
            });
        } catch (TooManyListenersException ex) {
            Logger.getLogger(BasicFileViewer.class.getName()).log(Level.SEVERE, null, ex);
        }

        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case STATE_PROPERTY:
                    onStateChange();
                    break;
                case FILE_HANDLER_PROPERTY:
                    onFileHandlerChange();
                    break;
                case FILE_PROPERTY:
                    onFileChange();
                    break;
            }
        });

        setLayout(new BorderLayout());
        add(startRenderer.getStartComponent(), BorderLayout.CENTER);
    }

    //<editor-fold defaultstate="collapsed" desc="Events handlers">
    private void onStateChange() {
        switch (state) {
            case DRAGGING:
                switchToComponent(dragRenderer.getDragComponent(dragEvent));
                break;
            case FAILED:
                switchToComponent(failureRenderer.getFailureComponent(file, cause));
                break;
            case LOADED:
                switchToComponent(viewer);
                break;
            case LOADING:
                switchToComponent(loadRenderer.getLoadComponent(file, LoadRenderer.NO_PROGRESS));
                recycleViewer();
                break;
            case READY:
                switchToComponent(startRenderer.getStartComponent());
                recycleViewer();
                break;
        }
    }

    private void onFileHandlerChange() {
    }

    private void onFileChange() {
        if (file == null) {
            setState(State.READY);
            return;
        }
        if (fileHandler == null
                || !fileHandler.accept(file)
                || state == State.LOADING) {
            return;
        }
        setState(State.LOADING);
        new SwingWorker<Object, Integer>() {
            @Override
            protected Object doInBackground() throws Exception {
                return fileHandler.asyncLoad(file, (min, max, value) -> publish((value - min) * 100 / (max - min)));
            }

            @Override
            protected void process(List<Integer> chunks) {
                if (!chunks.isEmpty()) {
                    switchToComponent(loadRenderer.getLoadComponent(file, chunks.get(chunks.size() - 1)));
                }
            }

            @Override
            protected void done() {
                try {
                    viewer = fileHandler.borrowViewer(get());
                    if (viewer instanceof JComponent) {
                        ((JComponent) viewer).setTransferHandler(null);
                    }
                    cause = null;
                    setState(State.LOADED);
                } catch (InterruptedException ex) {
                    cause = ex;
                    setState(State.FAILED);
                } catch (ExecutionException ex) {
                    cause = ex.getCause();
                    setState(State.FAILED);
                }
            }
        }.execute();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    private void setState(State state) {
        State old = this.state;
        this.state = state;
        firePropertyChange(STATE_PROPERTY, old, this.state);
    }

    public BasicFileHandler getFileHandler() {
        return fileHandler;
    }

    public void setFileHandler(BasicFileHandler fileHandler) {
        BasicFileHandler old = this.fileHandler;
        this.fileHandler = fileHandler;
        firePropertyChange(FILE_HANDLER_PROPERTY, old, this.fileHandler);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        File old = this.file;
        this.file = file;
        firePropertyChange(FILE_PROPERTY, old, this.file);
    }

    @Nonnull
    public StartRenderer getStartRenderer() {
        return startRenderer;
    }

    public void setStartRenderer(@Nullable StartRenderer startRenderer) {
        StartRenderer old = this.startRenderer;
        this.startRenderer = startRenderer != null ? startRenderer : DefaultStartRenderer.INSTANCE;
        firePropertyChange(START_RENDERER_PROPERTY, old, this.startRenderer);
    }

    @Nonnull
    public DragRenderer getDragRenderer() {
        return dragRenderer;
    }

    public void setDragRenderer(@Nullable DragRenderer dragRenderer) {
        DragRenderer old = this.dragRenderer;
        this.dragRenderer = dragRenderer != null ? dragRenderer : DefaultDragRenderer.INSTANCE;
        firePropertyChange(DRAG_RENDERER_PROPERTY, old, this.dragRenderer);
    }

    @Nonnull
    public LoadRenderer getLoadRenderer() {
        return loadRenderer;
    }

    public void setLoadRenderer(@Nullable LoadRenderer loadRenderer) {
        LoadRenderer old = this.loadRenderer;
        this.loadRenderer = loadRenderer != null ? loadRenderer : DefaultLoadRenderer.INSTANCE;
        firePropertyChange(LOAD_RENDERER_PROPERTY, old, this.loadRenderer);
    }

    @Nonnull
    public FailureRenderer getFailureRenderer() {
        return failureRenderer;
    }

    public void setFailureRenderer(@Nullable FailureRenderer failureRenderer) {
        FailureRenderer old = this.failureRenderer;
        this.failureRenderer = failureRenderer != null ? failureRenderer : DefaultFailureRenderer.INSTANCE;
        firePropertyChange(FAILURE_RENDERER_PROPERTY, old, this.failureRenderer);
    }
    //</editor-fold>

    private void switchToComponent(Component c) {
        removeAll();
        if (c != null) {
            add(c, BorderLayout.CENTER);
            validate();
            invalidate();
            repaint();
            c.setSize(getSize());
        }
    }

    private void recycleViewer() {
        if (viewer != null && fileHandler.isViewer(viewer)) {
            fileHandler.recycleViewer(viewer);
            viewer = null;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Data transfer">
    private final class FileTransferHandler extends TransferHandler {

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            boolean result = support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
                    || support.isDataFlavorSupported(FILE_DATA_FLAVOR);
            if (result && support.isDrop()) {
                support.setDropAction(LINK);
            }
            return result;
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            File file = getSingleFile(support.getTransferable());
            if (file != null) {
                setFile(file);
                return true;
            }
            return false;
        }
    }

    private static final DataFlavor FILE_DATA_FLAVOR = newLocalObjectDataFlavor(File.class);

    private static File getSingleFile(Transferable t) {
        if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                if (files.size() == 1) {
                    return files.get(0);
                }
            } catch (UnsupportedFlavorException | IOException ex) {
            }
        } else if (t.isDataFlavorSupported(FILE_DATA_FLAVOR)) {
            try {
                return (File) t.getTransferData(FILE_DATA_FLAVOR);
            } catch (UnsupportedFlavorException | IOException ex) {
            }
        }
        return null;
    }

    private static DataFlavor newLocalObjectDataFlavor(Class<?> clazz) {
        try {
            return new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + clazz.getName());
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Default renderers">
    private static final class XLabel extends JLabel {

        public XLabel() {
            setOpaque(true);
            JList resource = new JList();
            setBackground(resource.getSelectionForeground());
            setForeground(resource.getSelectionBackground());
            setFont(resource.getFont().deriveFont(resource.getFont().getSize2D() * 2));
            setHorizontalAlignment(SwingConstants.CENTER);
        }
    }

    private static final class DefaultStartRenderer implements StartRenderer {

        public static final DefaultStartRenderer INSTANCE = new DefaultStartRenderer();

        private final JLabel component;

        private DefaultStartRenderer() {
            this.component = new JLabel();
            JList resource = new JList();
            component.setOpaque(true);
            component.setHorizontalAlignment(SwingConstants.CENTER);
            component.setFont(resource.getFont().deriveFont(resource.getFont().getSize2D() * 2));
            component.setBackground(resource.getBackground());
            component.setForeground(withAlpha(resource.getForeground(), 100));
            component.setText("Drop file here");
        }

        @Override
        public Component getStartComponent() {
            return component;
        }
    }

    private static final class DefaultDragRenderer implements DragRenderer {

        public static final DefaultDragRenderer INSTANCE = new DefaultDragRenderer();

        private final JLabel component;

        private DefaultDragRenderer() {
            this.component = new JLabel();
            JList resource = new JList();
            component.setOpaque(true);
            component.setHorizontalAlignment(SwingConstants.CENTER);
            component.setFont(resource.getFont().deriveFont(resource.getFont().getSize2D() * 2));
            component.setBackground(withAlpha(resource.getSelectionBackground(), 200));
            component.setForeground(resource.getSelectionForeground());
            component.setBorder(ModernUI.createDropBorder(resource.getSelectionForeground()));
            component.setText("Drop file here");
        }

        @Override
        public Component getDragComponent(DropTargetDragEvent dtde) {
            return component;
        }
    }

    private static final class DefaultLoadRenderer implements LoadRenderer {

        public static final DefaultLoadRenderer INSTANCE = new DefaultLoadRenderer();

        private final XLabel component;

        private DefaultLoadRenderer() {
            this.component = new XLabel();
        }

        @Override
        public Component getLoadComponent(File file, int progress) {
            if (progress <= 0) {
                component.setText("Loading " + file.getName());
            } else {
                component.setText("<html><center><b>" + file.getName() + "</b><br>" + progress + "%");
            }
            return component;
        }
    }

    private static final class DefaultFailureRenderer implements FailureRenderer {

        public static final DefaultFailureRenderer INSTANCE = new DefaultFailureRenderer();

        private final XLabel component;

        private DefaultFailureRenderer() {
            this.component = new XLabel();
            component.setIcon(FontAwesome.FA_EXCLAMATION_CIRCLE.getIcon(component.getForeground(), component.getFont().getSize2D() * 2));
            component.setVerticalTextPosition(JLabel.BOTTOM);
            component.setHorizontalTextPosition(JLabel.CENTER);
            component.setIconTextGap(10);
        }

        @Override
        public Component getFailureComponent(File file, Throwable cause) {
            component.setText("<html><center>" + cause.getClass().getPackage().getName() + ".<b>" + cause.getClass().getSimpleName() + "</b><br><font size=+1>" + cause.getMessage() + "</font><br><br>");
            return component;
        }
    }
    //</editor-fold>
}
