/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.output;

import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.tss.sa.ISaOutputFactory;
import ec.tss.sa.output.TxtOutputConfiguration;
import ec.tss.sa.output.TxtOutputFactory;
import java.util.List;
import org.openide.nodes.Sheet;

/**
 *
 * @author Jean Palate
 */
public class TxtNode extends AbstractOutputNode<TxtOutputConfiguration> {

    public TxtNode() {
        super(new TxtOutputConfiguration());
        setDisplayName(TxtOutputFactory.NAME);
    }

    public TxtNode(TxtOutputConfiguration config) {
        super(config);
        setDisplayName(TxtOutputFactory.NAME);
    }

    @Override
    protected Sheet createSheet() {
        TxtOutputConfiguration config = getLookup().lookup(TxtOutputConfiguration.class);
        Sheet sheet = super.createSheet();
        NodePropertySetBuilder builder = new NodePropertySetBuilder();
        builder.reset("Location");
        builder.withFile().select(config, "Folder").directories(true).description("Base output folder. Will be extended by the workspace and processing names").add();
        sheet.put(builder.build());

        builder.reset("Content");
        builder.with(List.class).select(config, "Series").editor(Series.class).add();
        sheet.put(builder.build());
        return sheet;
    }

    @Override
    public ISaOutputFactory getFactory() {
        return new TxtOutputFactory(getLookup().lookup(TxtOutputConfiguration.class));
    }
}
