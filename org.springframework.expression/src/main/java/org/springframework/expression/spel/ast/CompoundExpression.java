/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.expression.spel.ast;

import org.antlr.runtime.Token;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelException;

/**
 * Represents a DOT separated expression sequence, such as 'property1.property2.methodOne()'
 * 
 * @author Andy Clement
 * @since 3.0
 */
public class CompoundExpression extends SpelNodeImpl {

	public CompoundExpression(Token payload) {
		super(payload);
	}

	/**
	 * Evalutes a compound expression. This involves evaluating each piece in turn and the return value from each piece
	 * is the active context object for the subsequent piece.
	 * @param state the state in which the expression is being evaluated
	 * @return the final value from the last piece of the compound expression
	 */
	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		TypedValue result = null;
		SpelNodeImpl nextNode = null;
		try {
			nextNode = getChild(0);
			result = nextNode.getValueInternal(state);
			for (int i = 1; i < getChildCount(); i++) {
				try {
					state.pushActiveContextObject(result);
					nextNode = getChild(i);
					result = nextNode.getValueInternal(state);
				} finally {
					state.popActiveContextObject();
				}
			}
		} catch (SpelException ee) {
			// Correct the position for the error before rethrowing
			ee.setPosition(nextNode.getCharPositionInLine());
			throw ee;
		}
		return result;
	}

	@Override
	public void setValue(ExpressionState state, Object value) throws EvaluationException {
		if (getChildCount() == 1) {
			getChild(0).setValue(state, value);
			return;
		}
		TypedValue ctx = getChild(0).getValueInternal(state);
		for (int i = 1; i < getChildCount() - 1; i++) {
			try {
				state.pushActiveContextObject(ctx);
				ctx = getChild(i).getValueInternal(state);
			} finally {
				state.popActiveContextObject();
			}
		}
		try {
			state.pushActiveContextObject(ctx);
			getChild(getChildCount() - 1).setValue(state, value);
		} finally {
			state.popActiveContextObject();
		}
	}

	@Override
	public boolean isWritable(ExpressionState state) throws EvaluationException {
		if (getChildCount() == 1) {
			return getChild(0).isWritable(state);
		}
		TypedValue ctx = getChild(0).getValueInternal(state);
		for (int i = 1; i < getChildCount() - 1; i++) {
			try {
				state.pushActiveContextObject(ctx);
				ctx = getChild(i).getValueInternal(state);
			} finally {
				state.popActiveContextObject();
			}
		}
		try {
			state.pushActiveContextObject(ctx);
			return getChild(getChildCount() - 1).isWritable(state);
		} finally {
			state.popActiveContextObject();
		}
	}

	@Override
	public String toStringAST() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getChildCount(); i++) {
			sb.append(getChild(i).toStringAST());
		}
		return sb.toString();
	}

}
