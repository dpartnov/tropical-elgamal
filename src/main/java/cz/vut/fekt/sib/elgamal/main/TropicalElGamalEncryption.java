package cz.vut.fekt.sib.elgamal.main;

import cz.vut.fekt.sib.elgamal.utils.Utils;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static cz.vut.fekt.sib.elgamal.utils.Utils.splitStringToMatrices;


public class TropicalElGamalEncryption
{
    public static void main( String[] args ) {

        // Public parameters
        final int publicPrimeNumber = 29;
        System.out.println("Public prime number p: " + publicPrimeNumber);
        final RealMatrix publicMatrix = MatrixUtils.createRealMatrix(new double[][]{{51.0, 93.0}, {41.0, 79.0}});
        System.out.println("Public matrix G:");
        Utils.printMatrix(publicMatrix);

        // message
        final String text = "Eve wants to kill you";
        System.out.println("Plain text: " + text + "\n");
        final LinkedList<RealMatrix> messages = Utils.splitStringToMatrices(text);




        // 1. Alice - Key generation
        // Alice chooses a random integer x
        final int x = 6;
        // Alice computes public key U = G^x mod p and sends it to Bob.
        final RealMatrix alicePublicKey = Utils.modulo(publicMatrix.power(x), publicPrimeNumber);
        System.out.println("Alice public key (U):");
        Utils.printMatrix(alicePublicKey);




        // 2. Bob - Key generation
        // Bob chooses a random integer y
        final int y = 2;

        // Bob computes public key V
        final RealMatrix bobPublicKey = Utils.modulo(publicMatrix.power(y), publicPrimeNumber);
        System.out.println("Bob public key (V):");
        Utils.printMatrix(bobPublicKey);

        // Bob computes private key K = U^y
        final RealMatrix bobPrivateKey = Utils.modulo(alicePublicKey.power(y), publicPrimeNumber);
        System.out.println("Bob private key K = U^y mod p:");
        Utils.printMatrix(bobPrivateKey);

        // Bob change private key matrix to diagonal matrix
        final RealMatrix diagonalMatrixS = Utils.convertToDiagonalMatrix(bobPrivateKey);
        System.out.println("Diagonal matrix S:");
        Utils.printMatrix(diagonalMatrixS);




        // 3. Encryption - Bob wants to send message
        final LinkedList<RealMatrix> encryptedMessages = new LinkedList<>();
        messages.stream().forEachOrdered(message -> {
            // Bob computes c = m + S for all messages
            encryptedMessages.add(Utils.addWithDiagonalMatrix(message, diagonalMatrixS));
        });

        System.out.println("Encrypted matrices:");
        encryptedMessages.stream().forEachOrdered(Utils::printMatrix);
        System.out.println("Encrypted text: " + Utils.convertToText(encryptedMessages));




        // 4. Decryption
        // Alice computes private key for decryption K = V^x mod p
        final RealMatrix alicePrivateKey = Utils.modulo(bobPublicKey.power(x), publicPrimeNumber);
        System.out.println("Alice private key = V^x mod p:");
        Utils.printMatrix(alicePrivateKey);

        // Alice transform private key into diagonal matrix
        final RealMatrix aliceDiagonalMatrixS = Utils.convertToDiagonalMatrix(alicePrivateKey);
        System.out.println("Alice diagonal matrix S:");
        Utils.printMatrix(aliceDiagonalMatrixS);

        // Alice decrypts the cipher matrices: m = c - S and then reveal the message from Bob
        final LinkedList<RealMatrix> decryptedMatrices = new LinkedList<>();
        encryptedMessages.stream().forEachOrdered(cipherMatrix -> {
            decryptedMatrices.add(Utils.minusWithDiagonalMatrix(cipherMatrix, aliceDiagonalMatrixS));
        });

        System.out.println("Decrypted matrices:");
        decryptedMatrices.stream().forEachOrdered(Utils::printMatrix);
        System.out.println("Decrypted text: " + Utils.convertToText(decryptedMatrices));
    }
}
