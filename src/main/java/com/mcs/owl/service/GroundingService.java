package com.mcs.owl.service;

import org.semanticweb.owlapi.model.OWLIndividual;

public class GroundingService {
	OntologyServices ontologyServices = OntologyServices.getOntologyServices();
	
	public OWLIndividual addGroundingToAgentIndividual( OWLIndividual agentIndi, String operaterId) {
		OWLIndividual indvidual =   ontologyServices.addIndividualToObjectProperty(
				ontologyServices.getOperationIdIRI("Grounding_"+operaterId+"_Agent"), agentIndi,
				ontologyServices.getServiceTemplateOntology(),
				ontologyServices.getSupportByObjectPropertyFromProcess());
		
		return indvidual;

	}
}
