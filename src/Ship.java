
/**
 *
 * @author Xavier
 */
import java.awt.Polygon;
import java.awt.Rectangle;

public class Ship extends BaseVectorShape {

    //define o poligono da nave 
    private int[] shipx = {-6, -3, 0, 3, 6, 0};
    private int[] shipy = {6, 7, 7, 7, 6, -7};

    private int HP;

    private int score;

    public static final int INITIAL_SCORE = 0;

    public static final int INITIAL_HP = 100;

    public static final int COLISSION_DAMAGE = 25;

    public static final int ATTACK_DAMAGE = 80;

    public static final int SPECIAL_ATTACK_DAMAGE = 500;

    //retângulo delimitador
    public Rectangle getBounds() {
        Rectangle r;
        r = new Rectangle((int) getX() - 6, (int) getY() - 6, 12, 12);
        return r;
    }

    //o que é este elemnto ?
    Ship() {
        setShape(new Polygon(shipx, shipy, shipx.length));
        setAlive(true);
        setScore(Ship.INITIAL_SCORE);
        setHP(Ship.INITIAL_HP);
    }

    /**
     * Set HP of the Ship
     *
     * @param HP
     */
    public void setHP(int HP) {
        this.HP = HP > 0 ? HP : 0;
    }

    /**
     * Retrieve HP of the Ship
     *
     * @return
     */
    public int getHP() {
        return this.HP;
    }

    /**
     * Set score of the Ship
     *
     * @param score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Retrieve score of the Ship
     *
     * @return
     */
    public int getScore() {
        return this.score;
    }
}
