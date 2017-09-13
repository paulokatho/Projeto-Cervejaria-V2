var Brewer = Brewer || {};

Brewer.GraficoVendaPorMes = (function() {
	
	function GraficoVendaPorMes() {
		this.ctx = $('#graficoVendasPorMes')[0].getContext('2d');
	}
	
	GraficoVendaPorMes.prototype.iniciar = function() {
		$.ajax({
			url: 'vendas/totalPorMes',
			method: 'GET',
			success: onDadosRecebidos.bind(this)
		});
	}
	
	function onDadosRecebidos(vendaMes) {//recebe por parametro a vendaMes. Aula 25-6 16:30
		var meses = [];//recebe os meses
		var valores = [];//recebe os valores
		vendaMes.forEach(function(obj) {//para cada venda do mes entrega no objeto 'obj'
			meses.unshift(obj.mes);// 'unshift coloca em ordem por primeiro, assim fica organizado como no order by do sql
			valores.unshift(obj.total);//mes e total são os atributos de VendaMes.java
		});
		
		var graficoVendasPorMes = new Chart(this.ctx, {
		    type: 'line',
		    data: {
		    	//labels: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun'],//***para teste. Aula 25-6 16:40
		    	labels: meses,
		    	datasets: [{
		    		label: 'Vendas por mês',
		    		backgroundColor: "rgba(26,179,148,0.5)",
	                pointBorderColor: "rgba(26,179,148,1)",
	                pointBackgroundColor: "#fff",
	                //data: [10, 5, 7, 2, 9] // teste como o labels:
	                data: valores
		    	}]
		    },
		});
	}
	
	return GraficoVendaPorMes;
	
}());

$(function() {
	var graficoVendaPorMes = new Brewer.GraficoVendaPorMes();
	graficoVendaPorMes.iniciar();
});
