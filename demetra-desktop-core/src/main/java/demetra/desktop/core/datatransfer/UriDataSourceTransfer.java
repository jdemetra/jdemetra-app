/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.core.datatransfer;

import demetra.desktop.datatransfer.DataSourceTransferSpi;
import demetra.desktop.datatransfer.DataTransfer;
import demetra.desktop.datatransfer.DataTransfers;
import demetra.tsprovider.DataSource;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;

import java.awt.datatransfer.Transferable;
import java.util.Optional;

/**
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider
public final class UriDataSourceTransfer implements DataSourceTransferSpi {

    @Override
    public boolean canHandle(Transferable t) {
        return getDataSource(t).isPresent();
    }

    @Override
    public boolean canHandle(Transferable t, String providerName) {
        Optional<DataSource> dataSource = getDataSource(t);
        return dataSource.isPresent() && dataSource.get().getProviderName().equals(providerName);
    }

    @Override
    public Optional<DataSource> getDataSource(Transferable t) {
        return !DataTransfer.getDefault().isTssTransferable(t) ? DataTransfers.tryParse(t, DataSource::parse) : Optional.empty();
    }

    @Override
    public Optional<DataSource> getDataSource(Transferable t, String providerName) {
        Optional<DataSource> result = getDataSource(t);
        return result.isPresent() && result.get().getProviderName().equals(providerName) ? result : Optional.empty();
    }
}
