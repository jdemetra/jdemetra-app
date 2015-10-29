/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties.l2fprod;

/**
 *
 * @author Jean Palate
 */
public class UserVariables {
        private final String[] vars_;

    public UserVariables(String... name) {
        if (name != null) {
            this.vars_ = name;
        }
        else {
            this.vars_ = new String[0];
        }
    }

    public String[] getNames() {
        return vars_;
    }

    @Override
    public String toString() {
        return (vars_ == null || vars_.length == 0) ? "Unused" : vars_.length + " vars";
    }
}
