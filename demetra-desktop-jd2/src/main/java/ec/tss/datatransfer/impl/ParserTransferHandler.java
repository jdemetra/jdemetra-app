/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.tss.datatransfer.impl;

import demetra.bridge.TsConverter;
import demetra.tsprovider.DataSource;
import ec.tss.datatransfer.DataSourceTransferHandler;
import java.awt.datatransfer.Transferable;
import java.util.Optional;
import demetra.desktop.datatransfer.DataTransfer;
import demetra.desktop.datatransfer.DataTransfers;
import nbbrd.io.text.Parser;
import nbbrd.service.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
public abstract class ParserTransferHandler implements DataSourceTransferHandler {

    abstract protected Parser<DataSource> getParser();

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
        return !DataTransfer.getDefault().isTssTransferable(t) ? DataTransfers.tryParse(t, getParser()::parse) : Optional.empty();
    }

    @Override
    public Optional<DataSource> getDataSource(Transferable t, String providerName) {
        Optional<DataSource> result = getDataSource(t);
        return result.isPresent() && result.get().getProviderName().equals(providerName) ? result : Optional.empty();
    }

    @ServiceProvider(DataSourceTransferHandler.class)
    public final static class XmlParserHandler extends ParserTransferHandler {

        @Override
        protected Parser<DataSource> getParser() {
            return ec.tss.tsproviders.DataSource.xmlParser().andThen(TsConverter::toDataSource)::parse;
        }
    }

    @ServiceProvider(DataSourceTransferHandler.class)
    public final static class UriParserHandler extends ParserTransferHandler {

        @Override
        protected Parser<DataSource> getParser() {
            return Parser.of(DataSource::parse);
        }
    }
}
