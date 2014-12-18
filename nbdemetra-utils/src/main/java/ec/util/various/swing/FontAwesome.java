/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package ec.util.various.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.Timer;

/**
 * Convenient enum that provides access to the "Font Awesome" font set (v4.2).
 *
 * @see http://fortawesome.github.io/Font-Awesome/
 * @author Philippe Charles
 * @author Mats Maggi
 */
public enum FontAwesome {

    FA_GLASS('\uF000'),
    FA_MUSIC('\uF001'),
    FA_SEARCH('\uF002'),
    FA_ENVELOPE_O('\uF003'),
    FA_HEART('\uF004'),
    FA_STAR('\uF005'),
    FA_STAR_O('\uF006'),
    FA_USER('\uF007'),
    FA_FILM('\uF008'),
    FA_TH_LARGE('\uF009'),
    FA_TH('\uF00A'),
    FA_TH_LIST('\uF00B'),
    FA_CHECK('\uF00C'),
    FA_TIMES('\uF00D'),
    FA_SEARCH_PLUS('\uF00E'),
    FA_SEARCH_MINUS('\uF010'),
    FA_POWER_OFF('\uF011'),
    FA_SIGNAL('\uF012'),
    FA_COG('\uF013'),
    FA_TRASH_O('\uF014'),
    FA_HOME('\uF015'),
    FA_FILE_O('\uF016'),
    FA_CLOCK_O('\uF017'),
    FA_ROAD('\uF018'),
    FA_DOWNLOAD('\uF019'),
    FA_ARROW_CIRCLE_O_DOWN('\uF01A'),
    FA_ARROW_CIRCLE_O_UP('\uF01B'),
    FA_INBOX('\uF01C'),
    FA_PLAY_CIRCLE_O('\uF01D'),
    FA_REPEAT('\uF01E'),
    FA_REFRESH('\uF021'),
    FA_LIST_ALT('\uF022'),
    FA_LOCK('\uF023'),
    FA_FLAG('\uF024'),
    FA_HEADPHONES('\uF025'),
    FA_VOLUME_OFF('\uF026'),
    FA_VOLUME_DOWN('\uF027'),
    FA_VOLUME_UP('\uF028'),
    FA_QRCODE('\uF029'),
    FA_BARCODE('\uF02A'),
    FA_TAG('\uF02B'),
    FA_TAGS('\uF02C'),
    FA_BOOK('\uF02D'),
    FA_BOOKMARK('\uF02E'),
    FA_PRINT('\uF02F'),
    FA_CAMERA('\uF030'),
    FA_FONT('\uF031'),
    FA_BOLD('\uF032'),
    FA_ITALIC('\uF033'),
    FA_TEXT_HEIGHT('\uF034'),
    FA_TEXT_WIDTH('\uF035'),
    FA_ALIGN_LEFT('\uF036'),
    FA_ALIGN_CENTER('\uF037'),
    FA_ALIGN_RIGHT('\uF038'),
    FA_ALIGN_JUSTIFY('\uF039'),
    FA_LIST('\uF03A'),
    FA_OUTDENT('\uF03B'),
    FA_INDENT('\uF03C'),
    FA_VIDEO_CAMERA('\uF03D'),
    FA_PICTURE_O('\uF03E'),
    FA_PENCIL('\uF040'),
    FA_MAP_MARKER('\uF041'),
    FA_ADJUST('\uF042'),
    FA_TINT('\uF043'),
    FA_PENCIL_SQUARE_O('\uF044'),
    FA_SHARE_SQUARE_O('\uF045'),
    FA_CHECK_SQUARE_O('\uF046'),
    FA_ARROWS('\uF047'),
    FA_STEP_BACKWARD('\uF048'),
    FA_FAST_BACKWARD('\uF049'),
    FA_BACKWARD('\uF04A'),
    FA_PLAY('\uF04B'),
    FA_PAUSE('\uF04C'),
    FA_STOP('\uF04D'),
    FA_FORWARD('\uF04E'),
    FA_FAST_FORWARD('\uF050'),
    FA_STEP_FORWARD('\uF051'),
    FA_EJECT('\uF052'),
    FA_CHEVRON_LEFT('\uF053'),
    FA_CHEVRON_RIGHT('\uF054'),
    FA_PLUS_CIRCLE('\uF055'),
    FA_MINUS_CIRCLE('\uF056'),
    FA_TIMES_CIRCLE('\uF057'),
    FA_CHECK_CIRCLE('\uF058'),
    FA_QUESTION_CIRCLE('\uF059'),
    FA_INFO_CIRCLE('\uF05A'),
    FA_CROSSHAIRS('\uF05B'),
    FA_TIMES_CIRCLE_O('\uF05C'),
    FA_CHECK_CIRCLE_O('\uF05D'),
    FA_BAN('\uF05E'),
    FA_ARROW_LEFT('\uF060'),
    FA_ARROW_RIGHT('\uF061'),
    FA_ARROW_UP('\uF062'),
    FA_ARROW_DOWN('\uF063'),
    FA_SHARE('\uF064'),
    FA_EXPAND('\uF065'),
    FA_COMPRESS('\uF066'),
    FA_PLUS('\uF067'),
    FA_MINUS('\uF068'),
    FA_ASTERISK('\uF069'),
    FA_EXCLAMATION_CIRCLE('\uF06A'),
    FA_GIFT('\uF06B'),
    FA_LEAF('\uF06C'),
    FA_FIRE('\uF06D'),
    FA_EYE('\uF06E'),
    FA_EYE_SLASH('\uF070'),
    FA_EXCLAMATION_TRIANGLE('\uF071'),
    FA_PLANE('\uF072'),
    FA_CALENDAR('\uF073'),
    FA_RANDOM('\uF074'),
    FA_COMMENT('\uF075'),
    FA_MAGNET('\uF076'),
    FA_CHEVRON_UP('\uF077'),
    FA_CHEVRON_DOWN('\uF078'),
    FA_RETWEET('\uF079'),
    FA_SHOPPING_CART('\uF07A'),
    FA_FOLDER('\uF07B'),
    FA_FOLDER_OPEN('\uF07C'),
    FA_ARROWS_V('\uF07D'),
    FA_ARROWS_H('\uF07E'),
    FA_BAR_CHART_O('\uF080'),
    FA_TWITTER_SQUARE('\uF081'),
    FA_FACEBOOK_SQUARE('\uF082'),
    FA_CAMERA_RETRO('\uF083'),
    FA_KEY('\uF084'),
    FA_COGS('\uF085'),
    FA_COMMENTS('\uF086'),
    FA_THUMBS_O_UP('\uF087'),
    FA_THUMBS_O_DOWN('\uF088'),
    FA_STAR_HALF('\uF089'),
    FA_HEART_O('\uF08A'),
    FA_SIGN_OUT('\uF08B'),
    FA_LINKEDIN_SQUARE('\uF08C'),
    FA_THUMB_TACK('\uF08D'),
    FA_EXTERNAL_LINK('\uF08E'),
    FA_SIGN_IN('\uF090'),
    FA_TROPHY('\uF091'),
    FA_GITHUB_SQUARE('\uF092'),
    FA_UPLOAD('\uF093'),
    FA_LEMON_O('\uF094'),
    FA_PHONE('\uF095'),
    FA_SQUARE_O('\uF096'),
    FA_BOOKMARK_O('\uF097'),
    FA_PHONE_SQUARE('\uF098'),
    FA_TWITTER('\uF099'),
    FA_FACEBOOK('\uF09A'),
    FA_GITHUB('\uF09B'),
    FA_UNLOCK('\uF09C'),
    FA_CREDIT_CARD('\uF09D'),
    FA_RSS('\uF09E'),
    FA_HDD_O('\uF0A0'),
    FA_BULLHORN('\uF0A1'),
    FA_BELL('\uF0F3'),
    FA_CERTIFICATE('\uF0A3'),
    FA_HAND_O_RIGHT('\uF0A4'),
    FA_HAND_O_LEFT('\uF0A5'),
    FA_HAND_O_UP('\uF0A6'),
    FA_HAND_O_DOWN('\uF0A7'),
    FA_ARROW_CIRCLE_LEFT('\uF0A8'),
    FA_ARROW_CIRCLE_RIGHT('\uF0A9'),
    FA_ARROW_CIRCLE_UP('\uF0AA'),
    FA_ARROW_CIRCLE_DOWN('\uF0AB'),
    FA_GLOBE('\uF0AC'),
    FA_WRENCH('\uF0AD'),
    FA_TASKS('\uF0AE'),
    FA_FILTER('\uF0B0'),
    FA_BRIEFCASE('\uF0B1'),
    FA_ARROWS_ALT('\uF0B2'),
    FA_USERS('\uF0C0'),
    FA_LINK('\uF0C1'),
    FA_CLOUD('\uF0C2'),
    FA_FLASK('\uF0C3'),
    FA_SCISSORS('\uF0C4'),
    FA_FILES_O('\uF0C5'),
    FA_PAPERCLIP('\uF0C6'),
    FA_FLOPPY_O('\uF0C7'),
    FA_SQUARE('\uF0C8'),
    FA_BARS('\uF0C9'),
    FA_LIST_UL('\uF0CA'),
    FA_LIST_OL('\uF0CB'),
    FA_STRIKETHROUGH('\uF0CC'),
    FA_UNDERLINE('\uF0CD'),
    FA_TABLE('\uF0CE'),
    FA_MAGIC('\uF0D0'),
    FA_TRUCK('\uF0D1'),
    FA_PINTEREST('\uF0D2'),
    FA_PINTEREST_SQUARE('\uF0D3'),
    FA_GOOGLE_PLUS_SQUARE('\uF0D4'),
    FA_GOOGLE_PLUS('\uF0D5'),
    FA_MONEY('\uF0D6'),
    FA_CARET_DOWN('\uF0D7'),
    FA_CARET_UP('\uF0D8'),
    FA_CARET_LEFT('\uF0D9'),
    FA_CARET_RIGHT('\uF0DA'),
    FA_COLUMNS('\uF0DB'),
    FA_SORT('\uF0DC'),
    FA_SORT_ASC('\uF0DD'),
    FA_SORT_DESC('\uF0DE'),
    FA_ENVELOPE('\uF0E0'),
    FA_LINKEDIN('\uF0E1'),
    FA_UNDO('\uF0E2'),
    FA_GAVEL('\uF0E3'),
    FA_TACHOMETER('\uF0E4'),
    FA_COMMENT_O('\uF0E5'),
    FA_COMMENTS_O('\uF0E6'),
    FA_BOLT('\uF0E7'),
    FA_SITEMAP('\uF0E8'),
    FA_UMBRELLA('\uF0E9'),
    FA_CLIPBOARD('\uF0EA'),
    FA_LIGHTBULB_O('\uF0EB'),
    FA_EXCHANGE('\uF0EC'),
    FA_CLOUD_DOWNLOAD('\uF0ED'),
    FA_CLOUD_UPLOAD('\uF0EE'),
    FA_USER_MD('\uF0F0'),
    FA_STETHOSCOPE('\uF0F1'),
    FA_SUITCASE('\uF0F2'),
    FA_BELL_O('\uF0A2'),
    FA_COFFEE('\uF0F4'),
    FA_CUTLERY('\uF0F5'),
    FA_FILE_TEXT_O('\uF0F6'),
    FA_BUILDING_O('\uF0F7'),
    FA_HOSPITAL_O('\uF0F8'),
    FA_AMBULANCE('\uF0F9'),
    FA_MEDKIT('\uF0FA'),
    FA_FIGHTER_JET('\uF0FB'),
    FA_BEER('\uF0FC'),
    FA_H_SQUARE('\uF0FD'),
    FA_PLUS_SQUARE('\uF0FE'),
    FA_ANGLE_DOUBLE_LEFT('\uF100'),
    FA_ANGLE_DOUBLE_RIGHT('\uF101'),
    FA_ANGLE_DOUBLE_UP('\uF102'),
    FA_ANGLE_DOUBLE_DOWN('\uF103'),
    FA_ANGLE_LEFT('\uF104'),
    FA_ANGLE_RIGHT('\uF105'),
    FA_ANGLE_UP('\uF106'),
    FA_ANGLE_DOWN('\uF107'),
    FA_DESKTOP('\uF108'),
    FA_LAPTOP('\uF109'),
    FA_TABLET('\uF10A'),
    FA_MOBILE('\uF10B'),
    FA_CIRCLE_O('\uF10C'),
    FA_QUOTE_LEFT('\uF10D'),
    FA_QUOTE_RIGHT('\uF10E'),
    FA_SPINNER('\uF110'),
    FA_CIRCLE('\uF111'),
    FA_REPLY('\uF112'),
    FA_GITHUB_ALT('\uF113'),
    FA_FOLDER_O('\uF114'),
    FA_FOLDER_OPEN_O('\uF115'),
    FA_SMILE_O('\uF118'),
    FA_FROWN_O('\uF119'),
    FA_MEH_O('\uF11A'),
    FA_GAMEPAD('\uF11B'),
    FA_KEYBOARD_O('\uF11C'),
    FA_FLAG_O('\uF11D'),
    FA_FLAG_CHECKERED('\uF11E'),
    FA_TERMINAL('\uF120'),
    FA_CODE('\uF121'),
    FA_REPLY_ALL('\uF122'),
    FA_MAIL_REPLY_ALL('\uF122'),
    FA_STAR_HALF_O('\uF123'),
    FA_LOCATION_ARROW('\uF124'),
    FA_CROP('\uF125'),
    FA_CODE_FORK('\uF126'),
    FA_CHAIN_BROKEN('\uF127'),
    FA_QUESTION('\uF128'),
    FA_INFO('\uF129'),
    FA_EXCLAMATION('\uF12A'),
    FA_SUPERSCRIPT('\uF12B'),
    FA_SUBSCRIPT('\uF12C'),
    FA_ERASER('\uF12D'),
    FA_PUZZLE_PIECE('\uF12E'),
    FA_MICROPHONE('\uF130'),
    FA_MICROPHONE_SLASH('\uF131'),
    FA_SHIELD('\uF132'),
    FA_CALENDAR_O('\uF133'),
    FA_FIRE_EXTINGUISHER('\uF134'),
    FA_ROCKET('\uF135'),
    FA_MAXCDN('\uF136'),
    FA_CHEVRON_CIRCLE_LEFT('\uF137'),
    FA_CHEVRON_CIRCLE_RIGHT('\uF138'),
    FA_CHEVRON_CIRCLE_UP('\uF139'),
    FA_CHEVRON_CIRCLE_DOWN('\uF13A'),
    FA_HTML5('\uF13B'),
    FA_CSS3('\uF13C'),
    FA_ANCHOR('\uF13D'),
    FA_UNLOCK_ALT('\uF13E'),
    FA_BULLSEYE('\uF140'),
    FA_ELLIPSIS_H('\uF141'),
    FA_ELLIPSIS_V('\uF142'),
    FA_RSS_SQUARE('\uF143'),
    FA_PLAY_CIRCLE('\uF144'),
    FA_TICKET('\uF145'),
    FA_MINUS_SQUARE('\uF146'),
    FA_MINUS_SQUARE_O('\uF147'),
    FA_LEVEL_UP('\uF148'),
    FA_LEVEL_DOWN('\uF149'),
    FA_CHECK_SQUARE('\uF14A'),
    FA_PENCIL_SQUARE('\uF14B'),
    FA_EXTERNAL_LINK_SQUARE('\uF14C'),
    FA_SHARE_SQUARE('\uF14D'),
    FA_COMPASS('\uF14E'),
    FA_CARET_SQUARE_O_DOWN('\uF150'),
    FA_CARET_SQUARE_O_UP('\uF151'),
    FA_CARET_SQUARE_O_RIGHT('\uF152'),
    FA_EUR('\uF153'),
    FA_GBP('\uF154'),
    FA_USD('\uF155'),
    FA_INR('\uF156'),
    FA_JPY('\uF157'),
    FA_RUB('\uF158'),
    FA_KRW('\uF159'),
    FA_BTC('\uF15A'),
    FA_FILE('\uF15B'),
    FA_FILE_TEXT('\uF15C'),
    FA_SORT_ALPHA_ASC('\uF15D'),
    FA_SORT_ALPHA_DESC('\uF15E'),
    FA_SORT_AMOUNT_ASC('\uF160'),
    FA_SORT_AMOUNT_DESC('\uF161'),
    FA_SORT_NUMERIC_ASC('\uF162'),
    FA_SORT_NUMERIC_DESC('\uF163'),
    FA_THUMBS_UP('\uF164'),
    FA_THUMBS_DOWN('\uF165'),
    FA_YOUTUBE_SQUARE('\uF166'),
    FA_YOUTUBE('\uF167'),
    FA_XING('\uF168'),
    FA_XING_SQUARE('\uF169'),
    FA_YOUTUBE_PLAY('\uF16A'),
    FA_DROPBOX('\uF16B'),
    FA_STACK_OVERFLOW('\uF16C'),
    FA_INSTAGRAM('\uF16D'),
    FA_FLICKR('\uF16E'),
    FA_ADN('\uF170'),
    FA_BITBUCKET('\uF171'),
    FA_BITBUCKET_SQUARE('\uF172'),
    FA_TUMBLR('\uF173'),
    FA_TUMBLR_SQUARE('\uF174'),
    FA_LONG_ARROW_DOWN('\uF175'),
    FA_LONG_ARROW_UP('\uF176'),
    FA_LONG_ARROW_LEFT('\uF177'),
    FA_LONG_ARROW_RIGHT('\uF178'),
    FA_APPLE('\uF179'),
    FA_WINDOWS('\uF17A'),
    FA_ANDROID('\uF17B'),
    FA_LINUX('\uF17C'),
    FA_DRIBBBLE('\uF17D'),
    FA_SKYPE('\uF17E'),
    FA_FOURSQUARE('\uF180'),
    FA_TRELLO('\uF181'),
    FA_FEMALE('\uF182'),
    FA_MALE('\uF183'),
    FA_GITTIP('\uF184'),
    FA_SUN_O('\uF185'),
    FA_MOON_O('\uF186'),
    FA_ARCHIVE('\uF187'),
    FA_BUG('\uF188'),
    FA_VK('\uF189'),
    FA_WEIBO('\uF18A'),
    FA_RENREN('\uF18B'),
    FA_PAGELINES('\uF18C'),
    FA_STACK_EXCHANGE('\uF18D'),
    FA_ARROW_CIRCLE_O_RIGHT('\uF18E'),
    FA_ARROW_CIRCLE_O_LEFT('\uF190'),
    FA_CARET_SQUARE_O_LEFT('\uF191'),
    FA_DOT_CIRCLE_O('\uF192'),
    FA_WHEELCHAIR('\uF193'),
    FA_VIMEO_SQUARE('\uF194'),
    FA_TRY('\uF195'),
    FA_PLUS_SQUARE_O('\uF196'),
    //4.1
    FA_BEHANCE('\uF1B4'),
    FA_BEHANCE_SQUARE('\uF1B5'),
    FA_BOMB('\uF1E2'),
    FA_BUILDING('\uF1AD'),
    FA_CAR('\uF1B9'),
    FA_CHILD('\uF1AE'),
    FA_CIRCLE_O_NOTCH('\uF1CE'),
    FA_CIRCLE_THIN('\uF1DB'),
    FA_CODEPEN('\uF1CB'),
    FA_CUBE('\uF1B2'),
    FA_CUBES('\uF1B3'),
    FA_DATABASE('\uF1C0'),
    FA_DELICIOUS('\uF1A5'),
    FA_DEVIANTART('\uF1BD'),
    FA_DIGG('\uF1A6'),
    FA_DRUPAL('\uF1A9'),
    FA_EMPIRE('\uF1D1'),
    FA_ENVELOPE_SQUARE('\uF199'),
    FA_FAX('\uF1AC'),
    FA_FILE_ARCHIVE_O('\uF1C6'),
    FA_FILE_AUDIO_O('\uF1C7'),
    FA_FILE_CODE_O('\uF1C9'),
    FA_FILE_EXCEL_O('\uF1C3'),
    FA_FILE_IMAGE_O('\uF1C5'),
    FA_FILE_PDF_O('\uF1C1'),
    FA_FILE_POWERPOINT_O('\uF1C4'),
    FA_FILE_VIDEO_O('\uF1C8'),
    FA_FILE_WORD_O('\uF1C2'),
    FA_GIT('\uF1D3'),
    FA_GIT_SQUARE('\uF1D2'),
    FA_GOOGLE('\uF1A0'),
    FA_GRADUATION_CAP('\uF19D'),
    FA_HACKER_NEWS('\uF1D4'),
    FA_HEADER('\uF1DC'),
    FA_HISTORY('\uF1DA'),
    FA_JOOMLA('\uF1AA'),
    FA_JSFIDDLE('\uF1CC'),
    FA_LANGUAGE('\uF1AB'),
    FA_LIFE_RING('\uF1CD'),
    FA_OPENID('\uF19B'),
    FA_PAPER_PLANE('\uF1D8'),
    FA_PAPER_PLANE_O('\uF1D9'),
    FA_PARAGRAPH('\uF1DD'),
    FA_PAW('\uF1B0'),
    FA_PIED_PIPER('\uF1A7'),
    FA_PIED_PIPER_ALT('\uF1A8'),
    FA_QQ('\uF1D6'),
    FA_REBEL('\uF1D0'),
    FA_RECYCLE('\uF1B8'),
    FA_REDDIT('\uF1A1'),
    FA_REDDIT_SQUARE('\uF1A2'),
    FA_SHARE_ALT('\uF1E0'),
    FA_SHARE_ALT_SQUARE('\uF1E1'),
    FA_SLACK('\uF198'),
    FA_SLIDERS('\uF1DE'),
    FA_SOUNDCLOUD('\uF1BE'),
    FA_SPACE_SHUTTLE('\uF197'),
    FA_SPOON('\uF1B1'),
    FA_SPOTIFY('\uF1BC'),
    FA_STEAM('\uF1B6'),
    FA_STEAM_SQUARE('\uF1B7'),
    FA_STUMBLEUPON('\uF1A4'),
    FA_STUMBLEUPON_CIRCLE('\uF1A3'),
    FA_TAXI('\uF1BA'),
    FA_TENCENT_WEIBO('\uF1D5'),
    FA_TREE('\uF1BB'),
    FA_UNIVERSITY('\uF19C'),
    FA_VINE('\uF1CA'),
    FA_WEIXIN('\uF1D7'),
    FA_WORDPRESS('\uF19A'),
    FA_YAHOO('\uF19E'),
    //4.2
    FA_ANGELLIST('\uF209'),
    FA_AREA_CHART('\uF1FE'),
    FA_AT('\uF1FA'),
    FA_BELL_SLASH('\uF1F6'),
    FA_BELL_SLASH_O('\uF1F7'),
    FA_BICYCLE('\uF206'),
    FA_BINOCULARS('\uF1E5'),
    FA_BIRTHDAY_CAKE('\uF1FD'),
    FA_BUS('\uF207'),
    FA_CALCULATOR('\uF1EC'),
    FA_CC('\uF20A'),
    FA_CC_AMEX('\uF1F3'),
    FA_CC_DISCOVER('\uF1F2'),
    FA_CC_MASTERCARD('\uF1F1'),
    FA_CC_PAYPAL('\uF1F4'),
    FA_CC_STRIPE('\uF1F5'),
    FA_CC_VISA('\uF1F0'),
    FA_COPYRIGHT('\uF1F9'),
    FA_EYEDROPPER('\uF1FB'),
    FA_FUTBOL_O('\uF1E3'),
    FA_GOOGLE_WALLET('\uF1EE'),
    FA_ILS('\uF20B'),
    FA_IOXHOST('\uF208'),
    FA_LASTFM('\uF202'),
    FA_LASTFM_SQUARE('\uF203'),
    FA_LINE_CHART('\uF201'),
    FA_MEANPATH('\uF20C'),
    FA_NEWSPAPER_O('\uF1EA'),
    FA_PAINT_BRUSH('\uF1FC'),
    FA_PAYPAL('\uF1ED'),
    FA_PIE_CHART('\uF200'),
    FA_PLUG('\uF1E6'),
    FA_SLIDESHARE('\uF1E7'),
    FA_TOGGLE_OFF('\uF204'),
    FA_TOGGLE_ON('\uF205'),
    FA_TRASH('\uF1F8'),
    FA_TTY('\uF1E4'),
    FA_TWITCH('\uF1E8'),
    FA_WIFI('\uF1EB'),
    FA_YELP('\uF1E9');

