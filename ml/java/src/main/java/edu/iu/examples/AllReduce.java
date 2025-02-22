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
package edu.iu.examples;

import edu.iu.harp.example.DoubleArrPlus;
import edu.iu.harp.partition.Partition;
import edu.iu.harp.partition.Table;
import edu.iu.harp.resource.DoubleArray;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import org.jetbrains.annotations.NotNull;

/**
 * This example demonstrate the use of AllReduce collective operation
 */
public class AllReduce extends BaseExampleMapper {
  @Override
  protected void mapCollective(KeyValReader reader, Context context) {
    int numTasks = getNumWorkers();
    // lets prepare the data to be reduced
    Table<DoubleArray> mseTable = createTable();

    long startTime = System.currentTimeMillis();
    double expectedSum = numTasks;
    // we are going to call the same operation num iterations times
    for (int i = 0; i < numIterations; i++) {
      // When calling the operation, each invocation should have a unique
      // operation name, otherwise the callsmay not complete
      allreduce("main", "all-reduce-" + i , mseTable);

      if (verify) {
        expectedSum = verify(numTasks, mseTable, expectedSum);
      }
    }
    LOG.info(String.format("Op %s it %d ele %d par %d time %d", cmd, numIterations,
        elements, numPartitions, (System.currentTimeMillis() - startTime)));
  }

  /**
   * Verify the results of all reduce operation
   * @param numTasks num tasks
   * @param mseTable table
   * @param expectedSum expected sum
   * @return next expected sum
   */
  private double verify(int numTasks, Table<DoubleArray> mseTable, double expectedSum) {
    ObjectCollection<Partition<DoubleArray>> partitions = mseTable.getPartitions();
    for (Partition<DoubleArray> p : partitions) {
      double[] dArray = p.get().get();
      for (double d : dArray) {
        if (d != expectedSum) {
          throw new RuntimeException("Un-expected value, expected: " + expectedSum + " got: " + d);
        }
      }
    }
    expectedSum = expectedSum * numTasks;
    LOG.info("Verification success");
    return expectedSum;
  }

  @NotNull
  private Table<DoubleArray> createTable() {
    Table<DoubleArray> mseTable = new Table<>(0, new DoubleArrPlus());
    for (int j = 0; j < numPartitions; j++) {
      double[] values = new double[elements];
      for (int k = 0; k < values.length; k++) {
        values[k] = 1;
      }
      mseTable.addPartition(new Partition<>(0, new DoubleArray(values, 0, elements)));
    }
    return mseTable;
  }
}
