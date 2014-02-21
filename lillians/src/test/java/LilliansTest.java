import no.ntnu.FlatMapCallback;
import no.ntnu.ontology.SparqlQueryFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.inference.OWLReasonerFactory;
import org.semanticweb.owl.model.*;
import org.semanticweb.reasonerfactory.pellet.PelletReasonerFactory;
import java.net.URISyntaxException;
import java.util.*;

import static no.ntnu.TestUtils.contains;
import static no.ntnu.TestUtils.flatSomething;
import static org.junit.Assert.*;

/**
 * @author Lillian Hella
 */
public class LilliansTest {
    private static SparqlQueryFactory factory;
    private static OWLOntology ontology;
    private static OWLOntologyManager manager;
    private static Reasoner reasoner;

    @BeforeClass
    public static void setUp() throws URISyntaxException {
        factory = new SparqlQueryFactory(LilliansTest.class.getResource("PersonalProfile.owl").toURI());
        ontology = factory.ontology;
        manager = factory.manager;
        reasoner = factory.reasoner;
    }

    //GET STARTED

    @Test
    public void loadOntologyTest() {
        Set<OWLClass> result = ontology.getReferencedClasses();
        assertEquals("dette var feil", 73, ontology.getReferencedClasses().size());
        assertTrue(contains("HighPriceSensitivity", result));
        assertEquals(false, result.toArray(new OWLClass[]{})[0].isOWLThing());
    }

