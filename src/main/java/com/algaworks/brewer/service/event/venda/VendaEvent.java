package com.algaworks.brewer.service.event.venda;

import com.algaworks.brewer.model.Venda;

/**
 * 
 * @author Katho
 * VendaEvent criado para auxiliar na hora de decrementar o valor dos itens no estoque no box em Dashboard.html
 * Aula 26-7 01:28
 */
public class VendaEvent {

	private Venda venda;

	public VendaEvent(Venda venda) {
		this.venda = venda;
	}

	public Venda getVenda() {
		return venda;
	}
	
}
