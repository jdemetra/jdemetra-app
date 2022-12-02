/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.datatransfer;

import com.google.common.base.Throwables;
import demetra.information.InformationSet;
import demetra.information.InformationSetSerializer;
import demetra.toolkit.io.xml.information.XmlInformationSet;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Jean Palate
 */
public class TransferableXmlInformation<T> implements Transferable {

    private final T obj_;
    private final InformationSetSerializer<T> serializer;
    private final DataFlavor local_;
    private final String type_, version_;
    private static JAXBContext context;

    static {
        try {
            context = JAXBContext.newInstance(XmlInformationSet.class);
        } catch (JAXBException ex) {
            context = null;
        }
    }

    public static <T> T read(Transferable dataobj, InformationSetSerializer<T> serializer, Class<T> tclass, String type, String version) {
        try {
            DataFlavor local = newLocalObjectDataFlavor(tclass);
            if (dataobj.isDataFlavorSupported(local)) {
                return (T) dataobj.getTransferData(local);
            }
            DataFlavor xmlflavor = BasicFormatter.DEMETRA;
            if (!dataobj.isDataFlavorSupported(xmlflavor)) {
                return null;
            }
            String str = (String) dataobj.getTransferData(BasicFormatter.DEMETRA);
            StringReader reader = new StringReader(str);

//	    byte[] bytearray = (byte[]) dataobj.getTransferData(xmlflavor);
//	    ByteArrayInputStream mem = new ByteArrayInputStream(bytearray, 0,
//		    bytearray.length);
//	    mem.reset();
            Unmarshaller unmarshaller = context.createUnmarshaller();
            XmlInformationSet x = (XmlInformationSet) unmarshaller.unmarshal(reader);
            if (x == null) {
                return null;
            }
            InformationSet info = x.create();
            if (type != null && !info.isContent(type, version)) {
                return null;
            }
            return serializer.read(info);
        } catch (UnsupportedFlavorException | IOException | JAXBException ex) {
            return null;
        }

    }

    public TransferableXmlInformation(T obj, InformationSetSerializer<T> serializer, String type, String version) {
        obj_ = obj;
        this.serializer=serializer;
        type_ = type;
        version_ = version;
        local_ = newLocalObjectDataFlavor(serializer.getClass());
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{BasicFormatter.DEMETRA, local_};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(BasicFormatter.DEMETRA) || flavor.equals(local_);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (obj_ == null) {
            return null;
        }
        if (flavor.equals(local_)) {
            return obj_;
        }
        if (!flavor.equals(BasicFormatter.DEMETRA)) {
            return null;
        }
        StringWriter writer = new StringWriter();
        try {
            InformationSet info = serializer.write(obj_, false);
            if (info == null) {
                return null;
            }
            info.setContent(type_, version_);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            XmlInformationSet xml = new XmlInformationSet();
            xml.copy(info);
            marshaller.marshal(xml, writer);
            writer.flush();
            return writer.toString();
        } catch (Exception ex) {
            return null;
        }
    }

    static DataFlavor newLocalObjectDataFlavor(Class<?> clazz) {
        try {
            return new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + clazz.getName());
        } catch (ClassNotFoundException ex) {
            throw Throwables.propagate(ex);
        }
    }
}
