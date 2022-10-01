import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;

// wordblock을 전체적으로 관리하는 class, static임
public class WordManager {
	// 단어 관리하는 용도의 vector
	private static Vector<String> wordVector = new Vector<String>(); 
	
	// 초기화 (main screen에서 호출, 이미 한번 했다면 더 안하고 리턴)
	public static void initiate() {
		if(wordVector.size() != 0) return;
		
		try {
			File file = new File("txt/basic.txt");
			FileReader filereader = new FileReader(file);
			BufferedReader bufReader = new BufferedReader(filereader);

			String line = "";
            while((line = bufReader.readLine()) != null){
            	wordVector.add(line);
            }           
            bufReader.close();

		} catch (Exception e) {
			return;
		}
	}
	
	// str을 받아서 단어를 추가함
	public static void addWord(String str) {
		try {
			File file = new File("txt/basic.txt");
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter(file, true));
			bufWriter.write("\r\n");
			bufWriter.write(str);
			wordVector.add(str);
			bufWriter.close();
		} catch (Exception e) {
			return;
		}
	}
	
	// 랜덤한 단어를 반환함. 
	public static String getRandomString() {
		String word;
		int arrSize = Config.getArrSize();
		
		if(Math.random() > 0.7) { // 30확률로 찬스 블록 투입
			word = "@";
		}
		else if(wordVector.size() > 0) { // 단어 있으면 랜덤 인덱스로 보내줌
			int randIndex = (int)(Math.random() * wordVector.size());
			String tmp = wordVector.get(randIndex);
			wordVector.remove(randIndex);
			word = tmp;
		}
		else{ // 단어 다 떨어지면 대충 만들어서 보내줌
			word = "YOUWIN";
		}
		
		// 단어 크기가 스테이지 크기보다 길 경우 짤라서 보내줌
		if(word.length() < arrSize - 2) {
			return word;
		}
		else {
			System.out.println(word.substring(0, arrSize - 2));
			return word.substring(0, arrSize - 2);
		}
	}
	
	// 워드 블록 만들어서 받아오기
	public static WordBlock getWordBlock() {
		return new WordBlock(getRandomString());
	}
	
	// 단어 추가하는 리스트에 표기하는 용도
	public static Vector<String> getWordVector() {
		return wordVector;
	}

}

// 단어 블록 객체
class WordBlock {
	private String wordStr = "test";
	private Color blockColor = null;
	
	public static Color[] colorArray = { 
			new Color(255, 134, 66),
			new Color(244, 220, 181),
			new Color(195, 183, 172),
			new Color(177, 221, 161),
			new Color(60, 214, 230),
			new Color(200, 112, 126),
			new Color(172, 153, 193),
	};
	
	public WordBlock(String str) {
		this.wordStr = str;
		this.blockColor = WordBlock.colorArray[(int)(Math.random()*7)];
	}
	
	public int getLen() {
		return wordStr.length();
	}
	
	public String getString() {
		return wordStr;
	}
	
	public Color getColor() {
		return blockColor;
	}
}