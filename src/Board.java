import java.util.LinkedList;
import java.util.Scanner;

public class Board {
    private char first;     // 先手
    private char player;    // 玩家

    public Board(char first, char player) {
        this.first = first;
        this.player = player;
    }

    public void humanMachinePlay() {
        Scanner input = new Scanner(System.in);
        TreeNode board = new TreeNode(first);
        if(first != player) {   // 电脑先手
            board.print();
            MCTS mcts = new MCTS(board.player, board.state, 15, 2);
            int[] move = mcts.getPlay();
            if(move == null) {
                System.out.println("当前状态计算机无可行解，自动弃权！");
            }
            System.out.print("计算结果为");
            System.out.println(" (" + String.valueOf(move[0] + 1) + ", " + String.valueOf(move[1] + 1) + ")");
            board.flip(move);
        }
        while(!board.gameOver()) {
            board.print();
            int[] move = new int[2];
            if(board.player == player) {
                LinkedList<int[]> validMoves = board.getValidMoves();
                if(validMoves.size() == 0) {
                    System.out.println("当前状态用户无可行解，自动弃权！");
                }
                System.out.print("等待用户输入，可行解为");
                for(int[] validMove : validMoves) {
                    System.out.print(" (" + String.valueOf(validMove[0] + 1) + ", " + String.valueOf(validMove[1] + 1) + ")");
                }
                System.out.print(": ");
                String moveString = input.nextLine();
                move[0] = Integer.valueOf(moveString.split(" ")[0]) - 1;
                move[1] = Integer.valueOf(moveString.split(" ")[1]) - 1;
                while(!board.isValidMove(move[0], move[1])) {
                    System.out.print("请输入可行解：");
                    moveString = input.nextLine();
                    move[0] = Integer.valueOf(moveString.split(" ")[0]) - 1;
                    move[1] = Integer.valueOf(moveString.split(" ")[1]) - 1;
                }
            }
            else {
                MCTS mcts = new MCTS(board.player, board.state, 15, 2);
                move = mcts.getPlay();
                if(move == null) {
                    System.out.println("当前状态计算机无可行解，自动弃权！");
                }
                System.out.print("计算结果为");
                System.out.println(" (" + String.valueOf(move[0] + 1) + ", " + String.valueOf(move[1] + 1) + ")");
            }
            board.flip(move);
        }
        board.print();
        if(board.getWinner() == 'W') {
            System.out.println("白棋获胜！");
        }
        else {
            System.out.println("黑棋获胜！");
        }
    }

    public char machineMachinePlay() {
        TreeNode board = new TreeNode('W');
        while(!board.gameOver()) {
//            board.print();
            int[] move;
            MCTS mcts;
            if(board.player == 'W') {
                mcts = new MCTS(board.player, board.state, 15, 1);
            }
            else {
                mcts = new MCTS(board.player, board.state, 15, 2);
            }
            move = mcts.getPlay();
//            if(move == null) {
//                System.out.println("当前状态计算机无可行解，自动弃权！");
//            }
//            else {
//                System.out.print("计算结果为");
//                System.out.println(" (" + String.valueOf(move[0] + 1) + ", " + String.valueOf(move[1] + 1) + ")");
//            }
            board.flip(move);
        }
        board.print();
        if(board.getWinner() == 'W') {
            System.out.println("白棋获胜！");
            return 'W';
        }
        else {
            System.out.println("黑棋获胜！");
            return 'B';
        }
    }
}
