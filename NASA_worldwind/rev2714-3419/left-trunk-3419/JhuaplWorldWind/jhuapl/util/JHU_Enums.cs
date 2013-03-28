using System;
namespace jhuapl.util
{
 public class JHU_Enums
 {
  public enum Affiliations
  {
   PENDING,
   UNKNOWN,
   ASSUMED_FRIEND,
   FRIEND,
   NEUTRAL,
   SUSPECT,
   HOSTILE,
   EXERCISE_PENDING,
   EXERCISE_UNKNOWN,
   EXERCISE_ASSUMED_FRIEND,
   EXERCISE_FRIEND,
   EXERCISE_NEUTRAL,
   JOKER,
   FAKER
  }
  public enum BattleDimensions
  {
   UNKNOWN,
   SPACE,
   AIR,
   GROUND,
   SEA_SURFACE,
   SEA_SUBSURFACE,
   SOF,
   OTHER
  }
  [Flags]
  public enum AnchorStyles
  {
   None = 0x0000,
   Top = 0x0001,
   Bottom = 0x0002,
   Left = 0x0004,
   Right = 0x0008,
  }
  public JHU_Enums()
  {
  }
 }
}
