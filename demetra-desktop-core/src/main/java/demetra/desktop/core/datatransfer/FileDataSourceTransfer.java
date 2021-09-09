/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.core.datatransfer;

import demetra.desktop.TsManager;
import demetra.desktop.datatransfer.DataSourceTransferSpi;
import demetra.desktop.datatransfer.DataTransfers;
import demetra.desktop.tsproviders.DataSourceProviderBuddySupport;
import demetra.tsprovider.DataSourceLoader;
import demetra.tsprovider.FileBean;
import demetra.tsprovider.FileLoader;
import ec.util.list.swing.JLists;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;

import javax.swing.*;
import java.awt.datatransfer.Transferable;
import java.beans.BeanInfo;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider
public final class FileDataSourceTransfer implements DataSourceTransferSpi {

    @Override
    public boolean canHandle(Transferable t) {
        Optional<File> file = DataTransfers.getSingleFile(t);
        return file.isPresent() && !getLoaders(file.get()).isEmpty();
    }

    @Override
    public boolean canHandle(Transferable t, String providerName) {
        Optional<File> file = DataTransfers.getSingleFile(t);
        if (file.isPresent()) {
            Optional<FileLoader> loader = TsManager.getDefault().getProvider(FileLoader.class, providerName);
            return loader.isPresent() && loader.get().accept(file.get());
        }
        return false;
    }

    @Override
    public Optional<demetra.tsprovider.DataSource> getDataSource(Transferable t) {
        File file = DataTransfers.getSingleFile(t).get();
        List<FileLoader> loaders = getLoaders(file);
        Optional<FileLoader> loader = chooseLoader(loaders);
        if (loader.isPresent()) {
            FileBean bean = loader.get().newBean();
            bean.setFile(file);
            if (DataSourceProviderBuddySupport.getDefault().getBeanEditor(loader.get().getSource(), "Open data source").editBean(bean, Exceptions::printStackTrace)) {
                return Optional.of(loader.get().encodeBean(bean));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<demetra.tsprovider.DataSource> getDataSource(Transferable t, String providerName) {
        File file = DataTransfers.getSingleFile(t).get();
        FileLoader loader = TsManager.getDefault().getProvider(FileLoader.class, providerName).get();
        FileBean bean = loader.newBean();
        bean.setFile(file);
        if (DataSourceProviderBuddySupport.getDefault().getBeanEditor(loader.getSource(), "Open data source").editBean(bean, Exceptions::printStackTrace)) {
            return Optional.of(loader.encodeBean(bean));
        }
        return Optional.empty();
    }

    public static List<FileLoader> getLoaders(final File file) {
        return TsManager.getDefault().getProviders()
                .filter(FileLoader.class::isInstance)
                .map(FileLoader.class::cast)
                .filter(o -> o.accept(file))
                .collect(Collectors.toList());
    }

    public static <T extends DataSourceLoader> Optional<T> chooseLoader(List<T> loaders) {
        if (loaders.size() == 1) {
            return Optional.of(loaders.get(0));
        }
        JComboBox cb = new JComboBox(loaders.toArray());
        cb.setRenderer(JLists.cellRendererOf(FileDataSourceTransfer::renderLoader));
        DialogDescriptor dd = new DialogDescriptor(cb, "Choose a loader");
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            return Optional.of((T) cb.getSelectedItem());
        }
        return Optional.empty();
    }

    private static void renderLoader(JLabel label, Object value) {
        DataSourceLoader loader = (DataSourceLoader) value;
        label.setText(loader.getDisplayName());
        label.setIcon(DataSourceProviderBuddySupport.getDefault().getIcon(loader.getSource(), BeanInfo.ICON_COLOR_16x16, false));
    }
}
