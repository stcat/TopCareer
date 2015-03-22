package jp.co.worksap.global;

import java.io.*;

public class Orienteering {
	
	public static void main(String[] args) {
		File file = new File(args[0]);
		try {
			// Read File
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = in.readLine();
			String[] tmp = line.split(" ");
			int width = Integer.parseInt(tmp[0]);
			int height = Integer.parseInt(tmp[1]);
			char[][] board = new char[height][width];
			int i = 0;
			while((line = in.readLine()) != null){  
                		for(int j = 0; j < width; j++){ 
                       			board[i][j] = line.charAt(j); 
                		} 
                		i++; 
			}
			in.close();
			
			// Initial and solve the orienteering game
			Game game = new Game(board);
			System.out.println(game.solve());
			
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found!");
		} catch (IOException e) {
			System.out.println("Input Error!");
		}
	}
}
	

