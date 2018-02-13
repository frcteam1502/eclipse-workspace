package org.usfirst.frc.team1502.robot;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.DigitalInput;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

//import org.usfirst.frc.team1502.robot.AutoBot.Unit;

public class TestRun {
    /*public TalonSRX rightWheel1;
    public TalonSRX rightWheel2;
    public TalonSRX leftWheel1;
    public TalonSRX leftWheel2;*/
	public TalonSRX leftWheel;
	public TalonSRX rightWheel;
	
    public VictorSPX omniWheels;
    public TalonSRX arm;
    public Joystick leftStick;
    public Joystick rightStick;
    public Joystick manipStick;
    public DigitalInput limitSwitch;
    public boolean gyroOnLastCycle = false;
    public int tickCount = 0; // 50 ticks per second
    public ADXRS450_Gyro spiGyro;
    public Accelerometer accel;
    public AnalogInput stringPot; //String Potentiometer
    
    public PIDController gyroPID;
    public boolean isKeplerStopped = false;
    public boolean isTeleop = false;
    
    static final double DEADZONE = 0.02;
    public static final double OVERALL_PID_GAIN = 0.04;
    public static final double WHEEL_TICKS_PER_DEGREE = 2588 / 360;
    
    public static final double DRIVE_DIST_TO_AXIS = 10; //Distance to Axis of Rotation, Inches
    public static final double OMNI_DIST_TO_AXIS = 23; //Distance to Axis of Rotation, Inches
    public static final double DRIVE_ANGLE_FORCE = 90; //Angle between Wheel and "Line" to AoR
    public static final double OMNI_ANGLE_FORCE = 115; //Angle between Wheel and "Line" to AoR
    public static final double DRIVE_CIRCUMFERENCE = 23.56; //Wheel Circumference, Inches
    public static final double OMNI_CIRCUMFERENCE = 25.13; //Wheel Circumference, Inches
    public static final double DRIVE_OMNI_RATIO = 0.4202;
    public static final double PULSES_PER_90DEG_YAW = 4706;
    
    static double EXP_RATE = 3;
    
    boolean gyroWasOnLastCycle = false;
    boolean gyroIsOn = false;
    
    public Rumbler r;
    
    public TestRun(Joystick left, Joystick right, Joystick manip, int w1, int w2, int w3, ADXRS450_Gyro spiGyro) {
        
    	Accelerometer accel;
    	this.accel = new BuiltInAccelerometer(); 
    	accel = new BuiltInAccelerometer(Accelerometer.Range.k8G);
    	
    	this.leftStick = left;
        this.rightStick = right;
        this.manipStick = manip;
        
        /*this.rightWheel1 = new TalonSRX(1);
        this.rightWheel2 = new TalonSRX(2);
        this.leftWheel1 = new TalonSRX(3);
        this.leftWheel2 = new TalonSRX(4);*/
        this.leftWheel = new TalonSRX(1);
        this.rightWheel = new TalonSRX(3);
        this.omniWheels = new VictorSPX(2);
        this.arm = new TalonSRX(6);
        
        this.gyroPID = new PIDController(1.325, 9.49e-4, 320);
        this.spiGyro = spiGyro;
        
        this.stringPot = new AnalogInput(0);
    }
    
    // Makes sure there's no unintended joystick drift
    public void autonomousPeriodic() {
    	
    }
    
    public void autonomousInit() {
    	/*String gameData;
    	gameData = DriverStation.getInstance().getGameSpecificMessage();
    	
    	if (gameData.valueOf(0) == "L") {
    		//Left
    	} else {
    		//Right
    	}*/
    	isTeleop = false;
    	leftWheel.configOpenloopRamp(.14, 10);
    	//leftWheel.configOpenloopRamp(.14, 10);
    	rightWheel.configOpenloopRamp(.14, 10);
    	//rightWheel2.configOpenloopRamp(.14, 10);
    	omniWheels.configOpenloopRamp(.14, 10);
    	arm.configOpenloopRamp(.14, 10);
    	AutoBot bot = new AutoBot(this);
    	Timer.startNewThread(() -> {
    		bot.leftPivotTurn(90);
    	});
    }
    
    public void teleopInit() {
    	isTeleop = true;
    	leftWheel.configOpenloopRamp(0.14, 15);
    	rightWheel.configOpenloopRamp(0.14, 15);
    	r = new Rumbler(manipStick);
    	r.rumbleBothFor(1, 300);
    	
    	spiGyro.reset();
    }
    
