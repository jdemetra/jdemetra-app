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
package ec.util.desktop.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.Nonnull;

/**
 *
 * @author Philippe Charles
 */
final class Processes {

    private Processes() {
        // static class
    }

    @Nonnull
    public static Process noOp() {
        return NoOpProcess.INSTANCE;
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    private static final class NoOpProcess extends Process {

        public static final NoOpProcess INSTANCE = new NoOpProcess();

        @Override
        public OutputStream getOutputStream() {
            return NullOutputStream.INSTANCE;
        }

        @Override
        public InputStream getInputStream() {
            return NullInputStream.INSTANCE;
        }

        @Override
        public InputStream getErrorStream() {
            return NullInputStream.INSTANCE;
        }

        @Override
        public int waitFor() throws InterruptedException {
            return 0;
        }

        @Override
        public int exitValue() {
            return 0;
        }

        @Override
        public void destroy() {
        }
    }

    private static final class NullInputStream extends InputStream {

        static final NullInputStream INSTANCE = new NullInputStream();

        @Override
        public int read() {
            return -1;
        }

        @Override
        public int available() {
            return 0;
        }
    }

    private static final class NullOutputStream extends OutputStream {

        static final NullOutputStream INSTANCE = new NullOutputStream();

        @Override
        public void write(int b) throws IOException {
            throw new IOException("Stream closed");
        }
    }
    //</editor-fold>
}
