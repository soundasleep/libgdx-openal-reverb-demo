/**
 * 
 */
package org.jevon.onitest.gdx.desktop;

import java.util.Random;

import org.jevon.onitest.gdx.desktop.audio.ReverbOpenALAudio;
import org.jevon.onitest.gdx.desktop.audio.ReverbSound;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Copied from
 * https://github.com/abvadabra/libgdx-audio-effects/blob/master/core/src/demo/DemoApplication.java
 * 
 * @author Jevon
 *
 */
public class OpenALDemoApplication extends ApplicationAdapter {

	public static void main(String[] args) {
		Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
		// cfg.useGL20 = true;
		cfg.setWindowedMode(1280 + 1, 960 + 1);
		cfg.setResizable(true);
		new Lwjgl3Application(new OpenALDemoApplication(), cfg);
	}

	private SpriteBatch batch;
	private BitmapFont font;

	private ReverbSound[] stepSounds = new ReverbSound[4];
	private int updateTick = 0;
	private Random rand = new Random();

	private int reverbPower = 100;
	private int cutoff = 100;
	private int directGain = 100;

	@Override
	public void create() {
		ReverbOpenALAudio.createInstance();

		batch = new SpriteBatch();
		font = new BitmapFont();

		for (int i = 0; i < stepSounds.length; i++)
			stepSounds[i] = ReverbOpenALAudio.instance
					.newSound(Gdx.files.internal("sounds/ultimate-survival/Stone Impact " + (i + 2) + ".wav"));
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void render() {
		super.render();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		batch.begin();
		font.draw(batch, "Reverb power (wet): " + (reverbPower) + "% (Use Up/Down to change)", 100, 100);
		font.draw(batch, "Low Pass Cutoff: " + (cutoff) + "% (Use Left/Right to change)", 100, 140);
		font.draw(batch, "Direct gain (dry): " + (directGain) + "% (Use A/D to change)", 100, 180);
		batch.end();

		if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
			reverbPower = Math.min(100, reverbPower + 5);
		if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN))
			reverbPower = Math.max(0, reverbPower - 5);
		if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
			cutoff = Math.min(100, cutoff + 5);
		if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
			cutoff = Math.max(0, cutoff - 5);
		if (Gdx.input.isKeyJustPressed(Input.Keys.A))
			directGain = Math.max(0, directGain - 5);
		if (Gdx.input.isKeyJustPressed(Input.Keys.D))
			directGain = Math.min(100, directGain + 5);

		if (updateTick++ % 100 == 0) {
			ReverbSound stepSound = stepSounds[rand.nextInt(stepSounds.length)];
			stepSound.setSettings(reverbPower / 100F, cutoff / 100F, directGain / 100f);
			stepSound.play(0.5f); // volume also affects reverb
		}
	}
}
