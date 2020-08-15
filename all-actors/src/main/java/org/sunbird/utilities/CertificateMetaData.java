package org.sunbird.utilities;

import java.util.Map;

public class CertificateMetaData {
    private String certificateName;
    private String certificateDescription;
    private String recipientName;
    private String recipientId;
    private String issuedDate;
    private String expiryDate;
    private String signatory0Image;
    private String signatory0Designation;
    private String signatory1Image;
    private String signatory1Designation;
    private String courseName;
    private String qrCodeUrl;
    private Map<String, Object> related;

    private CertificateMetaData(CertificateMetaData.Builder builder) {
        this.certificateName = builder.certificateName;
        this.certificateDescription = builder.certificateDescription;
        this.recipientName = builder.recipientName;
        this.recipientId = builder.recipientId;
        this.issuedDate = builder.issuedDate;
        this.expiryDate = builder.expiryDate;
        this.signatory0Image = builder.signatory0Image;
        this.signatory0Designation = builder.signatory0Designation;
        this.signatory1Image = builder.signatory1Image;
        this.signatory1Designation = builder.signatory1Designation;
        this.courseName = builder.courseName;
        this.qrCodeUrl = builder.qrCodeUrl;
        this.related = builder.related;
    }

    public String getCertificateName() {
        return certificateName;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public String getCertificateDescription() {
        return certificateDescription;
    }

    public void setCertificateDescription(String certificateDescription) {
        this.certificateDescription = certificateDescription;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getSignatory0Image() {
        return signatory0Image;
    }

    public void setSignatory0Image(String signatory0Image) {
        this.signatory0Image = signatory0Image;
    }

    public String getSignatory0Designation() {
        return signatory0Designation;
    }

    public void setSignatory0Designation(String signatory0Designation) {
        this.signatory0Designation = signatory0Designation;
    }

    public String getSignatory1Image() {
        return signatory1Image;
    }

    public void setSignatory1Image(String signatory1Image) {
        this.signatory1Image = signatory1Image;
    }

    public String getSignatory1Designation() {
        return signatory1Designation;
    }

    public void setSignatory1Designation(String signatory1Designation) {
        this.signatory1Designation = signatory1Designation;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public Map<String, Object> getRelated() {
        return related;
    }

    public void setRelated(Map<String, Object> related) {
        this.related = related;
    }

    public static class Builder {

        private String certificateName;
        private String certificateDescription;
        private String recipientName;
        private String recipientId;
        private String issuedDate;
        private String expiryDate;
        private String signatory0Image;
        private String signatory0Designation;
        private String signatory1Image;
        private String signatory1Designation;
        private String courseName;
        private String qrCodeUrl;
        private  Map<String, Object> related;

        public CertificateMetaData.Builder setCertificateName(String certificateName) {
            this.certificateName = certificateName;
            return this;
        }

        public CertificateMetaData.Builder setCertificateDescription(String certificateDescription) {
            this.certificateDescription = certificateDescription;
            return this;
        }

        public CertificateMetaData.Builder setRecipientName(String recipientName) {
            this.recipientName = recipientName;
            return this;
        }


        public CertificateMetaData.Builder setRecipientId(String recipientId) {
            this.recipientId = recipientId;
            return this;
        }


        public CertificateMetaData.Builder setIssuedDate(String issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        public CertificateMetaData.Builder setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }


        public CertificateMetaData.Builder setSignatory0Image(String signatory0Image) {
            this.signatory0Image = signatory0Image;
            return this;
        }

        public CertificateMetaData.Builder setSignatory0Designation(String signatory0Designation) {
            this.signatory0Designation = signatory0Designation;
            return this;
        }

        public CertificateMetaData.Builder setSignatory1Image(String signatory1Image) {
            this.signatory1Image = signatory1Image;
            return this;
        }


        public CertificateMetaData.Builder setSignatory1Designation(String signatory1Designation) {
            this.signatory1Designation = signatory1Designation;
            return this;
        }


        public CertificateMetaData.Builder setCourseName(String courseName) {
            this.courseName = courseName;
            return this;
        }

        public CertificateMetaData.Builder setQrCodeUrl(String qrCodeUrl) {
            this.qrCodeUrl = qrCodeUrl;
            return this;
        }

        public CertificateMetaData.Builder setRelated(Map<String, Object> related) {
            this.related = related;
            return this;
        }

        public CertificateMetaData build() {
            CertificateMetaData CertificateMetaData = new CertificateMetaData(this);
            return CertificateMetaData;

        }

    }

}
