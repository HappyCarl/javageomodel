/**
 * 
 */
package com.beoui.geocell;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.beoui.geocell.model.GeocellQuery;
import com.beoui.geocell.model.LocationCapable;

/**
 * JPA standard search
 * @author bfuster
 *
 */
public class JPALocationCapableRepositorySearchImpl<T extends LocationCapable> implements
		LocationCapableRepositorySearch<T> {

	private GeocellQuery baseQuery;
	private PersistenceManager pm;
	private Class<T> entityClass;
	
	public JPALocationCapableRepositorySearchImpl(GeocellQuery baseQuery, PersistenceManager pm, Class<T> clazz) {
		this.baseQuery = baseQuery;
		this.pm = pm;
	}
	
	@Override
	public List<T> search(List<String> geocells) {

		// Run query on the next set of geocells.
        //String and = baseQuery.getBaseQuery().toUpperCase().contains("WHERE") ? " && " : " ";
        String and = baseQuery.getBaseQuery() != null || baseQuery.getBaseQuery().trim().length() == 0 ? " && " : " ";
        Query query = pm.newQuery(entityClass, baseQuery.getBaseQuery() + and + "geocellsP.contains(geocells)");

        if(baseQuery.getDeclaredParameters() == null || baseQuery.getDeclaredParameters().trim().length() == 0) {
            query.declareParameters("String geocellsP");
        } else {
            query.declareParameters(baseQuery.getDeclaredParameters() + ", String geocellsP");
        }

        List<T> newResultEntities;
        if(baseQuery.getParameters() == null || baseQuery.getParameters().isEmpty()) {
            newResultEntities =(List<T>) query.execute(geocells);
        } else {
            List<Object> parameters = new ArrayList<Object>(baseQuery.getParameters());
            parameters.add(geocells);
            newResultEntities = (List<T>) query.executeWithArray(parameters.toArray());
        }
        
        return newResultEntities;
		
	}
	
}
