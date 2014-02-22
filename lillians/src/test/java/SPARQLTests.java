import com.hp.hpl.jena.rdf.model.RDFNode;
import no.ntnu.FlatMapCallback;
import no.ntnu.ontology.MultipleResultSetRetriever;
import no.ntnu.ontology.SparqlQueryFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owl.model.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static no.ntnu.TestUtils.contains;
import static no.ntnu.TestUtils.flatSomething;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Lillian Hella
 * Date: Feb 8, 2010
 */
public class SPARQLTests {

    static SparqlQueryFactory factory;

    @BeforeClass
    public static void beforeClass() throws URISyntaxException {
        factory = new SparqlQueryFactory(SPARQLTests.class.getResource("PersonalProfile.owl").toURI());
    }

    @Test
    public void findIndividualsOfClass() {
        List<OWLIndividual> result = factory.singleQuery("SELECT  ?x WHERE { ?x rdf:type OntologyPersonalProfile:Jam}", "x");
        assertEquals(10, result.size());
        assertTrue(contains("NoraHomeMadeStrawberryJam", result));
        assertTrue(contains("NoraHomeMadeStrawberryAndWildJam", result));
        assertTrue(contains("HervikEcoStrawberryJam", result));
    }

    @Test
    public void findAllHervikProducedJams() {
        List<OWLIndividual> result = factory.singleQuery("SELECT  ?x WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Jam . " +
                "?x OntologyPersonalProfile:hasProducer OntologyPersonalProfile:Hervik . " +
                "}", "x");
        assertEquals(2, result.size());
        assertTrue(contains("HervikStrawberryJam", result));
        assertTrue(contains("HervikEcoStrawberryJam", result));
    }

    @Test
    public void testFindAllAffinities() {
         List<OWLIndividual> result = factory.singleQuery("SELECT ?x WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Modifiers . " +
                "}","x");

        assertTrue(contains("BillsPriceSensitivity", result));
        assertTrue(contains("BillsFairTradeAffinity", result));
        assertTrue(contains("StudentEcoAffinity", result));
        assertTrue(contains("StudentPriceSensitivity", result));
        assertTrue(contains("StudentADHDAffinity", result));
        assertTrue(contains("BillsEcoAffinity", result));
        assertTrue(contains("BillsADHDAdditiveAffinity", result));
        assertTrue(contains("StudentFairTradeAffinity", result));
    }

    @Test
    public void testFindAllECoJams() {
        //From Bills list
        List<OWLIndividual> ecoList = factory.singleQuery("SELECT ?x WHERE { " +
                "?x  rdf:type OntologyPersonalProfile:EcologicalJam .}", "x");

        assertTrue(contains("ICAEcologicalStrawberryJam", ecoList));
        assertTrue(contains("HervikEcoStrawberryJam", ecoList));
        assertEquals(2, ecoList.size());
    }

    @Test
    public void testFindAllNoraProductsWithBenzoat() {
        List<OWLIndividual> result = factory.singleQuery("SELECT ?x WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Jam . " +
                "?x  OntologyPersonalProfile:hasProducer OntologyPersonalProfile:Nora . " +
                "?x OntologyPersonalProfile:containsAdditive OntologyPersonalProfile:Natriumbenzoat. " +
                //"?y OntologyPersonalProfile:hasEffect ?z . " +
                //"?z rdf:type OntologyPersonalProfile:Additive " +
                "}", "x");

        assertTrue(contains("NoraNoSugar", result));
        assertEquals(1, result.size());
    }

    @Test
    public void testFindAllJamsWithBenzoat() {
        List<OWLIndividual> result = factory.singleQuery("SELECT ?x WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Jam . " +
                "?x OntologyPersonalProfile:containsAdditive OntologyPersonalProfile:Natriumbenzoat. " +
                //"?y OntologyPersonalProfile:hasEffect ?z . " +
                //"?z rdf:type OntologyPersonalProfile:Additive " +
                "}", "x");
        assertTrue(contains("EuroshopperStrawberryJam", result));
        assertTrue(contains("NoraNoSugar", result));
        assertEquals(2, result.size());
    }

