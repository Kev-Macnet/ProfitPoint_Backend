/**
 * Mail Service
 * 
 * @author Jessica Tseng
 * @version $Id: MailService.java,v 1.0 2015/02/26 15:47 Exp $
 *
 */
package tw.com.leadtek.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailService {
    // private Logger logger = Logger.getLogger(getClass());
    private String iUser = null;
    private String iPassword = null;
    private String iHostName = null;

    private String iSmtpHost = null;

    public MailService() {
        // do nothing
    }

    /*
     * public MailService(String hostName, String usr, String password) { iUser = usr; iHostName =
     * hostName; iPassword = password; }
     */
    private void setSMTPHost(String smtpHost) {
        iSmtpHost = smtpHost;
    }

    private void setPOP3Host(String pop3Host) {}

    /**
     * smpt server不須認證狀態下發送電子郵件
     * 
     * @param ctx
     * @param smtpHost
     * @throws MailException
     */
    public void sendMessage(MailContext ctx, String smtpHost) throws MailException // ,
                                                                                   // javax.mail.MessagingException
    {

        condition(ctx != null, "context != null");
        condition(ctx.isOK(), "context info isOK");
        condition(smtpHost != null, "SMTP Host != null");

        Properties prop = new Properties();
        prop.put("mail.smtp.host", smtpHost);

        Session session = Session.getDefaultInstance(prop, null);
        Message msg = new MimeMessage(session);

            try {
                InternetAddress[] toList = makeInetAddresses(ctx.getTo());
                String from = ctx.getFrom();
                String formName = ctx.getFromName();
                msg.setFrom(new InternetAddress(from, formName, "UTF-8"));

                msg.setRecipients(javax.mail.Message.RecipientType.TO, toList);
                msg.setSubject(ctx.getSubject());
                Date sentDate = ctx.getDate();
                if (sentDate == null) {
                    sentDate = new Date();
                }
                msg.setSentDate(sentDate);

                Object content = ctx.getContent();

                setMessageContent(msg, content);
                Transport.send(msg);
                System.out.println("Sending message success");
            } catch (Exception e) {
                e.printStackTrace();
            }
//        } catch (Exception ex) {
//            // logger.error("send Message ",ex);
//            System.out.println("send Message Error:" + ex.getMessage());
//            throw new MailException("sendMessage: " + ex.toString());
//        }
    }

    /**
     * smpt server須認證狀態下發送電子郵件
     * 
     * @param ctx
     * @param smtpHost
     * @param user
     * @param pwd
     * @throws MailException
     */
    public void sendMessage(MailContext ctx, String smtpHost, String user, String pwd)
            throws MailException {

        condition(ctx != null, "context != null");
        condition(ctx.isOK(), "context info isOK");
        condition(smtpHost != null, "SMTP Host != null");

        // major processing
        Properties prop = new Properties();

        // Setup mail server
        prop.put("mail.smtp.host", smtpHost);
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.auth", "true");

        final String hostUName = user;
        final String hPassword = pwd;

        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(hostUName, hPassword);
            }
        };

        // Session session = Session.getDefaultInstance(prop, authenticator);
        Session session = Session.getInstance(prop, authenticator);
        javax.mail.Message msg = new MimeMessage(session);

        try {
            InternetAddress[] toList = makeInetAddresses(ctx.getTo());

            String from = ctx.getFrom();
            String formName = ctx.getFromName();
            msg.setFrom(new InternetAddress(from, formName, "UTF-8"));

            msg.setRecipients(javax.mail.Message.RecipientType.TO, toList);
            msg.setSubject(ctx.getSubject());
            Date sentDate = ctx.getDate();
            if (sentDate == null) {
                sentDate = new Date();
            }
            msg.setSentDate(sentDate);

            Object content = ctx.getContent();

            setMessageContent(msg, content);
            Transport.send(msg);
            System.out.println("Sending message success");
        } catch (Exception ex) {
            System.out.println("Send Message Error:" + ex.getMessage());
            // logger.error("sendMessage",ex);
            throw new MailException("sendMessage: " + ex.toString());
        }
    }

    private void setMessageContent(Message msg, Object content)
            throws javax.mail.MessagingException {
        if (content instanceof Multipart) {
            System.out.println("Sending Multipart message");
            msg.setContent((Multipart) content);
        } else // generic
        {
            System.out.println("Sending Text message");
            msg.setText((String) content);
        }
    }

    /**
     * makeInetAddresses() -- translates the string representations to the object forms that will
     * make sense to the transporter.
     */
    private InternetAddress[] makeInetAddresses(String[] addrs)
            throws javax.mail.internet.AddressException {
        ArrayList aaddr = new ArrayList();
        for (int i = 0; i < addrs.length; ++i) {
            try {
                aaddr.add(new InternetAddress(addrs[i], addrs[i], "big5"));
                // aaddr.add(InternetAddress.parse(addrs[i])[0]);
            } catch (Exception e) {
                System.out.println("makeInetAddress error:" + e.getMessage());
                // logger.error("makeInetAddresses",e);
            }
        }
        return (InternetAddress[]) aaddr.toArray(new InternetAddress[0]);
    }

    /**
     * condition(boolean, msg) -- miniscule condition checking which only throws exception instead
     * of the more violent assert() in C/C++.
     */
    private void condition(boolean predicate, String msg) throws MailException {
        if (!predicate) {
            throw new MailException("Assertion failed: " + msg);
        }
    }


    public static void main(String[] args) throws MailException {
        String smptHost = "59.120.139.196";
        String[] toList = {"ken_lai@leadtek.com.tw"};
        String fromEamil = "monitor@leadtek.com.tw";
        String fromName = "Payment Gateway";
        String subject = "Ken test java mail part 2";
        String mailBodyText = "java mail test...";
        StringBuffer mailBodyHtml =
                new StringBuffer("<html><body>").append("Ken test<br />")
                        .append("測試發送mail service<p>").append("</body></html>");
        MailService ms = new MailService();
        MailContext mc = new MailContext();

        mc.setFrom(fromEamil);
        mc.setFromName(fromName);
        mc.setTo(toList);
        mc.setSubject(subject);
        // 發送純文字電子郵件
        // mc.setMessageTxt(mailBodyText);

        // 發送HTML格式電子郵件
        mc.setMessageHtml(mailBodyHtml.toString());
        ms.sendMessage(mc, smptHost);
    }
}
