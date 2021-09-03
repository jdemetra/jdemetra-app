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
package demetra.desktop.nodes;

import ec.util.various.swing.OnAnyThread;
import ec.util.various.swing.OnEDT;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

import java.util.List;

/**
 *
 * @author Philippe Charles
 */
public abstract class FailSafeChildFactory extends ChildFactory.Detachable<Object> {

    @OnAnyThread
    abstract protected boolean tryCreateKeys(@NonNull List<Object> list) throws Exception;

    @OnEDT
    @Nullable
    protected Node tryCreateNodeForKey(@NonNull Object key) throws Exception {
        throw new AssertionError("Neither tryCreateNodeForKey() nor tryCreateNodesForKey() overridden in " + getClass().getName());
    }

    @OnEDT
    @Nullable
    protected Node[] tryCreateNodesForKey(@NonNull Object key) throws Exception {
        Node n = tryCreateNodeForKey(key);
        return n == null ? null : new Node[]{n};
    }

    @OnEDT
    @Nullable
    protected Node createExceptionNode(@NonNull Exception ex) {
        return new ExceptionNode(ex);
    }

    @OnEDT
    @Nullable
    protected Node[] createExceptionNodes(@NonNull Exception ex) {
        Node n = createExceptionNode(ex);
        return n == null ? null : new Node[]{n};
    }

    @OnEDT
    @Nullable
    @Override
    protected Node createWaitNode() {
        return super.createWaitNode();
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    @Override
    final protected boolean createKeys(List<Object> list) {
        try {
            return tryCreateKeys(list);
        } catch (Exception ex) {
            list.add(new ExceptionKey(ex));
            return true;
        }
    }

    @Override
    final protected Node createNodeForKey(Object key) {
        if (key instanceof ExceptionKey) {
            return createExceptionNode(((ExceptionKey) key).exception);
        }
        try {
            return tryCreateNodeForKey(key);
        } catch (Exception ex) {
            return createExceptionNode(ex);
        }
    }

    @Override
    final protected Node[] createNodesForKey(Object key) {
        if (key instanceof ExceptionKey) {
            return createExceptionNodes(((ExceptionKey) key).exception);
        }
        try {
            return tryCreateNodesForKey(key);
        } catch (Exception ex) {
            return createExceptionNodes(ex);
        }
    }

    private static final class ExceptionKey {

        private final Exception exception;

        public ExceptionKey(Exception exception) {
            this.exception = exception;
        }
    }
    //</editor-fold>
}
