package cz.vut.fekt.sib.elgamal.main;

import cz.vut.fekt.sib.elgamal.utils.Utils;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import static cz.vut.fekt.sib.elgamal.utils.Utils.splitStringToMatrices;


public class TropicalElGamalEncryption
{
    public static void main( String[] args ) {

        // Public parameters:
        final int primeNum = 29;
        System.out.println("Public prime number P: " + primeNum);
        final RealMatrix publicMatrixG = MatrixUtils.createRealMatrix(new double[][]{{51.0, 93.0}, {41.0, 79.0}});
        System.out.println("Public matrix G:");
        Utils.printMatrix(publicMatrixG);

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
        final RealMatrix alicePublicKey = Utils.modulo(afterPower, primeNum);
        System.out.println("Alice public key U = G⊗x mod p:");
        Utils.printMatrix(alicePublicKey);

        // 2. Encryption
        final int y = 2;
        // Bob computes KB = U⊗y
        final RealMatrix bobMatrixK = Utils.modulo(alicePublicKey.power(y), primeNum);
        System.out.println("Bob matrix KB = U⊗y mod p ( Private key ):");
        Utils.printMatrix(bobMatrixK);


        // Bob's public key:
        final RealMatrix bobPublicKey = Utils.modulo(publicMatrixG.power(y), primeNum);
        System.out.println("Bob public key matrix:");
        Utils.printMatrix(bobPublicKey);

        final RealMatrix diagonalMatrixS = Utils.convertToDiagonalMatrix(bobMatrixK);
        System.out.println("Diagonal matrix S:");
        Utils.printMatrix(diagonalMatrixS);

        final RealMatrix[] cipherMessages = new RealMatrix[messages.length];
        int index = 0;
        for (RealMatrix m : messages) {
            cipherMessages[index] = Utils.addWithDiagonalMatrix(m, diagonalMatrixS);
            index++;
        }

        System.out.println("Cipher matrices:");
        for (RealMatrix matrix : cipherMessages) {
            Utils.printMatrix(matrix);
        }

        System.out.println("Encrypted text: " + Utils.convertToText(cipherMessages));


        // Decryption
        // Alice then computes KA = V⊗x mod p:
        final RealMatrix alicePrivateKey = Utils.modulo(bobPublicKey.power(x), primeNum);
        System.out.println("Alice private key = V⊗x mod p:");
        Utils.printMatrix(alicePrivateKey);

        // Alice transform S into diagonal matrix
        final RealMatrix aliceDiagonalMatrixS = Utils.convertToDiagonalMatrix(alicePrivateKey);
        System.out.println("Alice diagonal matrix S:");
        Utils.printMatrix(aliceDiagonalMatrixS);

        System.out.println("Decrypted matrices:");
        int index2 = 0;
        final RealMatrix[] decryptedMessages = new RealMatrix[cipherMessages.length];
        // Alice decrypts the cipher text:
        for (RealMatrix matrix : cipherMessages) {
            final RealMatrix decryptedMatrix = Utils.minusWithDiagonalMatrix(matrix, aliceDiagonalMatrixS);
            decryptedMessages[index2] = decryptedMatrix;
            Utils.printMatrix(decryptedMatrix);
            index2++;
        }

        System.out.println("Decrypted text: " + Utils.convertToText(decryptedMessages));

    }
}
