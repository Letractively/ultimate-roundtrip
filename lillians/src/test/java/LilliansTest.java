import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasoner;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.inference.OWLReasonerFactory;
import org.semanticweb.owl.model.*;
import org.semanticweb.reasonerfactory.pellet.PelletReasonerFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import static org.junit.Assert.*;

/**
 * @author Lillian Hella
 */
public class LilliansTest {
    OWLOntologyManager manager;
    OWLOntology ontology;
    Reasoner reasoner;
    public static final String myURI = "http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl";


    //todo legge til reasoner i before ogs�?  + factory?

    @Before
    public void setUp() throws OWLOntologyCreationException, URISyntaxException {
        manager = OWLManager.createOWLOntologyManager();
        // We load an ontology
        // read the ontology
        ontology = manager.loadOntologyFromPhysicalURI(getClass().getResource("PersonalProfile.owl").toURI());
        reasoner = new Reasoner(manager);
        reasoner.loadOntology(ontology);

    }

    //GET STARTED

    @Test
    public void loadOntologyTest() {
        OWLClass[] result = ontology.getReferencedClasses().toArray(new OWLClass[]{});
        assertEquals("dette var feil", 73, ontology.getReferencedClasses().size());
        assertEquals("HighPriceSensitivity", result[0].toString());
        assertEquals(false, result[0].isOWLThing());
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
        assertEquals(myURI, ontologyURI);
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


    /*MAIN OWL API INFO LOOK UPS*/
    // Det meste under her er kopiert

    //FOR INDIVIDUALS

    //FOR CLASSES

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
        assertEquals("Food", resultEnd.get(1).toString());
        assertEquals("Jam", resultEnd.get(2).toString());
    }

    @Test
    public void findClassOfIndividualHervikJam() {
        //m� vi ha en hasName-relation?
        //hvilken klasse h�rer HervikblablaJam til?

        OWLDataFactory factory = manager.getOWLDataFactory();

        // create property and resources to query the reasoner
        //OWLClass jam = factory.getOWLClass(URI.create(myURI + "#Jam"));
        //OWLObjectProperty hasProducer = factory.getOWLObjectProperty(URI.create(myURI + "#hasProducer"));

        //Set<OWLIndividual> individuals = reasoner.getIndividuals(jam, false);
        //OWLIndividual[] ind = individuals.toArray(new OWLIndividual[]{});
        //String age = reasoner.getRelatedValue(ind[2], hasProducer).getLiteral();
        OWLIndividual hervikStrawberryJam = factory.getOWLIndividual(URI.create(myURI + "#HervikStrawberryJam"));
        List<OWLClass> result = new ArrayList<OWLClass>();
        Set<Set<OWLClass>> classesOfHSJSets = reasoner.getTypes(hervikStrawberryJam);
        Set<OWLClass> clsesOfHSJ = OWLReasonerAdapter.flattenSetOfSets(classesOfHSJSets); //todo gj�r ferdig!! trenger ikke like mye l�kker

        for (Set<OWLClass> classesOfHSJSet : classesOfHSJSets) {
            for (OWLClass owlClass : classesOfHSJSet) {
                result.add(owlClass);
            }
        }
        assertEquals("RegularProducedFood", result.get(0).toString());
        assertEquals("HervikProducts", result.get(1).toString());
        assertEquals("StrawberryJam", result.get(2).toString());
    }

    //FOR PROPERTY INFORMATION

    @Test
    public void getInstanceAndItsProperty() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        // create property and resources to query the reasoner
        OWLClass person = factory.getOWLClass(URI.create(myURI + "#Person"));
        OWLObjectProperty hasGender = factory.getOWLObjectProperty(URI.create(myURI + "#hasGender"));
        OWLDataProperty hasAge = factory.getOWLDataProperty(URI.create(myURI + "#hasAge"));

        Set<OWLIndividual> individuals = reasoner.getIndividuals(person, false);
        OWLIndividual[] ind = individuals.toArray(new OWLIndividual[]{});
        String age = reasoner.getRelatedValue(ind[2], hasAge).getLiteral();
        OWLIndividual gender = reasoner.getRelatedIndividual(ind[2], hasGender);

