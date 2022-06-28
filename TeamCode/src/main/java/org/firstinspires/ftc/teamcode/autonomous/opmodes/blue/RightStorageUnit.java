package org.firstinspires.ftc.teamcode.autonomous.opmodes.blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.z3db0y.susanalib.MecanumDriveTrain;

@Autonomous(name = "Blue Right Storage Unit", group = "FTC22Auto_Store")
public class RightStorageUnit extends Right {

    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();
        driveTrain.turn(180, 0.1, 1);
        driveTrain.driveCM(-45, 0.5);
        driveTrain.turn(180, 0.1, 1);
        driveTrain.strafeCM(MecanumDriveTrain.Side.LEFT ,10, 0.2);
    }
}