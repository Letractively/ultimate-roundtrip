import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.model.*;

import java.net.URI;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RankingTest {

    static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    static OWLDataFactory factory = manager.getOWLDataFactory();

    OWLOntology ontology;
    public Reasoner reasoner;
    public static final String myURI = "http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl";

    @Before
    public void setUp() throws OWLOntologyCreationException {
        ontology = manager.loadOntologyFromPhysicalURI(URI.create("file:./src/main/resources/PersonalProfile.owl"));
        reasoner = new Reasoner(manager);
        reasoner.loadOntology(ontology);
    }

    //todo bytt ut med spørring? getallaffinities

    /*
    byttet ut med spørring i stedet - denne går ikke på affinities for en person, men for alle med affinities
    @Test
    public void testFindPreferenceProductRelation() {
        OWLClass modifiers = findClassByName("#Modifiers");
        Set<OWLIndividual> individuals = reasoner.getIndividuals(modifiers, false);
        assertEquals(8, individuals.size());

        for (OWLIndividual individual : individuals) {
            System.out.println("individual = " + individual);
            OWLClass classesOfInd = reasoner.getType(individual);
            System.out.println("classesOfInd = " + classesOfInd);
        }

        //OWLClass strawberryJam = findClassByName("#StrawberryJam");
        //Set<OWLIndividual> jams = reasoner.getIndividuals(strawberryJam, false);

        OWLObjectProperty hasEcoProductRelation = findObjectProperty("#hasEcoProductRelation");
        System.out.println("hasEcoProductRelation = " + hasEcoProductRelation);

        Set<Set<OWLDescription>> domainsOfRelation = reasoner.getDomains(hasEcoProductRelation);
        OWLDescription ecoAffinity = (OWLDescription) ((Set<OWLDescription>)domainsOfRelation.toArray()[1]).toArray()[0];

        //todo men brukes denne til noe?
        Set<OWLDescription> rangeOfRelation = reasoner.getRanges(hasEcoProductRelation);
        OWLDescription ecoFood = rangeOfRelation.toArray(new OWLDescription[]{})[0];
        System.out.println("ecoFood = " + ecoFood);
        System.out.println("ecoAffinity = " + ecoAffinity);

        //Hente ut alle ecomenn (Eller bare den ene mannen vi har
        //Hvor ekomenn er de? Fra en skala fra 1-masse, hvor mye bryr de seg?
        //Få tak i hvor mye bill bryr seg om eco

        OWLClass ecoAffinityKlasse = ecoAffinity.asOWLClass();
        OWLIndividual billsEcoAffinity = (OWLIndividual) reasoner.getIndividuals(ecoAffinityKlasse, false).toArray()[0];
        System.out.println("billsEcoAffinity = " + billsEcoAffinity);
        OWLObjectProperty belongsTo = findObjectProperty("#belongsTo");
        assertEquals(findIndividual("#Student"), reasoner.getRelatedIndividual(billsEcoAffinity, belongsTo));


        //Hva betyr det at et produkt er økologisk?
        //Jo, det betyr at man skal se på om det er WOP er økologisk eller regular

    }
    **/

    @Test
    public void testRating() {
        //Get Product list - alle possible alternatives
        OWLClass strawberryJam = findClassByName("#StrawberryJam");
        Set<OWLIndividual> jams = reasoner.getIndividuals(strawberryJam, false);

        //test
        assertEquals("ICAEcologicalStrawberryJam", jams.toArray(new OWLIndividual[]{})[0].toString());
        assertEquals(10, jams.size());

        /*
                //Profile with Bills Preferences
                OWLIndividual bill = findIndividual("#Bill");
                OWLObjectProperty hasEcoAffinity = findObjectProperty("#hasEcoAffinity");
                OWLIndividual ecoAffinity = reasoner.getRelatedIndividual(bill, hasEcoAffinity);
                System.out.println("ecoAffinity = " + ecoAffinity);
                //Bill has Eco affinity
                OWLClass billsEcoAffinity = reasoner.getType(ecoAffinity);
                assertEquals("HighEcoAffinity", billsEcoAffinity.toString());

                OWLObjectProperty hasPriceSensitivity = findObjectProperty("#hasPriceSensitivity");
                OWLIndividual priceSensitivity = reasoner.getRelatedIndividual(bill, hasPriceSensitivity);
                OWLClass billsPriceSensitivity = reasoner.getType(priceSensitivity);
                assertEquals("MediumPriceSensitivity", billsPriceSensitivity.toString());
        **/

        //Calculate Bills affinities - hentes ut via spørring
        List<OWLIndividual> allAffinities = getAllAffinities();

        //WOP
        Map<OWLIndividual, SimpleSortable> jamMap = new HashMap<OWLIndividual, SimpleSortable>();

        for (OWLIndividual jam : jams) {
            jamMap.put(jam, new SimpleSortable(jam));
        }

        for (OWLIndividual jam : jams) {
            OWLIndividual relatedWayOfProductionIndividual = reasoner.getRelatedIndividual(jam, findObjectProperty("#hasWayOfProduction"));

            OWLDataProperty hasWOPValue = RankingTest.findDataType("#hasWOPValue");
            OWLConstant jamWOPValue = reasoner.getRelatedValue(relatedWayOfProductionIndividual, hasWOPValue);

            for (OWLIndividual affinity : allAffinities) {
                //Sjekke om Affinitien har noe å gjøre med Way of production og sånt
                if (isAffinityRelatedToProductInformation(affinity, jam)) {
                    System.out.println("affinity = " + affinity);
                    OWLDataProperty hasAffinityValue = RankingTest.findDataType("#hasAffinityValue");

                    OWLConstant affinityValue = reasoner.getRelatedValue(affinity, hasAffinityValue);
                    System.out.println("!!!!!!!!!!!affinityValue = " + affinityValue);

                    //todo trenger disse å hardkodes? finnes det en måte hvor man kan summere alle aktuelle delrelevanser hvor det er en match mellom preferanse og et produkts egenskap?
                    //todo delrelevans = preferanseverdi x tilfreddstillelse av egenskapet hos produktet
                    //todo relevans = summen av alle delrelevanser
                    jamMap.get(jam).addRelevance(affinityValue, jamWOPValue);
                }
            }
        }
        //Weight scheme for Eco Affinity compared to product

        //Score product

        //Sort list based on score
        List<SimpleSortable> sortables = new ArrayList<SimpleSortable>(jamMap.values());
        Collections.sort(sortables);
        assertEquals(findIndividual("#ICAEcologicalStrawberryJam"), sortables.get(0).jam);
        assertEquals(6, sortables.get(0).relevance());
        assertEquals(findIndividual("#HervikEcoStrawberryJam"), sortables.get(1).jam);
        assertEquals(6, sortables.get(1).relevance());
        assertEquals(findIndividual("#NoraOriginal"), sortables.get(2).jam);
        assertEquals(0, sortables.get(2).relevance());
        assertEquals(findIndividual("#EuroshopperStrawberryJam"), sortables.get(3).jam);
        assertEquals(findIndividual("#HervikStrawberryJam"), sortables.get(4).jam);
        assertEquals(findIndividual("#NoraSqueezy"), sortables.get(5).jam);
        assertEquals(findIndividual("#NoraLightStrawberryJam"), sortables.get(6).jam);
        assertEquals(findIndividual("#NoraNoSugar"), sortables.get(7).jam);
        assertEquals(findIndividual("#NoraHomeMadeStrawberryAndWildJam"), sortables.get(8).jam);
        assertEquals(findIndividual("#NoraHomeMadeStrawberryJam"), sortables.get(9).jam);

    }

    //hva skal denne brukes til?
    @Test
    public void testAffinityRelatedToProductInformation() {

        OWLIndividual hervikSJ = findIndividual("#HervikEcoStrawberryJam");
        OWLIndividual eco = findIndividual("#BillsEcoAffinity");
        assertTrue(isAffinityRelatedToProductInformation(eco, hervikSJ));
    }

    private boolean isAffinityRelatedToProductInformation(OWLIndividual affinity, OWLIndividual product) {
        OWLClass affinityClass = reasoner.getType(affinity);
        Set<Set<OWLClass>> superclass = reasoner.getSuperClasses(affinityClass);
        Set<OWLClass> superclassFlat = OWLReasonerAdapter.flattenSetOfSets(superclass);
        for (OWLClass owlClass : superclassFlat) {
            OWLObjectProperty hasQualityMark = findObjectProperty("#hasQualityMark");
            OWLIndividual ecoQM = findIndividual("#TESTEcological");

            boolean productIsEco = reasoner.hasObjectPropertyRelationship(product, hasQualityMark, ecoQM);
            boolean instanceOfClassEcoAffinity = owlClass.equals(findClassByName("#EcoAffinity"));
            if (instanceOfClassEcoAffinity && productIsEco)
                return true;
        }
        return false;
    }

    @Test
    public void testGetAllEcoProds() {
        assertEquals(2, getAllEcoProducts().size());
        assertEquals("HervikEcoStrawberryJam", getAllEcoProducts().get(0).toString());
    }


    private List<OWLIndividual> getAllEcoProducts() {
        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  " +
                "SELECT ?x " +
                "WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Food." +
                "?y rdf:type OntologyPersonalProfile:Ecological . " +
                "?x OntologyPersonalProfile:hasWayOfProduction ?y." +
                "}";

        SPARQLTests sparqlTest = new SPARQLTests();
        try {
            sparqlTest.setUp();
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException("Wooooops!");
        }
        List<String> ecosAsStrings = sparqlTest.jenaQuery(query, "x");
        List<OWLIndividual> individuals = new ArrayList<OWLIndividual>();
        for (String ecoAsString : ecosAsStrings) {
            individuals.add(factory.getOWLIndividual(URI.create(ecoAsString)));
        }
        return individuals;
    }

    private List<OWLIndividual> getAllAffinities() {
        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX OntologyPersonalProfile: <http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl#>" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>  " +
                "SELECT ?x " +
                "WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Modifiers . " +
                "?x OntologyPersonalProfile:belongsTo OntologyPersonalProfile:Bill." +
                "}";

        //finner alle affinities til en person  - bill
        SPARQLTests sparqlTest = new SPARQLTests();
        try {
            sparqlTest.setUp();
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException("Wooooops!");
        }
        List<String> affinitiesAsStrings = sparqlTest.jenaQuery(query, "x");
        List<OWLIndividual> individuals = new ArrayList<OWLIndividual>();
        for (String affinityAsString : affinitiesAsStrings) {
            individuals.add(factory.getOWLIndividual(URI.create(affinityAsString)));
        }
        return individuals;
    }

    private static OWLObjectProperty findObjectProperty(String type) {
        return factory.getOWLObjectProperty(URI.create(myURI + type));
    }

    private static OWLIndividual findIndividual(String name) {
        return factory.getOWLIndividual(URI.create(myURI + name));
    }

    public static OWLClass findClassByName(String className) {
        return manager.getOWLDataFactory().getOWLClass(URI.create(myURI + className));
    }

    public static OWLDataProperty findDataType(String name) {
        return manager.getOWLDataFactory().getOWLDataProperty(URI.create(myURI + name));
    }
}

