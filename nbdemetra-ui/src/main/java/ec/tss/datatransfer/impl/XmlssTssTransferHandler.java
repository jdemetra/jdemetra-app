/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package ec.tss.datatransfer.impl;

import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import ec.nbdemetra.ui.BeanHandler;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.Configurator;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.properties.IBeanEditor;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.properties.OpenIdePropertySheetBeanEditor;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsInformationType;
import ec.tss.TsStatus;
import ec.tss.datatransfer.TssTransferHandler;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;
import ec.tstoolkit.data.DataBlock;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.tstoolkit.utilities.Closeables;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.SystemFlavorMap;
import java.beans.IntrospectionException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.xml.stream.*;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 * XML Spreadsheet (XMLSS).
 *
 * @see http://msdn.microsoft.com/en-us/library/aa140066(v=office.10).aspx
 * @author Jean Palate
 */
@ServiceProvider(service = TssTransferHandler.class, position = 1000)
public class XmlssTssTransferHandler extends TssTransferHandler implements IConfigurable {

    private final static DataFlavor XMLSS = createXmlssDataFlavor();
    // PROPERTIES
    private final NumberFormat numberFormat;
    private final DateFormat dateFormat;
    private final Configurator<XmlssTssTransferHandler> configurator;
    private InternalConfig config;

    public XmlssTssTransferHandler() {
        this.numberFormat = NumberFormat.getNumberInstance(Locale.ROOT);
        numberFormat.setMaximumFractionDigits(9);
        numberFormat.setMaximumIntegerDigits(12);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.configurator = new InternalConfigHandler().toConfigurator(new InternalConfigConverter(), new InternalConfigEditor());
        this.config = new InternalConfig();
    }

    //<editor-fold defaultstate="collapsed" desc="INamedService">
    @Override
    public String getName() {
        return "XMLSS";
    }

