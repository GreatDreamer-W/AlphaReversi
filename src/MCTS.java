import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class MCTS {
    private TreeNode root;
    private int maxTime;    // 获得结果的最大运行时间，单位为毫秒
    private int maxMoves;
    private int evaluationStrategy;
    private int chooseStrategy;
    private int[][] priorityTable;
    private long beginTime;

    public MCTS(char player, char[][] state) {
        this(player, state, 1000, 15, 1, 1);
    }

    public MCTS(char player, char[][] state, int maxTime, int maxMoves, int evaluationStrategy, int chooseStrategy) {
        root = new TreeNode(player, state);
        this.maxTime = maxTime;
        this.maxMoves = maxMoves;
        this.evaluationStrategy = evaluationStrategy;
        this.chooseStrategy = chooseStrategy;
        beginTime = System.currentTimeMillis();
        priorityTable = new int[][]{{5, 1, 3, 3, 3, 3, 1, 5},
                                    {1, 1, 2, 2, 2, 2, 1, 1},
                                    {3, 2, 4, 4, 4, 4, 2, 3},
                                    {3, 2, 4, 0, 0, 4, 2, 3},
                                    {3, 2, 4, 0, 0, 4, 2, 3},
                                    {3, 2, 4, 4, 4, 4, 2, 3},
                                    {1, 1, 2, 2, 2, 2, 1, 1},
                                    {5, 1, 3, 3, 3, 3, 1, 5}};
    }

    public int[] getPlay() {
        LinkedList<int[]> validMoves = root.getValidMoves();
        if(validMoves.size() == 0) {
            return null;
        }
        else if(validMoves.size() == 1) {
            return validMoves.getFirst();
        }

        System.out.println(System.currentTimeMillis() - beginTime);
        while(System.currentTimeMillis() - beginTime < maxTime - 50) {
            for(int i = 0; i < 200; i++) {
                simulate();
//                System.out.print("IN");
                System.out.println(System.currentTimeMillis() - beginTime);
            }
//            System.out.println(System.currentTimeMillis() - beginTime);
        }
        System.out.println(System.currentTimeMillis() - beginTime);
        return root.subNodes.get(getIndexOfMaxUCB1(root)).move;
    }

    private void simulate() {
        long beginSimulate = System.currentTimeMillis();
        TreeNode currentNode = root;

        for(int i = 0; i < maxMoves; i++) {
            if(currentNode.numOfTiles >= 64 || System.currentTimeMillis() - beginTime > maxTime - 50) {
                return;
            }

            currentNode.plays++;
            if(currentNode.subNodes == null) {
                currentNode.getSubNodes();
            }

            // 选择一个move
            int k;
            for(k = 0; k < currentNode.subNodes.size(); k++) {
                if(currentNode.subNodes.get(k).plays == 0) {
                    break;
                }
            }

            if(k == currentNode.subNodes.size()) {  // 若所有的子节点均被访问过
                currentNode = currentNode.subNodes.get(getIndexOfMaxUCB1(currentNode));
            }
            else if(chooseStrategy == 1) {  // 随机选择
                Random random = new Random();
                int index = random.nextInt(currentNode.subNodes.size());
                currentNode = currentNode.subNodes.get(index);
            }
            else if(chooseStrategy == 2) {  // 结合priorityTable进行选择
                int sumPriority = 0;
                for(TreeNode subNode : currentNode.subNodes) {
                    if(subNode.move != null) {
                        sumPriority += priorityTable[subNode.move[0]][subNode.move[1]];
                    }
                }
                Random random = new Random();
                if(sumPriority > 0) {
                    int index = random.nextInt(sumPriority);
                    for(TreeNode subNode : currentNode.subNodes) {
                        sumPriority -= priorityTable[subNode.move[0]][subNode.move[1]];
                        if(sumPriority <= index) {
                            currentNode = subNode;
                        }
                    }
                }
                else {
                    currentNode = currentNode.subNodes.getFirst();
                }
            }
        }

        currentNode.plays++;

        // 回溯
        int win = 0;
        if(currentNode.getWinner() == root.player) {
            win = 1;
        }
        double evaluation = currentNode.getEvaluation(root.player, evaluationStrategy);
        while(currentNode != null) {
            currentNode.wins += win;
            currentNode.evaluation += evaluation;
            currentNode = currentNode.superNode;
        }

//        if(System.currentTimeMillis() - beginSimulate > 10) {
//            int a;
//        }
    }

    private int getIndexOfMaxUCB1(TreeNode node) {
        double[] ucb1 = new double[node.subNodes.size()];
        for(int x = 0; x < node.subNodes.size(); x++) {
            double w = node.subNodes.get(x).evaluation;
            double n = node.subNodes.get(x).plays;
            double t = node.plays;
            ucb1[x] = w / n + 1.414 * Math.sqrt(Math.log(t) / n);
        }
        int maxIndex = 0;
        for(int x = 0; x < node.subNodes.size(); x++) {
            if(ucb1[maxIndex] < ucb1[x]) {
                maxIndex = x;
            }
        }
        return maxIndex;
    }
}