    @Test
    public void consistencyCheck() throws OWLReasonerException {
        OWLReasonerFactory reasonerFactory = new PelletReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(manager);
        Set<OWLOntology> importsClosure = manager.getImportsClosure(ontology);
        reasoner.loadOntologies(importsClosure);
        reasoner.classify();

        //DLExpressivityChecker checker = new DLExpressivityChecker(importsClosure);

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
    public void correctURIInOntology() {
        String ontologyURI = ontology.getURI().toString();
        assertEquals(SparqlQueryFactory.myURI, ontologyURI);
    }


    @Test
    public void hasContactWithReasoner() {
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
    public void canAccessAllClasses() throws OWLReasonerException {
        OWLReasonerFactory reasonerFactory = new PelletReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(manager);

        Set<OWLOntology> importsClosure = manager.getImportsClosure(ontology);
        reasoner.loadOntologies(importsClosure);

        reasoner.classify();

        assertEquals(true, reasoner.isClassified());
    }

    @Test
    public void findSuperClassOfIndividualBill() {
        OWLIndividual bill = factory.findIndividual("#Bill");

        List<OWLClass> result = flatSomething(Collections.singletonList(bill), new FlatMapCallback() {
            public Set<Set<OWLClass>> doMagic(OWLEntity individual) {
                return factory.getTypes((OWLIndividual) individual);
            }
        });

        List<OWLClass> resultEnd = flatSomething(result, new FlatMapCallback() {
            public Set<Set<OWLClass>> doMagic(OWLEntity individual) {
                return reasoner.getSuperClasses((OWLClass)individual);
            }
        });

        System.err.println(resultEnd.toString());
        assertTrue(contains("Man", result));
        assertTrue(contains("EcoConcernedPerson", result));
        assertTrue(contains("Person", resultEnd));
    }


    @Test
    public void findSuperClassOfIndividualHSJ() {
        OWLIndividual hervikSJ = factory.findIndividual("#HervikStrawberryJam");

        List<OWLClass> types = flatSomething(Collections.singletonList(hervikSJ), new FlatMapCallback() {
            public Set<Set<OWLClass>> doMagic(OWLEntity individual) {
                return factory.getTypes((OWLIndividual)individual);
            }
        });

        List<OWLClass> superClasses = flatSomething(types, new FlatMapCallback() {
            public Set<Set<OWLClass>> doMagic(OWLEntity individual) {
                return reasoner.getSuperClasses((OWLClass)individual);
            }
        });

        assertTrue(contains("RegularProducedFood", types));
        assertTrue(contains("HervikProducts", types));
        assertTrue(contains("Food", superClasses));
        assertTrue(contains("Jam", superClasses));
        assertEquals("[Food, Food, Jam]", superClasses.toString());
    }

    @Test
    public void findClassOfIndividualHervikJam() {
        OWLIndividual hervikStrawberryJam = factory.findIndividual("#HervikStrawberryJam");
        List<OWLClass> result = flatSomething(Collections.singletonList(hervikStrawberryJam), new FlatMapCallback() {
            public Set<Set<OWLClass>> doMagic(OWLEntity individual) {
                return factory.getTypes((OWLIndividual)individual);
            }
        });
        assertEquals(3, result.size());
        assertEquals("HervikProducts", result.get(0).toString());
        assertEquals("RegularProducedFood", result.get(1).toString());
        assertEquals("StrawberryJam", result.get(2).toString());
    }

    @Test
    public void getInstanceAndItsProperty() {
        OWLClass person = factory.findClassByName("#Person");
        OWLObjectProperty hasGender = factory.findObjectProperty("#hasGender");
        OWLDataProperty hasAge = factory.findDataType("#hasAge");

        Set<OWLIndividual> individuals = reasoner.getIndividuals(person, false);

        assertEquals(6, individuals.size());
        boolean foundBill = false;
        for (OWLIndividual individual : individuals) {
            if("Bill".equals(individual.toString())) {
                assertEquals("39", reasoner.getRelatedValue(individual, hasAge).getLiteral());
                assertEquals("Male", factory.getRelatedIndividual(individual, hasGender).toString());
                foundBill = true;
            }
        }
        assertTrue("Could not find bill in resultSet", foundBill);
    }

    @Test
    public void findProducersOfJam() {
        OWLClass jam = factory.findClassByName("#Jam");
        OWLObjectProperty hasProducer = factory.findObjectProperty("#hasProducer");

        List<OWLIndividual> producers = new ArrayList<OWLIndividual>();
        for (OWLIndividual individual : reasoner.getIndividuals(jam, false)) {
            producers.add(factory.getRelatedIndividual(individual, hasProducer));
        }

        assertTrue(contains("Nora", producers));
        assertTrue(contains("Hervik", producers));
        assertTrue(contains("ICA", producers));
        assertTrue(contains("Euroshopper", producers));
    }

    @Test
    public void findProducerOfHervikJam() {
        // create property and resources to query the factory
        OWLIndividual hervikStrawberryJam = factory.findIndividual("#HervikStrawberryJam");
        OWLObjectProperty hasProducer = factory.findObjectProperty("#hasProducer");
        OWLIndividual producer = factory.getRelatedIndividual(hervikStrawberryJam, hasProducer);

        assertEquals("Hervik", producer.toString());
    }

    @Test
    public void checkClassOfInstance() {
        OWLIndividual bill = factory.findIndividual("#Bill");
        List<OWLClass> billsTypes = flatSomething(Collections.singletonList(bill), new FlatMapCallback() {
            public Set<Set<OWLClass>> doMagic(OWLEntity individual) {
                return factory.getTypes((OWLIndividual) individual);
            }
        });

        assertTrue(contains("Man", billsTypes));
        assertTrue(contains("EcoConcernedPerson", billsTypes));
        assertTrue(contains("MediumPriceConcernedPerson", billsTypes));
        assertTrue(contains("Adult", billsTypes));
        assertTrue(contains("FairTradeConcernedPerson", billsTypes));
    }


    @Test
    public void findJamsAndTheirProducers() {
        OWLClass strawberryJam = factory.findClassByName("#StrawberryJam");
        Set<OWLIndividual> individuals = reasoner.getIndividuals(strawberryJam, false);
        final OWLObjectProperty hasProducer = factory.findObjectProperty("#hasProducer");

        List<OWLIndividual> resultset = new ArrayList<OWLIndividual>();

        for (OWLIndividual ind : individuals) {
            Set<OWLIndividual> b = factory.getRelatedIndividuals(ind, hasProducer);
            for (OWLIndividual individual : b) {
                resultset.add(individual);
            }
        }

        assertEquals(9, resultset.size());
        assertTrue(contains("ICA", resultset));
        assertTrue(contains("Nora", resultset));
    }

    @Test
    public void findProducerHervikSJ() {
        OWLObjectProperty hasProducer = factory.findObjectProperty("#hasProducer");
        OWLIndividual hervikSJ = factory.findIndividual("#HervikStrawberryJam");
        OWLIndividual producer = factory.getRelatedIndividual(hervikSJ, hasProducer);

        assertEquals("Hervik", producer.toString());
    }


    @Test
    public void findAllDataPropertiesForIndividualBill() {
        // create property and resources to query the factory
        OWLIndividual bill = factory.findIndividual("#Bill");
        Set<OWLDataProperty> dataProperties = reasoner.getDataProperties(); //ALLE

        List<String> list = new ArrayList<String>();

        for (OWLDataProperty owlDataProperty : dataProperties) {
            System.out.println("owlDataProperty = " + owlDataProperty);

            OWLConstant value1 = reasoner.getRelatedValue(bill, owlDataProperty);
            System.out.println("value1 = " + value1);
            if (value1 != null) {
                list.add(value1.getLiteral());
            }

            Set<OWLDataRange> dataType = reasoner.getRanges(owlDataProperty);
            System.out.println("dataType = " + dataType);
            for (OWLDataRange owlDataRange : dataType) {
                String value = owlDataRange.toString();
                System.out.println("value = " + value);
            }
        }
        assertEquals("39", list.get(0));
    }


    @Test
    public void printOntologyClassTree() {
        reasoner.getKB().realize();
        reasoner.getKB().printClassTree();
    }

    @Test
    public void findTypeAndIngredientsOfJam() throws OWLOntologyCreationException, OWLOntologyChangeException, OWLReasonerException {
        OWLClass jam = factory.findClassByName("#Jam");
        OWLObjectProperty hasIngredient = factory.findObjectProperty("#hasIngredient");

        Set<OWLIndividual> individuals = reasoner.getIndividuals(jam, false);
        List<String> results = new ArrayList<String>();

        for (OWLIndividual ind : individuals) {
            // get the info about this specific individual
            //OWLIndividual name = .getRelatedIndividual(ind, hasIngredient);
            OWLClass type = factory.getType(ind);
            OWLIndividual ingredient = factory.getRelatedIndividual(ind, hasIngredient);
            results.add(ind.toString() + " " + type.getURI().getFragment() + " " + ingredient);
        }
        assertTrue(results.contains("ICAEcologicalStrawberryJam EcologicalStrawberryJam Strawberry"));
        assertTrue(results.contains("EuroshopperStrawberryJam RegularProducedFood Strawberry"));
        assertEquals(10, results.size());
    }

    @Test
    public void findBillsEcoAffinity() {
        OWLIndividual bill = factory.findIndividual("#Bill");
        OWLObjectProperty hasEcoAffinity = factory.findObjectProperty("#hasEcoAffinity");

        OWLIndividual ecoAffinity = factory.getRelatedIndividual(bill, hasEcoAffinity);
        OWLClass typeOfEcoAffinity = factory.getType(ecoAffinity);
        assertEquals("HighEcoAffinity", typeOfEcoAffinity.toString());
    }

    @Test
    public void findBillsPriceSensitivity() {
        OWLIndividual bill = factory.findIndividual("#Bill");
        OWLObjectProperty hasPriceAffinity = factory.findObjectProperty("#hasPriceSensitivity");

        OWLIndividual priceSensitivity = factory.getRelatedIndividual(bill, hasPriceAffinity);
        OWLClass typeOfPriceSensitivity = factory.getType(priceSensitivity);
        assertEquals("MediumPriceSensitivity", typeOfPriceSensitivity.toString());
    }

    @Test
    public void findBillsFairTradeAffinity() {
        OWLIndividual bill = factory.findIndividual("#Bill");
        OWLObjectProperty hasFairTradeAffinity = factory.findObjectProperty("#hasFairTradeAffinity");

        OWLIndividual fairTradeAffinity = factory.getRelatedIndividual(bill, hasFairTradeAffinity);
        OWLClass typeOfFairTradeAffinity = factory.getType(fairTradeAffinity);
        assertEquals("HighFairTradeAffinity", typeOfFairTradeAffinity.toString());
    }

    @Test
    //TODO Skal kanskje gjøres på en annen måte
    public void computeRelevanceOfAProduct() {
        //m� gj�res for alle aktuelle kandidater
        OWLIndividual icaEco = factory.findIndividual("#ICAEcologicalStrawberryJam");

        List<MyComparableClass> sortableList = new ArrayList<MyComparableClass>();
        sortableList.add(new MyComparableClass(icaEco, 3));
        sortableList.add(new MyComparableClass(icaEco, 11));
        sortableList.add(new MyComparableClass(icaEco, 2));
        for (MyComparableClass myComparableClass : sortableList) {
            System.out.println(myComparableClass.relevance);
        }
        Collections.sort(sortableList);
        for (MyComparableClass myComparableClass : sortableList) {
            System.out.println(myComparableClass.relevance);
        }

        //find all the other individuals of the same type
        //hva kan man hente ut ved hjelp av sp�rring?

        //todo assertEquals
    }

//hvordan finne ut range til en relasjon? ->  m� vite instansen du h�rer til
//properties til en instans    - m� trikses med
//verdier til en property til en instans - ok

//finne en instans sin klasse        - dvs noe med en property som h�rer til en eller annen klasse

    /*
 @Test
    public void findAllObjectPropertiesForIndividual() {
          = new (manager);
        .setOntology(ontology);

        OWLIndividual hervikSJ = factory.findIndividual("#HervikStrawberryJam"));
        OWLClass strawberryJam = factory.findClassByName("#StrawberryJam"));
        Set<Set<OWLClass>> type = .getTypes(hervikSJ);

        Set<OWLObjectProperty> objectProperties = .getObjectProperties(); //alle
        //s� kan man evt koble properties til domains, og videre lage lister for hvilke som h�rer til hvilke domains, og videre hvilke ranges de har
        for (OWLObjectProperty objectProperty : objectProperties) {

        }

        for (OWLProperty<?, ?> property : objectProperties) {
            if (property instanceof OWLObjectProperty) {
                OWLObjectProperty owlObjectProperty = (OWLObjectProperty) property;
                //OWLProperty prop2 = property.asOWLObjectProperty();
                System.out.println("owlObjectProperty = " + owlObjectProperty);
                //OWLObjectProperty prop22 = factory.findClassByName("#"+ prop2));
                OWLIndividual rangeOfProp2 = .getRelatedIndividual(hervikSJ, owlObjectProperty);
                System.out.println("rangeOfProp2 = " + rangeOfProp2);
            } else if (property instanceof OWLDataType) {
                OWLDataType owlDatatype = (OWLDataType) property;
                //OWLProperty prop1 = property.asOWLDataProperty();
                System.out.println("owlDatatype = " + owlDatatype);
                // OWLDataType rangeOfOwlDatatype = .getRelatedValue(hervikSJ, owlDatatype);
                //OWLIndividual rangeOfProp1 = .getRelatedIndividual(HervikSJ, );
            }


            //OWLPropertyRange     ;
        }

    }

     */


    public class MyComparableClass implements Comparable {
        final int relevance;
        final OWLIndividual owlIndividual;

        public MyComparableClass(OWLIndividual owlIndividual, int relevance) {
            this.relevance = relevance;
            this.owlIndividual = owlIndividual;
        }

        public int compareTo(Object o) {
            if (o instanceof MyComparableClass) {
                MyComparableClass other = (MyComparableClass) o;
                if (this.relevance < other.relevance)
                    return 1;
                else if (this.relevance == other.relevance)
                    return 0;
                else
                    return -1;

                // If this < o, return a negative value
                // If this = o, return 0
                // If this > o, return a positive value
            } else return 0;
        }
    }

}

//slutt





