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
package ec.nbdemetra.sa.revisionanalysis;

import demetra.ui.TsManager;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.sa.revisions.RevisionAnalysisDocument;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.algorithm.CompositeResults.Node;
import ec.tstoolkit.algorithm.IProcResults;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.DefaultInformationExtractor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.InformationExtractor;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.ComposedProcDocumentItemFactory;
import ec.ui.view.tsprocessing.DefaultItemUI;
import ec.ui.view.tsprocessing.DocumentInformationExtractor;
import ec.ui.view.tsprocessing.IProcDocumentView;
import ec.ui.view.tsprocessing.IProcDocumentViewFactory;
import ec.ui.view.tsprocessing.ItemUI;
import ec.ui.view.tsprocessing.ProcDocumentItemFactory;
import ec.ui.view.tsprocessing.ProcDocumentViewFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JComponent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mats Maggi
 */
public class RevisionAnalysisViewFactory extends ProcDocumentViewFactory<RevisionAnalysisDocument> {

    public static final String SA = "Sa series", REVISIONS = "Revisions";
    public static final String BY_COMPONENT = "By component", DATA = "Data", CHART = "Chart";
    public static final LinearId ALL_SA = new LinearId(SA);
    public static final LinearId DATA_BY_COMPONENT = new LinearId(REVISIONS, BY_COMPONENT, DATA);
    public static final LinearId CHART_BY_COMPONENT = new LinearId(REVISIONS, BY_COMPONENT, CHART);

    private static final IProcDocumentViewFactory<RevisionAnalysisDocument> INSTANCE = new RevisionAnalysisViewFactory();

    public static IProcDocumentViewFactory<RevisionAnalysisDocument> getDefault() {
        return INSTANCE;
    }

    public RevisionAnalysisViewFactory() {
        registerFromLookup(RevisionAnalysisDocument.class);
    }

    @Override
    public Id getPreferredView() {
        return ALL_SA;
    }

    //<editor-fold defaultstate="collapsed" desc="REGISTER ALL SA">
    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 1000)
    public static class AllSaFactory extends ItemFactory<Map<Ts, TsCollection>> {

        public AllSaFactory() {
            super(ALL_SA, AllSaExtractor.INSTANCE, new DefaultItemUI<IProcDocumentView<RevisionAnalysisDocument>, Map<Ts, TsCollection>>() {
                @Override
                public JComponent getView(IProcDocumentView<RevisionAnalysisDocument> host, Map<Ts, TsCollection> information) {
                    return new JTsComboGrid(information);
                }

            });
        }
    }
    //</editor-fold>

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 2110)
    public static class ByComponentDataFactory extends ItemFactory<IProcResults> {

        public ByComponentDataFactory() {
            super(DATA_BY_COMPONENT, ResultExtractor.INSTANCE, new DefaultItemUI<IProcDocumentView<RevisionAnalysisDocument>, IProcResults>() {
                @Override
                public JComponent getView(IProcDocumentView<RevisionAnalysisDocument> host, IProcResults information) {
                    return new RevisionAnalysisJGrid(information);
                }

            });
        }
    }

    @ServiceProvider(service = ProcDocumentItemFactory.class, position = 2120)
    public static class ByComponentChartFactory extends ItemFactory<IProcResults> {

        public ByComponentChartFactory() {
            super(CHART_BY_COMPONENT, ResultExtractor.INSTANCE, new DefaultItemUI<IProcDocumentView<RevisionAnalysisDocument>, IProcResults>() {
                @Override
                public JComponent getView(IProcDocumentView<RevisionAnalysisDocument> host, IProcResults information) {
                    return new RevisionAnalysisChart(information);
                }

            });
        }
    }

    //<editor-fold defaultstate="collapsed" desc="EXTRACTORS">
    public static class AllSaExtractor extends DocumentInformationExtractor<RevisionAnalysisDocument, Map<Ts, TsCollection>> {

        public static final AllSaExtractor INSTANCE = new AllSaExtractor();

        @Override
        protected Map<Ts, TsCollection> buildInfo(RevisionAnalysisDocument source) {
            Map<Ts, TsCollection> all = new LinkedHashMap<>();
            
            if (source.getInput() == null || source.getInput().isEmpty()) {
                return null;
            }

            CompositeResults rslt = source.getResults();
            Node n = rslt.getNode("batch");
            if (n != null) {
                if (n.results == null || n.results.getDictionary() == null) {
                    return null;
                }
                Map<String, Class> dictionary = n.results.getDictionary();
                Integer curIndex = 0;
                TsCollection col = TsManager.getDefault().newTsCollectionWithName(source.getInput().get(curIndex).getName());
                for (String s : dictionary.keySet()) {
                    String[] splitted = s.split("\\.");
                    if (s.endsWith(".sa")) {
                        TsData tsData = n.results.getData(s, TsData.class);
                        String name = splitted[0].replaceAll("series", "");
                        Integer sIndex = Integer.parseInt(name);
                        if (sIndex != curIndex) {
                            all.put(source.getInput().get(curIndex), col);
                            curIndex = sIndex;
                            col = TsManager.getDefault().newTsCollectionWithName(source.getInput().get(curIndex).getName());
                        }
                        col.quietAdd(TsManager.getDefault().newTs(splitted[1], null, tsData));
                    }
                }
                all.put(source.getInput().get(curIndex), col);
            }

            return all;
        }
    }

    private static class ResultExtractor extends DefaultInformationExtractor<RevisionAnalysisDocument, IProcResults> {

        public static final ResultExtractor INSTANCE = new ResultExtractor();

        @Override
        public IProcResults retrieve(RevisionAnalysisDocument source) {
            if (source.getInput() == null || source.getInput().isEmpty()) {
                return null;
            }
            return source.getResults();
        }
    };
    //</editor-fold>

    private static class ItemFactory<I> extends ComposedProcDocumentItemFactory<RevisionAnalysisDocument, I> {

        public ItemFactory(Id itemId, InformationExtractor<? super RevisionAnalysisDocument, I> informationExtractor, ItemUI<? extends IProcDocumentView<RevisionAnalysisDocument>, I> itemUI) {
            super(RevisionAnalysisDocument.class, itemId, informationExtractor, itemUI);
        }
    }
}
