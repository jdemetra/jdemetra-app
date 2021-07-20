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

import demetra.bridge.TsConverter;
import ec.tss.TsInformation;
import static ec.util.various.swing.FontAwesome.FA_RANDOM;
import ec.util.various.swing.JCommand;
import demetra.ui.util.FontAwesomeUtils;
import demetra.demo.DemoTsBuilder;
import java.awt.event.ActionEvent;
import static java.beans.BeanInfo.ICON_COLOR_16x16;
import java.util.function.BiConsumer;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import org.openide.awt.DropDownButtonFactory;

/**
 *
 * @author Philippe Charles
 * @param <C>
 */
final class RandomTsCommand<C> extends JCommand<C> {

    public static <X> RandomTsCommand<X> of(BiConsumer<X, TsInformation> consumer) {
        return new RandomTsCommand<>(consumer);
    }

    private final BiConsumer<C, TsInformation> consumer;
    private final BoundedRangeModel obsCountModel;
    private final DemoTsBuilder builder;

    private RandomTsCommand(BiConsumer<C, TsInformation> consumer) {
        this.consumer = consumer;
        this.obsCountModel = new DefaultBoundedRangeModel(12 * 10, 12, 0, 12 * 100);
        this.builder = new DemoTsBuilder();
    }

    @Override
    final public void execute(C c) throws Exception {
        consumer.accept(c, TsConverter.fromTsBuilder(builder.obsCount(obsCountModel.getValue()).build()));
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
        obsCountModel.addChangeListener(event -> result.actionPerformed(null));
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

        JLabel label = new JLabel(obsCountModel.getValue() + " obs");
        obsCountModel.addChangeListener(event -> label.setText(obsCountModel.getValue() + " obs"));
        menu.add(label);
        return menu;
    }
}
