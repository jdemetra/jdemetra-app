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
package be.nbb.tramoseats.io;

import static be.nbb.tramoseats.io.AbstractDecoder.takeCode;
import static be.nbb.tramoseats.io.AbstractDecoder.takePeriod;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.tstoolkit.modelling.ComponentType;
import ec.tstoolkit.modelling.TsVariableDescriptor;
import ec.tstoolkit.modelling.TsVariableDescriptor.UserComponentType;
import ec.tstoolkit.modelling.arima.tramo.OutlierSpec;
import ec.tstoolkit.modelling.arima.tramo.RegressionSpec;
import ec.tstoolkit.modelling.arima.tramo.TradingDaysSpec;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.regression.InterventionVariable;
import ec.tstoolkit.timeseries.regression.OutlierDefinition;
import ec.tstoolkit.timeseries.regression.OutlierType;
import ec.tstoolkit.timeseries.regression.TsVariable;
import ec.tstoolkit.timeseries.regression.TsVariables;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.tstoolkit.utilities.IntList;
import ec.tstoolkit.utilities.NamedObject;
import ec.tstoolkit.utilities.Tokenizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.openide.util.Exceptions;

/**
 *
 * @author Jean Palate
 */
public class LegacyDecoder extends AbstractDecoder {

    private static final String TEMP = "_ts_temp";
    private File path_;
    private TsDomain domain_;
    private final ProcessingContext context;
    private int iter;

    public LegacyDecoder(ProcessingContext context) {
        domain_ = null;
        decoders.add(new OutliersDecoder());
        if (context == null) {
            this.context = ProcessingContext.getActiveContext();
        } else {
            this.context = context;
        }
    }

    public void setFolder(File path) {
        path_ = path;
    }

    public List<Document> decodeMultiDocument(BufferedReader reader) {
        unused.clear();
        elements.clear();
        ArrayList<Document> docs = new ArrayList<>();
        Document doc0 = new Document();
        try {
            if (!readData(reader, doc0)) {
                return null;
            }
        } catch (IOException ex) {
            return null;
        }
        update(doc0.series);
        doc0.spec = new TramoSeatsSpecification();
        if (!readInputSection(reader, doc0.spec)) {
            return null;
        }
        docs.add(doc0);
        if (iter != 0) {
            switch (iter) {
                case 1:
                    while (true) {
                        TramoSeatsSpecification spec = new TramoSeatsSpecification();
                        if (!readInputSection(reader, spec)) {
                            break;
                        }
                        Document doc = new Document();
                        doc.name = doc0.name;
                        doc.series = doc0.series;
                        update(doc0.series);
                        doc.spec = spec;
                        docs.add(doc);
                    }
                    break;
                case 2:
                    ArrayList<NamedObject<TsData>> items = new ArrayList<>();
                    try {
                        readData(reader, items);
                    } catch (IOException ex) {
                        return null;
                    }
                    for (NamedObject<TsData> cur : items) {

                        Document doc = new Document();
                        doc.series = cur.object;
                        doc.name = cur.name;
                        doc.spec = doc0.spec.clone();
                        docs.add(doc);
                    }
                    break;
                case 3:
                    while (true) {
                        Document doc = new Document();
                        try {
                            if (!readData(reader, doc)) {
                                break;
                            }
                        } catch (IOException ex) {
                            return null;
                        }
                        update(doc.series);
                        doc.spec = new TramoSeatsSpecification();
                        if (!readInputSection(reader, doc.spec)) {
                            break;
                        }
                        docs.add(doc);
                    }
                    break;
            }
        }

        return docs;
    }

    @Override
    protected void readSpecificInputSection(TramoSeatsSpecification spec) {
        Number niter = takeInt(Item.ITER, elements);
        if (niter != null) {
            iter = niter.intValue();
        } else {
            iter = 0;
        }
    }

    @Override
    protected void update(TsData s) {
        domain_ = s.getDomain();
    }

    protected void readData(BufferedReader reader, List<NamedObject<TsData>> data) throws IOException {
        do {
            NamedObject<TsData> s = readData(reader);
            if (s == null) {
                break;
            } else {
                data.add(s);
            }
        } while (true);
    }

    @Override
    protected int readRegs(BufferedReader reader, RegressionSpec regression) {
        Number user = takeInt(Item.IUSER, elements);
        try {
            if (user != null) {
                switch (user.intValue()) {
                    case 2:
                        return readOutliers(reader, regression);
                    case 1:
                        return readVariables(reader, regression, false);
                    case -1:
                        return readVariables(reader, regression, true);
                    case -2:
                        return readHolidays(reader, regression);
                }
                return 0;
            }
            return readIntervention(reader, regression);
        } catch (IOException ex) {
            return 0;
        }
    }

