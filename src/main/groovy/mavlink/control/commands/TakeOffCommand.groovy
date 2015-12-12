package mavlink.control.commands

import mavlink.FormationControl
import mavlink.control.conditions.TakeOffCondition

class TakeOffCommand extends Command {

    @Override
    void execute(FormationControl control) {
        condition = new TakeOffCondition(height: -14,delta: 0.1)
        control.service.prepareToFlight(control.positionInFormation)
    }
}
