package org.usfirst.frc.team1502.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;

public class Robot extends IterativeRobot {
	public Joystick rightStick;
	public Joystick leftStick;
	
	public TalonSRX leftMotor;
	public TalonSRX rightMotor;
	public VictorSPX omniWheels;
	
	@Override
	public void robotInit() {
		leftStick = new Joystick(0);
		rightStick = new Joystick(1);
		
		leftMotor = new TalonSRX(1);
		rightMotor = new TalonSRX(3);
		omniWheels = new VictorSPX(2);
	}

	@Override
	public void autonomousInit() {
	}

	@Override
	public void autonomousPeriodic() {

	}

	@Override
	public void teleopPeriodic() {
		leftMotor.set(ControlMode.PercentOutput, rightStick.getY());
		rightMotor.set(ControlMode.PercentOutput, rightStick.getY());
		omniWheels.set(ControlMode.PercentOutput, leftStick.getX());
	}


	@Override
	public void testPeriodic() {
	}
}
