package com.algaworks.brewer.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Vendas;

@Service
public class CadastroVendaService {

	@Autowired
	private Vendas vendas;
	
	@Transactional
	public void salvar(Venda venda) {
		if(venda.isNova()) {
			venda.setDataCriacao(LocalDateTime.now());
		}
		/* ---Retirado na aula 23:16 - 25:48
		BigDecimal valorTotalItens = venda.getItens().stream()
				.map(ItemVenda::getValorTotal)
				.reduce(BigDecimal::add)
				.get();//mapeia o valor total e soma todo mundo acima e recupera todos com get()
		BigDecimal valorTotal = calcularValorTotal(valorTotalItens, venda.getValorFrete(), venda.getValorDesconto());
		//como o valor do frete/desconto não podem ser nulos, então usamos o Optional.ofNullable que retorna um big decimal de zero ou o valor que está no get() - aula 23-15 16:23
		venda.setValorTotal(valorTotal);
		*/
		
		if(venda.getDataEntrega() != null) {
			venda.setDataHoraEntrega(LocalDateTime.of(venda.getDataEntrega()
					, venda.getHorarioEntrega() != null ? venda.getHorarioEntrega() : LocalTime.NOON));
		}
		
		vendas.save(venda);
	}

}
