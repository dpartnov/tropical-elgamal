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

        // message:
        //final RealMatrix m = MatrixUtils.createRealMatrix(new double[][]{{65.0, 47.0}, {28.0, 72.0}});
        final String text = "Eve wants to kill you";
        System.out.println("Plain text: Eve want to kill you");
        final RealMatrix[] messages = splitStringToMatrices(text);

        // 1. Alice ( Key generation ):
        //Alice chooses a random integer x
        final int x = 6;
        // Alice computes U = G⊗x mod p and sends it to Bob.
        final RealMatrix afterPower = publicMatrixG.power(x);
        final RealMatrix alicePublicKey = modulo(afterPower, primeNum);
        System.out.println("Alice public key U = G⊗x mod p:");
        printMatrix(alicePublicKey);

        // 2. Encryption
        final int y = 2;
        // Bob computes KB = U⊗y
        final RealMatrix bobMatrixK = modulo(alicePublicKey.power(y), primeNum);
        System.out.println("Bob matrix KB = U⊗y mod p ( Private key ):");
        printMatrix(bobMatrixK);


        // Bob's public key:
        final RealMatrix bobPublicKey = modulo(publicMatrixG.power(y), primeNum);
        System.out.println("Bob public key matrix:");
        printMatrix(bobPublicKey);

        final RealMatrix diagonalMatrixS = convertToDiagonalMatrix(bobMatrixK);
        System.out.println("Diagonal matrix S:");
        printMatrix(diagonalMatrixS);

        final RealMatrix[] cipherMessages = new RealMatrix[messages.length];
        int index = 0;
        for (RealMatrix m : messages) {
            cipherMessages[index] = addWithInfinity(m, diagonalMatrixS);
            index++;
        }

        System.out.println("Cipher matrices:");
        for (RealMatrix matrix : cipherMessages) {
            printMatrix(matrix);
        }

        System.out.println("Encrypted text: " + convertToText(cipherMessages));


        // Decryption
        // Alice then computes KA = V⊗x mod p:
        final RealMatrix alicePrivateKey = modulo(bobPublicKey.power(x), primeNum);
        System.out.println("Alice private key = V⊗x mod p:");
        printMatrix(alicePrivateKey);

        // Alice transform S into diagonal matrix
        final RealMatrix aliceDiagonalMatrixS = convertToDiagonalMatrix(alicePrivateKey);
        System.out.println("Alice diagonal matrix S:");
        printMatrix(aliceDiagonalMatrixS);

        System.out.println("Decrypted matrices:");
        int index2 = 0;
        final RealMatrix[] decryptedMessages = new RealMatrix[cipherMessages.length];
        // Alice decrypts the cipher text:
        for (RealMatrix matrix : cipherMessages) {
            final RealMatrix decryptedMatrix = minusWithDiagonalMatrix(matrix, aliceDiagonalMatrixS);
            decryptedMessages[index2] = decryptedMatrix;
            printMatrix(decryptedMatrix);
            index2++;
        }

        System.out.println("Decrypted text: " + convertToText(decryptedMessages));

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

    public static RealMatrix minusWithDiagonalMatrix(RealMatrix realMatrix, RealMatrix diagonalMatrix) {
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

    private static RealMatrix[] splitStringToMatrices(String input) {
        int numBlocks = (int) Math.ceil(input.length() / 4.0);
        RealMatrix[] matrices = new RealMatrix[numBlocks];

        // Pad the input string with spaces to ensure its length is a multiple of 4
        input = String.format("%-" + (numBlocks * 4) + "s", input);

        for (int i = 0; i < numBlocks; i++) {
            String block = input.substring(i * 4, (i + 1) * 4);
            RealMatrix matrix = MatrixUtils.createRealMatrix(2, 2);

            int index = 0;
            for (int k = 0; k < 2; k++) {
                for (int j = 0; j < 2; j++) {
                    char c = (index < block.length()) ? block.charAt(index) : ' ';
                    double ascii = (int) c;
                    matrix.setEntry(k, j, ascii);
                    index++;
                }
            }

            matrices[i] = matrix;
        }

        return matrices;
    }

    private static String convertToText(RealMatrix[] matrices) {
        String result = "";

        for (RealMatrix matrix : matrices) {
            double[][] data = matrix.getData();
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[0].length; j++) {
                    result += ((char)(int) data[i][j]);
                }
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
