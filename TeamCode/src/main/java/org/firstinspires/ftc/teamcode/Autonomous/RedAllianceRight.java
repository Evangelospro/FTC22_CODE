package org.firstinspires.ftc.teamcode.Autonomous;

import com.arcrobotics.ftclib.hardware.SensorRevTOFDistance;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorGroup;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Autonomous.visionv2.Detector;

import java.util.Arrays;

@Autonomous(name="Red Alliance Right", group="FTC22")
public class RedAllianceRight extends LinearOpMode {

    Motor arm;
    Motor collector;
    ServoEx capper;
    Motor frontRight;
    Motor frontLeft;
    Motor backRight;
    Motor backLeft;
    MotorGroup duckSpinners;

    private void initMotors() {
        // Motors, servos, distance sensor and IMU
        BNO055IMU IMU = hardwareMap.get(BNO055IMU.class, "imu");
        SensorRevTOFDistance cargoDetector = new SensorRevTOFDistance(hardwareMap, "cargoDetector");
        Motor duckSpinner1 = new Motor( hardwareMap, "duckSpinner1");
        Motor duckSpinner2 = new Motor( hardwareMap, "duckSpinner2");
        duckSpinners = new MotorGroup(duckSpinner1, duckSpinner2);
        arm = new Motor(hardwareMap, "arm");
        collector = new Motor(hardwareMap, "collector");
        capper = new SimpleServo(hardwareMap, "capper",0,90);
        frontRight = new Motor(hardwareMap, "frontRight");
        frontLeft = new Motor(hardwareMap, "frontLeft");
        backRight = new Motor(hardwareMap, "backRight");
        backLeft = new Motor(hardwareMap, "backLeft");
    }

    @Override
    public void runOpMode(){
        initMotors();
        Robot robot = new Robot(Arrays.asList(backLeft, frontLeft, backRight, frontRight, arm, collector, duckSpinners), this);

        Detector detector = new Detector(hardwareMap);
        Detector.ElementPosition itemPos = detector.getElementPosition();
        telemetry.addData("Detected Cargo : ", robot.cargoDetection());
        telemetry.update();
        waitForStart();
        switch (itemPos) {
            case LEFT:
                robot.drive(Robot.Direction.LEFT, 0.5, 5000);
                break;
            case RIGHT:
                robot.strafe(Robot.Direction.LEFT, 0.5, 5000);
                break;
        }
    }
}