package Models;

import Enums.*;
import java.util.*;

public class GameObject {
  public UUID id;
  public Integer size;
  public Integer speed;
  public Integer currentHeading;
  public Position position;
  public ObjectTypes gameObjectType;
  //tambahin atribut yg belum ada
  public int effectsHash;
  public int torpedoSalvoCount;
  public int supernovaAvailable;
  public int teleporterCount;
  public int shieldCount;

  public GameObject(UUID id, Integer size, Integer speed, Integer currentHeading, Position position, ObjectTypes gameObjectType,
  int effectsHash, int torpedoSalvoCount, int supernovaAvailable, int teleporterCount, int shieldCount) {
    this.id = id;
    this.size = size;
    this.speed = speed;
    this.currentHeading = currentHeading;
    this.position = position;
    this.gameObjectType = gameObjectType;
    this.effectsHash = effectsHash;
    this.torpedoSalvoCount = torpedoSalvoCount;
    this.supernovaAvailable = supernovaAvailable;
    this.teleporterCount = teleporterCount;
    this.shieldCount = shieldCount;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getSpeed() {
    return speed;
  }

  public void setSpeed(int speed) {
    this.speed = speed;
  }

  public Position getPosition() {
    return position;
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  public ObjectTypes getGameObjectType() {
    return gameObjectType;
  }

  public void setGameObjectType(ObjectTypes gameObjectType) {
    this.gameObjectType = gameObjectType;
  }

  public static GameObject FromStateList(UUID id, List<Integer> stateList)
  {
    if (stateList.get(3) == 1) 
    { // Untuk kasus player (gameObjectType == 1)
      Position position = new Position(stateList.get(4), stateList.get(5));
      return new GameObject(id, stateList.get(0), stateList.get(1), stateList.get(2), position, 
      ObjectTypes.valueOf(stateList.get(3)), stateList.get(6), stateList.get(7), stateList.get(8), 
      stateList.get(9), stateList.get(10));
    }
    else 
    { /* Untuk kasus selain player (gameObjectType != 1), panjang stateList hanya 6, shg atribut effectsHash, toerpedoSalvoCount
      effectsHash, torpedoSalvoCount, supernovaAvailable, teleporterCount, dan shieldCount di assign 0 sesuai instruksi di dokumentasi */
      Position position = new Position(stateList.get(4), stateList.get(5));
      return new GameObject(id, stateList.get(0), stateList.get(1), stateList.get(2), position, 
      ObjectTypes.valueOf(stateList.get(3)), 0, 0, 0, 
      0, 0);
    }
    
  }
}
