import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasoner;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: hella
 * Date: Feb 8, 2010
 * Time: 8:52:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class SPARQLTests {
    static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    //OWLOntologyManager manager;
    OWLOntology ontology;
    Reasoner reasoner;
    public static final String myURI = "http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl";
    static OWLDataFactory factory = manager.getOWLDataFactory();

    @Before
    public void setUp() throws OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();
        // We load an ontology
        // read the ontology
        ontology = manager.loadOntologyFromPhysicalURI(URI.create("file:/Users/hella/IdeaProjects/ny-kodebase/src/main/resources/PersonalProfile.owl"));
        reasoner = new Reasoner(manager);
        reasoner.loadOntology(ontology);
    }

    @Test
    public void findIndividualsOfClass() {
        //forslag fra http://lists.owldl.com/pipermail/pellet-users/2008-December/003218.html
        // Get the KB from the reasoner
        KnowledgeBase kb = reasoner.getKB();
        // Create Pellet-Jena reasoner
        PelletReasoner jenaReasoner = new PelletReasoner();

        // Create a Pellet graph using the KB from OWLAPI
        PelletInfGraph graph = jenaReasoner.bind(kb);
        // Wrap the graph in a model
        InfModel model = ModelFactory.createInfModel(graph);
        // Create a query execution over this model

        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  " +
                "SELECT  ?x " +
                "WHERE { ?x rdf:type OntologyPersonalProfile:Jam}";

        Query queryQuery = QueryFactory.create(query);

        QueryExecution qe = SparqlDLExecutionFactory.create(queryQuery, model);

        ResultSet rs = qe.execSelect();

        String bl = rs.toString();

        String wantedRs = "[{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#NoraHomeMadeStrawberryAndWildJam}, {var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#HervikStrawberryJam}, {var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#ICAEcologicalStrawberryJam}, {var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#NoraSqueezy}, {var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#NoraLightStrawberryJam}, {var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#NoraOriginal}, {var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#EuroshopperStrawberryJam}, {var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#NoraNoSugar}, {var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#NoraHomeMadeStrawberryJam}, {var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#HervikEcoStrawberryJam}]";

        assertEquals(wantedRs, bl);
    }


    /*
            assertEquals({"http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#HervikStrawberryJam", });
            [0]="{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#HervikStrawberryJam}"
        [1]="{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#ICAEcologicalStrawberryJam}"
                [2]="{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#NoraLightStrawberryJam}"
        [3]="{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#EuroshopperStrawberryJam}"
        [4]="{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#HervikEcoStrawberryJam
        [5]="{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#NoraHomeMadeStrawberryJam}"

    **/

    @Test
    public void findAllCandidatesIndividualsOfClassWithSomeProperty() {
        //forslag fra http://lists.owldl.com/pipermail/pellet-users/2008-December/003218.html
        // Create Pellet-OWLAPI reasoner
        // Get the KB from the reasoner
        KnowledgeBase kb = reasoner.getKB();
        // Create Pellet-Jena reasoner
        PelletReasoner jenaReasoner = new PelletReasoner();
        // Create a Pellet graph using the KB from OWLAPI
        PelletInfGraph graph = jenaReasoner.bind(kb);
        // Wrap the graph in a model
        InfModel model = ModelFactory.createInfModel(graph);
        // Create a query execution over this model

        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  " +
                "SELECT  ?x " +
                "WHERE { ?x rdf:type OntologyPersonalProfile:Jam . " +
                "?x OntologyPersonalProfile:hasProducer OntologyPersonalProfile:Hervik . }";

        Query queryQuery = QueryFactory.create(query);

        QueryExecution qe = SparqlDLExecutionFactory.create(queryQuery, model);

        ResultSet rs = qe.execSelect();

        while (rs.hasNext()) {
            QuerySolution soln = rs.nextSolution();
            RDFNode x = soln.get("x");       // Get a result variable by name.
            System.out.println("x = " + x);
            Resource r = soln.getResource("x"); // Get a result variable - must be a resource
            System.out.println("r = " + r);
            //Literal l = soln.getLiteral("x");   // Get a result variable - must be a literal
            //System.out.println("l = " + l);
        }

        String bl = rs.toString();

        String wantedRs = "[{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#HervikStrawberryJam}, {var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#HervikEcoStrawberryJam}]";

        assertEquals(wantedRs, bl);

    }

    @Test
    public void test() {
        //forslag fra http://lists.owldl.com/pipermail/pellet-users/2008-December/003218.html
        // Create Pellet-OWLAPI reasoner
        // Get the KB from the reasoner
        KnowledgeBase kb = reasoner.getKB();
        // Create Pellet-Jena reasoner
        PelletReasoner jenaReasoner = new PelletReasoner();
        // Create a Pellet graph using the KB from OWLAPI
        PelletInfGraph graph = jenaReasoner.bind(kb);
        // Wrap the graph in a model
        InfModel model = ModelFactory.createInfModel(graph);
        // Create a query execution over this model

        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  " +
                "SELECT  ?x ?z ?y " +
                "WHERE { ?x rdf:type OntologyPersonalProfile:Man ." +
                "?x OntologyPersonalProfile:hasFirstName ?z . " +            //<Bill>
                "?x OntologyPersonalProfile:hasAge ?y  . " +
                "}";

        Query queryQuery = QueryFactory.create(query);

        QueryExecution qe = SparqlDLExecutionFactory.create(queryQuery, model);

        ResultSet rs = qe.execSelect();

        while (rs.hasNext()) {
            QuerySolution soln = rs.nextSolution();
            RDFNode x = soln.get("x");       // Get a result variable by name.
            System.out.println("x = " + x);

            RDFNode z = soln.get("z");
            System.out.println("z = " + z);

            System.out.println("soln.get(\"y\") = " + soln.get("y"));
            Resource r = soln.getResource("x"); // Get a result variable - must be a resource
            // System.out.println("r = " + r);
            //Literal l = soln.getLiteral("x");   // Get a result variable - must be a literal
            //System.out.println("l = " + l);
        }

        String bl = rs.toString();
        System.out.println("bl = " + bl);

        String wantedRs = "[{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#HervikStrawberryJam}, {var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#HervikEcoStrawberryJam}]";

        //assertEquals(wantedRs, bl);
        //todo hvordan håndtere result sett fra spørring - syntax for spørringene - hvis man vet alt dette er det vel like greit å bruke vanlig reasoning?
    }


    @Test
    public void test2() {
        //forslag fra http://lists.owldl.com/pipermail/pellet-users/2008-December/003218.html
        // Create Pellet-OWLAPI reasoner
        // Get the KB from the reasoner
        KnowledgeBase kb = reasoner.getKB();
        // Create Pellet-Jena reasoner
        PelletReasoner jenaReasoner = new PelletReasoner();
        // Create a Pellet graph using the KB from OWLAPI
        PelletInfGraph graph = jenaReasoner.bind(kb);
        // Wrap the graph in a model
        InfModel model = ModelFactory.createInfModel(graph);
        // Create a query execution over this model


        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  " +
                "SELECT  ?x ?y " +
                "WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Man . " +
                "?x OntologyPersonalProfile:hasFirstName \"Bill\" . " +            //<Bill>
                "?x OntologyPersonalProfile:hasAge ?y  . " +
                "}";

        Query queryQuery = QueryFactory.create(query);

        QueryExecution qe = SparqlDLExecutionFactory.create(queryQuery, model);

        ResultSet rs = qe.execSelect();

        while (rs.hasNext()) {
            QuerySolution soln = rs.nextSolution();
            RDFNode x = soln.get("x");       // Get a result variable by name.
            System.out.println("x = " + x);

            System.out.println("soln.get(\"y\") = " + soln.get("y"));
            Resource r = soln.getResource("x"); // Get a result variable - must be a resource
            // System.out.println("r = " + r);
            //Literal l = soln.getLiteral("x");   // Get a result variable - must be a literal
            //System.out.println("l = " + l);
        }

        String bl = rs.toString();
        System.out.println("bl = " + bl);

        String wantedRs = "[{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#HervikStrawberryJam}, {var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#HervikEcoStrawberryJam}]";

        //assertEquals(wantedRs, bl);
        //todo hvordan håndtere result sett fra spørring - syntax for spørringene - hvis man vet alt dette er det vel like greit å bruke vanlig reasoning?
    }

    @Test
    public void test3() {
        //forslag fra http://lists.owldl.com/pipermail/pellet-users/2008-December/003218.html
        // Create Pellet-OWLAPI reasoner
        // Get the KB from the reasoner
        KnowledgeBase kb = reasoner.getKB();
        // Create Pellet-Jena reasoner
        PelletReasoner jenaReasoner = new PelletReasoner();
        // Create a Pellet graph using the KB from OWLAPI
        PelletInfGraph graph = jenaReasoner.bind(kb);
        // Wrap the graph in a model
        InfModel model = ModelFactory.createInfModel(graph);
        // Create a query execution over this model


        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  " +
                "SELECT ?x " +
                "WHERE { " +
                "?y rdf:type OntologyPersonalProfile:EcoAffinity . " +
                //"?y OntologyPersonalProfile:hasFirstName \"Bill\" . " +
                "?y OntologyPersonalProfile:hasRelatedEcoProducts ?x  . " +
                "}";

        Query queryQuery = QueryFactory.create(query);

        QueryExecution qe = SparqlDLExecutionFactory.create(queryQuery, model);

        ResultSet rs = qe.execSelect();

        while (rs.hasNext()) {
            QuerySolution soln = rs.nextSolution();
            RDFNode x = soln.get("x");       // Get a result variable by name.
            System.out.println("x = " + x);

            System.out.println("soln.get(\"y\") = " + soln.get("y"));
            Resource r = soln.getResource("x"); // Get a result variable - must be a resource
            // System.out.println("r = " + r);
            //Literal l = soln.getLiteral("x");   // Get a result variable - must be a literal
            //System.out.println("l = " + l);
        }

        String bl = rs.toString();
        System.out.println("bl = " + bl);

        String wantedRs = "[{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#HervikStrawberryJam}, {var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#HervikEcoStrawberryJam}]";

        //assertEquals(wantedRs, bl);
        //todo hvordan håndtere result sett fra spørring - syntax for spørringene - hvis man vet alt dette er det vel like greit å bruke vanlig reasoning?
    }

    @Test
    public void testFindAllECoJams() {
        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  " +
                "SELECT ?x " +
                "WHERE { " +
                "?x  rdf:type OntologyPersonalProfile:EcologicalJam . " +
                "}";

        List<String> billsList = jenaQuery(query, "x");

        System.out.println("billsList.toString() = " + billsList.toString());

        List<OWLIndividual> result = new ArrayList<OWLIndividual>();

        for (String list : billsList) {
            OWLIndividual affinity = factory.getOWLIndividual(URI.create(list));
            OWLClass typeOfAffinity = reasoner.getType(affinity);

            //System.out.println("affinityURL = " + list);
            //System.out.println("typeOfAffinity = " + typeOfAffinity);

            result.add(affinity);
        }

        System.out.println("result = " + result.toString());
        assertEquals("ICAEcologicalStrawberryJam", result.get(0).toString());
        assertEquals("HervikEcoStrawberryJam", result.get(1).toString());
        assertEquals(2, result.size());
    }

    @Test
    public void testFindAllNoraProductsWithBenzoat() {
        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  " +
                "SELECT ?x " +
                "WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Jam . " +
                "?x  OntologyPersonalProfile:hasProducer OntologyPersonalProfile:Nora . " +
                "?x OntologyPersonalProfile:containsAdditive OntologyPersonalProfile:Natriumbenzoat. " +
                //"?y OntologyPersonalProfile:hasEffect ?z . " +
                //"?z rdf:type OntologyPersonalProfile:Additive " +
                "}";

        List<String> billsList = jenaQuery(query, "x");

        System.out.println("billsList.toString() = " + billsList.toString());

        List<OWLIndividual> result = new ArrayList<OWLIndividual>();

        for (String list : billsList) {
            OWLIndividual affinity = factory.getOWLIndividual(URI.create(list));
            OWLClass typeOfAffinity = reasoner.getType(affinity);

            //System.out.println("affinityURL = " + list);
            //System.out.println("typeOfAffinity = " + typeOfAffinity);

            result.add(affinity);
        }

        System.out.println("result = " + result.toString());
        assertEquals("NoraNoSugar", result.get(0).toString());
        assertEquals(1, result.size());
    }

    @Test
    public void testFindAllJamsWithBenzoat() {
        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  " +
                "SELECT ?x " +
                "WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Jam . " +
                "?x OntologyPersonalProfile:containsAdditive OntologyPersonalProfile:Natriumbenzoat. " +
                //"?y OntologyPersonalProfile:hasEffect ?z . " +
                //"?z rdf:type OntologyPersonalProfile:Additive " +
                "}";

        List<String> billsList = jenaQuery(query, "x");

        System.out.println("billsList.toString() = " + billsList.toString());

        List<OWLIndividual> result = new ArrayList<OWLIndividual>();

        for (String list : billsList) {
            OWLIndividual affinity = factory.getOWLIndividual(URI.create(list));
            OWLClass typeOfAffinity = reasoner.getType(affinity);

            //System.out.println("affinityURL = " + list);
            //System.out.println("typeOfAffinity = " + typeOfAffinity);

            result.add(affinity);
        }

        System.out.println("result = " + result.toString());
        assertEquals("EuroshopperStrawberryJam", result.get(0).toString());
        assertEquals("NoraNoSugar", result.get(1).toString());
        assertEquals(2, result.size());
    }

    @Test
    public void testFindAllEcoPersons() {
        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  " +
                "SELECT ?x " +
                "WHERE { " +
                "?x  rdf:type OntologyPersonalProfile:EcoConcernedPerson . " +
                "}";

        List<String> billsList = jenaQuery(query, "x");

        System.out.println("billsList.toString() = " + billsList.toString());

        List<OWLIndividual> result = new ArrayList<OWLIndividual>();
        for (String list : billsList) {
            OWLIndividual affinity = factory.getOWLIndividual(URI.create(list));
            OWLClass typeOfAffinity = reasoner.getType(affinity);
            System.out.println("affinityURL = " + list);
            System.out.println("typeOfAffinity = " + typeOfAffinity);
            result.add(affinity);
        }

        assertEquals("Bill", result.get(0).toString());
    }

    @Test
    public void testFindAll() {
        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  " +
                "SELECT ?x " +
                "WHERE { " +
                "?x  rdf:type OntologyPersonalProfile:EcoConcernedPerson . " +
                "}";

        List<String> billsList = jenaQuery(query, "x");

        System.out.println("billsList.toString() = " + billsList.toString());

        List<OWLIndividual> result = new ArrayList<OWLIndividual>();
        for (String list : billsList) {
            OWLIndividual affinity = factory.getOWLIndividual(URI.create(list));
            OWLClass typeOfAffinity = reasoner.getType(affinity);
            System.out.println("affinityURL = " + list);
            System.out.println("typeOfAffinity = " + typeOfAffinity);
            result.add(affinity);
        }

        assertEquals("Bill", result.get(0).toString());
    }


    @Test
    public void testSLItemsForBill() {
        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  " +
                "SELECT ?x " +
                "WHERE { " +
                "?y  rdf:type OntologyPersonalProfile:BillsShoppingList . " +
                "?y  OntologyPersonalProfile:hasShoppingListItem ?x. " +

                //   "?x OntologyPersonalProfile:hasShoppingListItem OntologyPersonalProfile:Bill." +
                "}";

        List<String> billsList = jenaQuery(query, "x");

        System.out.println("billsList.toString() = " + billsList.toString());

        List<OWLClass> result = new ArrayList<OWLClass>();
        for (String list : billsList) {
            OWLIndividual affinity = factory.getOWLIndividual(URI.create(list));
            OWLClass typeOfAffinity = reasoner.getType(affinity);

            System.out.println("affinityURL = " + list);
            System.out.println("typeOfAffinity = " + typeOfAffinity);

            result.add(typeOfAffinity);
        }

        //assertEquals("MediumPriceSensitivity", result.get(0).toString());
        //assertEquals("HighFairTradeAffinity", result.get(1).toString());
        //assertEquals(4, result.size());

    }

    @Test
    public void testFindAllAffinitiesForANamedPerson() {
        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  " +
                "SELECT ?x " +
                "WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Modifiers . " +
                "?x OntologyPersonalProfile:belongsTo OntologyPersonalProfile:Bill." +
                "}";

        //finner alle affinities til en person
        List<String> resultUrls = jenaQuery(query, "x");

        List<OWLClass> result = new ArrayList<OWLClass>();
        for (String affinityURL : resultUrls) {
            OWLIndividual affinity = factory.getOWLIndividual(URI.create(affinityURL));
            OWLClass typeOfAffinity = reasoner.getType(affinity);

            System.out.println("affinityURL = " + affinityURL);
            System.out.println("typeOfAffinity = " + typeOfAffinity);

            result.add(typeOfAffinity);
        }

        assertEquals("MediumPriceSensitivity", result.get(0).toString());
        assertEquals("HighFairTradeAffinity", result.get(1).toString());
        assertEquals(4, result.size());

    }

    public List<String> jenaQuery(String query, String unknownVariable) {
        KnowledgeBase kb = reasoner.getKB();
        // Create Pellet-Jena reasoner
        PelletReasoner jenaReasoner = new PelletReasoner();
        // Create a Pellet graph using the KB from OWLAPI
        PelletInfGraph graph = jenaReasoner.bind(kb);
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

    @Test
    public void testProductsRelatedToAffinity() {
        KnowledgeBase kb = reasoner.getKB();
        PelletReasoner jenaReasoner = new PelletReasoner();
        PelletInfGraph graph = jenaReasoner.bind(kb);
        InfModel model = ModelFactory.createInfModel(graph);

        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  " +
                "SELECT ?x " +
                "WHERE { " +
                "?x rdf:type OntologyPersonalProfile:EcologicalFood ." +
                "?y rdf:type OntologyPersonalProfile:EcoAffinity . " +
                "?y OntologyPersonalProfile:hasRelatedEcoProducts ?x ." +
                "}";   //fordi jeg ikke har spesifisert denne relasjonen til noen av individene...

        Query queryQuery = QueryFactory.create(query);

        QueryExecution qe = SparqlDLExecutionFactory.create(queryQuery, model);

        ResultSet rs = qe.execSelect();

        System.out.println("rs = " + rs.toString());

        List<OWLClass> result = new ArrayList<OWLClass>();

        while (rs.hasNext()) {
            QuerySolution soln = rs.nextSolution();
            RDFNode x = soln.get("x");       // Get a result variable by name.

            String affinityURL = x.toString();
            OWLIndividual affinity = factory.getOWLIndividual(URI.create(affinityURL));
            OWLClass typeOfAffinity = reasoner.getType(affinity);

            System.out.println("affinityURL = " + affinityURL);
            System.out.println("typeOfAffinity = " + typeOfAffinity);

            result.add(typeOfAffinity);
        }

//        assertEquals("MediumPriceSensitivity", result.get(0).toString());
        //      assertEquals("HighFairTradeAffinity", result.get(1).toString());
    }

    private static OWLIndividual findIndividual(String name) {
        return factory.getOWLIndividual(URI.create(myURI + name));
    }

    @Test
    public void findAllEcoPositives() {
        KnowledgeBase kb = reasoner.getKB();
        PelletReasoner jenaReasoner = new PelletReasoner();
        PelletInfGraph graph = jenaReasoner.bind(kb);
        InfModel model = ModelFactory.createInfModel(graph);

        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  " +
                "SELECT ?x " +
                "WHERE { " +
                "?x rdf:type OntologyPersonalProfile:StrawberryJam ." +
                //"?y OntologyPersonalProfile:Person . " +
                "OntologyPersonalProfile:Bill OntologyPersonalProfile:satisfiesHighEcoAffinity ?x ." +
                "}";

        Query queryQuery = QueryFactory.create(query);

        QueryExecution qe = SparqlDLExecutionFactory.create(queryQuery, model);

        ResultSet rs = qe.execSelect();

        System.out.println("rs = " + rs.toString());

        List<OWLClass> result = new ArrayList<OWLClass>();

        while (rs.hasNext()) {
            QuerySolution soln = rs.nextSolution();
            RDFNode x = soln.get("x");       // Get a result variable by name.

            String affinityURL = x.toString();

            System.out.println("affinityURL = " + affinityURL);
        }

       assertEquals("MediumPriceSensitivity", result.get(0).toString());

    }


}
