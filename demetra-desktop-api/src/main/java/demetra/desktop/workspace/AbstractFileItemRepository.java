/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.function.Consumer;
import org.openide.util.Exceptions;

/**
 *
 * @author Jean Palate
 * @param <D>
 */
public abstract class AbstractFileItemRepository<D> extends AbstractWorkspaceItemRepository<D> {

    private static demetra.workspace.WorkspaceItem toFileItem(WorkspaceItem item) {
        return demetra.workspace.WorkspaceItem.builder()
                .family(demetra.workspace.WorkspaceFamily.of(item.getFamily()))
                .id(item.getIdentifier())
                .label(item.getDisplayName())
                .readOnly(item.isReadOnly())
                .comments(item.getComments())
                .build();
    }

    private static File decodeFile(WorkspaceItem<?> item) {
        Workspace owner = item.getOwner();
        return owner != null ? FileRepository.decode(owner.getDataSource()) : null;
    }

    protected static <D, R> boolean loadFile(WorkspaceItem<?> item, Consumer<R> onSuccess) {
        File file = decodeFile(item);
        if (file != null) {
            try (demetra.workspace.file.FileWorkspace storage = demetra.workspace.file.FileWorkspace.open(file.toPath())) {
                onSuccess.accept((R) storage.load(toFileItem(item)));
                return true;
            } catch (IOException | InvalidPathException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return false;
    }

    protected static <D, R> boolean storeFile(WorkspaceItem<?> item, R value, Runnable onSuccess) {
        File file = decodeFile(item);
        if (file != null) {
            try (demetra.workspace.file.FileWorkspace storage = demetra.workspace.file.FileWorkspace.open(file.toPath())) {
                storage.store(toFileItem(item), value);
                onSuccess.run();
                return true;
            } catch (IOException | InvalidPathException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return false;
    }

    protected static <D, R> boolean deleteFile(WorkspaceItem<?> item) {
        File file = decodeFile(item);
        if (file != null) {
            try (demetra.workspace.file.FileWorkspace storage = demetra.workspace.file.FileWorkspace.open(file.toPath())) {
                storage.delete(toFileItem(item));
                return true;
            } catch (IOException | InvalidPathException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return false;
    }
}
