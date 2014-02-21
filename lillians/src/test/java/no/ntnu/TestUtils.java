package no.ntnu;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLEntity;
import java.util.*;

public class TestUtils {
    public static boolean contains(String comparison, Collection<? extends OWLEntity> listOfSomething) {
        for (OWLEntity owlClass : listOfSomething) {
            if(owlClass != null && owlClass.toString().equals(comparison))
                return true;
        }
        return false;
    }

    public static List<OWLClass> flatSomething(Collection<? extends OWLEntity> result, FlatMapCallback flatMapCallback) {
        List<OWLClass> typesOfAffinity = new ArrayList<OWLClass>();
        for (OWLEntity owlEntity : result) {
            Set<Set<OWLClass>> setOfSets = flatMapCallback.doMagic(owlEntity);
            for (Set<OWLClass> owlClasses : setOfSets) {
                for (OWLClass owlClass : owlClasses) {
                    typesOfAffinity.add(owlClass);
                }
            }
        }
        return typesOfAffinity;
    }
}

