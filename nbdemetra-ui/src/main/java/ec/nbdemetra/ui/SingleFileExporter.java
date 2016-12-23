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
package ec.nbdemetra.ui;

import ec.nbdemetra.ui.awt.ShowInFolderActionListener;
import ec.nbdemetra.ui.notification.MessageType;
import ec.nbdemetra.ui.notification.NotifyUtil;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.swing.SwingUtilities;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileChooserBuilder;

/**
 *
 * @author Philippe Charles
 * @since 2.2.0
 */
public final class SingleFileExporter {

    private Supplier<File> fileChooser;
    private Supplier<ProgressHandle> progressHandle;
    private BiConsumer<File, Throwable> error;
    private Consumer<File> success;

    public SingleFileExporter() {
        this.fileChooser = () -> new FileChooserBuilder(SingleFileExporter.class).showSaveDialog();
        this.progressHandle = () -> ProgressHandle.createHandle("Saving");
        this.error = (f, t) -> notifyError(f, t, "Export failed");
        this.success = f -> notifySuccess(f, "Export succeeded");
    }

    @Nonnull
    public SingleFileExporter fileChooser(@Nonnull Supplier<File> fileChooser) {
        this.fileChooser = Objects.requireNonNull(fileChooser);
        return this;
    }

    @Nonnull
    public SingleFileExporter file(@Nonnull File file) {
        Objects.requireNonNull(file);
        return fileChooser(() -> file);
    }

    @Nonnull
    public SingleFileExporter progressHandle(@Nonnull Supplier<ProgressHandle> progressHandle) {
        this.progressHandle = Objects.requireNonNull(progressHandle);
        return this;
    }

    @Nonnull
    public SingleFileExporter progressLabel(@Nonnull String displayName) {
        Objects.requireNonNull(displayName);
        return progressHandle(() -> ProgressHandle.createHandle(displayName));
    }

    @Nonnull
    public SingleFileExporter onError(@Nonnull BiConsumer<File, Throwable> error) {
        this.error = Objects.requireNonNull(error);
        return this;
    }

    @Nonnull
    public SingleFileExporter onErrorNotify(@Nonnull String message) {
        Objects.requireNonNull(message);
        return onError((f, t) -> notifyError(f, t, message));
    }

    @Nonnull
    public SingleFileExporter onSussess(@Nonnull Consumer<File> success) {
        this.success = Objects.requireNonNull(success);
        return this;
    }

    @Nonnull
    public SingleFileExporter onSussessNotify(@Nonnull String message) {
        Objects.requireNonNull(message);
        return onSussess(f -> notifySuccess(f, message));
    }

    public void execAsync(@Nonnull SingleFileTask task) {
        Objects.requireNonNull(task);
        File file = fileChooser.get();
        if (file != null) {
            CompletableFuture
                    .supplyAsync(() -> {
                        ProgressHandle ph = progressHandle.get();
                        ph.start();
                        try {
                            task.exec(file, ph);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        } finally {
                            ph.finish();
                        }
                        return file;
                    })
                    .whenCompleteAsync(this::notify, SwingUtilities::invokeLater);

        }
    }

    public interface SingleFileTask {

        void exec(File file, ProgressHandle ph) throws Exception;
    }

    private void notify(File file, Throwable ex) {
        if (ex != null) {
            Throwable tmp = unwrapException(ex, CompletionException.class, RuntimeException.class);
            error.accept(file, tmp);
        } else {
            success.accept(file);
        }
    }

    private static void notifyError(File file, Throwable t, String message) {
        NotifyUtil.error(message, t.getMessage(), t);
    }

    private static void notifySuccess(File file, String message) {
        NotifyUtil.show(message, "Show in folder", MessageType.SUCCESS, ShowInFolderActionListener.of(file), null, null);
    }

    private static Throwable unwrapException(Throwable ex, Class<? extends Throwable>... types) {
        return ex.getCause() != null && Arrays.stream(types).anyMatch(o -> o.isInstance(ex)) ? unwrapException(ex.getCause(), types) : ex;
    }

    @Nonnull
    public static FileChooserBuilder.SelectionApprover overwriteApprover() {
        return new SaveSelectionApprover();
    }

    private static final class SaveSelectionApprover implements FileChooserBuilder.SelectionApprover {

        @Override
        public boolean approve(File[] selection) {
            if (selection.length > 0 && selection[0].exists()) {
                NotifyDescriptor d = new NotifyDescriptor.Confirmation("Overwrite file?", NotifyDescriptor.OK_CANCEL_OPTION);
                return DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION;
            }
            return selection.length != 0;
        }
    }
}
