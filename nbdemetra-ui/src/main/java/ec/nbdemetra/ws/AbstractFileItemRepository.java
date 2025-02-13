/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws;

import com.google.common.base.Throwables;
import ec.demetra.workspace.WorkspaceFamily;
import ec.demetra.workspace.file.FileWorkspace;
import ec.tss.xml.IXmlConverter;
import ec.tss.xml.information.XmlInformationSet;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.information.InformationSetSerializable;
import ec.tstoolkit.utilities.IModifiable;
import ec.tstoolkit.utilities.Paths;
import org.openide.util.Exceptions;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 *
 * @author Jean Palate
 */
public abstract class AbstractFileItemRepository<D> extends AbstractWorkspaceItemRepository<D> {

    @Deprecated
    static final JAXBContext XML_INFORMATION_SET_CONTEXT;

    static {
        try {
            XML_INFORMATION_SET_CONTEXT = JAXBContext.newInstance(XmlInformationSet.class);
        } catch (JAXBException ex) {
            throw Throwables.propagate(ex);
        }
    }

    private static ec.demetra.workspace.WorkspaceItem toFileItem(WorkspaceItem item) {
        return ec.demetra.workspace.WorkspaceItem.builder()
                .family(WorkspaceFamily.of(item.getFamily()))
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
            try (FileWorkspace storage = FileWorkspace.open(file.toPath())) {
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
            try (FileWorkspace storage = FileWorkspace.open(file.toPath())) {
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
            try (FileWorkspace storage = FileWorkspace.open(file.toPath())) {
                storage.delete(toFileItem(item));
                return true;
            } catch (IOException | InvalidPathException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return false;
    }

    @Deprecated
    protected String fullName(WorkspaceItem<D> item, String repo, boolean createDir) {
        if (item.getOwner() == null) {
            return null;
        }
        String folder = FileRepository.getRepositoryFolder(item.getOwner(), repo, createDir);
        String sfile = Paths.concatenate(folder, item.getIdentifier());
        sfile = Paths.addExtension(sfile, "xml");
        return sfile;
    }

    @Deprecated
    public static <S, X extends IXmlConverter<S>> S loadLegacy(String sfile, Class<X> xclass) {
        File file = java.nio.file.Paths.get(sfile).toFile();
        if (!file.exists()) {
            return null;
        }
        if (!file.canRead()) {
            return null;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(xclass);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            X x = (X) unmarshaller.unmarshal(file);
            return x.create();
        } catch (Exception ex) {
            return null;

        }
    }

    @Deprecated
    public static <X> X loadXmlLegacy(String sfile, Class<X> xclass) {
        File file = java.nio.file.Paths.get(sfile).toFile();
        if (!file.exists()) {
            return null;
        }
        if (!file.canRead()) {
            return null;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(xclass);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            X x = (X) unmarshaller.unmarshal(file);
            return x;
        } catch (Exception ex) {
            return null;

        }
    }

    @Deprecated
    public static <X extends InformationSetSerializable> X loadInfo(String sfile, Class<X> xclass) {
        File file = java.nio.file.Paths.get(sfile).toFile();
        if (!file.exists()) {
            return null;
        }
        if (!file.canRead()) {
            return null;
        }

        try {
            Unmarshaller unmarshaller = XML_INFORMATION_SET_CONTEXT.createUnmarshaller();
            XmlInformationSet x = (XmlInformationSet) unmarshaller.unmarshal(file);
            X t = xclass.newInstance();
            if (!t.read(x.create())) {
                return null;
            }
            return t;
        } catch (JAXBException | InstantiationException | IllegalAccessException ex) {
            return null;
        }
    }

    @Deprecated
    public static <T, X extends IXmlConverter<T>> boolean saveLegacy(String sfile, WorkspaceItem item, Class<X> xclass) {
        Path file = java.nio.file.Paths.get(sfile);
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            JAXBContext context = JAXBContext.newInstance(xclass);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            X x = xclass.newInstance();
            x.copy((T) item.getElement());
            marshaller.marshal(x, writer);
            item.resetDirty();
            writer.flush();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Deprecated
    public static <T extends IModifiable, X extends IXmlConverter<T>> boolean saveLegacy(String sfile, T item, Class<X> xclass) {
        Path file = java.nio.file.Paths.get(sfile);
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            JAXBContext context = JAXBContext.newInstance(xclass);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            X x = xclass.newInstance();
            x.copy(item);
            marshaller.marshal(x, writer);
            writer.flush();
            item.resetDirty();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Deprecated
    public static <T extends InformationSetSerializable> boolean saveInfo(String sfile, T item) {
        Path file = java.nio.file.Paths.get(sfile);
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                Marshaller marshaller = XML_INFORMATION_SET_CONTEXT.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                InformationSet info = item.write(false);
                if (info == null) {
                    return false;
                }
                XmlInformationSet x = new XmlInformationSet();
                x.copy(info);
                marshaller.marshal(x, writer);
                writer.flush();
                return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Deprecated
    protected boolean delete(WorkspaceItem<D> doc, String repo) {
        String sfile = fullName(doc, repo, false);
        if (sfile == null) {
            return false;
        }
        File file = java.nio.file.Paths.get(sfile).toFile();
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }
}
