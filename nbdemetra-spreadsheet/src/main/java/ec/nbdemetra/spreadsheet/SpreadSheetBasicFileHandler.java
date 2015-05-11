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

import ec.util.grid.swing.ext.SheetRowRenderer;
import ec.util.grid.swing.ext.SheetCellRenderer;
import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import com.google.common.collect.FluentIterable;
import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.ThemeSupport;
import ec.tss.tsproviders.utils.DataFormat;
import ec.ui.commands.ColorSchemeCommand;
import ec.ui.interfaces.IColorSchemeAble;
import ec.util.chart.ColorScheme;
import ec.util.chart.swing.ColorSchemeIcon;
import ec.util.grid.swing.JGrid;
import ec.util.grid.swing.ext.SheetColumnRenderer;
import ec.util.grid.swing.ext.SheetGridCommand;
import ec.util.grid.swing.ext.SheetGridModel;
import ec.util.spreadsheet.Book;
import ec.util.spreadsheet.Sheet;
import ec.util.spreadsheet.helpers.ArrayBook;
import ec.util.various.swing.BasicFileViewer;
import ec.util.various.swing.JCommand;
import ec.util.various.swing.PopupMouseAdapter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.AbstractList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.util.Lookup;

/**
 *
 * @author Philippe Charles
 */
public final class SpreadSheetBasicFileHandler implements BasicFileViewer.BasicFileHandler {

    private final FluentIterable<? extends Book.Factory> factories;
    private final View uniqueView;

    public SpreadSheetBasicFileHandler() {
        this.factories = FluentIterable.from(Lookup.getDefault().lookupAll(Book.Factory.class));
        this.uniqueView = new View();
    }

    //<editor-fold defaultstate="collapsed" desc="BasicFileHandler">
    @Override
    public Object asyncLoad(File file, BasicFileViewer.ProgressCallback progress) throws Exception {
        ArrayBook.Builder result = ArrayBook.builder();
        Stopwatch sw = Stopwatch.createStarted();
        Book.Factory factory = factories.firstMatch(filePredicate(file)).get();
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
        ((View) uniqueView).setModel(Model.EMPTY);
    }

    @Override
    public boolean accept(File pathname) {
        return factories.anyMatch(filePredicate(pathname));
    }
    //</editor-fold>

    private static class Model {

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

    private static class View extends JPanel implements IColorSchemeAble {

        private static final String ZOOM_RATIO_PROPERTY = "zoomRatio";
        private static final String INVERT_COLORS_PROPERTY = "invertColors";
        //
        final JTabbedPane tabbedPane;
        final JMenuBar statusBar;
        final JLabel statusLabel;
        final JSlider zoomSlider;
        final JMenu zoomLabel;
        final JMenu menu;
        final SheetGridCommand copy = SheetGridCommand.copySelection(false, false);
        final SheetGridCommand copyAll = SheetGridCommand.copyAll(false, false);
        final DataFormat dataFormat = new DataFormat(Locale.ROOT, "yyyy-MM-dd", null);
        // Properties
        final ThemeSupport themeSupport;
        int zoomRatio;
        boolean invertColors;

        public View() {
            this.tabbedPane = new JTabbedPane();
            this.statusBar = new JMenuBar();
            this.statusLabel = new JLabel();
            this.zoomSlider = new JSlider(10, 200);
            this.zoomLabel = new JMenu("100%");
            this.themeSupport = new ThemeSupport() {
                @Override
                protected void colorSchemeChanged() {
                    View.this.firePropertyChange(COLOR_SCHEME_PROPERTY, null, themeSupport.getColorScheme());
                }
            };
            this.zoomRatio = 100;
            this.invertColors = false;
            this.menu = createMenu();

            zoomSlider.setValue(zoomRatio);
            zoomSlider.setOpaque(false);
            zoomSlider.setMaximumSize(new Dimension(100, 30));
            zoomSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    setZoomRatio(zoomSlider.getValue());
                }
            });
            for (int o : new int[]{200, 100, 75, 50, 25}) {
                zoomLabel.add(new JCheckBoxMenuItem(new ZoomRatioCommand(o).toAction(this))).setText(o + "%");
            }

//            statusBar.setFloatable(false);
            statusBar.setOpaque(false);
            statusBar.add(statusLabel);
            statusBar.add(Box.createHorizontalGlue());
//            statusBar.addSeparator();
            statusBar.add(zoomLabel);
            statusBar.add(zoomSlider);

            setLayout(new BorderLayout());
            add(tabbedPane, BorderLayout.CENTER);
            add(statusBar, BorderLayout.SOUTH);

            addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    switch (evt.getPropertyName()) {
                        case COLOR_SCHEME_PROPERTY:
                            onColorSchemeChange();
                            break;
                        case ZOOM_RATIO_PROPERTY:
                            onZoomRatioChange();
                            break;
                        case INVERT_COLORS_PROPERTY:
                            onInvertColorsChange();
                            break;
                    }
                }
            });
        }

        private JMenu createMenu() {
            JMenu result = new JMenu();
            result.add(new AbstractAction("Copy") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Component c = tabbedPane.getSelectedComponent();
                    if (c instanceof JGrid) {
                        copy.execute((JGrid) c);
                    }
                }
            });
            result.add(new AbstractAction("Copy all") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Component c = tabbedPane.getSelectedComponent();
                    if (c instanceof JGrid) {
                        copyAll.execute((JGrid) c);
                    }
                }
            });
            result.addSeparator();

            JMenuItem item;

            item = result.add(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            });
            item.setText("Edit format..");
            item.setEnabled(false);

            JMenu colorSchemeMenu = new JMenu("Color scheme");
            item = colorSchemeMenu.add(new JCheckBoxMenuItem(ColorSchemeCommand.applyColorScheme(null).toAction(this)));
            item.setText("Default");
            colorSchemeMenu.addSeparator();
            for (ColorScheme o : DemetraUI.getDefault().getColorSchemes()) {
                item = new JCheckBoxMenuItem(ColorSchemeCommand.applyColorScheme(o).toAction(this));
                item.setText(o.getDisplayName());
                item.setIcon(new ColorSchemeIcon(o));
                colorSchemeMenu.add(item);
            }
            result.add(colorSchemeMenu);

            item = result.add(new JCheckBoxMenuItem(new InvertColorsCommand().toAction(this)));
            item.setText("Invert colors");

            return result;
        }

        void addGrid(Sheet sheet) {
            JGrid grid = new JGrid();
            grid.setModel(new SheetGridModel(sheet));
            grid.getRowSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            grid.setColumnSelectionAllowed(true);
            grid.setRowRenderer(new SheetRowRenderer());
            grid.setColumnRenderer(new SheetColumnRenderer());
            grid.setOddBackground(null);
            grid.addMouseListener(PopupMouseAdapter.fromMenu(menu));
            grid.getActionMap().put("copy", copy.toAction(grid));
            tabbedPane.add(sheet.getName(), grid);
        }

        void setModel(Model model) {
            tabbedPane.removeAll();
            for (int s = 0; s < model.book.getSheetCount(); s++) {
                addGrid(model.book.getSheet(s));
            }
            onColorSchemeChange();
            onZoomRatioChange();
            statusLabel.setText(" File " + model.file.getName() + " loaded by " + model.factoryName + " in " + model.duration + " ms");
        }

        //<editor-fold defaultstate="collapsed" desc="Event handlers">
        private void onColorSchemeChange() {
            SheetCellRenderer cellRenderer = new SheetCellRenderer(dataFormat, themeSupport, invertColors);
            for (JGrid grid : asList(tabbedPane).filter(JGrid.class)) {
                grid.setGridColor(themeSupport.getGridColor());
                grid.setDefaultRenderer(Object.class, cellRenderer);
            }
        }

        private void onZoomRatioChange() {
            zoomLabel.setText(zoomRatio + "%");
            zoomSlider.setValue(zoomRatio);
            Font font = computeFont(getFont(), zoomRatio);
            for (JGrid grid : asList(tabbedPane).filter(JGrid.class)) {
                grid.setFont(font);
            }
        }

        private void onInvertColorsChange() {
            onColorSchemeChange();
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
        @Override
        public void setColorScheme(ColorScheme colorScheme) {
            themeSupport.setLocalColorScheme(colorScheme);
        }

        @Override
        public ColorScheme getColorScheme() {
            return themeSupport.getLocalColorScheme();
        }

        public int getZoomRatio() {
            return zoomRatio;
        }

        public void setZoomRatio(int zoomRatio) {
            int old = this.zoomRatio;
            this.zoomRatio = zoomRatio >= 10 && zoomRatio <= 200 ? zoomRatio : 100;
            firePropertyChange(ZOOM_RATIO_PROPERTY, old, this.zoomRatio);
        }

        public boolean isInvertColors() {
            return invertColors;
        }

        public void setInvertColors(boolean invertColors) {
            boolean old = this.invertColors;
            this.invertColors = invertColors;
            firePropertyChange(INVERT_COLORS_PROPERTY, old, this.invertColors);
        }
        //</editor-fold>
    }

    //<editor-fold defaultstate="collapsed" desc="Tools">
    private static Font computeFont(Font originalFont, int zoomRatio) {
        Font font = originalFont;
        float floatRatio = ((float) zoomRatio) / 100.0f;
        if (floatRatio != 1) {
            float scaledSize = originalFont.getSize2D() * floatRatio;
            font = originalFont.deriveFont(scaledSize);
        }
        return font;
    }

    private static Predicate<Book.Factory> filePredicate(final File file) {
        return new Predicate<Book.Factory>() {
            @Override
            public boolean apply(Book.Factory input) {
                return input != null && input.accept(file);
            }
        };
    }

    private static FluentIterable<Component> asList(final JTabbedPane pane) {
        return FluentIterable.from(new AbstractList<Component>() {
            @Override
            public Component get(int index) {
                return pane.getComponentAt(index);
            }

            @Override
            public int size() {
                return pane.getTabCount();
            }
        });
    }
    //</editor-fold>

    private static class ZoomRatioCommand extends JCommand<View> {

        private final int zoomRatio;

        public ZoomRatioCommand(int zoomRatio) {
            this.zoomRatio = zoomRatio;
        }

        @Override
        public void execute(View component) throws Exception {
            component.setZoomRatio(zoomRatio);
        }

        @Override
        public boolean isSelected(View component) {
            return zoomRatio == component.getZoomRatio();
        }

        @Override
        public ActionAdapter toAction(View component) {
            return super.toAction(component).withWeakPropertyChangeListener(component, View.ZOOM_RATIO_PROPERTY);
        }
    }

    private static class InvertColorsCommand extends JCommand<View> {

        @Override
        public void execute(View component) throws Exception {
            component.setInvertColors(!component.isInvertColors());
        }

        @Override
        public boolean isSelected(View component) {
            return component.isInvertColors();
        }

        @Override
        public ActionAdapter toAction(View component) {
            return super.toAction(component).withWeakPropertyChangeListener(component, View.INVERT_COLORS_PROPERTY);
        }
    }
}
