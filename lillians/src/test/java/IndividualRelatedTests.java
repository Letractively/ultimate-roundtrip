import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.inference.OWLReasonerFactory;
import org.semanticweb.owl.model.*;
import org.semanticweb.reasonerfactory.pellet.PelletReasonerFactory;

import java.net.URI;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: hella
 * Date: Feb 8, 2010
 * Time: 8:52:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class IndividualRelatedTests {
    OWLOntologyManager manager;
    OWLOntology ontology;
    Reasoner reasoner;
    public static final String myURI = "http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl";

    //todo legge til reasoner i before også?  + factory?

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

        OWLClass jam = manager.getOWLDataFactory().getOWLClass(URI.create(myURI + "#Jam"));

        Set<Set<OWLClass>> subClsSets = reasoner.getDescendantClasses(jam);

        Set<OWLClass> subClses = OWLReasonerAdapter.flattenSetOfSets(subClsSets);

        OWLClass strawberryJam = manager.getOWLDataFactory().getOWLClass(URI.create(myURI + "#StrawberryJam"));
        Set<OWLIndividual> ind = reasoner.getIndividuals(strawberryJam, true);

        assertEquals(4, subClses.size());
        assertEquals(8, ind.size());
    }


    @Test
    public void getAllInstancesOfACLass() {
        OWLClass strawberryJam = manager.getOWLDataFactory().getOWLClass(URI.create(myURI + "#StrawberryJam"));
        Set<OWLIndividual> individuals = reasoner.getIndividuals(strawberryJam, false);
        //todo skrive dem ut? trenger i alle fall navn på dem...
        assertEquals(10, individuals.size());
    } //todo kan bruke query for å hente dem lettere ut? hvordan leser man resultatet lettest?

    @Test
    public void getInstanceOfAClass() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLClass strawberryJam = factory.getOWLClass(URI.create(myURI + "#StrawberryJam"));
        Set<OWLIndividual> individuals = reasoner.getIndividuals(strawberryJam, false);
        //todo trengs forløkke?

        assertEquals("ICAEcologicalStrawberryJam", individuals.toArray()[0].toString());
    }

    @Test
    public void getAllInstancesOfAClass() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        // create property and resources to query the reasoner
        OWLClass person = factory.getOWLClass(URI.create(myURI + "#Person"));
        OWLObjectProperty hasGender = factory.getOWLObjectProperty(URI.create(myURI + "#hasGender"));
        OWLDataProperty hasAge = factory.getOWLDataProperty(URI.create(myURI + "#hasAge"));

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
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLObjectProperty prop = factory.getOWLObjectProperty(URI.create(myURI + "#hasEcoAffinity"));
        OWLClass person = factory.getOWLClass(URI.create(myURI + "#Person"));
        OWLClass ecoAffinity = factory.getOWLClass(URI.create(myURI + "#Person"));
        OWLClass jam = factory.getOWLClass(URI.create(myURI + "#Jam"));
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
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLIndividual bill = factory.getOWLIndividual(URI.create(myURI + "#Bill"));
        OWLIndividual billsEA = factory.getOWLIndividual(URI.create(myURI + "#BillsEcoAffinity"));
        OWLObjectProperty prop = factory.getOWLObjectProperty(URI.create(myURI + "#hasEcoAffinity"));
        boolean hasProperty = reasoner.hasObjectPropertyRelationship(bill, prop, billsEA);
        OWLObjectProperty hasGender = factory.getOWLObjectProperty(URI.create(myURI + "#hasGender"));
        boolean hasG = reasoner.hasObjectPropertyRelationship(bill, hasGender, billsEA);
        System.out.println("hasProperty = " + hasProperty);
        System.out.println("hasG = " + hasG);

        assertEquals(true, hasProperty);
        assertEquals(false, hasG);

    }

    @Test
    public void testIfProuctIsFairTrade() {
        //er et product fairTrade?
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLIndividual hervikSJ = factory.getOWLIndividual(URI.create(myURI + "#HervikStrawberryJam"));
        OWLObjectProperty hasQualityMark = factory.getOWLObjectProperty(URI.create(myURI + "#hasQualityMark"));
        OWLIndividual fairTrade = factory.getOWLIndividual(URI.create(myURI + "#FairTrade"));

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
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLIndividual HEcoSJ = factory.getOWLIndividual(URI.create(myURI + "#HervikEcoStrawberryJam"));
        OWLIndividual HSJ = factory.getOWLIndividual(URI.create(myURI + "#HervikStrawberryJam"));

        OWLObjectProperty hasQualityMark = factory.getOWLObjectProperty(URI.create(myURI + "#hasQualityMark"));
        OWLIndividual eco = factory.getOWLIndividual(URI.create(myURI + "#TESTEcological"));

        boolean isProductEco1 = reasoner.hasObjectPropertyRelationship(HSJ, hasQualityMark, eco);
        boolean isProductEco2 = reasoner.hasObjectPropertyRelationship(HEcoSJ, hasQualityMark, eco);
        System.out.println("isProductEco = " + isProductEco1);
        System.out.println("isProductEco2 = " + isProductEco2);
        
        assertEquals(false, isProductEco1);
        assertEquals(true, isProductEco2);


    }

    //hasdomain hasrange

    /*
    @Test
    public void testForCheckingType() {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLIndividual bill = factory.getOWLIndividual(URI.create(myURI + "Bill"));
        OWLDescription hm = new OWLDescription();
               // isOwlClass
        boolean hasType = reasoner.hasType(bill, hm, true);
        System.out.println("hasType = " + hasType);

    }
    **/


}
