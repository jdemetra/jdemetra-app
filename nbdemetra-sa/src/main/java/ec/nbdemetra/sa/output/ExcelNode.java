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
package ec.nbdemetra.sa.output;

import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.tss.sa.ISaOutputFactory;
import ec.tss.sa.output.SpreadsheetOutputConfiguration;
import ec.tss.sa.output.SpreadsheetOutputConfiguration.SpreadsheetLayout;
import ec.tss.sa.output.SpreadsheetOutputFactory;
import java.util.List;
import org.openide.nodes.Sheet;

/**
 * @deprecated 
 * @author Jean Palate
 */
@Deprecated
public class ExcelNode extends AbstractOutputNode<SpreadsheetOutputConfiguration> {

    public ExcelNode() {
        super(new SpreadsheetOutputConfiguration());
        setDisplayName(SpreadsheetOutputFactory.NAME);
    }

    public ExcelNode(SpreadsheetOutputConfiguration config) {
        super(config);
        setDisplayName(SpreadsheetOutputFactory.NAME);
    }

    @Override
    protected Sheet createSheet() {
        SpreadsheetOutputConfiguration config = getLookup().lookup(SpreadsheetOutputConfiguration.class);

        Sheet sheet = super.createSheet();

        NodePropertySetBuilder builder = new NodePropertySetBuilder();
        builder.reset("Location");
        builder.withFile().select(config, "Folder").directories(true).description("Base output folder. Will be extended by the workspace and processing names").add();
        builder.with(String.class).select(config, "fileName").display("File Name").add();
        sheet.put(builder.build());

        builder.reset("Layout");
        builder.withEnum(SpreadsheetLayout.class).select(config, "Layout").add();
        builder.withBoolean().select(config, "VerticalOrientation").add();
        builder.withBoolean().select(config, "FullName").display("Full series name")
                .description("If true, the fully qualified name of the series will be used (workbook + sheet + name). "
                        + "If false, only the name of the series will be displayed.").add();
        sheet.put(builder.build());

        builder.reset("Content");
        builder.with(List.class).select(config, "Series").editor(Series.class).add();
        sheet.put(builder.build());
        return sheet;
    }

    @Override
    public ISaOutputFactory getFactory() {
        return new SpreadsheetOutputFactory(getLookup().lookup(SpreadsheetOutputConfiguration.class));
    }
}
