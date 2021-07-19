package tw.com.leadtek.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MailContext {
    private Logger logger = LogManager.getLogger();
    private String from = null;
    private String fromName = null;
    private Collection to = null;
    private String subject = null;
    private Collection replyTo = null;
    private Collection cc = null;
    private Collection bcc = null;
    private Collection attFile = null;
    private Multipart iMp = null;
    private Date date = null;

    public MailContext() {

        init();
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String[] getTo() {
        // Object [] objs = to.toArray();
        // return (String[]) objs;
        return (String[]) to.toArray(new String[0]);
    }

    public void setTo(String[] tos) {
        to = arrayToCollection(tos);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String[] getReplyTo() {
        return (String[]) replyTo.toArray(new String[0]);
    }

    public void setReplyTo(String[] replyTos) {
        replyTo = arrayToCollection(replyTos);
    }

    public String[] getCc() {
        return (String[]) cc.toArray(new String[0]);
    }

    public void setCc(String[] ccs) {
        cc = arrayToCollection(ccs);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String[] getBcc() {
        return (String[]) bcc.toArray(new String[0]);
    }

    public void setBcc(String[] bccs) {
        bcc = arrayToCollection(bccs);
    }

    public void addTo(String toAddr) {
        if (toAddr != null) {
            to.add(toAddr);
        }
    }

    public void addReplyTo(String replyToAddr) {
        if (replyToAddr != null) {
            replyTo.add(replyToAddr);
        }
    }

    public void addCc(String ccAddr) {
        if (ccAddr != null) {
            cc.add(ccAddr);
        }
    }

    public void addBcc(String bccAddr) {
        if (bcc != null) {
            bcc.add(bccAddr);
        }
    }

    public Multipart getContent() {
        return iMp;
    }

    public void addAttachedFile(String aFileName) throws MailException {
        if (attFile == null)
            attFile = new ArrayList();
        try {
            attFile.add(aFileName);
            MimeBodyPart mbp = new MimeBodyPart();

            FileDataSource fds = new FileDataSource(aFileName);
            mbp.setDataHandler(new DataHandler(fds));
            mbp.setFileName(fds.getName());
            iMp.addBodyPart(mbp);
        } catch (Exception ex) {
            logger.error("addAttachedFile", ex);
            throw new MailException("sendMessage: " + ex.toString());
        }
    }

    public String[] getAttachedFiles() {
        if (attFile == null)
            return null;
        return (String[]) attFile.toArray(new String[0]);
    }

    /**
     * 設定郵件內容為文字格式
     * 
     * @param aTxt
     * @throws MailException
     */
    public void setMessageTxt(String aTxt) throws MailException {
        try {
            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setText(aTxt);
            iMp.addBodyPart(mbp, 0);
        } catch (Exception ex) {
            logger.error("setMessageTxt", ex);
            throw new MailException("sendMessage: " + ex.toString());
        }

    }

    /**
     * 設定郵件內容為HTML格式
     * 
     * @param aTxt
     * @throws MailException
     */
    public void setMessageHtml(String aTxt) throws MailException {
        try {
            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setContent(aTxt, "text/html; charset=UTF-8");
            // mbp.setText(aTxt);
            iMp.addBodyPart(mbp, 0);
        } catch (Exception ex) {
            logger.error("setMessageHtml", ex);
            throw new MailException("sendMessage: " + ex.toString());
        }

    }

    public String setMessageTxt() throws MailException {
        try {
            MimeBodyPart mbp = (MimeBodyPart) iMp.getBodyPart(0);
            return (String) mbp.getContent();
        } catch (Exception ex) {
            logger.error("setMessageTxt", ex);
            throw new MailException("sendMessage: " + ex.toString());
        }

    }

    /**
     * isOK -- returns true if this context contains the least information required to make a
     * send/receive request. It does not mean that the actual action will succeed.
     */
    public boolean isOK() {
        if (to != null)
            return true;
        else
            return false;
    }

    private Collection arrayToCollection(Object[] a) {
        ArrayList c = new ArrayList();
        for (int i = 0; i < a.length; ++i) {
            if (a[i] != null)
                c.add(a[i]);
        }
        return c;
    }

    /*
     * private void addArrayToCollection(Collection c, Object[] a) { for (int i = 0; i < a.length;
     * ++i) { c.add(a[i]); } }
     */
    private void init() {
        from = null;
        fromName = null;
        to = new ArrayList();
        cc = new ArrayList();
        bcc = new ArrayList();
        replyTo = new ArrayList();
        iMp = new MimeMultipart();

    }

}
