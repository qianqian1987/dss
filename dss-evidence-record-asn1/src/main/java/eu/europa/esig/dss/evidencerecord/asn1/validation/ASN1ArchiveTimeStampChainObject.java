package eu.europa.esig.dss.evidencerecord.asn1.validation;

import org.bouncycastle.asn1.tsp.ArchiveTimeStampChain;
import org.w3c.dom.Element;

import eu.europa.esig.dss.evidencerecord.common.validation.ArchiveTimeStampChainObject;

public class ASN1ArchiveTimeStampChainObject extends ArchiveTimeStampChainObject implements ASN1EvidenceRecordObject {

	/**
	 * The ASN1 Evidence Record representation of ArchiveTimeStampChain element
	 *
	 */
	private static final long serialVersionUID = 1027914551003735835L;

	/** The current Element */
    private final ArchiveTimeStampChain element;

    /** Order of the element */
    private int order;

    /**
     * Default constructor
     *
     * @param element {@link Element}
     */
    public ASN1ArchiveTimeStampChainObject(final ArchiveTimeStampChain element) {
        this.element = element;
    }

//    @Override
//    public ArchiveTimeStampChain getElement() {
//        return element;
//    }

    @Override
    public int getOrder() {
        return order;
    }

    /**
     * Sets order of the object within its parent
     *
     * @param order int value
     */
    public void setOrder(int order) {
        this.order = order;
    }


}
