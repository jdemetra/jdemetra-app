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

import demetra.desktop.design.SwingComponent;
import demetra.desktop.design.SwingProperty;
import demetra.ui.util.NbComponents;
import ec.tss.html.AbstractHtmlElement;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlTag;
import ec.tss.html.HtmlUtil;
import ec.ui.AHtmlView;
import ec.ui.html.JHtmlView;
import java.awt.BorderLayout;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JComponent;

/**
 *
 * @author Philippe Charles
 */
@SwingComponent
public final class JReflectComponent extends JComponent {

    public static JReflectComponent of(Class<?> clazz) {
        JReflectComponent result = new JReflectComponent();
        result.setClazz(clazz);
        return result;
    }

    @SwingProperty
    public static final String CLAZZ_PROPERTY = "clazz";

    @SwingProperty
    public static final String EXTRACTOR_PROPERTY = "extractor";

    private final AHtmlView htmlView;
    private Class<?> clazz;
    private Function<Class<?>, List<Method>> extractor;

    public JReflectComponent() {
        this.htmlView = new JHtmlView();
        this.clazz = null;
        this.extractor = o -> getPublicMethodsOf(o, true);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        add(NbComponents.newJScrollPane(htmlView), BorderLayout.CENTER);

        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case CLAZZ_PROPERTY:
                case EXTRACTOR_PROPERTY:
                    onChange();
                    break;
            }
        });
    }

    private void onChange() {
        htmlView.loadContent(HtmlUtil.toString(new ClazzReport(clazz, extractor)));
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

    public Function<Class<?>, List<Method>> getExtractor() {
        return extractor;
    }

    public void setExtractor(Function<Class<?>, List<Method>> extractor) {
        Function<Class<?>, List<Method>> old = this.extractor;
        this.extractor = extractor != null ? extractor : o -> getPublicMethodsOf(o, true);
        firePropertyChange(EXTRACTOR_PROPERTY, old, this.extractor);
    }
    //</editor-fold>

    private static final class ClazzReport extends AbstractHtmlElement {

        private final Class clazz;
        private final Function<Class<?>, List<Method>> extractor;

        public ClazzReport(Class clazz, Function<Class<?>, List<Method>> extractor) {
            this.clazz = clazz;
            this.extractor = extractor;
        }

        @Override
        public void write(HtmlStream stream) throws IOException {
            if (clazz != null) {
                stream.write(HtmlTag.HEADER1, clazz.getName()).newLine();
                stream.write("<table style='border:none;'>");
                for (Method o : extractor.apply(clazz)) {
                    stream.write("<tr>")
                            .write("<td>").write(o.getReturnType().getSimpleName()).write("</td>")
                            .write("<td align='left'>")
                            .write("<b>").write(o.getName()).write("</b>")
                            .write(" (");
                    Class[] parameters = o.getParameterTypes();
                    if (parameters.length > 0) {
                        stream.write(parameters[0].getSimpleName());
                        for (int i = 1; i < parameters.length; i++) {
                            stream.write(", ").write(parameters[i].getSimpleName());
                        }
                    }
                    stream.write(")</td>")
                            .write("</tr>");
                }
                stream.write("</table>");
            }
        }

    }

    public static List<Method> getPublicMethodsOf(Class<?> type, boolean inherit) {
        return inherit
                ? Arrays.asList(type.getMethods())
                : Stream.of(type.getDeclaredMethods())
                .filter(o -> Modifier.isPublic(o.getModifiers()))
                .sorted((l, r) -> l.getName().compareTo(r.getName()))
                .collect(Collectors.toList());
    }
}
