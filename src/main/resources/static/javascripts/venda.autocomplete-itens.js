//também houve uma customização no css 'brewer.css'
//acrescentar no layout padrão o numeral js que vimos na aula 22-7 mais ou menos aos 27 minutos.
Brewer = Brewer || {};

Brewer.Autocomplete = (function() {
	
	function Autocomplete() {
		this.skuOuNomeInput = $('.js-sku-nome-cerveja-input');
		var htmlTemplateAutocomplete = $('#template-autocomplete-cerveja').html();
		this.template = Handlebars.compile(htmlTemplateAutocomplete);
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
	}
	
	Autocomplete.prototype.iniciar = function() {
		var options = {
			url: function(skuOuNome) {
				return this.skuOuNomeInput.data('url') + "?skuOuNome=" + skuOuNome;
			}.bind(this),
			getValue: 'nome',
			minCharNumber: 3,
			requestDelay: 300,
			ajaxSettings: {
				contentType: 'application/json'
			},
			template: {
				type: 'custom',
				method: template.bind(this)
			},
			//utilizado na aula 23-5 em 22:24 para iniciar o js do venda.tabela-itens.js
			list: {
				onChooseEvent: onItemSelecionado.bind(this)
			}
		};
		
		this.skuOuNomeInput.easyAutocomplete(options);
	}
	
	function onItemSelecionado() {
		//esse cara será escutado em venda.tabela-itens.js no prototype
		this.emitter.trigger('item-selecionado', this.skuOuNomeInput.getSelectedItemData());
		this.skuOuNomeInput.val('');
		this.skuOuNomeInput.focus();
	}
	
	function template(nome, cerveja) {		
			cerveja.valorFormatado = Brewer.formatarMoeda(cerveja.valor);
			return this.template(cerveja);
	}
	
	return Autocomplete
	
}());

//aqui não tem o autoComplete.iniciar(), pois ele vai ficar em venda.tabela-itens, pois ele tem que estar iniciado
	//para poder pegar o item que estiver selecionado na tela e a quantidade também.
// mas para funcionar na tela de vendas tem que colocar em CadastroVendas o caminho do js /javascripts/venda.tabela-itens.js


/*
	Acrescentamos os seguintes arquivos em javascritp/vendors e stylesheets/vendors
	js = jquery.easy-autocomplete.min.js;
	css= easy-autocomplete.min.css / easy-autocomplete.themes.min.css
*/