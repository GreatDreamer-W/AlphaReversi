import java.util.LinkedList;
import java.util.Random;

public class MCTS {
    private TreeNode root;
    private int maxTime;    // 获得结果的最大运行时间，单位为毫秒
    private int maxMoves;
    double[] ucb1;

    public MCTS(char player, char[][] state) {
        root = new TreeNode(player, state);
        maxTime = 1000;
        maxMoves = 10;
    }

    public int[] getPlay() {
        LinkedList<int[]> validMoves = root.getValidMoves();
        if(validMoves.size() == 0) {
            return null;
        }
        else if(validMoves.size() == 1) {
            return validMoves.getFirst();
        }

        long beginTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - beginTime < maxTime) {
            simulate();
        }

        return root.subNodes.get(getIndexOfMaxUCB1(root)).move;
    }

    public void simulate() {
        TreeNode currentNode = root;

        for(int i = 0; i < maxMoves; i++) {
            if(currentNode.numOfTiles == 64) {
                break;
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
            if(k == currentNode.subNodes.size()) {  // 若所有的子节点均被访问过，计算ucb1
                currentNode = currentNode.subNodes.get(getIndexOfMaxUCB1(currentNode));
            }
            else {  // 否则随机选择
                Random random = new Random();
                int index = random.nextInt(currentNode.subNodes.size());
                currentNode = currentNode.subNodes.get(index);
            }
        }

        currentNode.plays++;
        if(currentNode.getWinner() == root.player) {
            while(currentNode != null) {
                currentNode.wins++;
                currentNode = currentNode.superNode;
            }
        }

    }

    private int getIndexOfMaxUCB1(TreeNode node) {
        double[] ucb1 = new double[node.subNodes.size()];
        for(int x = 0; x < node.subNodes.size(); x++) {
            double w = node.subNodes.get(x).wins;
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
