import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Created by james on 11/29/16.
 */
public class HumanGui {
  private JPanel contentPane;
  private JComboBox<String> voteBox;
  private JButton voteButton;
  private JTextField nameField;
  private JTextField ageField;
  private JButton clearButton;
  private JTextArea debugResponseArea;
  private JButton finishElectionButton;
  private Voter voter;
  private final ElectionBoard electionBoard;
  private final BulletinBoard bulletinBoard;

  public HumanGui(String voterFile, String candidatesFile) throws NoSuchAlgorithmException, IOException {
    electionBoard = new ElectionBoard(voterFile, candidatesFile);
    bulletinBoard = new BulletinBoard(electionBoard);
    voter = new Voter(electionBoard);
    voteBox.addItem("Select a Candidate:");
    for (int i = 0; i < electionBoard.candidates.size(); i++) {
      voteBox.addItem(electionBoard.candidates.get(i));
    }
    clearButton.addActionListener(e -> clear());
    voteButton.addActionListener(e -> vote());
    finishElectionButton.addActionListener(e -> results());
    finishElectionButton.setEnabled(true);
  }

  private void clear() {
    voteBox.setEnabled(true);
    nameField.setEditable(true);
    ageField.setEditable(true);
    voteButton.setEnabled(true);
    voteBox.setSelectedIndex(-1);
    nameField.setText("");
    ageField.setText("");
    voteButton.setEnabled(true);
    debugResponseArea.setText("");
    voter = new Voter(electionBoard);
  }

  private void vote() {
    if (!validate()) return;
    BigInteger plainVote[] = new BigInteger[electionBoard.candidates.size()];
    for (int i = 0; i < plainVote.length; i++) {
      plainVote[i] = BigInteger.ZERO;
    }
    plainVote[voteBox.getSelectedIndex() - 1] = BigInteger.ONE;
    debugResponseArea.setText("Plaintext vote is:\n" + arrayToBlockString(plainVote) + "\n");
    BigInteger encryptedVote[] = voter.encryptVote(plainVote);
    debugResponseArea.append("Vote encrypted to:\n" + arrayToBlockString(encryptedVote) + "\n");
    BigInteger blindSignedVote[];
    try {
      blindSignedVote = electionBoard.blindSignVote(nameField.getText(), Integer.parseInt(ageField.getText()), voter.encryptForBlindSign(encryptedVote));
    } catch (ElectionBoardError error) {
      debugResponseArea.append(error.getMessage());
      return;
    }
    debugResponseArea.append("Election Board successfully signed your vote as:\n" + arrayToBlockString(blindSignedVote) + "\n");
    BigInteger signedVote[] = voter.partiallyBlindSignedVote(blindSignedVote);
    debugResponseArea.append("Decrypted your part of the blind signed vote as:\n" + arrayToBlockString(blindSignedVote) + "\n");
    try {
      bulletinBoard.acceptAndZKPVote(signedVote, encryptedVote, voter);
    } catch (BulletinBoardError error) {
      debugResponseArea.append(error.getMessage());
      return;
    }
    debugResponseArea.append("Bulletin Board received and challenged your vote successfully.\n");
    voteBox.setEnabled(false);
    nameField.setEditable(false);
    ageField.setEditable(false);
    voteButton.setEnabled(false);
  }

  public void results() {
    clear();
    ResultsDialog dialog = new ResultsDialog(bulletinBoard);
    dialog.pack();
    dialog.setVisible(true);
    System.exit(0);
  }

  private String arrayToBlockString(BigInteger array[]) {
    if (array.length == 0) return "\t[]";
    StringBuilder sb = new StringBuilder("\t[");
    for (int i = 0; i < array.length; i++) {
      sb.append(array[i]).append(",\n\t");
    }
    sb.replace(sb.length() - 3, sb.length(), "]");
    return sb.toString();
  }

  private boolean validate() {
    if (voteBox.getSelectedIndex() <= 0) {
      showMessageDialog(contentPane, "You must select a candidate to vote.", "No Candidate Selected", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    nameField.setText(nameField.getText().trim());
    if (nameField.getText().isEmpty()) {
      showMessageDialog(contentPane, "You must enter a your name when you vote.", "No Name Entered", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    ageField.setText(ageField.getText().trim());
    if (ageField.getText().isEmpty()) {
      showMessageDialog(contentPane, "You must enter a your age when you vote.", "No Age Entered", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    try {
      if (Integer.parseInt(ageField.getText()) < 18) {
        showMessageDialog(contentPane, "You must be at least 18 to vote.", "Too Young", JOptionPane.ERROR_MESSAGE);
        return false;
      }
    } catch (NumberFormatException ex) {
      showMessageDialog(contentPane, "\"" + ageField.getText() + "\"" + " is not a valid age.", "Invalid Age", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
    if (args.length != 2) {
      System.err.println("usage: HumanGui voters.txt candidates.txt");
      System.exit(-1);
    }
    JFrame frame = new JFrame("HumanGui");
    frame.setContentPane(new HumanGui(args[0], args[1]).contentPane);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

  {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    contentPane = new JPanel();
    contentPane.setLayout(new GridLayoutManager(4, 3, new Insets(5, 5, 5, 5), -1, -1));
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
    contentPane.add(panel1, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    voteButton = new JButton();
    voteButton.setText("Vote!");
    panel1.add(voteButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    clearButton = new JButton();
    clearButton.setText("Clear");
    panel1.add(clearButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JScrollPane scrollPane1 = new JScrollPane();
    panel1.add(scrollPane1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(400, 300), null, 0, false));
    debugResponseArea = new JTextArea();
    debugResponseArea.setEditable(false);
    scrollPane1.setViewportView(debugResponseArea);
    nameField = new JTextField();
    contentPane.add(nameField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    contentPane.add(panel2, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final JLabel label1 = new JLabel();
    label1.setText("Name");
    panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JLabel label2 = new JLabel();
    label2.setText("Age:");
    panel2.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    ageField = new JTextField();
    contentPane.add(ageField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    final JLabel label3 = new JLabel();
    label3.setText("Select a Candidate:");
    contentPane.add(label3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    voteBox = new JComboBox();
    contentPane.add(voteBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(180, -1), null, 0, false));
    finishElectionButton = new JButton();
    finishElectionButton.setEnabled(false);
    finishElectionButton.setText("Finish Election");
    contentPane.add(finishElectionButton, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() { return contentPane; }
}
