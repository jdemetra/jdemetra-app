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
package ec.nbdemetra.sa;

import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.core.GlobalService;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.awt.JProperty;
import ec.nbdemetra.ui.awt.ListenableBean;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;
import ec.tstoolkit.utilities.ThreadPoolSize;
import ec.tstoolkit.utilities.ThreadPriority;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.api.options.OptionsDisplayer;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@GlobalService
@ServiceProvider(service = DemetraSA.class)
@Deprecated
public class DemetraSA extends ListenableBean implements IConfigurable {

    public static DemetraSA getDefault() {
        return Lookup.getDefault().lookup(DemetraSA.class);
    }

    @Deprecated
    public static DemetraSA getInstance() {
        return getDefault();
    }
    // PROPERTIES DEFINITION
    public static final String BATCH_POOL_SIZE_PROPERTY = "batchPoolSize";
    public static final String BATCH_PRIORITY_PROPERTY = "batchPriority";
    // DEFAULT PROPERTIES
    protected static final ThreadPoolSize DEFAULT_BATCH_POOL_SIZE = ThreadPoolSize.ALL_BUT_ONE;
    protected static final ThreadPriority DEFAULT_BATCH_PRIORITY = ThreadPriority.NORMAL;
    // PROPERTIES
    protected final JProperty<ThreadPoolSize> batchPoolSize;
    protected final JProperty<ThreadPriority> batchPriority;

    public DemetraSA() {
        this.batchPoolSize = new JProperty<ThreadPoolSize>(BATCH_POOL_SIZE_PROPERTY, DEFAULT_BATCH_POOL_SIZE) {
            @Override
            protected void firePropertyChange(ThreadPoolSize oldValue, ThreadPoolSize newValue) {
                // do nothing
            }
        };
        this.batchPriority = new JProperty<ThreadPriority>(BATCH_PRIORITY_PROPERTY, DEFAULT_BATCH_PRIORITY) {
            @Override
            protected void firePropertyChange(ThreadPriority oldValue, ThreadPriority newValue) {
                // do nothing
            }
        };

        DemetraUI.getDefault().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case DemetraUI.BATCH_POOL_SIZE_PROPERTY:
                        firePropertyChange(BATCH_POOL_SIZE_PROPERTY, evt.getOldValue(), evt.getNewValue());
                        break;
                    case DemetraUI.BATCH_PRIORITY_PROPERTY:
                        firePropertyChange(BATCH_PRIORITY_PROPERTY, evt.getOldValue(), evt.getNewValue());
                        break;
                }
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="GETTERS/SETTERS">
    @Deprecated
    public ThreadPoolSize getBatchPoolSize() {
        return DemetraUI.getDefault().getBatchPoolSize();
    }

    @Deprecated
    public void setBatchPoolSize(ThreadPoolSize batchThreadPool) {
        DemetraUI.getDefault().setBatchPoolSize(batchThreadPool);
    }

    @Deprecated
    public ThreadPriority getBatchPriority() {
        return DemetraUI.getDefault().getBatchPriority();
    }

    @Deprecated
    public void setBatchPriority(ThreadPriority batchPriority) {
        DemetraUI.getDefault().setBatchPriority(batchPriority);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="IConfigurable">
    @Override
    public Config getConfig() {
        return new ConfigBean(batchPoolSize.get(), batchPriority.get()).toConfig();
    }

    @Override
    public void setConfig(Config config) throws IllegalArgumentException {
        ConfigBean bean = new ConfigBean(config);
        batchPoolSize.set(bean.batchPoolSize);
        batchPriority.set(bean.batchPriority);
    }

    @Override
    public Config editConfig(Config config) throws IllegalArgumentException {
        OptionsDisplayer.getDefault().open(DemetraSAOptionsPanelController.ID);
        return getConfig();
    }
    //</editor-fold>

    static class ConfigBean {

        private static final String DOMAIN = DemetraSA.class.getName(), NAME = "INSTANCE", VERSION = "";
        static final IParam<Config, ThreadPoolSize> P1 = Params.onEnum(DEFAULT_BATCH_POOL_SIZE, BATCH_POOL_SIZE_PROPERTY);
        static final IParam<Config, ThreadPriority> P2 = Params.onEnum(DEFAULT_BATCH_PRIORITY, BATCH_PRIORITY_PROPERTY);
        ThreadPoolSize batchPoolSize;
        ThreadPriority batchPriority;

        ConfigBean(ThreadPoolSize batchPoolSize, ThreadPriority batchPriority) {
            this.batchPoolSize = batchPoolSize;
            this.batchPriority = batchPriority;
        }

        ConfigBean(Config config) {
            this(P1.get(config), P2.get(config));
        }

        Config toConfig() {
            Config.Builder b = Config.builder(DOMAIN, NAME, VERSION);
            P1.set(b, batchPoolSize);
            P2.set(b, batchPriority);
            return b.build();
        }
    }
}
