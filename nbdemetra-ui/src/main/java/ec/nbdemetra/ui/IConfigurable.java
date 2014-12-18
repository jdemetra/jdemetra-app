/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui;

/**
 *
 * @author Philippe Charles
 */
public interface IConfigurable {

    Config getConfig();

    void setConfig(Config config) throws IllegalArgumentException;

    Config editConfig(Config config) throws IllegalArgumentException;
}
