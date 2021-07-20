/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Jean Palate
 */
@XmlType(name = XmlWorkspaceItem.NAME)
public class XmlWorkspaceItem {
    static final String NAME = "demetraGenericItemType";

    @XmlElement
    public String family;
    @XmlElement
    public String name;
    @XmlElement
    public String file;
    @XmlAttribute
    public boolean readOnly;
    @XmlElement
    public String comments;

    public XmlWorkspaceItem() { }
}
