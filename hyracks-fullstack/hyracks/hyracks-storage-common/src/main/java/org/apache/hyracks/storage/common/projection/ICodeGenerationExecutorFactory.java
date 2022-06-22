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
package org.apache.hyracks.storage.common.projection;

import java.io.Serializable;

import org.apache.hyracks.api.context.IHyracksTaskContext;
import org.apache.hyracks.api.exceptions.HyracksDataException;

/**
 * This enables Hyracks to fuse several operators using an application language that does the processing of the fused
 * operators. A code is generated (in the application language) to replace those fused operators. The generated code
 * is then compiled and executed in each partition.
 */
public interface ICodeGenerationExecutorFactory extends Serializable {
    ICodeGenerationExecutor createExecutor(IHyracksTaskContext context) throws HyracksDataException;
}