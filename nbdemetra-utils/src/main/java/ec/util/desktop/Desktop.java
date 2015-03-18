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
package ec.util.desktop;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This interface is the main user entry point of the Desktop API. The real work
 * is done by a concrete implementation loaded at runtime.<p>You can instantiate
 * a concrete implementation but this work is already done by the
 * {@link DesktopManager} class through the use of {@link Factory}.
 *
 * @see DesktopManager
 * @see Factory
 * @author Philippe Charles
 */
public interface Desktop {

    /**
     * Represents an action type. Each platform supports a different set of
     * actions. You may use the {@link Desktop#isSupported} method to determine
     * if the given action is supported by the current platform.
     *
     * @see Desktop#isSupported(Desktop.Action)
     */
    public enum Action {

        /**
         * Represents an "open" action.
         *
         * @see Desktop#open(java.io.File)
         */
        OPEN,
        /**
         * Represents an "edit" action.
         *
         * @see Desktop#edit(java.io.File)
         */
        EDIT,
        /**
         * Represents a "print" action.
         *
         * @see Desktop#print(java.io.File)
         */
        PRINT,
        /**
         * Represents a "mail" action.
         *
         * @see Desktop#mail()
         * @see Desktop#mail(java.net.URI)
         */
        MAIL,
        /**
         * Represents a "browse" action.
         *
         * @see Desktop#browse(java.net.URI)
         */
        BROWSE,
        /**
         * Represents a "show in folder" action.
         *
         * @see Desktop#showInFolder(java.io.File)
         */
        SHOW_IN_FOLDER,
        /**
         * Represents a "move to trash" action.
         *
         * @see Desktop#moveToTrash(java.io.File[])
         */
        MOVE_TO_TRASH,
        /**
         * Represents a "search" action.
         *
         * @see Desktop#search(java.lang.String)
         */
        SEARCH,
        /**
         * Represents the lookup of known folders.
         * 
         * @see Desktop#getKnownFolderPath(ec.util.desktop.Desktop.KnownFolder) 
         */
        KNOWN_FOLDER_LOOKUP;
    }

    /**
     * Well-known folders
     */
    public enum KnownFolder {

        DESKTOP,
        DOWNLOAD,
        TEMPLATES,
        PUBLICSHARE,
        DOCUMENTS,
        MUSIC,
        PICTURES,
        VIDEOS
    }

    /**
     * Tests whether an action is supported on the current platform.
     *
     * <p>Even when the platform supports an action, a file or URI may not have
     * a registered application for the action. For example, most of the
     * platforms support the {@link Desktop.Action#OPEN} action. But for a
     * specific file, there may not be an application registered to open it. In
     * this case, {@link
     * #isSupported} may return {@code true}, but the corresponding action
     * method will throw an {@link IOException}.
     *
     * @param action the specified {@link Action}
     * @return <code>true</code> if the specified action is supported on the
     * current platform; <code>false</code> otherwise
     * @see Desktop.Action
     */
    boolean isSupported(@Nonnull Action action);

    /**
     * Launches the associated application to open the file.
     *
     * <p> If the specified file is a directory, the file manager of the current
     * platform is launched to open it.
     *
     * @param file the file to be opened with the associated application
     * @throws NullPointerException if {@code file} is {@code null}
     * @throws IllegalArgumentException if the specified file doesn't exist
     * @throws UnsupportedOperationException if the current platform does not
     * support the {@link Desktop.Action#OPEN} action
     * @throws IOException if the specified file has no associated application
     * or the associated application fails to be launched
     * @throws SecurityException if a security manager exists and its
     * {@link java.lang.SecurityManager#checkRead(java.lang.String)} method
     * denies read access to the file, or it denies the
     * <code>AWTPermission("showWindowWithoutWarningBanner")</code> permission,
     * or the calling thread is not allowed to create a subprocess
     * @see java.awt.AWTPermission
     */
    void open(@Nonnull File file) throws IOException;

