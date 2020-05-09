package com.mcs.owl;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mcs.owl.service.Matcher;
import com.mcs.owl.service.OntologyServices;
import com.mcs.owl.service.ServicesService;
import com.mcs.owl.utils.SwaggerParser;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;

@SpringBootApplication
public class OwlApplication {

	public static void main(String[] args) {
		
		OntologyServices ontologyServices = OntologyServices.getOntologyServices();
		ServicesService servicesService = new ServicesService();
		
		OpenAPI openAPI =  new SwaggerParser().Parser();
		System.out.println("No Of Size " + openAPI.getPaths().size());
		
		//create Main Service
		Paths paths = openAPI.getPaths();
		paths.forEach((pathUrl,pathItem)->{
			servicesService.addAgentIndividual(pathUrl,pathItem);
			
		});
		ontologyServices.saveOntology();
	new Matcher().getOutputs("ss");
//		
		
	
	}

}
