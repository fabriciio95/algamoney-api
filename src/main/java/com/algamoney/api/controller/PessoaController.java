package com.algamoney.api.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algamoney.api.event.RecursoCriadoEvent;
import com.algamoney.api.model.Pessoa;
import com.algamoney.api.repository.PessoaRepository;
import com.algamoney.api.service.PessoaService;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private PessoaService pessoaService;
	
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and hasAuthority('SCOPE_WRITE')")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Pessoa cadastrar(@RequestBody @Valid Pessoa pessoa, HttpServletResponse httpServleResponse) {
		
	   pessoa =  pessoaService.salvar(pessoa);
	   
	   publisher.publishEvent(new RecursoCriadoEvent(this, httpServleResponse, pessoa.getCodigo()));
	   
	   return pessoa;
	}
	
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and hasAuthority('SCOPE_READ')")
	@GetMapping
	public Page<Pessoa> pesquisar(@RequestParam(required = false) String nome, Pageable pageable) {
		
		if(nome != null) {
			return pessoaRepository.findByNomeContainingIgnoreCase(nome, pageable);
		}
		
		return pessoaRepository.findAll(pageable);
	}
	
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and hasAuthority('SCOPE_READ')")
	@GetMapping("/{codigo}")
	public ResponseEntity<Pessoa> buscar(@PathVariable Long codigo) {
		return pessoaRepository.findById(codigo)
				 .map(ResponseEntity::ok)
				 .orElse(ResponseEntity.notFound().build());
	}
	
	@PreAuthorize("hasAuthority('ROLE_REMOVER_PESSOA') and hasAuthority('SCOPE_WRITE')")
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletar(@PathVariable Long codigo) {
		pessoaRepository.deleteById(codigo);
	}
	
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and hasAuthority('SCOPE_WRITE')")
	@PutMapping("/{codigo}")
	public Pessoa atualizar(@PathVariable Long codigo, @RequestBody @Valid Pessoa pessoa) {
		return pessoaService.atualizar(pessoa, codigo);
	}
	
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and hasAuthority('SCOPE_WRITE')")
	@PutMapping("/{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizarPropriedadeAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {
		pessoaService.atualizarPropriedadeAtivo(codigo, ativo);
	}
	
}
