package edu.lewisu.cs.hdondiego;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

abstract class CameraEffect {
    protected OrthographicCamera cam;
    protected int duration, progress;
    protected ShapeRenderer renderer;
    protected SpriteBatch batch;
    public CameraEffect(OrthographicCamera cam, int duration, 
    SpriteBatch batch, ShapeRenderer renderer) {
        this.cam = cam;
        this.duration = duration;
        this.batch = batch;
        this.renderer = renderer;
        progress = duration;
    }
    public boolean isActive() {
        return (progress<duration);
    }
    public abstract void play();
    public void updateCamera() {
        cam.update();
        if (renderer != null) {
            renderer.setProjectionMatrix(cam.combined);
        }
        if (batch != null) {
            batch.setProjectionMatrix(cam.combined);
        }
    }
    public void start() {
        progress = 0;
    }
}

class CameraZoom extends CameraEffect {
    private int intensity;
    private int speed;
    public int getIntensity() {
        return intensity;
    }
    public void setIntensity(int intensity) {
        if (intensity < 0) {
            this.intensity = 0;
        } else {
            this.intensity = intensity;
        }
    }
    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        if (speed < 0) {
            speed = 0;
        } else {
            if (speed > duration) {
                speed = duration / 2;
            } else {
                this.speed = speed;
            }
        }
    }
    @Override
    public boolean isActive() {
        return super.isActive() && speed > 0;
    }
    public CameraZoom(OrthographicCamera cam, int duration, SpriteBatch batch,
    ShapeRenderer renderer, int intensity, int speed) {
        super(cam,duration,batch,renderer);
        setIntensity(intensity);
        setSpeed(speed);
    }
    
    @Override
    public void play() {
        if (isActive()) {
        	cam.zoom -= 0.1; // zooming in by 0.10
            updateCamera();
        }
    }
    @Override
    public void start() {
        super.start();
        cam.zoom -= 0.1;
        updateCamera();
    }
}

/**
 * This class represents an object that appears on the screen. 
 * It consists of a label. When the user clicks it, it triggers 
 * a sound to be played. Each SoundLabel object has a Label that
 * appears on the screen that the user will click, and a Sound
 * that will be played when it is clicked.
 */

class SoundLabel {
	private Label label;
	private Sound sound;

	public Sound getSound() {
		return sound;
	}
	
	public Label getLabel() {
		return label;
	}
	/**
	 * This sets up a SoundLabel that is ready to be clicked and play sounds
	 * @param pathToSound where the sound file is located
	 * @param textToShow the text to show on the screen
	 * @param style the font to use (in a nutshell)
	 * @param xpos xcoord where label will appear
	 * @param ypos ycoord where label will appear
	 */
	public SoundLabel(String pathToSound, String textToShow, LabelStyle style,
	int xpos, int ypos) {
		sound = Gdx.audio.newSound(Gdx.files.internal(pathToSound));
		label = new Label(textToShow,style);
		label.setPosition(xpos,ypos);
	}
	/**
	 * plays sound at max value
	 */
	public void playSound() {
		sound.play();
	}
	/**
	 * plays sound at requested volume
	 * @param vol the requested volume (between 0 and 1)
	 */
	public void playSound(float vol) {
		sound.play(vol);
	}
	/**
	 * This determines if the label was clicked
	 * @param x where the mouse's x coordinate is
	 * @param y where the mouse's y coordinate is
	 * @return true if x,y lie within the label's area
	 */
	public boolean wasClicked(int x, int y) {
		if (x >= label.getX() && x <= label.getX() + label.getWidth() &&
		y >= label.getY() && y <= label.getY() + label.getHeight()) {
			return true;
		} else {
			return false;
		}
	}
}

class VolumeButton {
	private Texture btn;
	private int xPos, yPos;
	
	// checks if the volume button was clicked
	public boolean wasClicked(int x, int y) {
		if (x >= xPos && x <= xPos + btn.getWidth() &&
			y >= yPos && y <= yPos + btn.getHeight()) {
			return true;
		} else {
			return false;
		}
	}
	
	public Texture getTexture() {
		return btn;
	}
	
	public int getXPos() {
		return xPos;
	}
	
	public int getYPos() {
		return yPos;
	}
	
	public VolumeButton(String filename, int x, int y) {
		btn = new Texture(filename);
		xPos = x;
		yPos = y;
	}
}

public class SoundBoard extends ApplicationAdapter {
	SpriteBatch batch;
	Texture board, music_note, hotdog, hotdog_reverse, mj_moonwalk, hot_surface, chunli_kicks, wii, empty_donut_box;
	Texture lego, football, big_mac, airplane, taco_bell, oil, coffee, weed, flag, dog;
	OrthographicCamera cam;
	int WIDTH, HEIGHT;
	Label titleLabel, descLabel, volumeLabel;
	LabelStyle blLabelStyle, whLabelStyle, titleLabelStyle, descLabelStyle;
	SoundLabel soundLabel, soundLabel1, soundLabel2, soundLabel3, soundLabel4, soundLabel5, soundLabel6, soundLabel7, soundLabel8;
	SoundLabel soundLabel9, soundLabel10, soundLabel11, soundLabel12, soundLabel13;
	ArrayList<SoundLabel> soundLabels;
	VolumeButton volumeUp, volumeDown;
	int volume, startX, startY, endX, endY, musicX, musicY;
	float slope;
	boolean moveMusicNote;
	CameraZoom cameraZoom;
	Sound dealOrNoDealSound;
	
