package org.usfirst.frc.team1502.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;

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
	
	public static final double GEAR_RATIO = 2;
	public static final double PULSES_PER_REV = 1440 * GEAR_RATIO;
	public static final double PULSES_PER_FOOT = PULSES_PER_REV / 2.09; //Encoder PPR / Wheel Circumference (Feet)
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
	
	public void go(double distance, Unit unit) {
		driveSys.leftWheel.setNeutralMode(NeutralMode.Brake);
		driveSys.rightWheel.setNeutralMode(NeutralMode.Brake);
		
		PIDController gyroLock;
		distance = convertToTicks(distance, unit);
		double speed;
		
		driveSys.spiGyro.reset();
		if (distance > 0) {
			speed = 0.4;
			gyroLock = new PIDController(1.325, 9.49e-4, 320);
		} else if (distance < 0) {
			speed = -0.4;
			gyroLock = new PIDController(0.575, 2.83e-4, 167.5);
		} else {
			return;
		}
		
		driveSys.leftWheel.set(ControlMode.PercentOutput, speed);
		driveSys.rightWheel.set(ControlMode.PercentOutput, -speed);
		Lambda getPos = () -> (driveSys.leftWheel.getSensorCollection().getQuadraturePosition() + driveSys.rightWheel.getSensorCollection().getQuadraturePosition()) / 2;
		double initialPosition = getPos.call();
		double destination = initialPosition + distance;
		double slowdownPoint = convertToTicks(0.5, Unit.kFeet);
		Lambda getDistLeft = () -> Math.abs((int) destination - getPos.call());
		
		while (getDistLeft.call() > 0 || !gyroLock.isStable(2) || driveSys.leftWheel.getSensorCollection().getQuadratureVelocity() * 10 / PULSES_PER_DEGREE < 10) {
			gyroLock.input(driveSys.spiGyro.getAngle());
			if ((gyroLock.latest().err > 0 && gyroLock.prev().err < 0) || (gyroLock.latest().err < 0 && gyroLock.prev().err > 0)) {
				gyroLock.reset();
				driveSys.spiGyro.reset();
			}
			driveSys.omniWheels.set(ControlMode.PercentOutput, gyroLock.getCorrection() * TestRun.OVERALL_PID_GAIN);
			if (getDistLeft.call() - Math.abs(distance) < slowdownPoint) {
				double power = getDistLeft.call() / slowdownPoint * speed;
				driveSys.leftWheel.set(ControlMode.PercentOutput, power);
				driveSys.rightWheel.set(ControlMode.PercentOutput, power);
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
}
