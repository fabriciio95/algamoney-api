package com.algamoney.api.mail;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class Mailer {

	@Autowired
	private JavaMailSender mailSender;

//	@EventListener
//	private void teste(ApplicationReadyEvent event) {
//		enviarEmail("fabriciusiqueira@hotmail.com",  Arrays.asList("fabriciusiqueira@gmail.com") , "TESTE", "<h1> teste </h1>");
//	}
	
	public void enviarEmail(String remetente, List<String> destinatarios, String assunto, String mensagem) {
		
		
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			
			helper.setFrom(remetente);
			helper.setTo(destinatarios.toArray(new String[0]));
			helper.setSubject(assunto);
			helper.setText(mensagem, true);
			
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new RuntimeException("Problemas com o envio de e-mail", e);
		}
	}
}
