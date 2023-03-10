package Enums;

public enum PlayerActions {
  FORWARD(1),
  STOP(2),
  STARTAFTERBURNER(3),
  STOPAFTERBURNER(4),
  FIRETORPEDOES(5),
  FIRESUPERNOVA(6),
  DETONATESUPERNOVA(7),
  FIRETELEPORTER(8),
  TELEPORT(9),
  USESHIELD(10);

  public final Integer value;

  private PlayerActions(Integer value) {
    this.value = value;
  }
}
