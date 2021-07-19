/**
 * Created on 2021/5/20.
 */
package tw.com.leadtek.nhiwidget.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerConfig {

  protected Logger logger = LogManager.getLogger();

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handle(HttpMessageNotReadableException e) {
    logger.warn("Returning HTTP 400 Bad Request", e);
    throw e;
  }
}
