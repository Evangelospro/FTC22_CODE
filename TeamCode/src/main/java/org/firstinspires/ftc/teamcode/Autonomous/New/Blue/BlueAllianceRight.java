package org.firstinspires.ftc.teamcode.Autonomous.New.Blue;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.z3db0y.susanalib.Logger;
import com.z3db0y.susanalib.MecanumDriveTrain;
import com.z3db0y.susanalib.Motor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Autonomous.visionv1.TseDetector;
import org.firstinspires.ftc.teamcode.Configurable;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

public class BlueAllianceRight extends LinearOpMode {
    TseDetector detector;
    Motor frontLeft;
    Motor frontRight;
    Motor backLeft;
    Motor backRight;
    Motor arm;
    Motor collector;
    Motor duckSpinner;
    DistanceSensor cargoDetector;
    TouchSensor armTouchSensor;
    BNO055IMU imu;

    MecanumDriveTrain driveTrain;

    public void initHardware() {
        frontLeft = new Motor(hardwareMap, "frontLeft");
        frontRight = new Motor(hardwareMap, "frontRight");
        backLeft = new Motor(hardwareMap, "backLeft");
        backRight = new Motor(hardwareMap, "backRight");
        arm = new Motor(hardwareMap, "arm");
        collector = new Motor(hardwareMap, "collector");
        duckSpinner = new Motor(hardwareMap, "duckSpinner");

        // Motor reversing
        backLeft.setDirection(Motor.Direction.REVERSE);
        frontRight.setDirection(Motor.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        arm.setTargetPosition(0);
        arm.setRunMode(DcMotor.RunMode.RUN_TO_POSITION);
        arm.setHoldPosition(true);

        cargoDetector = hardwareMap.get(DistanceSensor.class, "cargoDetector");
        armTouchSensor = hardwareMap.get(TouchSensor.class, "armTouch");

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu.initialize(parameters);
    }

    @Override
    public void runOpMode() {
        initHardware();

        Logger.setTelemetry(telemetry);
        driveTrain = new MecanumDriveTrain(frontLeft, frontRight, backLeft, backRight, imu);
        driveTrain.ratio = Configurable.driveGearRatio;
        driveTrain.wheelRadius = Configurable.wheelRadius;

        detector = new TseDetector();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        OpenCvWebcam webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        webcam.setPipeline(detector);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
                FtcDashboard.getInstance().startCameraStream(webcam, 0);
            }

            @Override
            public void onError(int errorCode) {

            }
        });

        waitForStart();
        TseDetector.Location itemPos = detector.getLocation(this);
        Logger.addData("Detected Cargo: " + itemPos);
        Logger.update();

        driveTrain.driveCM(15, 0.4);
        driveTrain.turn(90, 0.1, 1);
        driveTrain.driveCM(72, 0.2);
        driveTrain.turn(0, 0.1, 1);
        switch (itemPos) {
            case LEFT:
                arm.runToPositionAsync(Configurable.armHighPosition, 0.8);
                break;
            case RIGHT:
                arm.runToPositionAsync(Configurable.armLowPosition, 0.5);
                break;
            case CENTER:
                arm.runToPositionAsync(Configurable.armMidPosition, 0.5);
                break;
        }
        driveTrain.driveCM(48, 0.2);
        driveTrain.turn(0, 0.1, 1);
        double collectorPower = Configurable.disposeLowSpeed;
        while(!collector.runToPosition(Configurable.disposeTicks,collectorPower)){
            collectorPower += 0.05;
            Logger.addData("Collector Power: " + collectorPower);
            Logger.update();
        }
        driveTrain.driveCM(-27, 0.2);
        arm.setHoldPosition(false);
        new Thread(() -> {
            arm.setTargetPosition(0);
            arm.setRunMode(DcMotor.RunMode.RUN_TO_POSITION);
            while (!armTouchSensor.isPressed()) {
                arm.setPower(1);
            }
            arm.resetEncoder();
            arm.setPower(0);
        }).start();
        driveTrain.turn(-90, 0.1, 1);
        driveTrain.driveCM(65, 0.3);
        driveTrain.turn(-90, 0.1, 1);
        driveTrain.driveCM(65, 0.3);
        driveTrain.driveCM(-10, 0.3);
        driveTrain.turn(-125, 0.2, 1);

        driveTrain.driveCM(10, 0.3); // until stalled

        frontRight.setRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double duckSpinnerPower = Configurable.duckSpinnerPower;
        duckSpinner.resetStallDetection();
        duckSpinner.runToPositionAsync(Configurable.duckSpinnerTicks, -duckSpinnerPower);
        while (Math.abs(duckSpinner.getCurrentPosition()) < Math.abs(duckSpinner.getTargetPosition())) {
            frontRight.resetStallDetection();
            backRight.resetStallDetection();
            if (duckSpinner.isStalled()) {
                duckSpinnerPower += 0.04;
                Logger.addData("Duck Spinner Power: " + duckSpinnerPower);
                Logger.update();
            }
            else {
                while (!frontRight.isStalled() && !backRight.isStalled()) {
                    frontRight.setPower(-0.17);
                    backRight.setPower(-0.17);
                }
            }
            duckSpinner.setPower(-duckSpinnerPower);
            frontRight.setPower(0);
            backRight.setPower(0);
        }
        duckSpinner.setPower(0);
    }
}