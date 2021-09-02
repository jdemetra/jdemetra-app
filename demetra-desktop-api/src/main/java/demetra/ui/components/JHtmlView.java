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
package demetra.ui.components;

import demetra.desktop.design.SwingComponent;
import demetra.desktop.design.SwingProperty;
import demetra.ui.components.parts.HasTsCollection;
import internal.ui.components.DemoTsBuilder;

import javax.swing.*;
import java.awt.*;
import java.beans.Beans;

/**
 * @author Philippe Charles
 */
@SwingComponent
public final class JHtmlView extends JComponent {

    @SwingProperty
    public static final String HTML_PROPERTY = "html";
    private static final String DEFAULT_HTML = "";
    private String html = DEFAULT_HTML;

    public JHtmlView() {
        ComponentBackend.getDefault().install(this);
        applyDesignTimeProperties();
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        String old = this.html;
        this.html = html != null ? html : DEFAULT_HTML;
        firePropertyChange(HTML_PROPERTY, old, this.html);
    }

    private void applyDesignTimeProperties() {
        if (Beans.isDesignTime()) {
            setHtml("Html View");
            setPreferredSize(new Dimension(200, 150));
        }
    }
}
