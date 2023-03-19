package org.example;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;


public class TropicalElGamalEncryption
{
    public static void main( String[] args ) {

        // Public parameters:
        final int primeNum = 29;
        System.out.println("Public prime number P: " + primeNum);
        final RealMatrix publicMatrixG = MatrixUtils.createRealMatrix(new double[][]{{51.0, 93.0}, {41.0, 79.0}});
        System.out.println("Public matrix G:");
        printMatrix(publicMatrixG);

        // 1. Alice ( Key generation ):
        //Alice chooses a random integer x
        final int x = 6;
        // Alice computes U = G⊗x mod p and sends it to Bob.
        final RealMatrix afterPower = publicMatrixG.power(x);
        final RealMatrix aliceMatrixU = modulo(afterPower, primeNum);
        System.out.println("Alice matrix U = G⊗x mod p:");
        printMatrix(aliceMatrixU);

        // 2. Encryption
        final int y = 2;
        // Bob computes KB = U⊗y
        final RealMatrix bobMatrixK = modulo(aliceMatrixU.power(y), primeNum);
        System.out.println("Bob matrix KB = U⊗y mod p ( Private key ):");
        printMatrix(bobMatrixK);

        // message:
        final RealMatrix m = MatrixUtils.createRealMatrix(new double[][]{{65.0, 47.0}, {28.0, 72.0}});
        System.out.println("Plain text matrix:");
        printMatrix(m);

        // Bob's public key:
        final RealMatrix bobPublicKeyV = modulo(publicMatrixG.power(y), primeNum);
        System.out.println("Bob public key matrix:");
        printMatrix(bobPublicKeyV);

        final RealMatrix diagonalMatrixS = convertToDiagonalMatrix(bobMatrixK);
        System.out.println("Diagonal matrix S:");
        printMatrix(diagonalMatrixS);

        final RealMatrix cipherMatrix = addWithInfinity(m, diagonalMatrixS);
        System.out.println("Cipher matrix C:");
        printMatrix(cipherMatrix);


        // Decryption
        // Alice then computes KA = V⊗x mod p:
        final RealMatrix alicePrivateKey = modulo(bobPublicKeyV.power(x), primeNum);
        System.out.println("Alice private key = V⊗x mod p:");
        printMatrix(alicePrivateKey);

        // Alice transform S into diagonal matrix
        final RealMatrix aliceDiagonalMatrixS = convertToDiagonalMatrix(alicePrivateKey);
        System.out.println("Alice diagonal matrix S:");
        printMatrix(aliceDiagonalMatrixS);

        // Alice decrypts the cipher text:
        final RealMatrix decryptedMatrix = minusWithInfinity(cipherMatrix, aliceDiagonalMatrixS);
        System.out.println("Decrypted matrix:");
        printMatrix(decryptedMatrix);

    }

    private static RealMatrix modulo(final RealMatrix matrix, int modValue) {
        final RealMatrix result = matrix.copy();
        for (int i = 0; i < result.getRowDimension(); i++) {
            for (int j = 0; j < result.getColumnDimension(); j++) {
                double element = result.getEntry(i, j);
                double modElement = element % modValue;
                result.setEntry(i, j, modElement);
            }
        }

        return result;
    }

    private static RealMatrix convertToDiagonalMatrix(final RealMatrix matrix) {
        int dimension = matrix.getRowDimension();
        final RealMatrix result = matrix.copy();
        for (int i = 0; i < dimension; i++) {
            result.setEntry(i, i, Double.NEGATIVE_INFINITY);
        }
        return result;
    }

    public static RealMatrix addWithInfinity(RealMatrix realMatrix, RealMatrix diagonalMatrix) {
        int rows = realMatrix.getRowDimension();
        int cols = realMatrix.getColumnDimension();
        final RealMatrix result = MatrixUtils.createRealMatrix(rows, cols);
        final RealMatrix diagonalMatrixCopy = diagonalMatrix.copy();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double val1 = realMatrix.getEntry(i, j);
                double val2 = diagonalMatrixCopy.getEntry(i, j);
                if (Double.isInfinite(val2) && j == i) {
                    diagonalMatrixCopy.setEntry(i, j, 0);
                    double colSum = 0;
                    for (int k = 0; k < rows; k++) {
                        colSum += diagonalMatrixCopy.getEntry(k, j);
                    }
                    val2 = colSum;
                }
                result.setEntry(i, j, val1 + val2);
            }
        }

        return result;
    }

    public static RealMatrix minusWithInfinity(RealMatrix realMatrix, RealMatrix diagonalMatrix) {
        int rows = realMatrix.getRowDimension();
        int cols = realMatrix.getColumnDimension();
        final RealMatrix result = MatrixUtils.createRealMatrix(rows, cols);
        final RealMatrix diagonalMatrixCopy = diagonalMatrix.copy();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double val1 = realMatrix.getEntry(i, j);
                double val2 = diagonalMatrixCopy.getEntry(i, j);
                if (Double.isInfinite(val2) && j == i) {
                    diagonalMatrixCopy.setEntry(i, j, 0);
                    double colSum = 0;
                    for (int k = 0; k < rows; k++) {
                        colSum += diagonalMatrixCopy.getEntry(k, j);
                    }
                    val2 = colSum;
                }
                result.setEntry(i, j, val1 - val2);
            }
        }

        return result;
    }

    private static void printMatrix(RealMatrix matrix) {
        double[][] data = matrix.getData();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                System.out.printf("%6.2f ", data[i][j]);
            }
            System.out.println();
        }
        System.out.println("\n");
    }
}
