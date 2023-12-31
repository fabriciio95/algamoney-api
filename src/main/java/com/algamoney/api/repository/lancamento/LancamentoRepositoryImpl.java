package com.algamoney.api.repository.lancamento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.algamoney.api.dto.LancamentoEstatisticaCategoria;
import com.algamoney.api.dto.LancamentoEstatisticaDia;
import com.algamoney.api.dto.LancamentoEstatisticaPessoa;
import com.algamoney.api.model.Categoria_;
import com.algamoney.api.model.Lancamento;
import com.algamoney.api.model.Lancamento_;
import com.algamoney.api.model.Pessoa_;
import com.algamoney.api.repository.filter.LancamentoFilter;
import com.algamoney.api.repository.project.ResumoLancamento;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

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
	
	@Override
	public Page<ResumoLancamento> resumir(LancamentoFilter filter, Pageable pageable) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<ResumoLancamento> criteria = builder.createQuery(ResumoLancamento.class);
		
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		criteria.select(builder.construct(ResumoLancamento.class,
				root.get(Lancamento_.codigo), root.get(Lancamento_.descricao),
			    root.get(Lancamento_.dataVencimento), root.get(Lancamento_.dataPagamento), 
			    root.get(Lancamento_.valor), root.get(Lancamento_.tipo), 
			    root.get(Lancamento_.categoria).get(Categoria_.nome),
			    root.get(Lancamento_.pessoa).get(Pessoa_.nome)));
		
		Predicate[] predicates = criarRestricoes(filter, builder, root);
		
		criteria.where(predicates);
		
		
		TypedQuery<ResumoLancamento> query = entityManager.createQuery(criteria);
		
		adicionarPaginacaoQuery(query, pageable);
		
		List<ResumoLancamento> lancamentos = query.getResultList();
		
		return new PageImpl<>(lancamentos, pageable, totalElementos(filter));
		
	}
	
	@Override
	public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<LancamentoEstatisticaCategoria> query = builder.createQuery(LancamentoEstatisticaCategoria.class);
		
		Root<Lancamento> root = query.from(Lancamento.class);
		
		query.select(builder.construct(LancamentoEstatisticaCategoria.class, 
				root.get(Lancamento_.categoria),
				builder.sum(root.get(Lancamento_.valor))));
		
		LocalDate primeiroDia = mesReferencia.withDayOfMonth(1);
		LocalDate ultimoDia = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());
		
		query.where(
				builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), primeiroDia),
				builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), ultimoDia)
		);
		
		query.groupBy(root.get(Lancamento_.categoria));
			
		TypedQuery<LancamentoEstatisticaCategoria> typedQuery = entityManager.createQuery(query);
		
		return typedQuery.getResultList();
	}
	
	@Override
	public List<LancamentoEstatisticaDia> porDia(LocalDate mesReferencia) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<LancamentoEstatisticaDia> query = builder.createQuery(LancamentoEstatisticaDia.class);
		
		Root<Lancamento> root = query.from(Lancamento.class);
		
		query.select(builder.construct(LancamentoEstatisticaDia.class, 
				root.get(Lancamento_.tipo),
				root.get(Lancamento_.dataVencimento),
				builder.sum(root.get(Lancamento_.valor))));
		
		
		LocalDate primeiroDia = mesReferencia.withDayOfMonth(1);
		LocalDate ultimoDia = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());
		
		query.where(
				builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), primeiroDia),
				builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), ultimoDia)
		);
		
		query.groupBy(root.get(Lancamento_.dataVencimento), root.get(Lancamento_.tipo));
			
		TypedQuery<LancamentoEstatisticaDia> typedQuery = entityManager.createQuery(query);
		
		return typedQuery.getResultList();
	}
	
	@Override
	public List<LancamentoEstatisticaPessoa> porPessoa(LocalDate inicio, LocalDate fim) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<LancamentoEstatisticaPessoa> query = builder.createQuery(LancamentoEstatisticaPessoa.class);
		
		Root<Lancamento> root = query.from(Lancamento.class);
		
		query.select(builder.construct(LancamentoEstatisticaPessoa.class, 
				root.get(Lancamento_.tipo),
				root.get(Lancamento_.pessoa),
				builder.sum(root.get(Lancamento_.valor))));
		
		
		query.where(
				builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), inicio),
				builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), fim)
		);
		
		query.groupBy(root.get(Lancamento_.tipo), root.get(Lancamento_.pessoa));
			
		TypedQuery<LancamentoEstatisticaPessoa> typedQuery = entityManager.createQuery(query);
		
		return typedQuery.getResultList();
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
	

	private void adicionarPaginacaoQuery(TypedQuery<?> query, Pageable pageable) {
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
