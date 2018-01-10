package com.dauphine.bfa.cdr;

import java.util.Random;
import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.NonPositiveDefiniteMatrixException;
import org.apache.commons.math3.linear.NonSquareMatrixException;
import org.apache.commons.math3.linear.NonSymmetricMatrixException;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Box-Muller algo : https://fr.wikipedia.org/wiki/M%C3%A9thode_de_Box-Muller
 *
 * Created by yuyan_gan on 10/01/2018.
 */
public class RandomNumberGenerator {

    /**
     * Generate a random number
     * @return a random number
     */
    public static double generateRandomNumber() {

        final Random randomGenerator = new Random();
        final double u1 = randomGenerator.nextDouble();
        final double u2 = randomGenerator.nextDouble();

        final double number1 = Math.sqrt( -2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
        final double number2 = Math.sqrt( -2 * Math.log(u1)) * Math.sin(2 * Math.PI * u2);


        return number1;
    }

    /**
     * Generate an array of random numbers
     *
     * @param size thr half size of numbers
     * @return a size * 2 array of random numbers
     */
    public static double[] generateRandomNumberArray(final int size) {
        final double[] numbers = new double[size];
        final Random randomGenerator = new Random();

        int numberIdx = 0;
        for (int idx = 0; idx < Math.ceil(size/2.0); idx++) {
            final double u1 = randomGenerator.nextDouble();
            final double u2 = randomGenerator.nextDouble();
            final double number1 = Math.sqrt( -2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
            final double number2 = Math.sqrt( -2 * Math.log(u1)) * Math.sin(2 * Math.PI * u2);

            numbers[numberIdx++] = number1;
            if (numberIdx < numbers.length) {
                numbers[numberIdx++] = number2;
            }
        }

        return numbers;
    }

    /**
     * null
     */
    public static double[] generateCorrelatedRandomNumberArray(final double[][] correlatedMatrixInput, final int numberSize, final Simulator simulator) {
        return generateCorrelatedRandomNumberArray(correlatedMatrixInput, simulator.getRandomArray(numberSize));
    }

    /**
     * generate an array of correlated random numbers
     * @param correlatedMatrixInput input correlated matrix
     * @return an array of random number
     */
    public static double[] generateCorrelatedRandomNumberArray(final double[][] correlatedMatrixInput, final double[] randomNumbers) {
            final RealMatrix realMatrix = MatrixUtils.createRealMatrix(correlatedMatrixInput);

        try {
            final CholeskyDecomposition choleskyDecomposition = new CholeskyDecomposition(realMatrix);
            final RealMatrix lMatrix = choleskyDecomposition.getL();

            //print cholesky matrix

            println("****************************Cholesky matrix start**************************");
            double[][] datas = lMatrix.getData();
            for (double[] data : datas) {
                println("  ");
                for (double number : data) {
                    System.out.print(number + ",");
                }
            }
            println("");
            println("****************************Cholesky matrix end**************************");

            RealMatrix rowRealMatrix = MatrixUtils.createRowRealMatrix(randomNumbers);
            RealMatrix resultMatrix = rowRealMatrix.multiply(lMatrix);

            return resultMatrix.getRow(0);
        } catch (NonSquareMatrixException exception) {
            System.err.println("the matrix is not squared");
        } catch (NonSymmetricMatrixException exception) {
            System.err.println("the matrix is not Symmetric");
        } catch (NonPositiveDefiniteMatrixException exception) {
            System.err.println("the matrix is not positive definite");
        }
        return null;
    }

    private static void println(final String arg) {
        System.out.println(arg);
    }

    private RandomNumberGenerator() {}
}
