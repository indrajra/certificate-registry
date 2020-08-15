package org.sunbird.utilities;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.sunbird.BaseException;
import org.sunbird.JsonKeys;
import org.sunbird.message.IResponseMessage;
import org.sunbird.message.Localizer;
import org.sunbird.message.ResponseCode;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TemplateVarResolver {

    private ObjectMapper objectMapper = new ObjectMapper();
    private static Localizer localizer = Localizer.getInstance();


    public TemplateVarResolver() {
    }

    public CertificateMetaData generateCertMetaData(Map<String, Object> certificateData) throws BaseException {
        Map<String, Object> data;
        CertificateMetaData certificateMetaData;
        try {
            data = objectMapper.readValue((String) certificateData.get(JsonKeys.DATA),
                    new TypeReference<Map<String, Object>>() {
                    });
            Map<String, Object> badge = (Map<String, Object>) data.get(JsonKeys.BADGE);
            Map<String, Object> recipient = (Map<String, Object>) data.get(JsonKeys.RECIPIENT);
            List<Map<String, Object>> signatory = (List<Map<String, Object>>) data.get(JsonKeys.SIGNATORY);
            certificateMetaData = new CertificateMetaData.Builder().setCertificateName((String) badge.get(JsonKeys.NAME))
                    .setCertificateDescription((String) badge.get(JsonKeys.DESCRIPTION))
                    .setCourseName((String) badge.get(JsonKeys.NAME))
                    .setRecipientName((String) recipient.get(JsonKeys.NAME))
                    .setRecipientId((String) recipient.get(JsonKeys.IDENTITY))
                    .setSignatory0Image(CollectionUtils.isNotEmpty(signatory) && signatory.size() >= 1 ?
                            (String) signatory.get(0).get(JsonKeys.IMAGE) : "")
                    .setSignatory0Designation(CollectionUtils.isNotEmpty(signatory) && signatory.size() >= 1 ?
                            (String) signatory.get(0).get(JsonKeys.DESIGNATION) : "")
                    .setSignatory1Image(CollectionUtils.isNotEmpty(signatory) && signatory.size() >= 2 ?
                            (String) signatory.get(1).get(JsonKeys.IMAGE) : "")
                    .setSignatory1Designation(CollectionUtils.isNotEmpty(signatory) && signatory.size() >= 2 ?
                            (String) signatory.get(1).get(JsonKeys.DESIGNATION) : "")
                    .setIssuedDate(getIssuedDate((String) data.get(JsonKeys.ISSUED_ON)))
                    .setQrCodeUrl((String) certificateData.get(JsonKeys.QR_CODE_URL))
                    .setRelated(objectMapper.readValue((String) certificateData.get(JsonKeys.RELATED),
                            new TypeReference<Map<String, Object>>() {
                            }))
                    .build();
        } catch (IOException e) {
            throw new BaseException(IResponseMessage.SERVER_ERROR, getLocalizedMessage(IResponseMessage.SERVER_ERROR, null),
                    ResponseCode.SERVER_ERROR.getCode());
        }

        return certificateMetaData;
    }

    private String getIssuedDate(String issuedOn) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String dateInFormat;
        try {
            Date parsedIssuedDate = simpleDateFormat.parse(issuedOn);
            DateFormat format = new SimpleDateFormat("dd MMMM yyy", Locale.getDefault());
            format.format(parsedIssuedDate);
            dateInFormat = format.format(parsedIssuedDate);
            return dateInFormat;
        } catch (ParseException e) {
            return null;
        }
    }

    private static String getLocalizedMessage(String key, Locale locale) {
        return localizer.getMessage(key, locale);
    }


}
