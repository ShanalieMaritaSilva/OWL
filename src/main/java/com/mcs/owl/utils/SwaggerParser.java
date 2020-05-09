package com.mcs.owl.utils;

import java.util.Optional;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import com.mcs.owl.service.OntologyServices;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

public class SwaggerParser {

	
	OntologyServices ontologyServices = OntologyServices.getOntologyServices();
	
	public OpenAPI Parser() {
		
		return new OpenAPIV3Parser().read("C:\\Users\\shana\\Documents\\MCS\\Project\\OWL-S\\Swagger-master\\Swagger-master\\LH_public_API_swagger_2_0.json");
		
	}
	
	public OWLEntity getResourceFromDomain(String objectName) {
		IRI resourceIRI = IRI.create(ontologyServices.getDomainObjectPropertyIRI() + objectName);
		
		Stream<OWLEntity> entity = ontologyServices.getDomainOntology().entitiesInSignature(resourceIRI);
		
		Optional<OWLEntity> objectProperties = entity.findAny();
		if(objectProperties.isPresent()) {
			return objectProperties.get();
		}
	
		
		System.out.println("Not Found property" + " - getObjectPropertyFromDomain : "+ objectName);
		return null;
	}
}