        assertEquals("Male", gender.toString());
        assertEquals("39", age);
        assertEquals("Bill", individuals.toArray()[2].toString());
    }

    @Test
    public void findRangeOfRelation() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        // create property and resources to query the reasoner
        OWLClass jam = factory.getOWLClass(URI.create(myURI + "#Jam"));
        OWLObjectProperty hasProducer = factory.getOWLObjectProperty(URI.create(myURI + "#hasProducer"));

        Set<OWLIndividual> individuals = reasoner.getIndividuals(jam, false);
        OWLIndividual[] ind = individuals.toArray(new OWLIndividual[]{});
        //String age = reasoner.getRelatedValue(ind[2], hasProducer).getLiteral();
        OWLIndividual producer = reasoner.getRelatedIndividual(ind[2], hasProducer);

        assertEquals("Nora", producer.toString());
    }


    //SPARQL

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
        assertEquals(rs.toString(), "[{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#NoraHomeMadeStrawberryAndWildJam}, " +
                "{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#HervikStrawberryJam}, " +
                "{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#ICAEcologicalStrawberryJam}, " +
                "{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#NoraSqueezy}, " +
                "{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#NoraLightStrawberryJam}, " +
                "{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#NoraOriginal}, " +
                "{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#EuroshopperStrawberryJam}, " +
                "{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#NoraNoSugar}, " +
                "{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#NoraHomeMadeStrawberryJam}, " +
                "{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#HervikEcoStrawberryJam}]");
    }


    @Test
    public void findAllHervikProducedJams() {
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

        String query = "SELECT  ?x " +
                "WHERE { ?x rdf:type OntologyPersonalProfile:Jam . " +
                "?x OntologyPersonalProfile:hasProducer OntologyPersonalProfile:Hervik . }";

        Query queryQuery = QueryFactory.create(SparqlQueryFactory.prefix + query);

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

        assertEquals("[{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#HervikStrawberryJam}, " +
                "{var(x)=http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#HervikEcoStrawberryJam}]",
                rs.toString());

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
        for (Set<OWLClass> classesOfHSJSet : classesOfHSJ) {
            for (OWLClass owlClass : classesOfHSJSet) {
                result.add(owlClass);
            }
        }
        assertEquals("Man", result.get(0).toString());
        assertEquals("EcoConcernedPerson", result.get(1).toString());
        assertEquals("MediumPriceConcernedPerson", result.get(2).toString());
        assertEquals("Adult", result.get(3).toString());
        assertEquals("FairTradeConcernedPerson", result.get(4).toString());
    }


    @Test
    public void findJamsAndTheirProducers() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLClass strawberryJam = manager.getOWLDataFactory().getOWLClass(URI.create(myURI + "#StrawberryJam"));
        Set<OWLIndividual> individuals = reasoner.getIndividuals(strawberryJam, false);
        OWLObjectProperty hasProducer = factory.getOWLObjectProperty(URI.create(myURI + "#hasProducer"));

        List<OWLIndividual> resultset = new ArrayList<OWLIndividual>();

        for (OWLIndividual ind : individuals) {

            resultset.add(reasoner.getRelatedIndividual(ind, hasProducer));
            //for (Set<OWLClass> owlClasses : type) {
            //Set<OWLDescription> ranges = hasProducer.getRanges(ontology);
            //System.out.println(ind + "  " + "HasProducer: " + hasProducer.getRanges(ontology));
        }

        assertEquals(10, resultset.size());
        assertEquals("ICA", resultset.get(0).toString());
        //Ica - IcaStrawberryJam
        //Nora - NoraSqueezy
    }

    @Test
    public void findProducerHervikSJ() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLObjectProperty hasProducer = factory.getOWLObjectProperty(URI.create(myURI + "#hasProducer"));
        OWLIndividual hervikSJ = factory.getOWLIndividual(URI.create(myURI + "#HervikStrawberryJam"));
        OWLIndividual producer = reasoner.getRelatedIndividual(hervikSJ, hasProducer);

        assertEquals("Hervik", producer.toString());
    }


    @Test
    public void findAllDataPropertiesForIndividualBill() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        // create property and resources to query the reasoner
        OWLIndividual bill = factory.getOWLIndividual(URI.create(myURI + "#Bill"));
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
        OWLClass jam = manager.getOWLDataFactory().getOWLClass(URI.create(myURI + "#Jam"));
        OWLObjectProperty hasIngredient = manager.getOWLDataFactory().getOWLObjectProperty(URI.create(myURI + "#hasIngredient"));

        Set<OWLIndividual> individuals = reasoner.getIndividuals(jam, false);
        List<String> results = new ArrayList<String>();

        for (OWLIndividual ind : individuals) {
            // get the info about this specific individual
            //OWLIndividual name = reasoner.getRelatedIndividual(ind, hasIngredient);
            OWLClass type = reasoner.getType(ind);
            OWLIndividual ingredient = reasoner.getRelatedIndividual(ind, hasIngredient);
            results.add(ind.toString() + " " + type.getURI().getFragment() + " " + ingredient);
        }
        assertEquals("ICAEcologicalStrawberryJam EcologicalStrawberryJam Strawberry", results.get(0));
        assertEquals("EuroshopperStrawberryJam RegularProducedFood Strawberry", results.get(1));
        assertEquals(10, results.size());
    }

    @Test
    public void findBillsEcoAffinity() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLIndividual bill = factory.getOWLIndividual(URI.create(myURI + "#Bill"));
        OWLObjectProperty hasEcoAffinity = factory.getOWLObjectProperty(URI.create(myURI + "#hasEcoAffinity"));

        OWLIndividual ecoAffinity = reasoner.getRelatedIndividual(bill, hasEcoAffinity);
        OWLClass typeOfEcoAffinity = reasoner.getType(ecoAffinity);
        assertEquals("HighEcoAffinity", typeOfEcoAffinity.toString());
    }

    @Test
    public void findBillsPriceSensitivity() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLIndividual bill = factory.getOWLIndividual(URI.create(myURI + "#Bill"));
        OWLObjectProperty hasPriceAffinity = factory.getOWLObjectProperty(URI.create(myURI + "#hasPriceSensitivity"));

        OWLIndividual priceSensitivity = reasoner.getRelatedIndividual(bill, hasPriceAffinity);
        OWLClass typeOfPriceSensitivity = reasoner.getType(priceSensitivity);
        assertEquals("MediumPriceSensitivity", typeOfPriceSensitivity.toString());
    }

    @Test
    public void findBillsFairTradeAffinity() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLIndividual bill = factory.getOWLIndividual(URI.create(myURI + "#Bill"));
        OWLObjectProperty hasFairTradeAffinity = factory.getOWLObjectProperty(URI.create(myURI + "#hasFairTradeAffinity"));

        OWLIndividual fairTradeAffinity = reasoner.getRelatedIndividual(bill, hasFairTradeAffinity);
        OWLClass typeOfFairTradeAffinity = reasoner.getType(fairTradeAffinity);
        assertEquals("HighFairTradeAffinity", typeOfFairTradeAffinity.toString());
    }

    @Test
    //TODO Skal kanskje gjøres på en annen måte
    public void computeRelevanceOfAProduct() {
        //m� gj�res for alle aktuelle kandidater
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLIndividual icaEco = factory.getOWLIndividual(URI.create(myURI + "#ICAEcologicalStrawberryJam"));

        OWLIndividual bill = factory.getOWLIndividual(URI.create(myURI + "#Bill"));

        int relevance;

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

    /*  ikke s� lett n�r jeg ikke vet hvilke atributter som er relevante
    de avhenger jo av det vi f�r vite i profilen
    @Test
    public void findMostRlevantJam() {
        //forslag fra http://lists.owldl.com/pipermail/pellet-users/2008-December/003218.html
        Reasoner reasoner = new Reasoner(manager);
        reasoner.setOntology(ontology);
        KnowledgeBase kb = reasoner.getKB();
        PelletReasoner jenaReasoner = new PelletReasoner();
        // Create a Pellet graph using the KB from OWLAPI
        PelletInfGraph graph = jenaReasoner.bind(kb);
        // Wrap the graph in a model
        InfModel model = ModelFactory.createInfModel(graph);

        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  " +
                "SELECT  ?x" +
                "WHERE { " +
                "?x rdf:type OntologyPersonalProfile:StrawberryJam . " +
                "?x OntologyPersonalProfile: . " +            //<Bill>
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
        //todo hvordan h�ndtere result sett fra sp�rring - syntax for sp�rringene - hvis man vet alt dette er det vel like greit � bruke vanlig reasoning?
    }
    */


    @Test
    public void findAManWithAge() {
        List<OWLIndividual> a = new SparqlQueryFactory().executeQuery("SELECT ?z ?y " +
                "WHERE { ?x rdf:type OntologyPersonalProfile:Man ." +
                "?x OntologyPersonalProfile:hasFirstName ?z . " +            //<Bill>
                "?x OntologyPersonalProfile:hasAge ?y  . " +
                "}");
        System.out.println("a = " + a);
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

        assertEquals(wantedRs, bl);
        //todo hvordan h�ndtere result sett fra sp�rring - syntax for sp�rringene - hvis man vet alt dette er det vel like greit � bruke vanlig reasoning?
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
        //todo hvordan h�ndtere result sett fra sp�rring - syntax for sp�rringene - hvis man vet alt dette er det vel like greit � bruke vanlig reasoning?
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
                "SELECT ?y ?x " +
                "WHERE { " +
                "?y rdf:type OntologyPersonalProfile:EcoConcernedPerson . " +
                //"?y OntologyPersonalProfile:hasFirstName \"Bill\" . " +            //<Bill>
                "?y OntologyPersonalProfile:hasMatchingEcoProducts ?x  . " +
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
        //todo hvordan h�ndtere result sett fra sp�rring - syntax for sp�rringene - hvis man vet alt dette er det vel like greit � bruke vanlig reasoning?
    }

    @Test
    public void testFindAllAffinities() {
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
                "SELECT ?y " +
                "WHERE { " +
                "?y rdf:type OntologyPersonalProfile:Modifiers . " +
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
        //todo hvordan h�ndtere result sett fra sp�rring - syntax for sp�rringene - hvis man vet alt dette er det vel like greit � bruke vanlig reasoning?
    }

//hvordan finne ut range til en relasjon? ->  m� vite instansen du h�rer til
//properties til en instans    - m� trikses med
//verdier til en property til en instans - ok

//finne en instans sin klasse        - dvs noe med en property som h�rer til en eller annen klasse

    /*
 @Test
    public void findAllObjectPropertiesForIndividual() {
        OWLDataFactory factory = manager.getOWLDataFactory();

        Reasoner reasoner = new Reasoner(manager);
        reasoner.setOntology(ontology);

        OWLIndividual hervikSJ = factory.getOWLIndividual(URI.create(myURI + "#HervikStrawberryJam"));
        OWLClass strawberryJam = factory.getOWLClass(URI.create(myURI + "#StrawberryJam"));
        Set<Set<OWLClass>> type = reasoner.getTypes(hervikSJ);

        Set<OWLObjectProperty> objectProperties = reasoner.getObjectProperties(); //alle
        //s� kan man evt koble properties til domains, og videre lage lister for hvilke som h�rer til hvilke domains, og videre hvilke ranges de har
        for (OWLObjectProperty objectProperty : objectProperties) {

        }

        for (OWLProperty<?, ?> property : objectProperties) {
            if (property instanceof OWLObjectProperty) {
                OWLObjectProperty owlObjectProperty = (OWLObjectProperty) property;
                //OWLProperty prop2 = property.asOWLObjectProperty();
                System.out.println("owlObjectProperty = " + owlObjectProperty);
                //OWLObjectProperty prop22 = factory.getOWLObjectProperty(URI.create(myURI + "#"+ prop2));
                OWLIndividual rangeOfProp2 = reasoner.getRelatedIndividual(hervikSJ, owlObjectProperty);
                System.out.println("rangeOfProp2 = " + rangeOfProp2);
            } else if (property instanceof OWLDataType) {
                OWLDataType owlDatatype = (OWLDataType) property;
                //OWLProperty prop1 = property.asOWLDataProperty();
                System.out.println("owlDatatype = " + owlDatatype);
                // OWLDataType rangeOfOwlDatatype = reasoner.getRelatedValue(hervikSJ, owlDatatype);
                //OWLIndividual rangeOfProp1 = reasoner.getRelatedIndividual(HervikSJ, );
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





