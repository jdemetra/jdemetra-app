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
package demetra.desktop.core.interchange;

import demetra.ui.Config;
import demetra.desktop.interchange.Exportable;
import demetra.desktop.interchange.Importable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.checkerframework.checker.nullness.qual.NonNull;
import nbbrd.design.MightBePromoted;
import nbbrd.io.sys.SystemProperties;
import nbbrd.io.text.Parser;
import nbbrd.io.xml.Stax;
import nbbrd.io.xml.Xml;

/**
 *
 * @author Philippe Charles
 */
@MightBePromoted
@lombok.Value
@lombok.Builder
final class Configs {

    @lombok.NonNull
    String author;

    long creationTime;

    @lombok.Singular
    List<Config> items;

    public static Xml.@NonNull Formatter<Configs> xmlFormatter(boolean formattedOutput) {
        return FORMATTER;
    }

    public static Xml.@NonNull Parser<Configs> xmlParser() {
        return PARSER;
    }

    public static Configs fromExportables(List<? extends Exportable> exportables) {
        return new Configs(
                SystemProperties.DEFAULT.getUserName(),
                System.currentTimeMillis(),
                exportables.stream().map(Exportable::exportConfig).collect(Collectors.toList())
        );
    }

    public void performImport(List<? extends Importable> importables) throws IOException, IllegalArgumentException {
        for (Config o : items) {
            for (Importable importable : importables) {
                if (canImport(o, importable)) {
                    importable.importConfig(o);
                    break;
                }
            }
        }
    }

    public boolean canImport(List<? extends Importable> importables) {
        return items.stream().anyMatch(o -> canImport(o, importables));
    }

    private static boolean canImport(Config config, Importable importable) {
        return importable.getDomain().equals(config.getDomain());
    }

    private static boolean canImport(Config config, List<? extends Importable> importables) {
        return importables.stream().anyMatch(o -> canImport(config, o));
    }

    private static final Xml.Parser<Configs> PARSER = Stax.StreamParser.valueOf(Configs::parse);
    private static final Xml.Formatter<Configs> FORMATTER = Stax.StreamFormatter.of(Configs::format);

    private static final String CONFIGS_TAG = "configs";
    private static final String CONFIG_TAG = "config";
    private static final String CREATION_TIME_ATTR = "creationTime";
    private static final String AUTHOR_ATTR = "author";
    private static final String VALUE_ATTR = "value";
    private static final String KEY_ATTR = "key";
    private static final String PARAM_TAG = "param";
    private static final String VERSION_ATTR = "version";
    private static final String NAME_ATTR = "name";
    private static final String DOMAIN_ATTR = "domain";

    private static Configs parse(XMLStreamReader r) throws XMLStreamException {
        while (r.hasNext()) {
            if (r.next() == XMLStreamReader.START_ELEMENT && r.getLocalName().equals("configs")) {
                return parseConfigs(r);
            }
        }
        throw new XMLStreamException("Not valid xml");
    }

    private static Configs parseConfigs(XMLStreamReader r) throws XMLStreamException {
        Configs.Builder result = Configs.builder()
                .author(r.getAttributeValue(null, AUTHOR_ATTR))
                .creationTime(Parser.onLong().parse(r.getAttributeValue(null, CREATION_TIME_ATTR)));
        while (r.hasNext()) {
            switch (r.next()) {
                case XMLStreamReader.START_ELEMENT:
                    if (r.getLocalName().equals(CONFIG_TAG)) {
                        result.item(parseConfig(r));
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (r.getLocalName().equals(CONFIGS_TAG)) {
                        return result.build();
                    }
                    break;
            }
        }
        return result.build();
    }

    private static Config parseConfig(XMLStreamReader r) throws XMLStreamException {
        Config.Builder config = Config.builder(
                r.getAttributeValue(null, DOMAIN_ATTR),
                r.getAttributeValue(null, NAME_ATTR),
                r.getAttributeValue(null, VERSION_ATTR)
        );
        while (r.hasNext()) {
            switch (r.next()) {
                case XMLStreamReader.START_ELEMENT:
                    if (r.getLocalName().equals(PARAM_TAG)) {
                        config.parameter(
                                r.getAttributeValue(null, KEY_ATTR),
                                r.getAttributeValue(null, VALUE_ATTR)
                        );
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (r.getLocalName().equals(CONFIG_TAG)) {
                        return config.build();
                    }
                    break;
            }
        }
        return config.build();
    }

    private static void format(Configs configs, XMLStreamWriter w, Charset encoding) throws XMLStreamException {
        w.writeProcessingInstruction("xml version=\"1.0\" encoding=\"" + encoding.name() + "\" standalone=\"yes\"");
        w.writeStartElement(CONFIGS_TAG);
        w.writeAttribute(AUTHOR_ATTR, configs.getAuthor());
        w.writeAttribute(CREATION_TIME_ATTR, String.valueOf(configs.getCreationTime()));
        for (Config config : configs.getItems()) {
            w.writeStartElement(CONFIG_TAG);
            w.writeAttribute(DOMAIN_ATTR, config.getDomain());
            w.writeAttribute(NAME_ATTR, config.getName());
            w.writeAttribute(VERSION_ATTR, config.getVersion());
            for (Map.Entry<String, String> param : config.getParameters().entrySet()) {
                w.writeEmptyElement(PARAM_TAG);
                w.writeAttribute(KEY_ATTR, param.getKey());
                w.writeAttribute(VALUE_ATTR, param.getValue());
            }
            w.writeEndElement();
        }
        w.writeEndElement();
        w.writeEndDocument();
    }
}
