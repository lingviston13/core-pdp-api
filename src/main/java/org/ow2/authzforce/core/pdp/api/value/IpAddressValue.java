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
package org.ow2.authzforce.core.pdp.api.value;

import java.net.InetAddress;
import java.util.Objects;

import javax.xml.ws.Holder;

import com.google.common.net.InetAddresses;

/**
 * Represents the IPAddress datatype introduced in XACML 2.0. All objects of this class are immutable and all methods of the class are thread-safe.
 *
 * 
 * @version $Id: $
 */
public final class IpAddressValue extends StringParseableValue<String>
{

	/*
	 * InetAddresses deliberately avoids all nameservice lookups (e.g. no DNS) on the contrary to the JDK InetAddress.getByName(). Therefore no UnknownHostException to handle.
	 */
	private static void parseIPv4Address(final String val, final Holder<InetAddress> returnedAddress, final Holder<InetAddress> returnedMask, final Holder<NetworkPortRange> returnedRange)
	        throws IllegalArgumentException
	{
		assert val != null;

		final InetAddress address;
		final InetAddress mask;
		final NetworkPortRange range;

		// start out by seeing where the delimiters are
		final int maskPos = val.indexOf('/');
		final int rangePos = val.indexOf(':');

		// now check to see which components we have
		if (maskPos == rangePos)
		{
			/*
			 * the string is just an address InetAddresses deliberately avoids all nameservice lookups (e.g. no DNS) on the contrary to the JDK InetAddress.getByName().
			 */
			address = InetAddresses.forString(val);
			mask = null;
			range = NetworkPortRange.MAX;
		} else if (maskPos != -1)
		{
			// there is also a mask (and maybe a range)
			/*
			 * InetAddresses deliberately avoids all nameservice lookups (e.g. no DNS) on the contrary to the JDK InetAddress.getByName().
			 */
			address = InetAddresses.forString(val.substring(0, maskPos));
			if (rangePos != -1)
			{
				// there's a range too, so get it and the mask
				/*
				 * InetAddresses deliberately avoids all nameservice lookups (e.g. no DNS) on the contrary to the JDK InetAddress.getByName().
				 */
				mask = InetAddresses.forString(val.substring(maskPos + 1, rangePos));
				range = NetworkPortRange.getInstance(val.substring(rangePos + 1, val.length()));
			} else
			{
				// there's no range, so just get the mask
				/*
				 * InetAddresses deliberately avoids all nameservice lookups (e.g. no DNS) on the contrary to the JDK InetAddress.getByName().
				 */
				mask = InetAddresses.forString(val.substring(maskPos + 1, val.length()));
				// if the range is null, then create it as unbound
				range = NetworkPortRange.MAX;
			}
		} else
		{
			// there is a range, but no mask
			/*
			 * InetAddresses deliberately avoids all nameservice lookups (e.g. no DNS) on the contrary to the JDK InetAddress.getByName().
			 */
			address = InetAddresses.forString(val.substring(0, rangePos));
			mask = null;
			range = NetworkPortRange.getInstance(val.substring(rangePos + 1, val.length()));
		}

		returnedAddress.value = address;
		returnedMask.value = mask;
		returnedRange.value = range;
	}

