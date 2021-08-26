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
package ec.nbdemetra.spreadsheet;

import com.google.common.base.Stopwatch;
import demetra.ui.util.NbComponents;
import ec.util.desktop.Desktop;
import ec.util.desktop.DesktopManager;
import ec.util.grid.swing.ext.SpreadSheetView;
import ec.util.spreadsheet.Book;
import ec.util.spreadsheet.helpers.ArrayBook;
import ec.util.various.swing.BasicFileViewer;
import static ec.util.various.swing.FontAwesome.FA_EXTERNAL_LINK;
import static ec.util.various.swing.FontAwesome.FA_INFO;
import static ec.util.various.swing.FontAwesome.FA_SEARCH;
import ec.util.various.swing.JCommand;
import demetra.ui.util.FontAwesomeUtils;
import demetra.ui.components.parts.HasColorSchemeSupport;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import static java.beans.BeanInfo.ICON_MONO_16x16;
import java.io.File;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import static org.openide.awt.DropDownButtonFactory.createDropDownButton;
import org.openide.util.Lookup;

/**
 *
 * @author Philippe Charles
 */
public final class SpreadSheetBasicFileHandler implements BasicFileViewer.BasicFileHandler {

    private final Collection<? extends Book.Factory> factories;
    private final View uniqueView;

    public SpreadSheetBasicFileHandler() {
        this.factories = Lookup.getDefault().lookupAll(Book.Factory.class);
        this.uniqueView = new View();
    }

    //<editor-fold defaultstate="collapsed" desc="BasicFileHandler">
    @Override
    public Object asyncLoad(File file, BasicFileViewer.ProgressCallback progress) throws Exception {
        ArrayBook.Builder result = ArrayBook.builder();
        Stopwatch sw = Stopwatch.createStarted();
        Book.Factory factory = factories.stream().filter(Objects::nonNull).filter(o -> o.accept(file)).findFirst().get();
        try (Book book = factory.load(file)) {
            for (int s = 0; s < book.getSheetCount(); s++) {
                result.sheet(book.getSheet(s));
                progress.setProgress(0, book.getSheetCount(), s);
            }
        }
        return new Model(factory.getName(), file, result.build(), sw.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    @Override
    public boolean isViewer(Component c) {
        return c instanceof View;
    }

    @Override
    public Component borrowViewer(Object data) {
        uniqueView.setModel((Model) data);
        return uniqueView;
    }

    @Override
    public void recycleViewer(Component c) {
        uniqueView.setModel(Model.EMPTY);
    }

    @Override
    public boolean accept(File pathname) {
        return factories.stream().filter(Objects::nonNull).anyMatch(o -> o.accept(pathname));
    }
    //</editor-fold>

    private static final class Model {

        static final Model EMPTY = new Model("", new File(""), ArrayBook.builder().build(), 0);
        //
        final String factoryName;
        final File file;
        final ArrayBook book;
        final long duration;

        public Model(String factoryName, File file, ArrayBook book, long duration) {
            this.factoryName = factoryName;
            this.file = file;
            this.book = book;
            this.duration = duration;
        }
    }

    private static final class View extends JPanel {

        private static final String MODEL_PROPERTY = "model";

        private final SpreadSheetView view;
        private Model model;

        public View() {
            this.view = new SpreadSheetView();
            this.model = Model.EMPTY;

            view.setComponentPopupMenu(createPopupMenu().getPopupMenu());

            setLayout(new BorderLayout());
            add(view, BorderLayout.CENTER);
            add(createToolbar(), BorderLayout.NORTH);

            addPropertyChangeListener(evt -> {
                switch (evt.getPropertyName()) {
                    case MODEL_PROPERTY:
                        onModelChange();
                        break;
                }
            });
        }

        private JMenu createPopupMenu() {
            JMenu result = new JMenu();
            result.add(SpreadSheetView.copySelection().toAction(view)).setText("Copy");
            result.add(SpreadSheetView.copyAll().toAction(view)).setText("Copy all");
            result.addSeparator();

            JMenuItem item;

            item = result.add(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            });
            item.setText("Edit format..");
            item.setEnabled(false);

            result.add(HasColorSchemeSupport.menuOf(view));

            item = result.add(new JCheckBoxMenuItem(SpreadSheetView.invertColors().toAction(view)));
            item.setText("Invert colors");

            return result;
        }

        private JToolBar createToolbar() {
            JToolBar result = NbComponents.newInnerToolbar();
            result.setFloatable(false);
            result.setOpaque(false);

            JButton button;
            button = result.add(new InfoCmd().toAction(this));
            button.setIcon(FontAwesomeUtils.getIcon(FA_INFO, ICON_MONO_16x16));
            button.setToolTipText("Info");

            button = result.add(new ShowInFolderCmd().toAction(this));
            button.setIcon(FontAwesomeUtils.getIcon(FA_EXTERNAL_LINK, ICON_MONO_16x16));
            button.setToolTipText("Show in folder");

            result.addSeparator();

            button = createDropDownButton(FontAwesomeUtils.getIcon(FA_SEARCH, ICON_MONO_16x16), createZoomMenu().getPopupMenu());
            button.setToolTipText("Zoom");
            result.add(button);

            return result;
        }

        private JMenu createZoomMenu() {
            JMenu result = new JMenu();
            final JSlider slider = new JSlider(10, 200, 100);
            {
                slider.setPreferredSize(new Dimension(50, slider.getPreferredSize().height));
                slider.addChangeListener(evt -> view.setZoomRatio(slider.getValue()));
                view.addPropertyChangeListener(SpreadSheetView.ZOOM_RATIO_PROPERTY, evt -> slider.setValue(view.getZoomRatio()));
            }
            result.add(slider);
            for (int o : new int[]{200, 100, 75, 50, 25}) {
                result.add(new JCheckBoxMenuItem(SpreadSheetView.applyZoomRatio(o).toAction(view))).setText(o + "%");
            }
            return result;
        }

        private void onModelChange() {
            view.setModel(model.book);
        }

        public Model getModel() {
            return model;
        }

        public void setModel(Model model) {
            Model old = this.model;
            this.model = model != null ? model : Model.EMPTY;
            firePropertyChange(MODEL_PROPERTY, old, this.model);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    private static final class ShowInFolderCmd extends JCommand<View> {

        @Override
        public void execute(View component) throws Exception {
            DesktopManager.get().showInFolder(component.getModel().file);
        }

        @Override
        public boolean isEnabled(View component) {
            return DesktopManager.get().isSupported(Desktop.Action.SHOW_IN_FOLDER);
        }
    }

    private static final class InfoCmd extends JCommand<View> {

        @Override
        public void execute(View c) throws Exception {
            Model model = c.getModel();
            String message = " File '" + model.file.getName() + "' loaded by " + model.factoryName + " in " + model.duration + " ms";
            JOptionPane.showMessageDialog(c, message);
        }
    }
    //</editor-fold>
}
