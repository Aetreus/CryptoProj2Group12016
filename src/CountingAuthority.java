import java.math.BigInteger;
import java.util.ArrayList;
/**
 * Created by James on 12/1/2016.
 */

public class CountingAuthority {
  public static final class CAToken { private CAToken() {} }
  private static final CAToken token = new CAToken();
  
  private ElectionBoard electionBoard;
  
  public CountingAuthority(ElectionBoard board){
    electionBoard = board;
  }
  
  public BigInteger[] combineVotes(ArrayList<BigInteger[]> votes) throws ElectionBoardError
  {
    BigInteger[] summation = votes.get(0);
    for(int j = 1; j < votes.size(); j++){
      BigInteger[] source = votes.get(j);
      for(int i = 0; i < summation.length; i++){
        summation[i] = electionBoard.publicEncryption[i].add(summation[i],source[i]);
      }
    }
    return electionBoard.decrypt(summation,token);
  }
}
