package no.ntnu.ontology;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import java.util.ArrayList;
import java.util.List;

public class SingleResultSetRetrieverImpl implements ResultSetRetriever{
    String unknownVariable;

    public SingleResultSetRetrieverImpl(String unknownVariable) {
        this.unknownVariable = unknownVariable;
    }

    public List<String> getResultset(ResultSet rs) {
        List<String> resultUrls = new ArrayList<String>();
        while (rs.hasNext()) {
            QuerySolution soln = rs.nextSolution();
            RDFNode x = soln.get(unknownVariable);       // Get a result variable by name.

            String affinityURL = x.toString();
            resultUrls.add(affinityURL);
        }
        return resultUrls;
    }
}
