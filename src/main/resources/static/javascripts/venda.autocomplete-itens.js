//também houve uma customização no css 'brewer.css'
//acrescentar no layout padrão o numeral js que vimos na aula 22-7 mais ou menos aos 27 minutos.
Brewer = Brewer || {};

Brewer.Autocomplete = (function() {
	
	function Autocomplete() {
		this.skuOuNomeInput = $('.js-sku-nome-cerveja-input');
		var htmlTemplateAutocomplete = $('#template-autocomplete-cerveja').html();
		this.template = Handlebars.compile(htmlTemplateAutocomplete);
	}
	
	Autocomplete.prototype.iniciar = function() {
		var options = {
			url: function(skuOuNome) {
				return '/brewer/cervejas?skuOuNome=' + skuOuNome;
			},
			getValue: 'nome',
			minCharNumber: 3,
			requestDelay: 300,
			ajaxSettings: {
				contentType: 'application/json'
			},
			template: {
				type: 'custom',
				method: function(nome, cerveja) {
					cerveja.valorFormatado = Brewer.formatarMoeda(cerveja.valor);
					return this.template(cerveja);
				}.bind(this)
			}
		};
		
		this.skuOuNomeInput.easyAutocomplete(options);
	}
	
	return Autocomplete
	
}());

$(function() {
	
	var autocomplete = new Brewer.Autocomplete();
	autocomplete.iniciar();
	
})


/*
	Acrescentamos os seguintes arquivos em javascritp/vendors e stylesheets/vendors
	js = jquery.easy-autocomplete.min.js;
	css= easy-autocomplete.min.css / easy-autocomplete.themes.min.css
*/