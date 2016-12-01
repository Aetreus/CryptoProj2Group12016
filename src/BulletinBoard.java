import java.math.BigInteger;

/**
 * Created by james on 11/30/16.
 */
public class BulletinBoard {
  private static int CERTAINTY = 64;
  private ElectionBoard board;
  
  public BulletinBoard(ElectionBoard electionBoard) {
    board = electionBoard;
  }

  public void acceptAndZKPVote(BigInteger[] signedVote, BigInteger[] encryptedVote, Voter voter) throws BulletinBoardError{
    for(int i = 0; i < encryptedVote.length; i++){
      if(!(signedVote[i].modPow(board.publicExponent,board.modulus).equals(encryptedVote[i]))){
        throw new BulletinBoardError("Signature for segment " + i + " " + signedVote[i].modPow(board.publicExponent,board.modulus).toString() + " did not match encrypted vote " + encryptedVote[i].toString() + "\n");
      }
    }
    for(int i = 0; i < encryptedVote.length; i++){
      BigInteger N = board.publicEncryption[i].getPublicKey().getN();
      BigInteger NSquared = N.multiply(N);
      BigInteger g = N.add(BigInteger.ONE);
      for(int j = 0; j < CERTAINTY; j++){
        BigInteger u = voter.initZKP(i);
        BigInteger e;
        do{
          e = new BigInteger(N.bitLength(), board.random);
        }while(e.compareTo(N) > 0);
        BigInteger v = voter.getZKPV(e,i);
        BigInteger w = voter.getZKPW(e,i);
        if(!(g.modPow(v,NSquared).multiply(encryptedVote[i].modPow(e,NSquared)).multiply(w.modPow(N,NSquared)).equals(u))){
          throw new BulletinBoardError("Failed ZKP, u = " + u.toString() + " g = " + g.toString() + " c = " + encryptedVote[i].toString() + " e = " + e.toString() + " w = " + w.toString() + " N = " + N.toString() + "\n");
        }
      }
    }
  }
}
