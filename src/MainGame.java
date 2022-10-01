import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import java.util.Arrays;
import java.util.LinkedList; //import
import java.util.Queue; //import

class MainGame extends JFrame{
	// 스레드 관리를 위한 열거형
	private enum State{
		WORD_ADD, // 단어 추가
		WORD_DOWN, // 추가된 단어 내리기
		WORD_FIXED, // 단어 위치 결정하기
		GAMEOVER, // 게임 오버
	};
	
	// 입력 모드 관리를 위한 열거형
	private enum InputMode{
		FIXED,
		BREAK,
	};
	
	private Container c = getContentPane();
	private Stage stage = new Stage();
	private WordList wordList = new WordList();
	private ScoreField scoreField = new ScoreField();
	private InputField inputField = new InputField();
	private ScoreDialog scoreDialog = new ScoreDialog(this);
	private State gameState = null;
	
	private int ARRMAX = Config.getArrSize(); // 18칸인데 사방에 벽 떄문에
	private int threadTime = Config.getThreadTime();
	private int score = 0;
	
	// 움직이는 블록 관리용도
	private WordBlock mainMovedBlock = null;
	private int mainMovedBlockX = 0;
	private int mainMovedBlockY = 0;
	
	// 고정된 블록 관리용
	private WordBlock[][] mainFixedArray;
	
	// string 출력용 블럭
	private String[][] printStrArray;
	
	// 블록이 움직일 수 있나 확인하는 용도의 배열
	private boolean[][] blockedArray;
	
	// 색깔 칠하는 용도
	private Color[][] colorArray;
	
	// 큐 형식으로 나올 단어들을 저장함
	private Queue<WordBlock> wordBlockQueue = new LinkedList<WordBlock>(); 
	
	public MainGame(){
		this.setTitle("Blocks");
		this.setSize(1024, 768);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
		this.setVisible(true);
		
		// config에서 받아온 arrmax 값을 바탕으로 배열 크기 선언
		mainFixedArray = new WordBlock[ARRMAX][ARRMAX];
		printStrArray = new String[ARRMAX][ARRMAX];
		blockedArray = new boolean[ARRMAX][ARRMAX];
		colorArray = new Color[ARRMAX][ARRMAX];
		
		// 시작 전에 단어 4개 투입하고 단어 추가 상태로 넘김
		int count = 0;
		while(count < 4) {
			WordBlock tmpWordBlock = WordManager.getWordBlock();
			wordBlockQueue.add(tmpWordBlock);
			wordList.addWord(tmpWordBlock.getString());
			count++;
		}
		gameState = State.WORD_ADD;
		
		c.setBackground(new Color(0, 51, 102));
		c.add(scoreField);
		c.add(inputField);
		c.add(stage);
		c.add(wordList);
		c.add(new NyanCat());
		stage.setFocusable(true);
		stage.requestFocus();
		new WordThread().start();
		new ArrayThread().start();
	}
	
	// 한줄이 되었는가 확인하는 함수
	private void checkLine() {
		updateArray();
		for(int x = 1; x <ARRMAX - 1; x++) {
			for(int y = 1; y < ARRMAX - 1; y++) {
				if(blockedArray[x][y] == false) break;
				else if (y == (ARRMAX - 2)) {
					Sound.playClear();
					score += 1000;
					scoreField.repaint();
					Arrays.fill(mainFixedArray[x], null);
					for(int downX = x; downX > 0; downX--) {
						for(int downY = 1; downY < ARRMAX - 1; downY++) {
							mainFixedArray[downX][downY] = mainFixedArray[downX - 1][downY];
							mainFixedArray[downX - 1][downY] = null;
						}
					}
				}
			}
		}
	}
	
