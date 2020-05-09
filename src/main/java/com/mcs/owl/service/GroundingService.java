package com.mcs.owl.service;

import java.util.stream.Stream;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;

public class GroundingService {
	OntologyServices ontologyServices = OntologyServices.getOntologyServices();
	
	public OWLIndividual addGroundingToAgentIndividual( OWLIndividual agentIndi, String operaterId) {
		OWLIndividual indvidual =   ontologyServices.addIndividualToObjectProperty(
				ontologyServices.getOperationIdIRI("Grounding_"+operaterId+"_Agent"), agentIndi,
				ontologyServices.getServiceTemplateOntology(),
				ontologyServices.getSupportByObjectPropertyFromProcess());
		
		OWLClassAssertionAxiom ax =  this.ontologyServices.getDataFactory().getOWLClassAssertionAxiom(getwsdlGroundingClass(), indvidual);
		ontologyServices.getOntologyManager().addAxiom(ontologyServices.getGroundingTemplateOntology(), ax);
		
		// add the sub 
		addAtomicProcess(operaterId,indvidual);
		return indvidual;

	}
	public OWLClass getwsdlGroundingClass() {
		IRI atomicProcessClassIRI = IRI.create("http://www.daml.org/services/owl-s/1.2/Grounding.owl#WsdlGrounding");
		Stream<OWLClass> owlClassInProcess = ontologyServices.getGroundingOntology().classesInSignature();
		//ontologyServices.getServiceProcessTemplateOntology().classesInSignature().forEach(System.out::println);
		OWLClass atomicProcessClass = owlClassInProcess.filter(cls -> cls.getIRI().equals(atomicProcessClassIRI)).findAny().get();
		return atomicProcessClass;
	}
	
	public void addAtomicProcess(String operatorId, OWLIndividual indvidual) {
		
		ontologyServices.addIndividualToObjectProperty(ontologyServices.getOperationIdIRI("WsdlGrounding_"+operatorId),
				indvidual, ontologyServices.getGroundingTemplateOntology(), ontologyServices.gethasAtomicProcessObjectPropertyFromProcess());
	}
	
}
