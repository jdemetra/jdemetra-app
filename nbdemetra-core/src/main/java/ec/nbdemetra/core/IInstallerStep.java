/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.core;

/**
 *
 * @author Philippe Charles
 */
@Deprecated
public interface IInstallerStep {

    void restore();

    void close();
}
