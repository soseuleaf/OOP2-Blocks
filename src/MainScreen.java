import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

// ���� ��ũ��
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
		
		c.add(new StartButton("����"));
		c.add(new WordButton("�ܾ�"));
		c.add(new RankingButton("��ŷ"));
		c.add(new OptionButton("����"));
		c.add(new ExitButton("����"));
		
		Config.intiate(); // ���� �ʱ�ȭ (���� ���� �б�)
		WordManager.initiate(); // ���� �ʱ�ȭ (�ܾ� ���� �б�)

		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		new MainScreen();
	}
	
	// ������ �̹��� �г�
	private class BackGround extends JPanel{
		private ImageIcon bgImg = new ImageIcon("img/background.png");
		
		@Override
        public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(bgImg.getImage(), 0, 0, this);
        }
	}
	
	// ��ŷ ���̾�α�
	private class RankingDialog extends JDialog{
		public RankingDialog(JFrame frame) {
			super(frame, "��ŷ");
			this.setSize(550, 500);
			Container c = getContentPane();
			c.add(new RankingPanel());
		}
		
		// �ؽ�Ʈ �׸��� �뵵
		private class RankingPanel extends JPanel{
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				g.setFont(new Font("����", Font.BOLD, 30));
				g.drawString("���", 20, 40);
				g.drawString("�̸�", 130, 40);
				g.drawString("����", 270, 40);
				g.drawString("��¥", 400, 40);
				
				g.setFont(new Font("����", Font.PLAIN, 20));
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
				
				// �� ���ھ ��ϵǾ� ������ �߰��� �׸���.
				Score myScore = Config.getMyScore();
				g.setFont(new Font("����", Font.BOLD, 20));
				
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
	
	// �ܾ� �߰� ���̾�α�
	private class WordDialog extends JDialog{
		private JTextField tf = new JTextField(15);
		private JList<String> scrollList = new JList<String>();
		private JButton add = new JButton("ADD");
		
		public WordDialog(JFrame frame) {
			super(frame, "�ܾ� �߰�");
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
		
		// ���̾�α� ���� ����
		public void updateWordDialog() {
			scrollList.setListData(WordManager.getWordVector());
		}
		
	}
	
	// �ɼ� ���̾�α�
	private class OptionDialog extends JDialog{
		private VolumeTab volumeTab = new VolumeTab();
		private GameTab gameTab = new GameTab();
		private ThreadTab thTab = new ThreadTab();
		private InfoTab infoTab = new InfoTab();

		public OptionDialog(JFrame frame) {
			super(frame, "�ɼ�");
			this.setSize(300, 300);
			Container c = getContentPane();
			c.add(createTabbedPane());
		}
		
		// �� �г� ���ۿ�
		private JTabbedPane createTabbedPane() {
			JTabbedPane pane = new JTabbedPane();
			pane.addTab("�Ҹ�", volumeTab);
			pane.addTab("����", gameTab);
			pane.addTab("������", thTab);
			pane.addTab("����", infoTab);
			return pane;
		}
		
		// �ɼ� ���� ���̺� ��ư
		private class optionSaveButton extends JButton{
			public optionSaveButton() {
				super("���� ����");
				this.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println(volumeTab.getSlider() + "," + gameTab.getSlider() + "," + thTab.getSlider());
						// ���� ���� ����
						Sound.setVolume((volumeTab.getSlider() - 140f) / 2);
						// �ɼ� ���� +2�� ������ �迭 �¿찡 �����ɷ� ����� ����
						Config.setArrSize(gameTab.getSlider() + 2);
						Config.setThreadTime(thTab.getSlider());
						OptionDialog.this.setVisible(false);
					}
				});
			}
		}
		
		// ���� ��
		private class VolumeTab extends JPanel{
			private JLabel sjl = new JLabel("���� ����");
			private JLabel sdjl = new JLabel("���� ������ �����մϴ�.");
			private JLabel svjl = new JLabel("�⺻ ����: 100 | ���� ����: 100");
			private JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);

			public VolumeTab() {
				this.setLayout(new GridLayout(5, 1, 5, 10));
				
				sjl.setFont(new Font("����", Font.BOLD, 20));
				sjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(sjl);
				
				sdjl.setFont(new Font("����", Font.PLAIN, 15));
				sdjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(sdjl);
				
				svjl.setFont(new Font("����", Font.PLAIN, 15));
				svjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(svjl);
				
				slider.setPaintLabels(true);
				slider.setPaintTicks(true);
				slider.setPaintTrack(true);
				slider.setMajorTickSpacing(10);
				slider.setMinorTickSpacing(5);
				slider.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						svjl.setText("�⺻ ����: 100 | ���� ����: " + Integer.toString(slider.getValue()));
					}
				});
				this.add(slider);
				this.add(new optionSaveButton());
			}
			
			public int getSlider() {
				return slider.getValue();
			}
		}
		
		// ���� �ɼ� ��
		private class GameTab extends JPanel{
			private JLabel tjl = new JLabel("���� ����");
			private JLabel tdjl = new JLabel("<html>���� ���� ũ���� ���������� ����ϴ�.<br>��õ ���� 20�� �Դϴ�.</html>");
			private JLabel tvjl = new JLabel("�⺻ ����: 20 | ���� ����: 20");
			private JSlider sslider = new JSlider(JSlider.HORIZONTAL, 10, 40, 20);
			
			public GameTab() {
				this.setLayout(new GridLayout(5, 1, 5, 10));
				
				tjl.setFont(new Font("����", Font.BOLD, 20));
				tjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(tjl);
				
				tdjl.setFont(new Font("����", Font.PLAIN, 15));
				tdjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(tdjl);
				
				tvjl.setFont(new Font("����", Font.PLAIN, 15));
				tvjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(tvjl);
				
				sslider.setPaintLabels(true);
				sslider.setPaintTicks(true);
				sslider.setPaintTrack(true);
				sslider.setMajorTickSpacing(10);
				sslider.setMinorTickSpacing(5);
				sslider.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						tvjl.setText("�⺻ ����: 20 | ���� ����: " + Integer.toString(sslider.getValue()));
					}
				});
				this.add(sslider);
				this.add(new optionSaveButton());
			}
			
			public int getSlider() {
				return sslider.getValue();
			}
		}
		
		// ������ ���� ��
		private class ThreadTab extends JPanel{
			private JLabel tjl = new JLabel("������ ����");
			private JLabel tdjl = new JLabel("<html>������ ���� ���� �����մϴ�.<br>��õ ���� 1000 �Դϴ�. 1000 = 1��</html>");
			private JLabel tvjl = new JLabel("�⺻ ����: 1000 | ���� ����: 1000");
			private JSlider sslider = new JSlider(JSlider.HORIZONTAL, 200, 2000, 1000);
			
			public ThreadTab() {
				this.setLayout(new GridLayout(5, 1, 5, 10));
				
				tjl.setFont(new Font("����", Font.BOLD, 20));
				tjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(tjl);
				
				tdjl.setFont(new Font("����", Font.PLAIN, 15));
				tdjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(tdjl);
				
				tvjl.setFont(new Font("����", Font.PLAIN, 15));
				tvjl.setHorizontalAlignment(JLabel.CENTER);
				this.add(tvjl);
				
				sslider.setPaintLabels(true);
				sslider.setPaintTicks(true);
				sslider.setPaintTrack(true);
				sslider.setMajorTickSpacing(200);
				sslider.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						tvjl.setText("�⺻ ����: 1000 | ���� ����: " + Integer.toString(sslider.getValue()));
					}
				});
				this.add(sslider);
				this.add(new optionSaveButton());
			}
			
			public int getSlider() {
				return sslider.getValue();
			}
		}
		
		// ���� ��
		private class InfoTab extends JPanel{
			private JLabel jl1 = new JLabel("������");
			private JLabel jl2 = new JLabel();

			public InfoTab() {
				this.setLayout(new GridLayout(5, 1, 5, 10));
				
				jl1.setFont(new Font("����", Font.BOLD, 20));
				jl1.setHorizontalAlignment(JLabel.CENTER);
				this.add(jl1);
				this.add(new JLabel());
				jl2.setFont(new Font("����", Font.PLAIN, 15));
				jl2.setHorizontalAlignment(JLabel.CENTER);
				jl2.setText("<html>" + "����ϼ���Ʈ����Ʈ��" + "<br>" + "1771059 ���ؼ�" + "</html>");
				this.add(jl2);
				this.add(new JPanel());
				this.add(new optionSaveButton());
			}
		}
	}
	
	// ���� ȭ�鿡 �ִ� jbutton �߻� Ŭ���� �׼ǿ� ���� �̹��� �ٲ�
	private abstract static class Button extends JButton{
		static ImageIcon normal = new ImageIcon("img/button_normal.png");
		static ImageIcon over = new ImageIcon("img/button_over.png");
		static ImageIcon press = new ImageIcon("img/button_press.png");
		static Font buttonFont = new Font("����", Font.BOLD, 30);
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
		
		// �̸� �׸��� �뵵 �׳� �����ϸ� �Ⱥ���
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
            FontMetrics metrics = g.getFontMetrics(buttonFont);
            int width = metrics.stringWidth(str);
			g.setFont(buttonFont);
			g.drawString(str, WIDTH / 2 - width / 2, 85);
		}
	}
	
	// ��ŸƮ ��ư
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
	
	// �ܾ� �߰� ��ư
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
	
	// ��ŷ ��ư
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

	// �ɼ� ��ư
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