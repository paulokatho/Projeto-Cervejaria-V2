Brewer = Brewer || {};

Brewer.DialogoExcluir = (function() {
	
	function DialogoExcluir() {
		this.exclusaoBtn = $('.js-exclusao-btn')
	}
	
	DialogoExcluir.prototype.iniciar = function() {
		this.exclusaoBtn.on('click', onExcluirClicado.bind(this));
		
		if (window.location.search.indexOf('excluido') > -1) {//Vai montar a url com a palavra excluído e assim o search procura pela palavra excluido e exibo o 'swal' como popup. Aula 25-1 22:26
			swal('Pronto!', 'Excluído com sucesso!', 'success');
		}
	}
	
	function onExcluirClicado(evento) {
		event.preventDefault();
		var botaoClicado = $(evento.currentTarget);//icone de excluir. Esse é o kara que dispara o evento na tela
		var url = botaoClicado.data('url');
		var objeto = botaoClicado.data('objeto');
		
		/* Para exclusão é utilizado o Swal alert (sweet alert), visitar o site que tem vários modelos de alert 
		 * Em primeiro são as configurações do objeto
		 * Segundo são as configurações depois de clicar nesse objeto */
		swal({
			title: 'Tem certeza?',
			text: 'Excluir "' + objeto + '"? Você não poderá recuperar depois.',
			showCancelButton: true,
			confirmButtonColor: '#DD6B55',
			confirmButtonText: 'Sim, exclua agora!',
			closeOnConfirm: false
		}, onExcluirConfirmado.bind(this, url));
	}
	
	function onExcluirConfirmado(url) {
		//console.log('url', url);//para testar se aparece a url no console quando clica em OK na hora da exclusão
		$.ajax({
			url: url,
			method: 'DELETE',
			success: onExcluidoSucesso.bind(this),
			error: onErroExcluir.bind(this)//caso dê erro, gera esse tratamento que foi implementado no CervejasController
		});
	}
	
	function onExcluidoSucesso() {
		var urlAtual = window.location.href;
		var separador = urlAtual.indexOf('?') > -1 ? '&' : '?';//se a url ja tem ? ele coloca &, se não ?. Aula 25-1 20:38
		var novaUrl = urlAtual.indexOf('excluido') > -1 ? urlAtual : urlAtual + separador + 'excluido';//
		
		//pegamos a nova url gerada no prototype.iniciar
		window.location = novaUrl;//carrega a nova url na tela para indicar que foi excluído e assim manipular o evento de excluir
	}
	
	function onErroExcluir(e) {
		console.log('ahahahah', e.responseText);
		swal('Oops!', e.responseText, 'error');//a mensagem desse 'e.' é definido no CadastroCervejaService
	}
	
	return DialogoExcluir;
	
}());

$(function() {
	var dialogo = new Brewer.DialogoExcluir();
	dialogo.iniciar();
});
