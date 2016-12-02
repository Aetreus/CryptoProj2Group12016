import java.math.BigInteger;
import java.util.ArrayList;
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
  
  public BulletinBoard(ElectionBoard electionBoard) {
    board = electionBoard;
    candidates=board.candidates;
  }

  public void acceptAndZKPVote(BigInteger[] signedVote, BigInteger[] encryptedVote, Voter voter) throws BulletinBoardError{
    for(int i = 0; i < encryptedVote.length; i++){
      if(!(signedVote[i].modPow(board.publicExponent,board.modulus).equals(encryptedVote[i]))){
        throw new BulletinBoardError("Signature for segment " + i + " " + signedVote[i].modPow(board.publicExponent,board.modulus) + " did not match encrypted vote " + encryptedVote[i].toString() + "\n");
      }
    }
    for(int i = 0; i < encryptedVote.length; i++){
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
          throw new BulletinBoardError("Failed ZKP, u = " + u + " g = " + g + " c = " + encryptedVote[i] + " e = " + e + " w = " + w + " N = " + N + "\n");
        }
      }
    }
  }

  public List<List<BigInteger>> getVoteMatrix() {
    //Todo return a row by row matrix of the accepted encrypted votes, using debug matrix for now
    ArrayList<List<BigInteger>> matrix=new ArrayList<>();
    Random random=new Random();
    for(int i=0;i<5;++i){
      ArrayList<BigInteger> row=new ArrayList<>(candidates.size());
      for(int j=0;j<candidates.size();++j){
        row.add(BigInteger.valueOf(random.nextLong()));
      }
      matrix.add(Collections.unmodifiableList(row));
    }
    return Collections.unmodifiableList(matrix);
  }

  public BigInteger[] sendVotesToCountingAuthority() {
    CountingAuthority authority =new CountingAuthority(board);
    //TODO send all the votes to the authority which according to the project then sends the results to the EM, which should
    //probably echo them back here to the public
    BigInteger tmp[]=new BigInteger[candidates.size()];
    Random random=new Random();
    for(int i=0;i<candidates.size();++i){
      tmp[i]=BigInteger.valueOf(random.nextLong());
    }
    return tmp;
  }
}
