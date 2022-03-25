/*
 * Copyright 2015 National Bank of Belgium
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
package internal.desktop.spreadsheet.datatransfer;

import demetra.timeseries.TsCollection;
import ec.util.spreadsheet.Book;
import ec.util.spreadsheet.helpers.ArraySheet;
import java.io.IOException;
import internal.spreadsheet.grid.SheetGridInput;
import internal.spreadsheet.grid.SheetGridOutput;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

/**
 *
 * @author Philippe Charles
 * @since 2.2.0
 */
@lombok.RequiredArgsConstructor
final class SpreadSheetDataTransferSupport {

    public enum RawDataType {

        BYTES {
            @Override
            public Book toBook(Book.Factory factory, Object input) throws IOException {
                byte[] bytes = (byte[]) input;
                try ( ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
                    return factory.load(stream);
                }
            }

            @Override
            public Object fromBook(Book.Factory factory, Book book) throws IOException {
                try ( ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                    factory.store(stream, book);
                    return stream.toByteArray();
                }
            }

            @Override
            public boolean isInstance(Object obj) {
                return obj instanceof byte[];
            }
        },
        TEXT {
            @Override
            public Book toBook(Book.Factory factory, Object input) throws IOException {
                try ( ByteArrayInputStream stream = new ByteArrayInputStream(((String) input).getBytes())) {
                    return factory.load(stream);
                }
            }

            @Override
            public Object fromBook(Book.Factory factory, Book book) throws IOException {
                try ( ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                    factory.store(stream, book);
                    return new String(stream.toByteArray(), StandardCharsets.UTF_8);
                }
            }

            @Override
            public boolean isInstance(Object obj) {
                return obj instanceof String;
            }
        };

        public abstract Book toBook(Book.Factory factory, Object input) throws IOException;

        public abstract Object fromBook(Book.Factory factory, Book book) throws IOException;

        public abstract boolean isInstance(Object obj);
    }

    @lombok.NonNull
    private final Book.Factory factory;

    @lombok.NonNull
    private final Supplier<SpreadSheetDataTransferBean> options;

    @lombok.NonNull
    private final RawDataType resource;

    public boolean canExportTsCollection(TsCollection col) {
        return options.get().isExportTs();
    }

    public Object exportTsCollection(TsCollection col) throws IOException {
        SheetGridOutput output = SheetGridOutput.of(factory::isSupportedDataType);
        options.get().getTsWriter().write(col, output);
        final ArraySheet result = output.getResult();
        return resource.fromBook(factory, fixSheetName(result).toBook());
    }

    // FIXME: add property for default sheet name
    private ArraySheet fixSheetName(ArraySheet sheet) {
        return sheet.getName().isEmpty() ? sheet.rename("DND") : sheet;
    }
    
    public boolean canImportTsCollection(Object obj) {
        return options.get().isImportTs() && resource.isInstance(obj);
    }

    public TsCollection importTsCollection(Object obj) throws IOException {
        try ( Book book = resource.toBook(factory, obj)) {
            if (book.getSheetCount() > 0) {
                SheetGridInput input = SheetGridInput.of(book.getSheet(0), factory::isSupportedDataType);
                return options.get().getTsReader().read(input);
            }
            return TsCollection.EMPTY;
        }
    }

    public boolean canImportMatrix(Object obj) {
        return false;
    }

    public demetra.math.matrices.Matrix importMatrix(Object obj) throws IOException, ClassCastException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean canExportMatrix(demetra.math.matrices.Matrix matrix) {
        return false;
    }

    public Object exportMatrix(demetra.math.matrices.Matrix matrix) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean canImportTable(Object obj) {
        return false;
    }

    public demetra.util.Table<?> importTable(Object obj) throws IOException, ClassCastException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean canExportTable(demetra.util.Table<?> table) {
        return false;
    }

    public Object exportTable(demetra.util.Table<?> table) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
