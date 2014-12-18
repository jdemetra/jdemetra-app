/**
 * This package serves as a simple facade for accessing the features of the user's desktop.
 * 
 * <p>It is designed be a drop-in replacement of the
 * {@code java.awt.Desktop} API.<br>It also brings some useful enhancements: 
 * <ul>
 * <li>it is an interface so it allows dependency injection and therefore eases testing</li>
 * <li>the platform-specific implementation is plugged in at runtime</li>
 * <li>you can add your own implementation or supersede an existing one</li>
 * <li>it adds some features such as 
 * <a href="http://en.wikipedia.org/wiki/Desktop_search">desktop search</a>, 
 * retrieval of <a href="http://en.wikipedia.org/wiki/Special_folder">known folders</a> (download, movies, music, ...) 
 * and <a href="http://en.wikipedia.org/wiki/Trash_(computing)">trash</a> support</li> 
 * </ul>
 * <p>Typical usage:<p>
 * <code>
 * Desktop desktop = DesktopManager.get();<br>
 * File folder = desktop.getKnownFolder(KnownFolder.DOWNLOAD);
 * </code>
 * 
 * @see java.awt.Desktop
 * @author Philippe Charles
 */
package ec.util.desktop;
