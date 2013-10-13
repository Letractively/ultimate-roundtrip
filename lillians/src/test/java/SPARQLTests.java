import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasoner;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.model.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Lillian Hella
 * Date: Feb 8, 2010
 */
public class SPARQLTests {

    static SparqlQueryFactory factory;
    static Reasoner reasoner;

    @BeforeClass
    public static void beforeClass() {
        factory = new SparqlQueryFactory();
        reasoner = factory.reasoner;
    }

    @Test
    public void findIndividualsOfClass() {
        List<OWLIndividual> result = factory.executeQuery("SELECT  ?x WHERE { ?x rdf:type OntologyPersonalProfile:Jam}");
        assertEquals(10, result.size());
        assertEquals("NoraHomeMadeStrawberryAndWildJam", result.get(0).toString());
        assertEquals("HervikEcoStrawberryJam", result.get(9).toString());
    }

    @Test
    public void findAllCandidatesIndividualsOfClassWithSomeProperty() {
        List<OWLIndividual> result = factory.executeQuery("SELECT  ?x WHERE { ?x rdf:type OntologyPersonalProfile:Jam . " +
                "?x OntologyPersonalProfile:hasProducer OntologyPersonalProfile:Hervik . }");
        assertEquals(2, result.size());
        assertEquals("HervikStrawberryJam", result.get(0).toString());
        assertEquals("HervikEcoStrawberryJam", result.get(1).toString());
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
        //From Bills list
        List<OWLIndividual> ecoList = factory.executeQuery("SELECT ?x WHERE { " +
                "?x  rdf:type OntologyPersonalProfile:EcologicalJam .}");

        assertEquals("ICAEcologicalStrawberryJam", ecoList.get(0).toString());
        assertEquals("HervikEcoStrawberryJam", ecoList.get(1).toString());
        assertEquals(2, ecoList.size());
    }

    @Test
    public void testFindAllNoraProductsWithBenzoat() {
        List<OWLIndividual> result = factory.executeQuery("SELECT ?x WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Jam . " +
                "?x  OntologyPersonalProfile:hasProducer OntologyPersonalProfile:Nora . " +
                "?x OntologyPersonalProfile:containsAdditive OntologyPersonalProfile:Natriumbenzoat. " +
                //"?y OntologyPersonalProfile:hasEffect ?z . " +
                //"?z rdf:type OntologyPersonalProfile:Additive " +
                "}");

        assertEquals("NoraNoSugar", result.get(0).toString());
        assertEquals(1, result.size());
    }

    @Test
    public void testFindAllJamsWithBenzoat() {
        List<OWLIndividual> result = factory.executeQuery("SELECT ?x WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Jam . " +
                "?x OntologyPersonalProfile:containsAdditive OntologyPersonalProfile:Natriumbenzoat. " +
                //"?y OntologyPersonalProfile:hasEffect ?z . " +
                //"?z rdf:type OntologyPersonalProfile:Additive " +
                "}");
        assertEquals("EuroshopperStrawberryJam", result.get(0).toString());
        assertEquals("NoraNoSugar", result.get(1).toString());
        assertEquals(2, result.size());
    }

    @Test
    public void testFindAllEcoPersons() {
        List<OWLIndividual> result = factory.executeQuery("SELECT ?x WHERE { " +
                "?x  rdf:type OntologyPersonalProfile:EcoConcernedPerson . " +
                "}");
        assertEquals("Bill", result.get(0).toString());
    }

    @Test
    public void getShoppingListItems() {
        List<OWLIndividual> result = factory.executeQuery("SELECT ?x WHERE { " +
                "?y  rdf:type OntologyPersonalProfile:ShoppingList . " +
                "?y  OntologyPersonalProfile:hasShoppingListItem ?x. " +

                //   "?x OntologyPersonalProfile:hasShoppingListItem OntologyPersonalProfile:Bill." +
                "}");

        List<OWLClass> typesOfAffinity = new ArrayList<OWLClass>();
        for (OWLIndividual affinity : result) {
            typesOfAffinity.add(reasoner.getType(affinity));
        }

        assertEquals("HervikStrawberryJam", result.get(0).toString());
        assertEquals("RegularProducedFood", typesOfAffinity.get(0).toString());
    }

    @Test
    public void testFindAllAffinitiesForANamedPerson() {
        List<OWLIndividual> affinities = factory.executeQuery("SELECT ?x WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Modifiers . " +
                "?x OntologyPersonalProfile:belongsTo OntologyPersonalProfile:Bill." +
                "}");

        List<OWLClass> typesOfAffinity = new ArrayList<OWLClass>();
        for (OWLIndividual affinity : affinities) {
            typesOfAffinity.add(reasoner.getType(affinity));
        }

        assertEquals("MediumPriceSensitivity", typesOfAffinity.get(0).toString());
        assertEquals("HighFairTradeAffinity", typesOfAffinity.get(1).toString());
        assertEquals(4, typesOfAffinity.size());

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


    @Test
    public void findAllEcoPositives() {
        List<OWLIndividual> result = factory.executeQuery("SELECT ?x WHERE { " +
                "?x rdf:type OntologyPersonalProfile:StrawberryJam ." +
                //"?y OntologyPersonalProfile:Person . " +
                "OntologyPersonalProfile:Bill OntologyPersonalProfile:satisfiesHighEcoAffinity ?x ." +
                "}");
        assertEquals(2, result.size());
    }
}