class SimpleSortable implements Comparable {
    public final OWLIndividual jam;
    private List<OWLConstant> affinityValues = new ArrayList<OWLConstant>();
    private List<OWLConstant> jamWOPValues = new ArrayList<OWLConstant>();

    public SimpleSortable(OWLIndividual jam) {
        this.jam = jam;
    }

    private int calculateRelevance(OWLConstant affinity, OWLConstant wop) {
        int relevance = 0;
        if (affinity != null) {
            relevance = Integer.valueOf(affinity.getLiteral());
        }
        if (wop != null) {
            relevance = relevance * Integer.valueOf(wop.getLiteral());
        }
        return relevance;
    }

    public int relevance() {
        int relevance = 0;
        for (int i = 0; i < affinityValues.size(); i++) {
            relevance += calculateRelevance(affinityValues.get(i), jamWOPValues.get(i));
        }
        return relevance;
    }

    public int compareTo(Object o) {
        if (o instanceof SimpleSortable) {
            SimpleSortable other = (SimpleSortable) o;
            if (relevance() < other.relevance())
                return 1;
            else if (relevance() > other.relevance())
                return -1;
        }
        return 0;
    }

    public void addRelevance(OWLConstant affinityValue, OWLConstant jamWOPValue) {
        this.affinityValues.add(affinityValue);
        this.jamWOPValues.add(jamWOPValue);
    }
}

