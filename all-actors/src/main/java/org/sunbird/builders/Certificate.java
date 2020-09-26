package org.sunbird.builders;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * this is a builder class to build a Certificate object needs to be inserted into ES
 * @author anmolgupta
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Certificate {

    private String id;
    private String pdfUrl;
    private String jsonUrl;
    private Recipient recipient;
    private Map<String, Object> data;
    private Map<String, Object> related;
    private String createdBy;
    private String updatedBy;
    private boolean isRevoked;
    private String reason;
    private String accessCode;

    public Certificate(){}

    private Certificate(Builder builder) {
        this.id = builder.id;
        this.pdfUrl = builder.pdfUrl;
        this.jsonUrl = builder.jsonUrl;
        this.recipient = builder.recipient;
        this.data = builder.data;
        this.related = builder.related;
        this.createdBy = builder.createdBy;
        this.updatedBy = builder.updatedBy;
        this.isRevoked = builder.isRevoked;
        this.accessCode= builder.accessCode;
        this.reason=builder.reason;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public String getReason() {
        return reason;
    }

    public String getId() {
        return id;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getJsonUrl() {
        return jsonUrl;
    }


    public Map<String, Object> getData() {
        return data;
    }

    public Map<String, Object> getRelated() {
        return related;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    @JsonProperty(value = "isRevoked")
    public boolean isRevoked() {
        return isRevoked;
    }

    @Override
    public String toString() {
        return "Certificate{" +
                "id='" + id + '\'' +
                ", pdfUrl='" + pdfUrl + '\'' +
                ", jsonUrl='" + jsonUrl + '\'' +
                ", recipient=" + recipient +
                ", data=" + data +
                ", related=" + related +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", isRevoked=" + isRevoked +
                ", reason='" + reason + '\'' +
                ", accessCode='" + accessCode + '\'' +
                '}';
    }

    public static class Builder {


        private String id;
        private String pdfUrl;
        private String jsonUrl;
        private Recipient recipient;
        private Map<String, Object> data;
        private Map<String, Object> related;
        private String createdBy;
        private String updatedBy;
        private boolean isRevoked;
        private String accessCode;
        private String reason;

        public Builder setReason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder setData(Map<String, Object> data) {
            this.data = data;
            return this;
        }

        public Builder setRecipient(Recipient recipient) {
            this.recipient = recipient;
            return this;
        }


        public Builder setAccessCode(String accessCode) {
            this.accessCode = accessCode;
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setPdfUrl(String pdfUrl) {
            this.pdfUrl = pdfUrl;
            return this;

        }

        public Builder setJsonUrl(String jsonUrl) {
            this.jsonUrl = jsonUrl;
            return this;

        }
        public Builder setRelated(Map<String, Object> related) {
            this.related = related;
            return this;

        }

        public Builder setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder setUpdatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }


        public Builder setRevoked(boolean revoked) {
            isRevoked = revoked;
            return this;

        }
        public Certificate build(){
            Certificate certificate=new Certificate(this);
            return certificate;

        }
    }




}
