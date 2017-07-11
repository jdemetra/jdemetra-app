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
package ec.util.completion.swing;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.Timer;

/**
 *
 * @author Philippe Charles
 */
@SuppressWarnings("serial")
public class FileListCellRenderer extends CustomListCellRenderer<File> {

    private final Icon defaultIcon;
    private final FailSafeFactory iconFactory;
    private final File[] paths;

    public FileListCellRenderer(@Nonnull ExecutorService executor) {
        this(null, executor, new File[0]);
    }

    public FileListCellRenderer(@Nullable Icon defaultIcon, @Nonnull ExecutorService executor, @Nonnull File[] paths) {
        super(false);
        this.defaultIcon = defaultIcon;
        FailSafeFactory wellTimed = new WellTimedFactory(executor, 20, TimeUnit.MILLISECONDS);
        FailSafeFactory skipping = new SkippingFactory(wellTimed, 40, TimeUnit.MILLISECONDS);
        this.iconFactory = new DejaVuFactory(skipping, new DefaultFilter(30, TimeUnit.SECONDS));
        this.paths = paths;
    }

    @Override
    protected Icon toIcon(String term, JList list, File value, int index, boolean isSelected, boolean cellHasFocus) {
        File file = (File) value;
        if (!file.isAbsolute()) {
            for (File path : paths) {
                File tmp = new File(path, file.getPath());
                if (tmp.exists()) {
                    setToolTipText(tmp.getPath());
                    return iconFactory.create(new IconTask(tmp), defaultIcon);
                }
            }
        }
        setToolTipText(file.getPath());
        return iconFactory.create(new IconTask(file), defaultIcon);
    }

    interface FailSafeFactory {

        <V> V create(Callable<V> task, V defaultValue);
    }

    static class IconTask implements Callable<Icon> {

        static final JFileChooser FILE_CHOOSER = new JFileChooser();
        final File file;

        public IconTask(File file) {
            this.file = file;
        }

        @Override
        public Icon call() {
            return FILE_CHOOSER.getIcon(file);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof IconTask ? equals((IconTask) obj) : false;
        }

        protected boolean equals(IconTask other) {
            return this.file.equals(other.file);
        }

        @Override
        public int hashCode() {
            return file.hashCode();
        }
    }

    static class WellTimedFactory implements FailSafeFactory {

        final ExecutorService executor;
        final long timeout;
        final TimeUnit unit;

        public WellTimedFactory(ExecutorService executor, long timeout, TimeUnit unit) {
            this.executor = executor;
            this.timeout = timeout;
            this.unit = unit;
        }

        @Override
        public <V> V create(Callable<V> task, V defaultValue) {
            try {
                return executor.submit(task).get(timeout, unit);
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                return defaultValue;
            }
        }
    }

    static class SkippingFactory implements FailSafeFactory {

        final FailSafeFactory factory;
        final long delay;
        final TimeUnit unit;
        long latestErrorTime;

        public SkippingFactory(FailSafeFactory factory, long delay, TimeUnit unit) {
            this.factory = factory;
            this.delay = delay;
            this.unit = unit;
            this.latestErrorTime = 0;
        }

        @Override
        public <V> V create(Callable<V> task, V defaultValue) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - latestErrorTime < unit.convert(delay, TimeUnit.MILLISECONDS)) {
                return defaultValue;
            }
            V result = factory.create(task, defaultValue);
            if (result == defaultValue) {
                latestErrorTime = currentTime;
            }
            return result;
        }
    }

    static class DejaVuFactory implements FailSafeFactory {

        final FailSafeFactory factory;
        final Set<Callable<?>> filter;

        public DejaVuFactory(FailSafeFactory factory, Set<Callable<?>> filter) {
            this.factory = factory;
            this.filter = filter;
        }

        @Override
        public <V> V create(Callable<V> task, V defaultValue) {
            if (filter.contains(task)) {
                try {
                    return task.call();
                } catch (Exception ex) {
                    return defaultValue;
                }
            }
            V result = factory.create(task, defaultValue);
            if (result != defaultValue) {
                filter.add(task);
            }
            return result;
        }
    }

    static class DefaultFilter<Object> extends HashSet<Callable<Object>> {

        final Timer evictor;

        DefaultFilter(long delay, TimeUnit unit) {
            this.evictor = new Timer((int) TimeUnit.MILLISECONDS.convert(delay, unit), evt -> clear());
            evictor.setRepeats(false);
        }

        @Override
        public boolean add(Callable<Object> e) {
            evictor.restart();
            return super.add(e);
        }
    }
}
