// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAlternateEncoder;
import com.revrobotics.SparkPIDController;
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
  //controles
  private XboxController controle;
  private Joystick guitarra;

  //declaração dos motores
  public CANSparkMax m_leftMotor;
  public CANSparkMax m_rightMotor;
  public CANSparkMax m_leftMotorfollow;
  public CANSparkMax m_rightMotorfollow;
  public CANSparkMax m_shooteresquerdo;
  public CANSparkMax m_shooterdireito;
  public CANSparkMax m_movintake;
  public CANSparkMax m_intakecoletor;
  public CANSparkMax m_elevadordireito;
  public CANSparkMax m_elevadoresquerdo;

  //declaração do pid do intake
  public SparkPIDController pidintake;
 //declaração da variável de verificação de pressionamento do botão 9
  boolean b9p;
 //declaração do encoder do NEO de rotação do intake
  public RelativeEncoder intakEncoder;
 //declaração do valor de referência enviado para o PID do encoder de movimentação do intake
  double intakereference = 0;
 //declaração da váriavel que armazena o valor do "trigger" da direita
  double rightaxix;
 //declaração da váriavel que armazena o valor do "trigger" da esquerda
  double lefttaxix;
 //declaração do valor da saída do motor em relação do "trigger" da esquerda
  double leftaxisoutput;
 //declaração do valor da saída do motor em relação do "trigger" da direita
  double rightaxisoutput;
 //declaração da variável que armazena a posição do "encoder" do intake
  double intakeencoderposition;
  
  //função feita para o funcionamento do tank drive
  private DifferentialDrive diferentialdrive;
  //funções para o funcionamento dos "encoders"
  private static final SparkMaxAlternateEncoder.Type kAltEncType = SparkMaxAlternateEncoder.Type.kQuadrature;
  private static final int kCPR = 8192;
  private  RelativeEncoder m_alternateEncoder;
  //declaração das variáveis do PID
  public double kP, kD, kIz, kMaxOutput, kMinOutput;

  @Override
  public void robotInit() {

    //declaração das portas e tipos dos motores
    m_leftMotor = new CANSparkMax(3, MotorType.kBrushless);
    m_rightMotor = new CANSparkMax(1, MotorType.kBrushless);
    m_leftMotorfollow = new CANSparkMax(4, MotorType.kBrushless);
    m_rightMotorfollow = new CANSparkMax(2, MotorType.kBrushless);
    m_shooteresquerdo = new CANSparkMax(5,MotorType.kBrushed); 
    m_shooterdireito = new CANSparkMax(6,MotorType.kBrushed); 
    m_movintake = new CANSparkMax(7,MotorType.kBrushless);
    m_intakecoletor = new CANSparkMax(8,MotorType.kBrushless);
    m_elevadordireito = new CANSparkMax(9,MotorType.kBrushed);
    m_elevadoresquerdo = new CANSparkMax(10,MotorType.kBrushed);

    //função para fazer um motor realizar as mesmas ações que o outro
    m_rightMotorfollow.follow(m_rightMotor);
    m_leftMotorfollow.follow(m_leftMotor);
    //aplica a função do differentialdrive para os motores principais
    diferentialdrive = new DifferentialDrive(m_leftMotor, m_rightMotor);
    //reinicia os valores dos motores
    m_leftMotor.restoreFactoryDefaults();
    m_rightMotor.restoreFactoryDefaults();
    //define os parâmetros do encoder
    m_alternateEncoder = m_rightMotor.getAlternateEncoder(kAltEncType, kCPR);
    //define os tipos e portas dos controles
    controle = new XboxController(0);
    guitarra = new Joystick(1);
    //inverte o valor dos motores da direita do tank drive
    m_rightMotor.getInverted();

    //coleta a posição atual do encoder do intake e define a posição atual como a posição inicial(0)
    intakEncoder = m_movintake.getEncoder();
    intakEncoder.setPosition(0);

    //configura os modos dos motores
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

    //configura os valores do PID do intake
    pidintake = m_movintake.getPIDController();
    pidintake.setP(0.016);
    pidintake.setI(0);
    pidintake.setD(0.02);
    pidintake.setIZone(0);
    pidintake.setFF(0);
    pidintake.setOutputRange(-1, 1);
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
    
    //atualiza os valores das variáveis
    b9p = guitarra.getRawButton(9);
    pidintake.setReference(intakereference, CANSparkMax.ControlType.kPosition);
    rightaxix = controle.getRightTriggerAxis();
    lefttaxix = controle.getLeftTriggerAxis();
    leftaxisoutput = lefttaxix / 10;
    rightaxisoutput = rightaxix / 10;
    intakeencoderposition = intakEncoder.getPosition();

    //verifica se o segundo gatilho da direita está pressionado, se estiver ativa o modo reto do robô
    if (controle.getRightBumper() == true) {
      diferentialdrive.tankDrive(-controle.getLeftY() * 0.2, controle.getRightY() * 0.2);
      m_leftMotor.set(m_leftMotor.get());
      m_rightMotor.set(m_rightMotor.get());
    }
    //senão o tank drive normal será ativado
    else if (controle.getLeftTriggerAxis() < 0.50 || controle.getRightTriggerAxis() < 0.50) {
  diferentialdrive.tankDrive(-controle.getLeftY() * 0.5, controle.getRightY() * 0.5);
      m_leftMotor.set(m_leftMotor.get());
      m_rightMotor.set(m_rightMotor.get());
    //define a que gatilho o modo reto estará relacionado
   }if(lefttaxix > 0.50) {m_rightMotor.set(leftaxisoutput);
    m_leftMotor.set(-leftaxisoutput);
  }if(rightaxix > 0.50) {m_rightMotor.set(-rightaxisoutput);
  m_leftMotor.set(rightaxisoutput);}

    // verifica se o botão de baixo está pressionado e se o de cima está pressionado
    if (guitarra.getRawButton(1) && b9p == true) {
     m_elevadoresquerdo.set(0.5);
      }
     if(guitarra.getRawButton(1) && b9p == false){
      if (intakEncoder.getPosition() > -10) {
        intakereference = -45;
      }else if (intakEncoder.getPosition() < -25) {
        intakereference = 0;
    }}
    else if (guitarra.getRawButton(1) == false && guitarra.getRawButton(3) == false){
      m_elevadoresquerdo.set(0);
    }



    if (guitarra.getRawButton(2) && b9p == true) {
      m_elevadordireito.set(-0.5);
      }
     if(guitarra.getRawButton(2) && b9p == false){
     if (intakEncoder.getPosition() > -10 || intakEncoder.getPosition() < -25) {
        intakereference = -16;
    }else if (intakEncoder.getPosition() > -15 || intakEncoder.getPosition() < -13) {
        intakereference = 0;}}
    else if (guitarra.getRawButton(2) == false){
      m_elevadordireito.set(0);
    }



    if (guitarra.getRawButton(4) && b9p == true) {
      m_elevadordireito.set(0.5);
      }
     if(guitarra.getRawButton(4) && b9p == false){
     m_intakecoletor.set(0.5);
    } 
    else if (guitarra.getRawButton(4) == false && guitarra.getRawButton(3) == false){
       m_intakecoletor.set(0);
    }if (guitarra.getRawButton(4) && guitarra.getRawButton(2)){
      m_elevadordireito.set(0);
    }



    if (guitarra.getRawButton(3) && b9p == true) {
      m_elevadoresquerdo.set(-0.5);
      }
     if(guitarra.getRawButton(3) && b9p == false){
    m_intakecoletor.set(-0.8);}
    else if (guitarra.getRawButton(3) == false && guitarra.getRawButton(1) == false){
      m_elevadoresquerdo.set(0);
    }if (guitarra.getRawButton(3) == false && guitarra.getRawButton(4) == false) {
      m_intakecoletor.set(0);
    }

    if (guitarra.getRawButton(5) && b9p == true) {
     //BOTÃO DISPONÍVEL
      }
     if(guitarra.getRawButton(5) && b9p == false){
    m_shooterdireito.set(1);
    m_shooteresquerdo.set(-1);}
    else if (guitarra.getRawButton(5) == false){
      m_shooterdireito.set(0);
      m_shooteresquerdo.set(0);
    }

    //verifica se a palheta da guitarra está pressionada para cima ou para baixo, se estiver o intake irá atirar a nota
    int pov = guitarra.getPOV();
    if (pov == 180 || pov == 0) {
      m_intakecoletor.set(-1);
    }
    //aramzena a posição do intake
    double itkposition = intakEncoder.getPosition();

    //mostra o valor de uma variável na "SmartDashboard"
    SmartDashboard.putNumber("leftaxis", lefttaxix);
    SmartDashboard.putNumber("rightaxis", rightaxix);
    SmartDashboard.putNumber("itkposition", itkposition);
    SmartDashboard.putBoolean("b9p", b9p);
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