    @Test
    public void testFindAllEcoPersons() {
        List<OWLIndividual> result = factory.singleQuery("SELECT ?x WHERE { " +
                "?x  rdf:type OntologyPersonalProfile:EcoConcernedPerson . " +
                "}", "x");
        assertEquals(2, result.size());
        assertTrue(contains("EcoConcernedPerson", result));
        assertTrue(contains("Bill", result));
    }

    @Test
    public void getShoppingListItems() {
        List<OWLIndividual> result = factory.singleQuery("SELECT ?x WHERE { " +
                "?y  rdf:type OntologyPersonalProfile:ShoppingList . " +
                "?y  OntologyPersonalProfile:hasShoppingListItem ?x. " +
                //   "?x OntologyPersonalProfile:hasShoppingListItem OntologyPersonalProfile:Bill." +
                "}", "x");

        assertTrue(contains("HervikEcoStrawberryJam", result));
        List<OWLClass> typesOfAffinity = flatSomething(result, new FlatMapCallback() {
            public Set<Set<OWLClass>> doMagic(OWLEntity individual) {
                return factory.getTypes((OWLIndividual) individual);
            }
        });
        assertTrue(contains("EcologicalStrawberryJam", typesOfAffinity));
    }

    @Test
    public void testFindAllAffinitiesForANamedPerson() {
        List<OWLIndividual> affinities = factory.singleQuery("SELECT ?x WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Modifiers . " +
                "?x OntologyPersonalProfile:belongsTo OntologyPersonalProfile:Bill." +
                "}", "x");

        List<OWLClass> typesOfAffinity = new ArrayList<OWLClass>();
        for (OWLIndividual affinity : affinities) {
            typesOfAffinity.add(factory.getType(affinity));
        }

        assertTrue(contains("AvoidADHDAdditives", typesOfAffinity));
        assertTrue(contains("MediumPriceSensitivity", typesOfAffinity));
        assertTrue(contains("HighEcoAffinity", typesOfAffinity));
        assertTrue(contains("HighFairTradeAffinity", typesOfAffinity));
        assertEquals(4, typesOfAffinity.size());

    }

    @Test
    public void findAllEcoPositives() {
        List<OWLIndividual> result = factory.singleQuery("SELECT ?x WHERE { " +
                "?x rdf:type OntologyPersonalProfile:StrawberryJam ." +
                //"?y OntologyPersonalProfile:Person . " +
                "OntologyPersonalProfile:Bill OntologyPersonalProfile:satisfiesHighEcoAffinity ?x ." +
                "}", "x");
        assertEquals(2, result.size());
        assertEquals("ICAEcologicalStrawberryJam", result.get(0).toString());
        assertEquals("HervikEcoStrawberryJam", result.get(1).toString());

    }
    
    //Multiple variables
    @Test
    public void findAManWithAge() {
        Map<String, RDFNode> result = factory.multipleResultsQuery("SELECT ?x ?y ?z WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Man ." +
                "?x OntologyPersonalProfile:hasFirstName ?z . " +            //<Bill>
                "?x OntologyPersonalProfile:hasAge ?y  . " +
                "}", new MultipleResultSetRetriever()).get(0);
        assertEquals(SparqlQueryFactory.myURI + "#Bill", result.get("x").toString());
        assertEquals("39^^http://www.w3.org/2001/XMLSchema#int", result.get("y").toString());
        assertEquals("Bill^^http://www.w3.org/2001/XMLSchema#string", result.get("z").toString());
    }


