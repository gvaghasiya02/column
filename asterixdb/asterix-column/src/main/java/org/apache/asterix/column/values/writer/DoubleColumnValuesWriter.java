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
package org.apache.asterix.column.values.writer;

import java.io.IOException;

import org.apache.asterix.column.bytes.encoder.ParquetPlainValuesWriter;
import org.apache.asterix.column.values.IColumnValuesReader;
import org.apache.asterix.column.values.writer.filters.AbstractColumnFilterWriter;
import org.apache.asterix.column.values.writer.filters.DoubleColumnFilterWriter;
import org.apache.asterix.om.types.ATypeTag;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.hyracks.api.exceptions.HyracksDataException;
import org.apache.hyracks.data.std.api.IValueReference;
import org.apache.hyracks.storage.am.lsm.btree.column.api.IColumnWriteMultiPageOp;
import org.apache.hyracks.storage.am.lsm.btree.column.utils.UnsafeUtil;
import org.apache.parquet.bytes.BytesInput;

public final class DoubleColumnValuesWriter extends AbstractColumnValuesWriter {
    private final ParquetPlainValuesWriter doubleWriter;

    public DoubleColumnValuesWriter(Mutable<IColumnWriteMultiPageOp> multiPageOpRef, int columnIndex, int level,
            boolean collection, boolean filtered) {
        super(columnIndex, level, collection, filtered);
        doubleWriter = new ParquetPlainValuesWriter(multiPageOpRef);
    }

    @Override
    protected void addValue(ATypeTag tag, IValueReference value) throws IOException {
        final double normalizedDouble = getValue(tag, value.getByteArray(), value.getStartOffset());
        doubleWriter.writeDouble(normalizedDouble);
        filterWriter.addDouble(normalizedDouble);
    }

    private double getValue(ATypeTag typeTag, byte[] byteArray, int offset) {
        switch (typeTag) {
            case TINYINT:
                return byteArray[offset];
            case SMALLINT:
                return UnsafeUtil.getShort(byteArray, offset);
            case INTEGER:
                return UnsafeUtil.getInt(byteArray, offset);
            case BIGINT:
                return UnsafeUtil.getLong(byteArray, offset);
            case FLOAT:
                return UnsafeUtil.getFloat(byteArray, offset);
            case DOUBLE:
                return UnsafeUtil.getDouble(byteArray, offset);
            default:
                throw new IllegalAccessError(typeTag + "is not of floating type");
        }
    }

    @Override
    protected void resetValues() throws HyracksDataException {
        doubleWriter.reset();
    }

    @Override
    protected BytesInput getBytes() throws IOException {
        return doubleWriter.getBytes();
    }

    @Override
    protected int getValuesEstimatedSize() {
        return doubleWriter.getEstimatedSize();
    }

    @Override
    protected int getValuesAllocatedSize() {
        return doubleWriter.getAllocatedSize();
    }

    @Override
    protected void addValue(IColumnValuesReader reader) throws IOException {
        double value = reader.getDouble();
        doubleWriter.writeDouble(value);
        filterWriter.addDouble(value);
    }

    @Override
    protected AbstractColumnFilterWriter createFilter() {
        return new DoubleColumnFilterWriter();
    }

    @Override
    protected void closeValues() {
        doubleWriter.close();
    }

    @Override
    protected ATypeTag getTypeTag() {
        return ATypeTag.DOUBLE;
    }
}
