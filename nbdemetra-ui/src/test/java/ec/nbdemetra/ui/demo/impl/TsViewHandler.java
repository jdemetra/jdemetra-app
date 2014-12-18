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
import ec.ui.DemoUtils.RandomTsCollectionBuilder;
import ec.ui.interfaces.ITsView;
import static ec.util.various.swing.FontAwesome.FA_RANDOM;
import ec.util.various.swing.JCommand;
import ec.util.various.swing.ext.FontAwesomeUtils;
import java.awt.event.ActionEvent;
import static java.beans.BeanInfo.ICON_COLOR_16x16;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DemoComponentHandler.class)
public final class TsViewHandler extends DemoComponentHandler.InstanceOf<ITsView> {

    public TsViewHandler() {
        super(ITsView.class);
    }

    private final RandomTsCommand randomTsCommand = new RandomTsCommand();

    @Override
    public void doConfigure(ITsView c) {
        randomTsCommand.executeSafely(c);
    }

    @Override
    public void doFillToolBar(JToolBar toolBar, ITsView c) {
        JPopupMenu menu = new JPopupMenu();

        JButton randomTsButton = DropDownButtonFactory.createDropDownButton(FontAwesomeUtils.getIcon(FA_RANDOM, ICON_COLOR_16x16), menu);
        randomTsButton.addActionListener(randomTsCommand.toAction(c));

        menu.add(new JSlider(randomTsCommand.rangeModel));

        final JLabel label = new JLabel(randomTsCommand.rangeModel.getValue() + " obs");
        randomTsCommand.rangeModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                label.setText(randomTsCommand.rangeModel.getValue() + " obs");
            }
        });
        menu.add(label);

        toolBar.add(randomTsButton);
        toolBar.addSeparator();
    }

    private static final class RandomTsCommand extends JCommand<ITsView> {

        private final BoundedRangeModel rangeModel;
        private final RandomTsCollectionBuilder builder;

        public RandomTsCommand() {
            this.rangeModel = new DefaultBoundedRangeModel(12 * 10, 12, 0, 12 * 100);
            this.builder = new RandomTsCollectionBuilder().withSeries(1);
        }

        @Override
        public void execute(ITsView component) throws Exception {
            component.setTs(builder.withObs(rangeModel.getValue()).build().get(0));
        }

        @Override
        public ActionAdapter toAction(final ITsView component) {
            final ActionAdapter result = new ActionAdapter(component) {
                @Override
                public void handleException(ActionEvent event, Exception ex) {
                    String message = ex.getClass().getSimpleName() + ": " + ex.getMessage();
                    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
                }
            };
            rangeModel.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    result.actionPerformed(null);
                }
            });
            return result;
        }
    }
}
