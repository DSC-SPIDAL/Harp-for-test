/*
 * Copyright 2013-2017 Indiana University
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

package edu.iu.harp.resource;

import edu.iu.harp.io.DataType;
import edu.iu.harp.resource.Array;

import java.io.DataOutput;
import java.io.IOException;

/*******************************************************
 * ByteArray class for managing float[] data.
 ******************************************************/
public final class FloatArray
  extends Array<float[]> {

  public FloatArray(float[] arr, int start,
    int size) {
    super(arr, start, size);
  }

  /**
   * Get the number of Bytes of encoded data. One
   * byte for storing DataType, four bytes for
   * storing the size, size*4 bytes for storing
   * the data.
   */
  @Override
  public int getNumEnocdeBytes() {
    return size * 4 + 5;
  }

  /**
   * Encode the array as DataOutput
   */
  @Override
  public void encode(DataOutput out)
    throws IOException {
    out.writeByte(DataType.FLOAT_ARRAY);
    int len = start + size;
    out.writeInt(size);
    for (int i = start; i < len; i++) {
      out.writeFloat(array[i]);
    }
  }

  /**
   * Create an array. Firstly try to get an array
   * from ResourcePool; if failed, new an array.
   * 
   * @param len
   * @param approximate
   * @return
   */
  public static FloatArray create(int len,
    boolean approximate) {
    if (len > 0) {
      float[] floats =
        ResourcePool.get().getFloatsPool()
          .getArray(len, approximate);
      if (floats != null) {
        return new FloatArray(floats, 0, len);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  /**
   * Release the array from the ResourcePool
   */
  @Override
  public void release() {
    ResourcePool.get().getFloatsPool()
      .releaseArray(array);
    this.reset();
  }

  /**
   * Free the array from the ResourcePool
   */
  @Override
  public void free() {
    ResourcePool.get().getFloatsPool()
      .freeArray(array);
    this.reset();
  }
}
