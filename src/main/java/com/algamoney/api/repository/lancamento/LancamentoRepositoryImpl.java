package com.algamoney.api.repository.lancamento;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.algamoney.api.model.Lancamento;
import com.algamoney.api.model.Lancamento_;
import com.algamoney.api.repository.filter.LancamentoFilter;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Page<Lancamento> filtrar(LancamentoFilter filter, Pageable pageable) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
		
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		Predicate[] predicates = criarRestricoes(filter, builder, root);
		
		criteria.where(predicates);
		
		
		TypedQuery<Lancamento> query = entityManager.createQuery(criteria);
		
		adicionarPaginacaoQuery(query, pageable);
		
		List<Lancamento> lancamentos = query.getResultList();
		
		return new PageImpl<>(lancamentos, pageable, totalElementos(filter));
	}


	private Predicate[] criarRestricoes(LancamentoFilter filter, CriteriaBuilder builder, Root<Lancamento> root) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if(!ObjectUtils.isEmpty(filter.getDescricao())) {
			predicates.add(builder.like(
					builder.lower(root.get(Lancamento_.descricao)),
					"%" + filter.getDescricao().toLowerCase() + "%"
			));
		}
		
		if(filter.getDataVencimentoDe() != null) {
			predicates.add(
					   builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento),
							   filter.getDataVencimentoDe())
					);
		}
		
		if(filter.getDataVencimentoAte() != null) {
			predicates.add(
					builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), filter.getDataVencimentoAte())
			);
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}
	

	private void adicionarPaginacaoQuery(TypedQuery<Lancamento> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroPagina = paginaAtual * totalRegistrosPorPagina;
		
		query.setFirstResult(primeiroRegistroPagina);
		query.setMaxResults(totalRegistrosPorPagina);
	}
	
	private Long totalElementos(LancamentoFilter filter) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		Predicate[] predicates = criarRestricoes(filter, builder, root);
		
		criteria.where(predicates);
		
		criteria.select(builder.count(root));
		
		return entityManager.createQuery(criteria).getSingleResult();
		
	}

}
