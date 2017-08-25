Brewer = Brewer || {};

Brewer.PesquisaRapidaCliente = (function() {
	
	function PesquisaRapidaCliente() {
		this.pesquisaRapidaClientesModal = $('#pesquisaRapidaClientes');
		this.nomeInput = $('#nomeClienteModal');
		this.pesquisaRapidaBtn = $('.js-pesquisa-rapida-clientes-btn');
		this.containerTabelaPesquisa = $('#containerTabelaPesquisaRapidaClientes');
		this.htmlTabelaPesquisa = $('#tabela-pesquisa-rapida-cliente').html();//pegando o conteudo html do template pra renderizar no handlebars
		this.template = Handlebars.compile(this.htmlTabelaPesquisa);//compilndo o handlebars do container e jogando na variavel template
		this.mensagemErro = $('.js-mensagem-erro');
	}
	
	PesquisaRapidaCliente.prototype.iniciar = function() {
		this.pesquisaRapidaBtn.on('click', onPesquisaRapidaClicado.bind(this));		
		this.pesquisaRapidaClientesModal.on('shown.bs.modal', onModalShow.bind(this));
	}
	
	function onModalShow() {
		this.nomeInput.focus();
	}
	
	function onPesquisaRapidaClicado(event) {
		event.preventDefault();
		
		$.ajax({
			url: this.pesquisaRapidaClientesModal.find('form').attr('action'),
			method: 'GET',
			contentType: 'application/json',
			data: {
				nome: this.nomeInput.val()
			}, 
			success: onPesquisaConcluida.bind(this),
			error: onErroPesquisa.bind(this)
		});
	}
	
	//Obs: para exibir o resultado do banco é necessário criar uma tabela usando handle bars dentro da pasta 'templates/hbs' que estão nossos handle bars
	function onPesquisaConcluida(resultado) {
		//console.log('resultado: ', resultado); //teste para ver se esta trazendo os nomes do banco no console do google F12
		this.mensagemErro.addClass('hidden');

		var html = this.template(resultado);//resultado é o que está vindo de TabelaPesquisaRapidaClientes. O trecho que vai gerar o conteudo da nossa busca
		this.containerTabelaPesquisa.html(html);//o container vai receber o html gerado/buscado acima.
		
		/*nesse momento o cliente vai estar disponivel para seleção e por isso estara aqui e será chamado uma nova classe Brewer.TabelaClientePesquisaRapida
			vamos criar um novo objeto aqui, pois o cliente que aparece na pesquisa não existe antes da pesquisa, mas depois que já fez a pesquisa
			se pesquisar novamente esse nome não muda no js e por isso a necessidade de criar esse novo objeto.
		*/
		var tabelaClientePesquisaRapida = new Brewer.TabelaClientePesquisaRapida(this.pesquisaRapidaClientesModal);
		tabelaClientePesquisaRapida.iniciar();
	}
	
	//Foi colocado uma div class='alert alert-danger' em PesquisaRapidaCliente para exibir a msg quando houver erro.
	function onErroPesquisa() {
		this.mensagemErro.removeClass('hidden');//porem deixando assim ainda vai dar erro 500 e vai deixar log no servidor(Illegal Argument Exception) e para tratar tem o metodo ResponseEntity<void> em ClientesController
	}
	
	return PesquisaRapidaCliente;
	
}());

	/*Criando uma nova classe para pegar o id do cara que for selecionado no pesquisa rápida de cliente*/
Brewer.TabelaClientePesquisaRapida = (function() {
	
	function TabelaClientePesquisaRapida(modal) {
		this.modalCliente = modal;
		this.cliente = $('.js-cliente-pesquisa-rapida');
	}
	
	TabelaClientePesquisaRapida.prototype.iniciar = function() {
		this.cliente.on('click', onClienteSelecionado.bind(this));
	}
	
	function onClienteSelecionado(evento) {
		this.modalCliente.modal('hide');
		
		var clienteSelecionado = $(evento.currentTarget);
		$('#nomeCliente').val(clienteSelecionado.data('nome'));
		$('#codigoCliente').val(clienteSelecionado.data('codigo'));
	}
	
	return TabelaClientePesquisaRapida;
	
}());

$(function() {
	var pesquisaRapidaCliente = new Brewer.PesquisaRapidaCliente();
	pesquisaRapidaCliente.iniciar();
});