    private final char iconAsChar;

    private FontAwesome(char iconAsChar) {
        this.iconAsChar = iconAsChar;
    }

    public char getIconAsChar() {
        return iconAsChar;
    }

    @Nonnull
    public static Font getFont() {
        return LazyHolder.INSTANCE;
    }

    @Nonnull
    public Icon getIcon(@Nonnull Color color, float size) {
        return new ImageIcon(createImage(color, size, 0));
    }

    @Nonnull
    public Icon getIcon(@Nonnull Color color, float size, double angle) {
        return new ImageIcon(createImage(color, size, angle));
    }

    @Nonnull
    public Icon getSpinningIcon(@Nonnull Component component, @Nonnull Color color, float size) {
        return new SpinningIcon(component, createImage(color, size, 0));
    }

    @Nonnull
    public Image getImage(@Nonnull Color color, float size) {
        return createImage(color, size, 0);
    }

    @Nonnull
    public Image getImage(@Nonnull Color color, float size, double angle) {
        return createImage(color, size, angle);
    }

    @Nonnull
    public List<Image> getImages(@Nonnull Color color, @Nonnull float... sizes) {
        List<Image> result = new ArrayList<>();
        for (float size : sizes) {
            result.add(getImage(color, size));
        }
        return result;
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    @Nonnull
    private BufferedImage createImage(@Nonnull Color color, float size, double angle) {
        // https://github.com/FortAwesome/Font-Awesome/blob/master/less/fixed-width.less
        float w = (18 * size / 14);
        float h = size;
        
        BufferedImage result = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g = (Graphics2D) result.getGraphics();
        
        if (angle != 0) {
            AffineTransform trans = new AffineTransform();
            trans.rotate(Math.toRadians(angle), w / 2.0, h / 2.0);
            g.setTransform(trans);
        }
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(getFont().deriveFont(Font.PLAIN, size));
        g.setColor(color);
        
        FontMetrics fm = g.getFontMetrics();
        float x = (w - fm.charWidth(iconAsChar)) / 2f;
        float y = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) / 2f);
        
