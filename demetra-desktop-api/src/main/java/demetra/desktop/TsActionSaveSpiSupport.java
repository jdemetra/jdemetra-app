package demetra.desktop;

import demetra.desktop.beans.BeanEditor;
import demetra.desktop.properties.PropertySheetDialogBuilder;
import demetra.desktop.util.SingleFileExporter;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsInformationType;
import ec.util.various.swing.OnEDT;
import java.beans.IntrospectionException;
import java.io.File;
import java.util.List;
import java.util.function.Function;
import javax.swing.filechooser.FileFilter;
import nbbrd.design.LombokWorkaround;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.nodes.Sheet;

@lombok.Builder
public final class TsActionSaveSpiSupport implements TsActionSaveSpi {

    @lombok.experimental.Delegate
    @lombok.NonNull
    private final NamedService name;

    @lombok.NonNull
    private final FileChooserBuilder fileChooser;

    @lombok.NonNull
    private final Object bean;

    @lombok.NonNull
    private final BeanEditor editor;

    @lombok.NonNull
    private final Task task;

    @LombokWorkaround
    @lombok.NonNull
    public static Builder builder() {
        return new Builder()
                .editor(new PropertySheetDialogBuilder()::editBean);
    }

    @Override
    public void save(List<TsCollection> input) {
        SingleFileExporter.saveToFile(
                fileChooser,
                file -> editor.editBean(bean, this::reportError),
                file -> store(input, file)
        );
    }

    private void reportError(IntrospectionException error) {
        // TODO
    }

    @OnEDT
    private void store(List<TsCollection> data, File file) {
        String displayName = name.getDisplayName();
        SingleFileExporter
                .builder()
                .file(file)
                .progressLabel("Saving to " + displayName)
                .onErrorNotify("Saving to " + displayName + " failed")
                .onSuccessNotify(displayName + " saved")
                .build()
                .execAsync(task.getTask(data, bean));
    }

    public static <T> BeanEditor newEditor(Class<T> type, Function<T, Sheet> toSheet) {
        return options -> new PropertySheetDialogBuilder()
                .title("Options")
                .editSheet(toSheet.apply(type.cast(options)));
    }

    public static FileFilter newFileChooserFilter(java.io.FileFilter filter, String description) {
        return new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || filter.accept(file);
            }

            @Override
            public String getDescription() {
                return description;
            }
        };
    }

    public static TsCollection flatLoad(List<TsCollection> data) {
        return data
                .stream()
                .map(col -> col.load(TsInformationType.All, TsManager.get()))
                .flatMap(TsCollection::stream)
                .collect(TsCollection.toTsCollection());
    }

    @FunctionalInterface
    public interface Task {

        @OnEDT
        SingleFileExporter.SingleFileTask getTask(List<TsCollection> data, Object options);
    }
}
