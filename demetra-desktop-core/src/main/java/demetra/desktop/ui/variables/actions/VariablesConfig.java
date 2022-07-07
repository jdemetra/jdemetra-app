/*
 * Copyright 2017 National Bank of Belgium
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
package demetra.desktop.ui.variables.actions;

import demetra.desktop.Config;
import demetra.desktop.Converter;
import demetra.timeseries.regression.ModellingContext;
import demetra.timeseries.regression.TsDataSuppliers;
import demetra.util.NameManager;
import internal.workspace.file.xml.util.XmlTsVariables;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.Parser;
import nbbrd.io.xml.bind.Jaxb;

/**
 *
 * @author Philippe Charles
 */
final class VariablesConfig implements Converter<TsDataSuppliers, Config> {

    static final String DOMAIN = TsDataSuppliers.class.getName();

    private final Formatter<XmlTsVariables> formatter = Jaxb.Formatter.of(XmlTsVariables.class).asFormatter();
    private final Parser<XmlTsVariables> parser = Jaxb.Parser.of(XmlTsVariables.class).asParser();

    @Override
    public Config doForward(TsDataSuppliers a) {
        NameManager<TsDataSuppliers> manager = ModellingContext.getActiveContext().getTsVariableManagers();
        Config.Builder result = Config.builder(DOMAIN, manager.get(a), "");
        XmlTsVariables xml = new XmlTsVariables();
        xml.copy(a);
        result.parameter("xml", formatter.formatAsString(xml));
        return result.build();
    }

    @Override
    public TsDataSuppliers doBackward(Config b) {
        String tmp = b.getParameter("xml");
        if (tmp != null) {
            XmlTsVariables xml = parser.parse(tmp);
            if (xml != null) {
                TsDataSuppliers value = xml.create();
                if (value != null) {
                    return value;
                }
            }
        }
        throw new RuntimeException("Cannot parse config");
    }
}
