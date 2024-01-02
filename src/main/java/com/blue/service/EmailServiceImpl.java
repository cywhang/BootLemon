package com.blue.service;


import com.blue.dto.EmailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void sendMail(EmailVO vo) {
		try {
			String host = "smtp.naver.com";
			final String username = "cywhang2";
			final String password = "cksdud369";
			int port = 465;

			// 메일 내용
			String recipient = vo.getReceiveMail();
			String subject = vo.getSubject();
			String body = vo.getMessage();

			Properties props = System.getProperties();

			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", port);
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.ssl.trust", host);
			props.put("mail.smtp.ssl.protocols", "TLSv1.2");

			Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				String un = username;
				String pw = password;

				protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(un, pw);
				}
			});
			session.setDebug(true);

			Message mimeMessage = new MimeMessage(session);
			mimeMessage.setFrom(new InternetAddress("cywhang2@naver.com"));
			mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			mimeMessage.setSubject(subject);
			mimeMessage.setText(body);

			Transport.send(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
