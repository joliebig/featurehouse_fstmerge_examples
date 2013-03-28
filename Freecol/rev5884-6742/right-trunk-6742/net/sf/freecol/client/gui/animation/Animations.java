package net.sf.freecol.client.gui.animation;

import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.CombatModel.CombatResultType;



public class Animations {

    
    public static void unitMove(Canvas canvas, Unit unit, Tile source,
                                Tile destination) {
        new UnitMoveAnimation(canvas, unit, source, destination).animate();
    }
    
    
    public static void unitAttack(Canvas canvas, Unit attacker, Unit defender,
                                  CombatResultType result) {
        new UnitAttackAnimation(canvas, attacker, defender, result).animate();
    }


    
    public static int getAnimationSpeed(Canvas canvas, Unit unit) {
        FreeColClient client = canvas.getClient();
        String key = (client.getMyPlayer() == unit.getOwner())
            ? ClientOptions.MOVE_ANIMATION_SPEED
            : ClientOptions.ENEMY_MOVE_ANIMATION_SPEED;
        return client.getClientOptions().getInteger(key);
    }
}
