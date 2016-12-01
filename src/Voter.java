import paillierp.Paillier;
import paillierp.zkp.EncryptionZKP;

import java.math.BigInteger;
import java.util.Random;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

/**
 * Created by james on 11/30/16.
 */
public class Voter {

  private ElectionBoard electionBoard;
  private BigInteger r;
  public static Random random;
  private BigInteger[] plainVote;
  private BigInteger[] encryptRands;
  private BigInteger zkpr;
  private BigInteger zkps;
  
  static {
    try {
      random = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      random=new Random();
    }
  }
  
  public Voter(ElectionBoard board)  {
    electionBoard = board;
  }
  
  public BigInteger[] encryptForBlindSign(BigInteger[] encryptedVote) {
    BigInteger[] obscuredVote = new BigInteger[encryptedVote.length];
    do{
      r = new BigInteger(128,electionBoard.random);
    }while(!(r.gcd(electionBoard.modulus).equals(BigInteger.ONE)));
    for(int i = 0; i < encryptedVote.length; i++){
      obscuredVote[i] = encryptedVote[i].multiply(r.modPow(electionBoard.publicExponent,electionBoard.modulus)).mod(electionBoard.modulus);
    }
    return obscuredVote;
  }
  
  public BigInteger[] encryptVote(BigInteger[] plaintextVote)  {
    plainVote = plaintextVote;
    BigInteger[] encryptedVote = new BigInteger[plaintextVote.length];
    for(int i = 0; i < plaintextVote.length; i++)  {
      BigInteger N = electionBoard.publicEncryption[i].getPublicKey().getN();
      do{
        encryptRands[i] = new BigInteger(N.bitLength(), random);
      }while(encryptRands[i].compareTo(N) > 0);
      encryptedVote[i] = electionBoard.publicEncryption[i].encrypt(plaintextVote[i],encryptRands[i]);
    }
    return encryptedVote;
  }

  public BigInteger[] partiallyBlindSignedVote(BigInteger[] blindSignedVote) {
    BigInteger[] signedVote = new BigInteger[blindSignedVote.length];
    for(int i = 0; i < blindSignedVote.length; i++){
      signedVote[i] = blindSignedVote[i].divide(r);
    }
    return signedVote;
  }
  
  public BigInteger initZKP(int i){
    BigInteger N = electionBoard.publicEncryption[i].getPublicKey().getN();
    BigInteger NSquared = N.multiply(N);
    BigInteger g = N.add(BigInteger.ONE);
    do{
      zkpr = new BigInteger(N.bitLength(), random);
    }while(zkpr.compareTo(N) > 0);
    do{
      zkps = new BigInteger(N.bitLength(), random);
    }while(zkps.compareTo(N) > 0);
    return g.modPow(zkpr,NSquared).multiply(zkps.modPow(N,NSquared)).mod(NSquared);
  }
  
  public BigInteger getZKPV(BigInteger e,int i){
    BigInteger N = electionBoard.publicEncryption[i].getPublicKey().getN();
    BigInteger NSquared = N.multiply(N);
    return zkpr.add(e.multiply(plainVote[i])).mod(NSquared);
  }
  
  public BigInteger getZKPW(BigInteger e,int i){
    BigInteger N = electionBoard.publicEncryption[i].getPublicKey().getN();
    BigInteger NSquared = N.multiply(N);
    return zkps.multiply(encryptRands[i].modPow(e,NSquared)).mod(NSquared);
  }
}
