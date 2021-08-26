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

import ec.util.various.swing.FontAwesome;
import java.awt.Color;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Mats Maggi
 */
public enum MessageType {

    PLAIN(NotifyDescriptor.PLAIN_MESSAGE, new ImageIcon()),
    INFO(NotifyDescriptor.INFORMATION_MESSAGE, FontAwesome.FA_INFO_CIRCLE.getIcon(Color.BLUE, 20)),
    SUCCESS(NotifyDescriptor.INFORMATION_MESSAGE, FontAwesome.FA_CHECK_CIRCLE.getIcon(new Color(0, 166, 6), 20)),
    QUESTION(NotifyDescriptor.QUESTION_MESSAGE, FontAwesome.FA_QUESTION_CIRCLE.getIcon(Color.BLUE, 20)),
    ERROR(NotifyDescriptor.ERROR_MESSAGE, FontAwesome.FA_TIMES_CIRCLE.getIcon(Color.RED, 20)),
    WARNING(NotifyDescriptor.WARNING_MESSAGE, FontAwesome.FA_EXCLAMATION_TRIANGLE.getIcon(Color.ORANGE, 20));

    private final int notifyDescriptorType;
    private final Icon icon;

    MessageType(int notifyDescriptorType, Icon icon) {
        this.notifyDescriptorType = notifyDescriptorType;
        this.icon = icon;
    }

    int getNotifyDescriptorType() {
        return notifyDescriptorType;
    }

    Icon getIcon() {
        return icon;
    }
}
