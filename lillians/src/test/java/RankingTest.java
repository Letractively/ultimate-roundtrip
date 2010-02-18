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
    @Test
    public void testFindPreferenceProductRelation() {
        OWLClass modifiers = findClassByName("#Modifiers");
        Set<OWLIndividual> individuals = reasoner.getIndividuals(modifiers, false);
        assertEquals(4, individuals.size());

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
        assertEquals(findIndividual("#Bill"), reasoner.getRelatedIndividual(billsEcoAffinity, belongsTo));


        //Hva betyr det at et produkt er økologisk?
        //Jo, det betyr at man skal se på om det er WOP er økologisk eller regular

    }

    @Test
    public void testRating() {
        //Get Product list - alle possible alternatives
        OWLClass strawberryJam = findClassByName("#StrawberryJam");
        Set<OWLIndividual> jams = reasoner.getIndividuals(strawberryJam, false);

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

        //Calculate Bills affinities
        List<OWLIndividual> allAffinities = getAllAffinities();

        //WOP
        Map<OWLIndividual,  SimpleSortable> jamMap = new HashMap<OWLIndividual,SimpleSortable>();

        for (OWLIndividual jam : jams) {
            jamMap.put(jam, new SimpleSortable(jam));
        }

        for (OWLIndividual jam : jams) {
            //WayOfProduction
            System.out.println("jam = " + jam);

            //todo de 6 linjene skal bort
            OWLIndividual relatedWayOfProductionIndividual = reasoner.getRelatedIndividual(jam, findObjectProperty("#hasWayOfProduction"));
            System.out.println("relatedWayOfProductionIndividual = " + relatedWayOfProductionIndividual);

            OWLDataProperty hasWOPValue = RankingTest.findDataType("#hasWOPValue");
            OWLConstant jamWOPValue = reasoner.getRelatedValue(relatedWayOfProductionIndividual, hasWOPValue);

            if (jamWOPValue != null)
                System.out.println("WOPValueAndPerhapsSomethingElse = " + jamWOPValue.getLiteral());


            for (OWLIndividual affinity : allAffinities) {
                //Sjekke om Affinitien har noe å gjøre med Way of production og sånt


                boolean isRelated =  isAffinityRelatedToProductInformation(affinity, jam);
                if(isRelated){
                    System.out.println("affinity = " + affinity);
                    OWLDataProperty hasAffinityValue = RankingTest.findDataType("#hasAffinityValue");

                    OWLConstant affinityValue = reasoner.getRelatedValue(affinity, hasAffinityValue);
                    System.out.println("!!!!!!!!!!!affinityValue = " + affinityValue);

                    //todo trenger disse å hardkodes? finnes det en måte hvor man kan summere alle aktuelle delrelevanser hvor det er en match mellom preferanse og et produkts egenskap?
                    //todo delrelevans = preferanseverdi x tilfreddstillelse av egenskapet hos produktet
                    //todo relevans = summen av alle delrelevanser
                    jamMap.get(jam).addRelevance(affinityValue, jamWOPValue);
                }

                //OWLIndividual relatedWayOfProductionIndividual = reasoner.getRelatedIndividual(jam, findObjectProperty("#hasWayOfProduction"));
               // OWLClass wayOfProduction = reasoner.getType(relatedWayOfProductionIndividual);
                //ProductPrice

                //todo pris brukes ikke i sammenligningen
                OWLConstant priceOfJam = reasoner.getRelatedValue(jam, findDataType("#hasPricePerKilo"));
                int price = Integer.valueOf(priceOfJam.getLiteral());


                //jamMap.add(new EcoSortable(jam, billsEcoAffinity, wayOfProduction));
            }



        }


        //Weight scheme for Eco Affinity compared to product

        //Score product

        //Sort list based on score
        List<SimpleSortable> sortables = new ArrayList(jamMap.values());
        Collections.sort(sortables);
        int hmmmm = sortables.size();
        System.out.println("size jamMap = " + hmmmm);
        for (SimpleSortable ecoSortable : sortables) {
            System.out.println("ecoSortable = " + ecoSortable.jam);  //todo hvorfor ecoSortable?
            System.out.println("ecoSortable.relevance = " + ecoSortable.relevance());
        }
    }

    @Test
    public void testAffinityRelatedToProductInformation() {

        OWLIndividual ica = findIndividual("#HervikEcoStrawberryJam");
        OWLIndividual eco = findIndividual("#BillsEcoAffinity");
        assertTrue(isAffinityRelatedToProductInformation(eco, ica));
    }

    private boolean isAffinityRelatedToProductInformation(OWLIndividual affinity, OWLIndividual product) {
        OWLClass affinityClass = reasoner.getType(affinity);
        Set<Set<OWLClass>> superclass = reasoner.getSuperClasses(affinityClass);
        Set<OWLClass> superclassFlat = OWLReasonerAdapter.flattenSetOfSets(superclass);
        for (OWLClass owlClass : superclassFlat) {
            OWLObjectProperty hasQualityMark = findObjectProperty("#hasQualityMark");
            OWLIndividual eco = findIndividual("#TESTEcological");

            boolean productIsEco = reasoner.hasObjectPropertyRelationship(product, hasQualityMark, eco);
            boolean hasEcoAffinity = owlClass.equals(findClassByName("#EcoAffinity"));
            System.out.println("productIsEco = " + productIsEco);
            System.out.println("hasEcoAffinity = " + hasEcoAffinity);
            if (hasEcoAffinity && productIsEco)
                return true;
        }
        return false;
    }

    @Test
        public void testGetAllEcoProds(){
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
                "?y rdf:type OntologyPersonalProfile:Ecological . "+
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

    public SimpleSortable(OWLIndividual jam){
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
            if(relevance() < other.relevance())
                return 1;
            else if(relevance() > other.relevance())
                return -1;
        }
        return 0;
    }

    public void addRelevance(OWLConstant affinityValue, OWLConstant jamWOPValue) {
        this.affinityValues.add(affinityValue);
        this.jamWOPValues.add(jamWOPValue);
    }
}

