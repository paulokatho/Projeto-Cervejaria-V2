package com.algaworks.brewer.controller.validator;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.algaworks.brewer.model.Venda;

/***
 * 
 * @author Katho
 * Essa classe é para validação da tela de cadastroVenda.html, é uma maneira alternativa de validação oferecida pelo Spring.
 * 
 */

@Component
public class VendaValidator implements Validator{
	//Aula 23:16 04:40

	@Override
	public boolean supports(Class<?> clazz) {
		return Venda.class.isAssignableFrom(clazz);//Essa classe será mapeada para validação
	}

	@Override
	public void validate(Object target, Errors errors) {
		//Rejeita o campo cliente.codigo caso ele seja vazio e emite a mensagem para selecionar o cliente
		ValidationUtils.rejectIfEmpty(errors, "cliente.codigo","", "Selecione um cliente na pesquisa rápida");		
		
		Venda venda = (Venda) target;
		validarSeInformouApenasHorarioEntrega(errors, venda);
		validarSeInformouItens(errors, venda);
		validarValorTotalNegativo(errors, venda);//aula 23-16 28:08
	}

	private void validarValorTotalNegativo(Errors errors, Venda venda) {
		if (venda.getValorTotal().compareTo(BigDecimal.ZERO) < 0) {
			errors.reject("", "Valor total não pode ser negativo");
		}
	}

	private void validarSeInformouItens(Errors errors, Venda venda) {
		if(venda.getItens().isEmpty()) {
			errors.reject("", "Adicione pelo menos uma cerveja na venda");
		}
	}

	private void validarSeInformouApenasHorarioEntrega(Errors errors, Venda venda) {
		if (venda.getHorarioEntrega() != null && venda.getDataEntrega() == null) {
			errors.rejectValue("dataEntrega", "", "Informe uma data da entrega pra um horario");
		}
	}

	
}
