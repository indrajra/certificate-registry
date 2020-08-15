package org.sunbird.service;


import org.sunbird.BaseException;
import org.sunbird.request.Request;
import org.sunbird.response.Response;

/**
 * this is an interface class for implementing certificate related operations
 * @author anmolgupta
 *
 */
public interface ICertService{

    Response delete(Request request) throws BaseException;

    String add(Request request) throws BaseException;

    Response  validate(Request request) throws BaseException;

    Response download(Request request) throws BaseException;

    Response downloadV2(Request request) throws BaseException;

    Response generate(Request request) throws BaseException;

    Response verify(Request request) throws  BaseException;

    Response read(Request request) throws  BaseException;

    Response search(Request request) throws BaseException;

    Response readCertMetaData(Request request) throws  BaseException;
}
