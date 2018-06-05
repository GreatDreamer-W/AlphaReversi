import java.util.LinkedList;

public class TreeNode {
    public char player;
    public char[][] state;
    public int[] move;  // 从父节点到此节点的棋
    public LinkedList<TreeNode> subNodes;
    public TreeNode superNode;
    public int numOfTiles;
    public int wins;
    public int plays;
    public double evaluation;

    public TreeNode(char player) {
        this(player, new char[][]{{'0', '0', '0', '0', '0', '0', '0', '0'},
                        {'0', '0', '0', '0', '0', '0', '0', '0'},
                        {'0', '0', '0', '0', '0', '0', '0', '0'},
                        {'0', '0', '0', 'W', 'B', '0', '0', '0'},
                        {'0', '0', '0', 'B', 'W', '0', '0', '0'},
                        {'0', '0', '0', '0', '0', '0', '0', '0'},
                        {'0', '0', '0', '0', '0', '0', '0', '0'},
                        {'0', '0', '0', '0', '0', '0', '0', '0'}}, 4);
    }

    public TreeNode(char player, char[][] state) {
        this.player = player;
        this.state = state;
        move = null;
        subNodes = null;
        superNode = null;
        numOfTiles = 0;
        wins = 0;
        plays = 0;
        evaluation = 0;

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(state[i][j] != '0') {
                    numOfTiles++;
                }
            }
        }
    }

    public TreeNode(char player, char[][] state, int numOfTiles) {
        this.player = player;
        this.state = state;
        move = null;
        subNodes = null;
        superNode = null;
        this.numOfTiles = numOfTiles;
        wins = 0;
        plays = 0;
        evaluation = 0;
    }

    public LinkedList<int[]> getValidMoves() {
        LinkedList<int[]> validMoves = new LinkedList<>();
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(isValidMove(i, j)) {
                    int[] move = {i, j};
                    validMoves.addLast(move);
                }
            }
        }
        return validMoves;
    }

    public boolean isValidMove(int xStart, int yStart) {
        if(state[xStart][yStart] != '0' || !isOnBoard(xStart, yStart)) {
            return false;
        }

        state[xStart][yStart] = player;
        char anotherPlayer;
        if(player == 'W') {
            anotherPlayer = 'B';
        }
        else {
            anotherPlayer = 'W';
        }

        int[][] directions = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        for(int[] direction : directions) {
            int x = xStart + direction[0];
            int y = yStart + direction[1];
            int count = 0;
            while(isOnBoard(x, y) && state[x][y] == anotherPlayer) {
                count++;
                x += direction[0];
                y += direction[1];
            }
            if(isOnBoard(x, y) && state[x][y] == player && count > 0) {
                state[xStart][yStart] = '0';
                return true;
            }
        }
        state[xStart][yStart] = '0';
        return false;
    }

    private boolean isOnBoard(int x, int y) {
        return x >= 0 && x <= 7 && y >= 0 && y <=7;
    }

    public void flip(int[] move) {
        this.move = move;
        if(move != null) {
            numOfTiles++;
            int xStart = move[0];
            int yStart = move[1];
            char anotherPlayer;

            state[xStart][yStart] = player;
            anotherPlayer = (player == 'W') ? 'B' : 'W';

            int[][] directions = {{0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};
            for(int[] direction : directions) {
                int x = xStart + direction[0];
                int y = yStart + direction[1];
                while(isOnBoard(x, y) && state[x][y] == anotherPlayer) {
                    x += direction[0];
                    y += direction[1];
                }
                if(isOnBoard(x, y) && state[x][y] == player) {
                    while(true) {
                        x -= direction[0];
                        y -= direction[1];
                        if(x == xStart && y == yStart) {
                            break;
                        }
                        else {
                            state[x][y] = player;
                        }
                    }
                }
            }
        }
        player = (player == 'W') ? 'B' : 'W';
    }

    public void getSubNodes() {
        subNodes = new LinkedList<>();
        LinkedList<int[]> validMoves = getValidMoves();
        if(validMoves.size() > 0) {
            for(int[] move : validMoves) {
                TreeNode child = this.clone();
                child.superNode = this;
                child.flip(move);
                subNodes.add(child);
            }
        }
        else {  // 若无合法移动
            TreeNode child = this.clone();
            child.superNode = this;
            if(child.player == 'W') {
                child.player = 'B';
            }
            else {
                child.player = 'W';
            }
            subNodes.add(child);
        }
    }

    public void print() {
        TreeNode newNode = this.clone();
        char[][] board = newNode.state;
        Integer countWhite = 0;
        Integer countBlack = 0;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(board[i][j] == 'B') {
                    countBlack++;
                    board[i][j] = 'X';
                }
                else if(board[i][j] == 'W') {
                    countWhite++;
                    board[i][j] = 'O';
                }
                else if(isValidMove(i, j)) {
                    board[i][j] = '.';
                }
                else {
                    board[i][j] = ' ';
                }
            }
        }
        System.out.println("——————————————————————————————————————————————");
        System.out.println("白棋为O，黑棋为X，可行解为.");
        if(player == 'W') {
            System.out.println("轮到 白棋 下棋：    白棋数量为" + countWhite.toString() + "，黑棋数量为" + countBlack.toString());
        }
        else {
            System.out.println("轮到 黑棋 下棋：    黑棋数量为" + countBlack.toString() + "，白棋数量为" + countWhite.toString());
        }
        System.out.println("    1   2   3   4   5   6   7   8  ");
        System.out.println("  +-------------------------------+");
        for(int x = 0; x < 8; x++) {
            System.out.print(x+1);
            System.out.println(" | " + board[x][0] + " | " + board[x][1] +
                               " | " + board[x][2] + " | " + board[x][3] +
                               " | " + board[x][4] + " | " + board[x][5] +
                               " | " + board[x][6] + " | " + board[x][7] + " |");
            if(x < 7) {
                System.out.println("  |---+---+---+---+---+---+---+---|");
            }
            else {
                System.out.println("  +-------------------------------+");
            }
        }
    }

    public TreeNode clone() {
        TreeNode clone = new TreeNode(player);
        for(int i = 0; i < 8; i++) {
            System.arraycopy(state[i], 0, clone.state[i], 0, 8);
        }
        clone.numOfTiles = numOfTiles;
        return clone;
    }

    public char getWinner() {
        int countWhite = 0;
        int countBlack = 0;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(state[i][j] == 'W') {
                    countWhite++;
                }
                else if(state[i][j] == 'B') {
                    countBlack++;
                }
            }
        }
        if(countWhite > countBlack) {
            return 'W';
        }
        else if(countBlack > countWhite) {
            return 'B';
        }
        else {
            return '0'; // 平局
        }
    }

    public double getWins(char player, int strategy) {
        double countWhite = 0;
        double countBlack = 0;
        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (state[i][j] == 'W') {
                    countWhite += 1;
                } else if (state[i][j] == 'B') {
                    countBlack += 1;
                }
            }
        }
        if(strategy == 2) {
            if(player == 'W') {
                if(countBlack > countWhite) {
                    return 0;
                }
                else {
                    return Math.exp((countWhite - countBlack) / 32);
                }
            }
            else {
                if(countWhite > countBlack) {
                    return 0;
                }
                else {
                    return Math.exp((countBlack - countWhite) / 32);
                }
            }
        }
        else if(strategy == 3) {
            if(player == 'W') {
                    return Math.exp((countWhite - countBlack) / 32);
            }
            else {
                    return Math.exp((countBlack - countWhite) / 32);
            }
        }
        else {
            return 1;
        }
    }

    public boolean gameOver() {
        if(numOfTiles >= 64) {
            return true;
        }
        if(getValidMoves().size() == 0) {
            player = (player == 'W') ? 'B' : 'W';
            if(getValidMoves().size() == 0) {
                player = (player == 'W') ? 'B' : 'W';
                return true;
            }
            else {
                player = (player == 'W') ? 'B' : 'W';
                return false;
            }
        }
        else {
            return false;
        }
    }
}
