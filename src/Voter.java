import java.math.BigInteger;

/**
 * Created by james on 11/30/16.
 */
public class Voter {

  public Voter() {
  }

  public final ElectionBoard electionBoard;
  
  public Voter(ElectionBoard board)  {
    electionBoard = board;
  }
  
  public BigInteger[] encryptForBlindSign(BigInteger[] encryptedVote) {
    return new BigInteger[encryptedVote.length];
  }
  
  public BigInteger[] encryptVote(BigInteger[] plaintextVote)  {
    BigInteger[] encryptedVote = new BigInteger[plaintextVote.length];
    for(int i = 0; i < plaintextVote.length; i++)  {
      encryptedVote[i] = electionBoard.publicEncryption[i].encrypt(plaintextVote[i]);
    }
    return encryptedVote;
  }

  public BigInteger[] partiallyBlindSignedVote(BigInteger[] blindSignedVote) {
    return new BigInteger[blindSignedVote.length];
  }
}
