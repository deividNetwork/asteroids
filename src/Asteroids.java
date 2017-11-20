
/**
 * @author Xavier
 * JOGO ASTEROIDS
 */
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Classe principal do jogo que herda de da classe Applet
 *
 * @author deividnetwork
 */
public class Asteroids extends Applet implements Runnable, KeyListener {

    //thread principal que representa o ciclo do jogo(game loop)
    Thread gameloop;

    //usa este BufferdImage como um buffer duplo
    BufferedImage backbuffer;

    //objeto de desenho principal para o buffer de fundo 
    Graphics2D g2d;

    //um alternador (chave) para desenhar caixas delimitadoras
    boolean showBounds = false;

    //cria o veltor de asteróides 
    int ASTEROIDS = 20;
    Asteroid[] ast = new Asteroid[ASTEROIDS];

    //cria o vetor de balas 
    int BULLETS = 100;
    Bullet[] bullet = new Bullet[BULLETS];
    int currentBullet = 0;

    // a nave do jogador 
    Ship ship = new Ship();

    //cria a transformada identidade(0,0)
    AffineTransform identity = new AffineTransform();

    //cria um gerador de números aleatórios
    Random rand = new Random();

    File background;

    /**
     * Evento init do applet
     */
    public void init() {
        background = new File("src/image/background.jpg");

        // posiciona a nave
        ship.setX(320);
        ship.setY(240);

        //inicializa as balas
        for (int n = 0; n < BULLETS; n++) {
            bullet[n] = new Bullet();
        }

        //cria os asteróides
        for (int n = 0; n < ASTEROIDS; n++) {
            ast[n] = new Asteroid();
            ast[n].setRotationVelocity(rand.nextInt(3) + 1);
            ast[n].setX((double) rand.nextInt(600) + 20);
            ast[n].setY((double) rand.nextInt(440) + 20);
            ast[n].setMoveAngle(rand.nextInt(360));
            double ang = ast[n].getMoveAngle() - 90;
            ast[n].setVelX(calcAngleMoveX(ang));
            ast[n].setVelY(calcAngleMoveY(ang));
        }

        //inicializar o listener para a teclas de entrada do usuário
        addKeyListener(this);
        setFocusable(true);
    }

