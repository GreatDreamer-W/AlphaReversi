import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class Main {

    public static void main(String[] args) {
//        Board board = new Board('B', 'W');
//        board.machineMachinePlay();
        ForkJoinPool fjp = new ForkJoinPool(4);
        TreeNode t = new TreeNode('W');
        ForkJoinTask<int[]> mcts = new MCTS(t.player, t.state, 1000, 15, 1,1);
        fjp.invoke(mcts);
//        int countWhiteWins = 0;
//        int countBlackWins = 0;
//        for(int i = 0; i < 50; i++) {
//            System.out.println(i);
//            char winner = board.machineMachinePlay();
//            if(winner == 'W') {
//                countWhiteWins++;
//            }
//            else {
//                countBlackWins++;
//            }
//        }
//        System.out.print("白棋：");
//        System.out.println(countWhiteWins);
//        System.out.print("黑棋：");
//        System.out.println(countBlackWins);
//        board.humanMachinePlay();
    }
}
