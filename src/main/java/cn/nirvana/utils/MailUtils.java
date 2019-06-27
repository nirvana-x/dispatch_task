package cn.nirvana.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author
 * @Description ${TODO}
 * @Date 2019/6/25 17:11
 **/
@Configuration
public class MailUtils {

    private static String protocol;
    private static String host;
    private static String port;
    private static String auth;
    private static String debug;
    private static String sendMail;
    private static String senderNike;
    private static String authPassword;
    private static String subject;

    @Value("${mail.transport.protocol}")
    public void setProtocol(String protocol) {
        MailUtils.protocol = protocol;
    }

    @Value("${mail.smtp.host}")
    public void setHost(String host) {
        MailUtils.host = host;
    }

    @Value(("${mail.smtp.port}"))
    public void setPort(String port) {
        MailUtils.port = port;
    }

    @Value("${mail.smtp.auth}")
    public void setAuth(String auth) {
        MailUtils.auth = auth;
    }

    @Value(("${mail.debug}"))
    public void setDebug(String debug) {
        MailUtils.debug = debug;
    }

    @Value("${mail.sender}")
    public void setSendMail(String sendMail) {
        MailUtils.sendMail = sendMail;
    }

    @Value("${mail.senderNike}")
    public void setSenderNike(String senderNike) {
        MailUtils.senderNike = senderNike;
    }

    @Value("${mail.authPassword}")
    public void setAuthPassword(String authPassword) {
        MailUtils.authPassword = authPassword;
    }

    @Value("${mail.subject}")
    public static void setSubject(String subject) {
        MailUtils.subject = subject;
    }

    /**
     * 邮件发送
     *
     * @param email
     * @param emailMsg
     * @throws AddressException
     * @throws MessagingException
     */
    public static void sendMail(String email, String emailMsg)
            throws AddressException, MessagingException {
        // 1.创建一个程序与邮件服务器会话对象 Session
        Properties props = new Properties();
        //设置发送的协议
        // 指定协议
        props.put("mail.transport.protocol", protocol);
        // 主机 smtp.qq.com
        props.put("mail.smtp.host", host);
        // 端口
        props.put("mail.smtp.port", port);
        // 用户密码认证
        props.put("mail.smtp.auth", auth);
        // 调试模式
        props.put("mail.debug", debug);

        // 创建验证器
        Authenticator auth = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                //设置发送人的帐号和密码
                return new PasswordAuthentication(senderNike, authPassword);
            }
        };

        Session session = Session.getInstance(props, auth);
        // 2.创建一个Message，它相当于是邮件内容
        Message message = new MimeMessage(session);
        //设置发送者
        message.setFrom(new InternetAddress(sendMail));
        //设置发送方式与接收者
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        //设置邮件主题
        message.setSubject(subject);
        //设置邮件内容
        message.setContent(emailMsg, "text/html;charset=utf-8");
        // 3.创建 Transport用于将邮件发送
        Transport.send(message);
    }

    public static void main(String[] args) throws AddressException, MessagingException {
        sendMail("1441609919@qq.com", "定时任务执行监测有异常，请及时查看！");
    }
}
