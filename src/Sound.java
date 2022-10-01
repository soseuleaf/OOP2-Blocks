import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem; 
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound{	
	static File blockMoved = new File("media/move.wav");
	static File blockFixed = new File("media/put.wav");
	static File blockRemove = new File("media/remove.wav");
	static File blockClear = new File("media/clear.wav");
	static File wordSuccess = new File("media/correct.wav");
	static File wordFail = new File("media/wrong.wav");
	static File mouseOver = new File("media/over.wav");
	static File gameOver = new File("media/gameover.wav");
	
	static AudioInputStream audioStream;
	static FloatControl volumeControl;
	static Clip clip;
	static Float volume = -5.0f;
	
	public static void setVolume(float v) {
		volume = v;
	}
	
	public static void setSound() {
		volumeControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
		volumeControl.setValue(volume);
	}
	
	public static void playMoved() {
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(blockMoved));
			setSound();
			clip.start();
		} catch (Exception e) {
			return;
		}
	}
	
	public static void playFixed() {
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(blockFixed));
			setSound();
			clip.start();
		} catch (Exception e) {
			return;
		}
	}
	
	public static void playRemove() {
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(blockRemove));
			setSound();
			clip.start();
		} catch (Exception e) {
			return;
		}
	}
	
	public static void playClear() {
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(blockClear));
			setSound();
			clip.start();
		} catch (Exception e) {
			return;
		}
	}
	
	public static void playSuccess() {
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(wordSuccess));
			setSound();
			clip.start();
		} catch (Exception e) {
			return;
		}
	}
	
	public static void playFail() {
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(wordFail));
			setSound();
			clip.start();
		} catch (Exception e) {
			return;
		}
	}
	
	public static void playMouseOver() {
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(mouseOver));
			setSound();
			clip.start();
		} catch (Exception e) {
			return;
		}
	}
	
	public static void playGameOver() {
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(gameOver));
			setSound();
			clip.start();
		} catch (Exception e) {
			return;
		}
	}
}