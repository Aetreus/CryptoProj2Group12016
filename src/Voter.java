import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by james on 11/30/16.
 */
public class Voter {

  private ElectionBoard electionBoard;
  private BigInteger r;
  private BigInteger r_inverse;
  public static Random random;
  private BigInteger[] plainVote;
  private BigInteger[] encryptRands;
  private BigInteger zkpr;
  private BigInteger zkps;
  
  static {
    try {
      random = SecureRandom.getInstance("NativePRNGNonBlocking");
    } catch (NoSuchAlgorithmException e) {
      System.err.println("Failure to get a secure random number generator.");
      random=new Random();
    }
  }
  
  public Voter(ElectionBoard board)  {
    electionBoard = board;
  }
  
  public BigInteger[] encryptForBlindSign(BigInteger[] encryptedVote) {
    BigInteger[] obscuredVote = new BigInteger[encryptedVote.length];
    do{
      r = new BigInteger(512,electionBoard.random);
    }while(!(r.gcd(electionBoard.modulus).equals(BigInteger.ONE)));
    r_inverse=r.modInverse(electionBoard.modulus);
    for(int i = 0; i < encryptedVote.length; i++){
      obscuredVote[i] = encryptedVote[i].multiply(r.modPow(electionBoard.publicExponent,electionBoard.modulus)).mod(electionBoard.modulus);
    }
    return obscuredVote;
  }
  
  public BigInteger[] encryptVote(BigInteger[] plaintextVote)  {
    plainVote = plaintextVote;
    encryptRands=new BigInteger[plaintextVote.length];//Store x for the ZKP stage
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

  public BigInteger[] unBlindSignedVote(BigInteger[] blindSignedVote) {//Reverse obscuration of the signature
    BigInteger[] signedVote = new BigInteger[blindSignedVote.length];
    for(int i = 0; i < blindSignedVote.length; i++){
      signedVote[i] = blindSignedVote[i].multiply(r_inverse).mod(electionBoard.modulus);
    }
    return signedVote;
  }
  
  public BigInteger initZKP(int i){//Calculates u and stores r and s
    BigInteger N = electionBoard.publicEncryption[i].getPublicKey().getN();
    BigInteger NSquared = N.pow(2);
    BigInteger g = electionBoard.publicEncryption[i].getPublicKey().getNPlusOne();
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
    BigInteger NSquared = N.pow(2);
    return zkpr.subtract(e.multiply(plainVote[i])).mod(NSquared);
  }
  
  public BigInteger getZKPW(BigInteger e,int i){
    BigInteger N = electionBoard.publicEncryption[i].getPublicKey().getN();
    BigInteger NSquared = N.pow(2);
    return zkps.multiply(encryptRands[i].modPow(e.negate(),NSquared)).mod(NSquared);
  }
}
