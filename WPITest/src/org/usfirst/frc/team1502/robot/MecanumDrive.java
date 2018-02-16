package org.usfirst.frc.team1502.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;

public class MecanumDrive { // 0.03384 = P_TERM when using getRate()
	static double P_TERM = 0.08; // lower if robot jiggles, raise if the correction is not enough
	static double P_TERM_VDRIVE = 0.08;
	
	public static double expRate(double x, double exp) {
		if (x >= 0) {
			return Math.pow(x, exp);
		} else {
			return -Math.pow(-x, exp);
		}
	}
//	public static void resetGyro(TestRun driveSys) {
//		driveSys.spiGyro.reset();
//	}
	public static void drive(TestRun driveSys, double speed, double strafe, double turn) {
//		double wheelFL = 0; //remember to reverse LEFT wheels
//		double wheelFR = 0;
//		double wheelBL = 0;
//		double wheelBR = 0;
//		wheelFL += speed;
//		wheelFR += speed;
//		wheelBL += speed;
//		wheelBR += speed;
//		
//		wheelFL += turn;
//		wheelBL += turn;
//		wheelFR -= turn;
//		wheelBR -= turn;
//		
//		wheelFL += strafe;
//		wheelFR -= strafe;
//		wheelBL -= strafe;
//		wheelBR += strafe;
//		
//		double mag = Math.sqrt(speed*speed + strafe*strafe + turn*turn);
//		if (mag < 1) {
//			mag = 1;
//		}
//		wheelFL /= mag;
//		wheelFR /= mag;
//		wheelBL /= mag;
//		wheelBR /= mag;
//		
//		driveSys.leftWheel.set(-wheelFL);
//		driveSys.rightWheel.set(wheelFR);
//		driveSys.omniWheels.set(-wheelBL);
//		driveSys.leftBackWheel.set(-wheelBL);
//		driveSys.rightBackWheel.set(wheelBR);
	}
	
	public static void vDrive(TestRun driveSys, double left, double right) {
		//  ROBOT DIAGRAM
		//   |		  |
		//   |		  |
		//   |\		 /|
		//    b\ 	/b
		//     x\  /x
		//		 \/
		//		 /\
		//	   y/  \y
		//	   /	\
		// ___/a    a\__
		//
		// a: angle between omni wheels and axis of rotation
		// b: angle between front wheels and axis of rotation
		// x: distance between front wheels and axis of rotation
		// y: distance between omni wheels and axis of rotation
		//
		// stop insulting freshmen because two of them programmed this shit
		
		final double DEGS_TO_RADS = Math.PI / 180;
		
		final double A = 54;
		final double B = 51;
		final double X = 13;
		final double Y = 16;
		
		double attemptedL = left;
		double attemptedR = right;
		double attemptedO = (attemptedL - attemptedR) / 2;
		
		double o = attemptedO / Math.sin(DEGS_TO_RADS * A);
		
		double frontAdjust = Math.sin(DEGS_TO_RADS * B);
		
		o *= frontAdjust;
		
		double l = attemptedL;
		double r = attemptedR;
		
		assert Y > X;
		
		o *= Y / X;
		
		double mag = Math.sqrt(o * o + l * l + r * r);
		if (mag < 1) {
			mag = 1;
		}
		o /= mag;
		l /= mag;
		r /= mag;
		
//		driveSys.leftWheel.set(ControlMode.PercentOutput, l);
//		driveSys.rightWheel.set(ControlMode.PercentOutput, r);
//		driveSys.omniWheels.set(ControlMode.PercentOutput, o);
	}
	public static double getGyroTurn(TestRun driveSys, double speed) {
		//P_TERM = driveSys.rightStick.getThrottle() * 0.05;
		// ^^ uncomment the above to tune P_TERM with right throttle
//		double diff = driveSys.spiGyro.getAngle() * (0.5 + speed * 0.5);
//		return -diff * P_TERM_VDRIVE;
		// 4deg off course:
		// 0.02 extra turn power
		return .2;
	}
}
