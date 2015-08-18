/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Philippe Charles
 */
public class DhmsPropertyEditor extends PropertyEditorSupport {

    final JLabel painter;

    public DhmsPropertyEditor() {
        this.painter = new JLabel();
        // FIXME: don't hardcode this border
        painter.setBorder(new EmptyBorder(0, 3, 0, 0));
    }

    @Override
    public String getAsText() {
        Object value = getValue();
        return value instanceof Long ? millisToShortDhms((Long) value) : super.getAsText();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(shortDhmsToMillis(text));
    }

    @Override
    public boolean isPaintable() {
        return true;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
        painter.setText(millisToLongDhms((Long) getValue()));
        painter.setBounds(box);
        painter.paint(gfx);
    }
    //
    private final static long ONE_SECOND = 1000;
    private final static long SECONDS = 60;
    private final static long MINUTES = 60;
    private final static long HOURS = 24;

    private static int[] millisToDHMS(long duration) {
        duration /= ONE_SECOND;
        int seconds = (int) (duration % SECONDS);
        duration /= SECONDS;
        int minutes = (int) (duration % MINUTES);
        duration /= MINUTES;
        int hours = (int) (duration % HOURS);
        int days = (int) (duration / HOURS);
        return new int[]{days, hours, minutes, seconds};
    }

    /**
     * converts time (in milliseconds) to human-readable format "<w> days, <x>
     * hours, <y> minutes and (z) seconds"
     *
     * @see http://www.rgagnon.com/javadetails/java-0585.html
     */
    public static String millisToLongDhms(long duration) {
        if (duration < ONE_SECOND) {
            return "0 second";
        }
        int[] items = millisToDHMS(duration);
        StringBuilder res = new StringBuilder();
        if (items[0] > 0) {
            res.append(items[0]).append(" day").append(items[0] > 1 ? "s" : "")
                    .append(items[1] > 0 ? ", " : "");
        }
        if (items[1] > 0) {
            res.append(items[1]).append(" hour").append(items[1] > 1 ? "s" : "")
                    .append(items[2] > 0 ? ", " : "");
        }
        if (items[2] > 0) {
            res.append(items[2]).append(" minute").append(items[2] > 1 ? "s" : "");
        }
        if (res.length() > 0 && items[3] > 0) {
            res.append(" and ");
        }
        if (items[3] > 0) {
            res.append(items[3]).append(" second").append(items[3] > 1 ? "s" : "");
        }
        return res.toString();
    }

    /**
     * converts time (in milliseconds) to human-readable format "<dd:>hh:mm:ss"
     *
     * @see http://www.rgagnon.com/javadetails/java-0585.html
     */
    public static String millisToShortDhms(long duration) {
        int[] items = millisToDHMS(duration);
        return items[0] == 0
                ? String.format("%02d:%02d:%02d", items[1], items[2], items[3])
                : String.format("%dd%02d:%02d:%02d", items[0], items[1], items[2], items[3]);
    }

    public static long shortDhmsToMillis(String text) throws IllegalArgumentException {
        Pattern p = Pattern.compile("^(?:(\\d+)d)?(\\d{2}):(\\d{2}):(\\d{2})$");
        Matcher m = p.matcher(text);
        if (!m.matches()) {
            throw new IllegalArgumentException(text);
        }
        int days = m.group(1) != null ? Integer.parseInt(m.group(1)) : 0;
        int hours = Integer.parseInt(m.group(2));
        int minutes = Integer.parseInt(m.group(3));
        int seconds = Integer.parseInt(m.group(4));
        return (((days * 24L + hours) * 60 + minutes) * 60 + seconds) * 1000;
    }
}
