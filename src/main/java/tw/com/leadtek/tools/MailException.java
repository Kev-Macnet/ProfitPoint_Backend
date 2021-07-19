package tw.com.leadtek.tools;

public class MailException extends Exception {
  
  private static final long serialVersionUID = 6539524817655741759L;

  public MailException(String msg) {
    super(msg);
  }

  public String toString() {
    return "MailException:" + super.toString();
  }
}

