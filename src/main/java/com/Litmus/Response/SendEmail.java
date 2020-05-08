package com.Litmus.Response;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {

	public static void Sendmail(String recepient,HashMap<String, Integer > map)  {
		System.out.println("Preparing to sent mail.....");
		Properties properties = new Properties();
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "465");
		//ports 465, 575, 25
		 final String usn = "sri.litmus@gmail.com";
		final String password = "Litmus@123";

		// Get the Session object.// and pass username and password
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication("sri.litmus@gmail.com", "Litmus@123");
			}
		});
		Message message = prepareMessage(session, usn, recepient,map);
		try {
			Transport.send(message);
		} catch (MessagingException e) {
			System.out.println("Network not available....please check your connection.");
			System.exit(0);
		}
		System.out.println("Message sent Successfully");
	}
	private static Message prepareMessage(Session session, String usn, String recepient,HashMap<String,Integer> map) {
		
		

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(usn));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
			message.setSubject("Response Status.........");
			message.setText("Total number of rows="+map.get("totalLines")+"\n"+"Number of lines Transferred="+map.get("sendedLines")+"\n"
			+"Number of lines not sended= "+map.get("remainingLines"));
			return message;
		} catch (Exception ex) {
			Logger.getLogger(SendEmail.class.getName()).log(Level.SEVERE, null, ex);
			
		}
		return null;
	}
}