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
package org.apache.asterix.column.values.reader;

import java.io.IOException;

import org.apache.asterix.column.metadata.schema.collection.ArraySchemaNode;
import org.apache.asterix.column.values.IColumnValuesWriter;
import org.apache.asterix.column.values.reader.value.AbstractValueReader;
import org.apache.hyracks.api.exceptions.HyracksDataException;

/**
 * For primitive values that belong to an {@link ArraySchemaNode}
 */
public final class RepeatedPrimitiveColumnValuesReader extends AbstractColumnValuesReader {
    private final int[] delimiters;
    private final int[] levelToDelimiterMap;
    private int delimiterIndex;

    RepeatedPrimitiveColumnValuesReader(AbstractValueReader valueReader, int columnIndex, int maxLevel,
            int[] delimiters) {
        super(valueReader, columnIndex, maxLevel, false);
        this.delimiters = delimiters;
        delimiterIndex = delimiters.length;

        levelToDelimiterMap = new int[maxLevel + 1];
        int currentDelimiterIndex = 0;
        for (int level = maxLevel; level >= 0; level--) {
            if (currentDelimiterIndex < delimiters.length && level == delimiters[currentDelimiterIndex]) {
                currentDelimiterIndex++;
            }
            levelToDelimiterMap[level] = currentDelimiterIndex;
        }
    }

    @Override
    protected void resetValues() {
        delimiterIndex = delimiters.length;
    }

    @Override
    public boolean next() throws HyracksDataException {
        if (valueIndex == valueCount) {
            return false;
        }

        consumeDelimiterIfAny();
        nextLevel();
        setDelimiterIndex();
        if (level == maxLevel) {
            valueReader.nextValue();
        }
        valueIndex++;
        return true;
    }

    @Override
    public boolean isRepeated() {
        return delimiterIndex < delimiters.length && level > delimiters[delimiterIndex];
    }

    @Override
    public boolean isDelimiter() {
        return delimiterIndex < delimiters.length && level == delimiters[delimiterIndex];
    }

    @Override
    public int getDelimiterIndex() {
        return delimiterIndex;
    }

    @Override
    public boolean isLastDelimiter() {
        return isDelimiter() && delimiterIndex == delimiters.length - 1;
    }

    @Override
    public void write(IColumnValuesWriter writer, boolean callNext) throws HyracksDataException {
        //We always call next as repeated values cannot be primary keys
        if (!next()) {
            throw new IllegalStateException("No more values");
        }

        if (isRepeatedValue()) {
            while (!isLastDelimiter()) {
                writer.writeLevel(level);
                if (isValue()) {
                    try {
                        writer.writeValue(this);
                    } catch (IOException e) {
                        throw HyracksDataException.create(e);
                    }
                }
                next();
            }
        }
        //Add last delimiter, or NULL/MISSING
        writer.writeLevel(level);
    }

    private boolean isRepeatedValue() {
        return levelToDelimiterMap[level] < delimiters.length;
    }

    private void consumeDelimiterIfAny() {
        if (isDelimiter()) {
            delimiterIndex++;
        }
    }

    private void setDelimiterIndex() {
        if (isDelimiter() || level <= delimiters[delimiters.length - 1]) {
            return;
        }
        delimiterIndex = levelToDelimiterMap[level];
    }
}
