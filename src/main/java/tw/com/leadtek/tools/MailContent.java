/**
 * Created on 2020/9/18.
 */
package tw.com.leadtek.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MailContent {

  private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
  
  private ArrayList<String> messages = new ArrayList<String>();
  
  public void addMessage(String s) {
    messages.add("<font face=\"Courier New\" size=\"2\"> " + sdf.format(new Date()) + " " + s + "</font>");
  }
  
  public ArrayList<String> getMessages(){
    return messages;
  }
  
  public String getMailContent() {
    StringBuilder sb = new StringBuilder();
    for (String string : messages) {
      sb.append(string);
      sb.append("<p>");
    }
    return sb.toString();
  }
}
