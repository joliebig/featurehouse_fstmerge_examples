

package net.sf.freecol.common.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class FeatureContainer {

    private Map<String, Set<Ability>> abilities = new HashMap<String, Set<Ability>>();
    private Map<String, Set<Modifier>> modifiers = new HashMap<String, Set<Modifier>>();

    public Set<Ability> getAbilities() {
        Set<Ability> result = new HashSet<Ability>();
        for (Set<Ability> abilitySet : abilities.values()) {
            result.addAll(abilitySet);
        }
        return result;
    }

    public Set<Modifier> getModifiers() {
        Set<Modifier> result = new HashSet<Modifier>();
        for (Set<Modifier> modifierSet : modifiers.values()) {
            result.addAll(modifierSet);
        }
        return result;
    }

    
    public Set<Ability> getAbilitySet(String id) {
        return getAbilitySet(id, null, null);
    }

    
    public Set<Ability> getAbilitySet(String id, FreeColGameObjectType objectType) {
        return getAbilitySet(id, objectType, null);
    }

    
    public Set<Ability> getAbilitySet(String id, FreeColGameObjectType objectType, Turn turn) {
        if (Specification.getSpecification().getAbilities(id) == null) {
            throw new IllegalArgumentException("Unknown ability key: " + id);
        }
        Set<Ability> abilitySet = abilities.get(id);
        if (abilitySet == null) {
            return new HashSet<Ability>();
        } else {
            Set<Ability> result = new HashSet<Ability>();
            for (Ability ability : abilitySet) {
                if (ability.appliesTo(objectType, turn)) {
                    result.add(ability);
                }
            }
            return result;
        }
    }

    
    public boolean hasAbility(String id) {
        return hasAbility(id, null, null);
    }

    
    public boolean hasAbility(String id, FreeColGameObjectType objectType) {
        return hasAbility(id, objectType, null);
    }

    
    public boolean hasAbility(String id, FreeColGameObjectType objectType, Turn turn) {
        if (Specification.getSpecification().getAbilities(id) == null) {
            throw new IllegalArgumentException("Unknown ability key: " + id);
        }
        Set<Ability> abilitySet = abilities.get(id);
        if (abilitySet == null) {
            return false;
        } else {
            boolean foundApplicableAbility = false;
            for (Ability ability : abilitySet) {
                if (ability.appliesTo(objectType, turn)) {
                    if (ability.getValue()) {
                        foundApplicableAbility = true;
                    } else {
                        return false;
                    }
                }
            }
            return foundApplicableAbility;
        }
    }

    
    public static boolean hasAbility(Set<Ability> abilitySet) {
        if (abilitySet.isEmpty()) {
            return false;
        } else {
            for (Ability ability : abilitySet) {
                if (!ability.getValue()) {
                    return false;
                }
            }
            return true;
        }
    }

    
    public Set<Modifier> getModifierSet(String id) {
        return getModifierSet(id, null, null);
    }

    
    public Set<Modifier> getModifierSet(String id, FreeColGameObjectType objectType) {
        return getModifierSet(id, objectType, null);
    }

    
    public Set<Modifier> getModifierSet(String id, FreeColGameObjectType objectType, Turn turn) {
        if (Specification.getSpecification().getModifiers(id) == null &&
            Specification.getSpecification().getType(id) == null) {
            throw new IllegalArgumentException("Unknown modifier key: " + id);
        }
        Set<Modifier> modifierSet = modifiers.get(id);
        if (modifierSet == null) {
            return new HashSet<Modifier>();
        } else if (objectType == null) {
            return modifierSet;
        } else {
            Set<Modifier> result = new HashSet<Modifier>();
            for (Modifier modifier : modifierSet) {
                if (modifier.appliesTo(objectType, turn)) {
                    result.add(modifier);
                }
            }
            return result;
        }
    }

    
    public float applyModifier(float number, String id) {
        return applyModifier(number, id, null, null);
    }

    
    public float applyModifier(float number, String id, FreeColGameObjectType objectType) {
        return applyModifier(number, id, objectType, null);
    }

    
    public float applyModifier(float number, String id, FreeColGameObjectType objectType, Turn turn) {
        return applyModifierSet(number, turn, getModifierSet(id, objectType, turn));
    }

    
    public static float applyModifiers(float number, Turn turn, List<Modifier> modifierSet) {
        if (modifierSet == null) {
            return number;
        }
        float result = number;
        for (Modifier modifier : modifierSet) {
            float value = modifier.getValue();
            if (value == Modifier.UNKNOWN) {
                return Modifier.UNKNOWN;
            }
            if (modifier.hasIncrement() && turn != null) {
                int diff = turn.getNumber() - modifier.getFirstTurn().getNumber();
                switch(modifier.getIncrementType()) {
                case ADDITIVE:
                    value += modifier.getIncrement() * diff;
                    break;
                case MULTIPLICATIVE:
                    value *= modifier.getIncrement() * diff;
                    break;
                case PERCENTAGE:
                    value += (value * modifier.getIncrement() * diff) / 100;
                    break;
                }
            }
            switch(modifier.getType()) {
            case ADDITIVE:
                result += value;
                break;
            case MULTIPLICATIVE:
                result *= value;
                break;
            case PERCENTAGE:
                result += (result * value) / 100;
                break;
            }
        }
        return result;
    }

    
    public static float applyModifierSet(float number, Turn turn, Set<Modifier> modifierSet) {
        if (modifierSet == null) {
            return number;
        }
        float additive = 0, percentage = 0, multiplicative = 1;
        for (Modifier modifier : modifierSet) {
            float value = modifier.getValue();
            if (value == Modifier.UNKNOWN) {
                return Modifier.UNKNOWN;
            }
            if (modifier.hasIncrement() && turn != null) {
                int diff = turn.getNumber() - modifier.getFirstTurn().getNumber();
                switch(modifier.getIncrementType()) {
                case ADDITIVE:
                    value += modifier.getIncrement() * diff;
                    break;
                case MULTIPLICATIVE:
                    value *= modifier.getIncrement() * diff;
                    break;
                case PERCENTAGE:
                    value += (value * modifier.getIncrement() * diff) / 100;
                    break;
                }
            }
            switch(modifier.getType()) {
            case ADDITIVE:
                additive += value;
                break;
            case MULTIPLICATIVE:
                multiplicative *= value;
                break;
            case PERCENTAGE:
                percentage += value;
                break;
            }
        }
        float result = number;
        result += additive;
        result *= multiplicative;
        result += (result * percentage) / 100;
        return result;
    }

    
    public boolean addAbility(Ability ability) {
        if (ability == null) {
            return false;
        }
        Set<Ability> abilitySet = abilities.get(ability.getId());
        if (abilitySet == null) {
            abilitySet = new HashSet<Ability>();
            abilities.put(ability.getId(), abilitySet);
        }
        return abilitySet.add(ability);
    }

    
    public boolean addModifier(Modifier modifier) {
        if (modifier == null) {
            return false;
        }
        Set<Modifier> modifierSet = modifiers.get(modifier.getId());
        if (modifierSet == null) {
            modifierSet = new HashSet<Modifier>();
            modifiers.put(modifier.getId(), modifierSet);
        }
        return modifierSet.add(modifier);
    }

    
    public Ability removeAbility(Ability oldAbility) {
        if (oldAbility == null) {
            return null;
        } else {
            Set<Ability> abilitySet = abilities.get(oldAbility.getId());
            if (abilitySet == null) {
                return null;
            } else if (abilitySet.remove(oldAbility)) {
                return oldAbility;
            } else {
                return null;
            }
        }
    }

    
    public Modifier removeModifier(Modifier oldModifier) {
        if (oldModifier == null) {
            return null;
        } else {
            Set<Modifier> modifierSet = modifiers.get(oldModifier.getId());
            if (modifierSet == null) {
                return null;
            } else if (modifierSet.remove(oldModifier)) {
                return oldModifier;
            } else {
                return null;
            }
        }
    }

    
    public void add(FeatureContainer featureContainer) {
        for (Entry<String, Set<Ability>> entry : featureContainer.abilities.entrySet()) {
            Set<Ability> abilitySet = abilities.get(entry.getKey());
            if (abilitySet == null) {
                abilities.put(entry.getKey(), new HashSet<Ability>(entry.getValue()));
            } else {
                abilitySet.addAll(entry.getValue());
            }
        }
        for (Entry<String, Set<Modifier>> entry : featureContainer.modifiers.entrySet()) {
            Set<Modifier> modifierSet = modifiers.get(entry.getKey());
            if (modifierSet == null) {
                modifiers.put(entry.getKey(), new HashSet<Modifier>(entry.getValue()));
            } else {
                modifierSet.addAll(entry.getValue());
            }
        }
    }

    
    public void remove(FeatureContainer featureContainer) {
        for (Entry<String, Set<Ability>> entry : featureContainer.abilities.entrySet()) {
            Set<Ability> abilitySet = abilities.get(entry.getKey());
            if (abilitySet != null) {
                abilitySet.removeAll(entry.getValue());
            }
        }
        for (Entry<String, Set<Modifier>> entry : featureContainer.modifiers.entrySet()) {
            Set<Modifier> modifierSet = modifiers.get(entry.getKey());
            if (modifierSet != null) {
                modifierSet.removeAll(entry.getValue());
            }
        }
    }

    public boolean containsAbilityKey(String key) {
        return abilities.containsKey(key);
    }

    public boolean containsModifierKey(String key) {
        return modifiers.containsKey(key);
    }

}
