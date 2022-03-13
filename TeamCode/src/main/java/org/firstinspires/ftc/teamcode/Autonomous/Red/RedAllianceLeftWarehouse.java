package org.firstinspires.ftc.teamcode.Autonomous.Red;

import com.arcrobotics.ftclib.hardware.RevIMU;
import com.arcrobotics.ftclib.hardware.SensorRevTOFDistance;
import com.arcrobotics.ftclib.hardware.motors.CRServo;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorGroup;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Autonomous.Robot;
import org.firstinspires.ftc.teamcode.Autonomous.visionv1.TseDetector;
import org.firstinspires.ftc.teamcode.events.EventDispatcher;
import org.firstinspires.ftc.teamcode.events.Listener;

import java.util.Arrays;

@Autonomous(name="Red Alliance Left Warehouse", group="FTC22Auto_Ware_Red")
public class RedAllianceLeftWarehouse extends LinearOpMode {

    Motor arm;
    Motor collector;
    CRServo capper;
    Motor frontRight;
    Motor frontLeft;
    Motor backRight;
    Motor backLeft;
    MotorGroup duckSpinners;
    RevIMU imu;
    SensorRevTOFDistance cargoDetector;

    private void initHardware() {
        // Motors, servos, distance sensor and IMU
        imu = new RevIMU(hardwareMap);
        cargoDetector = new SensorRevTOFDistance(hardwareMap, "cargoDetector");
        Motor duckSpinner1 = new Motor( hardwareMap, "duckSpinner1");
        Motor duckSpinner2 = new Motor( hardwareMap, "duckSpinner2");
        duckSpinners = new MotorGroup(duckSpinner1, duckSpinner2);
        arm = new Motor(hardwareMap, "arm");
        collector = new Motor(hardwareMap, "collector");
        capper= new CRServo(hardwareMap, "capper");
        frontRight = new Motor(hardwareMap, "frontRight");
        frontLeft = new Motor(hardwareMap, "frontLeft");
        backRight = new Motor(hardwareMap, "backRight");
        backLeft = new Motor(hardwareMap, "backLeft");
    }

    @Override
    public void runOpMode(){
        initHardware();
        Robot robot = new Robot(Arrays.asList(backLeft, frontLeft, backRight, frontRight, arm, collector, duckSpinners, imu, cargoDetector), this);

        waitForStart();
        TseDetector.Location itemPos = robot.getTsePos();
        telemetry.addData("Detected Cargo: ", itemPos);
        telemetry.update();
        robot.drive(Robot.Direction.FORWARDS,0.8,20);
        if(itemPos.equals(TseDetector.Location.LEFT)) {
            robot.turn(0.8, -40);
            robot.drive(Robot.Direction.FORWARDS,0.6,39);
            robot.moveArm(Robot.Position.LOW.label,0.08);
        }
        else if(itemPos.equals(TseDetector.Location.RIGHT)) {
            robot.turn(0.8, -75);
            robot.drive(Robot.Direction.FORWARDS,0.4,60);
            robot.turn(0.8, 0);
            robot.moveArm(Robot.Position.HIGH.label,0.08);
        }
        // CENTER
        else {
            robot.turn(0.8, -65);
            robot.drive(Robot.Direction.FORWARDS,0.6,50);
            robot.turn(0.8, -25);
            robot.drive(Robot.Direction.FORWARDS,0.6,5);
            robot.moveArm(Robot.Position.MID.label,0.08);
        }
        robot.intake(Robot.Direction.OUT,0.6);
        robot.turn(0.8,0);
        robot.drive(Robot.Direction.BACKWARDS,0.8,28);
        robot.moveArm(Robot.Position.DOWN.label,0.1);
        robot.turn(0.8,-90);
        if(itemPos.equals(TseDetector.Location.RIGHT)) {
            robot.drive(Robot.Direction.BACKWARDS,0.8,140);
        }
        else {
            robot.drive(Robot.Direction.BACKWARDS,0.8,120);
        }
        robot.drive(Robot.Direction.BACKWARDS,0.8,100);
        robot.turn(0.3,-138);
        robot.duckSpin(0.45,3500);
        robot.drive(Robot.Direction.BACKWARDS,0.8,2);
        robot.turn(0.8,-100);
        robot.moveArm(Robot.Position.HIGH.label, 0.1);
        robot.turn(0.8,-90);
        robot.drive(Robot.Direction.FORWARDS,1,255);
    }
}