	// mainmovedblock이 해당 방향으로 이동 가능한지 확인하는 함수
	private boolean checkCanMoved(int direct) { // left0, down1, right2
		int tmpLen = mainMovedBlock.getLen();
		
		if(direct == 0) {
			if(blockedArray[mainMovedBlockX][mainMovedBlockY - 1] == false) {
				return true;
			}
		}
		else if(direct == 1) {
			for(int checkY = mainMovedBlockY; checkY < tmpLen + mainMovedBlockY; checkY++) {
				if(blockedArray[mainMovedBlockX + 1][checkY] == true) {
					return false;
				}
			}
			return true;
		}
		else if(direct == 2) {
			if(blockedArray[mainMovedBlockX][mainMovedBlockY + tmpLen] == false) {
				return true;
			}
		}
		return false;
	}
	
	// blockedarray의 좌우바닥을 true로 변경시켜 못 움직이는 부분으로 바꿈
	private void fillblockedArray() {
		for(int x = 0; x <ARRMAX; x++) {
			for(int y = 0; y < ARRMAX; y++) {
				if(x == (ARRMAX - 1) || y == 0 || y == (ARRMAX - 1)) {
					blockedArray[x][y] = true;
				}
				else {
					blockedArray[x][y] = false;
				}
			}
		}
	}
	
	// mainmovedblock과 fixedblock을 참고하여 printstr과 color에 저장하여 출력하게 함 
	private void updateArray() {
		int tmpLen = 0;
		String tmpStr = mainMovedBlock.getString();
		
		// 깨끗하게 배열 비워두기
		fillblockedArray();
		for(String tmp[]:printStrArray)
			Arrays.fill(tmp, null);
		for(Color tmp[]:colorArray)
			Arrays.fill(tmp, null);
		
		// mainmovedblock으로 갱신
		for(int index = 0; index < mainMovedBlock.getLen(); index++) {
			blockedArray[mainMovedBlockX][mainMovedBlockY + index] = true;
			printStrArray[mainMovedBlockX][mainMovedBlockY + index] = Character.toString(tmpStr.charAt(index));
			colorArray[mainMovedBlockX][mainMovedBlockY + index] = mainMovedBlock.getColor();
		}
		
		// fixedarray로 갱신
		for(int x = 1; x < ARRMAX - 1; x++) {
			for(int y = 1; y < ARRMAX - 1; y++) {
				if (mainFixedArray[x][y] != null) {
					WordBlock tmpWordBlock = mainFixedArray[x][y];
					tmpLen = tmpWordBlock.getLen();
					tmpStr = tmpWordBlock.getString();
					
					for(int index = 0; index < tmpLen; index++) {
						blockedArray[x][y + index] = true;
						printStrArray[x][y + index] = Character.toString(tmpStr.charAt(index));
						colorArray[x][y + index] = tmpWordBlock.getColor();
					}
				}
			}
		}
	}
	
	// main moved block을 움직이게 하는 함수
	private void updateMovedArray() {
		if(checkCanMoved(1)) { // 아래로 이동 가능한지 확인 하고
			mainMovedBlockX++; // x값을 +1 시킨다
		}
		else if(mainMovedBlockX == 0){ // 불가능한데 x값이 0(첫줄)이라면 게임 오버
			Sound.playGameOver();
			gameState = MainGame.State.GAMEOVER; // 상태 변경으로 다른 스레드 작동 안되게 막기
			scoreDialog.updateScore(); // 게임 오버 시키고 스코어 저장으로 넘어감
			scoreDialog.setVisible(true);
		}
		else { // 그냥 블록이 있어서 못 가는거면 main moved block을 fixedarray에 추가하고 단어 추가
			mainFixedArray[mainMovedBlockX][mainMovedBlockY] = mainMovedBlock;
			gameState = MainGame.State.WORD_ADD;
		}
		return;
	}
	