    public void teleopPeriodic() {
        double speed = MecanumDrive.expRate(deadZone(rightStick.getY()), EXP_RATE);
        double turn = MecanumDrive.expRate(deadZone(leftStick.getX()), EXP_RATE);
        
        if (leftStick.getRawButton(8)) {
        	spiGyro.calibrate();
        }
       
//        if (leftStick.getRawButton(11)) {
//        	double pwr = rightStick.getThrottle();
//        	pwr = (1 - pwr) / 2;
//        	System.out.println(pwr);
//           	leftWheel.set(ControlMode.PercentOutput, pwr * .4202);
//           	rightWheel.set(ControlMode.PercentOutput, pwr * .4202);
//           	omniWheels.set(ControlMode.PercentOutput, pwr * 1);
//           	return;
//        } if (rightStick.getRawButton(11)) {
//        	double pwr = rightStick.getThrottle();
//        	pwr = (1 - pwr) / 2;
//        	System.out.println(pwr);
//           	leftWheel.set(ControlMode.PercentOutput, pwr * -.4202);
//           	rightWheel.set(ControlMode.PercentOutput, pwr * -.4202);
//           	omniWheels.set(ControlMode.PercentOutput, pwr * -1);
//           	return;
//        }
        
        //System.out.println("Gyro angle: " + spiGyro.getAngle());
        	
        gyroWasOnLastCycle = gyroIsOn;
        gyroIsOn = rightStick.getRawButton(1);
        
        if (gyroIsOn && !gyroWasOnLastCycle) {
        	gyroPID.reset();
        	spiGyro.reset();
        }
        
        if (gyroIsOn) {
        	if (speed <= 0) { //Forwards or Stationary
	        	gyroPID.P = 0.575;
	        	gyroPID.I = 2.83e-4;
	        	gyroPID.D = 167.5;
	        } else { //Backwards
	        	gyroPID.P = 1.325;
	        	gyroPID.I = 9.49e-4;
	        	gyroPID.D = 320;
	        }
        	
        	gyroPID.input(spiGyro.getAngle());
            turn = -gyroPID.getCorrection() * OVERALL_PID_GAIN;
            
            System.out.println("stability: " + (Math.abs(turn) < 0.05));
            System.out.println("isStable:" + gyroPID.isStable(2));
        }
        
        //leftWheel.set(ControlMode.PercentOutput, speed);
        //rightWheel.set(ControlMode.PercentOutput, -speed);
        //omniWheels.set(ControlMode.PercentOutput, -turn);
        
        /*leftWheel1.set(ControlMode.PercentOutput, -rightStick.getY());
        leftWheel2.set(ControlMode.PercentOutput, -rightStick.getY());
        rightWheel1.set(ControlMode.PercentOutput, rightStick.getY());
        rightWheel2.set(ControlMode.PercentOutput, rightStick.getY());*/
        leftWheel.set(ControlMode.PercentOutput, rightStick.getY());
        rightWheel.set(ControlMode.PercentOutput, -rightStick.getY());
        omniWheels.set(ControlMode.PercentOutput, -leftStick.getX());
        
        System.out.println(rightWheel.getSensorCollection().getQuadraturePosition());
        
        if (manipStick.getRawButton(5)) { //retract manip screw "LB"
        	
        } else if (manipStick.getRawButton(6)) { //extend manip screw "RB"
        	
        } else {
        	
        }
        
        if (manipStick.getRawButton(1)) { //grab "A"
        	
        } else if (manipStick.getRawButton(2)) { //release "B"
        	
    	}
        
        if (manipStick.getRawAxis(3) >= .5) { //Up "RT"
        	arm.set(ControlMode.PercentOutput, -.5);
        } else if (manipStick.getRawAxis(2) >= .5) { //Down "LT"
        	arm.set(ControlMode.PercentOutput, .5);
        } else {
        	arm.set(ControlMode.PercentOutput, 0);
        }
//        
//        SmartDashboard.putNumber("X Value", accel.getX());
//        SmartDashboard.putNumber("Y Value", accel.getY());
//        SmartDashboard.putNumber("Z Value", accel.getZ());
        
        //System.out.println(stringPot.getValue());
    }
    
    public double deadZone(double i) {
        if (Math.abs(i) < DEADZONE) {
            i = 0.0;
        }
        return i;
    }
}
