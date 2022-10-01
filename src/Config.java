import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 점수 객체 객체끼리의 비교를 위해 comparable 인터페이스 상속받음
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
	
	// 쉼표 때문에 단어 자르는 에러 방지용으로 쉼표 자름
	public void setName(String name) {
		if(name.contains(",")) {
			name = name.replaceAll(",", "");
		}
		this.name = name;
	}
	
	// 반환용 함수들
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
	
	// 그냥 배열 출력 확인용
	@Override
	public String toString() {
		return name + "," + Integer.toString(score) + "," + getTime();
	}
	
	// 객체 비교용
	@Override
    public int compareTo(Score other) {
        return other.getScore() - getScore();
    }
}

// 환경 설정 class staitc임
public class Config {
	// 중복 이름 때문에 list로 관리함
	private static List<Score> scoreList = new ArrayList<>();
	private static int threadTime = 1000;
	private static int arrSize = 20;
	// 자신이 해당 게임에서 등록한 점수 관리용
	private static Score myScore = null;
	
	// 메인 화면에서 초기화를 하는데, 돌아갈 때마다 초기화되기 방지용 사이즈 체크
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
		
		// 읽고 정렬함
		Collections.sort(scoreList);
	}
	
	// 리스트에 점수 추가하는 용도의 함수
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
	
	// 반환용 함수
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
	
	// 자신이 점수 등록하는 함수 한번 등록 해놨다면 비교해서 가장 높은 점수로 보이게 함
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
	
	// 자신의 랭킹 반환
	public static int getRanking(String name) {
		for(int i = 0; i < scoreList.size(); i++) {
			if(scoreList.get(i).getName().equals(name)){
				return i;
			}
		}
		return -1;
	}
}
