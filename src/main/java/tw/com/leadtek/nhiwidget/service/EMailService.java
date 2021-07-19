/**
 * Created on 2021/5/6.
 */
package tw.com.leadtek.nhiwidget.service;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tw.com.leadtek.tools.MailContent;
import tw.com.leadtek.tools.MailContext;
import tw.com.leadtek.tools.MailService;

@Component
public class EMailService {
  
  private Logger logger = LogManager.getLogger();

  @Value("${mail.host}")
  private String mailHost;

  @Value("${mail.port}")
  private String mailPort;
  
  @Value("${mail.fromEmail}")
  private String fromEmail;
  
  @Value("${mail.password}")
  private String password;
  
  private MailContent mail;
  
  public void sendMail(String mailSubject, String mailToList, String content) {
    Properties props = new Properties();
    props.put("mail.smtp.host", mailHost);
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "false");
    props.put("mail.smtp.port", mailPort);
    Session session = Session.getInstance(props, new Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(fromEmail, password);
      }
    });

    try {
      MimeMessage message = new MimeMessage(session);
      message.setFrom(new InternetAddress(fromEmail));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailToList));
      message.setSubject(mailSubject, "UTF-8");
      StringBuffer mailBodyHtml =
          new StringBuffer("<html><body>").append(content).append("</body></html>");

      MimeBodyPart mbp = new MimeBodyPart();
      mbp.setContent(mailBodyHtml.toString(), "text/html; charset=UTF-8");
      Multipart mp = new MimeMultipart();
      mp.addBodyPart(mbp, 0);
      message.setContent(mp);

      Transport transport = session.getTransport("smtp");
      transport.connect(mailHost, Integer.parseInt(mailPort), fromEmail, password);
      Transport.send(message);
    } catch (MessagingException e) {
      logger.error(e.getMessage());
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  public void sendMailNoAuth(String mailSubject, String mailToList) {
    if (mail.getMessages().size() == 0) {
      return;
    } else {
      logger.info("================================ alert email =================================");
      for (int i = 0; i < mail.getMessages().size(); i++) {
        logger.info(mail.getMessages());
      }
      logger
          .info("============================== alert email end ================================");
    }

    try {

      String fromEmail = "ken_lai@leadtek.com.tw";
      String fromName = "test";
      logger.info("InternetAddress.parse(mailToList)");
      String[] toList = mailToList.split(",");
      String subject = mailSubject;
      // StringBuffer mailBodyHtml =
      // new StringBuffer("<html><body>").append(mail.getMailContent()).append("</body></html>");
      StringBuffer mailBodyHtml =
          new StringBuffer("<html><body>").append("test").append("</body></html>");

      MailService ms = new MailService();
      MailContext mc = new MailContext();

      mc.setFrom(fromEmail);
      mc.setFromName(fromName);
      mc.setTo(toList);
      mc.setSubject(subject);
      // 發送純文字電子郵件
      // mc.setMessageTxt(mailBodyText);

      // 發送HTML格式電子郵件
      mc.setMessageHtml(mailBodyHtml.toString());
      ms.sendMessage(mc, mailHost);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

}
