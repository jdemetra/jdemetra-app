/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.tss.datatransfer.impl;

import ec.tss.datatransfer.DataSourceTransferHandler;
import ec.tss.datatransfer.DataTransfers;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.utils.Parsers;
import java.awt.datatransfer.Transferable;
import java.util.Optional;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
public abstract class ParserTransferHandler extends DataSourceTransferHandler {

    abstract protected Parsers.Parser<DataSource> getParser();

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
        return !TssTransferSupport.getDefault().isTssTransferable(t) ? DataTransfers.tryParse(t, getParser()) : Optional.empty();
    }

    @Override
    public Optional<DataSource> getDataSource(Transferable t, String providerName) {
        Optional<DataSource> result = getDataSource(t);
        return result.isPresent() && result.get().getProviderName().equals(providerName) ? result : Optional.<DataSource>empty();
    }

    @ServiceProvider(service = DataSourceTransferHandler.class)
    public static class XmlParserHandler extends ParserTransferHandler {

        @Override
        protected Parsers.Parser<DataSource> getParser() {
            return DataSource.xmlParser();
        }
    }

    @ServiceProvider(service = DataSourceTransferHandler.class)
    public static class UriParserHandler extends ParserTransferHandler {

        @Override
        protected Parsers.Parser<DataSource> getParser() {
            return DataSource.uriParser();
        }
    }
}
