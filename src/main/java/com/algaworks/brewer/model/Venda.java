package com.algaworks.brewer.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "venda")
@DynamicUpdate//Para o cancelamento de venda, pois é somente para atualizar o status. 25-5 04:40
public class Venda {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;

	@Column(name = "data_criacao")
	private LocalDateTime dataCriacao;

	@Column(name = "valor_frete")
	private BigDecimal valorFrete;

	@Column(name = "valor_desconto")
	private BigDecimal valorDesconto;

	@Column(name = "valor_total")
	private BigDecimal valorTotal = BigDecimal.ZERO;//inicia valor com 0 para no box Total na tela de venda fique com 0,00 quando a tela é renderizada

	private String observacao;

	@Column(name = "data_hora_entrega")
	private LocalDateTime dataHoraEntrega;

	@ManyToOne
	@JoinColumn(name = "codigo_cliente")
	private Cliente cliente;

	@ManyToOne
	@JoinColumn(name = "codigo_usuario")
	private Usuario usuario;

	@Enumerated(EnumType.STRING)
	private StatusVenda status = StatusVenda.ORCAMENTO;

	/***
	 * Quando um venda é editada, os itens tem que ser removidos do banco e inserinos em um novo registro, e assim esse novo registro 
	 * 	deve ser o unico no banco e o antigo apagado. Se não o banco fica inconsistente. A coleção de itens não pode ficar orfã.
	 * É muito importante lembrar desse recurso e sempre verificar no banco de dados se realmente está sendo salvo certinho tudo.
	 * O 'orphanRemoval' está na Aula 25-5 13:40
	 */
	@OneToMany(mappedBy = "venda", cascade = CascadeType.ALL ,orphanRemoval = true)
	private List<ItemVenda> itens = new ArrayList<>(); //inicializando o array para não correr risco de dar null pointer exception

	@Transient
	private String uuid;

	@Transient
	private LocalDate dataEntrega;

	@Transient
	private LocalTime horarioEntrega;

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public BigDecimal getValorFrete() {
		return valorFrete;
	}

	public void setValorFrete(BigDecimal valorFrete) {
		this.valorFrete = valorFrete;
	}

	public BigDecimal getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(BigDecimal valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public LocalDateTime getDataHoraEntrega() {
		return dataHoraEntrega;
	}

	public void setDataHoraEntrega(LocalDateTime dataHoraEntrega) {
		this.dataHoraEntrega = dataHoraEntrega;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public StatusVenda getStatus() {
		return status;
	}

	public void setStatus(StatusVenda status) {
		this.status = status;
	}

	public List<ItemVenda> getItens() {
		return itens;
	}

	public void setItens(List<ItemVenda> itens) {
		this.itens = itens;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public LocalDate getDataEntrega() {
		return dataEntrega;
	}

	public void setDataEntrega(LocalDate dataEntrega) {
		this.dataEntrega = dataEntrega;
	}

	public LocalTime getHorarioEntrega() {
		return horarioEntrega;
	}

	public void setHorarioEntrega(LocalTime horarioEntrega) {
		this.horarioEntrega = horarioEntrega;
	}

	public boolean isNova() {
		return codigo == null;
	}
	
	public void adicionarItens(List<ItemVenda> itens) {
		this.itens = itens;
		this.itens.forEach(i -> i.setVenda(this));
	}
	
	public BigDecimal getValorTotalItens() {
		return getItens().stream()
				.map(ItemVenda::getValorTotal)
				.reduce(BigDecimal::add)
				.orElse(BigDecimal.ZERO); 
	}
	
	public void calcularValorTotal() { //deixando o serviço de calculo para que o valorTotal permaneça na tela quando, por exemplo a tela é renderizada ao clicar em salvar e a validação for acionada. Aula 23-16 25:46		
		this.valorTotal = calcularValorTotal(getValorTotalItens(), getValorFrete(), getValorDesconto());
	}
	
	/**
	 * Verifica a quntidade de dias que tem o orçamento.
	 * 'inicio' recebe a data de criação ou o horario atual(quando for uma nova venda)
	 * ChronoUnit verifica em dias a diferença entre a data 'inicio' e a data de agora 'now()'
	 * Depois de implementar utilizamos na tela de CadastroVenda.html para utilizar no box Criação exibindo quando foi realizado o orçamento
	 * Aula 25-4 19:45
	 * @return
	 */
	public Long getDiasCriacao() {
		LocalDate inicio = dataCriacao != null ? dataCriacao.toLocalDate() : LocalDate.now();
		return ChronoUnit.DAYS.between(inicio, LocalDate.now());
	}
	
	public boolean isSalvarPermitido() {
		return !status.equals(StatusVenda.CANCELADA);
	}
	
	/**
	 * Esse metodo e o de cima utilizados na tela de venda para desabilitar os botoes caso o usuario não tenha permissão para cancelar ou editar a venda.
	 * 25-5 17:40
	 * @return
	 */
	public boolean isSalvarProibido() {
		return !isSalvarPermitido();
	}
	
	private BigDecimal calcularValorTotal(BigDecimal valorTotalItens, BigDecimal valorFrete, BigDecimal valorDesconto) {
		BigDecimal valorTotal = valorTotalItens
				.add(Optional.ofNullable(valorFrete).orElse(BigDecimal.ZERO))
				.subtract(Optional.ofNullable(valorDesconto).orElse(BigDecimal.ZERO));//No valorTotalItens .add o valor do frete e subtrai o valor de desconto
		return valorTotal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Venda other = (Venda) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

}
