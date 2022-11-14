/*
 * Copyright 2016 National Bank of Belgium
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
package demetra.desktop.sa.diagnostics;

import demetra.desktop.Config;
import demetra.desktop.properties.NodePropertySetBuilder;
import org.openide.nodes.Sheet;
import nbbrd.io.text.BooleanProperty;
import demetra.desktop.Converter;
import jdplus.regarima.diagnostics.ResidualsDiagnosticsConfiguration;
import jdplus.sa.diagnostics.SaResidualsDiagnosticsFactory;
import nbbrd.io.text.DoubleProperty;

/**
 *
 * @author Mats Maggi
 */
public class SaResidualsDiagnosticsBuddy extends AbstractSaDiagnosticsFactoryBuddy<ResidualsDiagnosticsConfiguration, SaResidualsDiagnosticsBuddy.Bean> {

    @lombok.Data
    public static class Bean {

        private boolean active;
        private double badThresholdForNormality, uncertainThresholdForNormality;
        private double severeThresholdForTradingDaysPeak, badThresholdForTradingDaysPeak,
                uncertainThresholdForTradingDaysPeak;
        private double severeThresholdForSeasonalPeaks,
                badThresholdForSeasonalPeaks,
                uncertainThresholdForSeasonalPeaks;

        static Bean of(ResidualsDiagnosticsConfiguration config) {
            Bean bean = new Bean();
            bean.active = config.isActive();
            bean.badThresholdForNormality = config.getBadThresholdForNormality();
            bean.uncertainThresholdForNormality = config.getUncertainThresholdForNormality();
            bean.severeThresholdForSeasonalPeaks = config.getSevereThresholdForSeasonalPeaks();
            bean.badThresholdForSeasonalPeaks = config.getUncertainThresholdForSeasonalPeaks();
            bean.uncertainThresholdForSeasonalPeaks = config.getUncertainThresholdForSeasonalPeaks();
            bean.severeThresholdForTradingDaysPeak = config.getBadThresholdForTradingDaysPeak();
            bean.badThresholdForTradingDaysPeak = config.getBadThresholdForTradingDaysPeak();
            bean.uncertainThresholdForTradingDaysPeak = config.getUncertainThresholdForTradingDaysPeak();
            return bean;
        }

        ResidualsDiagnosticsConfiguration asCore() {
            return ResidualsDiagnosticsConfiguration.builder()
                    .active(active)
                    .badThresholdForNormality(badThresholdForNormality)
                    .uncertainThresholdForNormality(uncertainThresholdForNormality)
                    .severeThresholdForSeasonalPeaks(severeThresholdForSeasonalPeaks)
                    .badThresholdForSeasonalPeaks(badThresholdForSeasonalPeaks)
                    .uncertainThresholdForSeasonalPeaks(uncertainThresholdForSeasonalPeaks)
                    .severeThresholdForTradingDaysPeak(severeThresholdForTradingDaysPeak)
                    .badThresholdForTradingDaysPeak(badThresholdForTradingDaysPeak)
                    .uncertainThresholdForTradingDaysPeak(uncertainThresholdForTradingDaysPeak)
                    .build();
        }
    }

    private static final Converter<Bean, ResidualsDiagnosticsConfiguration> BEANCONVERTER = new BeanConverter();

    protected SaResidualsDiagnosticsBuddy() {
        super(new CoreConverter(), BEANCONVERTER);
    }

    @Override
    public AbstractSaDiagnosticsNode createNode() {
        return new DiagnosticsNode(bean());
    }

    @Override
    public String getName() {
        return SaResidualsDiagnosticsFactory.NAME;
    }

    @Override
    public void reset() {
        setCore(ResidualsDiagnosticsConfiguration.getDefault());
    }

    static final class BeanConverter implements Converter<Bean, ResidualsDiagnosticsConfiguration> {

        @Override
        public ResidualsDiagnosticsConfiguration doForward(Bean a) {
            return a.asCore();
        }

        @Override
        public Bean doBackward(ResidualsDiagnosticsConfiguration b) {
            return Bean.of(b);
        }
    }

    static final class CoreConverter implements Converter<ResidualsDiagnosticsConfiguration, Config> {

