import java.math.BigInteger;

/**
 * Created by james on 11/30/16.
 */
public class Voter {

  public Voter() {
  }

  public BigInteger[] encryptForBlindSign(BigInteger[] encryptedVote) {
    return new BigInteger[encryptedVote.length];
  }

  public BigInteger[] partiallyBlindSignedVote(BigInteger[] blindSignedVote) {
    return new BigInteger[blindSignedVote.length];
  }
}
