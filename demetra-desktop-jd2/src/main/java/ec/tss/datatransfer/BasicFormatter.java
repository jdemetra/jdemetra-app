/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.tss.datatransfer;

import java.awt.datatransfer.DataFlavor;

/**
 * * @author Palate Jean
 */
class BasicFormatter {

    // Dataflavors
    /**
     *
     */
    static final DataFlavor TEXT = new DataFlavor(
	    "text/plain;charset=utf-16;class=java.lang.String", "Text");
    /**
     *
     */
    static final DataFlavor DEMETRA;

    static {
	DataFlavor flavor = null;
	flavor = null;
	try {
	    flavor = java.awt.datatransfer.SystemFlavorMap
		    .decodeDataFlavor("XML Demetra");
	} catch (ClassNotFoundException ex) {
	}
	if (flavor == null) {
	    java.awt.datatransfer.SystemFlavorMap table = (java.awt.datatransfer.SystemFlavorMap) java.awt.datatransfer.SystemFlavorMap
		    .getDefaultFlavorMap();
	    flavor = new DataFlavor("xml/x;class=\"[B\"",
		    "XML Demetra");
	    table.addUnencodedNativeForFlavor(flavor, "XML Demetra");
            table.addFlavorForUnencodedNative("XML Demetra", flavor);
	}
	DEMETRA = flavor;
    }

}
