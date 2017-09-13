package com.algaworks.brewer.dto;

/***
 * 
 * @author Katho
 * A classe retorna um json para pegar no javascript e poder renderizar na tela no dashboard.
 * Também utiliza o sql nativo em resources/sql/consultas-nativas.xml
 * . Criado o arquivo xml consultas-nativas na pasta sql em main/resources
 * . Também foi criado o método totalPorMes() em vendasImpl.
 * . Para funcionar foi informado no jpaConfig o caminho para a pasta onde está o sql nativo.
 * . É preciso atender a requisição que vai realizar a consulta e retornar a nossa lista para o js. Ela será feita em VendasController, mesmo sendo Dashboard, pois faz parte da venda. No metodo listarTotalVendasPorMes()
 * . No dashboard.graficos.js também tem que mapear essa consulta sql.
 */

public class VendaMes {

	private String mes;
	private Integer total;
	
	public VendaMes() {
		
	}
	
	public VendaMes(String mes, Integer total) {
		this.mes = mes;
		this.total= total;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}
	
	
}
