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
package org.ow2.authzforce.core.pdp.api.func;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;
import org.ow2.authzforce.core.pdp.api.expression.Expression;
import org.ow2.authzforce.core.pdp.api.func.BaseFirstOrderFunctionCall.EagerMultiPrimitiveTypeEval;
import org.ow2.authzforce.core.pdp.api.value.AttributeValue;
import org.ow2.authzforce.core.pdp.api.value.BooleanValue;
import org.ow2.authzforce.core.pdp.api.value.Datatype;
import org.ow2.authzforce.core.pdp.api.value.SimpleValue;
import org.ow2.authzforce.core.pdp.api.value.StandardDatatypes;
import org.ow2.authzforce.core.pdp.api.value.StringValue;
import org.ow2.authzforce.xacml.identifiers.XacmlStatusCode;

/**
 * Generic match functions taking two parameters of possibly different types, e.g. a string and a URI.
 *
 * @param <T0>
 *            Type of the first parameter of this function.
 * @param <T1>
 *            Type of the second parameter of this function.
 * 
 * @version $Id: $
 */
public class NonEqualTypeMatchFunction<T0 extends AttributeValue, T1 extends AttributeValue> extends MultiParameterTypedFirstOrderFunction<BooleanValue>
{
	/**
	 * Generic match method interface for values of different types
	 * 
	 * @param <T0>
	 *            first type of value to be matched
	 * @param <T1>
	 *            second type of value to be matched
	 *
	 */
	public interface Matcher<T0 extends AttributeValue, T1 extends AttributeValue>
	{
		/**
		 * Evaluate function with second parameter as string
		 * 
		 * @param arg0
		 *            first function parameter
		 * @param arg1
		 *            second function parameter
		 * @return true if and only if both arguments match according to the matcher definition
		 * @throws IllegalArgumentException
		 *             if one of the arguments is not valid for this matcher
		 */
		boolean match(T0 arg0, T1 arg1) throws IllegalArgumentException;
	}

	/**
	 * Match function call factory
	 * 
	 * @param <T0>
	 *            first parameter type of match function
	 * @param <T1>
	 *            second parameter type of match function
	 *
	 */
	public static class CallFactory<T0 extends AttributeValue, T1 extends AttributeValue>
	{
		private final String invalidArgTypesErrorMsg;
		private final String invalidRegexErrorMsg;
		private final Datatype<T0> paramType0;
		private final Datatype<T1> paramType1;
		private final Matcher<T0, T1> matcher;
		private final FirstOrderFunctionSignature<BooleanValue> funcSig;

		private CallFactory(final FirstOrderFunctionSignature<BooleanValue> functionSig, final Datatype<T0> paramType0, final Datatype<T1> paramType1, final Matcher<T0, T1> matcher)
		{

			this.invalidArgTypesErrorMsg = "Function " + functionSig.getName() + ": Invalid arg types. Expected: " + paramType0 + "," + paramType1;
			this.invalidRegexErrorMsg = "Function " + functionSig.getName() + ": Invalid regular expression in arg#0";
			this.paramType0 = paramType0;
			this.paramType1 = paramType1;
			this.matcher = matcher;
			this.funcSig = functionSig;
		}

		protected FirstOrderFunctionCall<BooleanValue> getInstance(final List<Expression<?>> argExpressions, final Datatype<?>[] remainingArgTypes)
		{
			return new EagerMultiPrimitiveTypeEval<BooleanValue>(funcSig, argExpressions, remainingArgTypes)
			{
				@Override
				protected final BooleanValue evaluate(final Deque<AttributeValue> args) throws IndeterminateEvaluationException
				{
					final AttributeValue rawArg0 = args.poll();
					final AttributeValue rawArg1 = args.poll();

					final T0 arg0;
					final T1 arg1;
					try
					{
						arg0 = paramType0.cast(rawArg0);
						arg1 = paramType1.cast(rawArg1);
					} catch (final ClassCastException e)
					{
						throw new IndeterminateEvaluationException(invalidArgTypesErrorMsg, XacmlStatusCode.PROCESSING_ERROR.value(), e);
					}

					final boolean isMatched;
					try
					{
						isMatched = matcher.match(arg0, arg1);
					} catch (final PatternSyntaxException e)
					{
						throw new IndeterminateEvaluationException(invalidRegexErrorMsg, XacmlStatusCode.PROCESSING_ERROR.value(), e);
					}

					return BooleanValue.valueOf(isMatched);
				}
			};
		}

	}