    @Test
    public void findPreferencesOfPersonForPhd() {
        //Bills affinities
        List<OWLIndividual> result = factory.singleQuery("SELECT distinct ?typeOfAffinity WHERE " +
                "{ OntologyPersonalProfile:Bill OntologyPersonalProfile:hasAffinity ?affinity. ?affinity rdf:type OntologyPersonalProfile:ADHDAdditiveAffinity ; rdf:type ?typeOfAffinity .}", "typeOfAffinity");
        assertEquals(4, result.size());

        for (OWLIndividual owlIndividual : result) {
            Set<Set<OWLClass>> owlClass = factory.getTypes(owlIndividual);
            //factory.getIndividuals(owlClass, true);
            System.out.println(owlClass);
        }
        /*
        List<OWLIndividual> result = factory.singleQuery("select ?strawberry where " +
                                " { ?strawberry rdf:type OntologyPersonalProfile:StrawberryJam.}" +
                " UNION {?strawberry rdf:type OntologyPersonalProfile:EcologicalFood} }", "strawberry");
*/
        //"SELECT  ?x WHERE { ?x rdf:type OntologyPersonalProfile:Jam}"
    }

    @Test
    public void matchRelevantService() {
        List<OWLIndividual> result = factory.singleQuery("select ?person WHERE " +
                " { ?person rdf:type OntologyPersonalProfile:Person; OntologyPersonalProfile:hasAge ?age; rdf:type OntologyPersonalProfile:EcoConcernedPerson." +
                " FILTER (?age > 35) }", "person");

        assertEquals(1, result.size());
        assertTrue(contains("Bill", result));
    }

    @Test
    public void matchRelevantService2() {
        List<OWLIndividual> result = factory.singleQuery("select ?person WHERE " +
                " { ?person rdf:type OntologyPersonalProfile:Person; OntologyPersonalProfile:hasAge ?age; rdf:type OntologyPersonalProfile:EcoConcernedPerson; OntologyPersonalProfile:hasShoppingList ?list ." +
                " FILTER (?age > 35) " +
                " ?list rdf:type OntologyPersonalProfile:ShoppingList; OntologyPersonalProfile:hasShoppingListItem ?item." +
                " ?item rdf:type OntologyPersonalProfile:EcologicalFood }", "person");

        assertEquals(1, result.size());
        assertTrue(contains("Bill", result));
    }

    @Test
    public void askAdhdAffinity() {
        assertTrue(factory.ask("ask { OntologyPersonalProfile:Bill OntologyPersonalProfile:hasAffinity ?affinity. ?affinity rdf:type OntologyPersonalProfile:AvoidADHDAdditives.}"));
        List<OWLIndividual> jams = factory.singleQuery("select ?jam WHERE { ?jam rdf:type OntologyPersonalProfile:StrawberryJam; rdf:type OntologyPersonalProfile:ADHDEffectProducts.}", "jam");
        assertEquals(4, jams.size());
        assertTrue(contains("NoraHomeMadeStrawberryAndWildJam", jams));
        assertTrue(contains("EuroshopperStrawberryJam", jams));
        assertTrue(contains("NoraLightStrawberryJam", jams));
        assertTrue(contains("NoraNoSugar", jams));
    }

    @Test
    public void askEcoAffinity() {
        assertTrue(factory.ask("ask { OntologyPersonalProfile:Bill OntologyPersonalProfile:hasAffinity ?affinity. ?affinity rdf:type OntologyPersonalProfile:HighEcoAffinity.}"));
        List<OWLIndividual> jams = factory.singleQuery("select ?jam WHERE { ?jam rdf:type OntologyPersonalProfile:StrawberryJam; rdf:type OntologyPersonalProfile:EcologicalFood.}", "jam");
        assertEquals(2, jams.size());
        assertTrue(contains("ICAEcologicalStrawberryJam", jams));
        assertTrue(contains("HervikEcoStrawberryJam", jams));
    }

    @Test
    public void askFairTradeAffinity() {
        assertTrue(factory.ask("ask { OntologyPersonalProfile:Bill OntologyPersonalProfile:hasAffinity ?affinity. ?affinity rdf:type OntologyPersonalProfile:FairTradeAffinity.}"));
        List<OWLIndividual> jams = factory.singleQuery("select ?jam WHERE { ?jam rdf:type OntologyPersonalProfile:StrawberryJam; rdf:type OntologyPersonalProfile:FairTradeAffinity.}", "jam");
        assertEquals(0, jams.size());
    }
}
