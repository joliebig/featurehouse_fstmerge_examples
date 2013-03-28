

package net.sf.freecol.client.gui;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.FreeColException;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.FoundingFather;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Nation;
import net.sf.freecol.common.model.Ownable;
import net.sf.freecol.common.model.ResourceType;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileType;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Settlement.SettlementType;
import net.sf.freecol.common.model.Unit.Role;
import net.sf.freecol.common.resources.ResourceManager;


public final class ImageLibrary {

    private static final Logger logger = Logger.getLogger(ImageLibrary.class.getName());    
    
    public static final String UNIT_SELECT = "unitSelect.image",
                               DELETE = "delete.image",
                               PLOWED = "model.improvement.plow.image",
                               TILE_TAKEN = "tileTaken.image",
                               TILE_OWNED_BY_INDIANS = "nativeLand.image",
                               LOST_CITY_RUMOUR = "lostCityRumour.image",
                               DARKNESS = "halo.dark.image";

    
    private final float scalingFactor;


    
    public ImageLibrary() {
        this(1);
    }

    public ImageLibrary(float scalingFactor) {
        this.scalingFactor = scalingFactor;
    }


    
    public float getScalingFactor() {
        return scalingFactor;
    }

    
    public ImageLibrary getScaledImageLibrary(float scalingFactor) throws FreeColException {
        return new ImageLibrary(scalingFactor);
    }

    
    public Image getFoundingFatherImage(FoundingFather father) {
        return ResourceManager.getImage(father.getId() + ".image");
    }

    
    public Image getMonarchImage(Nation nation) {
        return ResourceManager.getImage(nation.getId() + ".monarch.image");
    }

    
    public ImageIcon getMonarchImageIcon(Nation nation) {
        return ResourceManager.getImageIcon(nation.getId() + ".monarch.image");
    }

    
    public ImageIcon getCoatOfArmsImageIcon(Nation nation) {
        return ResourceManager.getImageIcon(nation.getId() + ".coat-of-arms.image");
    }

    
    public Image getCoatOfArmsImage(Nation nation) {
        return getCoatOfArmsImage(nation, scalingFactor);
    }

    public Image getCoatOfArmsImage(Nation nation, double scale) {
        return ResourceManager.getImage(nation.getId() + ".coat-of-arms.image", scale);
    }

    
    public Image getBonusImage(Tile tile) {
        if (tile.hasResource()) {
            return getBonusImage(tile.getTileItemContainer().getResource().getType());
        } else {
            return null;
        }
    }

    public Image getBonusImage(ResourceType type) {
        return getBonusImage(type, scalingFactor);
    }

    public Image getBonusImage(ResourceType type, double scale) {
        return ResourceManager.getImage(type.getId() + ".image", scale);
    }

    
    public ImageIcon getBonusImageIcon(ResourceType type) {
        return new ImageIcon(getBonusImage(type));
    }

