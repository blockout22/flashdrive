package com.object;

import javax.vecmath.Vector3f;

import com.mesh.Mesh;
import com.mesh.MeshObject;

public class ObjectPlayer extends MeshObject {

	//reset to 0 after each day
	private int secondsOld = 0;
	private int daysOld = 0;
	private int yearsOld = 0;
	private int health = 100;
	private float ageSpeed = 150;
	
	private final int SECONDS_IN_DAY = 86400;
	private final int DAYS_IN_YEAR = 365;
	private final int TEEN_AGE = 13;
	private final int ADULT_AGE = 18;

	private long lastTime = System.currentTimeMillis();
	private long curTime = System.currentTimeMillis();
	
	//which value to update each timeStep, seconds, days?
	private int UPDATE_STATE = 2;
	
	//child teen adult?
	private int LIFE_STATE = 0;

	public ObjectPlayer(Mesh mesh, Vector3f position, Vector3f rotation, Vector3f scale) {
		super(mesh, position, rotation, scale);
	}
	
	public void update()
	{
		
	}

	public void updateLife() {
		curTime = System.currentTimeMillis();
		float timeStep = 1000 / ageSpeed;
		if (curTime - lastTime >= timeStep) {
			// if(lastTime + timeStep < curTime){
			lastTime = curTime;
			if(UPDATE_STATE == 0){
				secondsOld += 1;
			}else if(UPDATE_STATE == 1){
				secondsOld += SECONDS_IN_DAY;
			}else if(UPDATE_STATE == 2){
				secondsOld += SECONDS_IN_DAY * 7;
			}
			
			while(secondsOld >= SECONDS_IN_DAY){
				secondsOld -= SECONDS_IN_DAY;
				daysOld += 1;
//				if((secondsOld < SECONDS_IN_DAY))
//				System.out.println("[" + timeStep + "] AGE [Seconds: " + secondsOld + "]" + " [Days: " + daysOld + "]");
			}
			
			while(daysOld >= DAYS_IN_YEAR){
				daysOld -= DAYS_IN_YEAR;
				yearsOld += 1;
				
				if(yearsOld >= TEEN_AGE){
					LIFE_STATE = 1;
					if(yearsOld >= ADULT_AGE){
						LIFE_STATE = 2;
					}
				}
				
				String life = "";
				if(LIFE_STATE == 0){
					life = "Child";
				}else if(LIFE_STATE == 1){
					life = "Teen";
				}else if(LIFE_STATE == 2){
					life = "Adult";
				}
				System.out.println("[" + life + "]" + "Happy Birthday: " + yearsOld);
			}

		}
	}

	@Override
	public void interact() {
	}

}
