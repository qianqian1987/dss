package eu.europa.esig.dss.pki.service;

import eu.europa.esig.dss.pki.builder.GenericBuilder;
import eu.europa.esig.dss.pki.db.JaxbCertEntityRepository;
import eu.europa.esig.dss.pki.dto.CertSubjectWrapperDTO;
import eu.europa.esig.dss.pki.factory.GenericFactory;
import eu.europa.esig.dss.pki.model.DBCertEntity;
import eu.europa.esig.dss.pki.utils.PKIUtils;
import eu.europa.esig.dss.pki.wrapper.CertificateWrapper;
import eu.europa.esig.dss.pki.wrapper.EntityId;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.pki.manifest.CRLType;
import eu.europa.esig.pki.manifest.CertificateType;
import eu.europa.esig.pki.manifest.EntityKey;
import eu.europa.esig.pki.manifest.KeyAlgo;
import eu.europa.esig.pki.manifest.Pki;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static eu.europa.esig.dss.pki.constant.Constant.CRL_EXTENSION;
import static eu.europa.esig.dss.pki.constant.Constant.CRL_PATH;
import static eu.europa.esig.dss.pki.constant.Constant.CRT_EXTENSION;
import static eu.europa.esig.dss.pki.constant.Constant.CRT_PATH;
import static eu.europa.esig.dss.pki.constant.Constant.CUSTOM_URL_PREFIX;
import static eu.europa.esig.dss.pki.constant.Constant.EMPTY_URL_PREFIX;
import static eu.europa.esig.dss.pki.constant.Constant.EXTENDED_URL_PREFIX;
import static eu.europa.esig.dss.pki.constant.Constant.HOST;
import static eu.europa.esig.dss.pki.constant.Constant.OCSP_PATH;
import static eu.europa.esig.dss.pki.constant.Constant.country;
import static eu.europa.esig.dss.pki.constant.Constant.organisation;
import static eu.europa.esig.dss.pki.constant.Constant.organisationUnit;


