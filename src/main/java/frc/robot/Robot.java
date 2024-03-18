// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAlternateEncoder;
import com.revrobotics.CANSparkLowLevel.MotorType;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  private XboxController m_leftStick;
  private XboxController m_guitarra;
  public double leftaxis = m_leftStick.getLeftTriggerAxis();
  public double rightaxix = m_leftStick.getRightTriggerAxis();
  public CANSparkMax m_leftMotor;
  public CANSparkMax m_rightMotor;
  public CANSparkMax m_leftMotorfollow;
  public CANSparkMax m_rightMotorfollow;
  public CANSparkMax m_shooteresquerdo;
  public CANSparkMax m_shooterdireito;
  public CANSparkMax m_intakegirar;
  public CANSparkMax m_intakepegajoga;
  public CANSparkMax m_elevadordireito;
  public CANSparkMax m_elevadoresquerdo;

  public void modo_reto() {
    if ((leftaxis > 0) && (rightaxix == 0) && (m_leftStick.getLeftY() < 0.100) && (m_leftStick.getLeftX() > -0.100))  {
      m_leftMotor.set(leftaxis);
      m_rightMotor.set(-leftaxis);
  } else if ((rightaxix) > 0 && (leftaxis == 0)) {
      m_leftMotor.set(-rightaxix);
      m_rightMotor.set(rightaxix);
    }
  }
  public void elevadores() {
    if (m_guitarra.getRawButtonPressed(3) && m_guitarra.getRawButtonPressed(8) ) {
      m_elevadoresquerdo.set(-1);
  } else if (m_guitarra.getRawButtonPressed(2) && m_guitarra.getRawButtonPressed(8) ) {
      m_elevadordireito.set(-1); 
    } else if (m_guitarra.getRawButtonPressed(1) && m_guitarra.getRawButtonPressed(8) ) {
      m_elevadoresquerdo.set(1);
    } else if (m_guitarra.getRawButtonPressed(0) && m_guitarra.getRawButtonPressed(8)) {
      m_elevadordireito.set(1); 
    } }

  public void intake() {
    if (m_guitarra.getAButton() && (m_guitarra.getRawButtonReleased(8))) {
      m_intakepegajoga.set(0.5);
    } else if (m_guitarra.getBButton() && (m_guitarra.getRawButtonReleased(8))) {
      m_intakepegajoga.set(-0.5);
  } else if (m_guitarra.getYButton() && (m_guitarra.getRawButtonReleased(8))) {
      m_intakegirar.set(0.3); 
    } else if (m_guitarra.getXButton() && (m_guitarra.getRawButtonReleased(8))) {
      m_intakegirar.set(-0.3);
    } }

    public void shooter() {
      if (m_guitarra.getLeftBumperPressed() && (m_guitarra.getRawButtonReleased(8))) {
      m_shooteresquerdo.set(-0.5);
      m_shooterdireito.set(0.5);
    } }

    public void tankDrive() {
      m_myRobot.tankDrive(m_leftStick.getLeftY() * 0.7, m_leftStick.getRightY() * 0.7);
      m_leftMotor.set(m_leftMotor.get());
      m_rightMotor.set(m_rightMotor.get());
    }
    
  private DifferentialDrive m_myRobot;
  private static final SparkMaxAlternateEncoder.Type kAltEncType = SparkMaxAlternateEncoder.Type.kQuadrature;
  private static final int kCPR = 8192;
  private  RelativeEncoder m_alternateEncoder;
  public double kP, kD, kIz, kMaxOutput, kMinOutput;

  public void variáveis() {
    m_leftMotor = new CANSparkMax(1, MotorType.kBrushless);
    m_rightMotor = new CANSparkMax(3, MotorType.kBrushless);
    m_leftMotorfollow = new CANSparkMax(2, MotorType.kBrushless);
    m_rightMotorfollow = new CANSparkMax(4, MotorType.kBrushless);
    m_shooteresquerdo = new CANSparkMax(5,MotorType.kBrushed); //Talvez kBrusheless
    m_shooterdireito = new CANSparkMax(6,MotorType.kBrushed); //Talvez kBrusheless
    m_intakegirar = new CANSparkMax(7,MotorType.kBrushless);
    m_intakepegajoga = new CANSparkMax(8,MotorType.kBrushless);
    m_elevadordireito = new CANSparkMax(9,MotorType.kBrushless);
    m_elevadoresquerdo = new CANSparkMax(10,MotorType.kBrushless);
    m_rightMotorfollow.follow(m_rightMotor);
    m_leftMotorfollow.follow(m_leftMotor);
    m_myRobot = new DifferentialDrive(m_leftMotor, m_rightMotor);
    m_leftStick = new XboxController(0);
    m_rightMotorfollow.follow(m_rightMotor);
    m_leftMotorfollow.follow(m_leftMotor);
    m_leftMotor.restoreFactoryDefaults();
    m_rightMotor.restoreFactoryDefaults();
    m_alternateEncoder = m_rightMotor.getAlternateEncoder(kAltEncType, kCPR);
  }

  @Override
  public void robotInit() {
    variáveis(); //Revisar

    m_rightMotor.getInverted();

    if(m_leftMotor.setIdleMode(CANSparkMax.IdleMode.kCoast) != REVLibError.kOk){
      SmartDashboard.putString("Idle Mode", "Error");
    }

    if(m_leftMotor.getIdleMode() == CANSparkMax.IdleMode.kCoast) {
      SmartDashboard.putString("Idle Mode", "Coast");
    } else {
      SmartDashboard.putString("Idle Mode", "Brake");
    }

    if(m_leftMotor.setOpenLoopRampRate(0) != REVLibError.kOk) {
      SmartDashboard.putString("Ramp Rate", "Error");
    }

    SmartDashboard.putNumber("Ramp Rate", m_leftMotor.getOpenLoopRampRate());


  }
  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {
    tankDrive();
    modo_reto();
    elevadores();
    intake();
    shooter();
    
    SmartDashboard.putNumber("Encoder Position", m_alternateEncoder.getPosition());

    SmartDashboard.putNumber("Encoder Velocity", m_alternateEncoder.getVelocity());
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {}
}
