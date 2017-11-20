
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
    }
}
