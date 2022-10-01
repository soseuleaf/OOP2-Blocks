import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

// 메인 스크린
public class MainScreen extends JFrame{
	private Container c = new BackGround();
	private WordDialog wd = new WordDialog(this);
	private OptionDialog od = new OptionDialog(this);
	private RankingDialog rk = new RankingDialog(this);
	
	public MainScreen() {
		this.setTitle("Blocks");
		this.setSize(1024, 768);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(c);
		this.setLayout(null);
		
		c.add(new StartButton("시작"));
		c.add(new WordButton("단어"));
		c.add(new RankingButton("랭킹"));
		c.add(new OptionButton("설정"));
		c.add(new ExitButton("종료"));
		
		Config.intiate(); // 설정 초기화 (점수 파일 읽기)
		WordManager.initiate(); // 설정 초기화 (단어 파일 읽기)

		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		new MainScreen();
	}
	
	// 백드라운드 이미지 패널
	private class BackGround extends JPanel{
		private ImageIcon bgImg = new ImageIcon("img/background.png");
		
		@Override
        public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(bgImg.getImage(), 0, 0, this);
        }
	}
	
	// 랭킹 다이얼로그
	private class RankingDialog extends JDialog{
		public RankingDialog(JFrame frame) {
			super(frame, "랭킹");
			this.setSize(550, 500);
			Container c = getContentPane();
			c.add(new RankingPanel());
		}
		
		// 텍스트 그리는 용도
		private class RankingPanel extends JPanel{
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				g.setFont(new Font("돋움", Font.BOLD, 30));
				g.drawString("등수", 20, 40);
				g.drawString("이름", 130, 40);
				g.drawString("점수", 270, 40);
				g.drawString("날짜", 400, 40);
				
				g.setFont(new Font("돋움", Font.PLAIN, 20));
				for(int i = 0; i < 3; i++) {
					Score tmp = Config.getScore(i);
					g.drawString(Integer.toString(i + 1), 30, 80 + i * 30);
					g.drawString(tmp.getName(), 150, 80 + i * 30); 
					g.drawString(Integer.toString(tmp.getScore()), 270, 80 + i * 30);
					g.drawString(tmp.getTime(), 370, 80 + i * 30);
				}
				
				for(int i = 3; i < 10; i++) {
					Score tmp = Config.getScore(i);
					g.drawString(Integer.toString(i + 1), 30, 80 + i * 30);
					g.drawString(tmp.getName(), 150, 80 + i * 30); 
					g.drawString(Integer.toString(tmp.getScore()), 270, 80 + i * 30);
					g.drawString(tmp.getTime(), 370, 80 + i * 30);
				}
				
				// 내 스코어가 등록되어 있으면 추가로 그린다.
				Score myScore = Config.getMyScore();
				g.setFont(new Font("돋움", Font.BOLD, 20));
				
				if(myScore == null) {
					g.drawString("-", 30, 420);
					g.drawString("-", 150, 420); 
					g.drawString("-", 270, 420);
					g.drawString("-", 370, 420);
				}
				else {
					int ranking = Config.getRanking(myScore.getName());
					g.drawString(ranking >= 0 ? Integer.toString(ranking + 1) : "-", 30, 420);
					g.drawString(myScore.getName(), 150, 420); 
					g.drawString(Integer.toString(myScore.getScore()), 270, 420);
					g.drawString(myScore.getTime(), 370, 420);
				}
			}
		}
	}
	
	// 단어 추가 다이얼로그
	private class WordDialog extends JDialog{
		private JTextField tf = new JTextField(15);
		private JList<String> scrollList = new JList<String>();
		private JButton add = new JButton("ADD");
		
		public WordDialog(JFrame frame) {
			super(frame, "단어 추가");
			this.setSize(300, 500);
			this.setLayout(new FlowLayout());
			Container c = getContentPane();
			
			scrollList.setFixedCellWidth(250);
			scrollList.setVisibleRowCount(20);
			c.add(new JScrollPane(scrollList));
			
			add.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					WordManager.addWord(tf.getText());
					tf.setText("");
					updateWordDialog();
				}
			});
			c.add(tf);
			c.add(add);
		}
		
		// 다이얼로그 내용 갱신
		public void updateWordDialog() {
			scrollList.setListData(WordManager.getWordVector());
		}
		
	}
	
	// 옵션 다이얼로그
	private class OptionDialog extends JDialog{
		private VolumeTab volumeTab = new VolumeTab();
		private GameTab gameTab = new GameTab();
		private ThreadTab thTab = new ThreadTab();
		private InfoTab infoTab = new InfoTab();

		public OptionDialog(JFrame frame) {
			super(frame, "옵션");
			this.setSize(300, 300);
			Container c = getContentPane();
			c.add(createTabbedPane());
		}
		
		// 탭 패널 제작용
		private JTabbedPane createTabbedPane() {
			JTabbedPane pane = new JTabbedPane();
			pane.addTab("소리", volumeTab);
			pane.addTab("게임", gameTab);
			pane.addTab("스레드", thTab);
			pane.addTab("정보", infoTab);
			return pane;
		}
		
		// 옵션 저장 세이브 버튼
		private class optionSaveButton extends JButton{
			public optionSaveButton() {
				super("설정 저장");
				this.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println(volumeTab.getSlider() + "," + gameTab.getSlider() + "," + thTab.getSlider());
						// 사운드 볼륨 조절
						Sound.setVolume((volumeTab.getSlider() - 140f) / 2);
						// 옵션 조절 +2인 이유는 배열 좌우가 막힌걸로 만들기 때문
						Config.setArrSize(gameTab.getSlider() + 2);
						Config.setThreadTime(thTab.getSlider());
						OptionDialog.this.setVisible(false);
					}
				});
			}
		}
		
		// 볼륨 탭
		private class VolumeTab extends JPanel{
			private JLabel sjl = new JLabel("볼륨 설정");
			private JLabel sdjl = new JLabel("게임 볼륨을 조정합니다.");
			private JLabel svjl = new JLabel("기본 설정: 100 | 현재 설정: 100");
			private JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);

			public VolumeTab() {
				this.setLayout(new GridLayout(5, 1, 5, 10));
				
				sjl.setFont(new Font("돋움", Font.BOLD, 20));
				sjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(sjl);
				
				sdjl.setFont(new Font("돋움", Font.PLAIN, 15));
				sdjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(sdjl);
				
				svjl.setFont(new Font("돋움", Font.PLAIN, 15));
				svjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(svjl);
				
				slider.setPaintLabels(true);
				slider.setPaintTicks(true);
				slider.setPaintTrack(true);
				slider.setMajorTickSpacing(10);
				slider.setMinorTickSpacing(5);
				slider.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						svjl.setText("기본 설정: 100 | 현재 설정: " + Integer.toString(slider.getValue()));
					}
				});
				this.add(slider);
				this.add(new optionSaveButton());
			}
			
			public int getSlider() {
				return slider.getValue();
			}
		}
		
		// 게임 옵션 탭
		private class GameTab extends JPanel{
			private JLabel tjl = new JLabel("게임 설정");
			private JLabel tdjl = new JLabel("<html>설정 값의 크기의 스테이지가 만듭니다.<br>추천 값은 20개 입니다.</html>");
			private JLabel tvjl = new JLabel("기본 설정: 20 | 현재 설정: 20");
			private JSlider sslider = new JSlider(JSlider.HORIZONTAL, 10, 40, 20);
			
			public GameTab() {
				this.setLayout(new GridLayout(5, 1, 5, 10));
				
				tjl.setFont(new Font("돋움", Font.BOLD, 20));
				tjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(tjl);
				
				tdjl.setFont(new Font("돋움", Font.PLAIN, 15));
				tdjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(tdjl);
				
				tvjl.setFont(new Font("돋움", Font.PLAIN, 15));
				tvjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(tvjl);
				
				sslider.setPaintLabels(true);
				sslider.setPaintTicks(true);
				sslider.setPaintTrack(true);
				sslider.setMajorTickSpacing(10);
				sslider.setMinorTickSpacing(5);
				sslider.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						tvjl.setText("기본 설정: 20 | 현재 설정: " + Integer.toString(sslider.getValue()));
					}
				});
				this.add(sslider);
				this.add(new optionSaveButton());
			}
			
			public int getSlider() {
				return sslider.getValue();
			}
		}
		
		// 스레드 설정 탭
		private class ThreadTab extends JPanel{
			private JLabel tjl = new JLabel("스레드 설정");
			private JLabel tdjl = new JLabel("<html>스레드 갱신 텀을 조정합니다.<br>추천 값은 1000 입니다. 1000 = 1초</html>");
			private JLabel tvjl = new JLabel("기본 설정: 1000 | 현재 설정: 1000");
			private JSlider sslider = new JSlider(JSlider.HORIZONTAL, 200, 2000, 1000);
			
			public ThreadTab() {
				this.setLayout(new GridLayout(5, 1, 5, 10));
				
				tjl.setFont(new Font("돋움", Font.BOLD, 20));
				tjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(tjl);
				
				tdjl.setFont(new Font("돋움", Font.PLAIN, 15));
				tdjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(tdjl);
				
				tvjl.setFont(new Font("돋움", Font.PLAIN, 15));
				tvjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(tvjl);
				
				sslider.setPaintLabels(true);
				sslider.setPaintTicks(true);
				sslider.setPaintTrack(true);
				sslider.setMajorTickSpacing(200);
				sslider.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						tvjl.setText("기본 설정: 1000 | 현재 설정: " + Integer.toString(sslider.getValue()));
					}
				});
				this.add(sslider);
				this.add(new optionSaveButton());
			}
			
			public int getSlider() {
				return sslider.getValue();
			}
		}
		
		// 정보 탭
		private class InfoTab extends JPanel{
			private JLabel jl1 = new JLabel("제작자");
			private JLabel jl2 = new JLabel();

			public InfoTab() {
				this.setLayout(new GridLayout(5, 1, 5, 10));
				
				jl1.setFont(new Font("돋움", Font.BOLD, 20));
				jl1.setHorizontalAlignment(JLabel.CENTER);
				this.add(jl1);
				this.add(new JLabel());
				jl2.setFont(new Font("돋움", Font.PLAIN, 15));
				jl2.setHorizontalAlignment(JLabel.CENTER);
				jl2.setText("<html>" + "모바일소프트웨어트랙" + "<br>" + "1771059 김준수" + "</html>");
				this.add(jl2);
				this.add(new JPanel());
				this.add(new optionSaveButton());
			}
		}
	}
	
	// 메인 화면에 넣는 jbutton 추상 클래스 액션에 따라 이미지 바꿈
	private abstract static class Button extends JButton{
		static ImageIcon normal = new ImageIcon("img/button_normal.png");
		static ImageIcon over = new ImageIcon("img/button_over.png");
		static ImageIcon press = new ImageIcon("img/button_press.png");
		static Font buttonFont = new Font("돋움", Font.BOLD, 30);
		static final int WIDTH = 150;
		static final int HEIGHT = 150;
		private String str;
		
		public Button(String str){
			this.str = str;
			this.setIcon(normal);
			this.setSize(WIDTH, HEIGHT);
			this.setRolloverIcon(over);
			this.setPressedIcon(press);
			this.setBorderPainted(false);
			this.setFocusPainted(false);
			this.setContentAreaFilled(false);

			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					Sound.playMouseOver();
				}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					buttonEvent();
				}
			});
		}
		
		abstract public void buttonEvent();
		
		// 이름 그리는 용도 그냥 설정하면 안보임
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
            FontMetrics metrics = g.getFontMetrics(buttonFont);
            int width = metrics.stringWidth(str);
			g.setFont(buttonFont);
			g.drawString(str, WIDTH / 2 - width / 2, 85);
		}
	}
	
	// 스타트 버튼
	private class StartButton extends Button{
		public StartButton(String str) {
			super(str);
			this.setLocation(50, 450);
		}
		
		@Override
		public void buttonEvent() {
			MainScreen.this.setVisible(false);
			new MainGame();
		}
	}
	
	// 단어 추가 버튼
	private class WordButton extends Button{
		public WordButton(String str) {
			super(str);
			this.setLocation(240, 450);
		}

		@Override
		public void buttonEvent() {
			wd.updateWordDialog();
			wd.setVisible(true);
		}
	}
	
	// 랭킹 버튼
	private class RankingButton extends Button{
		public RankingButton(String str) {
			super(str);
			this.setLocation(430, 450);
		}

		@Override
		public void buttonEvent() {
			rk.repaint();
			rk.setVisible(true);
		}
	}

	// 옵션 버튼
	private class OptionButton extends Button{
		public OptionButton(String str) {
			super(str);
			this.setLocation(620, 450);
		}

		@Override
		public void buttonEvent() {
			od.setVisible(true);
		}
	}

	private class ExitButton extends Button{
		public ExitButton(String str) {
			super(str);
			this.setLocation(824, 450);
		}

		@Override
		public void buttonEvent() {
			System.exit(0);
		}
	}
}