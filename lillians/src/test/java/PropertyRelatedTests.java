import no.ntnu.ontology.SparqlQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLProperty;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author Lillian Hella
 * @since Feb 8, 2010
 * Time: 8:51:59 AM
 */
public class PropertyRelatedTests {
    OWLOntologyManager manager;
    Reasoner reasoner;
    OWLDataFactory owlDataFactory;
    public static final String myURI = "http://www.idi.ntnu.no/~hella/ontology/2009/OntologyPersonalProfile.owl";

    @Before
    public void setUp() throws OWLOntologyCreationException, URISyntaxException {
        manager = OWLManager.createOWLOntologyManager();
        // We load an ontology
        // read the ontology
        OWLOntology ontology = manager.loadOntologyFromPhysicalURI(getClass().getResource("PersonalProfile.owl").toURI());
        reasoner = new Reasoner(manager);
        reasoner.loadOntology(ontology);
        owlDataFactory = manager.getOWLDataFactory();
    }

    @Test
     public void findObjectPropertyForIndividualsOfAClass() {
         OWLClass strawberryJam = manager.getOWLDataFactory().getOWLClass(URI.create(myURI + "#StrawberryJam"));
         Set<OWLIndividual> individuals = reasoner.getIndividuals(strawberryJam, false);
         OWLObjectProperty hasProducer = owlDataFactory.getOWLObjectProperty(URI.create(myURI + "#hasProducer"));

         OWLIndividual producer = null;

         for (OWLIndividual ind : individuals) {

             producer = reasoner.getRelatedIndividual(ind, hasProducer);
             //for (Set<OWLClass> owlClasses : type) {
             //Set<OWLDescription> ranges = hasProducer.getRanges(ontology);
             //System.out.println(ind + "  " + "HasProducer: " + hasProducer.getRanges(ontology));
         }

         assertEquals("Nora", producer.toString());
     }

     @Test
     public void findObjectPropertyForIndividualHervikSJ() {
         OWLObjectProperty hasProducer = owlDataFactory.getOWLObjectProperty(URI.create(myURI + "#hasProducer"));
         OWLIndividual hervikSJ = owlDataFactory.getOWLIndividual(URI.create(myURI + "#HervikStrawberryJam"));
         OWLIndividual producer = reasoner.getRelatedIndividual(hervikSJ, hasProducer);

         assertEquals("Hervik", producer.toString());
     }

     @Test
     public void findAllDataPropertiesForIndividual() {
         // create property and resources to query the reasoner
         OWLIndividual bill = owlDataFactory.getOWLIndividual(URI.create(myURI + "#Bill"));
         OWLClass strawberryJam = owlDataFactory.getOWLClass(URI.create(myURI + "#StrawberryJam"));
         Set<OWLDataProperty> dataProperties = reasoner.getDataProperties(); //ALLE

         List<String> list = new ArrayList<String>();

         for (OWLDataProperty owlDataProperty : dataProperties) {
             System.out.println("owlDataProperty = " + owlDataProperty);

             OWLConstant value1 = reasoner.getRelatedValue(bill, owlDataProperty);
             //String jala = reasoner.
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
     public void findAllObjectPropertiesForIndividual() {
         OWLIndividual hervikSJ = owlDataFactory.getOWLIndividual(URI.create(myURI + "#HervikStrawberryJam"));
         OWLClass strawberryJam = owlDataFactory.getOWLClass(URI.create(myURI + "#StrawberryJam"));
         //Set<Set<OWLClass>> type = reasoner.getTypes(hervikSJ);

         Set<OWLObjectProperty> objectProperties = reasoner.getObjectProperties(); //alle
         //s� kan man evt koble properties til domains, og videre lage lister for hvilke som h�rer til hvilke domains, og videre hvilke ranges de har
         for (OWLObjectProperty objectProperty : objectProperties) {
             System.out.println("objectProperty = " + objectProperty);
             OWLIndividual range = reasoner.getRelatedIndividual(hervikSJ, objectProperty);
             System.out.println("range = " + range);
         }

         //todo lag test ?? hva pr�ver denne p�?
     }


     //todo gettypes vs gettype ??

     @Test
     public void findRangeOfPropertyOfIndividual() throws OWLOntologyCreationException, OWLOntologyChangeException, OWLReasonerException {
         reasoner.getKB().realize();
         reasoner.getKB().printClassTree();

         OWLClass jam = manager.getOWLDataFactory().getOWLClass(URI.create(myURI + "#Jam"));
         OWLObjectProperty hasIngredient = manager.getOWLDataFactory().getOWLObjectProperty(URI.create(myURI + "#hasIngredient"));

         Set<OWLIndividual> individuals = reasoner.getIndividuals(jam, false);
         for (OWLIndividual ind : individuals) {
             // get the info about this specific individual
             //OWLIndividual name = reasoner.getRelatedIndividual(ind, hasIngredient);
             OWLClass type = reasoner.getType(ind);
             OWLIndividual ingredient = reasoner.getRelatedIndividual(ind, hasIngredient);
             System.out.println("Type: " + type.getURI().getFragment());
             System.out.println("Ingredient:  = " + ingredient);
         }
         //todo lag test  ?? hvorfor? har antakeligvis mer enn en type - evt kj�re den p� uresonnert ontologi....
     }

     @Test
     public void findRangesOfPropertyOfIndividual() throws OWLOntologyCreationException, OWLOntologyChangeException, OWLReasonerException {
         reasoner.getKB().realize();
         reasoner.getKB().printClassTree();

         OWLClass jam = manager.getOWLDataFactory().getOWLClass(URI.create(myURI + "#Jam"));
         OWLObjectProperty hasIngredient = manager.getOWLDataFactory().getOWLObjectProperty(URI.create(myURI + "#hasIngredient"));

         Set<OWLIndividual> individuals = reasoner.getIndividuals(jam, false);
         for (OWLIndividual ind : individuals) {
             // get the info about this specific individual
             //OWLIndividual name = reasoner.getRelatedIndividual(ind, hasIngredient);
             Set<Set<OWLClass>> types = reasoner.getTypes(ind);
             for (Set<OWLClass> owlClasses : types) {

                 OWLIndividual ingredient = reasoner.getRelatedIndividual(ind, hasIngredient);
                 //System.out.println("Type: " + type.getURI().getFragment());
                 System.out.println("Ingredient:  = " + ingredient);
             }

         }
         //todo lag test - ligner veldig p� den over
     }


     //todo fjern?

     @Test
     public void findAllProperties() {
         // create property and resources to query the reasoner
         OWLIndividual hervikSJ = owlDataFactory.getOWLIndividual(URI.create(myURI + "#HervikStrawberryJam"));
         //properties = hervikSJ.
         Set<OWLProperty<?, ?>> properties = reasoner.getProperties();    //returnerer ALLE

         //s� kan man evt koble properties til domains, og videre lage lister for hvilke som h�rer til hvilke domains, og videre hvilke ranges de har

         for (OWLProperty<?, ?> property : properties) {
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

         //todo lag test - eller fjern siden jeg f� hentet ut datatype og objectproperties hver for seg
     }
    
}
