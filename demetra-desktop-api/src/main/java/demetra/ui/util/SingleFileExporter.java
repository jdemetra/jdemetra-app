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
package demetra.ui.util;

import demetra.desktop.notification.MessageType;
import demetra.desktop.notification.NotifyUtil;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.SwingUtilities;
import nbbrd.design.LombokWorkaround;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileChooserBuilder;

/**
 *
 * @author Philippe Charles
 * @since 2.2.0
 */
@lombok.Builder
public final class SingleFileExporter {

    @FunctionalInterface
    public interface SingleFileTask {

        void exec(File file, ProgressHandle ph) throws Exception;
    }

    @lombok.NonNull
    private final Supplier<File> fileChooser;

    @lombok.NonNull
    private final Supplier<ProgressHandle> progressHandle;

    @lombok.NonNull
    private final BiConsumer<File, Throwable> onError;

    @lombok.NonNull
    private final Consumer<File> onSuccess;

    public void execAsync(@NonNull SingleFileTask task) {
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

    private void notify(File file, Throwable ex) {
        if (ex != null) {
            Throwable tmp = unwrapException(ex, CompletionException.class, RuntimeException.class);
            onError.accept(file, tmp);
        } else {
            onSuccess.accept(file);
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

    @LombokWorkaround
    public static Builder builder() {
        return new Builder()
                .fileChooser(() -> new FileChooserBuilder(SingleFileExporter.class).showSaveDialog())
                .progressHandle(() -> ProgressHandle.createHandle("Saving"))
                .onError((f, t) -> notifyError(f, t, "Export failed"))
                .onSuccess(f -> notifySuccess(f, "Export succeeded"));
    }

    public static final class Builder {

        @NonNull
        public Builder file(@NonNull File file) {
            Objects.requireNonNull(file);
            return fileChooser(() -> file);
        }

        @NonNull
        public Builder progressLabel(@NonNull String displayName) {
            Objects.requireNonNull(displayName);
            return progressHandle(() -> ProgressHandle.createHandle(displayName));
        }

        @NonNull
        public Builder onErrorNotify(@NonNull String message) {
            Objects.requireNonNull(message);
            return onError((f, t) -> notifyError(f, t, message));
        }

        @NonNull
        public Builder onSuccessNotify(@NonNull String message) {
            Objects.requireNonNull(message);
            return onSuccess(f -> notifySuccess(f, message));
        }
    }

    public static void saveToFile(@NonNull FileChooserBuilder fileChooser, @NonNull Predicate<File> predicate, @NonNull Consumer<File> action) {
        File target = fileChooser.showSaveDialog();
        if (target != null && predicate.test(target)) {
            action.accept(target);
        }
    }

    @NonNull
    public static FileChooserBuilder newFileChooser(@NonNull Class type) {
        return new FileChooserBuilder(type).setSelectionApprover(overwriteApprover());
    }

    public static FileChooserBuilder.@NonNull SelectionApprover overwriteApprover() {
        return SaveSelectionApprover.INSTANCE;
    }

    private enum SaveSelectionApprover implements FileChooserBuilder.SelectionApprover {

        INSTANCE;

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
