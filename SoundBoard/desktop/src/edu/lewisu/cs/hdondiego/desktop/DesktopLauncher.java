package edu.lewisu.cs.hdondiego.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;

import edu.lewisu.cs.hdondiego.SoundBoard;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 960;
		config.height = 720;
		config.initialBackgroundColor = Color.WHITE;
		new LwjglApplication(new SoundBoard(), config);
	}
}
