/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.tss.datatransfer.impl;

import demetra.ui.TsManager;
import demetra.ui.datatransfer.DataTransfers;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import ec.nbdemetra.ui.tsproviders.actions.OpenProvidersAction;
import ec.tss.datatransfer.DataSourceTransferHandler;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IFileBean;
import ec.tss.tsproviders.IFileLoader;
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
            Optional<IFileLoader> loader = TsManager.getDefault().lookup(IFileLoader.class, providerName);
            return loader.isPresent() ? loader.get().accept(file.get()) : false;
        }
        return false;
    }

    @Override
    public Optional<DataSource> getDataSource(Transferable t) {
        File file = DataTransfers.getSingleFile(t).get();
        List<IFileLoader> loaders = OpenProvidersAction.getLoaders(file);
        Optional<IFileLoader> loader = OpenProvidersAction.chooseLoader(loaders);
        if (loader.isPresent()) {
            IFileBean bean = loader.get().newBean();
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
    public Optional<DataSource> getDataSource(Transferable t, String providerName) {
        File file = DataTransfers.getSingleFile(t).get();
        IFileLoader loader = TsManager.getDefault().lookup(IFileLoader.class, providerName).get();
        IFileBean bean = loader.newBean();
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
