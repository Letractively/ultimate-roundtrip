import no.ntnu.ontology.SparqlQueryFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.model.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author Lillian Hella
 * Date: Feb 8, 2010
 * Time: 8:52:14 AM
 */
public class ClassRelatedTests {
    SparqlQueryFactory factory;

    @Before
    public void setUp() throws OWLOntologyCreationException, URISyntaxException {
        factory = new SparqlQueryFactory(getClass().getResource("PersonalProfile.owl").toURI());
    }

       @Test
    public void findSuperClassOfIndividualBill() {
        OWLIndividual bill = factory.findIndividual("#Bill");
        Set<Set<OWLClass>> classesOfHSJ = factory.getTypes(bill);
        List<OWLClass> result = new ArrayList<OWLClass>();

        for (Set<OWLClass> classesOfHSJSet : classesOfHSJ) {
            for (OWLClass owlClass : classesOfHSJSet) {
                result.add(owlClass);
                System.out.println("owlIndividual = " + owlClass);
            }
        }

        List<OWLClass> resultEnd = new ArrayList<OWLClass>();

        for (OWLClass owlClass : result) {
            Set<Set<OWLClass>> superClassesOfTypesOfClass = factory.reasoner.getSuperClasses(owlClass);
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
        OWLIndividual hervikSJ = factory.findIndividual("#HervikStrawberryJam");
        Set<Set<OWLClass>> classesOfHSJ = factory.reasoner.getTypes(hervikSJ);
        List<OWLClass> result = new ArrayList<OWLClass>();

        for (Set<OWLClass> classesOfHSJSet : classesOfHSJ) {
            for (OWLClass owlClass : classesOfHSJSet) {
                result.add(owlClass);
                System.out.println("owlIndividual = " + owlClass);
            }
        }

        List<OWLClass> resultEnd = new ArrayList<OWLClass>();

        for (OWLClass owlClass : result) {
            Set<Set<OWLClass>> superClassesOfTypesOfClass = factory.reasoner.getSuperClasses(owlClass);
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

        // create property and resources to query the reasoner
        //OWLClass jam = factory.getOWLClass(URI.create(myURI + "#Jam"));
        //OWLObjectProperty hasProducer = factory.getOWLObjectProperty(URI.create(myURI + "#hasProducer"));

        //Set<OWLIndividual> individuals = reasoner.getIndividuals(jam, false);
        //OWLIndividual[] ind = individuals.toArray(new OWLIndividual[]{});
        //String age = reasoner.getRelatedValue(ind[2], hasProducer).getLiteral();
        OWLIndividual hervikStrawberryJam = factory.findIndividual("#HervikStrawberryJam");
        //OWLClass classOfHSJ = hervikStrawberryJam.;
        List<OWLClass> result = new ArrayList<OWLClass>();
        Set<Set<OWLClass>> classesOfHSJSets = factory.getTypes(hervikStrawberryJam);
        Set<OWLClass> clsesOfHSJ = OWLReasonerAdapter.flattenSetOfSets(classesOfHSJSets); //todo gjør ferdig!! trenger ikke like mye løkker

        for (Set<OWLClass> classesOfHSJSet : classesOfHSJSets) {
            for (OWLClass owlClass : classesOfHSJSet) {
                result.add(owlClass);
            }
        }
        assertEquals("RegularProducedFood", result.get(0).toString());
        assertEquals("HervikProducts", result.get(1).toString());
        assertEquals("StrawberryJam", result.get(2).toString());
    }

      @Test
    public void findProducerOfHervikJam() {
        // create property and resources to query the reasoner
        OWLIndividual hervikStrawberryJam = factory.findIndividual("#HervikStrawberryJam");
        OWLObjectProperty hasProducer = factory.findObjectProperty("#hasProducer");
        OWLIndividual producer = factory.getRelatedIndividual(hervikStrawberryJam, hasProducer);

        assertEquals("Hervik", producer.toString());
    }

    @Test
    public void checkClassOfInstance() {
        OWLIndividual bill = factory.findIndividual("#Bill");
        Set<Set<OWLClass>> classesOfHSJ = factory.getTypes(bill);
        List<OWLClass> result = new ArrayList<OWLClass>();
        for (Set<OWLClass> classesOfHSJSet : classesOfHSJ) {
            for (OWLClass owlClass : classesOfHSJSet) {
                result.add(owlClass);
            }
        }
        assertEquals("Man", result.get(0).toString());
        assertEquals("EcoConcernedPerson", result.get(1).toString());
    }

     @Test
    public void findAPersonsEcoAffinity() {
        OWLIndividual bill = factory.findIndividual("#Bill");
        OWLObjectProperty hasEcoAffinity = factory.findObjectProperty("#hasEcoAffinity");

        OWLIndividual ecoAffinity = factory.getRelatedIndividual(bill, hasEcoAffinity);
        OWLClass typeOfEcoAffinity = factory.getType(ecoAffinity);
        assertEquals("HighEcoAffinity", typeOfEcoAffinity.toString());
    }

    @Test
    public void findAPersonsPriceSensitivity() {
        OWLIndividual bill = factory.findIndividual("#Bill");
        OWLObjectProperty hasPriceAffinity = factory.findObjectProperty("#hasPriceSensitivity");

        OWLIndividual priceSensitivity = factory.getRelatedIndividual(bill, hasPriceAffinity);
        OWLClass typeOfPriceSensitivity = factory.getType(priceSensitivity);
        assertEquals("MediumPriceSensitivity", typeOfPriceSensitivity.toString());
    }

    @Test
    public void findAPersonsFairTradeAffinity() {
        OWLIndividual bill = factory.findIndividual("#Bill");
        OWLObjectProperty hasFairTradeAffinity = factory.findObjectProperty("#hasFairTradeAffinity");

        OWLIndividual fairTradeAffinity = factory.getRelatedIndividual(bill, hasFairTradeAffinity);
        OWLClass typeOfFairTradeAffinity = factory.getType(fairTradeAffinity);
        assertEquals("HighFairTradeAffinity", typeOfFairTradeAffinity.toString());
    }

    

    //blir nødt til å koble informasjonen vi har om produktet med en persons affinities
    //bruke EcoConcernedPerson, FairTradeConcerenedPerson, MediumPriceConcernedPerson osv
    //koble to og to sammen?
    //husk innholdet i produktet
}
