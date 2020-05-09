package com.mcs.owl.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;

public class ProcessService {
	
	
	OntologyServices ontologyServices = OntologyServices.getOntologyServices();
	
	public OWLDataProperty getParmValDataPropertyFromProcess() {
		IRI parameterTypeIRI = IRI.create("http://www.daml.org/services/owl-s/1.2/Process.owl#parameterValue");
		Optional<OWLDataProperty> dataProperties = ontologyServices.getProcessOntology().dataPropertiesInSignature()
				.filter(object -> object.getIRI().equals(parameterTypeIRI)).findAny();
		if(dataProperties.isPresent()) {
			return dataProperties.get();
		}
		System.out.println("Not Found property" + " - getParmValDataPropertyFromProcess");
		return null;
	}
	
	public OWLDataProperty getRefUriDataPropertyFromProcess() {
		IRI parameterTypeIRI = IRI.create("http://www.daml.org/services/owl-s/1.2/generic/Expression.owl#refURI");
		Optional<OWLDataProperty> dataProperties = ontologyServices.getExpOntology().dataPropertiesInSignature()
				.filter(object -> object.getIRI().equals(parameterTypeIRI)).findAny();
		if(dataProperties.isPresent()) {
			return dataProperties.get();
		}
		System.out.println("Not Found property" + " - getRefUriDataPropertyFromProcess");
		return null;
	}
	public OWLClass getAtomicProcessClass() {
		IRI atomicProcessClassIRI = IRI.create("http://www.daml.org/services/owl-s/1.2/Process.owl#AtomicProcess");
		List<OWLClass> owlClassInProcess = ontologyServices.getProcessOntology().classesInSignature().collect(Collectors.toList());
	//	ontologyServices.getServiceProcessTemplateOntology().classesInSignature().forEach(System.out::println);
		OWLClass atomicProcessClass = owlClassInProcess.stream().filter(cls -> cls.getIRI().equals(atomicProcessClassIRI)).findAny().get();
		return atomicProcessClass;
	}
	
	public OWLClass getInputClass() {
		IRI atomicProcessClassIRI = IRI.create("http://www.daml.org/services/owl-s/1.2/Process.owl#Input");
		Stream<OWLClass> owlClassInProcess = ontologyServices.getProcessOntology().classesInSignature();
		//ontologyServices.getServiceProcessTemplateOntology().classesInSignature().forEach(System.out::println);
		OWLClass atomicProcessClass = owlClassInProcess.filter(cls -> cls.getIRI().equals(atomicProcessClassIRI)).findAny().get();
		return atomicProcessClass;
	}
	public OWLClass getOutputClass() {
		IRI atomicProcessClassIRI = IRI.create("http://www.daml.org/services/owl-s/1.2/Process.owl#Output");
		Stream<OWLClass> owlClassInProcess = ontologyServices.getProcessOntology().classesInSignature();
		//ontologyServices.getServiceProcessTemplateOntology().classesInSignature().forEach(System.out::println);
		OWLClass atomicProcessClass = owlClassInProcess.filter(cls -> cls.getIRI().equals(atomicProcessClassIRI)).findAny().get();
		return atomicProcessClass;
	}
	
	public OWLIndividual addInputIndividual(Operation operation, Parameter parm, OWLEntity paramRefInDomin) {
	
		OWLIndividual methodIndividual =  ontologyServices.addIndividualToClass(ontologyServices.getOperationIdIRI("Process_"+operation.getOperationId()), getAtomicProcessClass(), 
				ontologyServices.getServiceProcessTemplateOntology());
		
		OWLDatatype stringDatatype = ontologyServices.getDataFactory().getStringOWLDatatype();
		//comment
		if(!parm.getDescription().isEmpty()) {
			OWLLiteral commentLiteral =  ontologyServices.getDataFactory().getOWLLiteral(parm.getDescription(),stringDatatype);
			addRDFAxioms(OWLRDFVocabulary.RDFS_COMMENT,commentLiteral,IRI.create(methodIndividual.toStringID()));
		}

		//label
		OWLLiteral labelLiteral =  ontologyServices.getDataFactory().getOWLLiteral("Process_"+operation.getOperationId() +"(ATOMIC)",stringDatatype);
		addRDFAxioms(OWLRDFVocabulary.RDFS_LABEL,labelLiteral,IRI.create(methodIndividual.toStringID()));
		
		//param input
		OWLIndividual inputIndividal = addInputIndividual(operation,parm.getName(),paramRefInDomin);
		addObjectPropertyAssertations(methodIndividual,inputIndividal);
		
		return methodIndividual;
	}
	
	public OWLIndividual addOutputIndividual(OWLEntity paramRefInDomin, String responseRef, OWLIndividual profileIndividual) {
		
		IRI reposneIri = ontologyServices.getOperationIdIRI(responseRef);
		OWLIndividual reponseIndi = ontologyServices.addIndividualToObjectProperty(reposneIri,profileIndividual,ontologyServices.getServiceTemplateOntology(),
				ontologyServices.getHasOutputObjectPropertyFromProcess());
		
		OWLClassAssertionAxiom ax =  this.ontologyServices.getDataFactory().getOWLClassAssertionAxiom(getOutputClass(), reponseIndi);
		ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProcessTemplateOntology(), ax);
		