    /**
     * Evento de update do applet para redesenhar a tela
     *
     * @param g
     */
    public void update(Graphics g) {
        try {
            //cria um buffer de fundo para renderizar os desenhos de forma suave, sem trepidações
            backbuffer = ImageIO.read(background);
            g2d = backbuffer.createGraphics();

            //inicializa as trnasformadas na identidade
            g2d.setTransform(identity);

            //imprime inforamção de status no canto superior da tela
            g2d.setColor(Color.WHITE);
            g2d.drawString("Ship: " + Math.round(ship.getX()) + ","
                    + Math.round(ship.getY()), 5, 15);
            g2d.drawString("Move angle: " + Math.round(
                    ship.getMoveAngle()) + 90, 5, 30);
            g2d.drawString("Face angle: " + Math.round(
                    ship.getFaceAngle()), 5, 45);
            g2d.drawString("HP: " + ship.getHP(), 5, 60);

            //desenha o gráfico do jogo
            drawShip();
            drawBullets();
            drawAsteroids();

            //redesenha a janela do applet
            paint(g);
        } catch (IOException ex) {
            Logger.getLogger(Asteroids.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * DrawShip desenha a nave na tela
     */
    public void drawShip() {
        g2d.setTransform(identity);
        g2d.translate(ship.getX(), ship.getY());
        g2d.rotate(Math.toRadians(ship.getFaceAngle()));
        g2d.setColor(Color.WHITE);
        g2d.fill(ship.getShape());
    }

    /**
     * drawBullets desenha as balas na tela
     */
    public void drawBullets() {

        //percorre o vetor de balas
        for (int n = 0; n < BULLETS; n++) {

            // o tiro está sendo usado?
            if (bullet[n].isAlive()) {

                //desenha a bala
                g2d.setTransform(identity);
                g2d.translate(bullet[n].getX(), bullet[n].getY());
                g2d.setColor(Color.WHITE);
                g2d.draw(bullet[n].getShape());
            }
        }
    }

    /**
     * drawAsteroids desenha os asteroids
     */
    public void drawAsteroids() {

        //percorre o vetor de asteróides
        for (int n = 0; n < ASTEROIDS; n++) {

            //o asteróide está ativo?
            if (ast[n].isAlive()) {

                //desenha o asteróide
                g2d.setTransform(identity);
                g2d.translate(ast[n].getX(), ast[n].getY());
                g2d.rotate(Math.toRadians(ast[n].getMoveAngle()));
                g2d.setColor(Color.WHITE);
                g2d.fill(ast[n].getShape());

            }
        }
    }

    /**
     * evento de repintar a janela -- desenhar o buffer de fundo
     *
     * @param g
     */
    public void paint(Graphics g) {

        //draw the back buffer onto the applet window
        g.drawImage(backbuffer, 0, 0, this);

    }

    /**
     * thread start event - start the game loop running
     */
    public void start() {

        //create the gameloop thread for real-time updates
        gameloop = new Thread(this);
        gameloop.start();
    }

    /**
     * evento run da thread (game loop)
     */
    public void run() {

        //adquire a thread atual
        Thread t = Thread.currentThread();

        //executa enquanto a thread está viva 
        while (t == gameloop) {

            try {
                //atualiza o  game loop
                gameUpdate();

                //o target framerate é de 50 fps
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            repaint();
        }
    }

    /**
     * evento stop da thread
     */
    public void stop() {

        // mata a thread (gameloop)
        gameloop = null;
    }

    /**
     * animação dos objetos no jogo
     */
    private void gameUpdate() {
        updateShip();
        updateBullets();
        updateAsteroids();
        checkCollisions();
    }

    /**
     * Atualiza a posição da nave baseado na velocidade
     */
    public void updateShip() {

        //update ship's X position
        ship.incX(ship.getVelX());

        //wrap around left/right
        if (ship.getX() < -10) {
            ship.setX(getSize().width + 10);
        } else if (ship.getX() > getSize().width + 10) {
            ship.setX(-10);
        }

        //update ship's Y position
        ship.incY(ship.getVelY());

        //wrap around top/bottom
        if (ship.getY() < -10) {
            ship.setY(getSize().height + 10);
        } else if (ship.getY() > getSize().height + 10) {
            ship.setY(-10);
        }
    }

    /**
     * Atualiza as balas baseado na velocidade
     */
    public void updateBullets() {

        //move cada uma das balas
        for (int n = 0; n < BULLETS; n++) {

            //a bala está sendo usada?
            if (bullet[n].isAlive()) {

                //atualiza a posição x da bala
                bullet[n].incX(bullet[n].getVelX());

                //a bala desaparece na borda esquerda/direita
                if (bullet[n].getX() < 0
                        || bullet[n].getX() > getSize().width) {
                    bullet[n].setAlive(false);
                }

                //atualiza a posição y da bala
                bullet[n].incY(bullet[n].getVelY());

                // a bala desaparece na borda superior/inferior
                if (bullet[n].getY() < 0
                        || bullet[n].getY() > getSize().height) {
                    bullet[n].setAlive(false);
                }
            }
        }
    }

    /**
     * Atualiza os asteróides baseado na velocidade
     */
    public void updateAsteroids() {

        // movimenta e rotaciona os asteróides
        for (int n = 0; n < ASTEROIDS; n++) {

            // o asteróide está ativo?
            if (ast[n].isAlive()) {

                //atuliza o valor de X do asteróide
                ast[n].incX(ast[n].getVelX());

                //corta o asteroide nas bordas da tela
                if (ast[n].getX() < -20) {
                    ast[n].setX(getSize().width + 20);
                } else if (ast[n].getX() > getSize().width + 20) {
                    ast[n].setX(-20);
                }

                //update the asteroid's Y value
                ast[n].incY(ast[n].getVelY());

                //corta o asteroide nas bordas da tela
                if (ast[n].getY() < -20) {
                    ast[n].setY(getSize().height + 20);
                } else if (ast[n].getY() > getSize().height + 20) {
                    ast[n].setY(-20);
                }

                //atualiza a rotação do asteróide
                ast[n].incMoveAngle(ast[n].getRotationVelocity());

                //mantem o ângulo entre 0-359 graus
                if (ast[n].getMoveAngle() < 0) {
                    ast[n].setMoveAngle(360 - ast[n].getRotationVelocity());
                } else if (ast[n].getMoveAngle() > 359) {
                    ast[n].setMoveAngle(ast[n].getRotationVelocity());
                }
            }
        }
    }

    /**
     * Verifica se os asteróides estão colidindo com as balas ou com a nave
     */
    public void checkCollisions() {

        //percorrer o vetor de asteróides
        for (int m = 0; m < ASTEROIDS; m++) {

            // o asteróide está ativo?
            if (ast[m].isAlive()) {

                /*
                 * verifica a colição com a bala 
                 */
                for (int n = 0; n < BULLETS; n++) {

                    //o tiro está sendo usado?
                    if (bullet[n].isAlive()) {

                        //executa o teste de colisão perform the collision test
                        if (ast[m].getBounds().contains(
                                bullet[n].getX(), bullet[n].getY())) {
                            bullet[n].setAlive(false);
                            ast[m].setAlive(false);
                            continue;
                        }
                    }
                }

                /*
                 * verifica colisão com a nave 
                 */
                if (ast[m].getBounds().intersects(ship.getBounds())) {
                    ast[m].setAlive(false);
                    ship.setX(320);
                    ship.setY(240);
                    ship.setFaceAngle(0);
                    ship.setVelX(0);
                    ship.setVelY(0);
                    ship.setHP(ship.getHP() - Ship.COLISSION_DAMAGE);

                    if (ship.getHP() <= 0) {
                        ship.setAlive(false);
                    }
                    //continue;
                }
            }
        }
    }

    /**
     * listener dos eventos de teclado--key listener events
     *
     * @param k
     */
    public void keyReleased(KeyEvent k) {
    }

    public void keyTyped(KeyEvent k) {
    }

    public void keyPressed(KeyEvent k) {
        int keyCode = k.getKeyCode();

        if (!ship.isAlive()) {
            return;
        }

        switch (keyCode) {

            case KeyEvent.VK_LEFT:
                //a seta esquerda rotaciona a nave em 5 graus à esquerda 
                ship.incFaceAngle(-5);
                if (ship.getFaceAngle() < 0) {
                    ship.setFaceAngle(360 - 5);
                }
                break;

            case KeyEvent.VK_RIGHT:
                //a seta direita rotaciona a nave em 5 graus à esquerda 
                ship.incFaceAngle(5);
                if (ship.getFaceAngle() > 360) {
                    ship.setFaceAngle(5);
                }
                break;

            case KeyEvent.VK_UP:
                //a seta para cima impulsiona a nave (1/10 da velocidade normal)
                ship.setMoveAngle(ship.getFaceAngle() - 90);
                ship.incVelX(calcAngleMoveX(ship.getMoveAngle()) * 0.1);
                ship.incVelY(calcAngleMoveY(ship.getMoveAngle()) * 0.1);
                break;
            case KeyEvent.VK_DOWN:
                //a seta para cima impulsiona a nave (1/10 da velocidade normal)
                ship.setMoveAngle(ship.getFaceAngle() + 90);
                ship.incVelX(calcAngleMoveX(ship.getMoveAngle()) * 0.1);
                ship.incVelY(calcAngleMoveY(ship.getMoveAngle()) * 0.1);
                break;
            //shift usado para freiar a nave
            case KeyEvent.VK_SHIFT:
                ship.setVelX(0);
                ship.setVelY(0);
                break;
            //Ctrl, Enter, ou Espaço são usadas para atirar
            case KeyEvent.VK_CONTROL:
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                //dispara uma bala
                currentBullet++;
                if (currentBullet > BULLETS - 1) {
                    currentBullet = 0;
                }
                bullet[currentBullet].setAlive(true);

                //aponta a bala para  mesma direção do bico da nave 
                bullet[currentBullet].setX(ship.getX());
                bullet[currentBullet].setY(ship.getY());
                bullet[currentBullet].setMoveAngle(ship.getFaceAngle() - 90);

                //dispara um tiro no mesmo ângulo da nave
                double angle = bullet[currentBullet].getMoveAngle();
                double svx = ship.getVelX();
                double svy = ship.getVelY();
                bullet[currentBullet].setVelX(svx + calcAngleMoveX(angle) * 2);
                bullet[currentBullet].setVelY(svy + calcAngleMoveY(angle) * 2);
                break;

        }
    }

    /**
     * calcula o movimento no eixo X baseado no ângulo da direção
     *
     * @param angle
     * @return
     */
    public double calcAngleMoveX(double angle) {
        return (double) (Math.cos(angle * Math.PI / 180));
    }

    /**
     * calcula o movimento no eixo X baseado no ângulo da direção
     *
     * @param angle
     * @return
     */
    public double calcAngleMoveY(double angle) {
        return (double) (Math.sin(angle * Math.PI / 180));
    }
}
