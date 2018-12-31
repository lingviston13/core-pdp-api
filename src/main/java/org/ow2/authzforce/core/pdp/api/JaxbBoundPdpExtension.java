/**
 * Copyright 2012-2018 THALES.
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
package org.ow2.authzforce.core.pdp.api;

import org.ow2.authzforce.xmlns.pdp.ext.AbstractPdpExtension;

/**
 * Marker Interface for PDP extensions bound to a specific XML/JAXB class (used for the configuration of the extension)
 * 
 * @param <T>
 *            XML/JAXB type used as configuration class for the extension. There must be a one-to-one relationship between such types and the JAXB-bound extensions.
 * 
 */
public abstract class JaxbBoundPdpExtension<T extends AbstractPdpExtension> implements PdpExtension
{
	/**
	 * Gets the XML/JAXB class used as configuration class for the extension. There must be a one-to-one relationship between such types and the JAXB-bound extensions.
	 * 
	 * @return XML/JAXB class bound to this extension
	 */
	public abstract Class<T> getJaxbClass();

	@Override
	public final String getId()
	{
		return getJaxbClass().getCanonicalName();
	}

}