	// 단어를 추가하는 스레드
	private class WordThread extends Thread{
		@Override
		public void run() {
			while(true) {
				try {
					sleep(threadTime);
					// add state라면 wordmanager에서 wordblock 한개를 가져옴.
					// 가져온 블록을 큐와 wordlist에 추가시킴
					// 그리고 블록 한개를 꺼내서 mainmoved 블록에 등록시킴
					// 해당 블록의 y 값은 단어 길이에 맞게 적절하게 계산함.
					// 다 했으면 mainmovedblock을 내리는 상태로 변환
					if(gameState == MainGame.State.WORD_ADD) {
						WordBlock tmpWordBlock = WordManager.getWordBlock();
						wordBlockQueue.add(tmpWordBlock);
						wordList.addWord(tmpWordBlock.getString());
						wordList.removeWord();
						
						mainMovedBlock = wordBlockQueue.poll();
						mainMovedBlockX = 0;
						int randBlockY = (int)(Math.random() * (ARRMAX - 2 - mainMovedBlock.getLen()));
						mainMovedBlockY = randBlockY + 1;
					
						gameState = MainGame.State.WORD_DOWN;
					}
				} 
				catch (InterruptedException e) {
					return;
				}
			}
		}
	}
	
	// 배열 관리하는 스레드
	private class ArrayThread extends Thread{
		@Override
		public void run() {
			while(true) {
				try {
					sleep(threadTime);
					// 단어가 내려가는 상태라면 해당 함수를 작동시킴
					// 추가로 stage를 다시 그림
					if(gameState == MainGame.State.WORD_DOWN) {
						updateMovedArray();
						updateArray();
						stage.repaint();
					}
				} 
				catch (InterruptedException e) {
					return;
				}
			}
		}
	}
	
	// 점수 표기하는 패널
	private class ScoreField extends JPanel{
		public ScoreField() {
			this.setSize(315,200);
			this.setLocation(685,230);
			this.setBackground(Color.BLACK);
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setFont(new Font("Consolas", Font.PLAIN, 50));
			g.setColor(Color.WHITE);
			g.drawString("Score", 5, 45);
			
			String scoreStr = Integer.toString(score);
			Font tmp = new Font("Consolas", Font.PLAIN, 100);
			g.setFont(tmp);
			g.setColor(Color.WHITE);
			
			FontMetrics metrics = g.getFontMetrics(tmp);
            int width = metrics.stringWidth(scoreStr);
            g.drawString(scoreStr, 315 / 2 - width / 2, 150);
		}
	}
	
	// 심심해서 넣은 고양이 gif
	private class NyanCat extends JPanel{
		Image image;
		
		public NyanCat() {
			this.setSize(315, 315);
			this.setLocation(685, 400);
			image = Toolkit.getDefaultToolkit().createImage("img/nyancat.gif");
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(image, 0, 0, this);
		}
	}
	
	// 다음 단어를 표기해주는 패널
	private class WordList extends JPanel{
		private final int arrSize = 5; 
		private String[] wordList = new String[arrSize]; 
		private int size = 0;
		
		public WordList() {
			this.setSize(315,200);
			this.setLocation(685,15);
			this.setBackground(Color.BLACK);
		}
		
		private void addWord(String name) {
			if(size < arrSize)
				wordList[size++] = name;
			this.repaint();
		}
		
		private void removeWord() {
			for(int i = 1; i < arrSize; i++) {
				wordList[i - 1] = wordList[i];
			}
			size--;
			this.repaint();
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setFont(new Font("Consolas", Font.PLAIN, 50));
			g.setColor(Color.WHITE);
			g.drawString("Next Word", 5, 45);
			
			if(size < arrSize) {
				String drawStr = wordList[0];
				g.setFont(new Font("Consolas", Font.PLAIN, 50));
				g.setColor(Color.MAGENTA);
				g.drawString(drawStr, 5, 100);
				
				for(int i = 1; i < size; i++) {
					drawStr = wordList[i];
					g.setFont(new Font("Consolas", Font.PLAIN, 20));
					g.setColor(Color.WHITE);
					g.drawString(drawStr, 10, 100 + (30 * i));
				}
			}
		}
	}

