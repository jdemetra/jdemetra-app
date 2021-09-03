/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.tss.datatransfer;

import demetra.desktop.datatransfer.DataTransfers;
import ec.tss.xml.IXmlConverter;
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
public class TransferableXml<T, X extends IXmlConverter<T>> implements Transferable {

    private final Class<X> xclass_;
    private final T obj_;
    private final DataFlavor local_;

    public static <T, X extends IXmlConverter<T>> T read(Transferable dataobj, Class<T> tclass, Class<X> xclass) {
        try {
            DataFlavor local = DataTransfers.newLocalObjectDataFlavor(tclass);
            if (dataobj.isDataFlavorSupported(local)) {
                return (T) dataobj.getTransferData(local);
            } else {
                return read(dataobj, xclass);
            }
        } catch (UnsupportedFlavorException | IOException ex) {
            return null;
        }
    }

    public static <T, X extends IXmlConverter<T>> T read(Transferable dataobj, Class<X> xclass) {
        try {
            DataFlavor xmlflavor = BasicFormatter.TEXT;
            if (!dataobj.isDataFlavorSupported(xmlflavor)) {
                return null;
            }
            String str = (String) dataobj.getTransferData(BasicFormatter.TEXT);
            StringReader reader = new StringReader(str);

//	    byte[] bytearray = (byte[]) dataobj.getTransferData(xmlflavor);
//	    ByteArrayInputStream mem = new ByteArrayInputStream(bytearray, 0,
//		    bytearray.length);
//	    mem.reset();
            JAXBContext context = JAXBContext.newInstance(xclass);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            X x = (X) unmarshaller.unmarshal(reader);
            return x.create();
        } catch (UnsupportedFlavorException | IOException | JAXBException ex) {
            return null;
        }

    }

    public TransferableXml(T obj, Class<X> xclass) {
        xclass_ = xclass;
        obj_ = obj;
        local_ = DataTransfers.newLocalObjectDataFlavor(obj.getClass());
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{local_, BasicFormatter.TEXT};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(local_) || flavor.equals(BasicFormatter.TEXT);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (obj_ == null) {
            return null;
        }
        if (flavor.equals(local_)) {
            return obj_;
        }
        if (!flavor.equals(BasicFormatter.TEXT)) {
            return null;
        }
        StringWriter writer = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(xclass_);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            X x = xclass_.newInstance();
            x.copy(obj_);
            marshaller.marshal(x, writer);
            writer.flush();
            return writer.toString();
        } catch (JAXBException | InstantiationException | IllegalAccessException ex) {
            return null;
        }
    }
}
