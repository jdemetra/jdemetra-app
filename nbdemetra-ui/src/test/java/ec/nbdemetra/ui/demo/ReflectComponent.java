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
package ec.nbdemetra.ui.demo;

import ec.nbdemetra.ui.ComponentFactory;
import ec.nbdemetra.ui.NbComponents;
import ec.tss.html.AbstractHtmlElement;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlTag;
import ec.tss.html.HtmlUtil;
import ec.ui.AHtmlView;
import java.awt.BorderLayout;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import javax.swing.JComponent;

/**
 *
 * @author Philippe Charles
 */
public final class ReflectComponent extends JComponent {

    public static ReflectComponent of(Class<?> clazz) {
        ReflectComponent result = new ReflectComponent();
        result.setClazz(clazz);
        return result;
    }

    public static final String CLAZZ_PROPERTY = "clazz";

    private final AHtmlView htmlView;
    private Class<?> clazz;

    public ReflectComponent() {
        this.htmlView = ComponentFactory.getDefault().newHtmlView();
        this.clazz = null;

        setLayout(new BorderLayout());
        add(NbComponents.newJScrollPane(htmlView), BorderLayout.CENTER);

        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case CLAZZ_PROPERTY:
                    onClazzChange();
                    break;
            }
        });
    }

    private void onClazzChange() {
        htmlView.loadContent(HtmlUtil.toString(new ClazzReport(clazz)));
    }

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        Class<?> old = this.clazz;
        this.clazz = clazz;
        firePropertyChange(CLAZZ_PROPERTY, old, this.clazz);
    }
    //</editor-fold>

    private static final class ClazzReport extends AbstractHtmlElement {

        private final Class clazz;

        public ClazzReport(Class clazz) {
            this.clazz = clazz;
        }

        @Override
        public void write(HtmlStream stream) throws IOException {
            if (clazz != null) {
                stream.write(HtmlTag.HEADER1, h1, clazz.getName()).newLine();
                Method[] methods = clazz.getMethods();
                Arrays.sort(methods, (l, r) -> l.getName().compareTo(r.getName()));
                for (Method o : methods) {
                    stream.write(o.getReturnType().getSimpleName());
                    stream.write(" <b>").write(o.getName()).write("</b>");
                    stream.write(" (");
                    Class[] parameters = o.getParameterTypes();
                    if (parameters.length > 0) {
                        stream.write(parameters[0].getSimpleName());
                        for (int i = 1; i < parameters.length; i++) {
                            stream.write(", ").write(parameters[i].getSimpleName());
                        }
                    }
                    stream.write(")").newLine();
                }
            }
        }
    }
}
