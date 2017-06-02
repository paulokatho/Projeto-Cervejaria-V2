package com.algaworks.brewer.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.Clientes;
import com.algaworks.brewer.service.exception.CpfCnpjClienteJaCadastradoException;

/***
 * 
 * @author Katho
 *
 *	Na classe a assinatura @PrePersist/@PreUpdate para retirar a mascara do cpf/cnpj só funciona um pouquinho antes de persistir no banco as informações.
 *	Então aqui no findByCpfOuCnpj não vai pegar esse valor sem máscara e para isso utilizamos o cliente.getCpfOuCnpjSemFormatacao();
 *	No enum TipoPessoa foi acrescentado: removerFormatacao(String cpfOuCnpj)
 *	
 */

@Service
public class CadastroClienteService {

	@Autowired
	private Clientes clientes;
	
	@Transactional
	public void salvar(Cliente cliente) {
		Optional<Cliente> clienteExistente = clientes.findByCpfOuCnpj(cliente.getCpfOuCnpjSemFormatacao());
		if (clienteExistente.isPresent()) {
			throw new CpfCnpjClienteJaCadastradoException("CPF/CNPJ já cadastrado");
		}
		
		clientes.save(cliente);
	}
	
}
