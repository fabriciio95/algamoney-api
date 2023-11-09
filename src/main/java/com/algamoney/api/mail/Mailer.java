package com.algamoney.api.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.algamoney.api.model.Lancamento;
import com.algamoney.api.model.Usuario;


@Component
public class Mailer {

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine templateEngine;
	
//	@Autowired
//	private LancamentoRepository lancRep;
//
//	@EventListener
//	private void teste(ApplicationReadyEvent event) {
//		
//		enviarEmail("fabriciusiqueira@hotmail.com",  Arrays.asList("fabriciusiqueira@gmail.com") ,
//				"TESTE LANCAMENTOS", "mail/aviso-lancamentos-vencidos", Map.of("lancamentos", lancRep.findAll()));
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
	
	public void enviarEmail(String remetente, List<String> destinatarios, String assunto, String template, Map<String, Object> parametros) {
		
			
			Context context = new Context(new Locale("pt", "BR"));
			
			parametros.entrySet().forEach(p -> context.setVariable(p.getKey(), p.getValue()));
			
			String mensagem = templateEngine.process(template, context);
			
			enviarEmail(remetente, destinatarios, assunto, mensagem);
		
	}
	
	public void avisarSobreLancamentosVencidos(List<Lancamento> lancamentosVencidos, List<Usuario> destinatarios) {
		
		Map<String, Object> variaveis = new HashMap<>();
		
		variaveis.put("lancamentos", lancamentosVencidos);
		
		List<String> emails = destinatarios.stream().map(Usuario::getEmail).toList();
		
		this.enviarEmail("fabriciusiqueira@hotmail.com", emails, "Lançamentos Vencidos", "mail/aviso-lancamentos-vencidos", variaveis);
	}
}
