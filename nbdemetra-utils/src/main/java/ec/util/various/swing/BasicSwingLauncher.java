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
package ec.util.various.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * A Swing launcher that allows fast GUI prototyping by handling tedious code
 * and using a fluent API.
 *
 * @author Philippe Charles
 */
public final class BasicSwingLauncher {

    private static final Logger LOGGER = Logger.getLogger(BasicSwingLauncher.class.getName());
    private String lookAndFeelClassName = null;
    private String title = null;
    private Dimension size = null;
    private Callable<? extends Component> contentSupplier = null;
    private boolean centerOnScreen = true;
    private Callable<? extends List<? extends Image>> iconsSupplier = null;
    private boolean resizable = true;

    //<editor-fold defaultstate="collapsed" desc="Options setters">
    @Nonnull
    public BasicSwingLauncher logLevel(@Nullable Level level) {
        LOGGER.setLevel(level);
        return this;
    }

    @Nonnull
    public BasicSwingLauncher systemLookAndFeel() {
        return lookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }

    @Nonnull
    public BasicSwingLauncher lookAndFeel(@Nullable String lookAndFeelClassName) {
        this.lookAndFeelClassName = lookAndFeelClassName;
        return this;
    }

    @Nonnull
    public BasicSwingLauncher title(@Nullable String title) {
        this.title = title;
        return this;
    }

    @Nonnull
    public BasicSwingLauncher size(int width, int height) {
        return size(new Dimension(width, height));
    }

    @Nonnull
    public BasicSwingLauncher size(@Nullable Dimension size) {
        this.size = size;
        return this;
    }

    @Nonnull
    public BasicSwingLauncher content(@Nullable Class<? extends Component> contentClass) {
        return content((Callable<? extends Component>) (contentClass == null ? null : newInstance(contentClass)));
    }

    @Nonnull
    public BasicSwingLauncher content(@Nullable Callable<? extends Component> contentSupplier) {
        this.contentSupplier = contentSupplier;
        return this;
    }

    @Nonnull
    public BasicSwingLauncher centerOnScreen(boolean centerOnScreen) {
        this.centerOnScreen = centerOnScreen;
        return this;
    }

    @Nonnull
    public BasicSwingLauncher resizable(boolean resizable) {
        this.resizable = resizable;
        return this;
    }

    @Nonnull
    public BasicSwingLauncher icons(@Nonnull String... iconsPaths) {
        return icons(newImageList(iconsPaths));
    }

    @Nonnull
    public BasicSwingLauncher icons(@Nullable Callable<? extends List<? extends Image>> iconsSupplier) {
        this.iconsSupplier = iconsSupplier;
        return this;
    }
    //</editor-fold>

    /**
     * Launch the application in a new frame with all the configured options.
     */
    public void launch() {
        launch(lookAndFeelClassName != null ? lookAndFeelClassName : UIManager.getSystemLookAndFeelClassName(),
                title != null ? title : "SimpleApp",
                size != null ? size : new Dimension(800, 600),
                contentSupplier != null ? contentSupplier : newInstance(JPanel.class), iconsSupplier != null ? iconsSupplier : newImageList(),
                centerOnScreen, resizable);
    }

    private static void launch(
            @Nonnull final String lookAndFeelClassName,
            @Nonnull final String title,
            @Nonnull final Dimension size,
            @Nonnull final Callable<? extends Component> contentSupplier,
            @Nonnull final Callable<? extends List<? extends Image>> iconsSupplier,
            final boolean centerOnScreen, final boolean resizable) {

        LOGGER.log(Level.FINE, "lookAndFeelClassName='%s'", lookAndFeelClassName);
        LOGGER.log(Level.FINE, "title='{0}'", title);
        LOGGER.log(Level.FINE, "size='{0}'", size);
        LOGGER.log(Level.FINE, "contentSupplier='{0}'", contentSupplier);
        LOGGER.log(Level.FINE, "centerOnScreen='{0}'", centerOnScreen);

        try {
            UIManager.setLookAndFeel(lookAndFeelClassName);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            LOGGER.log(Level.WARNING, "Cannot set look&feel", ex);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    JFrame frame = new JFrame();
                    frame.setTitle(title);
                    frame.setIconImages(iconsSupplier.call());
                    frame.getContentPane().add(contentSupplier.call());
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(size);
                    frame.setResizable(resizable);
                    if (centerOnScreen) {
                        frame.setLocationRelativeTo(null);
                    }
                    frame.setVisible(true);
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Cannot launch app", ex);
                }
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    @Nonnull
    private static <X> Callable<X> newInstance(@Nonnull final Class<X> contentClass) {
        return new Callable<X>() {
            @Override
            public X call() throws Exception {
                return contentClass.newInstance();
            }
        };
    }

    @Nonnull
    private static Callable<List<? extends Image>> newImageList(@Nonnull final String... iconsPaths) {
        return new Callable<List<? extends Image>>() {
            @Override
            public List<? extends Image> call() throws Exception {
                List<Image> result = new ArrayList<>();
                for (String o : iconsPaths) {
                    URL url = BasicSwingLauncher.class.getResource(o);
                    if (url != null) {
                        result.add(new ImageIcon(url).getImage());
                    }
                }
                return result;
            }
        };
    }
    //</editor-fold>
}
