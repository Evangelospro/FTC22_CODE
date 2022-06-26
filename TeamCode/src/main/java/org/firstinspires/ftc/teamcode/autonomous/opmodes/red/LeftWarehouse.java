package org.firstinspires.ftc.teamcode.autonomous.opmodes.red;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Configurable;

@Autonomous(name = "Red Left Warehouse", group="FTC22Auto_Ware")
public class LeftWarehouse extends Left {

    @Override
    public void runOpMode() {
        super.runOpMode();
        arm.runToPositionAsync(Configurable.armHighPosition, 1);
        arm.setHoldPosition(true);
        driveTrain.turn(-105, 0.1, 1);
        driveTrain.driveCM(150, 0.4);
        driveTrain.turn(-90, 0.1, 1);
        driveTrain.driveCM(150, 0.4);
    }
}
