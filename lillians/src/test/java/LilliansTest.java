import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.inference.OWLReasonerFactory;
import org.semanticweb.owl.io.OWLXMLOntologyFormat;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.DLExpressivityChecker;
import org.semanticweb.reasonerfactory.pellet.PelletReasonerFactory;

import java.net.URI;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: hella
 * Date: Jan 11, 2010
 * Time: 11:46:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class LilliansTest {
    OWLOntologyManager manager;
    OWLOntology ontology;
    public static final String myURI = "http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl";

    @Before
    public void setUp() throws OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();
        // We load an ontology
        // read the ontology
        ontology = manager.loadOntologyFromPhysicalURI(URI.create("file:/Users/hella/IdeaProjects/OWLapi/lillian/src/main/resources/PersonalProfile.owl"));
    }

    @Test
    public void loadOntologyTest() {
        OWLClass[] result = ontology.getReferencedClasses().toArray(new OWLClass[]{});
        assertEquals("dette var feil", 57, ontology.getReferencedClasses().size());
        assertEquals("HighPriceSensitivity", result[0].toString());
        assertEquals(false, result[0].isOWLThing());
    }

    @Test
    public void lookUpNumberOfIndividualsTest() {
        int individuals = ontology.getReferencedIndividuals().size();
        System.out.println("Number of individuals: " + individuals);
        assertEquals(true, individuals > 10);
    }

    @Test
    public void consistencyCheck() throws OWLReasonerException {
        OWLReasonerFactory reasonerFactory = new PelletReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(manager);
        Set<OWLOntology> importsClosure = manager.getImportsClosure(ontology);
        reasoner.loadOntologies(importsClosure);
        reasoner.classify();

        DLExpressivityChecker checker = new DLExpressivityChecker(importsClosure);

        boolean consistent = reasoner.isConsistent(ontology);

        assertEquals(true, consistent);
    }

    @Test
    public void inconsistentClassesCheck() throws OWLReasonerException {
        OWLReasonerFactory reasonerFactory = new PelletReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(manager);

        Set<OWLOntology> importsClosure = manager.getImportsClosure(ontology);
        reasoner.loadOntologies(importsClosure);
        reasoner.classify();

        Set<OWLClass> inconsistentClasses = reasoner.getInconsistentClasses();

        assertEquals(0, inconsistentClasses.size());
    }

    @Test
    public void contactWithReasoner() {
        OWLReasonerFactory reasonerFactory = new PelletReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(manager);
        Set<OWLOntology> importsClosure = manager.getImportsClosure(ontology);
        try {
            reasoner.loadOntologies(importsClosure);
            reasoner.classify();
        } catch (OWLReasonerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            fail("Reasoner probably not imported.");
        }


    }

    @Test
    public void accessAllClasses() throws OWLReasonerException {
        OWLReasonerFactory reasonerFactory = new PelletReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(manager);

        Set<OWLOntology> importsClosure = manager.getImportsClosure(ontology);
        reasoner.loadOntologies(importsClosure);
        reasoner.classify();

        //reasoner.;

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

        assertEquals(4, subClses.size());

        OWLClass strawberryJam = manager.getOWLDataFactory().getOWLClass(URI.create(myURI + "#StrawberryJam"));
        Set<OWLIndividual> ind = reasoner.getIndividuals(strawberryJam, true);

        assertEquals(4, ind.size());
    }

    /*
@Test
public void reasonerExample() throws OWLOntologyCreationException, OWLOntologyChangeException, OWLReasonerException {
    Reasoner reasoner = new Reasoner(manager);

    reasoner.loadOntology(ontology);

    reasoner.getKB().realize();
    reasoner.getKB().printClassTree();

    OWLClass jam = manager.getOWLDataFactory().getOWLClass(URI.create("myURI#Jam"));
    OWLObjectProperty hasIngredient = manager.getOWLDataFactory().getOWLObjectProperty(URI.create("myURI#hasIngredient"));

    Set<OWLIndividual> individuals = reasoner.getIndividuals(jam, false);
    for (OWLIndividual ind : individuals) {
        // get the info about this specific individual
        //
        String name = reasoner.getRelatedValue(ind, hasIngredient).getLiteral();
        OWLClass type = reasoner.getType(ind);
        OWLIndividual homepage = reasoner.getRelatedIndividual(ind, hasIngredient);

        System.out.println("Type: " + type.getURI().getFragment());
    }
}
    **/

    @Test
    public void correctURIInOntology() {
        String ontologyURI = ontology.getURI().toString();
        assertEquals(myURI, ontologyURI);
    }

    @Test
    public void checkClassOfInstance() {


        //assertEquals(person, pers);
    }

    @Test
    public void getAllInstanceOfACLass() {
        Reasoner reasoner = new Reasoner(manager);
        reasoner.loadOntology(ontology);

        OWLClass strawberryJam = manager.getOWLDataFactory().getOWLClass(URI.create(myURI + "#StrawberryJam"));
        Set<OWLIndividual> individuals = reasoner.getIndividuals(strawberryJam, false);
        //skrive dem ut? trenger i alle fall navn på dem...
        assertEquals(6, individuals.size());
    }

    @Test
    public void getInstanceOfAClass() {
        Reasoner reasoner = new Reasoner(manager);
        reasoner.loadOntology(ontology);
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLClass strawberryJam = manager.getOWLDataFactory().getOWLClass(URI.create(myURI + "#StrawberryJam"));
        Set<OWLIndividual> individuals = reasoner.getIndividuals(strawberryJam, false);

        assertEquals("ICAEcologicalStrawberryJam", individuals.toArray()[0].toString());
    }

    @Test
    public void getAllInstancesOfAClass() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        // load the ontology to the reasoner
        Reasoner reasoner = new Reasoner(manager);
        reasoner.setOntology(ontology);

        // create property and resources to query the reasoner
        OWLClass person = factory.getOWLClass(URI.create(myURI + "#Person"));
        OWLObjectProperty hasGender = factory.getOWLObjectProperty(URI.create(myURI + "#hasGender"));
        OWLDataProperty hasAge = factory.getOWLDataProperty(URI.create(myURI + "#hasAge"));

        // get all instances of class
        Set<OWLIndividual> individuals = reasoner.getIndividuals(person, false);
        OWLIndividual[] ind = individuals.toArray(new OWLIndividual[]{});
        String age = reasoner.getRelatedValue(ind[2], hasAge).getLiteral();
        OWLIndividual gender = reasoner.getRelatedIndividual(ind[2], hasGender);

        assertEquals(5, individuals.size());
        assertEquals("Bill", individuals.toArray()[2].toString());
    }

    @Test
    public void getInstanceAndItsProperty() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        // load the ontology to the reasoner
        Reasoner reasoner = new Reasoner(manager);
        reasoner.setOntology(ontology);

        // create property and resources to query the reasoner
        OWLClass person = factory.getOWLClass(URI.create(myURI + "#Person"));
        OWLObjectProperty hasGender = factory.getOWLObjectProperty(URI.create(myURI + "#hasGender"));
        OWLDataProperty hasAge = factory.getOWLDataProperty(URI.create(myURI + "#hasAge"));

        Set<OWLIndividual> individuals = reasoner.getIndividuals(person, false);
        OWLIndividual[] ind = individuals.toArray(new OWLIndividual[]{});
        String age = reasoner.getRelatedValue(ind[2], hasAge).getLiteral();
        OWLIndividual gender = reasoner.getRelatedIndividual(ind[2], hasGender);

        //System.out.println("age: " + age + "  gender: " + gender);

        assertEquals("Male", gender.toString());
        assertEquals("39", age);
        assertEquals("Bill", individuals.toArray()[2].toString());
    }

    @Test
    public void findRangeOfRelation() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        // load the ontology to the reasoner
        Reasoner reasoner = new Reasoner(manager);
        reasoner.setOntology(ontology);

        // create property and resources to query the reasoner
        OWLClass jam = factory.getOWLClass(URI.create(myURI + "#Jam"));
        OWLObjectProperty hasProducer = factory.getOWLObjectProperty(URI.create(myURI + "#hasProducer"));

        Set<OWLIndividual> individuals = reasoner.getIndividuals(jam, false);
        OWLIndividual[] ind = individuals.toArray(new OWLIndividual[]{});
        //String age = reasoner.getRelatedValue(ind[2], hasProducer).getLiteral();
        OWLIndividual producer = reasoner.getRelatedIndividual(ind[2], hasProducer);

        assertEquals("Hervik", producer.toString());
    }


    /*
    Reasoner reasoner = new Reasoner(manager);
    reasoner.loadOntology(ontology);
    OWLDataFactory factory = manager.getOWLDataFactory();

    OWLClass strawberryJam = manager.getOWLDataFactory().getOWLClass(URI.create(myURI + "#StrawberryJam"));
    Set<OWLIndividual> individuals = reasoner.getIndividuals(strawberryJam, false);

    OWLObjectProperty hasProducer = reasoner.getOWLObjectProperty(URI.create(myURI +"#hasProducer"));

    for( OWLIndividual ind : individuals ) {
        // get the info about this specific individual
        //String name = reasoner.getRelatedValue(ind, hasProducer).getLiteral();
        OWLClass type = reasoner.getType(ind);
        OWLIndividual homepage = reasoner.getRelatedIndividual(ind, hasProducer);

        // print the results
        //System.out.println("Name: " + name);
        System.out.println("Type: " + type.getURI().getFragment());
        if(homepage == null)
            System.out.println("HasProducer: Unknown");
        else
            System.out.println("HasProducer: " + hasProducer.getRanges(ontology));    //URI ikke så nyttig her   - trenger en for-løkke til
        System.out.println();
    }
    **/
    //assertEquals(den første på listen, individuals.size());
}


//hvordan finne ut range til en relasjon?
//properties til en instans
//verdier til en property til en instans

//finne en instans sin klasse        - dvs noe med en property som hører til en eller annen klasse
//finne instanser som tilfredsstiller visse krav





