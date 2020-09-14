package controllers.health;

import controllers.BaseController;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.sunbird.BaseException;
import org.sunbird.message.IResponseMessage;
import org.sunbird.message.ResponseCode;
import play.mvc.Result;
import play.mvc.Results;
import utils.module.SignalHandler;

import javax.inject.Inject;

/**
 * This controller class will responsible to check health of the services.
 *
 * @author Anmol
 */
public class HealthController extends BaseController {

  /**
   * This action method is responsible for checking Health.
   *
   * @return a CompletableFuture of success response
   */
  public CompletionStage<Result> getHealth() {
    CompletionStage<Result> response = handleRequest(request());
    return response;
  }
  /**
   * This action method is responsible for checking Health.
   *
   * @return a CompletableFuture of success response
   */
  public CompletionStage<Result> getServiceHealth(String service) {
    CompletionStage<Result> response = handleRequest(request());
    return response;
  }

}
