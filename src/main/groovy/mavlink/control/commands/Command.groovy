package mavlink.control.commands

import mavlink.FormationControl
import mavlink.control.conditions.Condition
import mavlink.util.ResettableCountDownLatch




abstract class Command {

    ResettableCountDownLatch latch
    Condition condition

    abstract void execute(FormationControl control)
}