package eu.europa.esig.dss.asic.common.validation;

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.EvidenceRecordWrapper;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlContainerInfo;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDigestMatcher;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignatureScope;
import eu.europa.esig.dss.diagnostic.jaxb.XmlTimestampedObject;
import eu.europa.esig.dss.enumerations.DigestMatcherType;
import eu.europa.esig.dss.model.ReferenceValidation;
import eu.europa.esig.dss.spi.x509.tsp.TimestampToken;
import eu.europa.esig.dss.spi.x509.tsp.TimestampedReference;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.evidencerecord.EvidenceRecord;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractASiCWithAsn1EvidenceRecordTestValidation extends AbstractASiCWithEvidenceRecordTestValidation {

    @Override
    protected void checkDetachedEvidenceRecords(List<EvidenceRecord> detachedEvidenceRecords) {
        assertTrue(Utils.isCollectionNotEmpty(detachedEvidenceRecords));

        for (EvidenceRecord evidenceRecord : detachedEvidenceRecords) {
            List<ReferenceValidation> referenceValidationList = evidenceRecord.getReferenceValidation();
            for (ReferenceValidation referenceValidation : referenceValidationList) {
                if (allArchiveDataObjectsProvidedToValidation() ||
                        DigestMatcherType.EVIDENCE_RECORD_ORPHAN_REFERENCE != referenceValidation.getType()) {
                    assertTrue(referenceValidation.isFound());
                    assertTrue(referenceValidation.isIntact());
                }
            }

            List<TimestampedReference> timestampedReferences = evidenceRecord.getTimestampedReferences();
            assertTrue(Utils.isCollectionNotEmpty(timestampedReferences));

            int tstCounter = 0;

            List<TimestampToken> timestamps = evidenceRecord.getTimestamps();
            for (TimestampToken timestampToken : timestamps) {
                assertTrue(timestampToken.isProcessed());
                assertTrue(timestampToken.isMessageImprintDataFound());
                assertTrue(timestampToken.isMessageImprintDataIntact());

                if (tstCounter > 0) {
                    List<ReferenceValidation> tstReferenceValidationList = timestampToken.getReferenceValidations();
                    assertTrue(Utils.isCollectionNotEmpty(tstReferenceValidationList));

                    boolean archiveTstDigestFound = false;
                    boolean archiveTstSequenceDigestFound = false;
                    for (ReferenceValidation referenceValidation : tstReferenceValidationList) {
                        if (DigestMatcherType.EVIDENCE_RECORD_ARCHIVE_TIME_STAMP.equals(referenceValidation.getType())) {
                            archiveTstDigestFound = true;
                        } else if (DigestMatcherType.EVIDENCE_RECORD_ARCHIVE_OBJECT.equals(referenceValidation.getType())) {
                            archiveTstSequenceDigestFound = true;
                        } else if (allArchiveDataObjectsProvidedToValidation() ||
                                DigestMatcherType.EVIDENCE_RECORD_ORPHAN_REFERENCE != referenceValidation.getType()) {
                            assertTrue(referenceValidation.isFound());
                            assertTrue(referenceValidation.isIntact());
                        }
                    }

                    if (tstReferenceValidationList.size() == 1) {
                        assertTrue(archiveTstDigestFound);
                    } else {
                        assertTrue(archiveTstSequenceDigestFound);
                    }

                }

                ++tstCounter;
            }
        }
    }

    @Override
    protected void checkEvidenceRecordTimestamps(DiagnosticData diagnosticData) {
        XmlContainerInfo containerInfo = diagnosticData.getContainerInfo();
        List<String> contentFiles = containerInfo.getContentFiles();

        for (EvidenceRecordWrapper evidenceRecord : diagnosticData.getEvidenceRecords()) {

            int tstCounter = 0;

            List<TimestampWrapper> timestamps = evidenceRecord.getTimestampList();
            for (TimestampWrapper timestamp : timestamps) {
                assertTrue(timestamp.isMessageImprintDataFound());
                assertTrue(timestamp.isMessageImprintDataIntact());
                assertTrue(timestamp.isSignatureIntact());
                assertTrue(timestamp.isSignatureValid());

                List<XmlSignatureScope> timestampScopes = timestamp.getTimestampScopes();
                assertEquals(contentFiles.size(), timestampScopes.size());

                List<XmlTimestampedObject> timestampedObjects = timestamp.getTimestampedObjects();
                assertTrue(Utils.isCollectionNotEmpty(timestampedObjects));

                if (tstCounter > 0) {
                    List<XmlDigestMatcher> tstDigestMatcherList = timestamp.getDigestMatchers();
                    assertTrue(Utils.isCollectionNotEmpty(tstDigestMatcherList));

                    boolean archiveTstDigestFound = false;
                    for (XmlDigestMatcher digestMatcher : tstDigestMatcherList) {
                        if (DigestMatcherType.EVIDENCE_RECORD_ARCHIVE_TIME_STAMP.equals(digestMatcher.getType())) {
                            archiveTstDigestFound = true;
                        } else if ((allArchiveDataObjectsProvidedToValidation() && tstCoversOnlyCurrentHashTreeData()) ||
                                DigestMatcherType.EVIDENCE_RECORD_ORPHAN_REFERENCE != digestMatcher.getType()) {
                            assertTrue(digestMatcher.isDataFound());
                            assertTrue(digestMatcher.isDataFound());
                        }
                    }

                    if (tstDigestMatcherList.size() == 2) {
                        assertTrue(archiveTstDigestFound || !tstCoversOnlyCurrentHashTreeData());
                    }
                }

                ++tstCounter;
            }
        }
    }

}
