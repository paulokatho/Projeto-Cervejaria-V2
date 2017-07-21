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
	}
	
	TabelaItens.prototype.iniciar = function() {
		this.autocomplete.on('item-selecionado', onItemSelecionado.bind(this));
	}
	
	function onItemSelecionado(evento, item) {
		var resposta = $.ajax({
			url: 'item', //dessa maneira substitiu o ultimo caminho da url por /item, ou seja, /item será para onde o post será enviado
			method: 'POST',
			data: {
				codigoCerveja: item.codigo
			}
		});
		
		resposta.done(function(data) {
			console.log('retorno', data);
		});
	}
	
	return TabelaItens;
	
}());

$(function() {
	
	var autocomplete = new Brewer.Autocomplete();
	autocomplete.iniciar();
	
	var tabelaItens = new Brewer.TabelaItens(autocomplete);
	tabelaItens.iniciar();
	
});