package in.megasoft.workplace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class FireworkView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isRunning = true;
    private final SurfaceHolder surfaceHolder;
    private final Paint paint;
    private final Random random;

    public FireworkView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        paint = new Paint();
        random = new Random();
    }

    @Override
    public void run() {
        while (isRunning) {
            if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas();

                // Clear the screen with a black background
                canvas.drawColor(Color.BLACK);

                // Draw fireworks
                for (int i = 0; i < 100; i++) {
                    paint.setColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                    float x = getWidth() / 2 + random.nextFloat() * 400 - 200;
                    float y = getHeight() / 2 + random.nextFloat() * 400 - 200;
                    canvas.drawCircle(x, y, random.nextFloat() * 10 + 5, paint);
                }

                surfaceHolder.unlockCanvasAndPost(canvas);

                // Slow down the animation
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void pause() {
        isRunning = false;
        try {
            if (thread != null) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }
}