        g.drawString(String.valueOf(iconAsChar), x, y);
        
        g.dispose();
        
        return result;
    }
    
    private static final class LazyHolder {
        
        private static final Font INSTANCE = create();
        
        private static final String PATH = "/ec/util/various/swing/fontawesome-webfont.ttf";
        
        private static Font create() {
            try (InputStream stream = LazyHolder.class.getResourceAsStream(PATH)) {
                Font result = Font.createFont(Font.TRUETYPE_FONT, stream);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(result);
                return result;
            } catch (FontFormatException | IOException ex) {
                throw new RuntimeException("Cannot load font", ex);
            }
        }
    }
    
    private static final class SpinningIcon implements Icon, Puppet {
        
        private static final int DURATION = 2000;
        
        private final Component component;
        private final ImageIcon imageIcon;
        private double position;
        
        private SpinningIcon(Component c, BufferedImage image) {
            this.component = c;
            this.imageIcon = new ImageIcon(image);
            Animator.INSTANCE.register(this);
        }
        
        @Override
        public void refresh(long timeInMillis) {
            position = 1f * (timeInMillis % DURATION) / DURATION;
            component.repaint();
        }
        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            double angle = Math.PI * 2 * position;
            
            BufferedImage image = (BufferedImage) imageIcon.getImage();
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            AffineTransform trans = new AffineTransform();
            trans.translate(x, y);
            trans.rotate(angle, getIconWidth() / 2d, getIconHeight() / 2d);
            
            g2d.drawImage(image, trans, c);
        }
        
        @Override
        public int getIconWidth() {
            return imageIcon.getIconWidth();
        }
        
        @Override
        public int getIconHeight() {
            return imageIcon.getIconHeight();
        }
    }
    
    private static final class Animator implements ActionListener {
        
        private static final int FPS = 60;
        public static final Animator INSTANCE = new Animator();
        
        private final Timer timer;
        private final List<WeakReference<Puppet>> items;
        
        private Animator() {
            this.timer = new Timer(1000 / FPS, this);
            timer.start();
            this.items = new ArrayList<>();
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            long time = System.currentTimeMillis();
            Iterator<WeakReference<Puppet>> iterator = items.iterator();
            while (iterator.hasNext()) {
                WeakReference<Puppet> ref = iterator.next();
                Puppet o = ref.get();
                if (o != null) {
                    o.refresh(time);
                } else {
                    iterator.remove();
                }
            }
        }
        
        public void register(Puppet stuff) {
            items.add(new WeakReference(stuff));
        }
    }
    
    private interface Puppet {
        
        void refresh(long timeInMillis);
    }
    //</editor-fold>
}
