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

import demetra.bridge.TsConverter;
import demetra.demo.PocProvider;
import demetra.ui.TsManager;
import demetra.ui.components.HasTs;
import ec.nbdemetra.ui.demo.DemoComponentHandler;
import static ec.nbdemetra.ui.demo.impl.TsCollectionHandler.getIcon;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceListener;
import ec.tss.tsproviders.IDataSourceProvider;
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
public final class TsAbleHandler extends DemoComponentHandler.InstanceOf<HasTs> {

    private final RandomTsCommand<HasTs> randomCommand;
    private final JCommand<HasTs> clearCommand;

    public TsAbleHandler() {
        super(HasTs.class);
        this.randomCommand = RandomTsCommand.of(TsAbleHandler::apply);
        this.clearCommand = JCommand.of(TsAbleHandler::clear);
    }

    @Override
    public void doConfigure(HasTs c) {
        randomCommand.executeSafely(c);
    }

    @Override
    public void doFillToolBar(JToolBar toolBar, HasTs c) {
        toolBar.add(randomCommand.toButton(c));
        toolBar.add(createFakeProviderButton(c));

        JButton item = toolBar.add(clearCommand.toAction(c));
        item.setIcon(FontAwesomeUtils.getIcon(FA_ERASER, ICON_COLOR_16x16));

        toolBar.addSeparator();
    }

    private static void apply(HasTs o, TsInformation ts) {
        o.setTs(TsConverter.toTs(ts.toTs()));
    }

    private static void clear(HasTs o) {
        o.setTs(null);
    }

    private static JButton createFakeProviderButton(HasTs view) {
        JMenu providerMenu = TsManager.getDefault()
                .lookup(IDataSourceProvider.class, PocProvider.NAME)
                .map(o -> createFakeProviderMenu(o, view))
                .orElseGet(JMenu::new);
        JButton result = DropDownButtonFactory.createDropDownButton(getIcon(FontAwesome.FA_DATABASE), providerMenu.getPopupMenu());
        result.setToolTipText("Data sources");
        enableTickFeedback(result);
        return result;
    }

    private static void enableTickFeedback(JButton button) {
        Optional<IDataSourceProvider> p = TsManager.getDefault().lookup(IDataSourceProvider.class, PocProvider.NAME);
        if (p.isPresent()) {
            IDataSourceListener changeListener = new IDataSourceListener() {
                Icon icon1 = getIcon(FontAwesome.FA_DATABASE);
                Icon icon2 = FontAwesome.FA_DATABASE.getIcon(Color.ORANGE.darker(), FontAwesomeUtils.toSize(ICON_COLOR_16x16));

                @Override
                public void changed(DataSource dataSource) {
                    SwingUtilities.invokeLater(() -> {
                        if (icon1.equals(button.getClientProperty("stuff"))) {
                            button.putClientProperty("stuff", icon2);
                            button.setIcon(icon2);
                        } else {
                            button.putClientProperty("stuff", icon1);
                            button.setIcon(icon1);
                        }
                    });
                }
            };
            button.putClientProperty("pocListener", changeListener);
            p.get().addDataSourceListener(changeListener);
        }
    }

    private static JMenu createFakeProviderMenu(IDataSourceProvider provider, HasTs view) {
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

    private static final class AddDataSetCommand extends JCommand<HasTs> {

        private final DataSet dataSet;

        public AddDataSetCommand(DataSet dataSet) {
            this.dataSet = dataSet;
        }

        @Override
        public void execute(HasTs component) throws Exception {
            Optional<demetra.tsprovider.Ts> ts = TsManager.getDefault()
                    .getTs(dataSet, TsInformationType.Definition)
                    .map(TsConverter::toTs);
            if (ts.isPresent()) {
                TsManager.getDefault().loadAsync(ts.get(), demetra.tsprovider.TsInformationType.All);
            }
            component.setTs(ts.orElse(null));
        }
    }
}
