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
package ec.util.desktop.impl;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.util.Factory;
import com.sun.jna.platform.win32.COM.util.IConnectionPoint;
import com.sun.jna.platform.win32.COM.util.IUnknown;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;

/**
 * http://en.wikipedia.org/wiki/Windows_Search
 *
 * @author Philippe Charles
 */
abstract class WinSearch {

    @Nonnull
    abstract public File[] search(@Nonnull String query) throws IOException;

    @Nonnull
    public static WinSearch noOp() {
        return NoOpSearch.INSTANCE;
    }

    @Nonnull
    public static WinSearch getDefault() {
        return LazyHolder.INSTANCE;
    }

    @Nonnull
    static WinSearch failing() {
        return FailingSearch.INSTANCE;
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    /**
     * http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
     */
    private static final class LazyHolder {

        private static final WinSearch INSTANCE = createInstance();

        private static WinSearch createInstance() {
            try {
                Class.forName("com.sun.jna.platform.win32.COM.util.Factory");
                return new JnaSearch();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(WinSearch.class.getName()).log(Level.SEVERE, "Cannot load JNA Platform");
                // fallback
                return noOp();
            }
        }
    }

    private static final class NoOpSearch extends WinSearch {

        private static final WinSearch INSTANCE = new NoOpSearch();

        @Override
        public File[] search(String query) throws IOException {
            return new File[0];
        }
    }

    private static final class FailingSearch extends WinSearch {

        public static final FailingSearch INSTANCE = new FailingSearch();

        @Override
        public File[] search(String query) throws IOException {
            throw new IOException();
        }
    }

    private static final class JnaSearch extends WinSearch {

        private final Factory factory = new Factory();

        @Override
        public File[] search(String query) throws IOException {
            List<File> result = new ArrayList<>();
            Connection conn = null;
            Recordset rs = null;
            try {
                conn = factory.createObject(Connection.class);
                conn.Open("Provider=Search.CollatorDSO;Extended Properties='Application=Windows';");
                rs = conn.Execute("SELECT System.ItemUrl FROM SYSTEMINDEX WHERE System.FileName like '%" + escapeQuery(query) + "%'");
                if (!(rs.getBOF() && rs.getEOF())) {
                    rs.MoveFirst();
                    while (!rs.getEOF()) {
                        result.add(new File(rs.getFields().getItem(0).getValue().toString().replace("file:", "")));
                        rs.MoveNext();
                    }
                }
            } catch (COMException ex) {
                throw new IOException(ex);
            } finally {
                if (rs != null) {
                    rs.Close();
                }
                if (conn != null) {
                    conn.Close();
                }
            }
            return result.toArray(new File[result.size()]);
        }

        private static String escapeQuery(String query) {
            return query.replace("'", "");
        }

        @ComObject(progId = "ADODB.Connection")
        private interface Connection extends IUnknown, IConnectionPoint {

            @ComMethod
            void Open(String connectionString);

            @ComMethod
            void Close();

            @ComMethod
            Recordset Execute(String commandText);
        }

        @ComInterface
        private interface Recordset {

            @ComMethod
            void Close();

            @ComProperty
            boolean getBOF();

            @ComProperty
            boolean getEOF();

            @ComMethod
            void MoveFirst();

            @ComMethod
            void MoveNext();

            @ComProperty
            Fields getFields();
        }

        @ComInterface
        private interface Fields {

            @ComProperty
            Field getItem(int index);
        }

        @ComInterface
        private interface Field {

            @ComProperty
            Object getValue();
        }
    }
    //</editor-fold>
}
