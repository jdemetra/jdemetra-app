/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui;

import com.google.common.base.Function;
import ec.nbdemetra.ui.ns.INamedService;
import ec.nbdemetra.ui.ns.NamedServiceNode;
import ec.tss.tsproviders.utils.IFormatter;
import ec.tss.tsproviders.utils.IParser;
import ec.util.chart.ColorScheme;
import org.openide.nodes.Node;

/**
 * Helper class to simplify code. Should be rendered useless by Jdk8.
 *
 * @author Philippe Charles
 */
@Deprecated
public final class Jdk6Functions {

    private Jdk6Functions() {
        // static class
    }

    public static Function<ColorScheme, String> colorSchemeName() {
        return o -> o.getName();
    }

    public static Function<ColorScheme, String> colorSchemeDisplayName() {
        return o -> o.getDisplayName();
    }

    public static Function<INamedService, String> namedServiceName() {
        return o -> o.getName();
    }

    public static Function<INamedService, String> namedServiceDisplayName() {
        return o -> o.getDisplayName();
    }

    public static Function<INamedService, NamedServiceNode> namedServiceToNode() {
        return o -> new NamedServiceNode(o);
    }

    public static <X> Function<X, CharSequence> forFormatter(IFormatter<X> formatter) {
        return o -> formatter.format(o);
    }

    public static <X> Function<CharSequence, X> forParser(IParser<X> parser) {
        return o -> parser.parse(o);
    }

    public static <X> Function<Node, X> lookupNode(Class<X> clazz) {
        return o -> o.getLookup().lookup(clazz);
    }
}
