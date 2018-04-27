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
package ec.nbdemetra.ui;

import ec.util.various.swing.JCommand;
import javax.annotation.Nonnull;
import javax.swing.ActionMap;

/**
 *
 * @author Philippe Charles
 */
public interface IConfigurable {

    @Nonnull
    Config getConfig();

    void setConfig(@Nonnull Config config) throws IllegalArgumentException;

    @Nonnull
    Config editConfig(@Nonnull Config config) throws IllegalArgumentException;

    static final String CONFIGURE_ACTION = "configure";

    static void registerActions(IConfigurable configurable, ActionMap am) {
        am.put(CONFIGURE_ACTION, ConfigureCommand.INSTANCE.toAction(configurable));
    }

    static final class ConfigureCommand extends JCommand<IConfigurable> {

        public static final ConfigureCommand INSTANCE = new ConfigureCommand();

        @Override
        public void execute(IConfigurable c) throws Exception {
            c.setConfig(c.editConfig(c.getConfig()));
        }
    }
}
