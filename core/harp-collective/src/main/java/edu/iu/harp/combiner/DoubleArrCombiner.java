package edu.iu.harp.combiner;

import edu.iu.harp.partition.PartitionCombiner;
import edu.iu.harp.partition.PartitionStatus;
import edu.iu.harp.resource.DoubleArray;

/**
 * Combine two double arrays according to a operation specified.
 * Supported operations are
 *     SUM,
 *     MINUS,
 *     MULTIPLY,
 *     MAX,
 *     MIN
 */
public class DoubleArrCombiner extends PartitionCombiner<DoubleArray> {
  private Operation operation;

  public DoubleArrCombiner(Operation operation) {
    this.operation = operation;
  }

  @Override
  public PartitionStatus combine(
      DoubleArray curPar, DoubleArray newPar) {
    double[] arr1 = curPar.get();
    int size1 = curPar.size();
    double[] arr2 = newPar.get();
    int size2 = newPar.size();
    if (size1 != size2) {
      // throw new Exception("size1: " + size1
      // + ", size2: " + size2);
      return PartitionStatus.COMBINE_FAILED;
    }

    switch (operation) {
      case SUM:
        for (int i = 0; i < size2; i++) {
          arr1[i] += arr2[i];
        }
        break;
      case MINUS:
        for (int i = 0; i < size2; i++) {
          arr1[i] -= arr2[i];
        }
        break;
      case MAX:
        for (int i = 0; i < size2; i++) {
          if (arr1[i] < arr2[i]) {
            arr1[i] = arr2[i];
          }
        }
        break;
      case MIN:
        for (int i = 0; i < size2; i++) {
          if (arr1[i] > arr2[i]) {
            arr1[i] = arr2[i];
          }
        }
        break;
      case MULTIPLY:
        for (int i = 0; i < size2; i++) {
          arr1[i] *= arr2[i];
        }
        break;
    }
    return PartitionStatus.COMBINED;
  }
}
