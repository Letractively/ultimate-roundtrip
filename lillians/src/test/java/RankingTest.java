import org.junit.BeforeClass;
import org.junit.Test;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.model.*;
import java.util.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RankingTest {

    static SparqlQueryFactory factory;
    static Reasoner reasoner;

    @BeforeClass
    public static void setUp() {
        factory = new SparqlQueryFactory();
        reasoner = factory.reasoner;
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
        OWLClass strawberryJam = factory.findClassByName("#StrawberryJam");
        Set<OWLIndividual> jams = reasoner.getIndividuals(strawberryJam, false);

        //test
        assertEquals("ICAEcologicalStrawberryJam", jams.toArray(new OWLIndividual[]{})[0].toString());
        assertEquals(10, jams.size());


        OWLIndividual bill = factory.findIndividual("#Bill");
        //Profile with Bills Preferences
        OWLObjectProperty hasEcoAffinity = factory.findObjectProperty("#hasEcoAffinity");
        OWLIndividual ecoAffinity = reasoner.getRelatedIndividual(bill, hasEcoAffinity);
        System.out.println("ecoAffinity = " + ecoAffinity);
        //Bill has Eco affinity
        OWLClass billsEcoAffinity = reasoner.getType(ecoAffinity);
        assertEquals("HighEcoAffinity", billsEcoAffinity.toString());

        OWLObjectProperty hasPriceSensitivity = factory.findObjectProperty("#hasPriceSensitivity");
        OWLIndividual priceSensitivity = reasoner.getRelatedIndividual(bill, hasPriceSensitivity);
        OWLClass billsPriceSensitivity = reasoner.getType(priceSensitivity);
        assertEquals("MediumPriceSensitivity", billsPriceSensitivity.toString());


        //Calculate Bills affinities - hentes ut via spørring
        List<OWLIndividual> allAffinities = factory.executeQuery(getAllAffinities);
        Map<OWLIndividual, SimpleSortable> jamMap = createJamMap(jams);

        addBoosters(allAffinities, jamMap);

        //Sort list based on score
        List<SimpleSortable> sortables = new ArrayList<SimpleSortable>(jamMap.values());
        Collections.sort(sortables);
        assertEquals(factory.findIndividual("#ICAEcologicalStrawberryJam"), sortables.get(0).jam);
        assertEquals(6, sortables.get(0).relevance());
        assertEquals(factory.findIndividual("#HervikEcoStrawberryJam"), sortables.get(1).jam);
        assertEquals(6, sortables.get(1).relevance());
        assertEquals(factory.findIndividual("#NoraOriginal"), sortables.get(2).jam);
        assertEquals(0, sortables.get(2).relevance());
        assertEquals(factory.findIndividual("#EuroshopperStrawberryJam"), sortables.get(3).jam);
        assertEquals(factory.findIndividual("#HervikStrawberryJam"), sortables.get(4).jam);
        assertEquals(factory.findIndividual("#NoraSqueezy"), sortables.get(5).jam);
        assertEquals(factory.findIndividual("#NoraLightStrawberryJam"), sortables.get(6).jam);
        assertEquals(factory.findIndividual("#NoraNoSugar"), sortables.get(7).jam);
        assertEquals(factory.findIndividual("#NoraHomeMadeStrawberryAndWildJam"), sortables.get(8).jam);
        assertEquals(factory.findIndividual("#NoraHomeMadeStrawberryJam"), sortables.get(9).jam);
    }

    @Test
    public void testRankedProducts() {
        //assertEquals(2, executeQuery(getAllEcoJams).size());
        assertEquals(2, factory.executeQuery(getProducts("Jam", "hasWayOfProduction", "Ecological")).size());
        assertEquals(10, factory.executeQuery(getProducts("Jam", "", "")).size());
        List<OWLIndividual> results = factory.executeQuery(getSatisfiesHighEcoAffinity);
        assertEquals(2, results.size());
        assertEquals("ICAEcologicalStrawberryJam", results.get(0).toString());
        assertEquals("HervikEcoStrawberryJam", results.get(1).toString());
    }

    private void addBoosters(List<OWLIndividual> allAffinities, Map<OWLIndividual, SimpleSortable> jamMap) {
        for (OWLIndividual jam : jamMap.keySet()) {
            OWLIndividual relatedWayOfProductionIndividual = reasoner.getRelatedIndividual(jam, factory.findObjectProperty("#hasWayOfProduction"));

            OWLDataProperty hasWOPValue = factory.findDataType("#hasWOPValue");
            OWLConstant jamWOPValue = reasoner.getRelatedValue(relatedWayOfProductionIndividual, hasWOPValue);

            for (OWLIndividual affinity : allAffinities) {
                //Sjekke om Affinitien har noe å gjøre med Way of production og sånt
                if (isAffinityRelatedToProductInformation(affinity, jam)) {
                    System.out.println("affinity = " + affinity);
                    OWLDataProperty hasAffinityValue = factory.findDataType("#hasAffinityValue");

                    OWLConstant affinityValue = reasoner.getRelatedValue(affinity, hasAffinityValue);
                    System.out.println("!!!!!!!!!!!affinityValue = " + affinityValue);

                    //todo trenger disse å hardkodes? finnes det en måte hvor man kan summere alle aktuelle delrelevanser hvor det er en match mellom preferanse og et produkts egenskap?
                    //todo delrelevans = preferanseverdi x tilfreddstillelse av egenskapet hos produktet
                    //todo relevans = summen av alle delrelevanser
                    jamMap.get(jam).addRelevance(affinityValue, jamWOPValue);
                }
            }
        }
    }

    @Test
    public void testBoosterRelevance() {
        List<OWLIndividual> boosters = factory.executeQuery(getAllHighBoosters("Bill"));

        assertEquals(2, boosters.size());
        assertEquals("HervikEcoStrawberryJam", boosters.get(1).toString());
        assertEquals("ICAEcologicalStrawberryJam", boosters.get(0).toString());


        //Get Product list - alle possible alternatives
        OWLClass strawberryJam = factory.findClassByName("#StrawberryJam");


        //List<OWLIndividual> allAffinities = getAllAffinities();
        Map<OWLIndividual, Integer> jamMap = new HashMap<OWLIndividual, Integer>();
        for (OWLIndividual jam : reasoner.getIndividuals(strawberryJam, false)) {
            jamMap.put(jam, 0);
        }
        //Add boosters
        for (OWLIndividual booster : boosters) {
            jamMap.put(booster, jamMap.get(booster) + 1);
        }
        for (OWLIndividual jam : jamMap.keySet()) {
            System.out.println(jam.toString() + " " + jamMap.get(jam));
        }


    }

    private Map<OWLIndividual, SimpleSortable> createJamMap(Set<OWLIndividual> jams) {
        Map<OWLIndividual, SimpleSortable> jamMap = new HashMap<OWLIndividual, SimpleSortable>();

        for (OWLIndividual jam : jams) {
            jamMap.put(jam, new SimpleSortable(jam));
        }
        return jamMap;
    }


    //hva skal denne brukes til?
    @Test
    public void testAffinityRelatedToProductInformation() {

        OWLIndividual hervikSJ = factory.findIndividual("#HervikEcoStrawberryJam");
        OWLIndividual eco = factory.findIndividual("#BillsEcoAffinity");
        assertTrue(isAffinityRelatedToProductInformation(eco, hervikSJ));
    }

    private boolean isAffinityRelatedToProductInformation(OWLIndividual affinity, OWLIndividual product) {
        OWLClass affinityClass = reasoner.getType(affinity);
        Set<Set<OWLClass>> superclass = reasoner.getSuperClasses(affinityClass);
        Set<OWLClass> superclassFlat = OWLReasonerAdapter.flattenSetOfSets(superclass);
        for (OWLClass owlClass : superclassFlat) {
            OWLObjectProperty hasQualityMark = factory.findObjectProperty("#hasQualityMark");
            OWLIndividual ecoQM = factory.findIndividual("#TESTEcological");

            boolean productIsEco = reasoner.hasObjectPropertyRelationship(product, hasQualityMark, ecoQM);
            boolean instanceOfClassEcoAffinity = owlClass.equals(factory.findClassByName("#EcoAffinity"));
            if (instanceOfClassEcoAffinity && productIsEco)
                return true;
        }
        return false;
    }

    @Test
    public void testGetAllEcoProds() {
        assertEquals(2, factory.executeQuery(getAllEcoProducts).size());
        assertEquals("HervikEcoStrawberryJam", factory.executeQuery(getAllEcoProducts).get(0).toString());
    }

    String getAllHighBoosters(String person) {
        return "SELECT ?x " +
                "WHERE { " +
                "?x rdf:type OntologyPersonalProfile:Food . " +
                "OntologyPersonalProfile:" + person + " OntologyPersonalProfile:boostersHigh ?x." +
                "}";
    }

    //finner alle affinities til en person  - bill
    String getAllAffinities = "SELECT ?x WHERE { " +
            "?x rdf:type OntologyPersonalProfile:Modifiers . " +
            "?x OntologyPersonalProfile:belongsTo OntologyPersonalProfile:Bill." +
            "}";

    String getAllEcoProducts = "SELECT ?x WHERE { " +
            "?x rdf:type OntologyPersonalProfile:Food." +
            "?y rdf:type OntologyPersonalProfile:Ecological . " +
            "?x OntologyPersonalProfile:hasWayOfProduction ?y." +
            "}";

    String getAllEcoJams = "SELECT ?x WHERE { " +
            "?x rdf:type OntologyPersonalProfile:Jam." +
            "?y rdf:type OntologyPersonalProfile:Ecological . " +
            "?x OntologyPersonalProfile:hasWayOfProduction ?y." +
            "}";
    String getSatisfiesHighEcoAffinity = "SELECT ?x WHERE { " +
            "?x rdf:type OntologyPersonalProfile:Jam. " +
            "OntologyPersonalProfile:Bill OntologyPersonalProfile:satisfiesHighEcoAffinity ?x." +
            "}";

    String getProducts(String xType, String relation, String yType) {
        String query = "SELECT ?x WHERE { ";
        if (!xType.isEmpty())
            query += "?x rdf:type OntologyPersonalProfile:" + xType + ".";
        if (!yType.isEmpty())
            query += "?y rdf:type OntologyPersonalProfile:" + yType + " . ";
        if (!relation.isEmpty())
            query += "?x OntologyPersonalProfile:" + relation + " ?y.";
        query += "}";
        return query;
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

