package com.algaworks.brewer.service.event.cerveja;

import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Cerveja;

public class CervejaSalvaEvent {

	private Cerveja cerveja;

	public CervejaSalvaEvent(Cerveja cerveja) {
		this.cerveja = cerveja;
	}

	public Cerveja getCerveja() {
		return cerveja;
	}
	
	public boolean temFoto() {
		return !StringUtils.isEmpty(cerveja.getFoto());
	}
	
	public boolean isNovaFoto() {//usado em CervejaListener, pois só pode salvar a foto caso a foto seja nova. Aula 25-2 28:48
		return cerveja.isNovaFoto();
	}
	
}
