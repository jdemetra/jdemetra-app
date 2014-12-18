/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.nbdemetra.ws.xml.compatibility;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Kristof Bayens
 */
@XmlType(name = XmlWksElement.NAME)
public class XmlWksElement {
    static final String NAME = "demetraItemType";

    @XmlElement
    public String name;
    @XmlElement
    public String file;
    @XmlAttribute
    public boolean readOnly;

    public XmlWksElement() { }
}
