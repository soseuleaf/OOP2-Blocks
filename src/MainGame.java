import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import java.util.Arrays;
import java.util.LinkedList; //import
import java.util.Queue; //import

class MainGame extends JFrame{
	// ������ ������ ���� ������
	private enum State{
		WORD_ADD, // �ܾ� �߰�
		WORD_DOWN, // �߰��� �ܾ� ������
		WORD_FIXED, // �ܾ� ��ġ �����ϱ�
		GAMEOVER, // ���� ����
	};
	
	// �Է� ��� ������ ���� ������
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
	
	private int ARRMAX = Config.getArrSize(); // 18ĭ�ε� ��濡 �� ������
	private int threadTime = Config.getThreadTime();
	private int score = 0;
	
	// �����̴� ��� �����뵵
	private WordBlock mainMovedBlock = null;
	private int mainMovedBlockX = 0;
	private int mainMovedBlockY = 0;
	
	// ������ ��� ������
	private WordBlock[][] mainFixedArray;
	
	// string ��¿� ��
	private String[][] printStrArray;
	
	// ����� ������ �� �ֳ� Ȯ���ϴ� �뵵�� �迭
	private boolean[][] blockedArray;
	
	// ���� ĥ�ϴ� �뵵
	private Color[][] colorArray;
	
	// ť �������� ���� �ܾ���� ������
	private Queue<WordBlock> wordBlockQueue = new LinkedList<WordBlock>(); 
	
