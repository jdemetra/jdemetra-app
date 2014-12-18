/*
 * Copyright 2014 National Bank of Belgium
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.openide.ErrorManager;
import org.openide.awt.NotificationDisplayer;

/**
 *
 * @author Mats Maggi
 */
public class NotifyUtil {

    private NotifyUtil() {
    }

    /**
     * Show message with the specified type and action listener
     *
     * @param title
     * @param type
     * @param message
     * @param actionListener
     * @param balloon
     * @param popup
     */
    public static void show(String title, String message, MessageType type,
            ActionListener actionListener, @Nullable JComponent balloon, @Nullable JComponent popup) {
        if (balloon == null && popup == null) {
            NotificationDisplayer.getDefault().notify(title, type.getIcon(), message, actionListener);
        } else {
            NotificationDisplayer.getDefault().notify(title,
                    type.getIcon(),
                    balloon == null ? new JLabel(message) : balloon,
                    popup == null ? new JLabel(message) : popup,
                    NotificationDisplayer.Priority.NORMAL,
                    NotificationDisplayer.Category.INFO);
        }

    }

    /**
     * Show message with the specified type and a default action which displays
     * the message using {@link MessageUtil} with the same message type
     *
     * @param title
     * @param message
     * @param type
     */
    public static void show(String title, final String message, final MessageType type) {
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MessageUtil.show(message, type);
            }
        };
        show(title, message, type, actionListener, null, null);
    }

    /**
     * Show an information notification
     *
     * @param title
     * @param message
     */
    public static void info(String title, String message) {
        show(title, message, MessageType.INFO);
    }

    /**
     * Show an error notification
     *
     * @param title
     * @param message
     */
    public static void error(String title, String message) {
        show(title, message, MessageType.ERROR);
    }

    /**
     * Show an error notification for an exception
     *
     * @param title
     * @param message
     * @param exception
     */
    public static void error(String title, final String message, final Throwable exception) {
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ErrorManager.getDefault().notify(exception);
            }
        };
        show(title, message, MessageType.ERROR, actionListener, null, null);
    }

    /**
     * Show a warning notification
     *
     * @param title
     * @param message
     */
    public static void warn(String title, String message) {
        show(title, message, MessageType.WARNING);
    }

    /**
     * Show an plain notification
     *
     * @param title
     * @param message
     */
    public static void plain(String title, String message) {
        show(title, message, MessageType.PLAIN);
    }
}
