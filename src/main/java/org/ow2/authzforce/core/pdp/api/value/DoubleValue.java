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

import java.util.Deque;

import javax.xml.bind.DatatypeConverter;

/**
 * Representation of an xs:double value. This class supports parsing xs:double values. All objects of this class are immutable and all methods of the class are thread-safe. The choice of the Java type
 * Double is based on JAXB schema-to-Java mapping spec: https://docs.oracle.com/javase/tutorial/jaxb/intro/bind.html
 *
 * 
 * @version $Id: $
 */
public final class DoubleValue extends NumericValue<Double, DoubleValue> implements Comparable<DoubleValue>
{
	private static final ArithmeticException ILLEGAL_DIV_BY_ZERO_EXCEPTION = new ArithmeticException("Illegal division by zero");

	/**
	 * Creates a new <code>DoubleAttributeValue</code> that represents the double value supplied.
	 *
	 * @param value
	 *            the <code>double</code> value to be represented
	 */
	public DoubleValue(final Double value)
	{
		super(value);
	}

	/**
	 * Creates instance from string according to xsd:double definition
	 * 
	 * @param val
	 *            A string containing lexical representation of xsd:double
	 * @throws NumberFormatException
	 *             {@code val} is not a valid string representation of a {@code double} value
	 */
	public DoubleValue(final String val) throws NumberFormatException
	{
		this(Double.valueOf(DatatypeConverter.parseDouble(val)));
	}

	/**
	 * Value zero
	 */
	public static final DoubleValue ZERO = new DoubleValue(Double.valueOf(0));

	/** {@inheritDoc} */
	@Override
	public int compareTo(final DoubleValue o)
	{
		return this.value.compareTo(o.value);
	}

	/** {@inheritDoc} */
	@Override
	public DoubleValue abs()
	{
		final double result = Math.abs(this.value.doubleValue());
		return new DoubleValue(Double.valueOf(result));
	}

	/** {@inheritDoc} */
	@Override
	public DoubleValue add(final Deque<? extends DoubleValue> others)
	{

		double sum = value.doubleValue();
		while (!others.isEmpty())
		{
			sum += others.poll().value.doubleValue();
		}

		return new DoubleValue(Double.valueOf(sum));
	}

	/** {@inheritDoc} */
	@Override
	public DoubleValue multiply(final Deque<? extends DoubleValue> others)
	{
		double product = value.doubleValue();
		while (!others.isEmpty())
		{
			product *= others.poll().value.doubleValue();
		}

		return new DoubleValue(Double.valueOf(product));
	}

	/** {@inheritDoc} */
	@Override
	public DoubleValue divide(final DoubleValue divisor) throws ArithmeticException
	{
		/*
		 * Quotes from Java Language Specification (Java SE 7 Edition), §4.2.3. Floating-Point Types, Formats, and Values http://docs.oracle.com/javase/specs/jls/se7/html/jls-4.html#jls-4.2 Quotes: "A
		 * NaN value is used to represent the result of certain invalid operations such as dividing zero by zero. [...] 1.0/0.0 has the value positive infinity, while the value of 1.0/-0.0 is negative
		 * infinity." Also "Example 4.2.4-1. Floating-point Operations" shows that 0.0/0.0 = NaN. Negative/Positive Infinity and NaN have their equivalent in XML schema (INF, -INF, Nan), so we can
		 * return the result of division by zero as it is (JAXB will convert it properly).
		 */

		final double result = value.doubleValue() / divisor.value.doubleValue();
		if (Double.isInfinite(result) || Double.isNaN(result))
		{
			throw ILLEGAL_DIV_BY_ZERO_EXCEPTION;
		}

		return new DoubleValue(Double.valueOf(result));
	}

	/**
	 * <p>
	 * floor
	 * </p>
	 *
	 * @see Math#floor(double)
	 * @return result of Math#floor(double) as AttributeValue
	 */
	public DoubleValue floor()
	{
		final double result = Math.floor(value.doubleValue());
		return new DoubleValue(Double.valueOf(result));
	}

	/**
	 * Rounds the double using default IEEE754 rounding mode . According to XACML core spec, §7.5 Arithmetic evaluation, "rounding - is set to round-half-even (IEEE 854 §4.1)" (
	 * {@link java.math.RoundingMode#HALF_EVEN}). This method uses {@link Math#rint(double)} that does the equivalent of the {@link java.math.RoundingMode#HALF_EVEN}.
	 *
	 * @return result of Math#rint(double) as AttributeValue
	 */
	public DoubleValue roundIEEE754Default()
	{
		final double result = Math.rint(value.doubleValue());
		return new DoubleValue(Double.valueOf(result));
	}

	// For quick testing
	// public static void main(String... args)
	// {
	// Double arg1 = new Double("1");
	// Double divisor = new Double("0");
	// Double result = arg1 / divisor;
	// System.out.println(result); // Infinity!
	// arg1 = new Double("-1");
	// result = arg1 / divisor;
	// System.out.println(result); // -Infinity!
	//
	// Double positiveZero = new Double("0.");
	// Double negativeZero = new Double("-0.");
	// System.out.println(positiveZero.equals(negativeZero));
	// double inf = DatatypeConverter.parseDouble("INF");
	// System.out.println(DatatypeConverter.printDouble(inf));
	// }

	/** {@inheritDoc} */
	@Override
	public DoubleValue subtract(final DoubleValue subtractedVal)
	{
		final double result = this.value.doubleValue() - subtractedVal.value.doubleValue();
		return new DoubleValue(Double.valueOf(result));
	}

	/**
	 * Converts this double value to a long, as specified by {@link Double#longValue()}
	 *
	 * @return <code>this</code> as an integer
	 */
	public long longValue()
	{
		return value.longValue();
	}

	/** {@inheritDoc} */
	@Override
	public String printXML()
	{
		return DatatypeConverter.printDouble(this.value.doubleValue());
	}

}
