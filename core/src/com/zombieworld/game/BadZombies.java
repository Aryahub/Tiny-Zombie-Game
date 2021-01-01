package com.zombieworld.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

import javax.naming.Context;

public class BadZombies extends ApplicationAdapter {
	SpriteBatch batch;
	Texture [] zombie;
	Texture background;
	Texture brain;
	Texture bomb;
	Texture death;
	int zombieState = 0;
	int pause = 0;
	float gravity = 0.4f;
	float velocity = 0;
	int zombieY = 0;
	ArrayList<Integer> brainXs = new ArrayList<>();
	ArrayList<Integer>brainYs = new ArrayList<>();
	ArrayList<Rectangle> brainRectangles = new ArrayList<>();
	ArrayList<Integer>bombXs = new ArrayList<>();
	ArrayList<Integer>bombYs = new ArrayList<>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
	Rectangle characterRectangle;
	int bombCount;
	int brainCount;
	Random random;
	int score = 0;
	BitmapFont font;
	int gameState = 0 ;
	Music bgMusic;
	Sound eatingSound;
	Sound deathSound;
	Sound tapSound;
	Preferences preferences;
	BitmapFont highScore;
	FreeTypeFontGenerator fontGenerator;
	FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
	
	
	
	public void makeBrain(){
		float height = random.nextFloat()*Gdx.graphics.getHeight();
		brainYs.add((int) height);
		brainXs.add(Gdx.graphics.getWidth());
	}
	public void makeBomb(){
		float height = random.nextFloat() *Gdx.graphics.getHeight();
		bombYs.add((int) height);
		bombXs.add(Gdx.graphics.getWidth());
	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		zombie = new Texture[18];
		background = new Texture("bg1.jpg");
		brain = new Texture("brain.png");
		bomb = new Texture("bomb.png");
		death = new Texture("Dying_014.png");
		random = new Random();
		bgMusic = Gdx.audio.newMusic(Gdx.files.internal("bgSound1.mp3"));
		eatingSound = Gdx.audio.newSound(Gdx.files.internal("eating2.ogg"));
		deathSound = Gdx.audio.newSound(Gdx.files.internal("deathSound1.wav"));
		tapSound = Gdx.audio.newSound(Gdx.files.internal("tapSound1.mp3"));
		fontGenerator  = new FreeTypeFontGenerator(Gdx.files.internal("font1.ttf"));
		fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParameter.size = 60;
		fontParameter.borderWidth =5;
		fontParameter.borderColor =Color.BLACK;
		fontParameter.color = Color.FOREST;// olive forest light-gray white coral
		font = fontGenerator.generateFont(fontParameter);
		for(int i =0 ;i< zombie.length;i++){
			zombie[i] = new Texture("Walking_"+i+".png");
		}
		zombieY = Gdx.graphics.getHeight()/2;
		highScore = fontGenerator.generateFont(fontParameter);
		bgMusic.setVolume(0.8f);
		bgMusic.setLooping(true);
		preferences = Gdx.app.getPreferences("HighScore");
		preferences.putInteger("HighScore",0);
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		if(gameState==1){
			//game is live
			//bombs
			if(bombCount < 250){ // bomb frequency
				bombCount++;
			}
			else{
				bombCount = 0;
				makeBomb();
			}
			bombRectangles.clear();
			for(int i = 0 ; i< bombXs.size();i++){
				batch.draw(bomb,bombXs.get(i),bombYs.get(i));
				bombXs.set(i,bombXs.get(i)-14); // bomb Speed
				bombRectangles.add(new Rectangle(bombXs.get(i),bombYs.get(i),bomb.getWidth(),bomb.getHeight()));
			}
			//brain
			if(brainCount<100){  // brain frequency
				brainCount++;
			}
			else{
				brainCount=0;
				makeBrain();
			}
			brainRectangles.clear();
			for(int i = 0 ; i< brainXs.size();i++){
				batch.draw(brain,brainXs.get(i),brainYs.get(i));
				brainXs.set(i,brainXs.get(i)-10);  // brain Speed
				brainRectangles.add(new Rectangle(brainXs.get(i),brainYs.get(i),brain.getWidth(),brain.getHeight()));
			}


			if(Gdx.input.justTouched()){
				velocity = -12;
				tapSound.play(1.0f);
				tapSound.setPitch(tapSound.play(1.0f),2);
				tapSound.setLooping(tapSound.play(1.0f),false);
			}
			velocity += gravity;
			zombieY -= velocity;
			if(zombieY <=50){
				zombieY = 50;
			}
			if(pause<2){  // Zombie Speed
				pause++;
			}
			else {
				pause = 0;
				if (zombieState < 17) {
					zombieState++;
				} else {
					zombieState = 0;
				}
			}
		}
		else if(gameState == 0){
			// waiting to start
			if(Gdx.input.justTouched()){
				gameState = 1;
				bgMusic.play();
			}
		}
		else if (gameState == 2){
			//Game Over
			if(Gdx.input.justTouched()) {

				gameState = 1;
				bgMusic.play();
				zombieY = Gdx.graphics.getHeight() / 2;
				score = 0;
				velocity = 0;
				brainXs.clear();
				brainYs.clear();
				brainRectangles.clear();
				brainCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombRectangles.clear();
				bombCount = 0;
			}
		}

		if (gameState == 2){
			batch.draw(death,Gdx.graphics.getWidth() / 5 - zombie[zombieState].getWidth() / 2, zombieY);
		} else {
			batch.draw(zombie[zombieState], Gdx.graphics.getWidth() / 5 - zombie[zombieState].getWidth() / 2, zombieY);
		}
		characterRectangle = new Rectangle(Gdx.graphics.getWidth()/5-zombie[zombieState].getWidth()/2,zombieY,zombie[zombieState].getWidth()/2,zombie[zombieState].getHeight());
		for(int i=0 ;i< brainRectangles.size();i++){
			if(Intersector.overlaps(characterRectangle,brainRectangles.get(i))){
				score++;
				if(score > preferences.getInteger("HighScore",0)){
					preferences.putInteger("HighScore",score);
					preferences.flush();
				}
				eatingSound.play(1.0f);
				eatingSound.setPitch(eatingSound.play(1.0f),2);
				eatingSound.setLooping(eatingSound.play(1.0f),false);
				brainRectangles.remove(i);
				brainXs.remove(i);
				brainYs.remove(i);
				break;
			}
		}
		for(int i=0 ;i< bombRectangles.size();i++){
			if(Intersector.overlaps(characterRectangle,bombRectangles.get(i))){
				gameState = 2;
				bombRectangles.remove(i);
				bombXs.remove(i);
				bombYs.remove(i);
				long id = deathSound.play(1.0f);
				deathSound.setPitch(id,2);
				deathSound.setLooping(id,false);
				bgMusic.stop();
				break;
			}
		}
		font.draw(batch,"Brains x "+String.valueOf(score),70,1000);
		highScore.draw(batch,"Highscore :"+String.valueOf(preferences.getInteger("HighScore",0)),Gdx.graphics.getWidth()-450,1000);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		eatingSound.dispose();
		deathSound.dispose();
		bgMusic.dispose();
		tapSound.dispose();
	}

}
