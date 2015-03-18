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
package ec.util.completion.swing;

import ec.util.completion.AbstractAutoCompletionSource;
import ec.util.completion.AutoCompletionSource;
import ec.util.completion.AutoCompletionSource.Behavior;
import ec.util.completion.AutoCompletionSources;
import ec.util.completion.FileAutoCompletionSource;
import ec.util.desktop.Desktop;
import ec.util.desktop.DesktopManager;
import ec.util.various.swing.BasicSwingLauncher;
import ec.util.various.swing.FontAwesome;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.border.Border;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Philippe Charles
 */
public final class JAutoCompletionDemo extends javax.swing.JPanel {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(JAutoCompletionDemo.class)
                .title("Auto Completion Demo")
                .size(414, 300)
                .resizable(true)
                .launch();
    }

    public JAutoCompletionDemo() {
        initComponents();

        initExample1();
        initExample2();
        initExample3();
        initExample4();
        initExample5();
        initExample6();
        initExample7();
        initExample8();
    }

    //<editor-fold defaultstate="collapsed" desc="Examples">
    final void initExample1() {
        Locale[] locales = Locale.getAvailableLocales();
        Arrays.sort(locales, LocaleComparator.INSTANCE);

        JAutoCompletion ac = new JAutoCompletion(singleLocale);
        ac.setSource(AutoCompletionSources.of(false, locales));
        ac.getList().setCellRenderer(new CustomListCellRenderer(false) {
            @Override
            protected String toString(String term, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Locale) {
                    Locale locale = (Locale) value;
                    String newValue = locale.toString() + " - " + locale.getDisplayName();
                    return super.toString(term, list, newValue, index, isSelected, cellHasFocus);
                }
                return super.toString(term, list, value, index, isSelected, cellHasFocus);
            }
        });

        singleLocaleLabel.setToolTipText(toHtml(ac));
    }

    final void initExample2() {
        Locale[] locales = Locale.getAvailableLocales();
        Arrays.sort(locales, LocaleComparator.INSTANCE);

        JAutoCompletion ac = new JAutoCompletion(multipleLocale);
        ac.setSeparator(",");
        ac.setSource(AutoCompletionSources.of(false, locales));

        multipleLocaleLabel.setToolTipText(toHtml(ac));
    }

    final void initExample3() {
        List<String> propertyNames = new ArrayList<>(System.getProperties().stringPropertyNames());
        Collections.sort(propertyNames);

        JAutoCompletion ac = new JAutoCompletion(systemProperty);
        ac.setSource(AutoCompletionSources.of(false, propertyNames));

        systemPropertyLabel.setToolTipText(toHtml(ac));
    }

    final void initExample4() {
        JAutoCompletion ac = new JAutoCompletion(file);
        ac.setSource(new DesktopFileAutoCompletionSource());
        ac.getList().setCellRenderer(new FileListCellRenderer(Executors.newSingleThreadExecutor()));

        fileLabel.setToolTipText(toHtml(ac));
    }

    final void initExample5() {
        final Map<String, FontAwesome> emoticons = new HashMap<>();
        emoticons.put(":-)", FontAwesome.FA_SMILE_O);
        emoticons.put(":-(", FontAwesome.FA_FROWN_O);
        emoticons.put(":-|", FontAwesome.FA_MEH_O);

        JAutoCompletion ac = new JAutoCompletion(instantMessaging);
        ac.setSeparator(" ");
        ac.setSource(AutoCompletionSources.of(true, emoticons.keySet()));
        ac.getList().setLayoutOrientation(JList.HORIZONTAL_WRAP);
        ac.getList().setVisibleRowCount(-1);
        ac.getList().setCellRenderer(new CustomListCellRenderer(false) {
            @Override
            protected Icon toIcon(String term, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return emoticons.get((String) value).getIcon(isSelected ? list.getSelectionForeground() : Color.ORANGE.darker(), 32f);
            }

            @Override
            protected String toString(String term, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return "";
            }
        });

        imLabel.setToolTipText(toHtml(ac));
    }

    final void initExample6() {
        JAutoCompletion ac = new JAutoCompletion(lotr);
        ac.setSource(new AbstractAutoCompletionSource<Lotr.Character>() {
            @Override
            protected Iterable<Lotr.Character> getAllValues() throws Exception {
                TimeUnit.SECONDS.sleep(1);
                return Arrays.asList(Lotr.load().characters);
            }

            @Override
            public Behavior getBehavior(String term) {
                return Behavior.ASYNC;
            }

            @Override
            protected String getValueAsString(Lotr.Character value) {
                return value.name;
            }

            @Override
            protected boolean matches(TermMatcher termMatcher, Lotr.Character input) {
                return termMatcher.matches(input.name)
                        || termMatcher.matches(input.description);
            }
        });
        ac.getList().setCellRenderer(new CustomListCellRenderer(false) {
            @Override
            protected String toString(String term, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Lotr.Character c = (Lotr.Character) value;
                setToolTipText(c.description);
                String name = isSelected ? c.name : ("<font color=" + (c.type.equals("Antagonists") ? "#BF381A" : "#397249") + ">" + c.name + "</font>");
                return "<html><b>" + name + "</b><br>" + c.description;
            }
        });
        ac.getList().setFixedCellHeight(40);

        lotrLabel.setToolTipText(toHtml(ac));
    }

    final void initExample7() {
        JAutoCompletion ac = new JAutoCompletion(exception);
        ac.setSource(new AutoCompletionSource() {
            @Override
            public Behavior getBehavior(String term) {
                return Behavior.ASYNC;
            }

            @Override
            public String toString(Object value) {
                return value.toString();
            }

            @Override
            public List<?> getValues(String term) throws Exception {
                TimeUnit.SECONDS.sleep(1);
                throw new IOException("boooooooom!");
            }
        });
    }

    final void initExample8() {
        JAutoCompletion ac = new JAutoCompletion(custom);
        ac.setSource(AutoCompletionSources.of(false, FontAwesome.values()));
        ac.getList().setCellRenderer(new DefaultListCellRenderer() {
            final Border border = BorderFactory.createEmptyBorder(5, 2, 5, 2);

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                FontAwesome fa = (FontAwesome) value;
                setText("<html><b>" + fa.name().replace("FA", "").replace("_", " ") + "</b><br>" + fa.name().toLowerCase(Locale.ROOT));
                setIcon(fa.getIcon(getForeground(), getFont().getSize2D() * 2));
                setBorder(border);
                return this;
            }
        });
    }
    //</editor-fold>

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        singleLocale = new javax.swing.JTextField();
        singleLocaleLabel = new javax.swing.JLabel();
        multipleLocaleLabel = new javax.swing.JLabel();
        multipleLocale = new javax.swing.JTextField();
        systemPropertyLabel = new javax.swing.JLabel();
        systemProperty = new javax.swing.JTextField();
        fileLabel = new javax.swing.JLabel();
        file = new javax.swing.JTextField();
        imLabel = new javax.swing.JLabel();
        instantMessaging = new javax.swing.JTextField();
        lotrLabel = new javax.swing.JLabel();
        lotr = new javax.swing.JTextField();
        lotrLabel1 = new javax.swing.JLabel();
        exception = new javax.swing.JTextField();
        custom = new javax.swing.JTextField();
        customLabel = new javax.swing.JLabel();

        singleLocaleLabel.setText("Single locale:");

        multipleLocaleLabel.setText("Multiple locale:");

        systemPropertyLabel.setText("System property:");

        fileLabel.setText("File path:");

        imLabel.setText("Instant messaging:");

        lotrLabel.setText("LOTR character:");

        lotrLabel1.setText("Exception:");

        customLabel.setText("Custom:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lotrLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lotrLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(singleLocaleLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(multipleLocaleLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(systemPropertyLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(fileLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(imLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(customLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lotr)
                    .addComponent(instantMessaging, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                    .addComponent(file)
                    .addComponent(systemProperty)
                    .addComponent(multipleLocale)
                    .addComponent(exception)
                    .addComponent(custom)
                    .addComponent(singleLocale))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(singleLocaleLabel)
                    .addComponent(singleLocale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(multipleLocaleLabel)
                    .addComponent(multipleLocale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(systemPropertyLabel)
                    .addComponent(systemProperty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileLabel)
                    .addComponent(file, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imLabel)
                    .addComponent(instantMessaging, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lotrLabel)
                    .addComponent(lotr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lotrLabel1)
                    .addComponent(exception, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(custom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(customLabel))
                .addContainerGap(29, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField custom;
    private javax.swing.JLabel customLabel;
    private javax.swing.JTextField exception;
    private javax.swing.JTextField file;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JLabel imLabel;
    private javax.swing.JTextField instantMessaging;
    private javax.swing.JTextField lotr;
    private javax.swing.JLabel lotrLabel;
    private javax.swing.JLabel lotrLabel1;
    private javax.swing.JTextField multipleLocale;
    private javax.swing.JLabel multipleLocaleLabel;
    private javax.swing.JTextField singleLocale;
    private javax.swing.JLabel singleLocaleLabel;
    private javax.swing.JTextField systemProperty;
    private javax.swing.JLabel systemPropertyLabel;
    // End of variables declaration//GEN-END:variables

    private static final class Lotr {

        @XmlRootElement(name = "mainCharacters")
        static class MainCharacters {

            @XmlElement(name = "character")
            Character[] characters;
        }

        static class Character {

            @XmlAttribute(name = "name")
            String name;
            @XmlAttribute(name = "type")
            String type;
            @XmlAttribute(name = "description")
            String description;
        }

        public static MainCharacters load() throws JAXBException, IOException {
            try (InputStream stream = JAutoCompletionDemo.class.getResourceAsStream("lotr.xml")) {
                JAXBContext context = JAXBContext.newInstance(Lotr.MainCharacters.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                return (Lotr.MainCharacters) unmarshaller.unmarshal(stream);
            }
        }
    }

    private static String toHtml(JAutoCompletion o) {
        return "<html>"
                + "<b>autoFocus :</b> " + o.isAutoFocus() + "<br>"
                + "<b>delay :</b> " + o.getDelay() + "<br>"
                + "<b>enabled :</b> " + o.isEnabled() + "<br>"
                + "<b>minLength :</b> " + o.getMinLength() + "<br>"
                + "<b>separator :</b> '" + o.getSeparator() + "'";
    }

    private enum LocaleComparator implements Comparator<Locale> {

        INSTANCE;

        @Override
        public int compare(Locale o1, Locale o2) {
            return o1.toString().compareTo(o2.toString());
        }
    }

    private static final class DesktopFileAutoCompletionSource extends FileAutoCompletionSource {

        private Desktop getDesktop() {
            return DesktopManager.get();
        }

        @Override
        public List<File> getValues(String term) throws IOException {
            final List<File> result = super.getValues(term);
            Desktop desktop = getDesktop();
            if (!desktop.isSupported(Desktop.Action.SEARCH) || term.length() < 3) {
                return result;
            }
            final List<File> enhancedResult = new ArrayList<>();
            if (fileFilter == null) {
                Collections.addAll(enhancedResult, desktop.search(term));
            } else {
                for (File o : desktop.search(term)) {
                    if (fileFilter.accept(o)) {
                        enhancedResult.add(o);
                    }
                }
            }
            if (enhancedResult.isEmpty()) {
                return result;
            }
            if (enhancedResult.addAll(result)) {
                Collections.sort(enhancedResult);
            }
            return enhancedResult;
        }
    }
}
