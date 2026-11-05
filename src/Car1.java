/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package car1;

/*
Python (Pygame)	               Java
pygame.image.load()	->     ImageIO.read()
self.rect.x	        ->     rect.x
self.rect.y	       ->      rect.y
self.rect.width	       ->      rect.width
self.rect.height	->     rect.height
self.rect.topleft	->     rect.setLocation()
self.speed	       ->      speed
self.update()	       ->      update()
*/
/**
 *
 * @author sodaa
 */

import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Car1 {

    private BufferedImage image;
    private int startX;
    private int startY;
    private int speed;
    private Rectangle rect;

    public Car1(int x, int y, int speed) {
        try {
            image = ImageIO.read(new File("car1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.startX = x;
        this.startY = y;
        this.speed = speed;

        int width = (image != null) ? image.getWidth() : 0;
        int height = (image != null) ? image.getHeight() : 0;

        rect = new Rectangle(x, y, width, height);
    }

    public void update() {
        rect.x += speed;

        if (rect.x > 840 || rect.x + rect.width < 0) {
            rect.setLocation(startX, startY);
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public Rectangle getRect() {
        return rect;
    }
}