import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasonerException;
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
 * Time: 8:51:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class PropertyRelatedTests {
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

    @Test
     public void findObjectPropertyForIndividualsOfAClass() {
         OWLDataFactory factory = manager.getOWLDataFactory();

         OWLClass strawberryJam = manager.getOWLDataFactory().getOWLClass(URI.create(myURI + "#StrawberryJam"));
         Set<OWLIndividual> individuals = reasoner.getIndividuals(strawberryJam, false);
         OWLObjectProperty hasProducer = factory.getOWLObjectProperty(URI.create(myURI + "#hasProducer"));

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
         OWLDataFactory factory = manager.getOWLDataFactory();

         OWLObjectProperty hasProducer = factory.getOWLObjectProperty(URI.create(myURI + "#hasProducer"));
         OWLIndividual hervikSJ = factory.getOWLIndividual(URI.create(myURI + "#HervikStrawberryJam"));
         OWLIndividual producer = reasoner.getRelatedIndividual(hervikSJ, hasProducer);

         assertEquals("Hervik", producer.toString());
     }

     @Test
     public void findAllDataPropertiesForIndividual() {
         OWLDataFactory factory = manager.getOWLDataFactory();

         // create property and resources to query the reasoner
         OWLIndividual bill = factory.getOWLIndividual(URI.create(myURI + "#Bill"));
         OWLClass strawberryJam = factory.getOWLClass(URI.create(myURI + "#StrawberryJam"));
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
         OWLDataFactory factory = manager.getOWLDataFactory();

         OWLIndividual hervikSJ = factory.getOWLIndividual(URI.create(myURI + "#HervikStrawberryJam"));
         OWLClass strawberryJam = factory.getOWLClass(URI.create(myURI + "#StrawberryJam"));
         //Set<Set<OWLClass>> type = reasoner.getTypes(hervikSJ);

         Set<OWLObjectProperty> objectProperties = reasoner.getObjectProperties(); //alle
         //så kan man evt koble properties til domains, og videre lage lister for hvilke som hører til hvilke domains, og videre hvilke ranges de har
         for (OWLObjectProperty objectProperty : objectProperties) {
             System.out.println("objectProperty = " + objectProperty);
             OWLIndividual range = reasoner.getRelatedIndividual(hervikSJ, objectProperty);
             System.out.println("range = " + range);
         }

         //todo lag test
     }


     //todo gettypes vs gettype

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
         //todo lag test
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
         //todo lag test
     }


     //todo fjern?

     @Test
     public void findAllProperties() {
         OWLDataFactory factory = manager.getOWLDataFactory();

         // create property and resources to query the reasoner
         OWLIndividual hervikSJ = factory.getOWLIndividual(URI.create(myURI + "#HervikStrawberryJam"));
         //properties = hervikSJ.
         Set<OWLProperty<?, ?>> properties = reasoner.getProperties();    //returnerer ALLE

         //så kan man evt koble properties til domains, og videre lage lister for hvilke som hører til hvilke domains, og videre hvilke ranges de har

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

         //todo lag test - eller fjern siden jeg få hentet ut datatype og objectproperties hver for seg
     }
    
}