		if(paramRefInDomin.isOWLClass()) {
			OWLClassAssertionAxiom ass =  this.ontologyServices.getDataFactory().getOWLClassAssertionAxiom(paramRefInDomin.asOWLClass(), reponseIndi);
			ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProcessTemplateOntology(), ass);
		}
	
		if(paramRefInDomin != null) {
			OWLDatatype anyURIDatatype = this.ontologyServices.getDataFactory().getOWLDatatype(
					OWL2Datatype.XSD_ANY_URI.getIRI());
			OWLLiteral labelLiteral =  this.ontologyServices.getDataFactory().getOWLLiteral(paramRefInDomin.toStringID(),anyURIDatatype);
			OWLDataPropertyAssertionAxiom dataPropertyAssertion = ontologyServices.getDataFactory().getOWLDataPropertyAssertionAxiom(getRefUriDataPropertyFromProcess(), reponseIndi,
					labelLiteral);
			ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProcessTemplateOntology(), dataPropertyAssertion);
		}

		OWLDatatype stringDatatype = this.ontologyServices.getDataFactory().getOWLDatatype(
				OWL2Datatype.XSD_STRING.getIRI());
		OWLLiteral parmValueLiteral =  this.ontologyServices.getDataFactory().getOWLLiteral(responseRef,stringDatatype);
		OWLDataPropertyAssertionAxiom dataPropertyAssertionValue = ontologyServices.getDataFactory().getOWLDataPropertyAssertionAxiom(getParmValDataPropertyFromProcess(), reponseIndi,
				parmValueLiteral);
	
		ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProcessTemplateOntology(), dataPropertyAssertionValue);
		
		return reponseIndi;
	}
	


	public void addRDFAxioms(OWLRDFVocabulary voc , OWLLiteral literal, IRI inOdividualPropertyIRI) {
		 OWLAnnotation ann = ontologyServices.getDataFactory().getOWLAnnotation( ontologyServices.getDataFactory().getOWLAnnotationProperty(voc.getIRI()), literal);
		 OWLAxiom axiom = ontologyServices.getDataFactory().getOWLAnnotationAssertionAxiom(inOdividualPropertyIRI, ann);
		// OWLAnnotationAssertionAxiom  axiom =  ontologyServices.getDataFactory().getOWLAnnotationAssertionAxiom();
		 ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProcessTemplateOntology(), axiom);
	}
	
	private OWLIndividual addInputIndividual(Operation operation, String parameterName, OWLEntity paramRefInDomin) {
		OWLIndividual paramIndividual =  this.ontologyServices.getDataFactory().getOWLNamedIndividual(ontologyServices.getOperationIdIRI("Process_"+operation.getOperationId()+"_"+parameterName));
		OWLClassAssertionAxiom ax =  this.ontologyServices.getDataFactory().getOWLClassAssertionAxiom(getInputClass(), paramIndividual);
		ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProcessTemplateOntology(), ax);
		
		if(paramRefInDomin != null) {
		
			OWLDatatype anyURIDatatype = this.ontologyServices.getDataFactory().getOWLDatatype(
					OWL2Datatype.XSD_ANY_URI.getIRI());
			OWLLiteral labelLiteral =  this.ontologyServices.getDataFactory().getOWLLiteral(paramRefInDomin.toStringID(),anyURIDatatype);
			OWLDataPropertyAssertionAxiom dataPropertyAssertion = ontologyServices.getDataFactory().getOWLDataPropertyAssertionAxiom(getRefUriDataPropertyFromProcess(), paramIndividual,
					labelLiteral);
			ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProcessTemplateOntology(), dataPropertyAssertion);
		}

		OWLDatatype stringDatatype = this.ontologyServices.getDataFactory().getOWLDatatype(
				OWL2Datatype.XSD_STRING.getIRI());
		OWLLiteral parmValueLiteral =  this.ontologyServices.getDataFactory().getOWLLiteral(parameterName,stringDatatype);
		OWLDataPropertyAssertionAxiom dataPropertyAssertionValue = ontologyServices.getDataFactory().getOWLDataPropertyAssertionAxiom(getParmValDataPropertyFromProcess(), paramIndividual,
				parmValueLiteral);
	
		ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProcessTemplateOntology(), dataPropertyAssertionValue);
		return paramIndividual;
		
	}
	public void addObjectPropertyAssertations(OWLIndividual methodIndividual, OWLIndividual inputIndividal) {   
		OWLAxiom axiom = ontologyServices.getDataFactory().getOWLObjectPropertyAssertionAxiom(ontologyServices.getHasInputObjectPropertyFromProcess(), methodIndividual,
				inputIndividal);
		
		ontologyServices.getOntologyManager().addAxiom(ontologyServices.getServiceProcessTemplateOntology(),axiom);
		
	}
	
	public OWLIndividual addProcessToAgentIndividual( OWLIndividual agentIndi) {
		return ontologyServices.addIndividualToObjectProperty(
				ontologyServices.getOperationIdIRI(" This is the top level process for LH"), agentIndi,
				ontologyServices.getServiceTemplateOntology(),
				ontologyServices.getDescribedByObjectPropertyFromProcess());

	}

	
}
