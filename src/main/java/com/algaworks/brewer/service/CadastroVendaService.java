package com.algaworks.brewer.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Vendas;

@Service
public class CadastroVendaService {

	@Autowired
	private Vendas vendas;
	
	@Transactional
	public Venda salvar(Venda venda) {
		if(venda.isNova()) {
			venda.setDataCriacao(LocalDateTime.now());
		} else {//esse else vem em 25-4 10:15
			Venda vendaExistente = vendas.findOne(venda.getCodigo());
			venda.setDataCriacao(vendaExistente.getDataCriacao());//setamos a data de criação, pois não queremos mudar a data que foi criado
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
		
		return vendas.saveAndFlush(venda);//saveAndFlush salva e retorna as alterações do banco no objeto venda e assim temos, por exemplo o codigo da venda. Aula 24-7 07:28
	}

	@Transactional
	public void emitir(Venda venda) {
		venda.setStatus(StatusVenda.EMITIDA);
		salvar(venda);
		
	}

	/**
	 * Essa anotação foi configurada em SecurityConfig em @EnableGlobalMethodSecurity
	 * Esquema de segurança muito grande para que somente quem fez a venda ou administrador possa cancelar a venda
	 * Também é necessario acrescentar campo hidden em CadastroVenda.html th:field="*{usuario}. 25-5 07:29
	 * hasRole é uma regra para que somente o admin possa cancelar qualquer venda. Foi inserido no banco uma nova permissao e grupo_permissao. 25-5 14:08
	 * @param venda
	 */
 
	@PreAuthorize("#venda.usuario == principal.usuario or hasRole('CANCELAR_VENDA')")//fazendo referencia ao usuario principal e o # é para informar que é para pegar o usuario que está dentro de venda (que fez a venda)
	@Transactional
	public void cancelar(Venda venda) {//Aula 25-5 02:00
		Venda vendaExistente = vendas.findOne(venda.getCodigo());
		
		vendaExistente.setStatus(StatusVenda.CANCELADA);//para atualizar somente o status da venda de fato usamos na classe Venda.java @DynamicUpdate. 25-05 04:48
		vendas.save(vendaExistente);
	}

}
