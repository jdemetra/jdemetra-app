package ec.util.completion.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import javax.swing.Popup;
import javax.swing.PopupFactory;

/**
 *
 * @author Philippe Charles
 */
public final class XPopup {

    public enum Anchor {

        TOP_LEADING,
        BOTTOM_LEADING,
        TOP_TRAILING,
        BOTTOM_TRAILING;
    }
    //
    private Popup popup;

    public XPopup() {
        this.popup = null;
    }

    public void hide() {
        if (popup != null) {
            popup.hide();
            popup = null;
        }
    }

    public void show(Component owner, Component content, Anchor anchor, Dimension gap) {
        if (owner == null || !owner.isShowing()) {
            return;
        }
        hide();
        Point location = getLocation(owner, content, anchor, gap);
        popup = PopupFactory.getSharedInstance().getPopup(owner, content, location.x, location.y);
        popup.show();
    }

    private static Rectangle getScreenBounds(GraphicsConfiguration gc) {
        Rectangle result = gc.getBounds();
        // Take into account screen insets, decrease viewport
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        result.x += screenInsets.left;
        result.y += screenInsets.top;
        result.width -= (screenInsets.left + screenInsets.right);
        result.height -= (screenInsets.top + screenInsets.bottom);
        return result;
    }

    private static Point getLocation(Component owner, Component content, Anchor anchor, Dimension gap) {
        // get top-left anchor
        Point screenLocation = owner.getLocationOnScreen();

        Point result = new Point();
        Dimension size = content.getPreferredSize();

        boolean above = anchor == Anchor.TOP_LEADING || anchor == Anchor.TOP_TRAILING;
        boolean leading = anchor == Anchor.TOP_LEADING || anchor == Anchor.BOTTOM_LEADING;

        if (!owner.getComponentOrientation().isLeftToRight()) {
            leading = !leading;
        }

        result.y = screenLocation.y + (above ? (-size.height - gap.height) : (owner.getHeight() + gap.height));
        result.x = screenLocation.x + (leading ? (gap.width) : (owner.getWidth() - size.width - gap.width));

        // Fit as much of the content on screen as possible
        Rectangle screenBounds = getScreenBounds(owner.getGraphicsConfiguration());
        if (result.x < screenBounds.x) {
            result.x = screenBounds.x;
        } else if (result.x - screenBounds.x + size.width > screenBounds.width) {
            result.x = screenBounds.x + Math.max(0, screenBounds.width - size.width);
        }
        if (result.y < screenBounds.y) {
            result.y = screenBounds.y;
        } else if (result.y - screenBounds.y + size.height > screenBounds.height) {
            result.y = screenBounds.y + Math.max(0, screenBounds.height - size.height);
        }
        return result;
    }
}
