import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.model.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: hella
 * Date: Feb 8, 2010
 * Time: 8:52:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClassRelatedTests {
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
        ontology = manager.loadOntologyFromPhysicalURI(URI.create("file:/Users/hella/IdeaProjects/OWLapi/lillian/src/main/resources/PersonalProfile.owl"));
        reasoner = new Reasoner(manager);
        reasoner.loadOntology(ontology);
    }

       @Test
    public void findSuperClassOfIndividualBill() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLIndividual bill = factory.getOWLIndividual(URI.create(myURI + "#Bill"));
        Set<Set<OWLClass>> classesOfHSJ = reasoner.getTypes(bill);
        List<OWLClass> result = new ArrayList<OWLClass>();

        for (Set<OWLClass> classesOfHSJSet : classesOfHSJ) {
            for (OWLClass owlClass : classesOfHSJSet) {
                result.add(owlClass);
                System.out.println("owlIndividual = " + owlClass);
            }
        }

        List<OWLClass> resultEnd = new ArrayList<OWLClass>();

        for (OWLClass owlClass : result) {
            Set<Set<OWLClass>> superClassesOfTypesOfClass = reasoner.getSuperClasses(owlClass);
            System.out.println("superClassesOfTypesOfClass = " + superClassesOfTypesOfClass);
            for (Set<OWLClass> owlClass2 : superClassesOfTypesOfClass) {
                System.out.println("owlClass2 = " + owlClass2);
                for (OWLClass aClass : owlClass2) {
                    resultEnd.add(aClass);
                }
            }
        }

        System.err.println(resultEnd.toString());
        assertEquals("Man", result.get(0).toString());
        assertEquals("EcoConcernedPerson", result.get(1).toString());
        assertEquals("Person", resultEnd.get(0).toString());
        assertEquals("Person", resultEnd.get(1).toString());
    }


    @Test
    @Ignore
    public void findSuperClassOfIndividualHSJ() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLIndividual hervikSJ = factory.getOWLIndividual(URI.create(myURI + "#HervikStrawberryJam"));
        Set<Set<OWLClass>> classesOfHSJ = reasoner.getTypes(hervikSJ);
        List<OWLClass> result = new ArrayList<OWLClass>();

        for (Set<OWLClass> classesOfHSJSet : classesOfHSJ) {
            for (OWLClass owlClass : classesOfHSJSet) {
                result.add(owlClass);
                System.out.println("owlIndividual = " + owlClass);
            }
        }

        List<OWLClass> resultEnd = new ArrayList<OWLClass>();

        for (OWLClass owlClass : result) {
            Set<Set<OWLClass>> superClassesOfTypesOfClass = reasoner.getSuperClasses(owlClass);
            System.out.println("superClassesOfTypesOfClass = " + superClassesOfTypesOfClass);
            for (Set<OWLClass> owlClass2 : superClassesOfTypesOfClass) {
                System.out.println("owlClass2 = " + owlClass2);
                for (OWLClass aClass : owlClass2) {
                    resultEnd.add(aClass);
                }
            }
        }


        System.err.println(resultEnd.toString());
        assertEquals("RegularProducedFood", result.get(0).toString());
        assertEquals("HervikProducts", result.get(1).toString());
        assertEquals("Food", resultEnd.get(0).toString());
        assertEquals("Jam", resultEnd.get(1).toString());
    }

   @Test
    public void findClassOfIndividualHervikJam() {
        //må vi ha en hasName-relation?
        //hvilken klasse hører HervikblablaJam til?

        OWLDataFactory factory = manager.getOWLDataFactory();

        // create property and resources to query the reasoner
        //OWLClass jam = factory.getOWLClass(URI.create(myURI + "#Jam"));
        //OWLObjectProperty hasProducer = factory.getOWLObjectProperty(URI.create(myURI + "#hasProducer"));

        //Set<OWLIndividual> individuals = reasoner.getIndividuals(jam, false);
        //OWLIndividual[] ind = individuals.toArray(new OWLIndividual[]{});
        //String age = reasoner.getRelatedValue(ind[2], hasProducer).getLiteral();
        OWLIndividual hervikStrawberryJam = factory.getOWLIndividual(URI.create(myURI + "#HervikStrawberryJam"));
        //OWLClass classOfHSJ = hervikStrawberryJam.;
        List<OWLClass> result = new ArrayList<OWLClass>();
        Set<Set<OWLClass>> classesOfHSJSets = reasoner.getTypes(hervikStrawberryJam);
        Set<OWLClass> clsesOfHSJ = OWLReasonerAdapter.flattenSetOfSets(classesOfHSJSets); //todo gjør ferdig!! trenger ikke like mye løkker

        for (Set<OWLClass> classesOfHSJSet : classesOfHSJSets) {
            for (OWLClass owlClass : classesOfHSJSet) {
                result.add(owlClass);
            }
        }
        // 0 0 HervikProducts
        //  1 0 StrawberryJam
        assertEquals("HervikProducts", result.get(0).toString());
    }

      @Test
    public void findProducerOfHervikJam() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        // create property and resources to query the reasoner
        OWLIndividual hervikStrawberryJam = factory.getOWLIndividual(URI.create(myURI + "#HervikStrawberryJam"));
        OWLObjectProperty hasProducer = factory.getOWLObjectProperty(URI.create(myURI + "#hasProducer"));
        OWLIndividual producer = reasoner.getRelatedIndividual(hervikStrawberryJam, hasProducer);

        assertEquals("Hervik", producer.toString());
    }

    @Test
    public void checkClassOfInstance() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLIndividual bill = factory.getOWLIndividual(URI.create(myURI + "#Bill"));
        Set<Set<OWLClass>> classesOfHSJ = reasoner.getTypes(bill);
        List<OWLClass> result = new ArrayList<OWLClass>();
        Set<Set<OWLClass>> classesOfHSJSets = classesOfHSJ;
        for (Set<OWLClass> classesOfHSJSet : classesOfHSJSets) {
            for (OWLClass owlClass : classesOfHSJSet) {
                result.add(owlClass);
            }
        }
        assertEquals("Man", result.get(0).toString());
        assertEquals("EcoConcernedPerson", result.get(1).toString());
    }

     @Test
    public void findAPersonsEcoAffinity() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLIndividual bill = factory.getOWLIndividual(URI.create(myURI + "#Bill"));
        OWLObjectProperty hasEcoAffinity = factory.getOWLObjectProperty(URI.create(myURI + "#hasEcoAffinity"));

        OWLIndividual ecoAffinity = reasoner.getRelatedIndividual(bill, hasEcoAffinity);
        OWLClass typeOfEcoAffinity = reasoner.getType(ecoAffinity);
        //System.out.println("ecoAffinity = " + ecoAffinity);
        //System.out.println("typeOfEcoAffinity = " + typeOfEcoAffinity);

        assertEquals("HighEcoAffinity", typeOfEcoAffinity.toString());
    }

    @Test
    public void findAPersonsPriceSensitivity() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLIndividual bill = factory.getOWLIndividual(URI.create(myURI + "#Bill"));
        OWLObjectProperty hasPriceAffinity = factory.getOWLObjectProperty(URI.create(myURI + "#hasPriceSensitivity"));

        OWLIndividual priceSensitivity = reasoner.getRelatedIndividual(bill, hasPriceAffinity);
        OWLClass typeOfPriceSensitivity = reasoner.getType(priceSensitivity);
        //System.out.println("ecoAffinity = " + ecoAffinity);
        //System.out.println("typeOfEcoAffinity = " + typeOfEcoAffinity);

        assertEquals("MediumPriceSensitivity", typeOfPriceSensitivity.toString());
    }

    @Test
    public void findAPersonsFairTradeAffinity() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLIndividual bill = factory.getOWLIndividual(URI.create(myURI + "#Bill"));
        OWLObjectProperty hasFairTradeAffinity = factory.getOWLObjectProperty(URI.create(myURI + "#hasFairTradeAffinity"));

        OWLIndividual fairTradeAffinity = reasoner.getRelatedIndividual(bill, hasFairTradeAffinity);
        OWLClass typeOfFairTradeAffinity = reasoner.getType(fairTradeAffinity);
        //System.out.println("ecoAffinity = " + ecoAffinity);
        //System.out.println("typeOfEcoAffinity = " + typeOfEcoAffinity);

        assertEquals("HighFairTradeAffinity", typeOfFairTradeAffinity.toString());
    }

    //blir nødt til å koble informasjonen vi har om produktet med en persons affinities
    //bruke EcoConcernedPerson, FairTradeConcerenedPerson, MediumPriceConcernedPerson osv
    //koble to og to sammen?
    //husk innholdet i produktet
}
