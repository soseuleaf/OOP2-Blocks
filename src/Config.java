import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ���� ��ü ��ü������ �񱳸� ���� comparable �������̽� ��ӹ���
class Score implements Comparable<Score>{
	private String name = null;
	private int score = 0;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	private LocalDate time = null;
	
	public Score(String name, int score, LocalDate time) {
		setName(name);
		setScore(score);
		setTime(time);
	}
	
	public Score(String name, int score, String time) {
		setName(name);
		setScore(score);
		setTime(LocalDate.parse(time, formatter));
	}
	
	// ��ǥ ������ �ܾ� �ڸ��� ���� ���������� ��ǥ �ڸ�
	public void setName(String name) {
		if(name.contains(",")) {
			name = name.replaceAll(",", "");
		}
		this.name = name;
	}
	
	// ��ȯ�� �Լ���
	public void setScore(int score) {
		this.score = score;
	}
	
	public void setTime(LocalDate time) {
		this.time = time;
	}
	
	public String getName() {
		return name;
	}
	
	public int getScore() {
		return score;
	}
	
	public String getTime() {
		return (time.format(formatter));
	}
	
	// �׳� �迭 ��� Ȯ�ο�
	@Override
	public String toString() {
		return name + "," + Integer.toString(score) + "," + getTime();
	}
	
	// ��ü �񱳿�
	@Override
    public int compareTo(Score other) {
        return other.getScore() - getScore();
    }
}

// ȯ�� ���� class staitc��
public class Config {
	// �ߺ� �̸� ������ list�� ������
	private static List<Score> scoreList = new ArrayList<>();
	private static int threadTime = 1000;
	private static int arrSize = 20;
	// �ڽ��� �ش� ���ӿ��� ����� ���� ������
	private static Score myScore = null;
	
	// ���� ȭ�鿡�� �ʱ�ȭ�� �ϴµ�, ���ư� ������ �ʱ�ȭ�Ǳ� ������ ������ üũ
	public static void intiate() {
		if(scoreList.size() != 0) return;
		try {
			File file = new File("txt/score.txt");
			BufferedReader bufReader = new BufferedReader(new FileReader(file));

			String line = "";
	        while((line = bufReader.readLine()) != null){
	        	String[] tmp = line.split(",");
	        	scoreList.add(new Score(tmp[0], Integer.parseInt(tmp[1]), tmp[2]));
	        }           
	        bufReader.close();
		} catch (Exception e) {
			return;
		}
		
		// �а� ������
		Collections.sort(scoreList);
	}
	
	// ����Ʈ�� ���� �߰��ϴ� �뵵�� �Լ�
	public static void addScore(String name, int score) {
		Score tmp = new Score(name, score, LocalDate.now());
		
		try {
			File file = new File("txt/score.txt");
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter(file, true));
			bufWriter.write("\r\n");
			bufWriter.write(tmp.toString());
			scoreList.add(tmp);
			bufWriter.close();
		} catch (Exception e) {
			return;
		}
	}
	
	public static void addScore(Score score) {
		try {
			File file = new File("txt/score.txt");
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter(file, true));
			bufWriter.write("\r\n");
			bufWriter.write(score.toString());
			scoreList.add(score);
			bufWriter.close();
		} catch (Exception e) {
			return;
		}
	}
	
	// ��ȯ�� �Լ�
	public static void setThreadTime(int tt) {
		threadTime = tt;
	}
	
	public static int getThreadTime() {
		return threadTime;
	}
	
	public static void setArrSize(int as) {
		arrSize = as;
	}
	
	public static int getArrSize() {
		return arrSize;
	}
	
	public static Score getMyScore() {
		Collections.sort(scoreList);
		return myScore;
	}
	
	public static Score getScore(int index) {
		Collections.sort(scoreList);
		return scoreList.get(index);
	}
	
	// �ڽ��� ���� ����ϴ� �Լ� �ѹ� ��� �س��ٸ� ���ؼ� ���� ���� ������ ���̰� ��
	public static void addMyScore(String name, int score) {
		Score tmp = new Score(name, score, LocalDate.now());
		
		if(myScore == null) {
			myScore = tmp;
			return;
		}
		
		int result = myScore.compareTo(tmp);
		
		if(result > 0) {
			myScore = tmp;
		}
		else if (result == 0){
			myScore = tmp;
		}
		else {
			return;
		}
	}
	
	// �ڽ��� ��ŷ ��ȯ
	public static int getRanking(String name) {
		for(int i = 0; i < scoreList.size(); i++) {
			if(scoreList.get(i).getName().equals(name)){
				return i;
			}
		}
		return -1;
	}
}