    private int readOutliers(BufferedReader reader, RegressionSpec regression) throws IOException {
        Number nser = takeInt(Item.NSER, elements);
        if (nser != null) {
            Tokenizer details = new Tokenizer(reader.readLine());
            for (int i = 0; i < nser.intValue(); ++i) {
                if (!details.hasNextToken()) {
                    return nser.intValue();
                }
                int pos;
                try {
                    pos = Integer.parseInt(details.nextToken());
                } catch (NumberFormatException err) {
                    return 0;
                }
                if (!details.hasNextToken()) {
                    return 0;
                }
                String code = details.nextToken();
                OutlierType ot = OutlierType.valueOf(code.toUpperCase());
                if (ot != null) {
                    TsPeriod p = domain_.get(pos - 1);
                    regression.add(new OutlierDefinition(p, ot, true));
                } else {
                    return 0;
                }
            }
            return nser.intValue();
        } else {
            return 0;
        }
    }

    @Override
    protected String nextInput(BufferedReader reader) {
        return nextItem(LegacyEncoder.INPUT_, reader);
    }

    private void nextLine(BufferedReader reader) throws IOException {
        int ch;
        while ((ch = reader.read()) != -1) {
            char c = (char) ch;
            if (c == '\n') {
                break;
            }
        }
    }

    private String nextItem(String id, BufferedReader reader) {
        StringBuilder builder = new StringBuilder();
        boolean start = false, stop = false;
        while (!stop) {
            try {
                int ch = reader.read();
                if (ch == -1) {
                    break;
                }
                if (ch == '$') {
                    if (!start) {
                        start = true;
                    } else {
                        stop = true;
                        nextLine(reader);
                    }
                } else {
                    char c = (char) ch;
                    if (start) {
                        builder.append(c);
                    }
                }
            } catch (IOException ex) {
                return null;
            }
        }
        String s = builder.toString().trim();
        int pos = s.indexOf(id);
        if (pos >= 0) {
            return s.substring(pos + id.length());
        }
        return null;
    }

    @Override
    protected String nextRegs(BufferedReader reader) {
        return nextItem(LegacyEncoder.REG_, reader);
    }

    private int readVariables(BufferedReader reader, RegressionSpec regression, boolean external) throws IOException {
        Number n = takeInt(Item.ILONG, elements);
        Number nser = takeInt(Item.NSER, elements);
        if (n == null || nser == null) {
            return 0;
        }
        int nvars = nser.intValue();
        String input = reader.readLine();

        ProcessingContext cnt = ec.tstoolkit.algorithm.ProcessingContext.getActiveContext();
        TsVariables vars = cnt.getTsVariables(TEMP);
        if (vars == null) {
            vars = new TsVariables();
            cnt.getTsVariableManagers().set(TEMP, vars);
        }
        String[] names = new String[nvars];
        boolean needreading = false;
        if (external) {
            int ip = input.indexOf('.');
            String ninput = ip < 0 ? input : input.substring(0, ip);
            for (int i = 0; i < nvars; ++i) {
                names[i] = ninput + '_' + i;
                if (!vars.contains(names[i])) {
                    needreading = true;
                }
            }
        } else {
            for (int i = 0; i < nvars; ++i) {
                names[i] = vars.nextName();
            }
            needreading = true;
        }

        if (needreading) {
            Matrix M = external ? readExternalMatrix(n.intValue(), nvars, input)
                    : readMatrix(n.intValue(), nser.intValue(), input);
            if (M != null) {
                for (int i = 0; i < nvars; ++i) {
                    if (!vars.contains(names[i])) {
                        TsVariable cvar = new TsVariable(names[i], new TsData(domain_.getStart(), M.column(i)));
                        vars.set(names[i], cvar);
                    }
                }
            } else {
                return nser.intValue();
            }
        }
        ArrayList<String> cdesc = new ArrayList<>();
        for (int i = 0; i < nvars; ++i) {
            Number regeffect = takeInt(Item.REGEFF, elements);
            if (regeffect != null && regeffect.intValue() == 6) {
                cdesc.add(InformationSet.item(TEMP, names[i]));
            } else {
                TsVariableDescriptor desc = new TsVariableDescriptor();
                desc.setName(InformationSet.item(TEMP, names[i]));
                if (regeffect != null) {
                    desc.setEffect(convert(regeffect.intValue()));
                }
                regression.add(desc);
            }
        }
        if (!cdesc.isEmpty()) {
            TradingDaysSpec td = regression.getCalendar().getTradingDays();
            String[] ntd = new String[cdesc.size()];
            ntd = cdesc.toArray(ntd);
            String[] otd = td.getUserVariables();
            if (otd != null) {
                String[] tmp = Arrays.copyOf(otd, otd.length + ntd.length);
                System.arraycopy(ntd, 0, tmp, otd.length, ntd.length);
                ntd = tmp;
            }
            td.setUserVariables(ntd);
        }
        return nser.intValue();
    }

    private int readHolidays(BufferedReader reader, RegressionSpec regression) throws IOException {
        Number n = takeInt(Item.ILONG, elements);
        Number nser = takeInt(Item.NSER, elements);
        Matrix M = readExternalMatrix(n.intValue(), nser.intValue(), reader.readLine().trim());
        if (M != null) {
            return nser.intValue();
        } else {
            return 0;
        }
    }

