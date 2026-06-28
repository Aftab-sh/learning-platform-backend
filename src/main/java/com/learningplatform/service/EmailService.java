package com.learningplatform.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.learningplatform.Exception.BadRequestException;
import com.learningplatform.entity.User;

@Service
@Slf4j
public class EmailService
{

    
    private final JavaMailSender mailSender;
    
    @Autowired
    EmailService(JavaMailSender mailSender)
    {
    	this.mailSender=mailSender;
    	
    }

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.base-url}")
    private String baseUrl;
    
    
    @Value("${app.frontend-url}")
    private String frontendUrl;

  


    
 // ── Verification Email ──
    @Async
    public void sendVerificationEmail(User user, String verificationToken) {
        String actionUrl = frontendUrl + "/verify-email?token=" + verificationToken;
        sendHtmlEmail(user.getEmail(), "Verify Your Email", actionUrl,
                "Thank you for registering. Please verify your email.",
                "Verify Email");
    }

    // ── Forgot Password Email ──
    @Async
    public void sendForgotPasswordEmail(String email, String resetToken) {
        String actionUrl = frontendUrl + "/reset-password?token=" + resetToken;
        sendHtmlEmail(email, "Reset Your Password", actionUrl,
                "We received a request to reset your password.",
                "Reset Password");
    }

    // ── Private method to send HTML email ──
    private void sendHtmlEmail(String toEmail, String subject, String actionUrl,
                               String message, String buttonText) {
        try {
            String content = buildEmailContent(subject, message, actionUrl, buttonText);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);          // ✅ Correct: expects String email
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setText(content, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new BadRequestException("Failed to send email: " + e.getMessage());
        }
    }

    // ── Build HTML content ──
    private String buildEmailContent(String subject, String message, String actionUrl, String buttonText) {
        return """
        <!DOCTYPE html>
        <html>
        <head><meta charset="UTF-8"></head>
        <body style="margin:0;padding:0;background:#f4f6f8;font-family:Arial,sans-serif;">
        <table width="100%%" cellpadding="0" cellspacing="0">
          <tr><td align="center">
            <table width="600" cellpadding="0" cellspacing="0"
                   style="background:white;margin-top:40px;border-radius:10px;overflow:hidden;box-shadow:0 2px 10px rgba(0,0,0,0.1);">
              <tr><td align="center" style="background:#2563eb;color:white;padding:25px;">
                <h1 style="margin:0;">CodeLearn</h1>
              </td></tr>
              <tr><td style="padding:40px;">
                <h2 style="color:#222;">%s</h2>
                <p style="font-size:16px;color:#555;">%s</p>
                <div style="text-align:center;margin:35px 0;">
                  <a href="%s" style="background:#2563eb;color:white;padding:14px 28px;
                          text-decoration:none;border-radius:6px;font-weight:bold;display:inline-block;">
                    %s
                  </a>
                </div>
                <p style="color:#666;font-size:13px;">
                  If the button doesn't work, copy this URL into your browser:<br>
                  <span style="color:#2563eb;word-break:break-all;">%s</span>
                </p>
                <hr>
                <p style="color:#888;font-size:13px;">
                  This link was generated automatically. If you did not request this, ignore this email.
                </p>
              </td></tr>
            </table>
          </td></tr>
        </table>
        </body>
        </html>
        """.formatted(subject, message, actionUrl, buttonText, actionUrl);
    }


     void sendEmail(String email, String token, String subject, String message,
                           String path, String buttonText) 
    {
    	
        try {
        	log.info("Sending email to {}",email);
            String actionUrl = baseUrl + path + "?token=" + token;

            String content = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                    </head>
                    <body style="margin:0;padding:0;background-color:#f4f6f8;font-family:Arial,sans-serif;">
                    <table width="100%%" cellpadding="0" cellspacing="0">
                        <tr>
                            <td align="center">
                                <table width="600" cellpadding="0" cellspacing="0"
                                       style="background:white;margin-top:40px;border-radius:10px;overflow:hidden;box-shadow:0 2px 10px rgba(0,0,0,0.1);">
                                    <tr>
                                        <td align="center"
                                            style="background:#2563eb;color:white;padding:25px;">
                                            <h1 style="margin:0;">Learning Platform</h1>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:40px;">
                                            <h2 style="color:#222;">%s</h2>
                                            <p style="font-size:16px;color:#555;">%s</p>
                                            <div style="text-align:center;margin:35px 0;">
                                                <a href="%s"
                                                   style="background:#2563eb;color:white;padding:14px 28px;
                                                          text-decoration:none;border-radius:6px;font-weight:bold;
                                                          display:inline-block;">
                                                    %s
                                                </a>
                                            </div>
                                            <p style="color:#666;">
                                                If the button above does not work,
                                                copy and paste the following URL into your browser:
                                            </p>
                                            <p style="word-break:break-all;color:#2563eb;">%s</p>
                                            <hr>
                                            <p style="color:#888;font-size:13px;">
                                                This link was generated automatically.
                                                If you did not request this action,
                                                you can safely ignore this email.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                    </body>
                    </html>
                    """.formatted(subject, message, actionUrl, buttonText, actionUrl);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setText(content, true);
            log.info("Email sent successfully");

            mailSender.send(mimeMessage);

        } catch (MessagingException e)
        {
            log.error("Failed to send email");

        }
    }
}