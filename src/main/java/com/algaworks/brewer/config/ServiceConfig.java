package com.algaworks.brewer.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.algaworks.brewer.service.CadastroCervejaService;
import com.algaworks.brewer.storage.FotoStorage;

/***
 * 
 * @author Katho
 * Ali no FotoStorage foi mudado aula 28:05 07:30 e a partir dele ele vai scanear e procurar beans que estiverem
 *  no pacoto storage. Temos os Bens FotoStorageLocal e FotoStorageS3, que também são @Components
 * Depois que implementamos aqui o FotosController vai usar esses beans para salvar a foto, e como o Spring vai
 *  saber qual dos @Beans que é pra usar?
 *  Se tentar subir dessa maneira ele vai estourar um exception 'No qualifying bean of type', pois ele está esperando 1 bean, 
 *   mas ele encontrou 2
 * Aí que entra so @Profile - aula 28:05 09:39
 *  No momento que estamos subindo a aplicação no servidor, conseguimos informar qual profile ele vai ser, 'local' ou 'prod'.
 *   Aí ele assume o profile correto
 *   Conseguimos setar um default em AppInitilizer no metodo onStartup()
 *   
 * Subiu a foto para o S3 no tempo de aula 14:40, tem algumas configurações no s3 pra mexer, fica ligado
 *
 * 
 */

@Configuration
@ComponentScan(basePackageClasses = { CadastroCervejaService.class, FotoStorage.class })
public class ServiceConfig {

	//Retirado na aula 28.05 06:38. Tem explicação em FotoStorage.
	//Esse bean era só pra explicar que era possivel criar um bean nosso para ser usado no sistema.
	/*@Bean 
	public FotoStorage fotoStorage() {
		return new FotoStorageLocal();
	}*/
	
}
