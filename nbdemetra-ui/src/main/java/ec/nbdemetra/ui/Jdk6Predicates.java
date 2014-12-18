/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui;

import com.google.common.base.Predicate;
import org.openide.nodes.Node;

/**
 * Helper class to simplify code. Should be rendered useless by Jdk8.
 *
 * @author Philippe Charles
 */
public final class Jdk6Predicates {

    private Jdk6Predicates() {
        // static class
    }

    public static Predicate<Node> lookupNode(final Class<?> clazz) {
        return new Predicate<Node>() {
            @Override
            public boolean apply(Node input) {
                return input.getLookup().lookup(clazz) != null;
            }
        };
    }
}
