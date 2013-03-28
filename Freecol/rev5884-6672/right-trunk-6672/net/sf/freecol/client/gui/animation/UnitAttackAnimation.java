

package net.sf.freecol.client.gui.animation;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.common.io.sza.SimpleZippedAnimation;
import net.sf.freecol.common.model.CombatModel.CombatResultType;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.Role;
import net.sf.freecol.common.resources.ResourceManager;



final class UnitAttackAnimation {

    private final Canvas canvas;
    private final Unit attacker;
    private final Unit defender;
    private final CombatResultType result;

    
    public UnitAttackAnimation(Canvas canvas, Unit attacker, Unit defender,
                               CombatResultType result) {
        this.canvas = canvas;
        this.attacker = attacker;
        this.defender = defender;
        this.result = result;
    }

    
    private SimpleZippedAnimation getAnimation(Canvas canvas, Unit unit,
                                               Direction direction) {
        float scale = canvas.getGUI().getMapScale();
        String roleStr = (unit.getRole() == Role.DEFAULT) ? ""
            : "." + unit.getRole().getId();
        String startStr = unit.getType().getId() + roleStr + ".attack.";
        String specialId = startStr + direction.toString().toLowerCase()
            + ".animation";

        SimpleZippedAnimation sza;
        sza = ResourceManager.getSimpleZippedAnimation(specialId, scale);
        if (sza == null) {
            String genericDirection;
            switch (direction) {
            case SW: case W: case NW: genericDirection = "w"; break;
            default:                  genericDirection = "e"; break;
            }
            String genericId = startStr + genericDirection + ".animation";
            sza = ResourceManager.getSimpleZippedAnimation(genericId, scale);
        }
        return sza;
    }


    
    public void animate() {
        Map map = attacker.getGame().getMap();
        Direction direction = map.getDirection(attacker.getTile(),
                                               defender.getTile());
        SimpleZippedAnimation sza;

        if (Animations.getAnimationSpeed(canvas, attacker) > 0) {
            if ((sza = getAnimation(canvas, attacker, direction)) != null) {
                new UnitImageAnimation(canvas, attacker, sza).animate();
            }
        }

        if (!result.isSuccess()
            && Animations.getAnimationSpeed(canvas, defender) > 0) {
            direction = direction.getReverseDirection();
            if ((sza = getAnimation(canvas, defender, direction)) != null) {
                new UnitImageAnimation(canvas, defender, sza).animate();
            }
        }
    }
}
