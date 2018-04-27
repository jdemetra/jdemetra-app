/*
 * Copyright 2018 National Bank of Belgium
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
package internal.ui.components;

import java.awt.Component;
import java.awt.Graphics;
import java.util.function.Function;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.plaf.LayerUI;

/**
 *
 * @author Philippe Charles
 * @param <T>
 */
public class ExtLayerUI<T extends Component> extends LayerUI<T> {

    private final CellRendererPane painter = new CellRendererPane();
    private final Function<T, Component> renderer;

    public ExtLayerUI(Function<T, Component> renderer) {
        this.renderer = renderer;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        Component rendererComponent = renderer.apply(((JLayer<T>) c).getView());
        if (rendererComponent != null) {
            painter.paintComponent(g, rendererComponent, c, 0, 0, c.getWidth(), c.getHeight());
        }
    }
}
