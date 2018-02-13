package org.usfirst.frc.team1502.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

interface Lambda {
	int call();
}

public class AutoBot {
	TestRun driveSys;
	
	public enum Unit {
		kFeet,
		kDegrees,
		kPulses
	};
	
	public static final double GEAR_RATIO = 2.461538; //32 / 13
	public static final double PULSES_PER_REV = 1440 * GEAR_RATIO;
	public static final double PULSES_PER_FOOT = PULSES_PER_REV * 0.51894; //Encoder PPR / Wheel Circumference (Feet)
	public static final double PULSES_PER_DEGREE = PULSES_PER_REV / 360; //Encoder PPR / Degrees in a Circle
	
	public AutoBot(TestRun driveSys) {
		this.driveSys = driveSys;
	}
	
	public static double convertToTicks(double value, Unit unit) {
		switch (unit) {
			case kFeet:
				return value * PULSES_PER_FOOT;
			case kDegrees:
				return value * PULSES_PER_DEGREE;
			case kPulses:
				return value;
			default:
				throw new ArithmeticException("Can't convert units");
		}
	}
	
	/**
	 * This function's return value only makes sense when used in a ratio with another result of this function on the bottom. Otherwise do not use. Good for calculating drive wheel to omni wheel speed ratio.
	 */
	public static double calculateUnionTurnSpeed (double degrees, double radius, double circumference, double angle) {
		return ((2 * 3.14 * radius) * degrees) / (360 * circumference * Math.sin(Math.toRadians(angle)));
	}
	