	// 단어 입력하는 textfield
	private class InputField extends JTextField{
		// 단어가 맞는지 틀린지 표기용 색깔
		Color normalBgColor = Color.WHITE;
		Color successBgColor = Color.GREEN;
		Color failBgColor = Color.RED;
		
		public InputField() {
			this.setSize(650, 50);
			this.setLocation(15, 670);
			this.setFont(new Font("Consolas", Font.PLAIN, 30));
			this.setForeground(Color.BLACK);
			this.addKeyListener(new fieldKeyEvent());
		}
		
		// 스레드로 깜빡이게 하기
		private void blinkResult(boolean success) {
			new Thread() {
				@Override
				public void run() {
					try {
						InputField.this.setBackground(success == true ? successBgColor : failBgColor);
						sleep(200);
						InputField.this.setBackground(normalBgColor);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
		
		// 키 이벤트, 엔터를 누르면 해당 값을 stage쪽에 보내주고 stage에 포커스를 바꿈
		private class fieldKeyEvent extends KeyAdapter {
			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				switch(keyCode) {
					case KeyEvent.VK_ENTER:
						stage.requestFocusInWindow();
						stage.inputFieldWord(InputField.this.getText());
						InputField.this.setText(null);
						break;
				}
			}
		}
	}
	
	// 점수 등록하는 다이얼로그
	private class ScoreDialog extends JDialog{
		private JLabel sl = new JLabel();
		private JTextField tf = new JTextField(10);
		private JButton ok = new JButton("점수 등록하기");
		
		public ScoreDialog(JFrame frame) {
			super(frame, "이름을 입력 하세요.");
			this.setSize(300, 200);
			
			Container c = getContentPane();
			c.setLayout(new GridLayout(3, 1, 5 , 10));
			
			c.add(sl);
			c.add(tf);
			c.add(ok);
			
			sl.setFont(new Font("돋움", Font.PLAIN, 20));
			sl.setHorizontalAlignment(JLabel.CENTER);
			sl.setText("당신의 점수는 " + Integer.toString(score) + " 점 입니다.");
			
			tf.setFont(new Font("돋움", Font.PLAIN, 20));
			
			// config에 점수 등록하고 메인 스크린을 띄움
			ok.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Config.addScore(tf.getText(), score);
					Config.addMyScore(tf.getText(), score);
					new MainScreen();
					MainGame.this.dispose();
				}
			});
		}
		
		// 텍스트 업데이트 용도
		public void updateScore() {
			sl.setText("당신의 점수는 " + Integer.toString(score) + " 점 입니다.");
		}
	}
	
	// 블록을 보여주는 스테이지 패널
	private class Stage extends JPanel{
		final Color FIXED_BORDER = Color.GREEN;
		final Color BREAK_BORDER = Color.RED;
		final int STAGE_SIZE = 650;
		final int STAGE_BLOCK = Config.getArrSize() - 2; // 막혀있는 부분때문에 -2한 값과 같음
		
		// 기본적으로 fixeㅇ
		InputMode inputMode = InputMode.FIXED;
		
		public Stage(){
			this.setSize(STAGE_SIZE, STAGE_SIZE);
			this.setLocation(15, 15);
			this.addKeyListener(new keyEvent());
			changeBorder();
		}
		
