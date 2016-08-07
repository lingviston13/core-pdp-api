/**
 * Copyright (C) 2012-2016 Thales Services SAS.
 *
 * This file is part of AuthZForce CE.
 *
 * AuthZForce CE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AuthZForce CE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AuthZForce CE.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.ow2.authzforce.core.pdp.api;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

import org.ow2.authzforce.xmlns.pdp.ext.AbstractDecisionCache;

/**
 * Authorization (XACML) decision result cache. Implements {@link Closeable} because a cache may use resources external to the JVM such as a disk or connection to a remote server for persistence,
 * replication, clustering, etc. Therefore, these resources must be released by calling {@link #close()} when it is no longer needed.
 * 
 */
public interface DecisionCache extends Closeable
{
	/**
	 * Factory for creating instance of DecisionCache extension
	 * 
	 * @param <CONF_T>
	 *            type of extension configuration (initialization parameters)
	 */
	abstract class Factory<CONF_T extends AbstractDecisionCache> extends JaxbBoundPdpExtension<CONF_T>
	{

		/**
		 * Instantiates decision cache extension
		 * 
		 * @param conf
		 *            extension parameters
		 * @return instance of extension
		 */
		public abstract DecisionCache getInstance(CONF_T conf);
	}

	/**
	 * Gets the decision result(s) from the cache for the given decision request(s). The ability to get multiple cached results at once allows the Cache implementation to optimize the retrieval by
	 * requesting all in the same request, e.g. if the cache is in a remote storage/server.
	 * 
	 * @param decisionInputs
	 *            individual decision input(s)
	 * @return a map where each entry key is an input from {@code decisionInputs}, and the value is the corresponding decision result from cache, or null if no such result found in cache. Each input
	 *         in {@code decisionInputs} but be a key in the Map returned, and the Map size must be equal to {@code decisionInputs.size()}.
	 */
	<DECISION_INPUT_T extends DecisionInput> Map<DECISION_INPUT_T, DecisionResult> getAll(List<DECISION_INPUT_T> decisionInputs);

	/**
	 * Puts a XACML decision requests and corresponding results in cache. The ability to put multiple cache entries at once allows the Cache implementation to optimize the creation/update by doing
	 * them all in the same request, e.g. if the cache is in a remote storage/server.
	 * 
	 * @param resultsByRequest
	 *            (request, result) pairs as key-value pairs to be cached
	 */
	<DECISION_INPUT_T extends DecisionInput> void putAll(Map<DECISION_INPUT_T, DecisionResult> resultsByRequest);

}
