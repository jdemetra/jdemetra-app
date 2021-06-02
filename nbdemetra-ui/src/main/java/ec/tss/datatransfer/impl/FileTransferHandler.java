/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.tss.datatransfer.impl;

import demetra.tsprovider.FileBean;
import demetra.tsprovider.FileLoader;
import demetra.ui.TsManager;
import demetra.ui.datatransfer.DataTransfers;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import ec.nbdemetra.ui.tsproviders.actions.OpenProvidersAction;
import ec.tss.datatransfer.DataSourceTransferHandler;
import java.awt.datatransfer.Transferable;
import java.beans.IntrospectionException;
import java.io.File;
import java.util.List;
import java.util.Optional;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DataSourceTransferHandler.class)
public class FileTransferHandler extends DataSourceTransferHandler {

    @Override
    public boolean canHandle(Transferable t) {
        Optional<File> file = DataTransfers.getSingleFile(t);
        return file.isPresent() && !OpenProvidersAction.getLoaders(file.get()).isEmpty();
    }

    @Override
    public boolean canHandle(Transferable t, String providerName) {
        Optional<File> file = DataTransfers.getSingleFile(t);
        if (file.isPresent()) {
            Optional<FileLoader> loader = TsManager.getDefault().getProvider(FileLoader.class, providerName);
            return loader.isPresent() ? loader.get().accept(file.get()) : false;
        }
        return false;
    }

    @Override
    public Optional<demetra.tsprovider.DataSource> getDataSource(Transferable t) {
        File file = DataTransfers.getSingleFile(t).get();
        List<FileLoader> loaders = OpenProvidersAction.getLoaders(file);
        Optional<FileLoader> loader = OpenProvidersAction.chooseLoader(loaders);
        if (loader.isPresent()) {
            FileBean bean = loader.get().newBean();
            bean.setFile(file);
            try {
                if (DataSourceProviderBuddySupport.getDefault().get(loader.get()).editBean("Open data source", bean)) {
                    return Optional.of(loader.get().encodeBean(bean));
                }
            } catch (IntrospectionException ex) {
                // TODO: throw exception?
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
        try {
            if (DataSourceProviderBuddySupport.getDefault().get(loader).editBean("Open data source", bean)) {
                return Optional.of(loader.encodeBean(bean));
            }
        } catch (IntrospectionException ex) {
            // TODO: throw exception?
        }
        return Optional.empty();
    }
}
