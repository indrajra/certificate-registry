package org.sunbird.serviceimpl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.sunbird.BaseException;
import org.sunbird.CertVars;
import org.sunbird.JsonKeys;
import org.sunbird.builders.Certificate;
import org.sunbird.builders.Recipient;
import org.sunbird.message.IResponseMessage;
import org.sunbird.message.Localizer;
import org.sunbird.message.ResponseCode;
import org.sunbird.request.Request;
import org.sunbird.response.Response;
import org.sunbird.service.ICertService;
import org.sunbird.utilities.CertificateMetaData;
import org.sunbird.utilities.CertificateUtil;
import org.sunbird.utilities.ESResponseMapper;
import org.sunbird.utilities.TemplateVarResolver;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;


/**
 * this is an implementation class for the cert service , it will handel all the certificate related transactions
 * @author anmolgupta
 */
public class CertsServiceImpl implements ICertService {
    private static Logger logger = Logger.getLogger(CertsServiceImpl.class);
    private static Localizer localizer = Localizer.getInstance();
    private static ObjectMapper requestMapper = new ObjectMapper();
    static Map<String, String> headerMap = new HashMap<>();
    static {
        headerMap.put("Content-Type", "application/json");
    }

    @Override
    public Response delete(Request request) throws BaseException {
        Map<String, Object> certAddReqMap = request.getRequest();
        Response response = new Response();
        if(StringUtils.isNotBlank((String)certAddReqMap.get(JsonKeys.OLD_ID))){
            boolean bool = CertificateUtil.deleteRecord((String)certAddReqMap.get(JsonKeys.OLD_ID));
            response.getResult().put(JsonKeys.RESPONSE,bool);
            logger.info("CertsServiceImpl:delete Deleted the record from cert_registry table for id "+certAddReqMap.get(JsonKeys.OLD_ID));
        }
        return response;
    }

    @Override
    public String add(Request request) throws BaseException {
        Map<String,Object> reqMap = request.getRequest();
        if(isPresentRecipientIdAndCertId(request)){
            validateCertAndRecipientId(reqMap);
            deleteOldCertificate((String) reqMap.get(JsonKeys.OLD_ID));
        }
        Map<String, Object> certAddReqMap = request.getRequest();
        assureUniqueCertId((String) certAddReqMap.get(JsonKeys.ID));
        processRecord(certAddReqMap,(String) request.getContext().get(JsonKeys.VERSION));
        logger.info("CertsServiceImpl:add:record successfully processed with request:"+certAddReqMap.get(JsonKeys.ID));
        return (String)certAddReqMap.get(JsonKeys.ID);
    }

    private void deleteOldCertificate(String oldCertId) throws BaseException {
        CertificateUtil.deleteRecord(oldCertId);
    }

