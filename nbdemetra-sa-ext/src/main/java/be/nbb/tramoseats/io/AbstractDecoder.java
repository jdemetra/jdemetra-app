/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or â€“ as soon they will be approved 
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

import ec.satoolkit.seats.SeatsSpecification;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tstoolkit.Parameter;
import ec.tstoolkit.ParameterType;
import ec.tstoolkit.modelling.DefaultTransformationType;
import ec.tstoolkit.modelling.arima.tramo.ArimaSpec;
import ec.tstoolkit.modelling.arima.tramo.AutoModelSpec;
import ec.tstoolkit.modelling.arima.tramo.CalendarSpec;
import ec.tstoolkit.modelling.arima.tramo.EasterSpec;
import ec.tstoolkit.modelling.arima.tramo.EstimateSpec;
import ec.tstoolkit.modelling.arima.tramo.RegressionSpec;
import ec.tstoolkit.modelling.arima.tramo.TradingDaysSpec;
import ec.tstoolkit.modelling.arima.tramo.TransformSpec;
import ec.tstoolkit.timeseries.calendars.TradingDaysType;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.tstoolkit.utilities.IntList;
import ec.tstoolkit.utilities.NamedObject;
import ec.tstoolkit.utilities.Tokenizer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jean Palate
 */
public abstract class AbstractDecoder implements IDecoder {

    private static final double MISSING = -99999;
    private static final String[] codes;

    static {
        Item[] c = Item.values();
        codes = new String[c.length];
        for (int i = 0; i < codes.length; ++i) {
            codes[i] = c[i].name();
        }
    }

    protected boolean readData(BufferedReader br, Document doc) throws IOException {
        String currentLine = br.readLine();
        if (currentLine == null) {
            return false;
        }
        doc.name = currentLine.trim();

        /* Read second line (parameters)
         * 1) Number of lines of data
         * 2) Year
         * 3) Period
         * 4) Frequency
         */
        currentLine = br.readLine();
        if (currentLine == null) {
            return false;
        }
        Tokenizer tokenizer = new Tokenizer(currentLine);
        String token;

        // Params : lines, year, period, frequency
        final IntList params = new IntList();

        while (tokenizer.hasNextToken()) {
            token = tokenizer.nextToken();
            int i = Integer.parseInt(token);
            params.add(i);
        }

        // Controlling params
        if (params.size() != 4) {
            return false;
        }
        int nbrObs = params.get(0);
        int period = params.get(2);
        int freq = params.get(3);
        if (freq < 1 || freq > 12 || 12 % freq != 0) {
            return false;
        }
        if (period <= 0 || period > freq) {
            return false;
        }

        List<Number> data = new ArrayList<>();
        while ((currentLine = br.readLine()) != null) {
            tokenizer = new Tokenizer(currentLine);
            while (tokenizer.hasNextToken()) {
                token = tokenizer.nextToken();
                double value = Double.parseDouble(token);
                data.add(value);
            }
            if (data.size() >= nbrObs) {
                break;
            }
        }

        double[] adata = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            double obs = data.get(i).doubleValue();
            adata[i] = (obs == MISSING ? Double.NaN : obs);
        }