        private final BooleanProperty activeParam = BooleanProperty.of("active", ResidualsDiagnosticsConfiguration.ACTIVE);
        private final DoubleProperty nBadParam = DoubleProperty.of("badNormalityThreshold", ResidualsDiagnosticsConfiguration.NBAD);
        private final DoubleProperty nUncertainParam = DoubleProperty.of("uncertainNormalityThreshold", ResidualsDiagnosticsConfiguration.NUNC);
        private final DoubleProperty tdSevereParam = DoubleProperty.of("severeTdThreshold", ResidualsDiagnosticsConfiguration.TDSEV);
        private final DoubleProperty tdBadParam = DoubleProperty.of("badTdThreshold", ResidualsDiagnosticsConfiguration.TDBAD);
        private final DoubleProperty tdUncertainParam = DoubleProperty.of("uncertainTdThreshold", ResidualsDiagnosticsConfiguration.TDUNC);
        private final DoubleProperty sSevereParam = DoubleProperty.of("severeSeasThreshold", ResidualsDiagnosticsConfiguration.SSEV);
        private final DoubleProperty sBadParam = DoubleProperty.of("badSeasThreshold", ResidualsDiagnosticsConfiguration.SBAD);
        private final DoubleProperty sUncertainParam = DoubleProperty.of("uncertainSeasThreshold", ResidualsDiagnosticsConfiguration.SUNC);

        @Override
        public Config doForward(ResidualsDiagnosticsConfiguration a) {
            Config.Builder result = Config.builder("diagnostics", this.getClass().getName(), "");
            activeParam.set(result::parameter, a.isActive());
            nBadParam.set(result::parameter, a.getBadThresholdForNormality());
            nUncertainParam.set(result::parameter, a.getUncertainThresholdForNormality());
            tdSevereParam.set(result::parameter, a.getSevereThresholdForTradingDaysPeak());
            tdBadParam.set(result::parameter, a.getBadThresholdForTradingDaysPeak());
            tdUncertainParam.set(result::parameter, a.getUncertainThresholdForTradingDaysPeak());
            sSevereParam.set(result::parameter, a.getSevereThresholdForSeasonalPeaks());
            sBadParam.set(result::parameter, a.getBadThresholdForSeasonalPeaks());
            sUncertainParam.set(result::parameter, a.getUncertainThresholdForSeasonalPeaks());
            return result.build();
        }

        @Override
        public ResidualsDiagnosticsConfiguration doBackward(Config b) {
            return ResidualsDiagnosticsConfiguration.builder()
                    .active(activeParam.get(b::getParameter))
                    .badThresholdForNormality(nBadParam.get(b::getParameter))
                    .uncertainThresholdForNormality(nUncertainParam.get(b::getParameter))
                    .severeThresholdForTradingDaysPeak(tdSevereParam.get(b::getParameter))
                    .badThresholdForTradingDaysPeak(tdBadParam.get(b::getParameter))
                    .uncertainThresholdForTradingDaysPeak(tdUncertainParam.get(b::getParameter))
                    .severeThresholdForSeasonalPeaks(sSevereParam.get(b::getParameter))
                    .badThresholdForSeasonalPeaks(sBadParam.get(b::getParameter))
                    .uncertainThresholdForSeasonalPeaks(sUncertainParam.get(b::getParameter))
                    .build();
        }
    }

    static class DiagnosticsNode extends AbstractSaDiagnosticsNode<Bean> {

        public DiagnosticsNode(Bean bean) {
            super(bean);
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();

            NodePropertySetBuilder builder = new NodePropertySetBuilder();
            builder.reset("Behaviour");
            builder.withBoolean().select(bean, "active").display("Enabled").add();
            sheet.put(builder.build());
            builder.reset("Normality");
            builder.withDouble()
                    .select(bean, "badThresholdForNormality")
                    .display("Bad")
                    .add();
            builder.withDouble()
                    .select(bean, "uncertainThresholdForNormality")
                    .display("Uncertain")
                    .add();
            sheet.put(builder.build());
            builder.reset("Seasonal");
            builder.withDouble()
                    .select(bean, "severeThresholdForSeasonalPeaks")
                    .display("Severe")
                    .add();
            builder.withDouble()
                    .select(bean, "badThresholdForSeasonalPeaks")
                    .display("Bad")
                    .add();
            builder.withDouble().select(bean, "uncertainThresholdForSeasonalPeaks")
                    .display("Uncertain")
                    .add();
            sheet.put(builder.build());
            builder.reset("Trading days");
            builder.withDouble()
                    .select(bean, "severeThresholdForTradingDaysPeak")
                    .display("Severe")
                    .add();
            builder.withDouble()
                    .select(bean, "badThresholdForTradingDaysPeak")
                    .display("Bad")
                    .add();
            builder.withDouble().select(bean, "uncertainThresholdForTradingDaysPeak")
                    .display("Uncertain")
                    .add();
            sheet.put(builder.build());

            return sheet;
        }
    }

}
