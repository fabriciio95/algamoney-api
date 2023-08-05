package com.algamoney.api.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.algamoney.api.model.Pessoa;
import com.algamoney.api.repository.PessoaRepository;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@PostMapping
	public ResponseEntity<Pessoa> cadastrar(@RequestBody @Valid Pessoa pessoa) {
		
		pessoa = pessoaRepository.save(pessoa);
		
	   URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/{codigo}").buildAndExpand(pessoa.getCodigo()).toUri();
	   
	   return ResponseEntity.created(uri).body(pessoa);
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<Pessoa> buscar(@PathVariable Long codigo) {
		return pessoaRepository.findById(codigo)
				 .map(ResponseEntity::ok)
				 .orElse(ResponseEntity.notFound().build());
	}
	
}
