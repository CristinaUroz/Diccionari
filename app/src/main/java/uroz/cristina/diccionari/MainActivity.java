package uroz.cristina.diccionari;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String res_dir = "/diccionari/data/";
    private ImageView ima;
    private Button next;
    private Button skip;
    private Button delete;
    private Button add;
    private TextView textView;
    private String text_g=" ";
    private int cont=0;
    private int cont2=0;
    private int cont2_t=0;
    private List<Integer> no = new ArrayList<Integer>();
    private int total=1205;
    private boolean array = false;
    private Bitmap bitmap;
    private int[] pos = new int[2]; // Posicio del ImageView
    private int[] xy = new int[2]; // Posicio del "click" en el bitmap
    private int[] pix = new int[25];
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final String FILENAME = "shopping_list.txt";
    private static final int MAX_BYTES = 80000;
    private int x_aux=0;
    private int y_aux=0;
    private boolean tocat = false;
    private int im_i=0;

    @Override
    protected void onStop(){
        super.onStop();
        Write();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ima = (ImageView) findViewById(R.id.pixels);
        next = (Button) findViewById(R.id.next);
        skip = (Button) findViewById(R.id.skip);
        delete = (Button) findViewById(R.id.delete);
        add= (Button) findViewById(R.id.add_line);
        textView = (TextView) findViewById(R.id.text);



        // PERMISOS
        // Es demana el permis per utilitzar la camera, i quan s'acepta, es demana el permis per escriure fitxers
        // Demana permisos de camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Log.i("Bernat", "demana camera opcio 1");
            } else {
                Log.i("Bernat", "demana camera opcio 2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }

        Read();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarpunts();
                passarimatge();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!array){
                no.add(cont+im_i);
                cont2_t=cont2_t+1;}
                passarimatge();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_g = text_g  + ";";
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                novaimatge ();
            }
        });

        novaimatge ();

    }

    public void passarimatge(){
        if (!array){
            cont++;
            if (cont<=total){
                novaimatge ();}
            else{
                cont2=0;
                array=true;
                try {
                    String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + res_dir;

                    // Comprova si el directori existeix per crear-lo en cas de que no sigui així
                    File dir = new File(fullPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    File file = new File(fullPath+ "lines.txt");
                    if (!file.exists()) {
                        file.createNewFile();

                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file,true);
                    String t="no resolts"+ System.getProperty("line.separator");
                    for (int s=0;s<cont2_t;s++){
                        t=t+"ima"+no.get(s)+ System.getProperty("line.separator");
                    }
                    fileOutputStream.write((t + System.getProperty("line.separator")).getBytes());


                } catch (Exception e) {
                    Log.e("TAG","Error al guardar la imatge original"+ e.getMessage());
                }
            }
        }
        else{
            this.finish();
        }

    }

    private void novaimatge (){
        String i = "ima" +Integer.toString(cont+im_i);
        //Toast.makeText(this, i, Toast.LENGTH_LONG).show();
        bitmap=getDrawableId(i).getBitmap();
        bitmap = convertToMutable(bitmap);
       // bitmap.getPixels(pix, 0, 5, 0, 0, 5, 5);
        ima.setImageBitmap(bitmap);
        text_g = i + ";" ;//+ Arrays.toString(pix) + ";";
        textView.setText(Integer.toString(cont+im_i));
    }

    public BitmapDrawable getDrawableId(String name) {
        Field f = null;
        BitmapDrawable d=null;
        try {
            f = R.drawable.class.getField(name);
            d = (BitmapDrawable) getResources().getDrawable(f.getInt(null));
        } catch (NoSuchFieldException e) {
            Log.i("Reflection", "Missing drawable " + name);
        } catch (IllegalAccessException e) {
            Log.i("Reflection", "Illegal access to field " + name);
        }

        return d;
    }

    public void guardarpunts()  {
            try {
                String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + res_dir;

                // Comprova si el directori existeix per crear-lo en cas de que no sigui així
                File dir = new File(fullPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(fullPath+ "lines.txt");
                if (!file.exists()) {
                    file.createNewFile();

                }
                FileOutputStream fileOutputStream = new FileOutputStream(file,true);
                fileOutputStream.write((text_g + System.getProperty("line.separator")).getBytes());


            } catch (Exception e) {
                Log.e("TAG","Error al guardar la imatge original"+ e.getMessage());
            }

    }

    public boolean onTouchEvent(MotionEvent arg1) {
        function(1, arg1);
        return true;
    }

    public void posicio(int clickX, int clickY) {
        ima.getLocationOnScreen(pos);
        if (bitmap.getHeight() * ima.getWidth() / bitmap.getWidth() < ima.getHeight()) {
            // L'imatge ocupa tot lample del imageView
            xy[0] = (int) ((clickX - pos[0]) * bitmap.getWidth() / ima.getWidth());
            xy[1] = (int) ((clickY - pos[1]) * bitmap.getWidth() / ima.getWidth() - ((ima.getHeight() * bitmap.getWidth() / ima.getWidth() - bitmap.getHeight()) / 2));
        } else {
            // L'imatge ocupa tota l'alçada del imageView
            xy[0] = (int) ((clickX - pos[0]) * bitmap.getHeight() / ima.getHeight() - (ima.getWidth() * bitmap.getHeight() / ima.getHeight() - bitmap.getWidth()) / 2);
            xy[1] = (int) ((clickY - pos[1]) * bitmap.getHeight() / ima.getHeight());
        }
    }

    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            // Fitxer temporal de treball que conte els bits de la imatge (no es una imatge)
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            // Es crea un RandomAccessFile
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // Ample i alt del bitmap
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            // Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
            imgIn.copyPixelsToBuffer(map);
            imgIn.recycle();
            System.gc();

            // Es crea el bitmap que es podra editar i s'hi carrega l'anterior
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            // load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            // close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();
            // delete the temp file
            file.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imgIn;
    }

    private void Write(){
        try {
            File f = new File(FILENAME);
            f.delete();

            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            String t = Integer.toString(cont)+";"+Integer.toString(cont2)+";"+Boolean.toString(array);
            for (int s=0;s<cont2_t;s++){
                t=t+ "ima"+no.get(s)+ System.getProperty("line.separator");
            }
            fos.write(t.getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            Log.e("Cristina", "WriteItemList: FileNotFoundException");
            Toast.makeText(this, R.string.cannot_write, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("Cristina", "WriteItemList: IOException");
            Toast.makeText(this, R.string.cannot_write, Toast.LENGTH_SHORT).show();
        }

    }

    private void Read(){

        try {
            FileInputStream fis = openFileInput(FILENAME);
            byte [] buffer = new byte[MAX_BYTES];
            int nread = fis.read(buffer);
            if (nread>0) {
                String content = new String(buffer, 0, nread);
                String[] parts = content.split(";");
                cont=Integer.parseInt(parts[0]);
                cont2=Integer.parseInt(parts[0]);
                array=Boolean.parseBoolean(parts[2]);
                for (int s=3; s<parts.length;s++){
                    no.add(Integer.parseInt(parts[s]));
                }
            }
            fis.close();
        } catch (FileNotFoundException e) {
            Log.i("Cristina", "ReadItemList: FileNotFoundException");
        } catch (IOException e) {
            Log.e("Cristina", "WriteItemList: IOException");
            Toast.makeText(this, R.string.cannot_read, Toast.LENGTH_SHORT).show();
        }

    }

    // Metode per demanar permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("Bernat", "permis camera concedit");
                    // Demana permisos d'escriptura
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Log.i("Bernat", "demana escriptura opcio 1");
                        } else {
                            Log.i("Bernat", "demana escriptura opcio 2");
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    }
                } else {
                    Log.i("Bernat", "permis camera denegat");
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("Bernat", "permis escriptura concedit");
                } else {
                    Log.i("Bernat", "permis escriptura denegat");
                }
            }
            // Altres permisos, afegir 'case'
        }
    }

    public void function(int requestCode, MotionEvent arg1) {
        switch (requestCode) {
            case 1: {
                Log.i("cris","suup");
                // Deshabilitem que es pugui tornar a tocar la pantalla fins acabar la funcio
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                int clickX = (int) arg1.getX();
                int clickY = (int) arg1.getY();

                // Mirem que el click estigui dins l'ImageView
                if (pos[0] < clickX && clickX < (pos[0] + ima.getWidth())) {
                    if (pos[1] < clickY && clickY < (pos[1] + ima.getHeight())) {
                        // Trobem la posicio equivalent al bitmap
                        posicio(clickX, clickY);
                        if (xy[0] >= 0 && xy[0] < bitmap.getWidth() && xy[1] >= 0 && xy[1] < bitmap.getHeight()) {
                            // Guardem el color del pixel clickat del bitmap per comparar-lo amb la resta
                            bitmap.setPixel(xy[0], xy[1], Color.RED);
                            if (!tocat) {
                                text_g = text_g + Integer.toString(xy[0] / 2) + ";" + Integer.toString(xy[1] / 2) + ";";
                                ima.setImageBitmap(bitmap);
                                tocat=true;
                            }
                            else {tocat=false;}
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        } else {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    } else {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

                return;
            }

        }
    }

}
