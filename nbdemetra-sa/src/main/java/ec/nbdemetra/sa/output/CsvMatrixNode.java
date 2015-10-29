/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.output;

import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.tss.sa.ISaOutputFactory;
import ec.tss.sa.output.CsvMatrixOutputConfiguration;
import ec.tss.sa.output.CsvMatrixOutputFactory;
import java.util.List;
import org.openide.nodes.Sheet;

/**
 *
 * @author Jean Palate
 */
public class CsvMatrixNode extends AbstractOutputNode<CsvMatrixOutputConfiguration> {

    public CsvMatrixNode() {
        super(new CsvMatrixOutputConfiguration());
        setDisplayName(CsvMatrixOutputFactory.NAME);
    }

    public CsvMatrixNode(CsvMatrixOutputConfiguration config) {
        super(config);
        setDisplayName(CsvMatrixOutputFactory.NAME);
    }

    @Override
    protected Sheet createSheet() {
        CsvMatrixOutputConfiguration config = getLookup().lookup(CsvMatrixOutputConfiguration.class);
        Sheet sheet = super.createSheet();

        NodePropertySetBuilder builder = new NodePropertySetBuilder();
        builder.reset("Location");
        builder.withFile().select(config, "Folder").directories(true).description("Base output folder. Will be extended by the workspace and processing names").add();
        builder.with(String.class).select(config, "fileName").display("File Name").add();
        sheet.put(builder.build());

        builder.reset("Content");
        builder.with(List.class).select(config, "Items").editor(Matrix.class).add();
        sheet.put(builder.build());
        return sheet;
    }

    @Override
    public ISaOutputFactory getFactory() {
        return new CsvMatrixOutputFactory(getLookup().lookup(CsvMatrixOutputConfiguration.class));
    }
}
