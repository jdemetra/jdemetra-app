/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 will be approved by the European Commission - subsequent
 versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 * See the Licence for the specific language governing
 permissions and limitations under the Licence.
 */
package ec.ui.view.tsprocessing.sa;

import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlResidualSeasonalityTest;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.view.tsprocessing.HtmlItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;

/**
 *
 * @author palatej
 */
public class ResidualSeasonalityTestUI <V extends IProcDocumentView<?>> extends HtmlItemUI<V, TsData> {

    @Override
    protected IHtmlElement getHtmlElement(V host, TsData sa) {
        return new HtmlResidualSeasonalityTest(sa);
    }

}
