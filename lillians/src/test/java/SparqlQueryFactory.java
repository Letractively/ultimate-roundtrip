import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasoner;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lillian Hella
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
    OWLDataFactory factory;


    public SparqlQueryFactory() {
        manager = OWLManager.createOWLOntologyManager();
        // We load an ontology
        try {
            ontology = manager.loadOntologyFromPhysicalURI(URI.create("file:/Users/hella/IdeaProjects/ny-kodebase/src/main/resources/PersonalProfile.owl"));
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException("Error creating ontology" + e);
        }
        // read the ontology
        reasoner = new Reasoner(manager);
        reasoner.loadOntology(ontology);
        factory = manager.getOWLDataFactory();
    }

    List<OWLIndividual> executeQuery(String query) {
        SparqlQueryFactory factory = new SparqlQueryFactory();

        List<String> ecosAsStrings = factory.jenaQuery(SparqlQueryFactory.prefix + query, "x");
        List<OWLIndividual> individuals = new ArrayList<OWLIndividual>();
        for (String ecoAsString : ecosAsStrings) {
            individuals.add(factory.getOWLIndividual(URI.create(ecoAsString)));
        }
        return individuals;
    }

    public List<String> jenaQuery(String query, String unknownVariable) {
        KnowledgeBase kb = reasoner.getKB();
        // Create Pellet-Jena reasoner
        PelletReasoner pelletReasoner = new PelletReasoner();
        // Create a Pellet graph using the KB from OWLAPI
        PelletInfGraph graph = pelletReasoner.bind(kb);
        // Wrap the graph in a model
        InfModel model = ModelFactory.createInfModel(graph);
        // Create a query execution over this model


        Query queryQuery = QueryFactory.create(query);

        QueryExecution qe = SparqlDLExecutionFactory.create(queryQuery, model);

        ResultSet rs = qe.execSelect();

        // String bl = rs.toString();
        //System.out.println("bl = " + bl);

        List<String> resultUrls = new ArrayList<String>();
        while (rs.hasNext()) {
            QuerySolution soln = rs.nextSolution();
            RDFNode x = soln.get(unknownVariable);       // Get a result variable by name.

            String affinityURL = x.toString();
            resultUrls.add(affinityURL);
        }
        return resultUrls;
    }

    public OWLIndividual getOWLIndividual(URI uri) {
        return factory.getOWLIndividual(uri);
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

    /**
     * Simplification
     */
    public OWLObjectProperty getOWLObjectProperty(String something) {
        return factory.getOWLObjectProperty(URI.create(myURI + something));
    }

}
