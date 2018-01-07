package com.algaworks.brewer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

/***
 * 
 * @author Katho
 * Classe de configuração para o serviço da Amazon S3 que vamos armazenas as imagens e thumbnais la. Aula 28.4 10:12
 * Foi colocado a dependencia dele no pom.
 * Dentro da pasta: Acces Key Amazon para o projeto - Brewer existe o arquivo access key que fiz o download da amazon
 * E essas informações são usadas em brewer-s3.properties
 * 
 * Com esse bean configuration vamos conseguir acessar a amazon e armazenar imagens, recuperar, apagar e etc.
 * E para esse bean funcionar tem que ir em AppInitializer e acrescentar o S3Config la.
 * Para a aula tem que lembrar de apagar o banco (itens_venda, venda e cervejas).
 * Não pode esquecer que no e-mailConfig também envia a imagem da cerveja, vai ter que mexer lá também depois.
 */

@Configuration
//@PropertySource(value = { "file://${HOME}/.brewer-s3.properties" }, ignoreResourceNotFound = true)// no windows não funciona como na aula 24-4 12:54
@PropertySource(value = { "classpath:brewer-s3.properties" }, ignoreResourceNotFound = true)//O ignoreResourceNotFound é pq não temos acesso ao arquivo e caminho lá do servidor
public class S3Config {

	@Autowired
	private Environment env;
	
	@Bean
	public AmazonS3 amazonS3() {
		AWSCredentials credentials = new BasicAWSCredentials(//Lembrar de configurar nossas imagens lá na Amazon e configurar no heroku(AWS_ACCESS_KEY_ID e AWS_SECRET_ACCESS_KEY). Aula 28.8 28:07
				env.getProperty("AWS_ACCESS_KEY_ID"), env.getProperty("AWS_SECRET_ACCESS_KEY"));
		AmazonS3 amazonS3 = new AmazonS3Client(credentials, new ClientConfiguration());
		Region regiao = Region.getRegion(Regions.SA_EAST_1); //essa regiao (SA_EAST_1)tem que ser a mesma que aparece na sua conta na amazon no link ex: https://console.aws.amazon.com/iam/home?region=sa-east-1
		amazonS3.setRegion(regiao);
		return amazonS3;
	}
}
