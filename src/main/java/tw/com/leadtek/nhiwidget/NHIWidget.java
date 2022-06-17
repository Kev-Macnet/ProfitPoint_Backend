/**
 * Created on 2020/12/17.
 */
package tw.com.leadtek.nhiwidget;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class NHIWidget extends SpringBootServletInitializer {

  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(NHIWidget.class);
  }

  public static void main(String[] args) {
    try {
      SpringApplication.run(NHIWidget.class, args);
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}