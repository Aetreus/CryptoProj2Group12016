import java.math.BigInteger;

/**
 * Created by james on 11/30/16.
 */
public class BulletinBoard {
  
  private ElectionBoard board;
  
  public BulletinBoard(ElectionBoard electionBoard) {
    board = electionBoard;
  }

  public void acceptAndZKPVote(BigInteger[] signedVote, BigInteger[] encryptedVote, Voter voter) throws BulletinBoardError{
    
  }
}
