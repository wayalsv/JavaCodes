package all.org.vyomlibrary;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Program for Mail Send of Wonders Errors
 *@author Sachin Mandavkar 
 */
public class MailSendToCustomer 
{
	static VyomSeleniumHandler selHandler = new VyomSeleniumHandler();
	final static String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	static String mailUserName ="";
	static String mailPassword = "";
	//String host = "10.60.0.124";

	public static Properties setProperty() throws Exception 
	{
		mailUserName = VyomSeleniumHandler.getProperties("MailUserId");		
		mailPassword = VyomSeleniumHandler.getProperties("MailPassword");	

		Properties props = new Properties();
		//props.put("mail.smtp.auth", "true");
		//props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "192.168.201.61");
		props.put("mail.smtp.port", "25");

		return props;

	}//End of setProperty()	

	public static Session setSession() throws Exception
	{
		Properties props = setProperty();
		Session session = Session.getInstance(props, new javax.mail.Authenticator() 
		{
			protected PasswordAuthentication getPasswordAuthentication() 
			{
				return new PasswordAuthentication(mailUserName,mailPassword);
			}
		});
		session.setDebug(true);
		return session;

	}//End of setSession()

	public static void sendMail(String MailSubject, String mailBody, String mailSendTo, Logger log) throws Exception
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String today = sdf.format(new Date());

			setSession();

			//String to = VyomSeleniumHandler.getProperties("SendTo");
			String cc = VyomSeleniumHandler.getProperties("SendCC");
			// Get the Session object.

			Session session = setSession();

			// Create a default MimeMessage object.
			Message message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(mailUserName,VyomSeleniumHandler.getProperties("MailSenderName")));

			// Set To: header field of the header.
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailSendTo));
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));

			// Set Subject: header field
			message.setSubject(MailSubject+"_"+today);

			// Now set the actual message
			StringBuilder msg = new StringBuilder();
			msg.append(mailBody);
			message.setText(msg.toString());

			// Send message
			Transport.send(message);
			log.info("Mail successfully send!");
		}catch(Exception e)
		{
			log.info("Error in SendMail :"+e);
		}
	}//End of sendMail

	public static void sendMail_With_Attachemnt(String mail_Sub, String mail_Body, String file_Name ,String file_Path,String mailSendTo, Logger log ) throws Exception
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH:mm:ss");
			String today = sdf.format(new Date());

			setSession();

			//String to = VyomSeleniumHandler.getProperties("SendTo");
			String cc = VyomSeleniumHandler.getProperties("SendCC");
			// Get the Session object.

			Session session = setSession();

			// Create a default MimeMessage object.
			Message message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(mailUserName, VyomSeleniumHandler.getProperties("MailSenderName")));

			// Set To: header field of the header.
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailSendTo));
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));

			// Set Subject: header field
			message.setSubject(mail_Sub+"_"+today);		

			BodyPart messageBodyPart = new MimeBodyPart();

			// Now set the actual message
			messageBodyPart.setText(mail_Body);

			// Create a multipar message
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			// Part two is attachment
			messageBodyPart = new MimeBodyPart();

			//String filename = "Input_one.";
			DataSource source = new FileDataSource(file_Path);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(file_Name);
			multipart.addBodyPart(messageBodyPart);

			// Send the complete message parts
			message.setContent(multipart);

			// Send message
			Transport.send(message);

			log.info("Mail Sent successfully with attachment....");
		}catch(Exception e)
		{
			log.info("Error in sendMail_With_Attachemnt :"+e);
		}
	}//End of sendMail_With_Attachemnt

	/*public static void main(String args[]) throws Exception
	{
		String body="Dear All, \n\n    Mesoka Site is Down for Aadhar Campaign Process..\n\nRegards,\nRobotics Team.\nHDFC Life";
		String sub = "Test"; 
		//sendMail(body);

		sendMail_With_Attachemnt(sub, body, "D:\\temp_test.txt");
	}*/
}
