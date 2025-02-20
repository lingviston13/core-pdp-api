/**
 * Copyright 2012-2019 THALES.
 *
 * This file is part of AuthzForce CE.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package org.ow2.authzforce.core.pdp.api;


/**
 * Attribute source identifier (request? PDP? custom attribute provider module?)
 *
 */
public interface AttributeSource
{
	/**
	 * Type of attribute source
	 *
	 */
	enum Type
	{
		REQUEST, PDP, OTHER;
	}

	/**
	 * Get the type of this attribute source
	 * 
	 * @return type of this attribute source
	 */
	Type getType();

	/**
	 * Get identifier
	 * 
	 * @return unique string identifier
	 */
	@Override
	String toString();
}
