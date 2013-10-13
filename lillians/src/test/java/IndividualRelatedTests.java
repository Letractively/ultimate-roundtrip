import org.junit.BeforeClass;
import org.junit.Test;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.inference.OWLReasonerFactory;
import org.semanticweb.owl.model.*;
import org.semanticweb.reasonerfactory.pellet.PelletReasonerFactory;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author Lillian Hella
 * Date: Feb 8, 2010
 */
public class IndividualRelatedTests {
  
    private static SparqlQueryFactory factory;
    private static OWLOntology ontology;
    private static OWLOntologyManager manager;
    private static Reasoner reasoner;

    @BeforeClass()
    public static void beforeClass(){
        factory = new SparqlQueryFactory();
        ontology = factory.ontology;
        manager = factory.manager;
        reasoner = factory.reasoner;
    }

    @Test
    public void lookUpNumberOfIndividualsTest() {
        int individuals = ontology.getReferencedIndividuals().size();
        //System.out.println("Number of individuals: " + individuals);
        assertEquals(true, individuals > 10);
    }

    @Test
    public void classAndIndTest() throws OWLOntologyCreationException, OWLOntologyChangeException, OWLReasonerException {
        OWLReasonerFactory reasonerFactory = new PelletReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(manager);

        Set<OWLOntology> importsClosure = manager.getImportsClosure(ontology);
        reasoner.loadOntologies(importsClosure);
        reasoner.classify();

        OWLClass jam = factory.findClassByName("#Jam");

        Set<Set<OWLClass>> subClsSets = reasoner.getDescendantClasses(jam);

        Set<OWLClass> subClses = OWLReasonerAdapter.flattenSetOfSets(subClsSets);

        OWLClass strawberryJam = factory.findClassByName("#StrawberryJam");
        Set<OWLIndividual> ind = reasoner.getIndividuals(strawberryJam, true);

        assertEquals(4, subClses.size());
        assertEquals(8, ind.size());
    }


    @Test
    public void getAllInstancesOfACLass() {
        OWLClass strawberryJam = factory.findClassByName("#StrawberryJam");
        Set<OWLIndividual> individuals = reasoner.getIndividuals(strawberryJam, false);
        //todo skrive dem ut? trenger i alle fall navn på dem...
        assertEquals(10, individuals.size());
    } //todo kan bruke query for å hente dem lettere ut? hvordan leser man resultatet lettest?

    @Test
    public void getInstanceOfAClass() {
        OWLClass strawberryJam = factory.findClassByName("#StrawberryJam");
        Set<OWLIndividual> individuals = reasoner.getIndividuals(strawberryJam, false);
        //todo trengs forløkke?

        assertEquals("ICAEcologicalStrawberryJam", individuals.toArray()[0].toString());
    }

    @Test
    public void getAllInstancesOfAClass() {
        // create property and resources to query the reasoner
        OWLClass person = factory.findClassByName("#Person");
        OWLObjectProperty hasGender = factory.findObjectProperty("#hasGender");
        OWLDataProperty hasAge = factory.findDataType("#hasAge");

        // get all instances of class
        Set<OWLIndividual> individuals = reasoner.getIndividuals(person, false);
        OWLIndividual[] ind = individuals.toArray(new OWLIndividual[]{});
        String age = reasoner.getRelatedValue(ind[2], hasAge).getLiteral();
        OWLIndividual gender = reasoner.getRelatedIndividual(ind[2], hasGender);

        assertEquals(6, individuals.size());
        assertEquals("39", age);
        assertEquals("Male", gender.toString());
        assertEquals("Bill", individuals.toArray()[2].toString());
    }

    @Test
    public void domainTest() {
        OWLObjectProperty prop = factory.getOWLObjectProperty("#hasEcoAffinity");
        OWLClass person = factory.findClassByName("#Person");
        OWLClass ecoAffinity = factory.findClassByName("#Person");
        OWLClass jam = factory.findClassByName("#Jam");
        boolean hmm = reasoner.hasDomain(prop, person);
        boolean hmm2 = reasoner.hasDomain(prop, jam);
        boolean hmm3 = reasoner.hasDomain(prop, ecoAffinity);
        System.out.println("hmm = " + hmm);
        System.out.println("hmm2 = " + hmm2);
        System.out.println("hmm3 = " + hmm3);

        assertEquals(true, hmm);
        assertEquals(false, hmm2);
        assertEquals(true, hmm3);
    }


    @Test
    public void testHasProperty() {
        OWLIndividual bill = factory.findIndividual("#Bill");
        OWLIndividual billsEA = factory.findIndividual("#BillsEcoAffinity");
        OWLObjectProperty prop = factory.findObjectProperty("#hasEcoAffinity");
        boolean hasProperty = reasoner.hasObjectPropertyRelationship(bill, prop, billsEA);
        OWLObjectProperty hasGender = factory.findObjectProperty("#hasGender");
        boolean hasG = reasoner.hasObjectPropertyRelationship(bill, hasGender, billsEA);
        System.out.println("hasProperty = " + hasProperty);
        System.out.println("hasG = " + hasG);

        assertEquals(true, hasProperty);
        assertEquals(false, hasG);

    }

    @Test
    public void testIfProuctIsFairTrade() {
        //er et product fairTrade?
        OWLIndividual hervikSJ = factory.findIndividual("#HervikStrawberryJam");
        OWLObjectProperty hasQualityMark = factory.findObjectProperty("#hasQualityMark");
        OWLIndividual fairTrade = factory.findIndividual("#FairTrade");

        boolean isProductFairTrade = reasoner.hasObjectPropertyRelationship(hervikSJ, hasQualityMark, fairTrade);
        System.out.println("isProductFairTrade = " + isProductFairTrade);

        assertEquals(false, isProductFairTrade);

    }

    //todo kan man lage metoder som sjekker disse egenskapene?
    //todo hvordan hente ut aktuelle egenskaper?
    //todo utgangspunktet er at man ha en individual -> trenger så alle egenskaper ved denne, eller kan man sjekke noen utvalgte?
    //todo metode ala som returnerer boolean (1 for ja og 0 for nei), med input individual - for da vil man kunne sjekke mange produkter for en og samme egenskap
    //todo 
    //todo hvordan vet man hvilke egenskaper man skal sjekke?

    @Test
    public void testIfProductIsEco() {
        OWLIndividual HEcoSJ = factory.findIndividual("#HervikEcoStrawberryJam");
        OWLIndividual HSJ = factory.findIndividual("#HervikStrawberryJam");

        OWLObjectProperty hasQualityMark = factory.findObjectProperty("#hasQualityMark");
        OWLIndividual eco = factory.findIndividual("#TESTEcological");

        boolean isProductEco1 = reasoner.hasObjectPropertyRelationship(HSJ, hasQualityMark, eco);
        boolean isProductEco2 = reasoner.hasObjectPropertyRelationship(HEcoSJ, hasQualityMark, eco);
        System.out.println("isProductEco = " + isProductEco1);
        System.out.println("isProductEco2 = " + isProductEco2);
        
        assertEquals(false, isProductEco1);
        assertEquals(true, isProductEco2);


    }
}
