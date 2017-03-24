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
package ec.nbdemetra.ui.variables.actions;

import com.google.common.base.Converter;
import ec.nbdemetra.ui.Config;
import ec.tss.tsproviders.utils.Formatters;
import ec.tss.tsproviders.utils.IFormatter;
import ec.tss.tsproviders.utils.IParser;
import ec.tss.tsproviders.utils.Parsers;
import ec.tss.xml.regression.XmlTsVariables;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.timeseries.regression.TsVariables;
import ec.tstoolkit.utilities.NameManager;

/**
 *
 * @author Philippe Charles
 */
final class VariablesConfig extends Converter<TsVariables, Config> {

    static final String DOMAIN = TsVariables.class.getName();

    private final IFormatter<XmlTsVariables> formatter = Formatters.onJAXB(XmlTsVariables.class, false);
    private final IParser<XmlTsVariables> parser = Parsers.onJAXB(XmlTsVariables.class);

    @Override
    protected Config doForward(TsVariables a) {
        NameManager<TsVariables> manager = ProcessingContext.getActiveContext().getTsVariableManagers();
        Config.Builder result = Config.builder(DOMAIN, manager.get(a), "");
        XmlTsVariables xml = new XmlTsVariables();
        xml.copy(a);
        result.put("xml", formatter.formatAsString(xml));
        return result.build();
    }

    @Override
    protected TsVariables doBackward(Config b) {
        String tmp = b.get("xml");
        if (tmp != null) {
            XmlTsVariables xml = parser.parse(tmp);
            if (xml != null) {
                TsVariables value = xml.create();
                if (value != null) {
                    return value;
                }
            }
        }
        throw new RuntimeException("Cannot parse config");
    }
}