	/**
	 * Match function call factory builder
	 * 
	 * @param <T0>
	 *            type of first value to be matched
	 * @param <T1>
	 *            type of second value to be matched against the first one
	 * 
	 */
	public interface CallFactoryBuilder<T0 extends AttributeValue, T1 extends AttributeValue>
	{
		/**
		 * Builds the match function call factory
		 * 
		 * @param functionSignature
		 *            match function signature
		 * @param paramType0
		 *            match function's first parameter type
		 * @param paramType1
		 *            match function's second parameter type
		 * @return match function call factory
		 */
		CallFactory<T0, T1> build(FirstOrderFunctionSignature<BooleanValue> functionSignature, Datatype<T0> paramType0, Datatype<T1> paramType1);
	}

	private final CallFactory<T0, T1> funcCallFactory;

	/**
	 * Creates a new <code>NonEqualTypeMatchFunction</code> based on a match method.
	 * 
	 * @param functionName
	 *            the name of the standard match function, including the complete namespace
	 * @param paramType0
	 *            first parameter type
	 * @param paramType1
	 *            second parameter type
	 * @param matcher
	 *            matching algorithm
	 * 
	 */
	public NonEqualTypeMatchFunction(final String functionName, final Datatype<T0> paramType0, final Datatype<T1> paramType1, final Matcher<T0, T1> matcher)
	{
		super(functionName, StandardDatatypes.BOOLEAN, false, Arrays.asList(paramType0, paramType1));
		this.funcCallFactory = new CallFactory<>(this.functionSignature, paramType0, paramType1, matcher);
	}

	/**
	 * Creates a new <code>NonEqualTypeMatchFunction</code> based on a match method call factory builder.
	 * 
	 * @param functionName
	 *            the name of the standard match function, including the complete namespace
	 * @param paramType0
	 *            first parameter type
	 * @param paramType1
	 *            second parameter type
	 * @param callFactoryBuilder
	 *            match function call factory builder
	 * 
	 */
	public NonEqualTypeMatchFunction(final String functionName, final Datatype<T0> paramType0, final Datatype<T1> paramType1, final CallFactoryBuilder<T0, T1> callFactoryBuilder)
	{
		super(functionName, StandardDatatypes.BOOLEAN, false, Arrays.asList(paramType0, paramType1));
		this.funcCallFactory = callFactoryBuilder.build(functionSignature, paramType0, paramType1);
	}

	/** {@inheritDoc} */
	@Override
	public FirstOrderFunctionCall<BooleanValue> newCall(final List<Expression<?>> argExpressions, final Datatype<?>... remainingArgTypes) throws IllegalArgumentException
	{
		/*
		 * Actual argument types are expected to be different, therefore we use the supertype AttributeValue as generic parameter type for all when creating the function call
		 */
		return funcCallFactory.getInstance(argExpressions, remainingArgTypes);
	}

	/**
	 * *-regexp-match function
	 * 
	 * @param <AV>
	 *            second parameter type
	 */
	public static class RegexpMatchCallFactoryBuilder<AV extends SimpleValue<String>> implements CallFactoryBuilder<StringValue, AV>
	{

		private final Matcher<StringValue, AV> regexMatcher = (regex, arg1) -> RegexpMatchFunctionHelper.match(regex, arg1);

		private class RegexpMatchCallFactory extends CallFactory<StringValue, AV>
		{
			private final RegexpMatchFunctionHelper regexFuncHelper;

			private RegexpMatchCallFactory(final FirstOrderFunctionSignature<BooleanValue> functionSignature, final Datatype<AV> secondParamType)
			{
				super(functionSignature, StandardDatatypes.STRING, secondParamType, regexMatcher);
				regexFuncHelper = new RegexpMatchFunctionHelper(functionSignature, secondParamType);
			}

			@Override
			protected FirstOrderFunctionCall<BooleanValue> getInstance(final List<Expression<?>> argExpressions, final Datatype<?>[] remainingArgTypes)
			{
				final FirstOrderFunctionCall<BooleanValue> compiledRegexFuncCall = regexFuncHelper.getCompiledRegexMatchCall(argExpressions, remainingArgTypes);
				/*
				 * compiledRegexFuncCall == null means no optimization using a pre-compiled regex could be done; in this case, use super.newCall() as usual, which will call match() down below,
				 * compiling the regex on-the-fly for each evaluation.
				 */
				return compiledRegexFuncCall == null ? super.getInstance(argExpressions, remainingArgTypes) : compiledRegexFuncCall;
			}
		}

		@Override
		public CallFactory<StringValue, AV> build(final FirstOrderFunctionSignature<BooleanValue> functionSignature, final Datatype<StringValue> paramType0, final Datatype<AV> paramType1)
		{
			return new RegexpMatchCallFactory(functionSignature, paramType1);
		}

	}

}
