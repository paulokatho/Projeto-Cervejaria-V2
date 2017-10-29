package com.algaworks.brewer.config.format;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

//Aula 27-2 12:15

/**
 * 
 * @author Katho
 *
 * Classe responsável por converter o valor BigDecimal. Esse recurso do spring ajuda e facilita a vida, pois quando fazemos a I17 e informamos
 *  um valor no campo de valor na pesquisa de vendas, esse valor ele tentar converter para dólar ao invés de manter como Real.
 *  Por isso é necessario criar essa classe.
 * Ela implementa BigDecimal que funciona assim, quando vai exibir na tela utiliza BigDecimal para String e quando faz o parse do valor da requisição
 *  e esse parse de String para bigDecimal.
 * Sempre que precisarmos tratar algum tipo de dado, podemos criar um formater, nesse caso esse formater é para tratar o bigDecimal, se for Integer
 *  terá outro tratamento. 
 */
public class BigDecimalFormatter implements Formatter<BigDecimal>{

	private DecimalFormat decimalFormat;
	
	//construtor da classe
	public BigDecimalFormatter(String pattern) {
		NumberFormat format = NumberFormat.getInstance(new Locale("pt", "BR"));
		decimalFormat = (DecimalFormat) format;
		decimalFormat.setParseBigDecimal(true);
		decimalFormat.applyPattern(pattern);
	}
	
	@Override
	public String print(BigDecimal object, Locale locale) {
		return decimalFormat.format(object);
	}

	@Override
	public BigDecimal parse(String text, Locale locale) throws ParseException {
		return (BigDecimal) decimalFormat.parse(text);
	}
	
}
