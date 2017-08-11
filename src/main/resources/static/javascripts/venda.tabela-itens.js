/*
	Esse js é responsavel por receber os itens que forem acrescentados na tela 
		de venda de itens.
	Ex: Quando selecionamos um item, tipo cerveja Brewer a tabela Item tem que ser informada
		que um item foi selecionado.
		
	Para que isso funcione certo o nosso objeto js Brewer já tem que estar criado.
*/

Brewer.TabelaItens = (function() {
	
	function TabelaItens(autocomplete) {
		this.autocomplete = autocomplete;
		this.tabelaCervejasContainer = $('.js-tabela-cervejas-container');
	}
	
	TabelaItens.prototype.iniciar = function() {
		this.autocomplete.on('item-selecionado', onItemSelecionado.bind(this));
	}
	
	function onItemSelecionado(evento, item) {
		var resposta = $.ajax({ //dessa maneira substitiu o ultimo caminho da url por /item, ou seja, /item será para onde o post será enviado
			url: 'item',
			method: 'POST',
			data: {
				codigoCerveja: item.codigo
			}
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	//aula 23-8 9:00
	function onItemAtualizadoNoServidor(html) {
		this.tabelaCervejasContainer.html(html);
		$('.js-tabela-cerveja-quantidade-item').on('change', onQuantidadeItemAlterado.bind(this));//renderiza a tabela na tela com o item selecionado
		$('.js-tabela-item').on('dblclick', onDoubleClick);//quando da 2 clicks na tela ele exibe opção de excluir
		//aula 23-10 10:22
		$('.js-exclusao-item-btn').on('click', onExclusaoItemClick.bind(this));
	}
	
	function onQuantidadeItemAlterado(evento) {
		var input = $(evento.target);
		var quantidade = input.val();
		var codigoCerveja = input.data('codigo-cerveja');
		
		var resposta = $.ajax({
			url: 'item/' + codigoCerveja,
			method: 'PUT',
			data: {
				quantidade: quantidade
			}
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	function onDoubleClick(evento) {
		$(this).toggleClass('solicitando-exclusao');//o (this) é o currentTarget - aula 23-9 07:00
	}
	
	function onExclusaoItemClick(evento) {
		var codigoCerveja = $(evento.target).data('codigo-cerveja');//codigo que está no botão de excluir de TabelaItensVenda.html
		var resposta = $.ajax({
			url: 'item/' + codigoCerveja,
			method: 'DELETE'
		});
		
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	return TabelaItens;
	
}());

$(function() {
	
	var autocomplete = new Brewer.Autocomplete();
	autocomplete.iniciar();
	
	var tabelaItens = new Brewer.TabelaItens(autocomplete);
	tabelaItens.iniciar();
	
});