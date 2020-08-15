package controllers;

import org.sunbird.JsonKeys;
import org.sunbird.request.Request;
import play.mvc.Result;
import validators.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * this controller will help you in understanding the process of passing request to Actors with operation.
 * @author anmolgupta
 */
public class CertificateController extends BaseController {

    /**
     * this action method will be called for adding certificate
     * @return CompletionStage of Result
     */
    public CompletionStage<Result> add()
    {
        IRequestValidator requestValidator=new CertAddRequestValidator();
        return handleRequest(request(),
                request -> {
                    Request req = (Request) request;
                    Map<String, Object> context = new HashMap<>();
                    context.put(JsonKeys.VERSION, JsonKeys.VERSION_1);
                    req.setContext(context);
                    requestValidator.validate(req);
                    return null;
                }, JsonKeys.CERT_ADD);
    }

    /**
     * this action method will be called for adding certificate
     * @return CompletionStage of Result
     */
    public CompletionStage<Result> addV2()
    {
        IRequestValidator requestValidator=new CertAddRequestValidator();
        return handleRequest(request(),
                request -> {
                    Request req = (Request) request;
                    Map<String, Object> context = new HashMap<>();
                    context.put(JsonKeys.VERSION, JsonKeys.VERSION_2);
                    req.setContext(context);
                    requestValidator.validate(req);
                    return null;
                }, JsonKeys.CERT_ADD_V2);
    }

    /**
     * this action method will be called for verifying certificate
     * @return CompletionStage of Result
     */
    public CompletionStage<Result> validate()
    {
        IRequestValidator requestValidator=new CertValidateRequestValidator();
        return handleRequest(request(),
            request -> {
                Request req = (Request) request;
                requestValidator.validate(req);
                return null;
            }, JsonKeys.CERT_VALIDATE);
    }

    /**
     * this action method will be called for downloading certificate
     * @return CompletionStage of Result
     */
    public CompletionStage<Result> download()
    {
        IRequestValidator requestValidator=new CertDownloadRequestValidator();
        return handleRequest(request(),
                request -> {
                    Request req = (Request) request;
                    requestValidator.validate(req);
                    return null;
                },  JsonKeys.CERT_DOWNLOAD);
    }


    /**
     * this action method will be called for downloading certificate
     *
     * @return CompletionStage of Result
     */
    public CompletionStage<Result> downloadV2(String id) {
        IRequestValidator requestValidator = new CertDownloadV2RequestValidator();
        return handleRequest(request(),
                request -> {
                    Request req = (Request) request;
                    req.getRequest().put(JsonKeys.ID, id);
                    requestValidator.validate(req);
                    return null;
                }, JsonKeys.CERT_DOWNLOAD_V2);
    }

    /**
     * this action method will be called for verify certificate
     * @return CompletionStage of Result
     */
    public CompletionStage<Result> verify()
    {
        IRequestValidator requestValidator=new CertVerifyRequestValidator();
        return handleRequest(request(),
                request -> {
                    Request req = (Request) request;
                    requestValidator.validate(req);
                    return null;
                }, JsonKeys.CERT_VERIFY);
    }

    /**
     * this action method will be called for reading certificate with id
     * @return CompletionStage of Result
     */
    public CompletionStage<Result> read(String id) {
        IRequestValidator requestValidator = new CertReadRequestValidator();
        return handleRequest(request(),
                request -> {
                    Request req = (Request) request;
                    req.getRequest().put(JsonKeys.ID, id);
                    requestValidator.validate(req);
                    return null;
                }, JsonKeys.READ);


    }


    public CompletionStage<Result> search()
    {
        IRequestValidator requestValidator=new CertSearchRequestValidator();
        return handleRequest(request(),
                request -> {
                    Request req = (Request) request;
                    requestValidator.validate(req);
                    return null;
                }, JsonKeys.SEARCH);
    }

    public CompletionStage<Result> readCertMetaData(String certId) {
        IRequestValidator requestValidator = new CertReadMetaDataValidator();
        return handleRequest(request(),
                request -> {
                    Request req = (Request) request;
                    req.getRequest().put(JsonKeys.ID, certId);
                    requestValidator.validate(req);
                    return null;
                }, JsonKeys.READ_CERT_META_DATA);
    }

}