	public MainGame(){
		this.setTitle("Blocks");
		this.setSize(1024, 768);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
		this.setVisible(true);
		
		// config���� �޾ƿ� arrmax ���� �������� �迭 ũ�� ����
		mainFixedArray = new WordBlock[ARRMAX][ARRMAX];
		printStrArray = new String[ARRMAX][ARRMAX];
		blockedArray = new boolean[ARRMAX][ARRMAX];
		colorArray = new Color[ARRMAX][ARRMAX];
		
		// ���� ���� �ܾ� 4�� �����ϰ� �ܾ� �߰� ���·� �ѱ�
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
	
	// ������ �Ǿ��°� Ȯ���ϴ� �Լ�
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
	
	// mainmovedblock�� �ش� �������� �̵� �������� Ȯ���ϴ� �Լ�
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
	
	// blockedarray�� �¿�ٴ��� true�� ������� �� �����̴� �κ����� �ٲ�
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
	
	// mainmovedblock�� fixedblock�� �����Ͽ� printstr�� color�� �����Ͽ� ����ϰ� �� 
	private void updateArray() {
		int tmpLen = 0;
		String tmpStr = mainMovedBlock.getString();
		
		// �����ϰ� �迭 ����α�
		fillblockedArray();
		for(String tmp[]:printStrArray)
			Arrays.fill(tmp, null);
		for(Color tmp[]:colorArray)
			Arrays.fill(tmp, null);
		
		// mainmovedblock���� ����
		for(int index = 0; index < mainMovedBlock.getLen(); index++) {
			blockedArray[mainMovedBlockX][mainMovedBlockY + index] = true;
			printStrArray[mainMovedBlockX][mainMovedBlockY + index] = Character.toString(tmpStr.charAt(index));
			colorArray[mainMovedBlockX][mainMovedBlockY + index] = mainMovedBlock.getColor();
		}
		
		// fixedarray�� ����
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
	
	// main moved block�� �����̰� �ϴ� �Լ�
	private void updateMovedArray() {
		if(checkCanMoved(1)) { // �Ʒ��� �̵� �������� Ȯ�� �ϰ�
			mainMovedBlockX++; // x���� +1 ��Ų��
		}
		else if(mainMovedBlockX == 0){ // �Ұ����ѵ� x���� 0(ù��)�̶�� ���� ����
			Sound.playGameOver();
			gameState = MainGame.State.GAMEOVER; // ���� �������� �ٸ� ������ �۵� �ȵǰ� ����
			scoreDialog.updateScore(); // ���� ���� ��Ű�� ���ھ� �������� �Ѿ
			scoreDialog.setVisible(true);
		}
		else { // �׳� ����� �־ �� ���°Ÿ� main moved block�� fixedarray�� �߰��ϰ� �ܾ� �߰�
			mainFixedArray[mainMovedBlockX][mainMovedBlockY] = mainMovedBlock;
			gameState = MainGame.State.WORD_ADD;
		}
		return;
	}
	
	// �ܾ �߰��ϴ� ������
	private class WordThread extends Thread{
		@Override
		public void run() {
			while(true) {
				try {
					sleep(threadTime);
					// add state��� wordmanager���� wordblock �Ѱ��� ������.
					// ������ ����� ť�� wordlist�� �߰���Ŵ
					// �׸��� ��� �Ѱ��� ������ mainmoved ��Ͽ� ��Ͻ�Ŵ
					// �ش� ����� y ���� �ܾ� ���̿� �°� �����ϰ� �����.
					// �� ������ mainmovedblock�� ������ ���·� ��ȯ
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
	
	// �迭 �����ϴ� ������
	private class ArrayThread extends Thread{
		@Override
		public void run() {
			while(true) {
				try {
					sleep(threadTime);
					// �ܾ �������� ���¶�� �ش� �Լ��� �۵���Ŵ
					// �߰��� stage�� �ٽ� �׸�
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
	
	// ���� ǥ���ϴ� �г�
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
	
	// �ɽ��ؼ� ���� ����� gif
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
	
	// ���� �ܾ ǥ�����ִ� �г�
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

	// �ܾ� �Է��ϴ� textfield
	private class InputField extends JTextField{
		// �ܾ �´��� Ʋ���� ǥ��� ����
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
		
		// ������� �����̰� �ϱ�
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
		
		// Ű �̺�Ʈ, ���͸� ������ �ش� ���� stage�ʿ� �����ְ� stage�� ��Ŀ���� �ٲ�
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
	
	// ���� ����ϴ� ���̾�α�
	private class ScoreDialog extends JDialog{
		private JLabel sl = new JLabel();
		private JTextField tf = new JTextField(10);
		private JButton ok = new JButton("���� ����ϱ�");
		
		public ScoreDialog(JFrame frame) {
			super(frame, "�̸��� �Է� �ϼ���.");
			this.setSize(300, 200);
			
			Container c = getContentPane();
			c.setLayout(new GridLayout(3, 1, 5 , 10));
			
			c.add(sl);
			c.add(tf);
			c.add(ok);
			
			sl.setFont(new Font("����", Font.PLAIN, 20));
			sl.setHorizontalAlignment(JLabel.CENTER);
			sl.setText("����� ������ " + Integer.toString(score) + " �� �Դϴ�.");
			
			tf.setFont(new Font("����", Font.PLAIN, 20));
			
			// config�� ���� ����ϰ� ���� ��ũ���� ���
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
		
		// �ؽ�Ʈ ������Ʈ �뵵
		public void updateScore() {
			sl.setText("����� ������ " + Integer.toString(score) + " �� �Դϴ�.");
		}
	}
	
	// ����� �����ִ� �������� �г�
	private class Stage extends JPanel{
		final Color FIXED_BORDER = Color.GREEN;
		final Color BREAK_BORDER = Color.RED;
		final int STAGE_SIZE = 650;
		final int STAGE_BLOCK = Config.getArrSize() - 2; // �����ִ� �κж����� -2�� ���� ����
		
		// �⺻������ fixe��
		InputMode inputMode = InputMode.FIXED;
		
		public Stage(){
			this.setSize(STAGE_SIZE, STAGE_SIZE);
			this.setLocation(15, 15);
			this.addKeyListener(new keyEvent());
			changeBorder();
		}
		
		// Ű �̺�Ʈ
		private class keyEvent extends KeyAdapter {
			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				switch(keyCode) {
					case KeyEvent.VK_CONTROL: // ctrl�� ������ �Է� ��带 �ٲ�
						if(inputMode == InputMode.FIXED) {
							inputMode = InputMode.BREAK; 
						}
						else {
							inputMode = InputMode.FIXED;	
						}
						changeBorder();
						break;
					case KeyEvent.VK_SPACE: // �ܾ� ��ġ ���ϰ� ����߸��� �뵵
						if(gameState == MainGame.State.WORD_FIXED) {
							Sound.playFixed(); 
							while(checkCanMoved(1)) {
								mainMovedBlockX++;
							}
							mainFixedArray[mainMovedBlockX][mainMovedBlockY] = mainMovedBlock;
							checkLine(); // ����߸��� ������ ����� üũ
							updateArray(); // ����� Ȯ���ϰ� �ٽ� �׸�
							stage.repaint();
							gameState = MainGame.State.WORD_ADD; // �߰� ���� ��ȯ
						}
						break;
					case KeyEvent.VK_ENTER: // �������� ��Ŀ������ ��ǲ�ʵ�� ��Ŀ�� �ٲ�
						inputField.requestFocusInWindow();
						break;
					case KeyEvent.VK_A: // fixed ���¿��� y�� �̵�
						if(gameState == MainGame.State.WORD_FIXED && checkCanMoved(0)) {
							Sound.playMoved();
							mainMovedBlockY--;
							updateArray();
							stage.repaint();
						}
						break;
					case KeyEvent.VK_D: // fixed ���¿��� y�� �̵�
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
		
		// �׵θ� �� �ٲٱ�
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
		
		// ��ǲ �ʵ忡�� �� �Ѱ��ִ� �Լ�
		// down ���°� �ƴ϶�� �۵� �ȵǰ� ��
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
		
		// ��� �׸��� �뵵
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