        doc.series = new TsData(new TsPeriod(TsFrequency.valueOf(freq), params.get(1), params.get(2) - 1), adata, false);
        return true;
    }

    protected NamedObject<TsData> readData(BufferedReader reader) throws IOException {
        String currentLine = reader.readLine();
        if (currentLine == null) {
            return null;
        }

        String name = currentLine.trim();

        /* Read second line (parameters)
         * 1) Number of lines of data
         * 2) Year
         * 3) Period
         * 4) Frequency
         */
        currentLine = reader.readLine();
        if (currentLine == null) {
            return null;
        }
        Tokenizer tokenizer = new Tokenizer(currentLine);
        String token;

        // Params : lines, year, period, frequency
        final IntList params = new IntList();

        while (tokenizer.hasNextToken()) {
            token = tokenizer.nextToken();
            int i = Integer.parseInt(token);
            params.add(i);
        }

        // Controlling params
        if (params.size() != 4) {
            return null;
        }
        int nbrObs = params.get(0);
        int period = params.get(2);
        int freq = params.get(3);
        if (freq < 1 || freq > 12 || 12 % freq != 0) {
            return null;
        }
        if (period <= 0 || period > freq) {
            return null;
        }

        List<Number> data = new ArrayList<>();
        while ((currentLine = reader.readLine()) != null) {
            tokenizer = new Tokenizer(currentLine);
            while (tokenizer.hasNextToken()) {
                token = tokenizer.nextToken();
                double value = Double.parseDouble(token);
                data.add(value);
            }
            if (data.size() >= nbrObs) {
                break;
            }
        }

        double[] adata = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
            double obs = data.get(i).doubleValue();
            adata[i] = (obs == MISSING ? Double.NaN : obs);
        }

        return new NamedObject<>(name, new TsData(new TsPeriod(TsFrequency.valueOf(freq), params.get(1), params.get(2) - 1), adata, false));
    }

    protected static interface ISpecDecoder {

        boolean process(Map<String, String> dictionary, TramoSeatsSpecification spec);
    }

    public static int decodeInt(String str) throws ParseException {
        NumberFormat integerInstance = NumberFormat.getIntegerInstance(Locale.ROOT);
        Number parse = integerInstance.parse(str);
        return parse.intValue();
    }

    public static double decodeDouble(String str) throws ParseException {
        NumberFormat doubleInstance = NumberFormat.getNumberInstance(Locale.ROOT);
        Number parse = doubleInstance.parse(str);
        return parse.doubleValue();
    }

    public static Number takeInt(Item item, Map<String, String> dic) {
        String key = item.name();
        return takeInt(key, dic);
    }

    public static TsPeriod takePeriod(TsFrequency freq, Item item, int idx, Map<String, String> dic) {
        StringBuilder bkey = new StringBuilder();
        bkey.append(item.name()).append('(').append(idx).append(')');
        String key = bkey.toString();
        return takePeriod(freq, key, dic);
    }

    public static String takeCode(Item item, int idx, Map<String, String> dic) {
        StringBuilder bkey = new StringBuilder();
        bkey.append(item.name()).append('(').append(idx).append(')');
        String key = bkey.toString();
        String code = dic.get(key);
        dic.remove(key);
        return code;
    }

    public static String takeCode(Item item, Map<String, String> dic) {
        String key = item.name();
        String code = dic.get(key);
        dic.remove(key);
        return code;
    }

    public static TsPeriod takePeriod(TsFrequency freq, Item item, Map<String, String> dic) {
        return takePeriod(freq, item.name(), dic);
    }

    public static Number takeDouble(Item item, Map<String, String> dic) {
        String key = item.name();
        return takeDouble(key, dic);
    }

    public static Number takeInt(Item item, int idx, Map<String, String> dic) {
        StringBuilder bkey = new StringBuilder();
        bkey.append(item.name()).append('(').append(idx).append(')');
        String key = bkey.toString();
        return takeInt(key, dic);
    }

    public static Number takeDouble(Item item, int idx, Map<String, String> dic) {
        StringBuilder bkey = new StringBuilder();
        bkey.append(item.name()).append('(').append(idx).append(')');
        String key = bkey.toString();
        return takeDouble(key, dic);
    }

    private static TsPeriod takePeriod(TsFrequency freq, String key, Map<String, String> dic) {
        String str = dic.get(key);
        if (str == null) {
            return null;
        }
        NumberFormat integerInstance = NumberFormat.getIntegerInstance(Locale.ROOT);
        Number year;
        Number period;
        try {
            dic.remove(key);
            year = integerInstance.parse(str.substring(0, 4));
            period = integerInstance.parse(str.substring(5, 7));
            return new TsPeriod(freq, year.intValue(), period.intValue() - 1);
        } catch (ParseException ex) {
            return null;
        }
    }

    private static Number takeInt(String key, Map<String, String> dic) {
        String str = dic.get(key);
        if (str == null) {
            return null;
        }
        NumberFormat integerInstance = NumberFormat.getIntegerInstance(Locale.ROOT);
        Number parse;
        try {
            parse = integerInstance.parse(str);
            dic.remove(key);
            return parse;
        } catch (ParseException ex) {
            return null;
        }
    }

    private static Number takeDouble(String key, Map<String, String> dic) {
        String str = dic.get(key);
        if (str == null) {
            return null;
        }
        NumberFormat doubleInstance = NumberFormat.getNumberInstance(Locale.ROOT);
        Number parse;
        try {
            parse = doubleInstance.parse(str);
            dic.remove(key);
            return parse;
        } catch (ParseException ex) {
            return null;
        }
    }

    protected static class RsaDecoder implements ISpecDecoder {

        @Override
        public boolean process(Map<String, String> dictionary, TramoSeatsSpecification spec) {
            Number n = takeInt(Item.RSA, dictionary);
            if (n != null) {
                switch (n.intValue()) {
                    case 0:
                        copyTo(TramoSeatsSpecification.RSA0, spec);
                        break;
                    case 1:
                        copyTo(TramoSeatsSpecification.RSA1, spec);
                        break;
                    case 2:
                        copyTo(TramoSeatsSpecification.RSA2, spec);
                        break;
                    case 3:
                        copyTo(TramoSeatsSpecification.RSA3, spec);
                        break;
                    default:
                        copyTo(TramoSeatsSpecification.RSAfull, spec);
                        break;
                }
            }
            return false;
        }

        private void copyTo(TramoSeatsSpecification rspec, TramoSeatsSpecification spec) {
            spec.setTramoSpecification(rspec.getTramoSpecification().clone());
            spec.setSeatsSpecification(rspec.getSeatsSpecification().clone());
            spec.setBenchmarkingSpecification(rspec.getBenchmarkingSpecification().clone());
        }
    }

    /**
     * Handles IMEAN, P, D, Q, BP, BD, BQ PHI, JPR, BPHI, JPS, TH, JQR, BTH,
     * JQS, INIT
     *
     * @author Jean Palate
     */
    protected static class ArimaDecoder implements ISpecDecoder {

        @Override
        public boolean process(Map<String, String> dictionary, TramoSeatsSpecification spec) {
            boolean processed = false;
            ArimaSpec arima = spec.getTramoSpecification().getArima();
            Number n = takeInt(Item.P, dictionary);
            if (n != null) {
                arima.setP(n.intValue());
                processed = true;
            }
            n = takeInt(Item.D, dictionary);
            if (n != null) {
                arima.setD(n.intValue());
                processed = true;
            }
            n = takeInt(Item.Q, dictionary);
            if (n != null) {
                arima.setQ(n.intValue());
                processed = true;
            }
            n = takeInt(Item.BP, dictionary);
            if (n != null) {
                arima.setBP(n.intValue());
                processed = true;
            }
            n = takeInt(Item.BD, dictionary);
            if (n != null) {
                arima.setBD(n.intValue());
                processed = true;
            }
            n = takeInt(Item.BQ, dictionary);
            if (n != null) {
                arima.setBQ(n.intValue());
                processed = true;
            }
            n = takeInt(Item.IMEAN, dictionary);
            if (n != null) {
                arima.setMean(0 != n.intValue());
                processed = true;
            }
            n = takeInt(Item.INIT, dictionary);
            if (n != null) {
                switch (n.intValue()) {
                    case 0:
                        arima.clearParameters();
                        break;
                    case 1:
                        arima.setParameterType(ParameterType.Initial);
                        break;
                    case 2:
                        // Fix all coefficients
                        arima.setParameterType(ParameterType.Fixed);
                        break;
                }
                processed = true;
            }
            if (readCoefficients(dictionary, arima)) {
                processed = true;
            }

            return processed;
        }

        private boolean readCoefficients(Map<String, String> dictionary, ArimaSpec arima) {
            boolean processed = false;
            Parameter[] p = readCoefficients(dictionary, arima.getP(), Item.PHI, Item.JPR);
            if (p != null) {
                arima.setPhi(p);
                processed = true;
            }
            p = readCoefficients(dictionary, arima.getQ(), Item.TH, Item.JQR);
            if (p != null) {
                arima.setTheta(p);
                processed = true;
            }
            p = readCoefficients(dictionary, arima.getBP(), Item.BPHI, Item.JPS);
            if (p != null) {
                arima.setBPhi(p);
                processed = true;
            }
            p = readCoefficients(dictionary, arima.getBQ(), Item.BTH, Item.JQS);
            if (p != null) {
                arima.setBTheta(p);
                processed = true;
            }
            return processed;
        }

        private Parameter[] readCoefficients(Map<String, String> dictionary, int n, Item item, Item status) {
            if (n == 0) {
                return null;
            }
            Parameter[] p = new Parameter[n];
            for (int i = 0; i < n; ++i) {
                Parameter c = null;
                Number v = takeDouble(item, i + 1, dictionary);
                Number s = takeInt(status, i + 1, dictionary);
                if (v != null) {
                    c = new Parameter(v.doubleValue(), ParameterType.Estimated);
                    if (s != null && s.intValue() == 1) {
                        c.setType(ParameterType.Fixed);
                    }
                }
                p[i] = c;
            }
            return Parameter.isDefault(p) ? null : p;
        }

    }

    /**
     * Handles IDIF, INIC, CANCEL, PC, PCR, TSIG, UB1, UB2
     *
     * @author Jean Palate
     */
    protected static class AutoModelDecoder implements ISpecDecoder {

        @Override
        public boolean process(Map<String, String> dictionary, TramoSeatsSpecification spec) {
            AutoModelSpec auto = spec.getTramoSpecification().getAutoModel();
            boolean processed = false;
            Number idif = takeInt(Item.IDIF, dictionary);
            Number inic = takeInt(Item.INIC, dictionary);
            if (idif != null && inic != null) {
                processed = true;
                auto.setEnabled(idif.intValue() > 0 && inic.intValue() > 0);
            }
            Number cancel = takeDouble(Item.CANCEL, dictionary);
            if (cancel != null) {
                processed = true;
                auto.setCancel(cancel.doubleValue());
            }
            Number pc = takeDouble(Item.PC, dictionary);
            if (pc != null) {
                processed = true;
                auto.setPc(pc.doubleValue());
            }
            Number pcr = takeDouble(Item.PCR, dictionary);
            if (pcr != null) {
                processed = true;
                auto.setPcr(pcr.doubleValue());
            }
            Number tsig = takeDouble(Item.TSIG, dictionary);
            if (tsig != null) {
                processed = true;
                auto.setTsig(tsig.doubleValue());
            }
            Number ub1 = takeDouble(Item.UB1, dictionary);
            if (ub1 != null) {
                processed = true;
                auto.setUb1(ub1.doubleValue());
            }
            Number ub2 = takeDouble(Item.UB2, dictionary);
            if (ub2 != null) {
                processed = true;
                auto.setUb2(ub2.doubleValue());
            }
            return processed;
        }
    }

    /**
     * Handles LAM, FCT
     *
     * @author Jean Palate
     */
    protected static class TransformDecoder implements ISpecDecoder {

        @Override
        public boolean process(Map<String, String> dictionary, TramoSeatsSpecification spec) {
            TransformSpec transform = spec.getTramoSpecification().getTransform();
            boolean processed = false;
            Number lam = takeInt(Item.LAM, dictionary);
            if (lam != null) {
                processed = true;
                switch (lam.intValue()) {
                    case 0:
                        transform.setFunction(DefaultTransformationType.Log);
                        break;
                    case 1:
                        transform.setFunction(DefaultTransformationType.None);
                        break;
                    case -1:
                        transform.setFunction(DefaultTransformationType.Auto);
                        break;
                }
            }
            Number fct = takeDouble(Item.FCT, dictionary);
            if (fct != null) {
                processed = true;
                transform.setFct(fct.doubleValue());
            }
            return processed;
        }
    }

    /**
     * Handles ITRAD, IEAST, IDUR
     *
     * @author Jean Palate
     */
    protected static class CalendarDecoder implements ISpecDecoder {

        @Override
        public boolean process(Map<String, String> dictionary, TramoSeatsSpecification spec) {
            CalendarSpec calendar = spec.getTramoSpecification().getRegression().getCalendar();
            EasterSpec easter = calendar.getEaster();
            TradingDaysSpec tradingDays = calendar.getTradingDays();
            boolean processed = false;
            Number ntd = takeInt(Item.ITRAD, dictionary);
            Number pftd = takeDouble(Item.pFTD, dictionary);
            if (ntd != null) {
                processed = true;
                
                int itrad = ntd.intValue();
                if (itrad == -2) {
                    tradingDays.setAutomaticMethod(TradingDaysSpec.AutoMethod.FTest);
                    if (pftd != null) {
                        tradingDays.setProbabibilityForFTest(1 - pftd.doubleValue());
                    }
                } else {
                    if (itrad < 0) {
                        tradingDays.setTest(true);
                        itrad = -itrad;
                    }
                    if (itrad == 2 || itrad == 7) {
                        tradingDays.setLeapYear(true);
                        --itrad;
                    }
                    switch (itrad) {
                        case 1:
                            tradingDays.setTradingDaysType(TradingDaysType.WorkingDays);
                            break;
                        case 6:
                            tradingDays.setTradingDaysType(TradingDaysType.TradingDays);
                            break;
                        default:
                            tradingDays.setTradingDaysType(TradingDaysType.None);
                            break;
                    }
                }
            }
            Number nee = takeInt(Item.IEAST, dictionary);
            if (nee != null) {
                processed = true;
                int ieast = nee.intValue();
                if (ieast < 0) {
                    easter.setTest(true);
                    ieast = -ieast;
                }
                switch (ieast) {
                    case 1:
                        easter.setOption(EasterSpec.Type.IncludeEaster); // ?
                        break;
                    case 2:
                        easter.setOption(EasterSpec.Type.IncludeEaster);
                        break;
                    case 3:
                        easter.setOption(EasterSpec.Type.IncludeEasterMonday);
                        break;
                    default:
                        easter.setOption(EasterSpec.Type.Unused);
                        break;
                }
            }
            Number dur = takeInt(Item.IDUR, dictionary);
            if (dur != null) {
                processed = true;
                easter.setDuration(dur.intValue());
            }
            return processed;
        }
    }

    /**
     * Handles RMOD, EPSPHI, XL, NOADMISS
     *
     * @author Jean Palate
     */
    protected static class SeatsDecoder implements ISpecDecoder {

        @Override
        public boolean process(Map<String, String> dictionary, TramoSeatsSpecification spec) {
            SeatsSpecification seats = spec.getSeatsSpecification();
            boolean processed = false;
            Number rmod = takeDouble(Item.RMOD, dictionary);
            if (rmod != null) {
                processed = true;
                seats.setTrendBoundary(rmod.doubleValue());
            }
            Number eps = takeDouble(Item.EPSPHI, dictionary);
            if (eps != null) {
                processed = true;
                seats.setSeasTolerance(eps.doubleValue());
            }
            Number xl = takeDouble(Item.XL, dictionary);
            if (xl != null) {
                processed = true;
                seats.setXlBoundary(xl.doubleValue());
            }
            Number nadmiss = takeInt(Item.NOADMISS, dictionary);
            if (nadmiss != null) {
                processed = true;
                seats.setApproximationMode(nadmiss.intValue() == 0 ? SeatsSpecification.ApproximationMode.None
                        : SeatsSpecification.ApproximationMode.Legacy);
            }

            return processed;
        }
    }

    /**
     * Handles TOL, UBP
     *
     * @author Jean Palate
     */
    protected static class EstimateDecoder implements ISpecDecoder {

        @Override
        public boolean process(Map<String, String> dictionary, TramoSeatsSpecification spec) {
            EstimateSpec estimate = spec.getTramoSpecification().getEstimate();
            boolean processed = false;
            Number tol = takeDouble(Item.TOL, dictionary);
            if (tol != null) {
                processed = true;
                estimate.setTol(tol.doubleValue());
            }
            Number ubp = takeDouble(Item.UBP, dictionary);
            if (ubp != null) {
                processed = true;
                estimate.setUbp(ubp.doubleValue());
            }
            return processed;
        }
    }

    protected final Map<String, String> elements = new HashMap<>();
    protected final Map<String, String> unused = new HashMap<>();
    protected final List<ISpecDecoder> decoders = new ArrayList<>();

    protected AbstractDecoder() {
        decoders.add(new RsaDecoder());
        decoders.add(new TransformDecoder());
        decoders.add(new EstimateDecoder());
        decoders.add(new ArimaDecoder());
        decoders.add(new AutoModelDecoder());
        decoders.add(new CalendarDecoder());
        decoders.add(new SeatsDecoder());

    }

    public Map<String, String> unusedElements() {
        return Collections.unmodifiableMap(unused);
    }

    protected abstract String nextInput(BufferedReader reader);

    protected abstract String nextRegs(BufferedReader reader);

    protected abstract int readRegs(BufferedReader reader, RegressionSpec regspec);

    protected abstract void readSpecificInputSection(TramoSeatsSpecification spec);

    protected boolean readInputSection(BufferedReader reader, TramoSeatsSpecification spec) {
        String input = nextInput(reader);
        if (input == null || !read(input)) {
            return false;
        }
        boolean ok = true;
        int n = elements.size();
        while (ok && n-- > 0) {
            ok = false;
            for (ISpecDecoder decoder : decoders) {
                if (decoder.process(elements, spec)) {
                    ok = true;
                    break;
                }
            }
        }
        readSpecificInputSection(spec);

        Number nregs = takeInt(Item.IREG, elements);
        int iregs = nregs == null ? 0 : nregs.intValue();

        unused.clear();
        unused.putAll(elements);
        String regs = null;
        while (iregs > 0) {
            regs = nextRegs(reader);
            if (regs == null) {
                break;
            }
            if (!read(regs)) {
                return false;
            }

            int nser = readRegs(reader, spec.getTramoSpecification().getRegression());
            if (nser == 0) {
                return false;
            } else {
                iregs -= nser;
            }
        }

        return true;
    }

    protected abstract void update(TsData s);

    @Override
    public Document decodeDocument(BufferedReader reader) {
        clear();
        Document doc = new Document();
        try {
            if (!readData(reader, doc)) {
                return null;
            }
        } catch (IOException ex) {
            return null;
        }
        doc.spec = new TramoSeatsSpecification();
        update(doc.series);
        if (!readInputSection(reader, doc.spec)) {
            return null;
        }

        return doc;

    }

    @Override
    public TramoSeatsSpecification decodeSpec(BufferedReader reader) {
        clear();
        TramoSeatsSpecification spec = new TramoSeatsSpecification();
        if (!readInputSection(reader, spec)) {
            return null;
        }
        return spec;
    }

    protected void clear() {
        unused.clear();
        elements.clear();
    }

    protected boolean read(final String str) {
        elements.clear();
        String input = str.replace(',', ' ');
        Tokenizer tokens = new Tokenizer(input);
        while (tokens.hasNextToken()) {
            String token = tokens.nextToken();
            String[] items = token.split("=");
            if (items.length == 2) {
                elements.put(normalize(items[0]), items[1]);
            } else if (items.length == 1) {
                elements.put(normalize(items[0]), null);
            } else {
                return false;
            }
        }
        return true;
    }

    protected static String normalize(final String s) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (!Character.isWhitespace(c)) {
                b.append(c);
            }
        }
        String c = b.toString();
        for (int i = 0; i < codes.length; ++i) {
            int k = c.indexOf('(');
            String pc = c;
            if (k >= 0) {
                pc = c.substring(0, k);
            }
            if (codes[i].equalsIgnoreCase(pc)) {
                return k < 0 ? codes[i] : codes[i] + c.substring(k);
            }
        }
        return c; // should not append
    }
}
