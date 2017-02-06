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

import java.util.List;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.Result;

/**
 * Decision result filter, i.e. a PDP extension that processes decision Results from policy evaluation engine before the final XACML Response is created (and returned back to the requester). For
 * example, a typical Result filter may combine multiple individual decisions - produced by the 'requestFilter' - to a single decision Result if and only if the XACML Request's 'CombinedDecision' is
 * set to true, as defined in XACML Multiple Decision Profile specification, section 3.
 * 
 */
public interface DecisionResultFilter extends PdpExtension
{
	/**
	 * Decision Result collector filtering results in a specific way
	 *
	 */
	interface FilteringResultCollector
	{
		/**
		 * Add new decision result to be filtered (with others, e.g. for combining into a single result) in this context
		 * 
		 * @param request
		 *            individual decision request
		 * 
		 * @param result
		 *            of the evaluation of {@code request} by the PDP
		 * @return results to be returned as final results iff not null, in which case subsequent results must be ignored; else (if null), continue calling this method with the subsequent results.
		 */
		List<Result> addResult(IndividualDecisionRequest request, PdpDecisionResult result);

		/**
		 * Get the final results after filtering all input results added so far with {@link #addResult(IndividualDecisionRequest, PdpDecisionResult)}. To be called when there is no more result to be
		 * filtered and {@link #addResult(IndividualDecisionRequest, PdpDecisionResult)} did not return anything non-null
		 * 
		 * @return filtered results
		 */
		List<Result> getFilteredResults();
	}

	/**
	 * Create a decision result collector for filtering multiple decision results together (e.g. in order to combine into a single result like in XACML Multiple Decision Profile)
	 * 
	 * @param numberOfInputResults
	 *            maximum number of results to be filtered in the new context, i.e. max number of call to {@link FilteringResultCollector#addResult(IndividualDecisionRequest, PdpDecisionResult)}
	 *            before calling final {@link FilteringResultCollector#getFilteredResults()}
	 * 
	 * @return filtering context
	 */
	FilteringResultCollector newResultCollector(int numberOfInputResults);

	/**
	 * 
	 * Support for CombinedDecision = true as specified in Multiple Decision Profile §3 for combining decisions.
	 * 
	 * @return true iff requests for combined decisions is supported by this
	 */
	boolean supportsMultipleDecisionCombining();
}
