package cz.vut.fekt.sib.elgamal.utils;

import org.apache.commons.math3.linear.RealMatrix;

public class Utils {

    public static RealMatrix modulo(final RealMatrix matrix, int modValue) {
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

    public static RealMatrix convertToDiagonalMatrix(final RealMatrix matrix) {
        int dimension = matrix.getRowDimension();
        final RealMatrix result = matrix.copy();
        for (int i = 0; i < dimension; i++) {
            result.setEntry(i, i, Double.NEGATIVE_INFINITY);
        }
        return result;
    }

    public static RealMatrix addWithDiagonalMatrix(RealMatrix realMatrix, RealMatrix diagonalMatrix) {
        int rows = realMatrix.getRowDimension();
        int cols = realMatrix.getColumnDimension();
        final RealMatrix result = org.apache.commons.math3.linear.MatrixUtils.createRealMatrix(rows, cols);
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
        final RealMatrix result = org.apache.commons.math3.linear.MatrixUtils.createRealMatrix(rows, cols);
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

    public static RealMatrix[] splitStringToMatrices(String input) {
        int numBlocks = (int) Math.ceil(input.length() / 4.0);
        RealMatrix[] matrices = new RealMatrix[numBlocks];

        // Pad the input string with spaces to ensure its length is a multiple of 4
        input = String.format("%-" + (numBlocks * 4) + "s", input);

        for (int i = 0; i < numBlocks; i++) {
            String block = input.substring(i * 4, (i + 1) * 4);
            RealMatrix matrix = org.apache.commons.math3.linear.MatrixUtils.createRealMatrix(2, 2);

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

    public static String convertToText(RealMatrix[] matrices) {
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

    public static void printMatrix(RealMatrix matrix) {
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
