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
package ec.util.chart.swing;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 * @since 2.1.0
 */
public abstract class JFreeChartWriter {

    @Nonnull
    abstract public String getMediaType();

    abstract public void writeChart(@Nonnull OutputStream stream, @Nonnull JFreeChart chart, @Nonnegative int width, @Nonnegative int height) throws IOException;

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    @ServiceProvider(service = JFreeChartWriter.class)
    public static final class SvgWriter extends JFreeChartWriter {

        @Override
        public String getMediaType() {
            return "image/svg+xml";
        }

        @Override
        public void writeChart(OutputStream stream, JFreeChart chart, int width, int height) throws IOException {
            Charts.writeChartAsSVG(stream, chart, width, height);
        }
    }

    @ServiceProvider(service = JFreeChartWriter.class)
    public static final class SvgzWriter extends JFreeChartWriter {

        @Override
        public String getMediaType() {
            return "image/svg+xml-compressed";
        }

        @Override
        public void writeChart(OutputStream stream, JFreeChart chart, int width, int height) throws IOException {
            try (GZIPOutputStream gzip = new GZIPOutputStream(stream)) {
                Charts.writeChartAsSVG(gzip, chart, width, height);
            }
        }
    }

    @ServiceProvider(service = JFreeChartWriter.class)
    public static final class PngWriter extends JFreeChartWriter {

        @Override
        public String getMediaType() {
            return "image/png";
        }

        @Override
        public void writeChart(OutputStream stream, JFreeChart chart, int width, int height) throws IOException {
            ChartUtilities.writeChartAsPNG(stream, chart, width, height);
        }
    }

    @ServiceProvider(service = JFreeChartWriter.class)
    public static final class JpegWriter extends JFreeChartWriter {

        @Override
        public String getMediaType() {
            return "image/jpeg";
        }

        @Override
        public void writeChart(OutputStream stream, JFreeChart chart, int width, int height) throws IOException {
            ChartUtilities.writeChartAsJPEG(stream, chart, width, height);
        }
    }
    //</editor-fold>
}
