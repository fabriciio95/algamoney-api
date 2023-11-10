package com.algamoney.api.service;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algamoney.api.model.Pessoa;
import com.algamoney.api.repository.PessoaRepository;

@Service
public class PessoaService {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Transactional
	public Pessoa salvar(Pessoa pessoa) {
		pessoa.getContatos().forEach(c -> c.setPessoa(pessoa));
		
		return pessoaRepository.save(pessoa);
	}
	
	@Transactional
	public Pessoa atualizar(Pessoa pessoa, Long codigo) {
		Pessoa pessoaSalva  = buscarPessoa(codigo);
		
		pessoa.getContatos().forEach(c -> c.setPessoa(pessoa));
		
		BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");
		
		return pessoaRepository.save(pessoaSalva);
	}

	@Transactional
	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
		Pessoa pessoa = buscarPessoa(codigo);
		
		pessoa.setAtivo(ativo);
		
	}
	
	private Pessoa buscarPessoa(Long codigo) {
		return pessoaRepository.findById(codigo)
				.orElseThrow(() -> new EmptyResultDataAccessException(1));
	}
}
