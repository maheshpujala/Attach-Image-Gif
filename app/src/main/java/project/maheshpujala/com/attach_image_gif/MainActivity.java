package project.maheshpujala.com.attach_image_gif;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by maheshpujala on 23/12/16.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imageFinal,imageOne,imageTwo;
    LinearLayout showLayout;
    File savingGifFile;
    Button share,show,back;
    boolean Saved = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageOne = (ImageView) findViewById(R.id.image1);
        imageTwo = (ImageView) findViewById(R.id.image2);
        imageFinal= (ImageView) findViewById(R.id.image3);
        showLayout = (LinearLayout) findViewById(R.id.show_layout);

         share = (Button) findViewById(R.id.share_button);
         share.setOnClickListener(this);
         show = (Button) findViewById(R.id.show_button);
         show.setOnClickListener(this);
         back = (Button) findViewById(R.id.back_button);
         back.setOnClickListener(this);

        Glide.with(imageOne.getContext()).load(R.drawable.leo).into(imageOne);
        Glide.with(imageTwo.getContext()).load(R.drawable.dance).into(imageTwo);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.share_button:
                if(!Saved){
                    generateGIF();
                    shareMergedGif(Uri.fromFile(savingGifFile));
                }else{
                    shareMergedGif(Uri.fromFile(savingGifFile));
                }
                break;
            case R.id.show_button:
                if(!Saved){
                    generateGIF();
                    showMergedGif(Uri.fromFile(savingGifFile));
                }else{
                    showMergedGif(Uri.fromFile(savingGifFile));
                }
                break;
            case R.id.back_button:
                showLayout.setVisibility(View.GONE);
                break;
        }
    }
    public void generateGIF() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        options.inJustDecodeBounds = false;

        Bitmap mainBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.leo,options);
        Bitmap[] gifBitmaps =  {BitmapFactory.decodeResource(getResources(), R.drawable.dance_1),
                BitmapFactory.decodeResource(getResources(), R.drawable.dance_2),
                BitmapFactory.decodeResource(getResources(), R.drawable.dance_3),
                BitmapFactory.decodeResource(getResources(), R.drawable.dance_4),
                BitmapFactory.decodeResource(getResources(), R.drawable.dance_5),
                BitmapFactory.decodeResource(getResources(), R.drawable.dance_6)};
        int w, h = 0;
        h = mainBitmap.getHeight()/2 + gifBitmaps[0].getHeight();
        if (mainBitmap.getWidth() > gifBitmaps[0].getWidth()) {
            w = mainBitmap.getWidth();
        } else {
            w = gifBitmaps[0].getWidth();
        }
        Log.e("Height="+mainBitmap.getHeight(),"Width="+mainBitmap.getWidth());
        Bitmap bitmap = null;
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                mainBitmap, w, h/2, false);
        canvas.drawBitmap(resizedBitmap,0,0,null);

        ArrayList<Bitmap> bitmapsList = new ArrayList<>();

        for(int i =0; i<gifBitmaps.length;i++){

            bitmapsList.add(createGifFrames(mainBitmap,gifBitmaps[i]));
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(bos);
        encoder.addFrame(bitmap);
        encoder.setPosition(0,0);
        for (int i = 0;i < bitmapsList.size();i++) {
            bitmap =bitmapsList.get(i) ;
            encoder.setPosition(0,resizedBitmap.getHeight());
            encoder.addFrame(bitmap);
        }
        encoder.finish();
        saveMergedGif(bos.toByteArray());
    }

    public Bitmap createGifFrames(Bitmap bitmap1, Bitmap bitmap2) {
        Bitmap GifFrames = null;
        int w, h = 0;
        h = bitmap1.getHeight() + bitmap2.getHeight();
        if (bitmap1.getWidth() > bitmap2.getWidth()) {
            w = bitmap1.getWidth();
        } else {
            w = bitmap2.getWidth();
        }
        GifFrames = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(GifFrames);
        Matrix matrix = new Matrix();
        matrix.postTranslate(0,0);
        canvas.drawBitmap(bitmap2,matrix,null);
        return GifFrames;
    }

    private void saveMergedGif(byte[] mergedGifBytes) {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "Final_Output.gif";
        savingGifFile= new File(baseDir, fileName);
        FileOutputStream outStream = null;
        try{
            outStream = new FileOutputStream(savingGifFile);
            outStream.write(mergedGifBytes);
            outStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        Saved = true;
    }

    private void showMergedGif(Uri mergedGif) {
        showLayout.setVisibility(View.VISIBLE);
        Glide.with(imageFinal.getContext()).load(mergedGif).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(imageFinal);
    }
    private void shareMergedGif(Uri mergedGif) {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image/gif");
        shareIntent.putExtra(Intent.EXTRA_STREAM, mergedGif);
        startActivity(Intent.createChooser(shareIntent, "Share GIF"));
    }
}