	public void go(double distance, Unit unit) {
		driveSys.leftWheel.setNeutralMode(NeutralMode.Brake);
		driveSys.rightWheel.setNeutralMode(NeutralMode.Brake);
		
		PIDController gyroLock;
		distance = (int) convertToTicks(distance, unit);
		double speed;
		int dir;
		
		driveSys.spiGyro.reset();

		if (distance > 0) {
			speed = 0.4;
			gyroLock = new PIDController(1.325, 9.49e-4, 320);
			dir = 1;
		} else if (distance < 0) {
			speed = -0.4;
			gyroLock = new PIDController(0.575, 2.83e-4, 167.5);
			dir = -1;
		} else {
			return;
		}
		
		driveSys.leftWheel.set(ControlMode.PercentOutput, -speed);
		driveSys.rightWheel.set(ControlMode.PercentOutput, speed);
		Lambda getPos = () -> (driveSys.leftWheel.getSensorCollection().getQuadraturePosition());
		int initialPosition = getPos.call();
		int destination = initialPosition + (int) distance;
		int slowdownPoint = (int) convertToTicks(1, Unit.kFeet);
		Lambda getDistLeft = () -> (destination - getPos.call());
		
		while (dir * getDistLeft.call() > 0 || !gyroLock.isStable(2) /*|| driveSys.leftWheel.getSensorCollection().getQuadratureVelocity() * 10 / PULSES_PER_DEGREE < 10*/) {
			if (driveSys.isTeleop) break;
			gyroLock.input(driveSys.spiGyro.getAngle());
			if ((gyroLock.latest().err > 0 && gyroLock.prev().err < 0) || (gyroLock.latest().err < 0 && gyroLock.prev().err > 0)) {
				gyroLock.reset();
				driveSys.spiGyro.reset();
			}
			driveSys.omniWheels.set(ControlMode.PercentOutput, gyroLock.getCorrection() * TestRun.OVERALL_PID_GAIN);
			SmartDashboard.putNumber("dist left", getDistLeft.call());
			SmartDashboard.putNumber("degs per second", driveSys.leftWheel.getSensorCollection().getQuadratureVelocity() * 10 / PULSES_PER_DEGREE);
			SmartDashboard.putNumber("current position", getPos.call());
			if (Math.abs(getDistLeft.call()) < slowdownPoint) {
				double power = (double) getDistLeft.call() / (double) slowdownPoint * Math.abs(speed);
				driveSys.leftWheel.set(ControlMode.PercentOutput, -power);
				driveSys.rightWheel.set(ControlMode.PercentOutput, power);
				SmartDashboard.putNumber("power", power);
				SmartDashboard.putNumber("power outputting", Math.random());
			}
			
			Thread.yield();
		}
		driveSys.leftWheel.set(ControlMode.PercentOutput, 0);
		driveSys.rightWheel.set(ControlMode.PercentOutput, 0);
		driveSys.omniWheels.set(ControlMode.PercentOutput, 0);
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			return;
		}
		driveSys.leftWheel.setNeutralMode(NeutralMode.Coast);
		driveSys.rightWheel.setNeutralMode(NeutralMode.Coast);
	}
	
	public void leftPivotTurn(double degrees) {
		double pulses = degrees * driveSys.PULSES_PER_90DEG_YAW / 90;
		int startPos = driveSys.rightWheel.getSensorCollection().getQuadraturePosition();
		SmartDashboard.putNumber("Before", startPos);
		driveSys.leftWheel.set(ControlMode.PercentOutput, 0);
		driveSys.rightWheel.set(ControlMode.PercentOutput, 0.3);
		driveSys.omniWheels.set(ControlMode.PercentOutput, 0.1);
		while (Math.abs(driveSys.rightWheel.getSensorCollection().getQuadraturePosition() - startPos) < pulses) {
			Thread.yield();
		}
		SmartDashboard.putNumber("After" , driveSys.rightWheel.getSensorCollection().getQuadraturePosition());
		
		driveSys.leftWheel.setNeutralMode(NeutralMode.Brake);
		driveSys.rightWheel.setNeutralMode(NeutralMode.Brake);
		
		driveSys.leftWheel.set(ControlMode.PercentOutput, 0);
		driveSys.rightWheel.set(ControlMode.PercentOutput, 0);
		driveSys.omniWheels.set(ControlMode.PercentOutput, 0);
	}
	
	public void turn(double degrees) {
		driveSys.leftWheel.setNeutralMode(NeutralMode.Brake);
		driveSys.rightWheel.setNeutralMode(NeutralMode.Brake);
		
		double speed;
		double dir;

		if (degrees > 0) {
			speed = -.4;
			dir = 1;
		} else if (degrees < 0) {
			speed = .4;
			dir = -1;
		} else {
			return;
		}
		driveSys.spiGyro.reset();
		Lambda getPos = () -> (int) (driveSys.spiGyro.getAngle());
		int initialPosition = getPos.call();
		int destination = initialPosition + (int) degrees;
		Lambda getDistLeft = () -> (destination - getPos.call());
		
		driveSys.leftWheel.set(ControlMode.PercentOutput, speed * driveSys.DRIVE_OMNI_RATIO);
		driveSys.rightWheel.set(ControlMode.PercentOutput, speed * driveSys.DRIVE_OMNI_RATIO);
		driveSys.omniWheels.set(ControlMode.PercentOutput, speed);
		
		while (dir * getDistLeft.call() > 0) {
			System.out.println("Normal: " + driveSys.spiGyro.getAngle());
			if (driveSys.isTeleop) break;
			
			SmartDashboard.putNumber("dist left", getDistLeft.call());
			SmartDashboard.putNumber("current position", getPos.call());
			
			Thread.yield();
		}
		
		driveSys.leftWheel.set(ControlMode.PercentOutput, 0);
		driveSys.rightWheel.set(ControlMode.PercentOutput, 0);
		driveSys.omniWheels.set(ControlMode.PercentOutput, 0);
		
		PIDController gyroLock = new PIDController(1.325, 9.49e-4, 320);
		
		while (driveSys.spiGyro.getAngle() - degrees > 0 || Math.abs(driveSys.spiGyro.getRate()) < 0.1) {
			System.out.println("PID: " + driveSys.spiGyro.getAngle());
			if (driveSys.isTeleop) break;
			gyroLock.input(driveSys.spiGyro.getAngle() - degrees);
			if ((gyroLock.latest().err > 0 && gyroLock.prev().err < 0) || (gyroLock.latest().err < 0 && gyroLock.prev().err > 0)) {
				gyroLock.reset();
				driveSys.spiGyro.reset();
			}
			driveSys.omniWheels.set(ControlMode.PercentOutput, gyroLock.getCorrection() * TestRun.OVERALL_PID_GAIN);
			Thread.yield();
		}
		driveSys.omniWheels.set(ControlMode.PercentOutput, 0);
		
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			return;
		}
		driveSys.leftWheel.setNeutralMode(NeutralMode.Coast);
		driveSys.rightWheel.setNeutralMode(NeutralMode.Coast);
	}
}
