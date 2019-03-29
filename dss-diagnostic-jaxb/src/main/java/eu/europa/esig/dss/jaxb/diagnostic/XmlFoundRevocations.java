//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.03.29 at 08:15:50 AM CET 
//


package eu.europa.esig.dss.jaxb.diagnostic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RelatedRevocation" type="{http://dss.esig.europa.eu/validation/diagnostic}RelatedRevocation" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="OrphanRevocationRefs" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="RevocationRef" type="{http://dss.esig.europa.eu/validation/diagnostic}RevocationRef" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "relatedRevocation",
    "orphanRevocationRefs"
})
public class XmlFoundRevocations implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "RelatedRevocation")
    protected List<XmlRelatedRevocation> relatedRevocation;
    @XmlElementWrapper(name = "OrphanRevocationRefs")
    @XmlElement(name = "RevocationRef", namespace = "http://dss.esig.europa.eu/validation/diagnostic")
    protected List<XmlRevocationRef> orphanRevocationRefs;

    /**
     * Gets the value of the relatedRevocation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relatedRevocation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelatedRevocation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XmlRelatedRevocation }
     * 
     * 
     */
    public List<XmlRelatedRevocation> getRelatedRevocation() {
        if (relatedRevocation == null) {
            relatedRevocation = new ArrayList<XmlRelatedRevocation>();
        }
        return this.relatedRevocation;
    }

    public List<XmlRevocationRef> getOrphanRevocationRefs() {
        if (orphanRevocationRefs == null) {
            orphanRevocationRefs = new ArrayList<XmlRevocationRef>();
        }
        return orphanRevocationRefs;
    }

    public void setOrphanRevocationRefs(List<XmlRevocationRef> orphanRevocationRefs) {
        this.orphanRevocationRefs = orphanRevocationRefs;
    }

}
