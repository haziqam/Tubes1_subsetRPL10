package Services;

import Enums.*;
import Models.*;


import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;
    private boolean afterBurnerStatus;
    private boolean superNovaStatus;
    private int supernova_tick = 0;
    private int afterburner_tick = 0;

    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
    }


    public GameObject getBot() {
        return this.bot;
    }

    public void setBot(GameObject bot) {
        this.bot = bot;
    }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }



    public int getWeight(GameObject x) {
        if (x.getGameObjectType() == ObjectTypes.FOOD) {
            return (3);
        } else if (x.getGameObjectType() == ObjectTypes.PLAYER) {
            if (x.getSize() < this.getBot().getSize()) {
                return (4);
            } else {
                return (-1);
            }
        } else if (x.getGameObjectType() == ObjectTypes.WORMHOLE) {
            return (-2);
        } else if (x.getGameObjectType() == ObjectTypes.GAS_CLOUD) {
            return (-5);
        } else if (x.getGameObjectType() == ObjectTypes.ASTEROID_FIELD) {
            return (-3);
        } else if (x.getGameObjectType() == ObjectTypes.TORPEDOSALVO) {
            return (-4);
        } else if (x.getGameObjectType() == ObjectTypes.SUPERFOOD) {
            return (5);
        } else if (x.getGameObjectType() == ObjectTypes.SUPERNOVAPICKUP) {
            return (6);
        } else if (x.getGameObjectType() == ObjectTypes.SUPERNOVABOMB) {
            return (-6);
        } else if (x.getGameObjectType() == ObjectTypes.TELEPORTER) {
            return (0);
        } else if (x.getGameObjectType() == ObjectTypes.SHIELD) {
            if (this.getBotRadius() >= 40) {
                return (2);
            } else {
                return (1);
            }
        } else {
            return (0);
        }
    }

    public PlayerAction action(GameObject x, PlayerAction playerAction) {
        double botRadius = this.getBotRadius();
        int botSize = this.bot.getSize();

        if (x.getGameObjectType() == ObjectTypes.FOOD) {
            playerAction.heading = getHeadingBetween(x);
            playerAction.action = PlayerActions.FORWARD;
        } else if (x.getGameObjectType() == ObjectTypes.PLAYER) {
            if (botSize > x.getSize()) {
                playerAction.heading = getHeadingBetween(x);
                if (botSize - 5 > 17) {
                    playerAction.action = PlayerActions.FIRETORPEDOES;
                } else if (botSize > 20) {
                    playerAction.action = PlayerActions.STARTAFTERBURNER;
                    this.afterBurnerStatus = true;
                } else {
                    playerAction.action = PlayerActions.FORWARD;
                }
            } else {
                if (x.getSize() - (10-getDistanceBetween(x, this.bot)) < botSize) {
                    if (botSize - 5 > 17 && this.getBot().torpedoSalvoCount >= 3) {
                        playerAction.heading = getHeadingBetween(x);
                        playerAction.action = PlayerActions.FIRETORPEDOES;
                    } else {
                        playerAction.heading = (getHeadingBetween(x) + 180) % 360;
                        playerAction.action = PlayerActions.FORWARD;
                    }
                } else if (this.getBot().supernovaAvailable != 0) {
                    playerAction.heading = getHeadingBetween(x);
                    playerAction.action = PlayerActions.FIRESUPERNOVA;
                    this.superNovaStatus = true;
                } else {
                    playerAction.heading = (getHeadingBetween(x) + 180) % 360;
                    playerAction.action = PlayerActions.FORWARD;
                }
            }
        } else if (x.getGameObjectType() == ObjectTypes.WORMHOLE) {
            playerAction.heading = (getHeadingBetween(x) + 90) % 360;
            playerAction.action = PlayerActions.FORWARD;
        } else if (x.getGameObjectType() == ObjectTypes.GAS_CLOUD) {
            playerAction.heading = (getHeadingBetween(x) + 180) % 360;
            playerAction.action = PlayerActions.FORWARD;
        } else if (x.getGameObjectType() == ObjectTypes.ASTEROID_FIELD) {
            playerAction.heading = (getHeadingBetween(x) + 180) % 360;
            playerAction.action = PlayerActions.FORWARD;
        } else if (x.getGameObjectType() == ObjectTypes.TORPEDOSALVO) {
            if (botSize > 20) {
                playerAction.heading = (getHeadingBetween(x) + 90) % 360;
                playerAction.action = PlayerActions.STARTAFTERBURNER;
                this.afterBurnerStatus = true;
            } else {
                playerAction.heading = (getHeadingBetween(x) + 90) % 360;
                playerAction.action = PlayerActions.FORWARD;
            }
        } else if (x.getGameObjectType() == ObjectTypes.SUPERFOOD) {
            if (botSize > 20) {
                playerAction.heading = getHeadingBetween(x);
                playerAction.action = PlayerActions.STARTAFTERBURNER;
                this.afterBurnerStatus = true;
            } else {
                playerAction.heading = getHeadingBetween(x);
                playerAction.action = PlayerActions.FORWARD;
            }
        } else if (x.getGameObjectType() == ObjectTypes.SUPERNOVAPICKUP) {
            playerAction.heading = getHeadingBetween(x);
            playerAction.action = PlayerActions.FORWARD;
        } else if (x.getGameObjectType() == ObjectTypes.SUPERNOVABOMB) {
            playerAction.heading = (getHeadingBetween(x) + 90) % 360;
            playerAction.action = PlayerActions.STARTAFTERBURNER;
            this.afterBurnerStatus = true;
        } else if (x.getGameObjectType() == ObjectTypes.TELEPORTER) {
            playerAction.heading = getHeadingBetween(x);
            playerAction.action = PlayerActions.FORWARD;
        } else if (x.getGameObjectType() == ObjectTypes.SHIELD) {
            playerAction.heading = getHeadingBetween(x);
            playerAction.action = PlayerActions.FORWARD;
        }
        
        if (this.supernova_tick < 2 && this.superNovaStatus == true) {
            this.supernova_tick = this.supernova_tick + 1;
        } else {
            this.supernova_tick = 0;
        }

        if (this.afterburner_tick < 2 && this.afterBurnerStatus == true) {
            this.afterburner_tick = this.afterburner_tick + 1;
        } else {
            this.afterburner_tick = 0;
        }

        return (playerAction);
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {
        int maks = -99;
        int num_arr = 1;
        
        if (gameState.getWorld().getRadius() != null) { 
            int worldRadius = gameState.getWorld().getRadius();
            double botRadius = getBotRadius();
            int botSize = this.bot.getSize();

            if (this.superNovaStatus == true && this.supernova_tick == 3) {
                playerAction.action = PlayerActions.DETONATESUPERNOVA;
                this.superNovaStatus = false;
                this.playerAction = playerAction;
                return;
            }
            
            if (this.afterBurnerStatus == true && this.afterburner_tick == 2) {
                playerAction.action = PlayerActions.STOPAFTERBURNER;
                this.afterBurnerStatus = false;
                this.playerAction = playerAction;
                return;
            }

            if (botRadius + 2*botSize >= worldRadius-80) {
                playerAction.heading = getHeadingToCenter();
                playerAction.action = PlayerActions.FORWARD;
                this.playerAction = playerAction;
                return;
            } else {
                if (!gameState.getGameObjects().isEmpty()) {
                    var OBJ = gameState.getGameObjects().stream().sorted(Comparator
                                                        .comparing(item -> getDistanceBetween(bot, item)))
                                                        .collect(Collectors.toList());
                    var PLY = gameState.getPlayerGameObjects().stream().sorted(Comparator
                                                            .comparing(item -> getDistanceBetween(bot, item)))
                                                            .collect(Collectors.toList());
                    if (worldRadius == 0 || PLY == null) {
                        //do nothing
                        return;
                    }
                    
                    for (int i = 0; i < 5; i++) {
                        if (getWeight(OBJ.get(i)) / getDistanceBetween(OBJ.get(i), bot) > maks) {
                            maks = i;
                            num_arr = 1;
                        }
                    }

                    for (int i = 0; i < 2; i++) {
                        if (getWeight(PLY.get(i)) / getDistanceBetween(PLY.get(i), bot) > maks) {
                            maks = i;
                            num_arr = 2;
                        }
                    }
                    
                    if (num_arr == 1) {
                        playerAction = action(OBJ.get(maks), playerAction);
                    } else {
                        playerAction = action(PLY.get(maks), playerAction);
                    }
                    
                } else {
                    playerAction.action = PlayerActions.FORWARD;
                }
            
            }
        }
        
        this.playerAction = playerAction;
        return;
    }



    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream().filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }

    private double getDistanceBetween(GameObject object1, GameObject object2) {
        //diganti dari var ke double
        double triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        double triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int getHeadingBetween(GameObject otherObject) {
        //diganti dari var ke int
        int direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }

    private double getBotRadius() {
        //method baru
        double currentX = this.getBot().getPosition().getX();
        double currentY = this.getBot().getPosition().getY();
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }

    private int getHeadingToCenter() {
        double currentX = this.getBot().getPosition().getX();
        double currentY = this.getBot().getPosition().getY();
        int headingToCenter = 180 + toDegrees(Math.atan2(currentY, currentX));
        return (headingToCenter)%360;
    }
}
