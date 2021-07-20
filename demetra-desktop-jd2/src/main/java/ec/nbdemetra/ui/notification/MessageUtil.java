/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.nbdemetra.ui.notification;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Mats Maggi
 */
public class MessageUtil {

    private MessageUtil() {
    }

    /**
     * Gets the dialog displayer used to show message boxes
     * @return The dialog displayer used to show message boxes
     */
    public static DialogDisplayer getDialogDisplayer() {
        return DialogDisplayer.getDefault();
    }

    /**
     * Show a message of the specified type
     * messageType As in {@link NotifyDescription} message type constants.
     * @param message
     * @param messageType
     */
    public static void show(String message, MessageType messageType) {
        getDialogDisplayer().notify(new NotifyDescriptor.Message(message, messageType.getNotifyDescriptorType()));
    }

    /**
     * Show an exception message dialog
     * @param message
     * @param exception
     */
    public static void showException(String message, Throwable exception) {
        getDialogDisplayer().notify(new NotifyDescriptor.Exception(exception, message));
    }

    /**
     * Show an information dialog
     * @param message
     */
    public static void info(String message) {
        show(message, MessageType.INFO);
    }

    /**
     * Show an error dialog
     * @param message
     */
    public static void error(String message) {
        show(message, MessageType.ERROR);
    }

    /**
     * Show an error dialog for an exception
     * @param message
     * @param exception
     */
    public static void error(String message, Throwable exception) {
        showException(message, exception);
    }

    /**
     * Show an question dialog
     * @param message
     */
    public static void question(String message) {
        show(message, MessageType.QUESTION);
    }

    /**
     * Show an warning dialog
     * @param message
     */
    public static void warn(String message) {
        show(message, MessageType.WARNING);
    }

    /**
     * Show an plain dialog
     * @param message
     */
    public static void plain(String message) {
        show(message, MessageType.PLAIN);
    }
}
