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
package ec.nbdemetra.ui.ns;

import com.google.common.base.Preconditions;
import ec.nbdemetra.ui.Config;

/**
 *
 * @author Philippe Charles
 */
public class NamedServiceSupport {

    protected final Class<? extends INamedService> service;
    protected final String name;

    public NamedServiceSupport(Class<? extends INamedService> service, String name) {
        this.service = service;
        this.name = name;
    }

    public Class<? extends INamedService> getService() {
        return service;
    }

    public String getName() {
        return name;
    }

    public void check(Config config) {
        Preconditions.checkArgument(config.getDomain().equals(service.getName()));
        Preconditions.checkArgument(config.getName().equals(name));
    }

    public Config.Builder newBuilder() {
        return Config.builder(service.getName(), name, "");
    }
}
