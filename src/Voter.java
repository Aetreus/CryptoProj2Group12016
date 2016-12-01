import java.math.BigInteger;
import java.util.Random;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

/**
 * Created by james on 11/30/16.
 */
public class Voter {

  public Voter() {
  }

  public final ElectionBoard electionBoard;
  private BigInteger r;
  public static Random random;
  
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
    BigInteger[] encryptedVote = new BigInteger[plaintextVote.length];
    for(int i = 0; i < plaintextVote.length; i++)  {
      encryptedVote[i] = electionBoard.publicEncryption[i].encrypt(plaintextVote[i]);
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
}
