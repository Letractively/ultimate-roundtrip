import org.junit.Test;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.inference.OWLReasonerFactory;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.DLExpressivityChecker;
import org.semanticweb.owl.util.OWLEntityRemover;
import org.semanticweb.owl.util.SimpleURIMapper;
import org.semanticweb.owl.vocab.OWLRestrictedDataRangeFacetVocabulary;
import org.semanticweb.owl.vocab.XSDVocabulary;
import org.semanticweb.reasonerfactory.pellet.PelletReasonerFactory;
import uk.ac.manchester.cs.owl.OWLClassImpl;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: hella
 * Date: Jan 10, 2010
 * Time: 11:33:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class PizzaOntologyTest {
    @Test
    public void whatIsFirstOWLClassInOntologyEX1() throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        URI physicalURI = URI.create("http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl");
        OWLOntology ontology = manager.loadOntologyFromPhysicalURI(physicalURI);

        OWLClass[] result = ontology.getReferencedClasses().toArray(new OWLClass[]{});
        assertEquals("dette var feil", 100, ontology.getReferencedClasses().size());
        assertEquals("UnclosedPizza", result[0].toString());
        assertEquals(false, result[0].isOWLThing());
    }

    @Test
    public void checkAddedClasesAndSubclassEX2() throws OWLOntologyCreationException, OWLOntologyChangeException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        URI ontologyURI = URI.create("http://www.co-ode.org/ontologies/testont.owl");
        URI physicalURI = URI.create("file:/tmp/MyOnt.owl");
        SimpleURIMapper mapper = new SimpleURIMapper(ontologyURI, physicalURI);
        manager.addURIMapper(mapper);
        OWLOntology ontology = manager.createOntology(ontologyURI);

        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLClass clsA = factory.getOWLClass(URI.create(ontologyURI + "#A"));
        OWLClass clsB = factory.getOWLClass(URI.create(ontologyURI + "#B"));
        OWLAxiom axiom = factory.getOWLSubClassAxiom(clsA, clsB);
        AddAxiom addAxiom = new AddAxiom(ontology, axiom);
        manager.applyChange(addAxiom);

        Set<OWLClass> referencedClasses = ontology.getReferencedClasses();
        OWLClass[] cls = referencedClasses.toArray(new OWLClass[]{});

        assertEquals(clsA, cls[0].asOWLClass());
        assertEquals(clsB, cls[1].asOWLClass());

        Set<OWLDescription> superClasses = clsA.getSuperClasses(ontology);

        OWLDescription[] desc = superClasses.toArray(new OWLDescription[]{});
        assertEquals(clsB, desc[0].asOWLClass());
    }

    @Test
    public void checkAddedRulesEX3() throws OWLOntologyCreationException, OWLOntologyChangeException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        // All ontologies have a URI, which is used to identify the ontology.  You should
        // think of the ontology URI as the "name" of the ontology.  This URI frequently
        // resembles a Web address (i.e. http://...), but it is important to realise that
        // the ontology URI might not necessarily be resolvable.  In other words, we
        // can't necessarily get a document from the URI corresponding to the ontology
        // URI, which represents the ontology.
        // In order to have a concrete representation of an ontology (e.g. an RDF/XML
        // file), we MAP the ontology URI to a PHYSICAL URI.  We do this using a URIMapper

        // Let's create an ontology and name it "http://www.co-ode.org/ontologies/testont.owl"
        // We need to set up a mapping which points to a concrete file where the ontology will
        // be stored. (It's good practice to do this even if we don't intend to save the ontology).
        URI ontologyURI = URI.create("http://www.co-ode.org/ontologies/testont.owl");
        // Create a physical URI which can be resolved to point to where our ontology will be saved.
        URI physicalURI = URI.create("file:/tmp/SWRLTest.owl");
        SimpleURIMapper mapper = new SimpleURIMapper(ontologyURI, physicalURI);
        manager.addURIMapper(mapper);

        OWLOntology ontology = manager.createOntology(ontologyURI);
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLClass clsA = factory.getOWLClass(URI.create(ontologyURI + "#A"));
        OWLClass clsB = factory.getOWLClass(URI.create(ontologyURI + "#B"));
        SWRLAtomIVariable var = factory.getSWRLAtomIVariable(URI.create(ontologyURI + "#x"));
        SWRLRule rule = factory.getSWRLRule(
                Collections.singleton(
                        factory.getSWRLClassAtom(clsA, var)
                ),
                Collections.singleton(
                        factory.getSWRLClassAtom(clsB, var)
                ));
        manager.applyChange(new AddAxiom(ontology, rule));

        OWLObjectProperty prop = factory.getOWLObjectProperty(URI.create(ontologyURI + "#propA"));
        OWLObjectProperty propB = factory.getOWLObjectProperty(URI.create(ontologyURI + "#propB"));
        SWRLObjectPropertyAtom propAtom = factory.getSWRLObjectPropertyAtom(prop, var, var);
        SWRLObjectPropertyAtom propAtom2 = factory.getSWRLObjectPropertyAtom(propB, var, var);
        Set<SWRLAtom> antecedent = new HashSet<SWRLAtom>();
        antecedent.add(propAtom);
        antecedent.add(propAtom2);
        SWRLRule rule2 = factory.getSWRLRule(antecedent,
                Collections.singleton(propAtom));

        manager.applyChange(new AddAxiom(ontology, rule2));

        assertEquals(true, ontology.containsAxiom(rule));
        assertEquals(true, ontology.containsAxiom(rule2));
    }


    @Test
    public void checkAddedRulesEX4() throws OWLOntologyCreationException, OWLOntologyChangeException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();

        String base = "http://www.semanticweb.org/ontologies/individualsexample";

        OWLOntology ont = man.createOntology(URI.create(base));

        OWLDataFactory dataFactory = man.getOWLDataFactory();

        OWLIndividual matthew = dataFactory.getOWLIndividual(URI.create(base + "#matthew"));
        OWLIndividual peter = dataFactory.getOWLIndividual(URI.create(base + "#peter"));
        OWLObjectProperty hasFather = dataFactory.getOWLObjectProperty(URI.create(base + "#hasFather"));
        OWLObjectPropertyAssertionAxiom assertion = dataFactory.getOWLObjectPropertyAssertionAxiom(matthew, hasFather, peter);
        AddAxiom addAxiomChange = new AddAxiom(ont, assertion);
        man.applyChange(addAxiomChange);

        //man.saveOntology(ont, URI.create("file:/tmp/example.owl"));
        //sjekk at relasjonen er lagt til
        assertEquals(true, ont.containsAxiom(assertion));
    }


    @Test
    public void deleteIndividualsEX5() throws OWLOntologyCreationException, OWLOntologyChangeException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology ont = man.loadOntologyFromPhysicalURI(URI.create("http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl"));

        OWLEntityRemover remover = new OWLEntityRemover(man, Collections.singleton(ont));
        //System.out.println("Number of individuals: " + ont.getReferencedIndividuals().size());
        // Loop through each individual that is referenced in the pizza ontology, and ask it
        // to accept a visit from the entity remover.  The remover will automatically accumulate
        // the changes which are necessary to remove the individual from the ontologies (the pizza
        // ontology) which it knows about
        assertEquals(5, ont.getReferencedIndividuals().size());
        for (OWLIndividual ind : ont.getReferencedIndividuals()) {
            ind.accept(remover);
        }
        // Now we get all of the changes from the entity remover, which should be applied to
        // remove all of the individuals that we have visited from the pizza ontology.  Notice that
        // "batch" deletes can essentially be performed - we simply visit all of the classes, properties
        // and individuals that we want to remove and then apply ALL of the changes afer using the
        // entity remover to collect them
        man.applyChanges(remover.getChanges());
        //System.out.println("Number of individuals: " + ont.getReferencedIndividuals().size());
        // At this point, if we wanted to reuse the entity remover, we would have to reset it
        remover.reset();

        assertEquals(0, ont.getReferencedIndividuals().size());
    }

    @Test
    public void restrictiosAsSuperclassesEX6() throws OWLOntologyCreationException, OWLOntologyChangeException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        String base = "http://org.semanticweb.restrictionexample";
        OWLOntology ont = man.createOntology(URI.create(base));

        // In this example we will add an axiom to state that all Heads have
        // parts that are noses (in fact, here we merely state that a Head has
        // at least one nose!).  We do this by createing an existential (some) restriction
        // to describe the class of things which have a part that is a nose (hasPart some Nose),
        // and then we use this restriction in a subclass axiom to state that Head is a subclass
        // of things that have parts that are Noses SubClassOf(Head, hasPart some Nose) -- in
        // other words, Heads have parts that are noses!

        // First we need to obtain references to our hasPart property and our Nose class
        OWLDataFactory factory = man.getOWLDataFactory();
        OWLObjectProperty hasPart = factory.getOWLObjectProperty(URI.create(base + "#hasPart"));
        OWLClass nose = factory.getOWLClass(URI.create(base + "#Nose"));
        // Now create a restriction to describe the class of individuals that have at least one
        // part that is a kind of nose
        OWLDescription hasPartSomeNose = factory.getOWLObjectSomeRestriction(hasPart, nose);

        // Obtain a reference to the Head class so that we can specify that Heads have noses
        OWLClass head = factory.getOWLClass(URI.create(base + "#Head"));
        // We now want to state that Head is a subclass of hasPart some Nose, to do this we
        // create a subclass axiom, with head as the subclass and "hasPart some Nose" as the
        // superclass (remember, restrictions are also classes - they describe classes of individuals
        // -- they are anonymous classes).
        OWLSubClassAxiom ax = factory.getOWLSubClassAxiom(head, hasPartSomeNose);

        // Add the axiom to our ontology
        AddAxiom addAx = new AddAxiom(ont, ax);
        man.applyChange(addAx);

        assertEquals(true, ont.containsAxiom(ax));

    }

    @Test
    public void checkAddedAxiomEX7() throws OWLOntologyCreationException, OWLOntologyChangeException {
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        String base = "http://org.semanticweb.datarangeexample";
        OWLOntology ont = man.createOntology(URI.create(base));

        OWLDataFactory factory = man.getOWLDataFactory();
        OWLDataProperty hasAge = factory.getOWLDataProperty(URI.create(base + "hasAge"));
        // For completeness, we will make hasAge functional by adding an axiom to state this
        OWLFunctionalDataPropertyAxiom funcAx = factory.getOWLFunctionalDataPropertyAxiom(hasAge);
        man.applyChange(new AddAxiom(ont, funcAx));

        // Now create the data range which correponds to int greater than 18.  To do this, we
        // get hold of the int datatype and then restrict it with a minInclusive facet restriction.
        OWLDataType intDataType = factory.getOWLDataType(XSDVocabulary.INT.getURI());
        // Create the value "18", which is an int.
        OWLTypedConstant eighteenConstant = factory.getOWLTypedConstant(18);
        // Now create our custom datarange, which is int greater than or equal to 18.  To do this,
        // we need the minInclusive facet
        OWLRestrictedDataRangeFacetVocabulary facet = OWLRestrictedDataRangeFacetVocabulary.MIN_INCLUSIVE;
        // Create the restricted data range by applying the facet restriction with a value of 18 to int
        OWLDataRange intGreaterThan18 = factory.getOWLDataRangeRestriction(intDataType,
                facet,
                eighteenConstant);
        // Now we can use this in our datatype restriction on hasAge
        OWLDescription thingsWithAgeGreaterOrEqualTo18 = factory.getOWLDataSomeRestriction(hasAge, intGreaterThan18);
        // Now we want to say all adults have an age that is greater or equal to 18 - i.e. Adult is a subclass of
        // hasAge some int[>= 18]
        // Obtain a reference to the Adult class
        OWLClass adult = factory.getOWLClass(URI.create(base + "#Adult"));
        // Now make adult a subclass of the things that have an age greater to or equal to 18
        OWLSubClassAxiom ax = factory.getOWLSubClassAxiom(adult, thingsWithAgeGreaterOrEqualTo18);
        // Add our axiom to the ontology
        man.applyChange(new AddAxiom(ont, ax));

        assertEquals(true, ont.containsAxiom(funcAx));
        assertEquals(true, ont.containsAxiom(ax));
    }

    @Test
    public void interactWithReasonerEX8() throws OWLOntologyCreationException, OWLOntologyChangeException, OWLReasonerException {
        final String PHYSICAL_URI = "http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl";
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        // Load a copy of the pizza ontology.  We'll load the ontology from the web.
        OWLOntology ont = manager.loadOntologyFromPhysicalURI(URI.create(PHYSICAL_URI));
        System.out.println("Loaded " + ont.getURI());
        // We need to create an instance of Reasoner.  Reasoner provides the basic
        // query functionality that we need, for example the ability obtain the subclasses
        // of a class etc.  To do this we use a reasoner factory.

        // Create a reasoner factory.  In this case, we will use pellet, but we could also
        // use FaCT++ using the FaCTPlusPlusReasonerFactory.
        // Pellet requires the Pellet libraries  (pellet.jar, aterm-java-x.x.jar) and the
        // XSD libraries that are bundled with pellet: xsdlib.jar and relaxngDatatype.jar
        // make sure these jars are on the classpath
        OWLReasonerFactory reasonerFactory = new PelletReasonerFactory();

        OWLReasoner reasoner = reasonerFactory.createReasoner(manager);

        // We now need to load some ontologies into the reasoner.  This is typically the
        // imports closure of an ontology that we're interested in.  In this case, we want
        // the imports closure of the pizza ontology.  Note that no assumptions are made
        // about the dependency of one ontology on another ontology.  This means that if
        // we loaded just the pizza ontology (using a singleton set) then any imported ontologies
        // would not automatically be loaded.
        // Obtain and load the imports closure of the pizza ontology
        Set<OWLOntology> importsClosure = manager.getImportsClosure(ont);
        reasoner.loadOntologies(importsClosure);
        reasoner.classify();

        // We can examine the expressivity of our ontology (some reasoners do not support
        // the full expressivity of OWL)
        DLExpressivityChecker checker = new DLExpressivityChecker(importsClosure);
        System.out.println("Expressivity: " + checker.getDescriptionLogicName());

        // We can determine if the pizza ontology is actually consistent.  (If an ontology is
        // inconsistent then owl:Thing is equivalent to owl:Nothing - i.e. there can't be any
        // models of the ontology)
        boolean consistent = reasoner.isConsistent(ont);
        //System.out.println("Consistent: " + consistent);
        //System.out.println("\n");

        assertEquals(true, consistent);

        // We can easily get a list of inconsistent classes.  (A class is inconsistent if it
        // can't possibly have any instances).  Note that the getInconsistentClasses method
        // is really just a convenience method for obtaining the classes that are equivalent
        // to owl:Nothing.
        Set<OWLClass> inconsistentClasses = reasoner.getInconsistentClasses();
        if (!inconsistentClasses.isEmpty()) {
            System.out.println("The following classes are inconsistent: ");
            for (OWLClass cls : inconsistentClasses) {
                System.out.println("    " + cls);
            }
        } else {
            System.out.println("There are no inconsistent classes");
        }
        System.out.println("\n");

        // Now we want to query the reasoner for all descendants of VegetarianPizza - i.e. all
        // vegetarian pizzas.
        // Get a reference to the vegetarian pizza class
        OWLClass vegPizza = manager.getOWLDataFactory().getOWLClass(URI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#VegetarianPizza"));

        // Now use the reasoner to obtain the subclasses of vegetarian pizza.  Note the reasoner
        // returns a set of sets.  Each set represents a subclass of vegetarian pizza where the
        // classes in the set represent equivalence classes.  For example, if we asked for the
        // subclasses of A and got back {{B, C}, {D}} then A would have essentially to subclasses.
        // One of these subclasses would be equivalent to the class D, and the other would be the class that
        // was equivalent to class B and class C.
        Set<Set<OWLClass>> subClsSets = reasoner.getDescendantClasses(vegPizza);
        // In this case, we don't particularly care about the equivalences, so we will flatten this
        // set of sets and print the result
        System.out.println("Vegetarian pizzas: ");
        Set<OWLClass> subClses = OWLReasonerAdapter.flattenSetOfSets(subClsSets);
        for (OWLClass cls : subClses) {
            System.out.println("    " + cls);
        }

        System.out.println("\n");

        // We can easily retrieve the instances of a class.  In this example we'll obtain the instances of
        // country.  First we need to get a reference to the country class
        OWLClass country = manager.getOWLDataFactory().getOWLClass(URI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#Country"));
        System.out.println("Instances of country: ");
        for (OWLIndividual ind : reasoner.getIndividuals(country, true)) {
            System.out.println("    " + ind);
        }

    }
}
