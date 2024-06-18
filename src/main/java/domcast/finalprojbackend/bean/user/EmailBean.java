package domcast.finalprojbackend.bean.user;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Properties;

/**
 * Bean for sending emails
 * Uses Gmail SMTP server
 * @author José Castro
 * @author Pedro Domingos
 */
@Stateless
public class EmailBean {

    @EJB
    private UserBean userBean;

    private static final Logger logger = LogManager.getLogger(EmailBean.class);
    private final String username = "domcast46@gmail.com";
    private final String password = System.getenv("SMTP_DOMCAST");
    private final String host = "smtp.gmail.com";
    private final int port = 587 ;

    /**
     * Default constructor for the EmailBean class
     */
    public EmailBean() {}

    /**
     * Sends an email to the specified email address with the specified subject and body
     * @param to the email address to send the email to
     * @param subject the subject of the email
     * @param body the body of the email
     * @return true if the email was sent successfully, false otherwise
     */
    public boolean sendEmail(String to, String subject, String body) {
        logger.info("Sending email to: {}", to);
        boolean sent = false;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                logger.info("Authenticating email");
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            sent = true;

            logger.info("Email sent to: {}", to);

        } catch (MessagingException e) {
            logger.error("Email not sent to: {}. Exception: {}", to, e.getMessage());
            logger.error("Stack Trace: ", e);
        }

        return sent;
    }

    /**
        * Sends a confirmation email to the user with the validation token for account confirmation
        * @param email the email address of the user
        * @param validationToken the validation token for account confirmation
        * @return true if the email was sent successfully, false otherwise
        */
    public boolean sendConfirmationEmail(String email, String validationToken) {
        logger.info("Sending confirmation email to: {}", email);

        boolean sent = false;


        String subject = "Agile Scrum - Account Confirmation";
        String confirmationLink = "http://localhost:5173/confirm/" + validationToken;
        String body = "Dear " + email + ",\n\n"
                + "Thank you for registering with us. Please click on the link below to confirm your account.\n\n"
                + "Confirmation Link: " + confirmationLink;

        if (sendEmail(email, subject, body)) {
            sent = true;
            logger.info("Confirmation email sent to: {}", email);
        } else {
            try {
                userBean.delete(email);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            logger.error("Confirmation email not sent to: {}", email);
        }
        return sent;
    }

    /**
     * Sends a password reset email to the user with the validation token for password reset
     * @param email the email address of the user
     * @param firstName the first name of the user
     * @param validationToken the validation token for password reset
     * @return true if the email was sent successfully, false otherwise
     */
    public boolean sendPasswordResetEmail(String email, String firstName, String validationToken) {
        if (email == null || email.isEmpty() || firstName == null || firstName.isEmpty() || validationToken == null || validationToken.isEmpty()) {
            throw new IllegalArgumentException("Email, firstName, and validationToken must not be null or empty");
        }

        logger.info("Sending password reset email to: {}", email);
        boolean sent = false;

        String subject = "Agile Scrum - Password Reset";
        String resetLink = "http://localhost:5173/reset-password/" + validationToken;
        String body = "Dear " + firstName + ",\n\n"
                + "Please click on the link below to reset your password.\n\n"
                + "Reset Link: " + resetLink;

        try {
            if (sendEmail(email, subject, body)) {
                sent = true;
                logger.info("Password reset email sent to: {}", email);
            } else {
                logger.error("Password reset email not sent to: {}", email);
            }
        } catch (Exception e) {
            logger.error("Error sending password reset email to: {}", email);
        }

        return sent;
    }
}
