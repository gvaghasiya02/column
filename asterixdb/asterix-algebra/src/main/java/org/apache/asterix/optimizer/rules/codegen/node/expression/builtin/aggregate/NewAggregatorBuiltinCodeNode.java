/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.asterix.optimizer.rules.codegen.node.expression.builtin.aggregate;

import org.apache.asterix.optimizer.rules.codegen.node.CodeGenTemplates;
import org.apache.asterix.optimizer.rules.codegen.node.ICodeNode;
import org.apache.asterix.optimizer.rules.codegen.node.expression.builtin.AbstractBuiltinFunction;
import org.apache.hyracks.api.exceptions.SourceLocation;

public class NewAggregatorBuiltinCodeNode extends AbstractBuiltinFunction {
    private static final long serialVersionUID = -1300590989702641543L;

    public NewAggregatorBuiltinCodeNode(SourceLocation sourceLocation, ICodeNode... args) {
        super(sourceLocation, CodeGenTemplates.appendResultWriter(args));
    }

    @Override
    protected String getRawName() {
        return "newAggregator";
    }
}