    @Override
    public String getDisplayName() {
        return "XML Spreadsheet (XMLSS)";
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TssTransferHandler">
    @Override
    public DataFlavor getDataFlavor() {
        return XMLSS;
    }

    @Override
    public boolean canExportTsCollection(TsCollection col) {
        return config.exportTimeSeries;
    }

    @Override
    public Object exportTsCollection(TsCollection col) throws IOException {
        col.load(TsInformationType.Data);
        return tsCollectionToBytes(col);
    }

    @Override
    public boolean canImportTsCollection(Object obj) {
        return config.importTimeSeries && obj instanceof byte[];
    }

    @Override
    public TsCollection importTsCollection(Object obj) throws IOException {
        return tsCollectionFromBytes((byte[]) obj);
    }

    @Override
    public boolean canExportMatrix(Matrix matrix) {
        return config.exportMatrix && !matrix.isEmpty();
    }

    @Override
    public Object exportMatrix(Matrix matrix) throws IOException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            XMLStreamWriter xml = XMLOutputFactory.newInstance().createXMLStreamWriter(stream);
            try (Closeable c = Closeables.asCloseable(xml)) {
                XmlssWriterFacade xmlss = new XmlssWriterFacade(xml);
                xmlss.beginWorkbook();
                xmlss.beginWorksheet();
                xmlss.beginTable();
                for (DataBlock row : matrix.rowList()) {
                    xmlss.beginRow();
                    for (int idx = 0; idx < row.getLength(); idx++) {
                        xmlss.writeCell(row.get(idx));
                    }
                    xmlss.endRow();
                }
                xmlss.endTable();
                xmlss.endWorksheet();
                xmlss.endWorkbook();
                return stream.toByteArray();
            }
        } catch (XMLStreamException ex) {
            throw new IOException(ex);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="IConfigurable">
    @Override
    public Config getConfig() {
        return configurator.getConfig(this);
    }

    @Override
    public void setConfig(Config config) {
        configurator.setConfig(this, config);
    }

    @Override
    public Config editConfig(Config config) {
        return configurator.editConfig(config);
    }
    //</editor-fold>

    public byte[] tsCollectionToBytes(TsCollection col) throws IOException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            XMLStreamWriter xml = XMLOutputFactory.newInstance().createXMLStreamWriter(stream);
            try (Closeable c = Closeables.asCloseable(xml)) {
                write(col, new XmlssWriterFacade(xml));
                return stream.toByteArray();
            }
        } catch (XMLStreamException ex) {
            throw new IOException(ex);
        }
    }

    public TsCollection tsCollectionFromBytes(byte[] bytes) throws IOException {
        // FIXME: there is a bug here with some chars at the end of the stream
        ByteArrayInputStream mem = new ByteArrayInputStream(bytes, 0, bytes.length - 2);
        TsCollection result = null;
        try {
            XMLInputFactory xmlif = XMLInputFactory.newInstance();
            XMLStreamReader xmlr = xmlif.createXMLStreamReader(mem);
            int cols = 0;
            int rows = 0;
            int colnum = 0;
            int rownum = 0;
            boolean datesAreVertical = true;

            while (xmlr.hasNext()) {
                xmlr.next();
                if (xmlr.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    if (xmlr.getLocalName().equals("Row")) {
                        cols = Math.max(cols, colnum);
                        colnum = 0;
                        int count = xmlr.getAttributeCount();
                        for (int i = 0; i < count; i++) {
                            if (xmlr.getAttributeLocalName(i).equals("Index")) {
                                rows = Integer.parseInt(xmlr.getAttributeValue(i)) - 1;
                            }
                        }
                    } else if (xmlr.getLocalName().equals("Cell")) {
                        int count = xmlr.getAttributeCount();
                        for (int i = 0; i < count; i++) {
                            if (xmlr.getAttributeLocalName(i).equals("Index")) {
                                colnum = Integer.parseInt(xmlr.getAttributeValue(i)) - 1;
                            }
                        }
                    } else if (xmlr.getLocalName().equals("Data")
                            && colnum == 0) {
                        String type = "";
                        int count = xmlr.getAttributeCount();
                        for (int i = 0; i < count; i++) {
                            if (xmlr.getAttributeLocalName(i).equals("Type")) {
                                type = xmlr.getAttributeValue(i);
                                datesAreVertical = type.equals("DateTime");
                            }
                        }
                    }
                } else if (xmlr.getEventType() == XMLStreamConstants.END_ELEMENT) {
                    switch (xmlr.getLocalName()) {
                        case "Row":
                            rows++;
                            break;
                        case "Cell":
                            colnum++;
                            break;
                    }
                }
            }

            mem.reset();
            xmlr = xmlif.createXMLStreamReader(mem);
            if (cols < 1 || rows < 1) {
                return null;
            }
            Matrix datamatrix = new Matrix(rows, cols);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    datamatrix.set(i, j, Double.NaN);
                }
            }
            Date[] dates = new Date[(datesAreVertical ? rows : cols)];
            String[] titles = new String[(datesAreVertical ? cols : rows)];
            rownum = 0;
            colnum = 0;

            while (xmlr.hasNext()) {
                xmlr.next();
                if (xmlr.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    switch (xmlr.getLocalName()) {
                        case "Row": {
                            colnum = 0;
                            int count = xmlr.getAttributeCount();
                            for (int i = 0; i < count; i++) {
                                if (xmlr.getAttributeLocalName(i).equals("Index")) {
                                    rownum = Integer.parseInt(xmlr.getAttributeValue(i)) - 1;
                                }
                            }
                            break;
                        }
                        case "Cell": {
                            int count = xmlr.getAttributeCount();
                            for (int i = 0; i < count; i++) {
                                if (xmlr.getAttributeLocalName(i).equals("Index")) {
                                    colnum = Integer.parseInt(xmlr.getAttributeValue(i)) - 1;
                                }
                            }
                            break;
                        }
                        case "Data": {
                            String type = "";
                            int count = xmlr.getAttributeCount();
                            for (int i = 0; i < count; i++) {
                                if (xmlr.getAttributeLocalName(i).equals("Type")) {
                                    type = xmlr.getAttributeValue(i);
                                }
                            }
                            String str = xmlr.getElementText();
                            switch (type) {
                                case "String":
                                    titles[(datesAreVertical ? colnum : rownum)] = str;
                                    break;
                                case "Number":
                                    try {
                                        double valy = numberFormat.parse(str).doubleValue();
                                        datamatrix.set(rownum, colnum, valy);
                                    } catch (Exception ex) {
                                    }
                                    break;
                                case "DateTime":
                                    try {
                                        Date date = dateFormat.parse(str);
                                        dates[(datesAreVertical ? rownum : colnum)] = date;
                                    } catch (Exception ex) {
                                    }
                                    break;
                            }
                            break;
                        }
                    }
                } else if (xmlr.getEventType() == XMLStreamConstants.END_ELEMENT) {
                    switch (xmlr.getLocalName()) {
                        case "Row":
                            rownum++;
                            break;
                        case "Cell":
                            colnum++;
                            break;
                    }
                }
            }

            TsCollectionAnalyser analyser = new TsCollectionAnalyser();

            analyser.data = (datesAreVertical ? datamatrix : datamatrix.transpose());
            analyser.dates = dates;
            analyser.titles = titles;
            result = analyser.create();
            xmlr.close();

            for (Ts s : result) {
                if (s.hasData() == TsStatus.Valid) {
                    s.set(s.getTsData());//.cleanExtremities());
                }
            }
        } catch (XMLStreamException ex) {
            throw new IOException("Problem while retrieving data", ex);
        }
        return result;
    }

    private void write(TsCollection col, XmlssWriterFacade writer) throws XMLStreamException {
        writer.beginWorkbook();
        writer.beginWorksheet();

        TsCollectionAnalyser analyser = new TsCollectionAnalyser();
        analyser.set(col, config.beginPeriod);

        if (analyser.data != null && analyser.dates != null && analyser.titles != null) {

            writer.beginTable();

            int nbdates = analyser.dates.length;
            int nseries = analyser.titles.length;
            if (config.vertical) // une série par colonne
            {
                // écriture des titres des séries
                if (config.showTitle) {
                    writer.beginRow();
                    if (config.showDates) {
                        writer.writeCell("");
                    }
                    for (int i = 0; i < nseries; i++) {
                        writer.writeCell(analyser.titles[i]);
                    }
                    writer.endRow();
                }

                for (int i = 0; i < nbdates; i++) {
                    writer.beginRow();
                    if (config.showDates) {
                        writer.writeCell(analyser.dates[i]);
                    }
                    for (int j = 0; j < nseries; j++) {
                        writer.writeCell(analyser.data.get(i, j));
                    }
                    writer.endRow();
                }
            } // une série par ligne
            else {
                if (config.showDates) {
                    writer.beginRow();
                    if (config.showTitle) {
                        writer.writeCell("");
                    }
                    for (int i = 0; i < nbdates; i++) {
                        writer.writeCell(analyser.dates[i]);
                    }
                    writer.endRow();
                }
                for (int i = 0; i < nseries; i++) {
                    writer.beginRow();
                    if (config.showTitle) {
                        writer.writeCell(analyser.titles[i]);
                    }
                    for (int j = 0; j < nbdates; j++) {
                        writer.writeCell(analyser.data.get(j, i));
                    }
                    writer.endRow();
                }
            }
            writer.endTable();
        }
        writer.endWorksheet();
        writer.endWorkbook();
    }

    static class XmlssWriterFacade {

        final XMLStreamWriter writer;
        private final NumberFormat numberFormat;
        private final DateFormat dateFormat;

        public XmlssWriterFacade(XMLStreamWriter writer) {
            this.writer = writer;
            numberFormat = NumberFormat.getNumberInstance(Locale.ROOT);
            numberFormat.setMaximumFractionDigits(9);
            numberFormat.setMaximumIntegerDigits(12);
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }

        public void beginWorkbook() throws XMLStreamException {
            writer.writeStartDocument();
            writer.writeStartElement("Workbook");
            writer.writeAttribute("xmlns", "urn:schemas-microsoft-com:office:spreadsheet");
            writer.writeAttribute("xmlns:o", "urn:schemas-microsoft-com:office:office");
            writer.writeAttribute("xmlns:x", "urn:schemas-microsoft-com:office:excel");
            writer.writeAttribute("xmlns:ss", "urn:schemas-microsoft-com:office:spreadsheet");
            writer.writeAttribute("xmlns:html", "http://www.w3.org/TR/REC-html40");
            writer.writeStartElement("Styles");
            writer.writeStartElement("Style");
            writer.writeAttribute("ss:ID", "s24");
            writer.writeStartElement("NumberFormat");
            writer.writeAttribute("ss:Format", "Short Date");
            writer.writeEndElement();// NumberFormat
            writer.writeEndElement();// style
            writer.writeEndElement();// styles
        }

        public void endWorkbook() throws XMLStreamException {
            writer.writeEndElement();// workbook
            writer.writeEndDocument();
            writer.flush();
        }

        public void beginWorksheet() throws XMLStreamException {
            writer.writeStartElement("Worksheet");
            writer.writeAttribute("ss:Name", "Sheet1");
        }

        public void endWorksheet() throws XMLStreamException {
            writer.writeEndElement();// Worksheet
            writer.flush();
        }

        public void beginTable() throws XMLStreamException {
            writer.writeStartElement("Table");
        }

        public void endTable() throws XMLStreamException {
            writer.writeEndElement();// table
        }

        public void beginRow() throws XMLStreamException {
            writer.writeStartElement("Row");
        }

        public void endRow() throws XMLStreamException {
            writer.writeEndElement();// row
        }

        public void writeCell(Date date) throws XMLStreamException {
            writer.writeStartElement("Cell");
            writer.writeAttribute("ss:StyleID", "s24");
            writer.writeStartElement("Data");
            writer.writeAttribute("ss:Type", "DateTime");
            String sd = dateFormat.format(date);
            writer.writeCharacters(sd);
            writer.writeEndElement();// data
            writer.writeEndElement();// cell
        }

        public void writeCell(double val) throws XMLStreamException {
            writer.writeStartElement("Cell");
            writer.writeStartElement("Data");
            if (!Double.isNaN(val)) {
                writer.writeAttribute("ss:Type", "Number");
                writer.writeCharacters(numberFormat.format(val));
            } else {
                writer.writeAttribute("ss:Type", "String");
                writer.writeCharacters("");
            }
            writer.writeEndElement();// data
            writer.writeEndElement();// cell
        }

        public void writeCell(String txt) throws XMLStreamException {
            writer.writeStartElement("Cell");
            writer.writeStartElement("Data");
            writer.writeAttribute("ss:Type", "String");
            writer.writeCharacters(Strings.nullToEmpty(txt));
            writer.writeEndElement();// data
            writer.writeEndElement();// cell
        }
    }

    private static DataFlavor createXmlssDataFlavor() {
        DataFlavor result = null;
        try {
            result = SystemFlavorMap.decodeDataFlavor("XML Spreadsheet");
        } catch (ClassNotFoundException ex) {
        }
        if (result == null) {
            result = new DataFlavor("xml/x;class=\"[B\"", "XML Spreadsheet");
            SystemFlavorMap map = (SystemFlavorMap) SystemFlavorMap.getDefaultFlavorMap();
            map.addUnencodedNativeForFlavor(result, "XML Spreadsheet");
            map.addFlavorForUnencodedNative("XML Spreadsheet", result);
            return result;
        }
        return result;
    }

    public static final class InternalConfig {

        /**
         * true : one series per column, false : one series per line
         */
        public boolean vertical = true;
        /**
         * show or not the dates
         */
        public boolean showDates = true;
        /**
         * show or not the titles of the series
         */
        public boolean showTitle = true;
        /**
         * true to set the dates at the beginning of the period, false for the
         * end of the period
         */
        public boolean beginPeriod = true;
        public boolean importTimeSeries = true;
        public boolean exportTimeSeries = true;
        public boolean importMatrix = true;
        public boolean exportMatrix = true;
    }

    private static final class InternalConfigHandler extends BeanHandler<InternalConfig, XmlssTssTransferHandler> {

        @Override
        public InternalConfig loadBean(XmlssTssTransferHandler resource) {
            return resource.config;
        }

        @Override
        public void storeBean(XmlssTssTransferHandler resource, InternalConfig bean) {
            resource.config = bean;
        }
    }

    private static final class InternalConfigEditor implements IBeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            Sheet sheet = new Sheet();
            NodePropertySetBuilder b = new NodePropertySetBuilder();

            b.reset("tscollection").display("Time Series");
            b.withBoolean().selectField(bean, "importTimeSeries").display("Allow import").add();
            b.withBoolean().selectField(bean, "exportTimeSeries").display("Allow export").add();
            b.withBoolean().selectField(bean, "vertical").display("Vertical alignment").add();
            b.withBoolean().selectField(bean, "showDates").display("Include date headers").add();
            b.withBoolean().selectField(bean, "showTitle").display("Include title headers").add();
            b.withBoolean().selectField(bean, "beginPeriod").display("Begin period").add();
            sheet.put(b.build());
            b.reset("matrix").display("Matrix");
