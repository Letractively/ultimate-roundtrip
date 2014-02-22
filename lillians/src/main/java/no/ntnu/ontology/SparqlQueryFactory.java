package no.ntnu.ontology;

import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasoner;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.*;

import java.net.URI;
import java.util.*;

/**
 * @author Lillian Hella
 * Handles all the difficulties of the Ontology API in a simple to use abstraction
 */

public class SparqlQueryFactory {
    public final OWLOntologyManager manager;
    public final OWLOntology ontology;
    public final Reasoner reasoner;
    public static final String myURI = "http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl";
    public static final String prefix = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                    "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                    "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  ";
    private final OWLDataFactory factory;
    ResultSetRetriever resultRetriever = new MultipleResultSetRetriever();


    public SparqlQueryFactory(URI ontologyFile) {
        manager = OWLManager.createOWLOntologyManager();
        // We load an ontology
        try {
            this.ontology = manager.loadOntology(ontologyFile);
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException("Error creating ontology" + e);
        }
        // read the ontology
        reasoner = new Reasoner(manager);
        reasoner.loadOntology(this.ontology);
        factory = manager.getOWLDataFactory();
    }

    public List<OWLIndividual> singleQuery(String query, String variableName) {
        List<Map<String, OWLIndividual>> resultMap = convertToOWLIndividuals(multipleResultsQuery(query, resultRetriever));
        List<OWLIndividual> results = new ArrayList<OWLIndividual>();
        for (Map<String, OWLIndividual> individualMap : resultMap) {
            results.add(individualMap.get(variableName));
        }
        return results;
    }

    public List<Map<String, RDFNode>> multipleResultsQuery(String query, ResultSetRetriever resultSetRetriever) {
        KnowledgeBase kb = reasoner.getKB();
        // Create Pellet-Jena reasoner
        PelletReasoner pelletReasoner = new PelletReasoner();
        // Create a Pellet graph using the KB from OWLAPI
        PelletInfGraph graph = pelletReasoner.bind(kb);
        // Wrap the graph in a model
        InfModel model = ModelFactory.createInfModel(graph);
        // Create a query execution over this model

        Query queryQuery = QueryFactory.create(SparqlQueryFactory.prefix + query);
        QueryExecution qe = SparqlDLExecutionFactory.create(queryQuery, model);
        ResultSet resultSet = qe.execSelect();
        return resultSetRetriever.retrieveResultset(resultSet);
    }

    public boolean ask(String query) {
        KnowledgeBase kb = reasoner.getKB();
        // Create Pellet-Jena reasoner
        PelletReasoner pelletReasoner = new PelletReasoner();
        // Create a Pellet graph using the KB from OWLAPI
        PelletInfGraph graph = pelletReasoner.bind(kb);
        // Wrap the graph in a model
        InfModel model = ModelFactory.createInfModel(graph);
        // Create a query execution over this model

        Query queryQuery = QueryFactory.create(SparqlQueryFactory.prefix + query);
        QueryExecution qe = SparqlDLExecutionFactory.create(queryQuery, model);
        return qe.execAsk();
    }

    List<Map<String, OWLIndividual>> convertToOWLIndividuals(List<Map<String, RDFNode>> in) {
        ArrayList<Map<String, OWLIndividual>> result = new ArrayList<Map<String, OWLIndividual>>();
        for (Map<String, RDFNode> stringOWLIndividualMap : in) {
            for (Map.Entry<String, RDFNode> stringRDFNodeEntry : stringOWLIndividualMap.entrySet()) {
                HashMap<String, OWLIndividual> individualMap = new HashMap<String, OWLIndividual>();
                individualMap.put(stringRDFNodeEntry.getKey(), factory.getOWLIndividual(URI.create(stringRDFNodeEntry.getValue().toString())));
                result.add(individualMap);
            }
        }
        return result;
    }


    public OWLObjectProperty findObjectProperty(String type) {
        return factory.getOWLObjectProperty(URI.create(myURI + type));
    }

    public OWLIndividual findIndividual(String name) {
        return factory.getOWLIndividual(URI.create(myURI + name));
    }

    public OWLClass findClassByName(String className) {
        return manager.getOWLDataFactory().getOWLClass(URI.create(myURI + className));
    }

    public OWLDataProperty findDataType(String name) {
        return manager.getOWLDataFactory().getOWLDataProperty(URI.create(myURI + name));
    }

    public Set<OWLIndividual> getIndividuals(OWLDescription owlDescription, boolean direct) {
        return reasoner.getIndividuals(owlDescription, direct);
    }

    public OWLIndividual getRelatedIndividual(OWLIndividual individual, OWLObjectProperty relation) {
        return reasoner.getRelatedIndividual(individual, relation);
    }
    public Set<OWLIndividual> getRelatedIndividuals(OWLIndividual individual, OWLObjectProperty relation) {
        return reasoner.getRelatedIndividuals(individual, relation);
    }

    public OWLClass getType(OWLIndividual individual) {
        return reasoner.getType(individual);
    }

    public Set<Set<OWLClass>> getTypes(OWLIndividual indvidual) {
        return reasoner.getTypes(indvidual);
    }
}

