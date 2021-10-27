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
import demetra.timeseries.Ts;
import demetra.timeseries.TsData;
import java.util.ArrayList;
import java.util.Collection;
import jdplus.regsarima.regular.CheckLast;

/**
 * POJO Object corresponding to items in JTsCheckLastList Contains Ts
 * information, processed values of Check Last (relative and absolute errors),
 * specification used, ...
 *
 * @author Mats Maggi
 */
public class AnomalyItem {

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
    private Integer id_ = 0;
    private Ts ts_;
    private int backCount;
    private Double[] absoluteError, relativeError;
    private Status status_ = Status.Unprocessed;

    public AnomalyItem() {
    }

    public AnomalyItem(Ts s) {
        this(s, 1);
    }

    public AnomalyItem(Ts s, int backCount) {
        ts_ = s;
        this.backCount = backCount;
        absoluteError = new Double[backCount];
        relativeError = new Double[backCount];
    }

    public Integer getId() {
        return id_;
    }

    public void setId(Integer id_) {
        this.id_ = id_;
    }

    public int getBackCount() {
        return backCount;
    }

    public void setBackCount(int backCount) {
        this.backCount = backCount;
        absoluteError = new Double[backCount];
        relativeError = new Double[backCount];
    }

    public Double[] getAbsoluteError() {
        return absoluteError;
    }

    public void setAbsoluteError(Double[] absoluteError) {
        this.absoluteError = absoluteError;
    }

    public Double[] getRelativeError() {
        return relativeError;
    }

    public void setRelativeError(Double[] relativeError) {
        this.relativeError = relativeError;
    }

    public void clearValues() {
        absoluteError = new Double[backCount];
        relativeError = new Double[backCount];
        status_ = Status.Unprocessed;
    }

    public Ts getTs() {
        return ts_;
    }

    public void setTs(Ts ts_) {
        this.ts_ = ts_;
    }

    public TsData getTsData() {
        return ts_.getData();
    }

    public Status getStatus() {
        synchronized (id_) {
            return status_;
        }
    }

    public boolean isProcessed() {
        return status_.isProcessed();
    }

    public boolean isInvalid() {
        return status_.isInvalid();
    }
    
    public boolean isNotProcessable() {
        return status_.isNotProcessable();
    }

    public Double getAbsoluteError(int index) {
        if (index < 0 || index > backCount - 1) {
            throw new IllegalArgumentException("Given index for absolute error is incorrect");
        }
        return absoluteError[index];
    }

    public Double getRelativeError(int index) {
        if (index < 0 || index > backCount - 1) {
            throw new IllegalArgumentException("Given index for relative error is incorrect");
        }
        return relativeError[index];
    }

    public void process(CheckLast check) {
        synchronized (id_) {
            if (status_ == Status.Pending) {
                return;
            }
            status_ = Status.Pending;
        }

        TsData d = ts_.getData();
        if (d.length() > 0) {
            boolean ok = check.check(d);

            if (ok) {
                DoubleSeqCursor acursor = check.getAbsoluteErrors().cursor();
                for (int i = 0; i < check.getBackCount(); i++) {
                    relativeError[i] = check.getRelativeError(i);
                    absoluteError[i] = acursor.getAndNext();
                }

                status_ = Status.Processed;
            } else {
                status_ = Status.NotProcessable;
            }
        } else {
            status_ = Status.Invalid;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (ts_ != null) {
            String item = ts_.getName();
            if (item != null) {
                builder.append(" - ").append(item);
            }
        }
        return builder.toString();
    }
    
    public static Collection createBeanCollection() {
        return new ArrayList<>();
    }
}
