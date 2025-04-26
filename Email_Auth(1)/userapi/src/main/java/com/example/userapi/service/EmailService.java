package com.example.userapi.service;

import com.example.userapi.model.User;

import io.jsonwebtoken.io.IOException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

//    @Autowired
//    private JavaMailSender mailSender;
//
//    public void sendSimpleEmail(String toEmail, String subject, String body) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("noreply@userapi.com");
//        message.setTo(toEmail);
//        message.setSubject(subject);
//        message.setText(body);
//        mailSender.send(message);
//    }
//
//   public void sendAccountCreatedEmail(User user) {
//        String subject = "Your Account Has Been Created";
//       String body = String.format(
//            "Dear %s,\n\n" +
//            "Your account has been successfully created with the following details:\n\n" +
//            "Employee ID: %s\n" +
//            "Name: %s\n" +
//            "Email: %s\n" +
//            "Mobile: %s\n\n" +
//            "Thank you for registering with us.\n\n" +
//            "Best regards,\n" +
//            "User API Team",
//            user.getName(), user.getEmpId(), user.getName(), 
//            user.getEmail(), user.getMobileNumber()
//        );
//        sendSimpleEmail(user.getEmail(), subject, body);
//    }
	    private JavaMailSender mailSender;

	    public void sendAccountCreatedEmail(User user) throws MessagingException {
	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
	        
	        String subject = "Your Account Has Been Created";
	        
	        // HTML email with table formatting
	        String htmlBody = String.format(
	            "<html>" +
	            "<body>" +
	            "<p>Dear %s,</p>" +
	            "<p>Your account has been successfully created with the following details:</p>" +
	            "<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse;'>" +
	            "<tr style='background-color: #f2f2f2;'><th>Field</th><th>Value</th></tr>" +
	            "<tr><td>Employee ID</td><td>%s</td></tr>" +
	            "<tr><td>Name</td><td>%s</td></tr>" +
	            "<tr><td>Email</td><td>%s</td></tr>" +
	            "<tr><td>Mobile</td><td>%s</td></tr>" +
	            "<tr><td>Mobile Pass</td><td>%s</td></tr>" +
	            "</table>" +
	            "<p>Thank you for registering with us.</p>" +
	            "<p>Best regards,<br/>User API Team</p>" +
	            "</body>" +
	            "</html>",
	            user.getName(),
	            user.getEmpId(),
	            user.getName(),
	            user.getEmail(),
	            user.getMobileNumber(),
	            user.getMobilePass() ? "Enabled" : "Disabled"
	        );

	        helper.setTo(user.getEmail());
	        helper.setSubject(subject);
	        helper.setText(htmlBody, true); // true = isHTML
	        
	        mailSender.send(message);
	    }


	        public void sendAccountUpdatedEmail(User user) throws MessagingException {
	            MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
	            
	            String subject = "Your Account Has Been Updated";
	            String htmlBody = String.format(
	                "<html>" +
	                "<body>" +
	                "<p>Dear %s,</p>" +
	                "<p>Your account details have been updated. Here are your current details:</p>" +
	                "<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse; width: 100%%;'>" +
	                "<tr style='background-color: #f2f2f2;'><th>Field</th><th>Value</th></tr>" +
	                "<tr><td>Employee ID</td><td>%s</td></tr>" +
	                "<tr><td>Name</td><td>%s</td></tr>" +
	                "<tr><td>Email</td><td>%s</td></tr>" +
	                "<tr><td>Mobile</td><td>%s</td></tr>" +
	                "<tr><td>Mobile Pass</td><td>%s</td></tr>" +
	                "</table>" +
	                "<p style='color: red;'>If you did not request these changes, please contact support immediately.</p>" +
	                "<p>Best regards,<br/>User API Team</p>" +
	                "</body>" +
	                "</html>",
	                user.getName(),
	                user.getEmpId(),
	                user.getName(),
	                user.getEmail(),
	                user.getMobileNumber(),
	                user.getMobilePass() ? "Enabled" : "Disabled"
	            );

	            helper.setTo(user.getEmail());
	            helper.setSubject(subject);
	            helper.setText(htmlBody, true);
	            mailSender.send(message);
	        }

	        public void sendAccountDeletedEmail(String email, String empId, String name) throws MessagingException {
	            MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
	            
	            String subject = "Account Deletion Notification";
	            String htmlBody = String.format(
	                "<html>" +
	                "<body>" +
	                "<p>Dear %s,</p>" +
	                "<p>Your account (Employee ID: <strong>%s</strong>) has been deleted from our system.</p>" +
	                "<div style='background-color: #fff3cd; padding: 15px; border-left: 6px solid #ffc107;'>" +
	                "<p>If this was not your intention or if you have any questions, " +
	                "please contact our support team immediately.</p>" +
	                "</div>" +
	                "<p>Best regards,<br/>User API Team</p>" +
	                "</body>" +
	                "</html>",
	                name, empId
	            );

	            helper.setTo(email);
	            helper.setSubject(subject);
	            helper.setText(htmlBody, true);
	            mailSender.send(message);
	        }

	        public void sendUserReportEmail(String recipientEmail, User user) throws MessagingException {
	            MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
	            
	            String subject = "Your Account Details Report";
	            String htmlBody = String.format(
	                "<html>" +
	                "<head><style>" +
	                "table { border-collapse: collapse; width: 100%%; margin-bottom: 20px; }" +
	                "th { background-color: #4CAF50; color: white; text-align: left; padding: 8px; }" +
	                "td { padding: 8px; border-bottom: 1px solid #ddd; }" +
	                "tr:nth-child(even) { background-color: #f2f2f2; }" +
	                "</style></head>" +
	                "<body>" +
	                "<p>Dear %s,</p>" +
	                "<p>Here are your current account details:</p>" +
	                "<table>" +
	                "<tr><th>Field</th><th>Value</th></tr>" +
	                "<tr><td>Employee ID</td><td>%s</td></tr>" +
	                "<tr><td>Name</td><td>%s</td></tr>" +
	                "<tr><td>Email</td><td>%s</td></tr>" +
	                "<tr><td>Mobile Number</td><td>%s</td></tr>" +
	                "<tr><td>Mobile Pass</td><td>%s</td></tr>" +
	                "<tr><td>Account Created</td><td>%s</td></tr>" +
	                "<tr><td>Last Updated</td><td>%s</td></tr>" +
	                "</table>" +
	                "<p>Best regards,<br/>User API Team</p>" +
	                "</body>" +
	                "</html>",
	                user.getName(),
	                user.getEmpId(),
	                user.getName(),
	                user.getEmail(),
	                user.getMobileNumber(),
	                user.getMobilePass() ? "Enabled" : "Disabled",
	                user.getCreatedAt(),
	                user.getUpdatedAt() != null ? user.getUpdatedAt() : "N/A"
	            );

	            helper.setTo(recipientEmail);
	            helper.setSubject(subject);
	            helper.setText(htmlBody, true);
	            mailSender.send(message);
	        }

	        public void sendAllUsersReportEmail(String recipientEmail, List<User> users) throws MessagingException {
	            MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
	            
	            String subject = "Complete Users Report";
	            StringBuilder htmlBody = new StringBuilder();
	            htmlBody.append(
	                "<html>" +
	                "<head><style>" +
	                "table { border-collapse: collapse; width: 100%%; margin-bottom: 20px; }" +
	                "th { background-color: #343a40; color: white; padding: 10px; }" +
	                "td { padding: 8px; border: 1px solid #ddd; }" +
	                "tr:nth-child(even) { background-color: #f8f9fa; }" +
	                ".header { background-color: #6c757d; color: white; padding: 15px; }" +
	                "</style></head>" +
	                "<body>" +
	                "<div class='header'><h2>Complete Users Report</h2></div>" +
	                "<table>" +
	                "<tr><th>Employee ID</th><th>Name</th><th>Email</th><th>Mobile</th><th>Mobile Pass</th><th>Created At</th><th>Updated At</th></tr>"
	            );

	            for (User user : users) {
	                htmlBody.append(String.format(
	                    "<tr>" +
	                    "<td>%s</td>" +
	                    "<td>%s</td>" +
	                    "<td>%s</td>" +
	                    "<td>%s</td>" +
	                    "<td>%s</td>" +
	                    "<td>%s</td>" +
	                    "<td>%s</td>" +
	                    "</tr>",
	                    user.getEmpId(),
	                    user.getName(),
	                    user.getEmail(),
	                    user.getMobileNumber(),
	                    user.getMobilePass() ? "Yes" : "No",
	                    user.getCreatedAt(),
	                    user.getUpdatedAt() != null ? user.getUpdatedAt() : "N/A"
	                ));
	            }

	            htmlBody.append(
	                "</table>" +
	                "<p>Total Users: " + users.size() + "</p>" +
	                "<p>Best regards,<br/>User API Team</p>" +
	                "</body>" +
	                "</html>"
	            );

	            helper.setTo(recipientEmail);
	            helper.setSubject(subject);
	            helper.setText(htmlBody.toString(), true);
	            mailSender.send(message);
	        }

	        // Keep existing simple email method for fallback
	        private void sendSimpleEmail(String to, String subject, String text) {
	            try {
	                SimpleMailMessage message = new SimpleMailMessage();
	                message.setTo(to);
	                message.setSubject(subject);
	                message.setText(text);
	                mailSender.send(message);
	            } catch (Exception e) {
	                // Log error but don't throw
	                System.err.println("Failed to send simple email: " + e.getMessage());
	            }
	        }
}
    

    
//   
//    public void sendAccountUpdatedEmail(User user) {
//        String subject = "Your Account Has Been Updated";
//        String body = String.format(
//            "Dear %s,\n\n" +
//            "Your account details have been updated. Here are your current details:\n\n" +
//            "Employee ID: %s\n" +
//            "Name: %s\n" +
//            "Email: %s\n" +
//            "Mobile: %s\n\n" +
//            "If you did not request these changes, please contact support immediately.\n\n" +
//            "Best regards,\n" +
//            "User API Team",
//            user.getName(), user.getEmpId(), user.getName(), 
//            user.getEmail(), user.getMobileNumber()
//        );
//        sendSimpleEmail(user.getEmail(), subject, body);
//    }
//
//    public void sendAccountDeletedEmail(String email, String empId, String name) {
//        String subject = "Your Account Has Been Deleted";
//        String body = String.format(
//            "Dear %s,\n\n" +
//            "Your account (Employee ID: %s) has been deleted from our system.\n\n" +
//            "If this was not your intention or if you have any questions, " +
//            "please contact our support team.\n\n" +
//            "Best regards,\n" +
//            "User API Team",
//            name, empId
//        );
//        sendSimpleEmail(email, subject, body);
//    }
//
//    public void sendUserReportEmail(String recipientEmail, User user) {
//        String subject = "Your Account Details";
//        String body = String.format(
//            "Dear %s,\n\n" +
//            "Here are your current account details:\n\n" +
//            "Employee ID: %s\n" +
//            "Name: %s\n" +
//            "Email: %s\n" +
//            "Mobile: %s\n" +
//            "Mobile Pass Enabled: %s\n\n" +
//            "Best regards,\n" +
//            "User API Team",
//            user.getName(), user.getEmpId(), user.getName(), 
//            user.getEmail(), user.getMobileNumber(),
//            user.getMobilePass() ? "Yes" : "No"
//        );
//        sendSimpleEmail(recipientEmail, subject, body);
//    }
//
//    
//    public void sendAllUsersReportEmail(String recipientEmail, List<User> users) {
//        String subject = "All Users Report";
//        StringBuilder body = new StringBuilder();
//        body.append("Complete User Report:\n\n");
//        
//        for (User user : users) {
//            body.append(String.format(
//                "Employee ID: %s\nName: %s\nEmail: %s\nMobile: %s\nMobile Pass: %s\nCreated At: %s\n\n",
//                user.getEmpId(), user.getName(), 
//                user.getEmail(), user.getMobileNumber(),
//                user.getMobilePass() ? "Yes" : "No",
//                user.getCreatedAt()
//            ));
//        }
//        
//        body.append("\nBest regards,\nUser API Team");
//        sendSimpleEmail(recipientEmail, subject, body.toString());
//    }
//}
//
//
//

