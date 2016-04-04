/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties.l2fprod;

/**
 *
 * @author Jean Palate
 */
public class UserVariable {
        private final String var_;

    public UserVariable(String name) {
        var_ = name;
     }

    public String getName() {
        return var_;
    }

    @Override
    public String toString() {
        return (var_ == null) ? "Unused" : var_;
    }
}
