package tw.com.leadtek.nhiwidget.log;

import org.springframework.http.ResponseEntity;

import java.util.LinkedList;

public abstract class ApiClient {

  private LinkedList<String> parts;

  public ApiClient() {
    parts = new LinkedList<String>();
  }

  public void add(String part) {
    parts.addLast(part);
  }

  public void showProperties() {
    for (String part : parts) System.out.println(part);
  }

  public abstract void showResult();

  public abstract ResponseEntity<?> call();
}