    public ImageIcon getScaledBonusImageIcon(ResourceType type, float scale) {
        return new ImageIcon(getBonusImage(type, scale));
    }


    
    private ImageIcon convertToGrayscale(Image image) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        ColorConvertOp filter = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        BufferedImage srcImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        srcImage.createGraphics().drawImage(image, 0, 0, null);
        return new ImageIcon(filter.filter(srcImage, null));
    }


    
    public Image getCompoundTerrainImage(TileType type, double scale) {
        
        Image terrainImage = getTerrainImage(type, 0, 0, scale);
        Image overlayImage = getOverlayImage(type, 0, 0, scale);
        Image forestImage = getForestImage(type, scale);
        if (overlayImage == null && forestImage == null) {
            return terrainImage;
        } else {
            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration();
            int width = terrainImage.getWidth(null);
            int height = terrainImage.getHeight(null);
            if (overlayImage != null) {
                height = Math.max(height, overlayImage.getHeight(null));
            }
            if (forestImage != null) {
                height = Math.max(height, forestImage.getHeight(null));
            }
            BufferedImage compositeImage = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
            Graphics2D g = compositeImage.createGraphics();
            g.drawImage(terrainImage, 0, height - terrainImage.getHeight(null), null);
            if (overlayImage != null) {
                g.drawImage(overlayImage, 0, height - overlayImage.getHeight(null), null);
            }
            if (forestImage != null) {
                g.drawImage(forestImage, 0, height - forestImage.getHeight(null), null);
            }
            g.dispose();
            return compositeImage;
        }
    }

    
    public Image getOverlayImage(TileType type, int x, int y) {
        return getOverlayImage(type, x, y, scalingFactor);
    }

    public Image getOverlayImage(TileType type, int x, int y, double scale) {
        int index = (x + y) % 2;
        return ResourceManager.getImage(type.getId() + ".overlay" + index + ".image", scale);
    }

    
    public Image getTerrainImage(TileType type, int x, int y) {
        return getTerrainImage(type, x, y, scalingFactor);
    }

    public Image getTerrainImage(TileType type, int x, int y, double scale) {
        String key = (type == null) ? "model.tile.unexplored" : type.getId();
        
        
        
        int index = (( y % 8 <= 2) || ((x+y) % 2 == 0 )) ? 0 : 1;
        return ResourceManager.getImage(key + ".center" + index + ".image", scale);
    }

    
    public Image getBorderImage(TileType type, Direction direction, int x, int y) {
        String key = (type == null) ? "model.tile.unexplored" : type.getId();
        
        
        
        String index = (( y % 8 <= 2) || ((x+y) % 2 == 0 )) ? "_even" : "_odd";
        return ResourceManager.getImage(key + ".border_" + direction + index + ".image", scalingFactor);
    }

    
    public Image getRiverMouthImage(Direction direction, int magnitude, int x, int y) {
        String key = "delta_" + direction + (magnitude == 1 ? "_small" : "_large");
        return ResourceManager.getImage(key, scalingFactor);
    }

    
    public Image getRiverImage(int index) {
        return getRiverImage(index, scalingFactor);
    }

    public Image getRiverImage(int index, double scale) {
        return ResourceManager.getImage("river" + index, scale);
    }

    
    public Image getBeachImage(int index) {
        return ResourceManager.getImage("beach" + index, scalingFactor);
    }

    
    public Image getForestImage(TileType type) {
        return getForestImage(type, scalingFactor);
    }

    public Image getForestImage(TileType type, double scale) {
        return ResourceManager.getImage(type.getId() + ".forest", scale);
    }

    
    public Image getMiscImage(String id) {
        return getMiscImage(id, scalingFactor);
    }

    public Image getMiscImage(String id, double scale) {
        return ResourceManager.getImage(id, scale);
    }

    
    public ImageIcon getMiscImageIcon(String id) {
        return new ImageIcon(getMiscImage(id));
    }

    
    public Image getGoodsImage(GoodsType goodsType) {
        return getGoodsImage(goodsType, scalingFactor);
    }

    public Image getGoodsImage(GoodsType goodsType, double scale) {
        return ResourceManager.getImage(goodsType.getId() + ".image", scale);
    }

    
    public ImageIcon getGoodsImageIcon(GoodsType goodsType) {
        return ResourceManager.getImageIcon(goodsType.getId() + ".image");
    }

    
    public ImageIcon getScaledGoodsImageIcon(GoodsType type, double scale) {
        return new ImageIcon(getGoodsImage(type, scale));
    }

    
    public Image getColorChip(Ownable ownable, double scale) {
        return ResourceManager.getChip(ownable.getOwner().getNationID() + ".chip", scale);
    }

    
    public Image getMissionChip(Ownable ownable, boolean expertMission, double scale) {
        if (expertMission) {
            return ResourceManager.getChip(ownable.getOwner().getNationID()
                                           + ".mission.expert.chip", scale);
        } else {
            return ResourceManager.getChip(ownable.getOwner().getNationID()
                                           + ".mission.chip", scale);
        }
    }

    
    public Image getAlarmChip(Tension.Level alarm, final boolean visited, double scale) {
        if (visited) {
            return ResourceManager.getChip("alarmChip.visited."
                                           + alarm.toString().toLowerCase(), scale);
        } else {
            return ResourceManager.getChip("alarmChip." + alarm.toString().toLowerCase(), scale);
        }
    }

    
    public int getTerrainImageWidth(TileType type) {
        return getTerrainImage(type, 0, 0).getWidth(null);
    }

    
    public int getTerrainImageHeight(TileType type) {
        return getTerrainImage(type, 0, 0).getHeight(null);
    }

    
    public int getCompoundTerrainImageHeight(TileType type) {
        int height = getTerrainImageHeight(type);
        if (type != null) {
            Image overlayImage = getOverlayImage(type, 0, 0);
            if (overlayImage != null) {
                height = Math.max(height, overlayImage.getHeight(null));
            }
            if (type.isForested()) {
                height = Math.max(height, getForestImage(type).getHeight(null));
            }
        }
        return height;
    }

    
    public Image getSettlementImage(SettlementType settlementType) {
        return getSettlementImage(settlementType, scalingFactor);
    }

    public Image getSettlementImage(SettlementType settlementType, double scale) {
        return ResourceManager.getImage(settlementType.toString() + ".image", scale);
    }

    
    public Image getSettlementImage(Settlement settlement) {

        if (settlement instanceof Colony) {
            Colony colony = (Colony) settlement;

            
            if (colony.isUndead()) {
                return getSettlementImage(SettlementType.UNDEAD);
            } else {
                int stockadeLevel = 0;
                if (colony.getStockade() != null) {
                    stockadeLevel = colony.getStockade().getLevel();
                }
                int unitCount = colony.getUnitCount();
                switch(stockadeLevel) {
                case 0:
                    if (unitCount <= 3) {
                        return getSettlementImage(SettlementType.SMALL_COLONY);
                    } else if (unitCount <= 7) {
                        return getSettlementImage(SettlementType.MEDIUM_COLONY);
                    } else {
                        return getSettlementImage(SettlementType.LARGE_COLONY);
                    }
                case 1:
                    if (unitCount > 7) {
                        return getSettlementImage(SettlementType.LARGE_STOCKADE);
                    } else if (unitCount > 3) {
                        return getSettlementImage(SettlementType.MEDIUM_STOCKADE);
                    } else {
                        return getSettlementImage(SettlementType.SMALL_STOCKADE);
                    }
                case 2:
                    if (unitCount > 7) {
                        return getSettlementImage(SettlementType.LARGE_FORT);
                    } else {
                        return getSettlementImage(SettlementType.MEDIUM_FORT);
                    }
                case 3:
                    return getSettlementImage(SettlementType.LARGE_FORTRESS);
                default:
                    return getSettlementImage(SettlementType.SMALL_COLONY);
                }
            }

        } else { 
            return getSettlementImage(((IndianSettlement) settlement).getTypeOfSettlement());
        }
    }

    
    public ImageIcon getUnitImageIcon(Unit unit) {
        return getUnitImageIcon(unit.getType(), unit.getRole(), scalingFactor);
    }

    public ImageIcon getUnitImageIcon(Unit unit, double scale) {
        return getUnitImageIcon(unit.getType(), unit.getRole(), scale);
    }

    
    public ImageIcon getUnitImageIcon(UnitType unitType) {
        return getUnitImageIcon(unitType, scalingFactor);
    }

    public ImageIcon getUnitImageIcon(UnitType unitType, double scale) {
        Image im = ResourceManager.getImage(unitType.getId() + ".image", scale);
        return (im == null) ? null : new ImageIcon(im);
    }
    
    
    public ImageIcon getUnitImageIcon(UnitType unitType, Role role) {
        return getUnitImageIcon(unitType, role, scalingFactor);
    }

    public ImageIcon getUnitImageIcon(UnitType unitType, Role role, double scale) {
        final String roleStr = (role != Role.DEFAULT) ? "." + role.getId() : "";
        final Image im = ResourceManager.getImage(unitType.getId() + roleStr + ".image", scale);
        return (im != null) ? new ImageIcon(im) : null;
    }

    
    public ImageIcon getUnitImageIcon(Unit unit, boolean grayscale) {
        return getUnitImageIcon(unit.getType(), unit.getRole(), grayscale);
    }

    
    public ImageIcon getUnitImageIcon(UnitType unitType, boolean grayscale) {
        return getUnitImageIcon(unitType, Role.DEFAULT, grayscale);
    }

    
    public ImageIcon getUnitImageIcon(UnitType unitType, Role role, boolean grayscale) {
        return getUnitImageIcon(unitType, role, grayscale, scalingFactor);
    }

    public ImageIcon getUnitImageIcon(UnitType unitType, Role role, boolean grayscale, double scale) {
        if (grayscale) {
            String key = unitType.getId() + (role == Role.DEFAULT ? "" : "." + role.getId()) + ".image";
            final Image im = ResourceManager.getGrayscaleImage(key, scale);
            return (im != null) ? new ImageIcon(im) : null;
        } else {
            return getUnitImageIcon(unitType, role, scale);
        }
    }


}
