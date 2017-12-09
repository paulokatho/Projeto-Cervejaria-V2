var Brewer = Brewer || {};

Brewer.UploadFoto = (function() {
	
	function UploadFoto() {
		this.inputNomeFoto = $('input[name=foto]');
		this.inputContentType = $('input[name=contentType]');
		this.novaFoto = $('input[name=novaFoto]');//se ta cadastrando uma nova foto passa por aqui e para que seja acrescentado a string 'temp/'. Aula 25-2 20:14 e 22:35 
		this.inputUrlFoto = $('input[name=urlFoto]');//seta a url com nome da foto para fz o upload de foto. Precisamos desse kara na Classe Cervela.java. Aula 28.05 23:11 e 24:40
		
		this.htmlFotoCervejaTemplate = $('#foto-cerveja').html();
		this.template = Handlebars.compile(this.htmlFotoCervejaTemplate);
		
		this.containerFotoCerveja = $('.js-container-foto-cerveja');
		
		this.uploadDrop = $('#upload-drop');		
		this.imgLoading = $('.js-img-loading');
	}
	
	UploadFoto.prototype.iniciar = function () {
		var settings = {
				type: 'json',
				filelimit: 1,
				allow: '*.(jpg|jpeg|png)',
				action: this.containerFotoCerveja.data('url-fotos'),
				complete: onUploadCompleto.bind(this),
				beforeSend: adicionarCsrfToken, //para acrescentar o token csrf na página de cadastro cerveja ao carregar a foto. Ver CadastroCerveja.html
				loadstart: onLoadStart.bind(this)//loadstart é um call back que tem e podemos usar, mas é melhor estudar um pouco melhor depois no google. Aula 28-6 19:10
		}
		
		UIkit.uploadSelect($('#upload-select'), settings);
		UIkit.uploadDrop(this.uploadDrop, settings);
		
		if (this.inputNomeFoto.val()) {//qdo renderiza a pagina vê se tem foto aí ali em renderizaFoto ele não coloca  o caminho temp/
			renderizarFoto.call(this, { 
				nome:  this.inputNomeFoto.val(), 
				contentType: this.inputContentType.val(), 
				url: this.inputUrlFoto.val()});
		}
	}
	
	function onLoadStart() {
		this.imgLoading.removeClass('hidden');
	}
	
	function onUploadCompleto(resposta) {//Se chegar aqui é uma foto nova. Aqui tem que colocar o /temp, pois em FotoCerveja.html não está o caminho completo para a pasta temp para poder funcionar a edição. Aula 25-2 18:56
		this.novaFoto.val('true');
		this.inputUrlFoto.val(resposta.url);//usa essa variavel para armzenar o nome da foto com a url e usa ali em cima no renderizarFoto.call()
		this.imgLoading.addClass('hidden');
		renderizarFoto.call(this, resposta);
	}
	
	function renderizarFoto(resposta) {
		this.inputNomeFoto.val(resposta.nome);
		this.inputContentType.val(resposta.contentType);
		
		this.uploadDrop.addClass('hidden');
		
		/*** Tirado na aula 28-5 17:10 e tem comentario em FotoCerveja.html - O que estiver com esse comentario azul foi tirado nessa aula
		 * var foto = '';//para funcionar inserir foto nova tem que acrescentar caminho 'temp/'. Depois chama ali no foto: foto<-essa é a variavel. Aula 25-2 18:40
		if (this.novaFoto.val() == 'true') {
			foto = 'temp/';
		}
		foto += resposta.nome;
		*/
		
		/*** var htmlFotoCerveja = this.template({foto: foto});*///foto: está em FotoCerveja.html, funciona para quando clicar para editar a foto. Aula 25-2 16:17
		var htmlFotoCerveja = this.template({url: resposta.url});
		this.containerFotoCerveja.append(htmlFotoCerveja);
		
		$('.js-remove-foto').on('click', onRemoverFoto.bind(this));
	}
	
	function onRemoverFoto() {
		$('.js-foto-cerveja').remove();
		this.uploadDrop.removeClass('hidden');
		this.inputNomeFoto.val('');
		this.inputContentType.val('');
		this.novaFoto.val('false');
	}
	
	function adicionarCsrfToken(xhr) {
		var token = $('input[name=_csrf]').val();
		var header = $('input[name=_csrf_header]').val();
		xhr.setRequestHeader(header, token);
	}
	
	return UploadFoto;
	
})();

$(function() {
	var uploadFoto = new Brewer.UploadFoto();
	uploadFoto.iniciar();
});