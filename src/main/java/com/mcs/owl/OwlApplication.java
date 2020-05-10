package com.mcs.owl;

import java.util.Optional;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mcs.owl.service.OntologyServices;
import com.mcs.owl.service.ServicesService;
import com.mcs.owl.utils.SwaggerParser;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.servers.Server;

@SpringBootApplication
public class OwlApplication {

	public static void main(String[] args) {
		
		OntologyServices ontologyServices = OntologyServices.getOntologyServices();
		ServicesService servicesService = new ServicesService();
		
		OpenAPI openAPI =  new SwaggerParser().Parser();
		
		Optional<Server> serverUrl = openAPI.getServers().stream().findFirst();
		
	
		
		//System.out.println("No Of Size " + openAPI.);
		
		//create Main Service
		Paths paths = openAPI.getPaths();
		paths.forEach((pathUrl,pathItem)->{
			if(serverUrl.isPresent()) {
				pathUrl = serverUrl.get().getUrl() + pathUrl;
			}
			servicesService.addAgentIndividual(pathUrl,pathItem);
			
		});
		ontologyServices.saveOntology();
		
		
	
	}

}
