package no.ntnu.ontology;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lillian Hella
 * Date: Oct 14, 2013
 * Packs the resultset into a usable simple List/Map structure
 */
public class MultipleResultSetRetriever implements ResultSetRetriever {
    public List<Map<String, RDFNode>> retrieveResultset(ResultSet rs) {
        List<Map<String, RDFNode>> results = new ArrayList<Map<String, RDFNode>>();
        while (rs.hasNext()) {
            Map<String, RDFNode> resultset = new HashMap<String, RDFNode>();

            QuerySolution querySolution = rs.nextSolution();
            List<String> vars = rs.getResultVars();
            for (String var : vars) {
                resultset.put(var, querySolution.get(var));
            }
            results.add(resultset);
        }
        return results;
    }
}
