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
package ec.nbdemetra.ui.demo.impl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.demo.DemoComponentHandler;
import ec.nbdemetra.ui.demo.DemoTsActions;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.ui.DemoUtils;
import ec.ui.commands.TsCollectionViewCommand;
import ec.ui.interfaces.ITsCollectionView;
import ec.ui.interfaces.ITsCollectionView.TsUpdateMode;
import ec.util.various.swing.JCommand;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import javax.swing.*;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DemoComponentHandler.class)
public final class TsCollectionHandler extends DemoComponentHandler.InstanceOf<ITsCollectionView> {

    public TsCollectionHandler() {
        super(ITsCollectionView.class);
    }

    static final DemoUtils.RandomTsCollectionBuilder BUILDER = new DemoUtils.RandomTsCollectionBuilder().withForecast(3);
    final TsCollection col = BUILDER.build();

    @Override
    public void doConfigure(ITsCollectionView c) {
        c.getTsCollection().append(col);
        c.setTsAction(DemoTsActions.SHOW_DIALOG);
    }

    @Override
    public void doFillToolBar(JToolBar toolBar, ITsCollectionView c) {
        toolBar.add(createAddButton(c));
        toolBar.add(createRemoveButton(c));
        toolBar.add(createUpdateModeButton(c));
        toolBar.add(createSelectionButton(c));
        toolBar.add(createSizeLabel(c));
    }

    static JButton createAddButton(final ITsCollectionView view) {
        JMenu menu = new JMenu();
        for (int i = 10; i < 10000; i *= 10) {
            menu.add(new AddRandomCommand(i).toAction(view)).setText(Integer.toString(i));
        }
        JButton result = DropDownButtonFactory.createDropDownButton(DemetraUiIcon.LIST_ADD_16, menu.getPopupMenu());
        result.addActionListener(new AddRandomCommand(1).toAction(view));
        return result;
    }

    static final class AddRandomCommand extends JCommand<ITsCollectionView> {

        private final int size;

        public AddRandomCommand(int size) {
            this.size = size;
        }

        @Override
        public void execute(ITsCollectionView component) throws Exception {
            component.getTsCollection().append(BUILDER.withSeries(size).build());
        }
    }

    static JButton createRemoveButton(ITsCollectionView view) {
        JMenu menu = new JMenu();
        menu.add(TsCollectionViewCommand.clear().toAction(view)).setText("Clear");
        JButton result = DropDownButtonFactory.createDropDownButton(DemetraUiIcon.LIST_REMOVE_16, menu.getPopupMenu());
        result.addActionListener(RemoveLastCommand.INSTANCE.toAction(view));
        return result;
    }

    static final class RemoveLastCommand extends JCommand<ITsCollectionView> {

        public static final RemoveLastCommand INSTANCE = new RemoveLastCommand();

        @Override
        public ActionAdapter toAction(ITsCollectionView component) {
            return super.toAction(component).withWeakPropertyChangeListener((Component) component, ITsCollectionView.COLLECTION_PROPERTY);
        }

        @Override
        public boolean isEnabled(ITsCollectionView component) {
            return !component.getTsCollection().isEmpty();
        }

        @Override
        public void execute(ITsCollectionView component) throws Exception {
            component.getTsCollection().removeAt(component.getTsCollection().getCount() - 1);
        }
    }

    static JButton createUpdateModeButton(final ITsCollectionView view) {
        final JMenu menu = new JMenu();
        for (TsUpdateMode o : TsUpdateMode.values()) {
            menu.add(new JCheckBoxMenuItem(ApplyTsUpdateModeCommand.VALUES.get(o).toAction(view))).setText(o.name());
        }
        return DropDownButtonFactory.createDropDownButton(DemetraUiIcon.TABLE_RELATION_16, menu.getPopupMenu());
    }

    static final class ApplyTsUpdateModeCommand extends JCommand<ITsCollectionView> {

        public static final EnumMap<TsUpdateMode, ApplyTsUpdateModeCommand> VALUES;

        static {
            VALUES = new EnumMap<>(TsUpdateMode.class);
            for (TsUpdateMode o : TsUpdateMode.values()) {
                VALUES.put(o, new ApplyTsUpdateModeCommand(o));
            }
        }

        private final TsUpdateMode value;

        private ApplyTsUpdateModeCommand(TsUpdateMode value) {
            this.value = value;
        }

        @Override
        public ActionAdapter toAction(ITsCollectionView component) {
            return super.toAction(component).withWeakPropertyChangeListener((Component) component, ITsCollectionView.UDPATE_MODE_PROPERTY);
        }

        @Override
        public boolean isSelected(ITsCollectionView component) {
            return component.getTsUpdateMode() == value;
        }

        @Override
        public void execute(ITsCollectionView component) throws Exception {
            component.setTsUpdateMode(value);
        }
    }

    static JButton createSelectionButton(final ITsCollectionView view) {
        final Action[] selectionActions = {
            new AbstractAction("All") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    view.setSelection(view.getTsCollection().toArray());
                }
            },
            new AbstractAction("None") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    view.setSelection(null);
                }
            },
            new AbstractAction("Alternate") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    List<Ts> tmp = new ArrayList<>();
                    for (int i = 0; i < view.getTsCollection().getCount(); i += 2) {
                        tmp.add(view.getTsCollection().get(i));
                    }
                    view.setSelection(Iterables.toArray(tmp, Ts.class));
                }
            },
            new AbstractAction("Inverse") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    List<Ts> tmp = Lists.newArrayList(view.getTsCollection());
                    tmp.removeAll(Arrays.asList(view.getSelection()));
                    view.setSelection(Iterables.toArray(tmp, Ts.class));
                }
            }
        };
        JPopupMenu menu = new JPopupMenu();
        for (Action o : selectionActions) {
            menu.add(o);
        }
        JButton result = DropDownButtonFactory.createDropDownButton(DemetraUiIcon.PUZZLE_16, menu);
        result.addActionListener(new ActionListener() {
            int i = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                selectionActions[i++ % selectionActions.length].actionPerformed(e);
            }
        });
        return result;
    }

    static JLabel createSizeLabel(final ITsCollectionView view) {
        final JLabel result = new JLabel(" [0/0] ");
        view.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case ITsCollectionView.SELECTION_PROPERTY:
                        result.setText(" [" + view.getSelectionSize() + "/" + view.getTsCollection().getCount() + "] ");
                        break;
                }
            }
        });
        return result;
    }
}
