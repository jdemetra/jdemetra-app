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
package demetra.ui.nodes;

import demetra.ui.NamedService;
import demetra.ui.Config;
import demetra.ui.Persistable;
import demetra.ui.actions.Configurable;
import java.awt.Image;
import java.awt.event.ActionEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import demetra.ui.actions.Resetable;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author Philippe Charles
 */
public final class NamedServiceNode extends AbstractNode {

    public NamedServiceNode(@NonNull NamedService namedService) {
        this(namedService, new InstanceContent());
    }

    private NamedServiceNode(NamedService namedService, InstanceContent abilities) {
        super(Children.LEAF, new AbstractLookup(abilities));
        // order matters !
        if (namedService instanceof Persistable) {
            if (namedService instanceof Resetable) {
                abilities.add(new LateConfigurableAndResetable((Persistable) namedService, (Resetable) namedService));
            } else {
                abilities.add(new LateConfigurable((Persistable) namedService));
            }
        }
        abilities.add(namedService);
        setName(namedService.getName());
        setDisplayName(namedService.getDisplayName());
    }

    protected NamedService getNamedService() {
        return getLookup().lookup(NamedService.class);
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
        final Configurable configurable = getLookup().lookup(Configurable.class);
        return configurable != null ? new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configurable.configure();
            }
        } : super.getPreferredAction();
    }

    public static void loadAll(ExplorerManager em, Iterable<? extends NamedService> items) {
        Stream<NamedServiceNode> nodes = StreamSupport.stream(items.spliterator(), false).map(NamedServiceNode::new);
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
    private static class LateConfigurable {

        protected final Persistable configurable;
        protected Config latestConfig;

        public LateConfigurable(Persistable configurable) {
            this.configurable = configurable;
            this.latestConfig = configurable.getConfig();
        }

        void apply() {
            configurable.setConfig(latestConfig);
        }
    }

    private static final class LateConfigurableAndResetable extends LateConfigurable implements Resetable {

        private final Resetable resetable;

        public LateConfigurableAndResetable(Persistable configurable, Resetable resetable) {
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