    /**
     * Launches the associated editor application and opens a file for editing.
     *
     * @param file the file to be opened for editing
     * @throws NullPointerException if the specified file is {@code null}
     * @throws IllegalArgumentException if the specified file doesn't exist
     * @throws UnsupportedOperationException if the current platform does not
     * support the {@link Desktop.Action#EDIT} action
     * @throws IOException if the specified file has no associated editor, or
     * the associated application fails to be launched
     * @throws SecurityException if a security manager exists and its
     * {@link java.lang.SecurityManager#checkRead(java.lang.String)} method
     * denies read access to the file, or {@link
     * java.lang.SecurityManager#checkWrite(java.lang.String)} method denies
     * write access to the file, or it denies the
     * <code>AWTPermission("showWindowWithoutWarningBanner")</code> permission,
     * or the calling thread is not allowed to create a subprocess
     * @see java.awt.AWTPermission
     */
    void edit(@Nonnull File file) throws IOException;

    /**
     * Prints a file with the native desktop printing facility, using the
     * associated application's print command.
     *
     * @param file the file to be printed
     * @throws NullPointerException if the specified file is {@code
     * null}
     * @throws IllegalArgumentException if the specified file doesn't exist
     * @throws UnsupportedOperationException if the current platform does not
     * support the {@link Desktop.Action#PRINT} action
     * @throws IOException if the specified file has no associated application
     * that can be used to print it
     * @throws SecurityException if a security manager exists and its
     * {@link java.lang.SecurityManager#checkRead(java.lang.String)} method
     * denies read access to the file, or its {@link
     * java.lang.SecurityManager#checkPrintJobAccess()} method denies the
     * permission to print the file, or the calling thread is not allowed to
     * create a subprocess
     */
    void print(@Nonnull File file) throws IOException;

    /**
     * Launches the default browser to display a {@code URI}. If the default
     * browser is not able to handle the specified {@code URI}, the application
     * registered for handling {@code URIs} of the specified type is invoked.
     * The application is determined from the protocol and path of the
     * {@code URI}, as defined by the {@code URI} class. <p> If the calling
     * thread does not have the necessary permissions, and this is invoked from
     * within an applet, {@code AppletContext.showDocument()} is used.
     * Similarly, if the calling does not have the necessary permissions, and
     * this is invoked from within a Java Web Started application,
     * {@code BasicService.showDocument()} is used.
     *
     * @param uri the URI to be displayed in the user default browser
     * @throws NullPointerException if {@code uri} is {@code null}
     * @throws UnsupportedOperationException if the current platform does not
     * support the {@link Desktop.Action#BROWSE} action
     * @throws IOException if the user default browser is not found, or it fails
     * to be launched, or the default handler application failed to be launched
     * @throws SecurityException if a security manager exists and it denies the
     * <code>AWTPermission("showWindowWithoutWarningBanner")</code> permission,
     * or the calling thread is not allowed to create a subprocess; and not
     * invoked from within an applet or Java Web Started application
     * @throws IllegalArgumentException if the necessary permissions are not
     * available and the URI can not be converted to a {@code URL}
     * @see java.net.URI
     * @see java.awt.AWTPermission
     * @see java.applet.AppletContext
     */
    void browse(@Nonnull URI uri) throws IOException;

    /**
     * Launches the mail composing window of the user default mail client.
     *
     * @throws UnsupportedOperationException if the current platform does not
     * support the {@link Desktop.Action#MAIL} action
     * @throws IOException if the user default mail client is not found, or it
     * fails to be launched
     * @throws SecurityException if a security manager exists and it denies the
     * <code>AWTPermission("showWindowWithoutWarningBanner")</code> permission,
     * or the calling thread is not allowed to create a subprocess
     * @see java.awt.AWTPermission
     */
    void mail() throws IOException;

