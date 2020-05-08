package com.mcs.owl;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mcs.owl.service.OntologyServices;
import com.mcs.owl.service.ProcessService;
import com.mcs.owl.service.ProfileService;
import com.mcs.owl.utils.SwaggerParser;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;

@SpringBootApplication
public class OwlApplication {

	public static void main(String[] args) {
		
		OntologyServices ontologyServices = OntologyServices.getOntologyServices();
		ProcessService processService = new ProcessService();
		ProfileService profileService = new ProfileService();
		
		
		OpenAPI openAPI =  new SwaggerParser().Parser();
		System.out.println("No Of Size " + openAPI.getPaths().size());
		
		//get nearest Path
		PathItem paths = openAPI.getPaths().get("/references/airports/nearest/{latitude},{longitude}");
		System.out.print(paths.getGet().getParameters());
		System.out.print(paths.getGet().getResponses());
		
		//get the class from schema
		OWLObjectProperty paramObjectProperty = processService.getObjectPropertyFromDomain("longitude");
		System.out.println(paramObjectProperty);
		
		OWLIndividual processIndi = processService.addIndividual(paths.getGet());
		
		ontologyServices.saveOntology();
	
		//get profile
		try {
			
			//profileService.addService(openAPI);
			profileService.addSubClass(paths.getGet(),processIndi);
			ontologyServices.importProcessOntologyToProfile();
			ontologyServices.saveOntologyProfile();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
