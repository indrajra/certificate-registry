package org.sunbird.common.factory;

import org.sunbird.common.ElasticSearchRestHighImpl;
import org.sunbird.common.inf.ElasticSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsClientFactory {

  private static ElasticSearchService restClient = null;
  private static Logger logger=LoggerFactory.getLogger(EsClientFactory.class);

  /**
   * This method return REST client for elastic search
   *
   * @return ElasticSearchService with the respected type impl
   */
  public static ElasticSearchService getInstance() {
      return getRestClient();
  }

  private static ElasticSearchService getRestClient() {
    if (restClient == null) {
      restClient = new ElasticSearchRestHighImpl();
    }
    return restClient;
  }
}
