package com.mcs.owl.service;

import java.util.Optional;
import java.util.stream.Stream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.springframework.stereotype.Service;

import io.swagger.v3.oas.models.Operation;

@Service
public class OntologyServices {

	private OWLOntologyManager owlManager = null;

	private OWLOntology domainOntology = null;
	private OWLOntology serviceProcessTemplateOntology = null;
	private OWLOntology serviceProfileTemplateOntology = null;
	private OWLOntology serviceTemplateOntology = null;

	private OWLOntology processOntology = null;
	private OWLOntology profileOntology = null;
	private OWLOntology serviceOntology = null;

	private static OntologyServices ontologyServices = null;

	private final String HASH = "#";

	private OntologyServices() {
		try {

			domainOntology = getOntologyManager().loadOntology(getDomainOntologyIRI());
			serviceProcessTemplateOntology = getOntologyManager()
					.loadOntologyFromOntologyDocument(getServiceProcessTemplateOntologyIRI());
			serviceProfileTemplateOntology = getOntologyManager()
					.loadOntologyFromOntologyDocument(getServiceProfileTemplateOntologyIRI());
			serviceTemplateOntology = getOntologyManager()
					.loadOntologyFromOntologyDocument(getServiceTemplateOntologyIRI());

			OWLImportsDeclaration processDec = getDataFactory().getOWLImportsDeclaration(getProcessOntologyIRI());
			processOntology = getOntologyManager().getImportedOntology(processDec);

			OWLImportsDeclaration profileDec = getDataFactory().getOWLImportsDeclaration(getProfileOntologyIRI());
			profileOntology = getOntologyManager().getImportedOntology(profileDec);

			OWLImportsDeclaration serviceDec = getDataFactory().getOWLImportsDeclaration(getServiceOntologyIRI());
			serviceOntology = getOntologyManager().getImportedOntology(serviceDec);

		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static OntologyServices getOntologyServices() {
		if (ontologyServices == null) {
			ontologyServices = new OntologyServices();
		}
		return ontologyServices;
	}

	public OWLOntologyManager getOntologyManager() {
		owlManager = owlManager != null ? owlManager : OWLManager.createOWLOntologyManager();
		owlManager.getOntologyConfigurator().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
		return owlManager;
	}

	public OWLAnonymousIndividual getOWLAnonymousIndividual() {
		return getOntologyManager().getOWLDataFactory().getOWLAnonymousIndividual();
	}

	public IRI getDomainOntologyIRI() {
		return IRI.create("https://schema.org/docs/schemaorg.owl");
	}

	public IRI getServiceProcessTemplateOntologyIRI() {
		return IRI.create("file:C:/Users/shana/Documents/MCS/Project/OWL-S/Ontology/OWLS/LHProcess.owl");
	}

	public IRI getServiceProfileTemplateOntologyIRI() {
		return IRI.create("file:C:/Users/shana/Documents/MCS/Project/OWL-S/Ontology/OWLS/LHProfile.owl");
	}

	public IRI getServiceTemplateOntologyIRI() {
		return IRI.create("file:C:/Users/shana/Documents/MCS/Project/OWL-S/Ontology/OWLS/LHService.owl");
	}

	public IRI getProcessOntologyIRI() {
		return IRI.create("http://www.daml.org/services/owl-s/1.2/Process.owl");
	}

	public IRI getProfileOntologyIRI() {
		return IRI.create("http://www.daml.org/services/owl-s/1.2/Profile.owl");
	}

	public IRI getServiceOntologyIRI() {
		return IRI.create("http://www.daml.org/services/owl-s/1.2/Service.owl");
	}

	public String getDomainObjectPropertyIRI() {
		return "http://schema.org/";
	}

	public IRI getCustomOntologyIRI(String name) {
		return IRI.create("file:/tmp/" + name);
	}

	public IRI getOperationIdIRI(String operationId) {
		return IRI.create(getServiceProcessTemplateOntologyIRI() + HASH + operationId);
	}

	public IRI getProfileOperationIdIRI(String operationId) {
		return IRI.create(getServiceProfileTemplateOntologyIRI() + HASH + operationId);
	}

	public IRI getServiceOperationIdIRI(String operationId) {
		return IRI.create(getServiceTemplateOntologyIRI() + HASH + operationId);
	}

//	public OWLOntology getCustomOntology(IRI iri) {
//		OWLOntology customOntology = null;
//		try {
//			if(customOntology == null) {
//				customOntology=  getOntologyManager().loadOntologyFromOntologyDocument(getCustomOntologyIRI());	
//			}
//		} catch (OWLOntologyCreationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return customOntology;
//	}

	public OWLDataFactory getDataFactory() {
		return getOntologyManager().getOWLDataFactory();

	}

	public void getClassesInOntology() {
		domainOntology.classesInSignature().forEach(System.out::println);
	}

	public Stream<OWLObjectProperty> getObjectProperties() {
		return domainOntology.objectPropertiesInSignature();
	}

	public void saveOntology() {
		// Save our ontology
		try {
			importOntology();
			owlManager.saveOntology(serviceProcessTemplateOntology, this.getCustomOntologyIRI("LHProcessService.owl"));
			System.out.println(
					"ONOTOLOGY SAVED --->  " + this.getCustomOntologyIRI("LHProcessService.owl").getIRIString());

			owlManager.saveOntology(serviceProfileTemplateOntology, this.getCustomOntologyIRI("LHProfileService.owl"));
			System.out.println(
					"ONOTOLOGY SAVED --->  " + this.getCustomOntologyIRI("LHProfileService.owl").getIRIString());

			owlManager.saveOntology(serviceTemplateOntology, this.getCustomOntologyIRI("LHService.owl"));
			
			System.out.println("ONOTOLOGY SAVED --->  " + this.getCustomOntologyIRI("LHService.owl").getIRIString());

		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void importOntology() {
		OWLImportsDeclaration importDeclaration = getOntologyManager().getOWLDataFactory()
				.getOWLImportsDeclaration(getCustomOntologyIRI("LHProfileService.owl"));
		getOntologyManager().applyChange(new AddImport(getServiceTemplateOntology(), importDeclaration));
		
		importDeclaration = getOntologyManager().getOWLDataFactory()
				.getOWLImportsDeclaration(getCustomOntologyIRI("LHProcessService.owl"));
		getOntologyManager().applyChange(new AddImport(getServiceTemplateOntology(), importDeclaration));
		
		importDeclaration = getOntologyManager().getOWLDataFactory()
				.getOWLImportsDeclaration(getCustomOntologyIRI("LHProcessService.owl"));
		getOntologyManager().applyChange(new AddImport(getServiceProfileTemplateOntology(), importDeclaration));
		
		importDeclaration = getOntologyManager().getOWLDataFactory()
				.getOWLImportsDeclaration(getCustomOntologyIRI("LHProfileService.owl"));
		getOntologyManager().applyChange(new AddImport(getServiceProcessTemplateOntology(), importDeclaration));
	}

	public OWLObjectProperty getHasInputObjectPropertyFromProcess() {
		IRI paramObjectIRI = IRI.create("http://www.daml.org/services/owl-s/1.2/Process.owl#hasInput");
		Optional<OWLObjectProperty> objectProperties = getProcessOntology().objectPropertiesInSignature()
				.filter(object -> object.getIRI().equals(paramObjectIRI)).findAny();
		if (objectProperties.isPresent()) {
			return objectProperties.get();
		}
		System.out.println("Not Found property");
		return null;
	}

	public OWLObjectProperty getPresentObjectPropertyFromProcess() {
		IRI paramObjectIRI = IRI.create("http://www.daml.org/services/owl-s/1.2/Service.owl#presents");
		Optional<OWLObjectProperty> objectProperties = getServiceOntology().objectPropertiesInSignature()
				.filter(object -> object.getIRI().equals(paramObjectIRI)).findAny();
		if (objectProperties.isPresent()) {
			return objectProperties.get();
		}
		System.out.println("Not Found property");
		return null;
	}

	public OWLIndividual addIndividualToClass(IRI indiIRI, OWLClass owlClass, OWLOntology ontology) {

		OWLIndividual individual = getDataFactory().getOWLNamedIndividual(indiIRI);
		OWLClassAssertionAxiom ax = ontologyServices.getDataFactory().getOWLClassAssertionAxiom(owlClass, individual);
		ontologyServices.getOntologyManager().addAxiom(ontology, ax);
		return individual;
	}

	public OWLIndividual addIndividualToClass(OWLIndividual individual, OWLClass owlClass, OWLOntology ontology) {

		OWLClassAssertionAxiom ax = ontologyServices.getDataFactory().getOWLClassAssertionAxiom(owlClass, individual);
		ontologyServices.getOntologyManager().addAxiom(ontology, ax);
		return individual;
	}

	public OWLIndividual addIndividualToObjectProperty(IRI indiIRI, OWLIndividual mappingIndividual,
			OWLOntology ontology, OWLObjectProperty objectProperty) {
		OWLIndividual individual = ontologyServices.getDataFactory().getOWLNamedIndividual(indiIRI);
		OWLAxiom axiom = ontologyServices.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty,
				mappingIndividual, individual );

		ontologyServices.getOntologyManager().addAxiom(ontology, axiom);
		return individual;
	}

	public OWLClass addSubClass(OWLClass subClass, OWLClass superClass, OWLOntology ontology) {

		try {
			OWLAxiom axiom = getDataFactory().getOWLSubClassOfAxiom(subClass, superClass);
			ontologyServices.getOntologyManager().addAxiom(ontology, axiom);	
		}catch(Exception ex) {
			
		}
		
		return subClass;

	}

	public OWLOntology getServiceProcessTemplateOntology() {
		return serviceProcessTemplateOntology;
	}

	public OWLOntology getServiceProfileTemplateOntology() {
		return serviceProfileTemplateOntology;
	}

	public OWLOntology getProcessOntology() {
		return processOntology;
	}

	public OWLOntology getProfileOntology() {
		return profileOntology;
	}

	public OWLOntology getServiceTemplateOntology() {
		return serviceTemplateOntology;
	}

	public OWLOntology getServiceOntology() {
		return serviceOntology;
	}

}
