package no.ntnu;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLEntity;

import java.util.Set;

public interface FlatMapCallback {
    Set<Set<OWLClass>> doMagic(OWLEntity individual) ;
}
