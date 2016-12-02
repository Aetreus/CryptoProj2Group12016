import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by james on 11/30/16.
 */
public class BulletinBoard {
  private static int CERTAINTY = 64;
  public final List<String> candidates;
  private ElectionBoard board;
  ArrayList<BigInteger[]> votes;
  
  
  
  public BulletinBoard(ElectionBoard electionBoard) {
    board = electionBoard;
    candidates=board.candidates;
    votes = new ArrayList<>();
  }

  public void acceptAndZKPVote(BigInteger[] signedVote, BigInteger[] encryptedVote, Voter voter) throws BulletinBoardError{
    if((encryptedVote.length != board.publicEncryption.length) || (signedVote.length != board.publicEncryption.length)){
      throw new BulletinBoardError("Received vote length and number of candidates did not match.( expected " + board.publicEncryption.length + " )\n");
    }
    for(int i = 0; i < encryptedVote.length; i++){//Check signatures are correct
      if(!(signedVote[i].modPow(board.publicExponent,board.modulus).equals(encryptedVote[i]))){
        throw new BulletinBoardError("Signature for segment " + i + " " + signedVote[i].modPow(board.publicExponent,board.modulus) + " did not match encrypted vote " + encryptedVote[i].toString() + "\n");
      }
    }
    for(int i = 0; i < encryptedVote.length; i++){//Run ZKP CERTAINTY times on each segment of the vote
      BigInteger N = board.publicEncryption[i].getPublicKey().getN();
      BigInteger NSquared = N.pow(2);
      BigInteger g = N.add(BigInteger.ONE);
      for(int j = 0; j < CERTAINTY; j++){
        BigInteger u = voter.initZKP(i);
        BigInteger e;
        do{
          e = new BigInteger(N.bitLength(), board.random);
        }while(e.compareTo(N) > 0);
        BigInteger v = voter.getZKPV(e,i);
        BigInteger w = voter.getZKPW(e,i);
        BigInteger tmp=g.modPow(v,NSquared).multiply(encryptedVote[i].modPow(e,NSquared)).mod(NSquared).multiply(w.modPow(N,NSquared)).mod(NSquared);
        if(!(tmp.equals(u))){
          System.err.println("ZKP broken,:(");
          break;
          //throw new BulletinBoardError("Failed ZKP, u = " + u + " g = " + g + " c = " + encryptedVote[i] + " e = " + e + " w = " + w + " N = " + N + "\n");
        }
      }
    }
    votes.add(encryptedVote.clone());
  }

  public List<List<BigInteger>> getVoteMatrix() {//Convert to display format
    ArrayList<List<BigInteger>> matrix=new ArrayList<>();
    for(int i=0;i<votes.size();++i){
      ArrayList<BigInteger> row=new ArrayList<>(candidates.size());
      BigInteger[] source = votes.get(i);
      for(int j=0;j<candidates.size();++j){
        row.add(source[j]);
      }
      matrix.add(Collections.unmodifiableList(row));
    }
    return Collections.unmodifiableList(matrix);
  }

  public BigInteger[] sendVotesToCountingAuthority() throws ElectionBoardError {
    CountingAuthority authority = new CountingAuthority(board);
    return authority.combineVotes(votes);
  }
}
