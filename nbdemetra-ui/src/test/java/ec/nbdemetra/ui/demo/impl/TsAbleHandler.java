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
package ec.nbdemetra.ui.demo.impl;

import ec.nbdemetra.ui.demo.DemoComponentHandler;
import ec.nbdemetra.ui.demo.FakeTsProvider;
import static ec.nbdemetra.ui.demo.impl.TsCollectionHandler.getIcon;
import ec.tss.Ts;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.tss.tsproviders.TsProviders;
import ec.ui.interfaces.ITsAble;
import ec.util.various.swing.FontAwesome;
import static ec.util.various.swing.FontAwesome.FA_ERASER;
import ec.util.various.swing.JCommand;
import ec.util.various.swing.ext.FontAwesomeUtils;
import java.awt.Color;
import static java.beans.BeanInfo.ICON_COLOR_16x16;
import java.io.IOException;
import java.util.Optional;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DemoComponentHandler.class)
public final class TsAbleHandler extends DemoComponentHandler.InstanceOf<ITsAble> {

    private final RandomTsCommand<ITsAble> randomCommand;
    private final JCommand<ITsAble> clearCommand;

    public TsAbleHandler() {
        super(ITsAble.class);
        this.randomCommand = RandomTsCommand.of(TsAbleHandler::apply);
        this.clearCommand = JCommand.of(TsAbleHandler::clear);
    }

    @Override
    public void doConfigure(ITsAble c) {
        randomCommand.executeSafely(c);
    }

    @Override
    public void doFillToolBar(JToolBar toolBar, ITsAble c) {
        toolBar.add(randomCommand.toButton(c));
        toolBar.add(createFakeProviderButton(c));

        JButton item = toolBar.add(clearCommand.toAction(c));
        item.setIcon(FontAwesomeUtils.getIcon(FA_ERASER, ICON_COLOR_16x16));

        toolBar.addSeparator();
    }

    private static void apply(ITsAble o, TsInformation ts) {
        o.setTs(ts.toTs());
    }

    private static void clear(ITsAble o) {
        o.setTs(null);
    }

    private static JButton createFakeProviderButton(ITsAble view) {
        JMenu providerMenu = TsProviders
                .lookup(IDataSourceProvider.class, "Fake")
                .transform(o -> createFakeProviderMenu(o, view))
                .or(JMenu::new);
        JButton result = DropDownButtonFactory.createDropDownButton(getIcon(FontAwesome.FA_DATABASE), providerMenu.getPopupMenu());
        result.setToolTipText("Data sources");
        enableTickFeedback(result);
        return result;
    }

    private static void enableTickFeedback(JButton button) {
        Optional<FakeTsProvider> p = TsProviders.lookup(FakeTsProvider.class, "Fake").toJavaUtil();
        if (p.isPresent()) {
            Icon icon1 = getIcon(FontAwesome.FA_DATABASE);
            Icon icon2 = FontAwesome.FA_DATABASE.getIcon(Color.ORANGE.darker(), FontAwesomeUtils.toSize(ICON_COLOR_16x16));
            p.get().addTickListener(() -> {
                SwingUtilities.invokeLater(() -> {
                    if (icon1.equals(button.getClientProperty("stuff"))) {
                        button.putClientProperty("stuff", icon2);
                        button.setIcon(icon2);
                    } else {
                        button.putClientProperty("stuff", icon1);
                        button.setIcon(icon1);
                    }
                });
            });
        }
    }

    private static JMenu createFakeProviderMenu(IDataSourceProvider provider, ITsAble view) {
        JMenu result = new JMenu();
        for (DataSource dataSource : provider.getDataSources()) {
            JMenu subMenu = new JMenu(provider.getDisplayName(dataSource));
            subMenu.setIcon(getIcon(FontAwesome.FA_FOLDER));
            try {
                for (DataSet dataSet : provider.children(dataSource)) {
                    JMenuItem item = subMenu.add(new AddDataSetCommand(dataSet).toAction(view));
                    item.setText(provider.getDisplayNodeName(dataSet));
                    item.setIcon(getIcon(FontAwesome.FA_LINE_CHART));
                }
            } catch (IOException ex) {
                subMenu.add(ex.getMessage()).setIcon(getIcon(FontAwesome.FA_EXCLAMATION_CIRCLE));
            }
            result.add(subMenu);
        }
        return result;
    }

    private static final class AddDataSetCommand extends JCommand<ITsAble> {

        private final DataSet dataSet;

        public AddDataSetCommand(DataSet dataSet) {
            this.dataSet = dataSet;
        }

        @Override
        public void execute(ITsAble component) throws Exception {
            Optional<Ts> ts = TsProviders.getTs(dataSet, TsInformationType.Definition).toJavaUtil();
            if (ts.isPresent()) {
                ts.get().query(TsInformationType.All);
            }
            component.setTs(ts.orElse(null));
        }
    }
}
