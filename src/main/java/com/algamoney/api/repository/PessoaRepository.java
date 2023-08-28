package com.algamoney.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.algamoney.api.model.Pessoa;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long>{

	Page<Pessoa> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
