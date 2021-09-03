/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.util;

import org.openide.DialogDescriptor;

/**
 *
 * @author Philippe Charles
 */
public interface IDialogDescriptorProvider {

    DialogDescriptor createDialogDescriptor(String title);
}
