package com.algaworks.brewer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExpiradoController {

	@GetMapping("/expired")
	public String acessoExpirado() {
		return "Expired";
	}
	
}
