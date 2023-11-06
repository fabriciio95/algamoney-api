package com.algamoney.api.repository.lancamento;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algamoney.api.dto.LancamentoEstatisticaCategoria;
import com.algamoney.api.model.Lancamento;
import com.algamoney.api.repository.filter.LancamentoFilter;
import com.algamoney.api.repository.project.ResumoLancamento;

public interface LancamentoRepositoryQuery {

	Page<Lancamento> filtrar(LancamentoFilter filter, Pageable pageable);
	
	Page<ResumoLancamento> resumir(LancamentoFilter filter, Pageable pageable);
	
	List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia);
}
