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
package ec.ui.utils;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Mats Maggi
 */
public class LoadingPanel extends JPanel {

    private boolean loading = false;
    private final JComponent mainPanel;
    private final GlassPane loadingPanel = new GlassPane();
    private final String LOADING = "LOADING";
    private final String MAIN = "MAIN";

    public LoadingPanel(JComponent pane) {
        super();
        setLayout(new CardLayout());
        mainPanel = pane;
        add(mainPanel, MAIN);
        add(loadingPanel, LOADING);
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        CardLayout cl = (CardLayout) (getLayout());
        cl.show(this, loading ? LOADING : MAIN);
    }

    private class GlassPane extends JPanel {

        public GlassPane() {
            setLayout(new BorderLayout());
            JLabel label = new JLabel("Loading...", JLabel.CENTER);
            label.setFont(label.getFont().deriveFont(16f));
            add(label, BorderLayout.CENTER);
            setOpaque(false);
        }
    }

}
