import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;

// wordblock�� ��ü������ �����ϴ� class, static��
public class WordManager {
	// �ܾ� �����ϴ� �뵵�� vector
	private static Vector<String> wordVector = new Vector<String>(); 
	
	// �ʱ�ȭ (main screen���� ȣ��, �̹� �ѹ� �ߴٸ� �� ���ϰ� ����)
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
	
	// str�� �޾Ƽ� �ܾ �߰���
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
	
	// ������ �ܾ ��ȯ��. 
	public static String getRandomString() {
		String word;
		int arrSize = Config.getArrSize();
		
		if(Math.random() > 0.7) { // 30Ȯ���� ���� ��� ����
			word = "@";
		}
		else if(wordVector.size() > 0) { // �ܾ� ������ ���� �ε����� ������
			int randIndex = (int)(Math.random() * wordVector.size());
			String tmp = wordVector.get(randIndex);
			wordVector.remove(randIndex);
			word = tmp;
		}
		else{ // �ܾ� �� �������� ���� ���� ������
			word = "YOUWIN";
		}
		
		// �ܾ� ũ�Ⱑ �������� ũ�⺸�� �� ��� ©�� ������
		if(word.length() < arrSize - 2) {
			return word;
		}
		else {
			System.out.println(word.substring(0, arrSize - 2));
			return word.substring(0, arrSize - 2);
		}
	}
	
	// ���� ��� ���� �޾ƿ���
	public static WordBlock getWordBlock() {
		return new WordBlock(getRandomString());
	}
	
	// �ܾ� �߰��ϴ� ����Ʈ�� ǥ���ϴ� �뵵
	public static Vector<String> getWordVector() {
		return wordVector;
	}

}

// �ܾ� ��� ��ü
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