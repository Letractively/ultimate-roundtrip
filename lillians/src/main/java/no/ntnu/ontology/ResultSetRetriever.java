package no.ntnu.ontology;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import java.util.List;
import java.util.Map;

public interface ResultSetRetriever {
    public List<Map<String, RDFNode>> retrieveResultset(ResultSet rs);
}
