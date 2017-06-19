Brewer = Brewer || {};

Brewer.MultiSelecao = (function() {
	
	function MultiSelecao() {
		this.statusBtn = $('.js-status-btn');
		this.selecaoCheckbox = $('.js-selecao');
	}
	
	MultiSelecao.prototype.iniciar = function() {
		this.statusBtn.on('click', onStatusBtnClicado.bind(this));
	}
	
	function onStatusBtnClicado(event) {
		var botaoClicado = $(event.currentTarget);
		var status = botaoClicado.data('status');
		
		var checkBoxSelecionados = this.selecaoCheckbox.filter(':checked');
		var codigos = $.map(checkBoxSelecionados, function(c) {
			return $(c).data('codigo');
		});
		
		//só entra aqui se no usuariosPesquisa for selecionado usuarios no checkbox, se não dá uma bad request
		if (codigos.length > 0) {
			$.ajax({
				url: '/brewer/usuarios/status',
				method: 'PUT',
				data: {
					codigos: codigos,
					status: status //status é passado para o usuariosController com um @RequestParam("status")
				}, 
				success: function() {
					window.location.reload();
				}
			});
			
		}
	}
	
	return MultiSelecao;
	
}());

$(function() {
	var multiSelecao = new Brewer.MultiSelecao();
	multiSelecao.iniciar();
});