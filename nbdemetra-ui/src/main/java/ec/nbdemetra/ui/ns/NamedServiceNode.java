/*
 * Copyright 2016 National Bank of Belgium
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
package ec.nbdemetra.ui.ns;

import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.IResetable;
import ec.nbdemetra.ui.nodes.AbstractNodeBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Philippe Charles
 */
public class NamedServiceNode extends AbstractNode {

    public NamedServiceNode(@NonNull INamedService namedService) {
        this(namedService, new InstanceContent());
    }

    private NamedServiceNode(INamedService namedService, InstanceContent abilities) {
        super(Children.LEAF, new AbstractLookup(abilities));
        // order matters !
        if (namedService instanceof IConfigurable) {
            if (namedService instanceof IResetable) {
                abilities.add(new LateConfigurableAndResetable((IConfigurable) namedService, (IResetable) namedService));
            } else {
                abilities.add(new LateConfigurable((IConfigurable) namedService));
            }
        }
        abilities.add(namedService);
        setName(namedService.getName());
        setDisplayName(namedService.getDisplayName());
    }

    protected INamedService getNamedService() {
        return getLookup().lookup(INamedService.class);
    }

    @Override
    public Image getIcon(int type) {
        Image image = getNamedService().getIcon(type, false);
        return image != null ? image : super.getIcon(type);
    }

    @Override
    public Image getOpenedIcon(int type) {
        Image image = getNamedService().getIcon(type, true);
        return image != null ? image : super.getIcon(type);
    }

    @Override
    protected Sheet createSheet() {
        return getNamedService().createSheet();
    }

    public void applyConfig() {
        LateConfigurable c = getLookup().lookup(LateConfigurable.class);
        if (c != null) {
            c.apply();
        }
    }

    @Override
    public Action getPreferredAction() {
        final IConfigurable configurable = getLookup().lookup(IConfigurable.class);
        return configurable != null ? new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configurable.setConfig(configurable.editConfig(configurable.getConfig()));
            }
        } : super.getPreferredAction();
    }

    public static void loadAll(ExplorerManager em, Iterable<? extends INamedService> items) {
        Stream<NamedServiceNode> nodes = StreamSupport.stream(items.spliterator(), false).map(o -> new NamedServiceNode(o));
        em.setRootContext(new AbstractNodeBuilder().add(nodes).build());
    }

    public static void storeAll(ExplorerManager em) {
        for (Node o : em.getRootContext().getChildren().getNodes()) {
            if (o instanceof NamedServiceNode) {
                ((NamedServiceNode) o).applyConfig();
            }
            for (Node c : o.getChildren().getNodes()) {
                if (c instanceof NamedServiceNode) {
                    ((NamedServiceNode) c).applyConfig();
                }
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static class LateConfigurable implements IConfigurable {

        protected final IConfigurable configurable;
        protected Config latestConfig;

        public LateConfigurable(IConfigurable configurable) {
            this.configurable = configurable;
            this.latestConfig = configurable.getConfig();
        }

        @Override
        public Config getConfig() {
            return latestConfig;
        }

        @Override
        public void setConfig(Config config) throws IllegalArgumentException {
            latestConfig = config;
        }

        @Override
        public Config editConfig(Config config) throws IllegalArgumentException {
            return configurable.editConfig(config);
        }

        void apply() {
            configurable.setConfig(latestConfig);
        }
    }

    private static final class LateConfigurableAndResetable extends LateConfigurable implements IResetable {

        private final IResetable resetable;

        public LateConfigurableAndResetable(IConfigurable configurable, IResetable resetable) {
            super(configurable);
            this.resetable = resetable;
        }

        @Override
        public void reset() {
            Config tmp = configurable.getConfig();
            resetable.reset();
            latestConfig = configurable.getConfig();
            configurable.setConfig(tmp);
        }
    }
    //</editor-fold>
}
