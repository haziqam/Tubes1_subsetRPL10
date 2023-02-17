package Enums;

public enum ObjectTypes {
  PLAYER(1, 0),
  FOOD(2, 2),
  WORMHOLE(3, -2),
  GASCLOUD(4, -4),
  ASTEROIDFIELD(5, -1),
  TORPEDOSALVO(6, -3),
  SUPERFOOD(7, 3),
  SUPERNOVAPICKUP(8, 1),
  SUPERNOVABOMB(9, -5),
  TELEPORTER(10, 0),
  SHIELD(11, 4);
  
  public final Integer value;
  // Tambahan atribut: profit/bobot setiap objek. Negatif jika harus dihindari, positif jika harus dikejar
  // dan nol netral atau perlu pertimbangan lain (misalnya player)
  private final Integer profit;

  ObjectTypes(Integer value, Integer profit) {
    this.value = value;
    this.profit = profit;
  }

  public static ObjectTypes valueOf(Integer value) {
    for (ObjectTypes objectType : ObjectTypes.values()) {
      if (objectType.value == value) return objectType;
    }

    throw new IllegalArgumentException("Value not found");
  }

  public Integer getProfit() {
    return this.profit;
  }
}