	public void setupLabelStyle() {
		blLabelStyle = new LabelStyle();
		blLabelStyle.font = new BitmapFont(Gdx.files.internal("font/gill_sans_mt_black_15pt.fnt"));
		whLabelStyle = new LabelStyle();
		whLabelStyle.font = new BitmapFont(Gdx.files.internal("font/gill_sans_mt_white_15pt.fnt"));
	}
	/**
	 * render the soundLabel on the screen
	 */
	public void drawSoundLabel() {
		for (SoundLabel sl : soundLabels) {
			sl.getLabel().draw(batch,1);
		}
	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		board = new Texture("board_custom.png");
		volumeDown = new VolumeButton("volume_down.png", 820, 10);
		volumeUp = new VolumeButton("volume_up.png", 915, 10);
		hotdog = new Texture("cool_sc_hotdog.png");
		hotdog_reverse = new Texture("cool_sc_hotdog_reverse.png");
		mj_moonwalk = new Texture("mj_moonwalk.png");
		hot_surface = new Texture("hot_surface.png");
		chunli_kicks = new Texture("chunli_kicks.png");
		empty_donut_box = new Texture("empty_donut_box.png");
		lego = new Texture("lego.png");
		football = new Texture("football.png");
		big_mac = new Texture("big_mac.png");
		taco_bell = new Texture("taco bell.jpg");
		oil = new Texture("oil.jpg");
		coffee = new Texture("coffee.jpg");
		weed = new Texture("weed.jpg");
		flag = new Texture("american_flag.jpg");
		dog = new Texture("dog.jpg");
		
		airplane = new Texture("airplane.png");
		music_note = new Texture("music_note.png");
		wii = new Texture("wii.jpg");
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		volume = 100;
		startX = 0;
		startY = 0;
		musicX = 0;
		musicY = 0;
		endX = 0;
		endY = 0;
		slope = 0.0f;
		moveMusicNote = false;
		System.out.printf("WIDTH: %d\nHEIGHT: %d", WIDTH, HEIGHT);
		System.out.printf("\nBoard Width: %d\nBoard Height: %d\nBoard Depth: %d\n", board.getWidth(), board.getHeight(), board.getDepth());
		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.translate(WIDTH/2, HEIGHT/2);
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		setupLabelStyle();
		
		cam.zoom = 10.0f;
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		
		cameraZoom = new CameraZoom(cam, 3, batch, null, 5, 5);
		dealOrNoDealSound = Gdx.audio.newSound(Gdx.files.internal("audio/deal_or_no_deal_short.mp3"));
		
		titleLabelStyle = new LabelStyle();
		titleLabelStyle.font = new BitmapFont(Gdx.files.internal("font/haettenschweiler_black_35pt.fnt"));
		titleLabel = new Label("The American Culture Game", titleLabelStyle);
		titleLabel.setPosition(330, 680);
		descLabelStyle = new LabelStyle();
		descLabelStyle.font = new BitmapFont(Gdx.files.internal("font/gill_sans_mt_black_20pt.fnt"));
		descLabel = new Label("The more things you recognize, the more American-ly cultured and legendary you are.", descLabelStyle);
		descLabel.setPosition(125, 635);
		volumeLabel = new Label("Volume: " + (float)volume/100, blLabelStyle);
		volumeLabel.setPosition(850, 45);
		
		soundLabels = new ArrayList<>();
		soundLabel = new SoundLabel("audio/hehe_michael_jackson.mp3", "Message from our King", blLabelStyle, 155, 465);
		soundLabels.add(soundLabel);
		soundLabel1 = new SoundLabel("audio/ouu_michael_jackson.mp3", "PSA from our King", whLabelStyle, 345, 465);
		soundLabels.add(soundLabel1);
		soundLabel2 = new SoundLabel("audio/chunli_kicks.mp3", "Taste of D-feet", blLabelStyle, 520, 465);
		soundLabels.add(soundLabel2);
		soundLabel3 = new SoundLabel("audio/wii_sports_theme.mp3", "Musical Masterpiece\n of the Century", whLabelStyle, 670, 460);
		soundLabels.add(soundLabel3);
		soundLabel4 = new SoundLabel("audio/doh_homer.mp3", "When you realize you ate\n all the donuts", whLabelStyle, 145, 310);
		soundLabels.add(soundLabel4);
		soundLabel5 = new SoundLabel("audio/lego_starwars_death.mp3", "Turning in homework\n last minute", blLabelStyle, 325, 310);
		soundLabels.add(soundLabel5);
		soundLabel6 = new SoundLabel("audio/fox_sports.mp3", "Every sports fan's anthem", whLabelStyle, 485, 310);
		soundLabels.add(soundLabel6);
		soundLabel7 = new SoundLabel("audio/im_lovin_it.mp3", "America's Cuisine", blLabelStyle, 675, 310);
		soundLabels.add(soundLabel7);
		soundLabel8 = new SoundLabel("audio/ill_be_back.mp3", "After eating Taco Bell", blLabelStyle, 155, 170);
		soundLabels.add(soundLabel8);
		soundLabel9 = new SoundLabel("audio/the_avengers.mp3", "country: *finds oil*\namerica:", whLabelStyle, 335, 160);
		soundLabels.add(soundLabel9);
		soundLabel10 = new SoundLabel("audio/mario_power_up.mp3", "After Dr. Klump\n drinks coffee", blLabelStyle, 515, 160);
		soundLabels.add(soundLabel10);
		soundLabel11 = new SoundLabel("audio/spongebob_laugh.mp3", "Everybody on 4/20 day", whLabelStyle, 665, 160);
		soundLabels.add(soundLabel11);
		soundLabel12 = new SoundLabel("audio/never_gonna_give_you.mp3", "Mariah Carey's\n National Anthem Cover", whLabelStyle, 155, 15);
		soundLabels.add(soundLabel12);
		soundLabel13 = new SoundLabel("audio/mission_failed.mp3", "When your dog ate\n your programming\n homework doesn't work", blLabelStyle, 320, 10);
		soundLabels.add(soundLabel13);
		dealOrNoDealSound.play();

	}

