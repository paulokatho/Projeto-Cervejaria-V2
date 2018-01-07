package com.algaworks.brewer.config;

import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.Cervejas;

@Configuration
@ComponentScan(basePackageClasses = Cervejas.class)
@EnableJpaRepositories(basePackageClasses = Cervejas.class, enableDefaultTransactions = false)
@EnableTransactionManagement
@ComponentScan(basePackageClasses = Cervejas.class)
public class JPAConfig {

	
	/***
	 * Quando estivermos usando local vamos usar esse jdbc/brewerDB e quando estiver em produção vamos usar a configuração do heroku.
	 * Vamos utilizar o mesmo conceito de profile que utilizamos na geração de imagem no amazon S3 
	 * Lembrando que podemos criar @Bean para métodos também como abaixo, especificando que esse bean será criado, por exemplo quando o profile local for chamado.
	 * No pom.xml foi colocado uma biblioteca 
	 * 
	 * Aula 28-8 09:00
	 * 			 25:08 - Tem que configurar o MailConfig e também S3Config para alternar os profiles 'prod ou local'
	 * 
	 * @return
	 * 
	 */
	@Profile("local")
	@Bean
	public DataSource dataSource() {
		JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
		dataSourceLookup.setResourceRef(true);
		return dataSourceLookup.getDataSource("jdbc/brewerDB");//esse vem do datasource que esta em context.xml //mudou na aula 28-8 08:07
	}
	
	@Profile("prod")
	@Bean
	public DataSource dataSourceProd() throws URISyntaxException {
		URI jdbUri = new URI(System.getenv("JAWSDB_URL"));

	    String username = jdbUri.getUserInfo().split(":")[0];
	    String password = jdbUri.getUserInfo().split(":")[1];
	    String port = String.valueOf(jdbUri.getPort());
	    String jdbUrl = "jdbc:mysql://" + jdbUri.getHost() + ":" + port + jdbUri.getPath();
	    
	    BasicDataSource dataSource = new BasicDataSource();
	    dataSource.setUrl(jdbUrl);
	    dataSource.setUsername(username);
	    dataSource.setPassword(password);
	    dataSource.setInitialSize(10);
	    return dataSource;
	}
	
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.MYSQL);
		adapter.setShowSql(false);
		adapter.setGenerateDdl(false);
		adapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
		return adapter;
	}
	
	@Bean
	public EntityManagerFactory entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setJpaVendorAdapter(jpaVendorAdapter);
		factory.setPackagesToScan(Cerveja.class.getPackage().getName());
		factory.setMappingResources("sql/consultas-nativas.xml");//ensinando ao jpa onde fica o arquivo consultas-vendas para poder utilizar em consultas nativas. Aula 26-5 11:20
		factory.afterPropertiesSet();
		return factory.getObject();
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}
	
}
