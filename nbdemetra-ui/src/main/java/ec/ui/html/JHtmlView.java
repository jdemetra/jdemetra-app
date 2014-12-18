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
import java.awt.BorderLayout;
import javax.swing.BorderFactory;

/**
 *
 * @author Philippe Charles
 */
public final class JHtmlView extends AHtmlView {

    private final JHtmlPane html;

    public JHtmlView() {
        this.html = new JHtmlPane();
        html.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new BorderLayout());
        add(NbComponents.newJScrollPane(html), BorderLayout.CENTER);
    }

    @Override
    public void loadContent(String content) {
        html.setText(content);
        html.setCaretPosition(0);
    }
}
