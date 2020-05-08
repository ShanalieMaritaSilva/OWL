package com.mcs.owl.service;

import java.util.stream.Stream;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.Parameter;

public class ServicesService {

	OntologyServices ontologyServices = OntologyServices.getOntologyServices();
	ProcessService processService = new ProcessService();
	ProfileService profileService = new ProfileService();
	


	public void addAgentIndividual(PathItem pathItem) {
		
		IRI agentIRI = ontologyServices.getServiceOperationIdIRI(pathItem.getGet().getOperationId()+"_Agent");
		
		OWLIndividual agentIndividual = ontologyServices.addIndividualToClass(agentIRI,
				getServiceClass(), ontologyServices.getServiceTemplateOntology());
		
		OWLIndividual profileIndividual = profileService.addProfileIndividual(pathItem.getGet(), agentIndividual);
		
		profileService.addServiceClass(pathItem.getGet(),profileIndividual);
		
		for(Parameter parm : pathItem.getGet().getParameters()) {
			OWLObjectProperty paramRefInDomin = processService.getObjectPropertyFromDomain(parm.getName());
			if(paramRefInDomin != null) {
				OWLIndividual processIndi = processService.addIndividual(pathItem.getGet(),parm, paramRefInDomin);
				profileService.addProcessIndividualProfileIndividual(profileIndividual, processIndi);
			}
			
		}
		
		
		
		
	}
	private OWLClass getServiceClass() {
		IRI serviceClassIRI = IRI.create("http://www.daml.org/services/owl-s/1.2/Service.owl#Service");
		Stream<OWLClass> owlClassInProcess = ontologyServices.getServiceOntology().classesInSignature();
		return owlClassInProcess.filter(cls -> cls.getIRI().equals(serviceClassIRI)).findAny().get();
	}

}