public class PKICertificationEntityBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(PKICertificationEntityBuilder.class);

    private static JaxbCertEntityRepository entityService;
    private static PKICertificationEntityBuilder instance;


    public static PKICertificationEntityBuilder getInstance() {
        if (instance == null) {
            synchronized (PKICertificationEntityBuilder.class) {
                entityService = GenericFactory.getInstance().create(JaxbCertEntityRepository.class);
                instance = new PKICertificationEntityBuilder();
            }
        }
        return instance;
    }

    private PKICertificationEntityBuilder() {

    }

    /**
     * Initializes the certificate entities and their related information using the provided PKIs.
     *
     * @throws Exception if an error occurs during initialization.
     */
    public void persistCertEntity(Pki pki) {

        Map<EntityId, X500Name> x500names = new HashMap<>();
        Map<String, KeyPair> keyPairs = new HashMap<>();
        Map<EntityId, DBCertEntity> entities = new HashMap<>();

        LOG.info("PKI {} : {} certificates", pki.getName(), pki.getCertificate().size());

        for (CertificateType certType : pki.getCertificate()) {

            LOG.info("Init '{}' ...", certType.getSubject());

            DBCertEntity issuer = getIssuer(entities, certType.getIssuer());
            String issuerName = issuer != null ? issuer.getSubject() : certType.getSubject();
            CertificateWrapper wrapper = new CertificateWrapper(certType, issuerName);
            try {
                KeyPair subjectKeyPair = getKeyPair(keyPairs, wrapper.getSubject(), wrapper.getKeyAlgo());
                KeyPair issuerKeyPair = wrapper.isSelfSigned() ? subjectKeyPair : getKeyPair(keyPairs, getIssuerSubject(entities, wrapper.getIssuer()), wrapper.getKeyAlgo());

                X500Name subjectX500Name = getX500NameSubject(x500names, wrapper, new CertSubjectWrapperDTO(certType, pki.getCountry(), pki.getOrganization()));
                X500Name issuerX500Name = getX500NameIssuer(x500names, wrapper.getIssuer());
                X509CertBuilder certBuilder = getX509CertBuilder(wrapper, subjectKeyPair, issuerKeyPair, subjectX500Name, issuerX500Name);
                X509CertificateHolder certificateHolder = certBuilder.build(BigInteger.valueOf(wrapper.getSerialNumber()), wrapper.getNotBefore(), wrapper.getNotAfter());

                DBCertEntity dbCertEntity = entityService.save(buildDbCertEntity(wrapper, certificateHolder, subjectKeyPair, entities, pki.getName()));
                saveEntity(entities, wrapper.getKey(), dbCertEntity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            LOG.info("Creation of '{}' : DONE", certType.getSubject());
        }
    }


    private DBCertEntity buildDbCertEntity(CertificateWrapper wrapper, X509CertificateHolder certificateHolder, KeyPair subjectKeyPair, Map<EntityId, DBCertEntity> entities, String pkiName) {

        boolean selfSign = wrapper.getIssuer().equals(wrapper.getKey());
        DBCertEntity dbCertEntity;
        try {
            dbCertEntity = GenericBuilder.of(DBCertEntity::new)
                    .with(DBCertEntity::setSubject, PKIUtils.getCommonName(certificateHolder))
                    .with(DBCertEntity::setSerialNumber, certificateHolder.getSerialNumber().longValue())
                    .with(DBCertEntity::setCertificateToken, DSSUtils.loadCertificate(certificateHolder.getEncoded()))
                    .with(DBCertEntity::setPrivateKey, subjectKeyPair.getPrivate().getEncoded())
                    .with(DBCertEntity::setPrivateKeyAlgo, subjectKeyPair.getPrivate().getAlgorithm())
                    .with(DBCertEntity::setRevocationDate, wrapper.getRevocationDate())
                    .with(DBCertEntity::setRevocationReason, wrapper.getRevocationReason())
                    .with(DBCertEntity::setSuspended, wrapper.isSuspended())
                    .with(DBCertEntity::setOcspResponder, getEntity(entities, wrapper.getOCSPResponder(), false))
                    .with(DBCertEntity::setTrustAnchor, wrapper.isTrustAnchor())
                    .with(DBCertEntity::setCa, wrapper.isCA())
                    .with(DBCertEntity::setTsa, wrapper.isTSA())
                    .with(DBCertEntity::setOcsp, wrapper.isOcspSigning())
                    .with(DBCertEntity::setToBeIgnored, wrapper.isToBeIgnored())
                    .with(DBCertEntity::setPkiName, pkiName)
                    .with(DBCertEntity::setPss, wrapper.isPSS())
                    .with(DBCertEntity::setDigestAlgo, wrapper.getDigestAlgo() != null ? wrapper.getDigestAlgo().value() : null)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (wrapper.isSelfSigned()) {
            dbCertEntity.setParent(dbCertEntity);
        } else {
            dbCertEntity.setParent(getEntity(entities, wrapper.getIssuer(), selfSign));
        }

        return dbCertEntity;
    }

    private X509CertBuilder getX509CertBuilder(CertificateWrapper wrapper, KeyPair subjectKeyPair, KeyPair issuerKeyPair, X500Name subjectX500Name, X500Name issuerX500Name) {
        X509CertBuilder certBuilder = new X509CertBuilder();
        certBuilder.subject(subjectX500Name, subjectKeyPair.getPublic());
        certBuilder.issuer(issuerX500Name, issuerKeyPair.getPrivate());

        certBuilder.digestAlgo(wrapper.getDigestAlgo());

        certBuilder.aia(getAiaUrl(wrapper.getAIA()));
        String urlCrl = getCrlUrl(wrapper.getCRL());
        certBuilder.crl(urlCrl);
        certBuilder.ocsp(getOcspUrl(wrapper.getOCSP()));

        certBuilder.keyUsage(wrapper.getKeyUsage());
        certBuilder.certificatePolicies(wrapper.getCertificatePolicies());
        certBuilder.qcStatementIds(wrapper.getQCStatementsIds());

        if (wrapper.isCA()) {
            certBuilder.ca();
        }
        if (wrapper.isTSA()) {
            certBuilder.timestamping();
        }
        if (wrapper.isOcspNoCheck()) {
            certBuilder.ocspNoCheck();
        }
        if (wrapper.isOcspSigning()) {
            certBuilder.ocspSigningExtension();
        }

        certBuilder.pss(wrapper.isPSS());
        return certBuilder;
    }

    /**
     * Retrieves the issuer certificate entity with the given entity key from the entities map.
     *
     * @param entities  The map of certificate entities, where the key is the EntityId and the value is the DBCertEntity.
     * @param entityKey The entity key for the issuer certificate.
     * @return The issuer certificate entity associated with the given entity key, or null if not found.
     */
    private DBCertEntity getIssuer(Map<EntityId, DBCertEntity> entities, EntityKey entityKey) {
        if (entityKey.getSerialNumber() != null) {
            return entities.get(new EntityId(entityKey));
        }
        return null;
    }

    /**
     * Retrieves the subject name of the certificate entity associated with the given EntityId from the entities map.
     *
     * @param entities The map of certificate entities, where the key is the EntityId and the value is the DBCertEntity.
     * @param key      The EntityId for the certificate entity.
     * @return The subject name of the certificate entity associated with the given EntityId.
     * @throws IllegalArgumentException if the certificate entity is not found in the entities map.
     */
    private String getIssuerSubject(Map<EntityId, DBCertEntity> entities, EntityId key) {
        DBCertEntity entity = entities.get(key);
        if (entity == null) {
            throw new IllegalArgumentException("Entity not found " + key);
        }
        return entity.getSubject();
    }

    private DBCertEntity getEntity(Map<EntityId, DBCertEntity> entities, EntityId key, boolean ignoreException) {
        if (key != null) {
            DBCertEntity certEntity = entities.get(key);
            if (certEntity == null && !ignoreException) {
                throw new IllegalArgumentException("Entity not found " + key);
            }
            return certEntity;
        }
        return null;
    }

    public String getCrlUrl(CRLType crlEntity) {
        if (crlEntity != null && crlEntity.getValue() != null) {
            if (crlEntity.getDate() == null) {
                return HOST + CRL_PATH + getCertStringUrl(crlEntity, EXTENDED_URL_PREFIX) + CRL_EXTENSION;
            } else {
                Date time = crlEntity.getDate().toGregorianCalendar().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
                String date = sdf.format(time);
                if (crlEntity.isFutur() == null) {
                    return HOST + CRL_PATH + date + "/" + crlEntity.getValue() + CRL_EXTENSION;
                } else {
                    return HOST + CRL_PATH + date + "/" + crlEntity.isFutur() + "/" + crlEntity.getValue() + CRL_EXTENSION;
                }
            }
        }
        return null;
    }

    private String getOcspUrl(EntityKey entityKey) {
        if (entityKey != null) {
            return HOST + OCSP_PATH + getCertStringUrl(entityKey, CUSTOM_URL_PREFIX);
        }
        return null;
    }

    private String getAiaUrl(EntityKey entityKey) {
        if (entityKey != null) {
            return HOST + CRT_PATH + getCertStringUrl(entityKey, EMPTY_URL_PREFIX) + CRT_EXTENSION;
        }
        return null;
    }

    private String getCertStringUrl(EntityKey entityKey, String urlPrefix) {
        return entityKey.getSerialNumber() != null ? urlPrefix + entityKey.getValue() + "/" + entityKey.getSerialNumber() : entityKey.getValue();
    }

    /**
     * Get issuer from x500names map
     *
     * @param x500names
     * @param key
     * @throws IllegalStateException X500Name not found in map for given key
     */
    private X500Name getX500NameIssuer(Map<EntityId, X500Name> x500names, EntityId key) {
        if (x500names.containsKey(key)) {
            return x500names.get(key);
        }
        throw new IllegalStateException("EntityId not found : " + key);
    }

    /**
     * Initialize subject based on given subject/organization (optional.)/country (optional.)
     *
     * @param x500Names          a map between {@link EntityId} and {@link X500Name}
     * @param certificateWrapper {@link CertificateWrapper}
     * @param subjectWrapper     {@link CertSubjectWrapperDTO}
     * @throws IllegalStateException Common name is null
     */
    private X500Name getX500NameSubject(Map<EntityId, X500Name> x500Names, CertificateWrapper certificateWrapper, CertSubjectWrapperDTO subjectWrapper) {
        EntityId key = certificateWrapper.getKey();
        if (x500Names.containsKey(key)) {
            return x500Names.get(key);
        } else {
            if (subjectWrapper.getCommonName() == null) {
                throw new IllegalStateException("Missing common name for " + key);
            }

            String tmpCountry;
            if (!Utils.isStringEmpty(subjectWrapper.getCountry())) {
                tmpCountry = subjectWrapper.getCountry();
            } else {
                tmpCountry = country;
            }

            String tmpOrganisation;
            if (!Utils.isStringEmpty(subjectWrapper.getOrganization())) {
                tmpOrganisation = subjectWrapper.getOrganization();
            } else {
                tmpOrganisation = organisation;
            }

            X500Name x500Name = new X500NameBuilder().commonName(subjectWrapper.getCommonName()).pseudo(subjectWrapper.getPseudo()).country(tmpCountry).organisation(tmpOrganisation).organisationUnit(organisationUnit).build();
            x500Names.put(key, x500Name);
            x500Names.put(new EntityId(certificateWrapper.getSubject(), null), x500Name);
            return x500Name;
        }
    }

    private KeyPair getKeyPair(Map<String, KeyPair> keyPairs, String subject, KeyAlgo algo) throws GeneralSecurityException {
        if (keyPairs.containsKey(subject)) {
            return keyPairs.get(subject);
        } else {
            KeyPair keyPair = KeyPairBuilder.build(algo);
            keyPairs.put(subject, keyPair);
            return keyPair;
        }
    }

    private void saveEntity(Map<EntityId, DBCertEntity> entities, EntityId key, DBCertEntity entity) {
        entities.put(key, entity);
        entities.put(new EntityId(entity.getSubject(), null), entity);
    }

}