	/*
	 * InetAddresses deliberately avoids all nameservice lookups (e.g. no DNS) on the contrary to the JDK InetAddress.getByName(). Therefore no UnknownHostException to handle.
	 */
	private static void parseIPv6Address(final String val, final Holder<InetAddress> returnedAddress, final Holder<InetAddress> returnedMask, final Holder<NetworkPortRange> returnedRange)
	        throws IllegalArgumentException
	{
		// Let's validate
		final InetAddress address;
		final InetAddress mask;
		final NetworkPortRange range;
		final int len = val.length();

		// get the required address component
		int endIndex = val.indexOf(']');
		/*
		 * InetAddresses deliberately avoids all nameservice lookups (e.g. no DNS) on the contrary to the JDK InetAddress.getByName().
		 */
		address = InetAddresses.forString(val.substring(1, endIndex));

		// see if there's anything left in the string
		if (endIndex != len - 1)
		{
			// if there's a mask, it's also an IPv6 address
			if (val.charAt(endIndex + 1) == '/')
			{
				final int startIndex = endIndex + 3;
				endIndex = val.indexOf(']', startIndex);
				mask = InetAddresses.forString(val.substring(startIndex, endIndex));
			} else
			{
				mask = null;
			}

			// finally, see if there's a port range, if we're not finished
			if (endIndex != len - 1 && val.charAt(endIndex + 1) == ':')
			{
				range = NetworkPortRange.getInstance(val.substring(endIndex + 2, len));
			} else
			{
				range = NetworkPortRange.MAX;
			}
		} else
		{
			mask = null;
			range = NetworkPortRange.MAX;
		}

		returnedAddress.value = address;
		returnedMask.value = mask;
		returnedRange.value = range;
	}

	private static void parseIPAddress(final String val, final Holder<InetAddress> returnedAddress, final Holder<InetAddress> returnedMask, final Holder<NetworkPortRange> returnedRange)
	        throws IllegalArgumentException
	{
		// an IPv6 address starts with a '['
		if (val.indexOf('[') == 0)
		{
			parseIPv6Address(val, returnedAddress, returnedMask, returnedRange);
		} else
		{
			parseIPv4Address(val, returnedAddress, returnedMask, returnedRange);
		}
	}

	/*
	 * These fields are not actually needed in the XACML core specification since no function uses them, but it might be useful for new XACML profile or custom functions dealing with network access
	 * control for instance.
	 */
	private final InetAddress address;
	private final InetAddress mask;

	/*
	 * Forced to be non-transient (although derived from other fields) to comply with Serializable contract while staying final
	 */
	private final NetworkPortRange portRange;

	private transient volatile int hashCode = 0; // Effective Java - Item 9

	/**
	 * Instantiates from string representation
	 *
	 * @param val
	 *            string form of IP address
	 * @throws java.lang.IllegalArgumentException
	 *             if {@code val} is not a valid XACML IPAddress string
	 */
	public IpAddressValue(final String val) throws IllegalArgumentException
	{
		super(val);
		final Holder<InetAddress> addressHolder = new Holder<>();
		final Holder<InetAddress> maskHolder = new Holder<>();
		final Holder<NetworkPortRange> rangeHolder = new Holder<>();
		parseIPAddress(this.value, addressHolder, maskHolder, rangeHolder);
		address = addressHolder.value;
		mask = maskHolder.value;
		portRange = rangeHolder.value;
	}

	// /**
	// * Returns the address represented by this object.
	// *
	// * @return the address
	// */
	// public InetAddress getAddress()
	// {
	// return address;
	// }
	//
	// /**
	// * Returns the mask represented by this object, or null if there is no mask.
	// *
	// * @return the mask or null
	// */
	// public InetAddress getMask()
	// {
	// return mask;
	// }
	//
	// /**
	// * Returns the port range represented by this object which will be unbound if no range was specified.
	// *
	// * @return the range
	// */
	// public PortRange getRange()
	// {
	// return portRange;
	// }

	/** {@inheritDoc} */
	@Override
	public int hashCode()
	{
		if (hashCode == 0)
		{
			// hash regardless of letter case
			hashCode = Objects.hash(address, mask, portRange);
		}

		return hashCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (!(obj instanceof IpAddressValue))
		{
			return false;
		}

		final IpAddressValue other = (IpAddressValue) obj;
		// address and range non-null
		return this.address.equals(other.address) && this.portRange.equals(other.portRange) && Objects.equals(this.mask, other.mask);
	}

	/** {@inheritDoc} */
	@Override
	public String printXML()
	{
		return this.value;
	}

}
