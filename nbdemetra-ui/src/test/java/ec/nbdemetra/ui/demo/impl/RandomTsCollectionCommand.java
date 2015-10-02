/*
 * Copyright 2015 National Bank of Belgium
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

import ec.tss.TsCollection;
import ec.ui.DemoUtils;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.awt.DropDownButtonFactory;

/**
 *
 * @author Philippe Charles
 * @param <C>
 */
abstract class RandomTsCollectionCommand<C> extends JCommand<C> {

    private final BoundedRangeModel obsCountModel;
    private final DemoUtils.RandomTsCollectionBuilder builder;

    public RandomTsCollectionCommand() {
        this.obsCountModel = new DefaultBoundedRangeModel(12 * 10, 12, 0, 12 * 100);
        this.builder = new DemoUtils.RandomTsCollectionBuilder().withSeries(1);
    }

    abstract protected void apply(C c, TsCollection col);

    @Override
    final public void execute(C c) throws Exception {
        apply(c, builder.withObs(obsCountModel.getValue()).build());
    }

    @Override
    public JCommand.ActionAdapter toAction(final C component) {
        final JCommand.ActionAdapter result = new JCommand.ActionAdapter(component) {
            @Override
            public void handleException(ActionEvent event, Exception ex) {
                String message = ex.getClass().getSimpleName() + ": " + ex.getMessage();
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        obsCountModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                result.actionPerformed(null);
            }
        });
        return result;
    }

    public JButton toButton(C component) {
        JButton item = DropDownButtonFactory.createDropDownButton(FontAwesomeUtils.getIcon(FA_RANDOM, ICON_COLOR_16x16), createRandomMenu());
        item.addActionListener(toAction(component));
        return item;
    }

    private JPopupMenu createRandomMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.add(new JSlider(obsCountModel));

        final JLabel label = new JLabel(obsCountModel.getValue() + " obs");
        obsCountModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                label.setText(obsCountModel.getValue() + " obs");
            }
        });
        menu.add(label);
        return menu;
    }
}