		// 키 이벤트
		private class keyEvent extends KeyAdapter {
			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				switch(keyCode) {
					case KeyEvent.VK_CONTROL: // ctrl을 누르면 입력 모드를 바꿈
						if(inputMode == InputMode.FIXED) {
							inputMode = InputMode.BREAK; 
						}
						else {
							inputMode = InputMode.FIXED;	
						}
						changeBorder();
						break;
					case KeyEvent.VK_SPACE: // 단어 위치 정하고 떨어뜨리기 용도
						if(gameState == MainGame.State.WORD_FIXED) {
							Sound.playFixed(); 
							while(checkCanMoved(1)) {
								mainMovedBlockX++;
							}
							mainFixedArray[mainMovedBlockX][mainMovedBlockY] = mainMovedBlock;
							checkLine(); // 떨어뜨리고 한줄이 됬는지 체크
							updateArray(); // 됬는지 확인하고 다시 그림
							stage.repaint();
							gameState = MainGame.State.WORD_ADD; // 추가 모드로 전환
						}
						break;
					case KeyEvent.VK_ENTER: // 스테이지 포커스에서 인풋필드로 포커스 바꿈
						inputField.requestFocusInWindow();
						break;
					case KeyEvent.VK_A: // fixed 상태에서 y축 이동
						if(gameState == MainGame.State.WORD_FIXED && checkCanMoved(0)) {
							Sound.playMoved();
							mainMovedBlockY--;
							updateArray();
							stage.repaint();
						}
						break;
					case KeyEvent.VK_D: // fixed 상태에서 y축 이동
						if(gameState == MainGame.State.WORD_FIXED && checkCanMoved(2)) {
							Sound.playMoved();
							mainMovedBlockY++;
							updateArray();
							stage.repaint();
						}
						break;
				}
			}
		}
		
		// 테두리 색 바꾸기
		private void changeBorder() {
			switch(inputMode) {
				case FIXED:
					this.setBorder(new LineBorder(FIXED_BORDER, 8));
					break;
				case BREAK:
					this.setBorder(new LineBorder(BREAK_BORDER, 8));
					break;
				default:
					this.setBorder(new LineBorder(Color.WHITE, 8));
					break;
			}
		}
		
		// 입풋 필드에서 값 넘겨주는 함수
		// down 상태가 아니라면 작동 안되게 함
		private void inputFieldWord(String str) {
			if(gameState != MainGame.State.WORD_DOWN) return;
			
			if(mainMovedBlock.getString().equals(str) || mainMovedBlock.getString().equals("@")){
				Sound.playSuccess();
				if(inputMode == InputMode.FIXED) {
					score += str.length() * 10;
					scoreField.repaint();
					gameState = MainGame.State.WORD_FIXED;
				}
				else if (inputMode == InputMode.BREAK) {
					Sound.playRemove();
					gameState = MainGame.State.WORD_ADD;
				}
				inputField.blinkResult(true);
			}
			else {
				Sound.playFail();
				inputField.blinkResult(false);
			}
		}
		
		// 배경 그리는 용도
		@Override
		public void paintComponent(Graphics g) {
			int xLen = Stage.this.getWidth() / STAGE_BLOCK;
			int yLen = Stage.this.getHeight() / STAGE_BLOCK;
			int xStart = 0;
			int yStart = 0;
			int drawCount = 0;
			
			Color blockColor = new Color(255, 211, 105);
			Color bgColorEven = new Color(34, 40, 49);
			Color bgColorOdd = new Color(57, 62, 70);
			
			super.paintComponent(g);
			Sound.playMoved();

			for(int x = 1; x < ARRMAX - 1; x++) {
				for(int y = 1; y < ARRMAX - 1; y++) {
					if (printStrArray[x][y] != null) {
						g.setColor(colorArray[x][y]);
						g.fillRect(xStart, yStart, xLen, yLen);
						g.setColor(Color.BLACK);
						g.setFont(new Font("Consolas", Font.PLAIN, 30));
						g.drawString(printStrArray[x][y], xStart + 10, yStart + 25);
					}
					else {
						g.setColor((drawCount % 2 == 0) ? bgColorEven : bgColorOdd);
						g.fillRect(xStart, yStart, xLen, yLen);
					}
					xStart += xLen;
					drawCount++;
				}
				drawCount++;
				xStart = 0;
				yStart += yLen;
			}
		}
	}
}
