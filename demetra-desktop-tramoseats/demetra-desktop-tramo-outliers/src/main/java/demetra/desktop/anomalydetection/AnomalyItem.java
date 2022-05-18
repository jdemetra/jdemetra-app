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
package demetra.desktop.anomalydetection;

import demetra.data.DoubleSeqCursor;
import demetra.timeseries.TsData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import jdplus.regsarima.regular.CheckLast;

/**
 * POJO Object corresponding to items in JTsCheckLastList Contains Ts
 * information, processed values of Check Last (relative and absolute errors),
 * specification used, ...
 *
 * @author Mats Maggi
 */
public class AnomalyItem {

    private static final AtomicInteger ID = new AtomicInteger(0);

    public enum Status {

        Unprocessed("Unprocessed"),
        Pending("Pending"),
        Processed("Processed"),
        Invalid("Invalid"),
        NotProcessable("NotProcessable");

        Status(String name) {

        }

        public boolean isProcessed() {
            return this == Processed || this == NotProcessable;
        }

        public boolean isInvalid() {
            return this == Invalid;
        }

        public boolean isNotProcessable() {
            return this == NotProcessable;
        }
    }

    private final String name;
    private final Integer id;
    private final TsData data;
    private int backCount;
    private double[] absoluteError, relativeError;
    private Status status = Status.Unprocessed;

    public AnomalyItem(String name, TsData s, int backCount) {
        id = ID.getAndIncrement();
        this.name = name;
        this.data = s;
        this.backCount = backCount;
    }

    public Integer getId() {
        return id;
    }

    public int getBackCount() {
        return backCount;
    }

    public void reset(int backCount) {
        synchronized (id) {
            this.backCount = backCount;
            absoluteError = null;
            relativeError = null;
            status = Status.Unprocessed;
        }
    }

    public double[] getAbsoluteError() {
        return absoluteError;
    }

    public double[] getRelativeError() {
        return relativeError;
    }

    public void clearValues() {
        absoluteError = null;
        relativeError = null;
        status = Status.Unprocessed;
    }

    public TsData getData() {
        return data;
    }

    public Status getStatus() {
        synchronized (id) {
            return status;
        }
    }

    public boolean isProcessed() {
        synchronized (id) {
            return status.isProcessed();
        }
    }

    public boolean isInvalid() {
        synchronized (id) {
            return status.isInvalid();
        }
    }

    public boolean isNotProcessable() {
        synchronized (id) {
            return status.isNotProcessable();
        }
    }

    public double getAbsoluteError(int index) {
        if (absoluteError == null)
            return Double.NaN;
        if (index < 0 || index > backCount - 1) {
            throw new IllegalArgumentException("Given index for absolute error is incorrect");
        }
        return absoluteError[index];
    }

    public double getRelativeError(int index) {
        if (relativeError == null)
            return Double.NaN;
        if (index < 0 || index > backCount - 1) {
            throw new IllegalArgumentException("Given index for relative error is incorrect");
        }
        return relativeError[index];
    }

    public void process(CheckLast check) {
        synchronized (id) {
            if (status == Status.Pending) {
                return;
            }
            status = Status.Pending;
        }

        if (data.length() > 0) {
            boolean ok = check.check(data);
            if (ok) {
                relativeError = new double[backCount];
                absoluteError = new double[backCount];
                DoubleSeqCursor acursor = check.getAbsoluteErrors().cursor();
                for (int i = 0; i < check.getBackCount(); i++) {
                    relativeError[i] = check.getRelativeError(i);
                    absoluteError[i] = acursor.getAndNext();
                }

                status = Status.Processed;
            } else {
                status = Status.NotProcessable;
            }
        } else {
            status = Status.Invalid;
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public static Collection createBeanCollection() {
        return new ArrayList<>();
    }
}
