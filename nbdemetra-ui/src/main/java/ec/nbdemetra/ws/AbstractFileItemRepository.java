/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws;

import com.google.common.base.Throwables;
import ec.tss.xml.IXmlConverter;
import ec.tss.xml.information.XmlInformationSet;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.information.InformationSetSerializable;
import ec.tstoolkit.utilities.IModifiable;
import ec.tstoolkit.utilities.Paths;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Jean Palate
 */
public abstract class AbstractFileItemRepository<D> extends AbstractWorkspaceItemRepository<D> {

    static final JAXBContext XML_INFORMATION_SET_CONTEXT;

    static {
        try {
            XML_INFORMATION_SET_CONTEXT = JAXBContext.newInstance(XmlInformationSet.class);
        } catch (JAXBException ex) {
            throw Throwables.propagate(ex);
        }
    }

    protected String fullName(WorkspaceItem<D> item, String repo, boolean createDir) {
        if (item.getOwner() == null) {
            return null;
        }
        String folder = FileRepository.getRepositoryFolder(item.getOwner(), repo, createDir);
        String sfile = Paths.concatenate(folder, item.getIdentifier());
        sfile = Paths.addExtension(sfile, "xml");
        return sfile;
    }

//    protected <X extends IXmlConverter<D>> boolean loadXml(WorkspaceItem<D> item, String repo, Class<X> xclass) {
//        String sfile = fullName(item, repo, false);
//        if (sfile == null) {
//            return false;
//        }
//        D el=loadLegacy(sfile, xclass);
//        if (el == null)
//            el=LoadInfo(sfile, this.)
//        item.setElement(el);
//        item.setDirty(false);
//        return el != null;
//    }
    public static <S, X extends IXmlConverter<S>> S loadLegacy(String sfile, Class<X> xclass) {
        File file = new File(sfile);
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

    public static <X> X loadXmlLegacy(String sfile, Class<X> xclass) {
        File file = new File(sfile);
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

    public static <X extends InformationSetSerializable> X loadInfo(String sfile, Class<X> xclass) {
        File file = new File(sfile);
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

//    protected <X extends IXmlConverter<D>> boolean saveXml(WorkspaceItem item, String repo, Class<X> xclass) {
//        String sfile = fullName(item, repo, true);
//        if (sfile == null) {
//            return false;
//        }
//        return saveLegacy(sfile, item, xclass);
//    }
    public static <T, X extends IXmlConverter<T>> boolean saveLegacy(String sfile, WorkspaceItem item, Class<X> xclass) {
        File file = new File(sfile);
        try (FileOutputStream stream = new FileOutputStream(file)) {
            //XMLOutputFactory factory=XMLOutputFactory.newInstance();
            try (OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {

                JAXBContext context = JAXBContext.newInstance(xclass);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                X x = xclass.newInstance();
                x.copy((T) item.getElement());
                marshaller.marshal(x, writer);
                item.resetDirty();
                writer.flush();
                return true;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    public static <T extends IModifiable, X extends IXmlConverter<T>> boolean saveLegacy(String sfile, T item, Class<X> xclass) {
        File file = new File(sfile);
        try (FileOutputStream stream = new FileOutputStream(file)) {
            //XMLOutputFactory factory=XMLOutputFactory.newInstance();
            try (OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {

                JAXBContext context = JAXBContext.newInstance(xclass);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                X x = xclass.newInstance();
                x.copy(item);
                marshaller.marshal(x, writer);
                writer.flush();
                item.resetDirty();
                return true;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    public static <T extends InformationSetSerializable> boolean saveInfo(String sfile, T item) {
        File file = new File(sfile);
        try (FileOutputStream stream = new FileOutputStream(file)) {
            //XMLOutputFactory factory=XMLOutputFactory.newInstance();
            try (OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {

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
            }
        } catch (Exception ex) {
            return false;
        }
    }

    protected boolean delete(WorkspaceItem<D> doc, String repo) {
        String sfile = fullName(doc, repo, false);
        if (sfile == null) {
            return false;
        }
        File file = new File(sfile);
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }
}
