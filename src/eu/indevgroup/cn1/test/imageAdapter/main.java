package eu.indevgroup.cn1.test.imageAdapter;


import com.codename1.io.Log;
import com.codename1.ui.Component;
import com.codename1.ui.Display;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Form;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.URLImage;
import static com.codename1.ui.URLImage.RESIZE_SCALE_TO_FILL;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import java.io.IOException;

public class main {
    private static final String IMAGE_URL         = "http://test.ftp27.ru/img/logo";
    private static final String IMAGE_FORMAT      = ".png";
    private static final String IMAGE_PLACEHOLDER = "placeholder.png";

    private Form current;
    private Resources theme;
    
    public static final URLImage.ImageAdapter ToCircle = new URLImage.ImageAdapter() {
        
        int borderWidth = 6;
        
        public EncodedImage adaptImage(EncodedImage downloadedImage, EncodedImage placeholderImage) {
            Image originalImage;
            
            // Crop and resize
            int w = downloadedImage.getWidth();
            int h = downloadedImage.getHeight();
            if (w > h) {
                originalImage = downloadedImage.subImage(
                    (w-h)/2, 0, 
                    h, h, 
                    true
                );
            } else {
                originalImage = downloadedImage.subImage(
                    0, (h-w)/2, 
                    w, w, 
                    true
                );
            }
            
            
            int pS = Math.min(placeholderImage.getHeight(), placeholderImage.getWidth());
            originalImage = originalImage.scaledHeight(pS);
            
            originalImage = EncodedImage.createFromImage(originalImage, false);
            w = originalImage.getWidth();
            h = originalImage.getHeight();
            Log.p(Integer.toString(w)+";"+Integer.toString(h));
            Image finalImage = Image.createImage(w+2*borderWidth, h+2*borderWidth);

            Image maskedImage = originalImage.applyMask(
                createCircleMask(w,h)
            );
            
            Graphics g  = finalImage.getGraphics();
            g.setColor(0xff3d00);
            g.fillRect(
                    0, 0, 
                    finalImage.getWidth(), finalImage.getHeight() 
                   // 0, 360
            );
            g.drawImage(maskedImage, borderWidth, borderWidth);
            
            w = finalImage.getWidth();
            h = finalImage.getHeight();
            
            return EncodedImage.createFromImage(
                finalImage.applyMask(
                    createCircleMask(w,h)
                ),
                false
            );
        }
        
        public Object createCircleMask(int w, int h) {
            Image maskImage = Image.createImage(w, h);
            Graphics g = maskImage.getGraphics();
            g.setAntiAliased(true);
            g.setColor(0x000000);
            g.fillRect(0, 0, w, h);
            g.setColor(0xffffff);
            g.fillArc(0, 0, w, h, 0, 360);
            return maskImage.createMask();
        }

        public boolean isAsyncAdapter() {
            return false;
        }
    };

    public void init(Object context) {
        try {
            theme = Resources.openLayered("/theme");
            UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void start() {
        if(current != null){
            current.show();
            return;
        }
        
        Form hi = new Form();
        hi.getContentPane().setLayout(new GridLayout(3, 2));
        hi.getContentPane().setUIID("form");
        hi.getContentPane().setScrollableY(false);
        hi.show();
        
        EncodedImage placeholder = EncodedImage.createFromImage(
            theme.getImage(IMAGE_PLACEHOLDER)
                .scaledWidth(Display.getInstance().getDisplayWidth()/2),false
        );
        
        for (int i=0; i<6; i++) {
            Label l = new Label( 
                URLImage.createToStorage(
                    placeholder, 
                    "Image_"+Integer.toString(i), 
                    IMAGE_URL+i+IMAGE_FORMAT, 
                    ToCircle
                )
            );
            l.setUIID("Label");
            hi.addComponent(l);
        }
    }

    public void stop() {
        current = Display.getInstance().getCurrent();
    }
    
    public void destroy() {
    }

}
