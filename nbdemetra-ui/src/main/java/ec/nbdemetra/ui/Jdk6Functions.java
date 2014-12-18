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
public final class Jdk6Functions {

    private Jdk6Functions() {
        // static class
    }

    public static Function<ColorScheme, String> colorSchemeName() {
        return COLOR_SCHEME_NAME;
    }

    public static Function<ColorScheme, String> colorSchemeDisplayName() {
        return COLOR_SCHEME_DISPLAY_NAME;
    }

    public static Function<INamedService, String> namedServiceName() {
        return NAMED_SERVICE_NAME;
    }

    public static Function<INamedService, String> namedServiceDisplayName() {
        return NAMED_SERVICE_DISPLAY_NAME;
    }

    public static Function<INamedService, NamedServiceNode> namedServiceToNode() {
        return NAMED_SERVICE_TO_NODE;
    }

    public static <X> Function<X, CharSequence> forFormatter(final IFormatter<X> formatter) {
        return new Function<X, CharSequence>() {
            @Override
            public CharSequence apply(X input) {
                return formatter.format(input);
            }
        };
    }

    public static <X> Function<CharSequence, X> forParser(final IParser<X> parser) {
        return new Function<CharSequence, X>() {
            @Override
            public X apply(CharSequence input) {
                return parser.parse(input);
            }
        };
    }

    public static <X> Function<Node, X> lookupNode(final Class<X> clazz) {
        return new Function<Node, X>() {
            @Override
            public X apply(Node input) {
                return input.getLookup().lookup(clazz);
            }
        };
    }
    //
    private static final Function<ColorScheme, String> COLOR_SCHEME_NAME = new Function<ColorScheme, String>() {
        @Override
        public String apply(ColorScheme input) {
            return input.getName();
        }
    };
    private static final Function<ColorScheme, String> COLOR_SCHEME_DISPLAY_NAME = new Function<ColorScheme, String>() {
        @Override
        public String apply(ColorScheme input) {
            return input.getDisplayName();
        }
    };
    private static final Function<INamedService, String> NAMED_SERVICE_NAME = new Function<INamedService, String>() {
        @Override
        public String apply(INamedService input) {
            return input.getName();
        }
    };
    private static final Function<INamedService, String> NAMED_SERVICE_DISPLAY_NAME = new Function<INamedService, String>() {
        @Override
        public String apply(INamedService input) {
            return input.getDisplayName();
        }
    };
    private static final Function<INamedService, NamedServiceNode> NAMED_SERVICE_TO_NODE = new Function<INamedService, NamedServiceNode>() {
        @Override
        public NamedServiceNode apply(INamedService input) {
            return new NamedServiceNode(input);
        }
    };
}
