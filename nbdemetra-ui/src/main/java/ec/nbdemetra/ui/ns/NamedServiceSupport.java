/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
