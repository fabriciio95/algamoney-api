package com.algamoney.api.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algamoney.api.event.RecursoCriadoEvent;
import com.algamoney.api.model.Pessoa;
import com.algamoney.api.repository.PessoaRepository;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Pessoa cadastrar(@RequestBody @Valid Pessoa pessoa, HttpServletResponse httpServleResponse) {
		
	   pessoa =  pessoaRepository.save(pessoa);
	   
	   publisher.publishEvent(new RecursoCriadoEvent(this, httpServleResponse, pessoa.getCodigo()));
	   
	   return pessoa;
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<Pessoa> buscar(@PathVariable Long codigo) {
		return pessoaRepository.findById(codigo)
				 .map(ResponseEntity::ok)
				 .orElse(ResponseEntity.notFound().build());
	}
	
}