    private void validateCertAndRecipientId(Map<String,Object> reqMap) throws BaseException {
        String certId = (String) reqMap.get(JsonKeys.OLD_ID);
        String recipientId = (String) reqMap.get(JsonKeys.RECIPIENT_ID);
        Response response = CertificateUtil.getCertRecordByID(certId);
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) response.getResult().get(JsonKeys.RESPONSE);
        if(CollectionUtils.isNotEmpty(resultList)) {
            Map<String, Object> result = resultList.get(0);
            try {
                Map<String,Object> recipient = requestMapper.readValue((String)result.get(JsonKeys.RECIPIENT), Map.class);
                if(StringUtils.isNotBlank((String)recipient.get(JsonKeys.ID)) && !recipientId.equalsIgnoreCase((String)recipient.get(JsonKeys.ID))){
                    throw new BaseException(IResponseMessage.INVALID_REQUESTED_DATA,localizer.getMessage(IResponseMessage.INVALID_REQUESTED_DATA,null),ResponseCode.BAD_REQUEST.getCode());
                }
            }catch (IOException ex){
                throw new BaseException(IResponseMessage.SERVER_ERROR,getLocalizedMessage(IResponseMessage.SERVER_ERROR,null),ResponseCode.SERVER_ERROR.getCode());
            }
        }
    }

    private boolean isPresentRecipientIdAndCertId(Request request) {
        Map<String,Object> reqMap = request.getRequest();
        String certId = (String) reqMap.get(JsonKeys.OLD_ID);
        String recipientId = (String) reqMap.get(JsonKeys.RECIPIENT_ID);
        if(StringUtils.isNotBlank(certId) && StringUtils.isNotBlank(recipientId)){
            return true;
        }
        return false;
    }

    private void assureUniqueCertId(String certificatedId) throws BaseException {
        if (CertificateUtil.isIdPresent(certificatedId)) {
            logger.error(
                    "CertificateActor:addCertificate:provided certificateId exists in record:" + certificatedId);
            throw new BaseException(IResponseMessage.INVALID_REQUESTED_DATA, getLocalizedMessage(IResponseMessage.ID_ALREADY_EXISTS,null), ResponseCode.CLIENT_ERROR.getCode());
        }
        logger.info("CertificateActor:addCertificate:successfully certId not found in records creating new record");
    }


    private Response processRecord(Map<String, Object> certReqAddMap, String version) throws BaseException {
        Certificate certificate=getCertificate(certReqAddMap);
        if(version.equalsIgnoreCase(JsonKeys.VERSION_1)) {
            certificate.setPdfUrl((String)certReqAddMap.get(JsonKeys.PDF_URL));
        }
        Map<String,Object>recordMap= requestMapper.convertValue(certificate,Map.class);
        return CertificateUtil.insertRecord(recordMap);
    }
    private Certificate getCertificate(Map<String, Object> certReqAddMap) {
        Certificate certificate = new Certificate.Builder()
                .setId((String) certReqAddMap.get(JsonKeys.ID))
                .setData(getData(certReqAddMap))
                .setRevoked(false)
                .setAccessCode((String)certReqAddMap.get(JsonKeys.ACCESS_CODE))
                .setJsonUrl((String)certReqAddMap.get(JsonKeys.JSON_URL))
                .setRecipient(getCompositeReciepientObject(certReqAddMap))
                .setRelated((Map)certReqAddMap.get(JsonKeys.RELATED))
                .setReason((String)certReqAddMap.get(JsonKeys.REASON))
                .build();
        logger.info("CertsServiceImpl:getCertificate:certificate object formed: "+certificate);
        return certificate;
    }
    private Recipient getCompositeReciepientObject(Map<String, Object> certAddRequestMap) {
        Recipient recipient = new Recipient.Builder()
                .setName((String) certAddRequestMap.get(JsonKeys.RECIPIENT_NAME))
                .setId((String) certAddRequestMap.get(JsonKeys.RECIPIENT_ID))
                .setType((String) certAddRequestMap.get(JsonKeys.RECIPIENT_TYPE))
                .build();
    return recipient;
    }

    private Map<String, Object> getData(Map<String, Object> certAddRequestMap) {
        return (Map) certAddRequestMap.get(JsonKeys.JSON_DATA);
    }

    @Override
    public Response validate(Request request) throws BaseException {
        Map<String,Object> valCertReq = request.getRequest();
        String certificatedId = (String) valCertReq.get(JsonKeys.CERT_ID);
        String accessCode = (String) valCertReq.get(JsonKeys.ACCESS_CODE);
        Map<String,Object>esCertData=CertificateUtil.getCertificate(certificatedId);
        if (MapUtils.isNotEmpty(esCertData) && StringUtils.equalsIgnoreCase((String)esCertData.get(JsonKeys.ACCESS_CODE),accessCode)) {
            Certificate certificate=getCertObject(esCertData);
            Map<String,Object>responseMap=new HashMap<>();
            responseMap.put(JsonKeys.JSON,certificate.getData());
            responseMap.put(JsonKeys.PDF_URL,certificate.getPdfUrl());
            responseMap.put(JsonKeys.RELATED,certificate.getRelated());
            Response response=new Response();
            response.put(JsonKeys.RESPONSE,responseMap);
            return response;
        }
        else{
            logger.error("NO valid record found with provided certificate Id and accessCode respectively:"+certificatedId+":"+accessCode);
            throw new BaseException(IResponseMessage.INVALID_REQUESTED_DATA, MessageFormat.format(getLocalizedMessage(IResponseMessage.INVALID_ID_PROVIDED,null),certificatedId,accessCode), ResponseCode.CLIENT_ERROR.getCode());
        }

    }
    private Certificate getCertObject(Map<String,Object>esCertMap){
        Certificate certificate=requestMapper.convertValue(esCertMap,Certificate.class);
        return certificate;
    }


    @Override
    public Response download(Request request) throws BaseException {
        Response response = new Response();
        try {
            Map<String, Object> certReqMap = new HashMap<>();
            Map<String,String>requestMap=new HashMap<>();
            requestMap.put(JsonKeys.PDF_URL,(String)request.getRequest().get(JsonKeys.PDF_URL));
            certReqMap.put(JsonKeys.REQUEST,requestMap);
            String requestBody = requestMapper.writeValueAsString(certReqMap);
            logger.info("CertsServiceImpl:download:request body found:" + requestBody);
            String apiToCall = CertVars.getSERVICE_BASE_URL().concat(CertVars.getDOWNLOAD_URI());
            logger.info("CertsServiceImpl:download:complete url found:" + apiToCall);
            Future<HttpResponse<JsonNode>>responseFuture=CertificateUtil.makeAsyncPostCall(apiToCall,requestBody,headerMap);
            HttpResponse<JsonNode> jsonResponse = responseFuture.get();
            if (jsonResponse != null && jsonResponse.getStatus() == HttpStatus.SC_OK) {
                String signedUrl=jsonResponse.getBody().getObject().getJSONObject(JsonKeys.RESULT).getString(JsonKeys.SIGNED_URL);
                response.put(JsonKeys.SIGNED_URL,signedUrl);
            } else {
                throw new BaseException(IResponseMessage.INVALID_REQUESTED_DATA, MessageFormat.format(getLocalizedMessage(IResponseMessage.INVALID_PROVIDED_URL,null),(String)request.getRequest().get(JsonKeys.PDF_URL)), ResponseCode.CLIENT_ERROR.getCode());
            }

        } catch (Exception e) {
            logger.error("CertsServiceImpl:download:exception occurred:" + e);
            throw new BaseException(IResponseMessage.INTERNAL_ERROR, getLocalizedMessage(IResponseMessage.INTERNAL_ERROR,null), ResponseCode.SERVER_ERROR.getCode());
        }
        return response;
    }

    @Override
    public Response downloadV2(Request request) throws BaseException {
        String certId = (String) request.getRequest().get(JsonKeys.ID);
        logger.info("CertServiceImpl:downloadV2:idProvided:" + certId);
        Response certData = CertificateUtil.getCertRecordByID(certId);
        Response response = new Response();
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) certData.getResult().get(JsonKeys.RESPONSE);
        if (CollectionUtils.isNotEmpty(resultList) && MapUtils.isNotEmpty(resultList.get(0))) {
            Map<String, Object> certInfo = resultList.get(0);
            try {
                Map<String, Object> data = requestMapper.readValue((String) certInfo.get(JsonKeys.DATA), new TypeReference<Map<String, Object>>() {});
                response.put(JsonKeys.PRINT_URI, data.get(JsonKeys.PRINT_URI));
            } catch (Exception e) {
                logger.error("CertsServiceImpl:downloadV2:exception occurred:" + e);
                throw new BaseException(IResponseMessage.INTERNAL_ERROR, getLocalizedMessage(IResponseMessage.INTERNAL_ERROR, null), ResponseCode.SERVER_ERROR.getCode());
            }
        } else {
            throw new BaseException(IResponseMessage.RESOURCE_NOT_FOUND, localizer.getMessage(IResponseMessage.RESOURCE_NOT_FOUND, null), ResponseCode.RESOURCE_NOT_FOUND.getCode());
        }
        return response;
    }

    @Override
    public Response generate(Request request) throws BaseException {
        Response response = new Response();
        try {
            Map<String, Object> certReqMap = new HashMap<>();
            certReqMap.put(JsonKeys.REQUEST,request.getRequest());
            String requestBody = requestMapper.writeValueAsString(certReqMap);
            logger.info("CertsServiceImpl:generate:request body found:" + requestBody);
            String apiToCall = CertVars.getSERVICE_BASE_URL().concat(CertVars.getGenerateUri());
            logger.info("CertsServiceImpl:generate:complete url found:" + apiToCall);
            Future<HttpResponse<JsonNode>>responseFuture=CertificateUtil.makeAsyncPostCall(apiToCall,requestBody,headerMap);
            HttpResponse<JsonNode> jsonResponse = responseFuture.get();
            if (jsonResponse != null && jsonResponse.getStatus() == HttpStatus.SC_OK) {
                String stringifyResponse=jsonResponse.getBody().getObject().getJSONObject(JsonKeys.RESULT).get(JsonKeys.RESPONSE).toString();
                List<Map<String,Object>> apiRespList=requestMapper.readValue(stringifyResponse,List.class);
                response.put(JsonKeys.RESPONSE,apiRespList);
            } else {
                throw new BaseException(IResponseMessage.INVALID_REQUESTED_DATA, MessageFormat.format(getLocalizedMessage(IResponseMessage.INVALID_PROVIDED_URL,null),"2222"), ResponseCode.CLIENT_ERROR.getCode());
            }

        } catch (Exception e) {
            logger.error("CertsServiceImpl:generate:exception occurred:" + e);
            throw new BaseException(IResponseMessage.INTERNAL_ERROR, IResponseMessage.INTERNAL_ERROR, ResponseCode.SERVER_ERROR.getCode());
        }
        return response;
    }

    @Override
    public Response verify(Request request) throws BaseException {
        Response response = new Response();
        try {
            Map<String, Object> certVerifyReqMap = new HashMap<>();
            certVerifyReqMap.put(JsonKeys.REQUEST,composeCertVerifyRequest(request));
            String requestBody = requestMapper.writeValueAsString(certVerifyReqMap);
            logger.info("CertsServiceImpl:verify:request body prepared:" + requestBody);
            String apiToCall = CertVars.getSERVICE_BASE_URL().concat(CertVars.getVerifyUri());
            logger.info("CertsServiceImpl:verify:complete url prepared:" + apiToCall);
            Future<HttpResponse<JsonNode>>responseFuture=CertificateUtil.makeAsyncPostCall(apiToCall,requestBody,headerMap);
            HttpResponse<JsonNode> jsonResponse = responseFuture.get();
            if (jsonResponse != null && jsonResponse.getStatus() == HttpStatus.SC_OK) {
                String stringifyResponse=jsonResponse.getBody().getObject().getJSONObject(JsonKeys.RESULT).get(JsonKeys.RESPONSE).toString();
                Map<String,Object> apiResp=requestMapper.readValue(stringifyResponse,Map.class);
                response.put(JsonKeys.RESPONSE,apiResp);
            }
            else if(jsonResponse!=null && jsonResponse.getStatus() == HttpStatus.SC_BAD_REQUEST){
                String stringifyResponse=jsonResponse.getBody().getObject().getJSONObject(JsonKeys.RESULT).get(JsonKeys.MESSAGE).toString();
                throw new BaseException(IResponseMessage.INVALID_REQUESTED_DATA,stringifyResponse, ResponseCode.CLIENT_ERROR.getCode());
            }
            else {
                throw new BaseException(IResponseMessage.INVALID_REQUESTED_DATA,IResponseMessage.INVALID_REQUESTED_DATA, ResponseCode.CLIENT_ERROR.getCode());
            }

        } catch (Exception e) {
            logger.error("CertsServiceImpl:verify:exception occurred:" + e);
            throw new BaseException(IResponseMessage.INTERNAL_ERROR, e.getMessage(), ResponseCode.SERVER_ERROR.getCode());
        }
        return response;
    }

    @Override
    public Response read(Request request) throws BaseException {
        String id=(String)request.getRequest().get(JsonKeys.ID);
        logger.info("CertServiceImpl:read:idProvided:"+id);
        Map<String,Object>esCertData=CertificateUtil.getCertificate(id);
        logger.info("CertServiceImpl:read:esCert data is :"+esCertData);
        if(MapUtils.isEmpty(esCertData)){
            throw new BaseException(IResponseMessage.RESOURCE_NOT_FOUND,localizer.getMessage(IResponseMessage.RESOURCE_NOT_FOUND,null), ResponseCode.RESOURCE_NOT_FOUND.getCode());
        }
        Certificate certificate=getCertObject(esCertData);
        Response response=new Response();
        response.put(JsonKeys.RESPONSE,certificate);
        return response;
    }

    @Override
    public Response readCertMetaData(Request request) throws BaseException {
        String certId = (String) request.getRequest().get(JsonKeys.ID);
        logger.info("CertServiceImpl:read:idProvided:" + certId);
        Response certData = CertificateUtil.getCertRecordByID(certId);
        Response response = new Response();
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) certData.getResult().get(JsonKeys.RESPONSE);
        if (CollectionUtils.isNotEmpty(resultList) && MapUtils.isNotEmpty(resultList.get(0))) {
            Map<String, Object> certInfo = resultList.get(0);
            TemplateVarResolver templateVarResolver = new TemplateVarResolver();
            CertificateMetaData certificateMetaData = templateVarResolver.generateCertMetaData(certInfo);
            response.put(JsonKeys.RESPONSE, requestMapper.convertValue(certificateMetaData, Map.class));
        } else {
            throw new BaseException(IResponseMessage.RESOURCE_NOT_FOUND, localizer.getMessage(IResponseMessage.RESOURCE_NOT_FOUND, null), ResponseCode.RESOURCE_NOT_FOUND.getCode());
        }
        return response;
    }

    private Map<String,Object> composeCertVerifyRequest(Request request){
        Map<String,Object>certificate=new HashMap<>();
        Map<String,Object>certVerifyMap=new HashMap<>();
        if(MapUtils.isNotEmpty((Map)request.getRequest().get(JsonKeys.DATA))) {
            certificate.put(JsonKeys.DATA, request.getRequest().get(JsonKeys.DATA));
        }
        if(StringUtils.isNotBlank((String)request.getRequest().get(JsonKeys.ID))) {
            certificate.put(JsonKeys.ID, request.getRequest().get(JsonKeys.ID));
        }
        certVerifyMap.put(JsonKeys.CERTIFICATE,certificate);
        return certVerifyMap;
    }

    private static String getLocalizedMessage(String key, Locale locale){
        return localizer.getMessage(key, locale);
    }


    @Override
    public Response search(Request request) throws BaseException{
        Response response = new Response();
        try {
            String requestBody = requestMapper.writeValueAsString(request.getRequest());
            logger.info("CertsServiceImpl:search:request body found:" + requestBody);
            String apiToCall = CertVars.getEsSearchUri();
            logger.info("CertsServiceImpl:search:complete url found:" + apiToCall);
            Future<HttpResponse<JsonNode>> responseFuture = CertificateUtil.makeAsyncPostCall(apiToCall, requestBody, headerMap);
            HttpResponse<JsonNode> jsonResponse = responseFuture.get();
            if (jsonResponse != null && jsonResponse.getStatus() == HttpStatus.SC_OK) {
                String jsonArray = jsonResponse.getBody().getObject().getJSONObject(JsonKeys.HITS).toString();
                Map<String,Object> apiResp=requestMapper.readValue(jsonArray,Map.class);
                ESResponseMapper mappedResponse = new ObjectMapper().convertValue(apiResp,ESResponseMapper.class);
                response.put(JsonKeys.RESPONSE, mappedResponse);
            } else {
                throw new BaseException(IResponseMessage.INVALID_REQUESTED_DATA,jsonResponse.getBody().toString(), ResponseCode.CLIENT_ERROR.getCode());
            }
        } catch (Exception e) {
            logger.error("CertsServiceImpl:search:exception occurred:" + e);
            throw new BaseException(IResponseMessage.INTERNAL_ERROR, IResponseMessage.INTERNAL_ERROR, ResponseCode.SERVER_ERROR.getCode());
        }
        return response;
    }
}