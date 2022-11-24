/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.properties.l2fprod;

/**
 *
 * @author Jean Palate
 */
public class UserVariables {
        private final String[] vars;

    public UserVariables(String... name) {
        if (name != null) {
            this.vars = name;
        }
        else {
            this.vars = new String[0];
        }
    }

    public String[] getNames() {
        return vars;
    }

    @Override
    public String toString() {
        return (vars == null || vars.length == 0) ? "Unused" : vars.length + " vars";
    }
}