    /**
     * Launches the mail composing window of the user default mail client,
     * filling the message fields specified by a {@code
     * mailto:} URI.
     *
     * <p> A
     * <code>mailto:</code> URI can specify message fields including
     * <i>"to"</i>, <i>"cc"</i>, <i>"subject"</i>, <i>"body"</i>, etc. See <a
     * href="http://www.ietf.org/rfc/rfc2368.txt">The mailto URL scheme (RFC
     * 2368)</a> for the {@code mailto:} URI specification details.
     *
     * @param mailtoURI the specified {@code mailto:} URI
     * @throws NullPointerException if the specified URI is {@code
     * null}
     * @throws IllegalArgumentException if the URI scheme is not
     * <code>"mailto"</code>
     * @throws UnsupportedOperationException if the current platform does not
     * support the {@link Desktop.Action#MAIL} action
     * @throws IOException if the user default mail client is not found or fails
     * to be launched
     * @throws SecurityException if a security manager exists and it denies the
     * <code>AWTPermission("showWindowWithoutWarningBanner")</code> permission,
     * or the calling thread is not allowed to create a subprocess
     * @see java.net.URI
     * @see java.awt.AWTPermission
     */
    void mail(@Nonnull URI mailtoURI) throws IOException;

    /**
     * Launches the default file manager and select the specified folder, file
     * or application .
     *
     * @param file the file to be shown
     * @throws UnsupportedOperationException if the current platform does not
     * support the {@link Desktop.Action#SHOW_IN_FOLDER} action
     * @throws IOException if the user default file manager is not found or
     * fails to be launched
     */
    void showInFolder(@Nonnull File file) throws IOException;

    /**
     * Moves the given files to the system trash, if one is available.
     *
     * @param files the files to be moved to the system trash
     * @throws UnsupportedOperationException if the current platform does not
     * support the {@link Desktop.Action#MOVE_TO_TRASH} action
     * @throws IOException if the operation failed
     */
    void moveToTrash(@Nonnull File... files) throws IOException;

    /**
     * Returns the path of a known folder as a {@link File}.
     *
     * @param knownFolder
     * @return a <code>File</code> object if the folder has been found and
     * exist, null otherwise
     * @throws IOException if the operation failed
     * @throws UnsupportedOperationException if the current platform does not
     * support the {@link Desktop.Action#KNOWN_FOLDER_LOOKUP} action
     */
    @Nullable
    File getKnownFolderPath(@Nonnull KnownFolder knownFolder) throws IOException;

    @Deprecated
    @Nullable
    File getKnownFolder(@Nonnull KnownFolder knownFolder);

    /**
     * Performs a file search using the default desktop search engine.
     *
     * @param query
     * @return a non-null array of files
     * @throws UnsupportedOperationException if the current platform does not
     * support the {@link Desktop.Action#SEARCH} action
     * @throws IOException if the user default desktop search engine fails to be
     * launched
     */
    @Nonnull
    File[] search(@Nonnull String query) throws IOException;

    /**
     * A factory used to create a Desktop implementation.<p>A Desktop
     * implementation is never created directly but through a factory. The
     * factory gives also the type of support and therefore allows to select the
     * best Desktop implementation available.
     */
    public interface Factory {

        /**
         * Defines the type of support that a Desktop offers.
         */
        public enum SupportType {

            NONE,
            BASIC,
            GENERIC,
            SPECIFIC
        }

        /**
         * Gets the type of support for the current operating system.
         *
         * @param osArch
         * @param osName
         * @param osVersion
         * @return the type of support
         */
        @Nonnull
        SupportType getSupportType(String osArch, String osName, String osVersion);

        /**
         * Creates a new Desktop implementation.
         *
         * @param osArch
         * @param osName
         * @param osVersion
         * @return a non-null desktop implementation
         */
        @Nonnull
        Desktop create(String osArch, String osName, String osVersion);
    }
}
