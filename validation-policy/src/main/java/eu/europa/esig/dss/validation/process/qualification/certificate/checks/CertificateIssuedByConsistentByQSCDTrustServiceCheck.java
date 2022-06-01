package eu.europa.esig.dss.validation.process.qualification.certificate.checks;

import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationCertificateQualification;
import eu.europa.esig.dss.diagnostic.TrustedServiceWrapper;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.policy.jaxb.LevelConstraint;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.process.ChainItem;

import java.util.List;

/**
 * Checks if there are consistent by QSCD TrustedServices issues the certificate in question at control time
 *
 */
public class CertificateIssuedByConsistentByQSCDTrustServiceCheck extends ChainItem<XmlValidationCertificateQualification> {

    /** List of consistent Trusted Services issued the certificate at control time */
    private final List<TrustedServiceWrapper> trustServicesAtTime;

    /**
     * Default constructor
     *
     * @param i18nProvider {@link I18nProvider}
     * @param result {@link XmlValidationCertificateQualification}
     * @param trustServicesAtTime a list of {@link TrustedServiceWrapper}
     * @param constraint {@link LevelConstraint}
     */
    public CertificateIssuedByConsistentByQSCDTrustServiceCheck(I18nProvider i18nProvider,
                XmlValidationCertificateQualification result, List<TrustedServiceWrapper> trustServicesAtTime,
                LevelConstraint constraint) {
        super(i18nProvider, result, constraint);

        this.trustServicesAtTime = trustServicesAtTime;
    }

    @Override
    protected boolean process() {
        return Utils.isCollectionNotEmpty(trustServicesAtTime);
    }

    @Override
    protected MessageTag getMessageTag() {
        return MessageTag.QUAL_HAS_CONSISTENT_BY_QSCD;
    }

    @Override
    protected MessageTag getErrorMessageTag() {
        return MessageTag.QUAL_HAS_CONSISTENT_BY_QSCD_ANS;
    }

    @Override
    protected Indication getFailedIndicationForConclusion() {
        return Indication.FAILED;
    }

    @Override
    protected SubIndication getFailedSubIndicationForConclusion() {
        return null;
    }

}
