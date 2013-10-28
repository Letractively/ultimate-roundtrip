import com.hp.hpl.jena.rdf.model.RDFNode;
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
import static org.junit.Assert.assertEquals;

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
        assertEquals("NoraHomeMadeStrawberryAndWildJam", result.get(0).toString());
        assertEquals("HervikEcoStrawberryJam", result.get(9).toString());
    }

    @Test
    public void findAllHervikProducedJams() {
        List<OWLIndividual> result = factory.singleQuery("SELECT  ?x WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Jam . " +
                "?x OntologyPersonalProfile:hasProducer OntologyPersonalProfile:Hervik . " +
                "}", "x");

        assertEquals("HervikStrawberryJam", result.get(0).toString());
        assertEquals("HervikEcoStrawberryJam", result.get(1).toString());
    }

    @Test
    public void testFindAllAffinities() {
         List<OWLIndividual> result = factory.singleQuery("SELECT ?x WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Modifiers . " +
                "}","x");

        assertEquals("BillsPriceSensitivity", result.get(0).toString());
        assertEquals("BillsFairTradeAffinity", result.get(1).toString());
        assertEquals("StudentEcoAffinity", result.get(2).toString());
        assertEquals("StudentPriceSensitivity", result.get(3).toString());
        assertEquals("StudentADHDAffinity", result.get(4).toString());
        assertEquals("BillsEcoAffinity", result.get(5).toString());
        assertEquals("BillsADHDAdditiveAffinity", result.get(6).toString());
        assertEquals("StudentFairTradeAffinity", result.get(7).toString());
    }

    @Test
    public void testFindAllECoJams() {
        //From Bills list
        List<OWLIndividual> ecoList = factory.singleQuery("SELECT ?x WHERE { " +
                "?x  rdf:type OntologyPersonalProfile:EcologicalJam .}", "x");

        assertEquals("ICAEcologicalStrawberryJam", ecoList.get(0).toString());
        assertEquals("HervikEcoStrawberryJam", ecoList.get(1).toString());
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

        assertEquals("NoraNoSugar", result.get(0).toString());
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
        assertEquals("EuroshopperStrawberryJam", result.get(0).toString());
        assertEquals("NoraNoSugar", result.get(1).toString());
        assertEquals(2, result.size());
    }

    @Test
    public void testFindAllEcoPersons() {
        List<OWLIndividual> result = factory.singleQuery("SELECT ?x WHERE { " +
                "?x  rdf:type OntologyPersonalProfile:EcoConcernedPerson . " +
                "}", "x");
        assertEquals("Bill", result.get(0).toString());
    }

    @Test
    public void getShoppingListItems() {
        List<OWLIndividual> result = factory.singleQuery("SELECT ?x WHERE { " +
                "?y  rdf:type OntologyPersonalProfile:ShoppingList . " +
                "?y  OntologyPersonalProfile:hasShoppingListItem ?x. " +
                //   "?x OntologyPersonalProfile:hasShoppingListItem OntologyPersonalProfile:Bill." +
                "}", "x");

        List<OWLClass> typesOfAffinity = new ArrayList<OWLClass>();
        for (OWLIndividual affinity : result) {
            typesOfAffinity.add(factory.getType(affinity));
        }

        assertEquals("HervikStrawberryJam", result.get(0).toString());
        assertEquals("RegularProducedFood", typesOfAffinity.get(0).toString());
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

        assertEquals("MediumPriceSensitivity", typesOfAffinity.get(0).toString());
        assertEquals("HighFairTradeAffinity", typesOfAffinity.get(1).toString());
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

}
