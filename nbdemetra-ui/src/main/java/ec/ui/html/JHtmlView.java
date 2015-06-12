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
package ec.ui.html;

import ec.nbdemetra.ui.NbComponents;
import ec.ui.AHtmlView;
import ec.util.various.swing.JCommand;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JMenu;

/**
 *
 * @author Philippe Charles
 */
public final class JHtmlView extends AHtmlView {

    private final JHtmlPane html;

    public JHtmlView() {
        this.html = new JHtmlPane();
        html.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        html.setComponentPopupMenu(createMenu().getPopupMenu());

        setLayout(new BorderLayout());
        add(NbComponents.newJScrollPane(html), BorderLayout.CENTER);
    }

    @Override
    public void loadContent(String content) {
        html.setText(content);
        html.setCaretPosition(0);
    }

    private JMenu createMenu() {
        JMenu result = new JMenu();
        result.add(CopyCmd.INSTANCE.toAction(html)).setText("Copy");
        return result;
    }

    private static final class CopyCmd extends JCommand<JHtmlPane> {

        private static final CopyCmd INSTANCE = new CopyCmd();

        @Override
        public void execute(JHtmlPane c) throws Exception {
            if (c.getSelectedText() != null) {
                c.copy();
            } else {
                c.selectAll();
                c.copy();
                c.select(0, 0);
            }
        }
    }
}
