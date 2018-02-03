package org.usfirst.frc.team1502.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;

public class Robot extends IterativeRobot {
	
	// Add in drive
	TestRun drive;
	
	// Create Joysticks
	Joystick leftStick;
	Joystick rightStick;
	Joystick manipStick;
	
	// Motor Controllers
	// Add SPI Gyro ADXRS450
	public ADXRS450_Gyro spiGyro;	
	
    public void robotInit() {
    	// Initialize Joysticks
    	leftStick = new Joystick(0);
		rightStick = new Joystick(1);
		manipStick = new Joystick(2);
		
		// Initialize and Calibrate SPI Gyro
		spiGyro = new ADXRS450_Gyro();
		spiGyro.calibrate();
		CameraServer.getInstance().startAutomaticCapture();
		// Load in Drive.java with controls
		drive = new TestRun(leftStick, rightStick, manipStick, 1, 2, 3, spiGyro);

		
    }

    public void autonomousInit() {
//    	spiGyro.reset();
    	drive.autonomousInit();
    	
    }
    
    public void autonomousPeriodic() {
    	
    	// see Drive.java for a list of autonomous methods
    	drive.autonomousPeriodic();
    	
    }
    
    public void disabledInit(){
    
    }
    
    public void disabledPeriodic(){
    	
   
    }
    
    public void teleopInit(){
    	
//    	spiGyro.reset();
    	
    }
    
    public void teleopPeriodic() {
    	
    	// this is in Drive.java (no crap)
		drive.teleopPeriodic();	
		//System.out.println("gyro angle: " + spiGyro.getAngle() + " gyro rate: " + spiGyro.getRate());
	} 
} 