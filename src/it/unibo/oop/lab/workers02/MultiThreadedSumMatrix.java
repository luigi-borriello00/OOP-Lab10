package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nThreads;

    public MultiThreadedSumMatrix(final int nThreads) {
        super();
        this.nThreads = nThreads;
    }

    private static class Worker extends Thread {
        private final double [][] matrix;
        private final int startpos;
        private final int nelem;
        private double res;

        /**
         * Build a new worker.
         * 
         * @param list
         *            the list to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of elems to sum up for this worker
         */
        Worker(final double [][] matrix, final int startpos, final int nelem) {
            super();
            this.matrix = Arrays.copyOf(matrix, matrix.length);
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for (int i = startpos; i < this.matrix.length && i < startpos + nelem; i++) {
                for (final double elem : this.matrix[i]) {
                    this.res += elem;
                }
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public double getResult() {
            return this.res;
        }

    }

    public double sum(final double[][] matrix) {
        final int size = matrix.length % nThreads + matrix.length / nThreads;
        final List<Worker> workList = new ArrayList<>(nThreads);
        double finalSum = 0;
        for (int start = 0; start < matrix.length; start += size) {
            workList.add(new Worker(matrix, start, size));
        }
        for (final Worker worker : workList) {
            worker.start();
            try {
                worker.join();
                finalSum += worker.getResult();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return finalSum;
    }
}
