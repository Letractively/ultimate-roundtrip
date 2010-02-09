import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.*;

import java.net.URI;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class StigsTest {

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

        OWLClass strawberryJam = findClassByName("#StrawberryJam");
        Set<OWLIndividual> jams = reasoner.getIndividuals(strawberryJam, false);

        OWLObjectProperty hasEcoProductRelation = findObjectProperty("#hasEcoProductRelation");
        System.out.println("hasEcoProductRelation = " + hasEcoProductRelation);

        Set<Set<OWLDescription>> domainsOfRelation = reasoner.getDomains(hasEcoProductRelation);
        OWLDescription ecoAffinity = (OWLDescription) ((Set<OWLDescription>)domainsOfRelation.toArray()[1]).toArray()[0];

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
        //Get Product list
        OWLClass strawberryJam = findClassByName("#StrawberryJam");
        Set<OWLIndividual> jams = reasoner.getIndividuals(strawberryJam, false);

        assertEquals("ICAEcologicalStrawberryJam", jams.toArray(new OWLIndividual[]{})[0].toString());
        assertEquals(10, jams.size());


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

        //Calculate Bills affinities
        List<OWLIndividual> allAffinities = getAllAffinities();
        for (OWLIndividual affinity : allAffinities) {
            OWLDataProperty hasAffinityValue = StigsTest.findDataType("#hasAffinityValue");
            OWLConstant ecoAffinityAndPerhapsSomethingElse = reasoner.getRelatedValue(affinity, hasAffinityValue);
            if (ecoAffinityAndPerhapsSomethingElse != null)
                System.out.println("ecoAffinityAndPerhapsSomethingElse = " + ecoAffinityAndPerhapsSomethingElse.getLiteral());
        }

        //WOP
        List<EcoSortable> sortableJam = new ArrayList<EcoSortable>();
        for (OWLIndividual jam : jams) {
            //WayOfProduction
            OWLIndividual relatedWayOfProductionIndividual = reasoner.getRelatedIndividual(jam, findObjectProperty("#hasWayOfProduction"));

            OWLDataProperty hasWOPValue = StigsTest.findDataType("#hasWOPValue");
            OWLConstant jamWOPValue = reasoner.getRelatedValue(relatedWayOfProductionIndividual, hasWOPValue);
            if (jamWOPValue != null)
                System.out.println("ecoAffinityAndPerhapsSomethingElse = " + jamWOPValue.getLiteral());


            for (OWLIndividual affinity : allAffinities) {
                //eksempel eco
                //affinity = HighEcoAffinity
                //Finn produktets WayOfProduction
                //basert på at ecoAffinity har en relasjon til wayOfProduction
                //Gang hasWOPValue med wayOfProduction's value
                //Legg alle verdiene du får til produktets "relevance" og så kan de sorteres basert på den.



                //OWLIndividual relatedWayOfProductionIndividual = reasoner.getRelatedIndividual(jam, findObjectProperty("#hasWayOfProduction"));
                OWLClass wayOfProduction = reasoner.getType(relatedWayOfProductionIndividual);
                //ProductPrice
                System.out.println("jam = " + jam);
                OWLConstant priceOfJam = reasoner.getRelatedValue(jam, findDataType("#hasPricePerKilo"));
                int price = Integer.valueOf(priceOfJam.getLiteral());


                sortableJam.add(new EcoSortable(jam, billsEcoAffinity, wayOfProduction));
            }




        }


        //Weight scheme for Eco Affinity compared to product

        //Score product

        //Sort list based on score
        Collections.sort(sortableJam);
        for (EcoSortable ecoSortable : sortableJam) {
            System.out.println("ecoSortable = " + ecoSortable.owlIndividual);
            System.out.println("ecoSortable.relevance = " + ecoSortable.relevance);
        }
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

        //finner alle affinities til en person
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


class EcoSortable implements Comparable {
    final OWLIndividual owlIndividual;
    int relevance;

    public EcoSortable(OWLIndividual owlIndividual, OWLClass billsEcoAffinity, OWLClass wop) {
        this.owlIndividual = owlIndividual;
        int thisProductHasEcoRelevance = calculateEcoRelevance(wop);
        int consumersEcoAffinity = whatIsConsumersEcoAffinity(billsEcoAffinity);
        relevance = consumersEcoAffinity * thisProductHasEcoRelevance;

    }

    private int whatIsConsumersEcoAffinity(OWLClass ecoAffinity) {

        
        if (ecoAffinity == StigsTest.findClassByName("#HighEcoAffinity")) {
            return 2;
        } else if (ecoAffinity == StigsTest.findClassByName("#MediumEcoAffinity")) {
            return 1;
        } else {
            System.out.println("IKKE " + ecoAffinity);
            return 0;
        }
    }

    private int calculateEcoRelevance(OWLClass wop) {
        if (wop == StigsTest.findClassByName("#Ecological")) {
            return 1;
        } else if (wop == StigsTest.findClassByName("#Regular")) {
            return -1;
        } else {
            return 0;
        }
    }

    public int compareTo(Object o) {
        if (o instanceof EcoSortable) {
            EcoSortable other = (EcoSortable) o;
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