//            b.withBoolean().selectField(bean, "importMatrix").display("Import enabled").add();
            b.withBoolean().selectField(bean, "exportMatrix").display("Allow export").add();
            sheet.put(b.build());

            return OpenIdePropertySheetBeanEditor.editSheet(sheet, "Configure XML Spreadsheet (XMLSS)",
                    ImageUtilities.icon2Image(DemetraUiIcon.CLIPBOARD_PASTE_DOCUMENT_TEXT_16));
        }
    }

    private static final class InternalConfigConverter extends Converter<InternalConfig, Config> {

        private static final String DOMAIN = TssTransferHandler.class.getName(), NAME = "XMLSS", VERSION = "";
        private static final IParam<Config, Boolean> VERTICAL = Params.onBoolean(true, "vertical");
        private static final IParam<Config, Boolean> SHOW_DATES = Params.onBoolean(true, "showDates");
        private static final IParam<Config, Boolean> SHOW_TITLE = Params.onBoolean(true, "showTitle");
        private static final IParam<Config, Boolean> BEGIN_PERIOD = Params.onBoolean(true, "beginPeriod");
        private static final IParam<Config, Boolean> IMPORT_TS = Params.onBoolean(true, "importEnabled");
        private static final IParam<Config, Boolean> EXPORT_TS = Params.onBoolean(true, "exportEnabled");
        private static final IParam<Config, Boolean> IMPORT_MATRIX = Params.onBoolean(true, "importMatrix");
        private static final IParam<Config, Boolean> EXPORT_MATRIX = Params.onBoolean(true, "exportMatrix");

        @Override
        protected Config doForward(InternalConfig a) {
            Config.Builder b = Config.builder(DOMAIN, NAME, VERSION);
            VERTICAL.set(b, a.vertical);
            SHOW_DATES.set(b, a.showDates);
            SHOW_TITLE.set(b, a.showTitle);
            BEGIN_PERIOD.set(b, a.beginPeriod);
            IMPORT_TS.set(b, a.importTimeSeries);
            EXPORT_TS.set(b, a.exportTimeSeries);
            IMPORT_MATRIX.set(b, a.importMatrix);
            EXPORT_MATRIX.set(b, a.exportMatrix);
            return b.build();
        }

        @Override
        protected InternalConfig doBackward(Config config) {
            Preconditions.checkArgument(DOMAIN.equals(config.getDomain()));
            Preconditions.checkArgument(NAME.equals(config.getName()));
            InternalConfig result = new InternalConfig();
            result.vertical = VERTICAL.get(config);
            result.showDates = SHOW_DATES.get(config);
            result.showTitle = SHOW_TITLE.get(config);
            result.beginPeriod = BEGIN_PERIOD.get(config);
            result.importTimeSeries = IMPORT_TS.get(config);
            result.exportTimeSeries = EXPORT_TS.get(config);
            result.importMatrix = IMPORT_MATRIX.get(config);
            result.exportMatrix = EXPORT_MATRIX.get(config);
            return result;
        }
    }
}