	// linkedhashmap or arraylist - for the many SoundLabelObjects
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(cam.zoom > 1.0f) {
			cameraZoom.start();
		}
		cameraZoom.play();
		
		batch.begin();
		
		if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
			System.out.printf("Mouse Click X: %d, Mouse Click Y: %d\n", Gdx.input.getX(), Gdx.input.getY());
			if (volumeUp.wasClicked(Gdx.input.getX(), HEIGHT - Gdx.input.getY())) { //HEIGHT-Gdx.input.getY()
				if (volume < 100) {
					volume += 10;
					volumeLabel.setText("Volume: " + (float)volume/100);
				}
			}
			
			if (volumeDown.wasClicked(Gdx.input.getX(), HEIGHT - Gdx.input.getY())) { // HEIGHT-Gdx.input.getY()
				if (volume > 0) {
					volume -= 10;
					volumeLabel.setText("Volume: " + (float)volume/100);
				}
			}
			
			for (SoundLabel sl : soundLabels) {
				if (sl.wasClicked(Gdx.input.getX(), HEIGHT-Gdx.input.getY())) {
					endX = Gdx.input.getX();
					endY = Gdx.input.getY();
					slope = (float)(endY - startY)/(endX - startX);
					//batch.draw(music_note, musicX, slope * (musicX + startX));
					moveMusicNote = true;
					sl.playSound((float)volume/100);
				}
			}
		}
		
		batch.draw(board, 0, 0);
		batch.draw(volumeDown.getTexture(), volumeDown.getXPos(), volumeDown.getYPos()); // 820, 10
		batch.draw(volumeUp.getTexture(), volumeUp.getXPos(), volumeUp.getYPos()); // 915, 10
		batch.draw(hotdog, 10, 260, 120, 120);
		batch.draw(hotdog_reverse, 828, 260, 120, 120);
		drawSoundLabel();
		batch.draw(mj_moonwalk, 170, 480, 100, 110);
		batch.draw(empty_donut_box, 170, 345, 100, 110);
		batch.draw(lego, 345, 345, 100, 100);
		batch.draw(football, 515, 345, 100, 100);
		batch.draw(big_mac, 680, 345, 100, 100);
		batch.draw(hot_surface, 350, 490, 100, 100);
		batch.draw(chunli_kicks, 500, 490, 100, 100);
		batch.draw(taco_bell, 170, 200, 100, 80);
		batch.draw(oil, 340, 200, 100, 80);
		batch.draw(coffee, 515, 200, 100, 80);
		batch.draw(weed, 680, 200, 100, 80);
		batch.draw(flag, 175, 55, 100, 80);
		batch.draw(dog, 345, 65, 100, 80);
		batch.draw(wii, 680, 495, 100, 100);
		titleLabel.draw(batch,1);
		descLabel.draw(batch,1);
		volumeLabel.draw(batch, 1);
		if (moveMusicNote) {
			musicX++;
			if ((musicX != endX) && (musicY != endY)) {
				musicY = (int)((slope * musicX) + startX);
				batch.draw(music_note, musicX, musicY, 50, 50);
			} else {
				moveMusicNote = false;
				startX = endX;
				startY = endY;
			}
		} else {
			batch.draw(music_note, musicX, musicY, 50, 50);
		}
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		board.dispose();
		hotdog.dispose();
		hotdog_reverse.dispose();
		mj_moonwalk.dispose();
		hot_surface.dispose();
		chunli_kicks.dispose();
		wii.dispose();
	}
}