    private int readIntervention(BufferedReader reader, RegressionSpec regression) throws IOException {
        Number seq = takeInt(Item.ISEQ, elements);
        if (seq == null) {
            return 0;
        }
        Number delta = takeDouble(Item.DELTA, elements);
        Number deltas = takeDouble(Item.DELTAS, elements);
        Number id1ds = takeInt(Item.ID1DS, elements);

        InterventionVariable var = new InterventionVariable();
        int[] params = nextIntParameters(reader);
        for (int i = 0; i < params.length; i += 2) {
            TsPeriod start = domain_.get(params[i] - 1), end = start.plus(params[i + 1] - 1);
            var.add(start.firstday(), end.lastday());
        }
        if (delta != null) {
            var.setDelta(delta.doubleValue());
        }
        if (deltas != null) {
            var.setDeltaS(deltas.doubleValue());
        }
        if (id1ds != null && id1ds.intValue() == 1) {
            var.setD1DS(true);
        }
        regression.add(var);
        return 1;
    }

    private int[] nextIntParameters(BufferedReader reader) throws IOException {
        String input = reader.readLine();

        Tokenizer tokenizer = new Tokenizer(input);
        String token;

        // Params : lines, year, period, frequency
        final IntList params = new IntList();

        while (tokenizer.hasNextToken()) {
            token = tokenizer.nextToken();
            int i = Integer.parseInt(token);
            params.add(i);
        }
        return params.toArray();
    }

    private Matrix readExternalMatrix(int n, int nser, String file) {
        try {
            File f = path_ == null ? new File(file) : new File(path_, file);
            FileReader reader = new FileReader(f);
            StringBuilder builder = new StringBuilder();
            char[] data = new char[1024];
            int nr;
            do {
                nr = reader.read(data);
                if (nr > 0) {
                    builder.append(data, 0, nr);
                }
            } while (nr > 0);
            Tokenizer tokenizer = new Tokenizer(builder.toString());
            Matrix m = new Matrix(n, nser);
            int r = 0, c = -1;
            while (tokenizer.hasNextToken()) {
                String token = tokenizer.nextToken();
                try {
                    double val = Double.parseDouble(token);
                    if (++c == nser) {
                        c = 0;
                        ++r;
                    }
                    if (r >= n) {
                        break;
                    }
                    m.set(r, c, val);
                } catch (NumberFormatException err) {
                    return null;
                }
            }
            return m;
        } catch (IOException err) {
            return null;
        }
    }

    private Matrix readMatrix(int n, int nser, String buffer) {
        Tokenizer tokenizer = new Tokenizer(buffer);
        Matrix m = new Matrix(n, nser);
        int r = 0, c = -1;
        while (tokenizer.hasNextToken()) {
            String token = tokenizer.nextToken();
            try {
                double val = Double.parseDouble(token);
                if (++c == nser) {
                    c = 0;
                    ++r;
                }
                if (r >= n) {
                    break;
                }
                m.set(r, c, val);
            } catch (NumberFormatException err) {
                return null;
            }
        }
        return m;
    }

    static UserComponentType convert(int intValue) {
        switch (intValue) {
            case 1:
                return UserComponentType.Trend;
            case 2:
                return UserComponentType.Seasonal;
            case 3:
                return UserComponentType.Irregular;
            default:
                return UserComponentType.Undefined;
        }
    }

    /**
     * Handles IATIP, AIO, VA, IMVX,
     *
     * @author Jean Palate
     */
    class OutliersDecoder implements ISpecDecoder {

        OutliersDecoder() {
        }

        @Override
        public boolean process(Map<String, String> dictionary, TramoSeatsSpecification spec) {
            OutlierSpec outliers = spec.getTramoSpecification().getOutliers();
            boolean processed = false;
            Number iatip = takeDouble(Item.IATIP, dictionary);
            Number aio = takeInt(Item.AIO, dictionary);
            if (iatip != null) {
                processed = true;
                if (iatip.intValue() == 0) {
                    outliers.clearTypes();
                } else if (aio != null) {
                    outliers.setAIO(aio.intValue());
                } else {
                    outliers.setAIO(2);
                }
            }
            Number va = takeDouble(Item.VA, dictionary);
            if (va != null) {
                processed = true;
                outliers.setCriticalValue(va.doubleValue());
            }
            Number imvx = takeInt(Item.IMVX, dictionary);
            if (imvx != null) {
                processed = true;
                outliers.setEML(imvx.intValue() == 1);
            }
            if (readSpan(dictionary, outliers)) {
                processed = true;
            }
            return processed;
        }

        private boolean readSpan(Map<String, String> dictionary, OutlierSpec outliers) {
            Number int1 = takeInt(Item.INT1, dictionary);
            Number int2 = takeInt(Item.INT2, dictionary);
            if (int1 == null && int2 == null) {
                return false;
            } else {
                if (domain_ != null) {
                    int i1 = int1 == null ? 0 : int1.intValue() - 1;
                    int i2 = int2 == null ? domain_.getLength() - 1 : int2.intValue() - 1;
                    if (i1 < 0 || i1 >= i2 || i2 >= domain_.getLength()) {
                        TsPeriod start = domain_.get(i1), end = domain_.get(i2);
                        outliers.getSpan().between(start.firstday(), end.lastday());
                    }
                }
                return true;
            }
        }
    }

}
