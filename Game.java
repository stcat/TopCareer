package jp.co.worksap.global;

import java.util.*;

public class Game {
	private char[][] board;
	private int width, height, numberOfCheckpoints;
	// label all the checkpoints 
	private Map<Integer, Integer> labelMap;
	// store the distance between any two checkpoints or start/goal
	private int[][] distance;
	
	public Game(char[][] inputBoard) {
		height = inputBoard.length;
		width = inputBoard[0].length;
		board = new char[height][width];
		numberOfCheckpoints = 0;
		int label = 1, goal = 0;
		labelMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				board[i][j] = inputBoard[i][j];
				switch (board[i][j]) {
					case '@':
						numberOfCheckpoints++;
						labelMap.put(i * width + j, label++);
						break;
					case 'S':
						labelMap.put(i * width + j, 0);
						break;
					case 'G':
						goal = i * width + j;
				}
			}
		}
		labelMap.put(goal, numberOfCheckpoints + 1);
	}
	
	/*
	 * Print the board as well as the distance matrix, for test use only
	 */
	public void print() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				System.out.print(board[i][j]);
			}
			System.out.println();
		}
		if (distance != null) {
			for (int i = 0; i < numberOfCheckpoints + 2; i++) {
				for (int j = 0; j < numberOfCheckpoints + 2; j++) {
					System.out.print(distance[i][j]);
				}
				System.out.println();
			}
		}
		
	}
	
	/*
	 * Solve the problem by dynamic programming O(N^2 * 2^n) time, better than O(N!)
	 */
	public int solve() {
		int minPath = Integer.MAX_VALUE;
		// Calculate the distance board
		distance = new int[numberOfCheckpoints + 2][numberOfCheckpoints + 2];
		for (int hash : labelMap.keySet()) {
			if (calculateDistance(hash) == -1) {
				return -1;
			}
		}
		// If there is no checkpoint, return the distance between start and goal
		if (numberOfCheckpoints == 0) {
			return distance[1][0];
		}
		
		// solve the TSP (travelling salesman problem) with dynamic programming  
		int n = numberOfCheckpoints + 1;
		int[][] dp = new int[1 << n][n];
		for (int[] d : dp) {
			Arrays.fill(d, Integer.MAX_VALUE / 2);
		}
		dp[1][0] = 0;
		for (int mask = 1; mask < 1 << n; mask += 2) {
			for (int i = 1; i < n; i++) {
				if ((mask & 1 << i) != 0) {
					for (int j = 0; j < n; j++) {
						if ((mask & 1 << j) != 0) {
	    						dp[mask][i] = Math.min(dp[mask][i], dp[mask ^ (1 << i)][j] + distance[j][i]);
	    					}
	    				}
	    			}
			}
	    	}
	    	for (int i = 1; i < n; i++) {
	    		minPath= Math.min(minPath, dp[(1 << n) - 1][i] + distance[i][n]);
	    	}
		return minPath;
	}
	
	/*
	 *  Calculate the distance between any two checkpoints or start/goal
	 *  Use BFS (Breadth-First Search)  
	 */
	public int calculateDistance(int hash) {
		int label = labelMap.get(hash);
		int[][] distanceBoard = new int[height][width];
		for (int[] d : distanceBoard) {
			Arrays.fill(d, -1);
		}
		int count = 0;
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.offer(hash);
		queue.offer(-1);
		int d = 0;
		while (count < numberOfCheckpoints + 2) {
			int current = queue.poll();
			if (current == -1) {
				if (queue.isEmpty()) {
					return -1;
				} else {
					queue.offer(-1);
					d++;
				}
			} else {
				int i = current / width;
				int j = current % width;
				if (distanceBoard[i][j] == -1) {
					distanceBoard[i][j] = d;
					if (labelMap.containsKey(current)) {
						int l = labelMap.get(current);
						distance[label][l] = d;
						distance[l][label] = d;
						count++;
					}
					if (i > 0 && distanceBoard[i - 1][j] == -1 && board[i - 1][j] != '#') {
						queue.offer(current - width);
					}
					if (i < height && distanceBoard[i + 1][j] == -1 && board[i + 1][j] != '#') {
						queue.offer(current + width);
					}
					if (j > 0 && distanceBoard[i][j - 1] == -1 && board[i][j - 1] != '#') {
						queue.offer(current - 1);
					}
					if (j < width && distanceBoard[i][j + 1] == -1 && board[i][j + 1] != '#') {
						queue.offer(current + 1);
					}
				}
			}
		}
		return 0;
	}
}
