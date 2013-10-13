package no.ntnu.ontology;

import com.hp.hpl.jena.query.ResultSet;

import java.util.List;

public interface ResultSetRetriever {
    public List<String> getResultset(ResultSet rs);
}
