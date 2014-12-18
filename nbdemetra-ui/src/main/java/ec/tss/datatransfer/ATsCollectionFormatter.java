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
package ec.tss.datatransfer;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.ns.AbstractNamedService;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.tss.TsCollection;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author Philippe Charles
 * @deprecated use {@link TssTransferHandler} instead
 */
@Deprecated
public abstract class ATsCollectionFormatter extends AbstractNamedService implements ITsCollectionFormatter {

    protected ATsCollectionFormatter(String name) {
        super(ITsCollectionFormatter.class, name);
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.icon2Image(DemetraUiIcon.CLIPBOARD_PASTE_DOCUMENT_TEXT_16);
    }

    @Override
    public Sheet createSheet() {
        Sheet result = super.createSheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();
        b.with(String.class).select("DataFlavor", getDataFlavor().getMimeType()).display("Data Flavor").add();
        result.put(b.build());
        return result;
    }

    static ImmutableList<TssTransferHandler> getLegacyHandlers() {
        return LEGACY_HANDLERS;
    }

    private static class TssTransferHandlerAdapter extends TssTransferHandler {

        protected final ITsCollectionFormatter formatter;

        public TssTransferHandlerAdapter(ITsCollectionFormatter formatter) {
            this.formatter = formatter;
        }

        @Override
        public DataFlavor getDataFlavor() {
            return formatter.getDataFlavor();
        }

        @Override
        public boolean canExportTsCollection(TsCollection col) {
            return formatter.canExportTransferData(col);
        }

        @Override
        public Object exportTsCollection(TsCollection col) throws IOException {
            return formatter.toTransferData(col);
        }

        @Override
        public boolean canImportTsCollection(Object obj) {
            return formatter.canImportTransferData(obj);
        }

        @Override
        public TsCollection importTsCollection(Object obj) throws IOException, ClassCastException {
            return formatter.fromTransferData(obj);
        }

        @Override
        public String getName() {
            return formatter.getName();
        }

        @Override
        public String getDisplayName() {
            return formatter.getDisplayName();
        }

        @Override
        public Image getIcon(int type, boolean opened) {
            return formatter.getIcon(type, opened);
        }

        @Override
        public Sheet createSheet() {
            return formatter.createSheet();
        }
    }

    private static class TssTransferHandlerAdapter2 extends TssTransferHandlerAdapter implements IConfigurable {

        public TssTransferHandlerAdapter2(ITsCollectionFormatter formatter) {
            super(formatter);
            Preconditions.checkArgument(formatter instanceof IConfigurable);
        }

        @Override
        public Config getConfig() {
            return ((IConfigurable) formatter).getConfig();
        }

        @Override
        public void setConfig(Config config) throws IllegalArgumentException {
            ((IConfigurable) formatter).setConfig(config);
        }

        @Override
        public Config editConfig(Config config) throws IllegalArgumentException {
            return ((IConfigurable) formatter).editConfig(config);
        }
    }
    //
    private static final Function<ITsCollectionFormatter, TssTransferHandler> TO_HANDLER = new Function<ITsCollectionFormatter, TssTransferHandler>() {
        @Override
        public TssTransferHandler apply(ITsCollectionFormatter input) {
            return input instanceof IConfigurable ? new TssTransferHandlerAdapter2(input) : new TssTransferHandlerAdapter(input);
        }
    };
    private static final ImmutableList<TssTransferHandler> LEGACY_HANDLERS = FluentIterable.from(Lookup.getDefault().lookupAll(ITsCollectionFormatter.class)).transform(TO_HANDLER).toList();
}
