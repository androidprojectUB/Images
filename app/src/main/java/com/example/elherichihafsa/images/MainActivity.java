package com.example.elherichihafsa.images;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;

import static com.example.elherichihafsa.images.R.id.Nav_menu;
import static com.example.elherichihafsa.images.R.id.image;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {


    Button bPhoto, bSelection;

    SeekBar seekbarLuminosity, seekbarContrast;

    TextView textLuminosity, textContrast;

    private Bitmap bmp, bmpSave, operation;
    ImageView myImageView;

    int maxLum, progressIntLuminosity, maxContrast, progressIntContrast;

    private static int RESULT_LOAD_IMG = 1;

    private static final int CAMERA_REQUEST = 1888;

    //Prendre des photos depuis la caméra

    static String Camera_Photo_ImagePath = "";
    private static File f;
    private static int Take_Photo = 2;
    static String Camera_Photo_ImageName = "";
    public static String SaveFolderName;
    private static File gallery;

    //Zoom

    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;

    // Ces matrices seront utilisées pour enregistrer les pixels de l'image

    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // Les 3 états (events) que l'utilisateur essaie d'exécuter

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    //Ces objets PointF sont utilisés pour enregistrer le(s) point(s) que l'utilisateur touche
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private NavigationView nv;
    private View v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myImageView = (ImageView) findViewById(R.id.lenna);
        final Bitmap b = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.lenna);
        bmp = b.copy(Bitmap.Config.ARGB_8888, true);
        bmpSave = b.copy(Bitmap.Config.ARGB_8888, true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        mToolbar = (Toolbar) findViewById(R.id.nav_action);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nv = (NavigationView) findViewById(R.id.NavigationView);
        final View headerLayout = nv.getHeaderView(0);


        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            public boolean onNavigationItemSelected(MenuItem item) {
                if(mToggle.onOptionsItemSelected(item)){
                    return true;
                }
                item.setChecked(false);
                mDrawerLayout.closeDrawers();
                switch (item.getItemId()) {

                    case (R.id.nav_camera):
                        photoCamera();
                        return true;

                    case (R.id.nav_gallery):
                        photoGallery(v);
                        return true;

                    case (R.id.nav_quit):
                        finish();
                        return true;

                    case (R.id.nav_save):
                        save();
                        return true;

                    case (R.id.nav_reset):
                        reset();
                        return true;

                    case R.id.nav_gray:
                        toGray(bmp);
                        return true;

                    case R.id.nav_sepia:
                        sepia(bmp);
                        return true;

                    case R.id.nav_invert:
                        invert(bmp);
                        return true;

                    case R.id.nav_red:
                        colorFilter(bmp,1,0,0);
                        return true;

                    case R.id.nav_green:
                        colorFilter(bmp,0,1,0);
                        return true;

                    case R.id.nav_Blue:
                        colorFilter(bmp,0,0,1);
                        return true;

                    case R.id.nav_random:
                        toColorize(bmp);
                        return true;

                    case R.id.nav_colordepth:
                        decreaseColorDepth(bmp,64);
                        return true;

                    case R.id.nav_contrast:
                        contrast(bmp);
                        return true;

                    case R.id.nav_egalbw:
                        egalHistogramBlackAndWhite(bmp);
                        return true;

                    case R.id.nav_egalc:
                        egalHistogram(bmp);
                        return true;

                    case R.id.nav_moyenneur:
                        Moyenneur(bmp);
                        return true;

                    case R.id.nav_flip:
                        flip(bmp,true,true);
                        return true;

                    case R.id.nav_rotate:
                        rotate(bmp,90f);
                        return true;

                    case R.id.nav_Gauss3x3:
                        GaussianBlur3x3(bmp);
                        return true;

                    case R.id.nav_median:
                        median(bmp);
                        return true;

                    case R.id.nav_sharpen:
                        return true;

                    case R.id.nav_test:
                        return true;
                }
                return true;

            }
        });


        seekbarLuminosity = (SeekBar) findViewById(R.id.seekBarLuminosity);
        seekbarContrast = (SeekBar) findViewById(R.id.seekBarContrast);
        textLuminosity = (TextView) findViewById(R.id.textLuminosity);
        textContrast = (TextView) findViewById(R.id.textContrast);


        // Permissions d'accès au téléphone pour récupérer les images
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };


        maxContrast = 200;
        seekbarContrast.setMax(maxContrast);
        seekbarContrast.setProgress(maxContrast / 2);
        progressIntContrast = maxContrast / 2;


        // Création d'une seekBar pour le réglage du contraste
        seekbarContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int progressChange = progress;
                contrast(bmp, progressChange - progressIntContrast);
                progressIntContrast = progressChange;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        maxLum = getMax(bmp);
        seekbarLuminosity.setMax(maxLum);

        seekbarLuminosity.setProgress(maxLum / 2);
        progressIntLuminosity = maxLum / 2;

        // Création d'une seekBar pour le réglage de la luminosité
        seekbarLuminosity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int progressChange = progress;
                brightnessSeek(bmp, progressChange - progressIntLuminosity);
                progressIntLuminosity = progressChange;


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        myImageView.setOnTouchListener((View.OnTouchListener) this);


    }


    public void photoCamera() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            // créer un dossier pour contenir les photos prises
            SaveFolderName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/App Cam";
            gallery = new File(SaveFolderName);
            if (!gallery.exists())
                gallery.mkdirs();
            // enregistrer les photos prises
            Camera_Photo_ImageName = "Photo" + ".jpg";
            Camera_Photo_ImagePath = SaveFolderName + "/" + "PictureApp" + ".jpg";
            System.err.println(" Camera_Photo_ImagePath  " + Camera_Photo_ImagePath);
            f = new File(Camera_Photo_ImagePath);
            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f)), Take_Photo);
            System.err.println("f" + f);

        }
    }

    public void photoGallery(View v) {

                /*Vérification des permissions, si la permission n'est pas accordé pour accéder à la
                galerie on la demande, si l'utilisateur n'as pas accordé l'autorisation on ne peut pas
                y accéder tant qu'il ne mettera pas oui
                */

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            loadImagefromGallery(v);
        }
    }


    /* Les deux fonctions suivantes permettent de récupérer une image depuis la galerie
* et de l'affecter à la view pour pouvoir ensuite effectuer les différents traitement*/

    public void loadImagefromGallery(View view) {
        // Création d'un intent pour récupérer une image depuis la galerie
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Commencement de l'intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        try {

            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) { // Si l'image est récupérée depuis la galerie

                // Récupération de l'image
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Uri selectedImage = data.getData();

                // Obtention d'un curseur
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);

                // On se place sur la première colonne
                cursor.moveToFirst();

                // Affectation de l'image dans l'imageView après avoir décodé la source
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();


                myImageView = (ImageView) findViewById(R.id.lenna);

                bmp = BitmapFactory.decodeFile(picturePath);
                bmp = bmp.copy(Bitmap.Config.ARGB_8888,true);
                myImageView.setImageBitmap(bmp);

            } else if (requestCode == Take_Photo) { // Si l'image est prise depuis la caméra
                String filePath = null;

                filePath = Camera_Photo_ImagePath;
                if (filePath != null) {
                    bmp = (new_decode(new File(filePath)));

                    ExifInterface ei = new ExifInterface(Camera_Photo_ImagePath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            bmp = rotateImage(bmp, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            bmp = rotateImage(bmp, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            bmp = rotateImage(bmp, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:

                        default:
                            break;
                    }

                    int newHeight = (int) ( bmp.getHeight() * (512.0 / bmp.getWidth()) );
                    Bitmap putImage = Bitmap.createScaledBitmap(bmp, 512, newHeight, true);

                    myImageView.setImageBitmap(putImage);
                } else {
                    bmp = null;
                }
            } else {
                Toast.makeText(this, "Vous n'avez pas selectionné d'image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Un problème s'est produit", Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    // fonction permettant de décoder un fichier contenant une photo prise depuis la caméra
    /* Fonction récupérée sur un forum de stackOverFlow */

    public Bitmap new_decode(File f) {

        // decode image size
        int targetW = myImageView.getWidth();
        int targetH = myImageView.getHeight();

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scaleFactor = Math.min(width_tmp / targetW, height_tmp / targetH);
        // decode with inSampleSize
        try {
            o.inJustDecodeBounds = false;
            o.inSampleSize = scaleFactor;
            o.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            myImageView.setImageBitmap(bitmap);
            return bitmap;

        } catch (OutOfMemoryError e) {
            // TODO: handle exception
            e.printStackTrace();
            System.gc();
            return null;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public void reset() {
        myImageView = (ImageView) findViewById(R.id.lenna);
        final Bitmap b = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.lenna);
        bmp = b.copy(Bitmap.Config.ARGB_8888, true);
        bmp = bmpSave.copy(Bitmap.Config.ARGB_8888, true);
        myImageView.setImageBitmap(bmp);
        maxLum = getMax(bmp);
        maxContrast = 200;
        seekbarLuminosity.setProgress(maxLum / 2);
        seekbarContrast.setProgress(maxContrast / 2);

    }

    public void save() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Bitmap bitmap = ((BitmapDrawable) myImageView.getDrawable()).getBitmap();

            ContentResolver cr = getContentResolver();
            String title = "myBitmap";
            String description = "Modified Bitmap";
            String savedURL = MediaStore.Images.Media.insertImage(cr, bitmap, title, description);

            Toast.makeText(MainActivity.this, savedURL, Toast.LENGTH_LONG).show();
        }
    }

    public int getMax(Bitmap bmp) {
        int h = bmp.getHeight();
        int w = bmp.getWidth();
        int[] pixels = new int[h * w];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);


        int pixel0 = pixels[0];
        int r0 = Color.red(pixel0);
        int g0 = Color.green(pixel0);
        int b0 = Color.blue(pixel0);
        int max = (r0 + g0 + b0) / 3;

        for (int i = 1; i < h * w; ++i) {
            int pixel = pixels[i];
            int r = Color.red(pixel);
            int g = Color.green(pixel);
            int b = Color.blue(pixel);

            if ((r + g + b) / 3 > max) {
                max = (r + g + b) / 3;
            }
        }
        return max;
    }

    public void brightnessSeek(Bitmap bmp, int value) {

        // image size
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        // color information
        int A, R, G, B;
        int pixel;


        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        // scan through all pixels
        for (int i = 0; i < width * height; ++i) {

            // get pixel color
            pixel = pixels[i];
            A = Color.alpha(pixel);
            R = Color.red(pixel);
            G = Color.green(pixel);
            B = Color.blue(pixel);

            // increase/decrease each channel
            R += value;
            if (R > 255) {
                R = 255;
            } else if (R < 0) {
                R = 0;
            }

            G += value;
            if (G > 255) {
                G = 255;
            } else if (G < 0) {
                G = 0;
            }

            B += value;
            if (B > 255) {
                B = 255;
            } else if (B < 0) {
                B = 0;
            }

            pixels[i] = Color.rgb(R, G, B);

        }

        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
        myImageView.setImageBitmap(bmp);

    }

    public void flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        myImageView.setImageBitmap(bmp);
    }

    public void rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        myImageView.setImageBitmap(bmp);
    }

    public void colorFilter(Bitmap bmp, double red, double green, double blue) {
        operation = bmp.copy(Bitmap.Config.ARGB_8888,true);
        // image size
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        // color information
        int R, G, B;

        int[] pixels = new int[width*height];
        bmp.getPixels(pixels,0,width,0,0,width,height);

        // scan through all pixels
        for(int i = 0; i < pixels.length; ++i) {
                // apply filtering on each channel R, G, B
                R = (int)(Color.red(pixels[i]) * red);
                G = (int)(Color.green(pixels[i]) * green);
                B = (int)(Color.blue(pixels[i]) * blue);
                pixels[i] = Color.rgb(R,G,B);
        }
        operation.setPixels(pixels,0,width,0,0,width,height);
        myImageView.setImageBitmap(operation);
    }

    public void toGray(Bitmap bmp) {
        int h = bmp.getHeight();
        int w = bmp.getWidth();
        int[] pixels = new int[h * w];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        // récupère les pixels
        for (int i = 0; i < h * w; ++i) {
            // récupère les couleurs RGB du pixel
            int r = Color.red(pixels[i]);
            int b = Color.blue(pixels[i]);
            int g = Color.green(pixels[i]);
            // calcule une moyenne de la couleur
            int moy = (int) (0.3 * r + 0.59 * g + 0.11 * b);
            // affecte cette couleur aux pixels
            pixels[i] = Color.rgb(moy, moy, moy);
        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
        myImageView.setImageBitmap(bmp);
    }

    public void sepia(Bitmap bmp) {

        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int R, G, B;

        int[] pixels = new int[h * w];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        // Parcourt l'image
        for (int i = 0; i < pixels.length; i++) {

            R = Color.red(pixels[i]);
            G = Color.green(pixels[i]);
            B = Color.blue(pixels[i]);
            B = G = R = (int) (0.3 * R + 0.59 * G + 0.11 * B); // Griser les pixels
            // appliquer le niveau d'intensité nécessaire pour obtenir le filtre sepia à chaque canal de couleurs
            R += 94;
            if (R > 255) {
                R = 255;
            }

            G += 38;
            if (G > 255) {
                G = 255;
            }

            B += 18;
            if (B > 255) {
                B = 255;
            }
            pixels[i] = Color.rgb(R, G, B);
        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
        myImageView.setImageBitmap(bmp);
    }

    public void invert(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[h * w];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);

        for (int i = 0; i < pixels.length; i++) {
            int R = 255 - Color.red(pixels[i]);
            int G = 255 - Color.green(pixels[i]);
            int B = 255 - Color.blue(pixels[i]);

            pixels[i] = Color.rgb(R, G, B);
        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
        myImageView.setImageBitmap(bmp);
    }

public static Bitmap Median3x3(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();


        int[] tabPixel = new int[9];
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int x = 1; x < width - 1; ++x) {
            for (int y = 1; y < height - 1; ++y) {


                int index = 0;
                int i = 0;
                for (int u = -1; u <= 1; ++u) {
                    for (int v = -1; v <= 1; ++v) {
                        index = (y + v) * width + (x + u);
                        tabPixel[i] = pixels[index];
                        i += 1;

                    }
                }

                Arrays.sort(tabPixel);
                bmp.setPixel(x, y, tabPixel[(tabPixel.length / 2)]);
            }

        }
        return bmp;
    }
 
    public void toColorize(Bitmap bmp) {
        // Récupération des dimensions
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        int[] pixels = new int[width * height];
        // crée une variable aléatoire
        Random ran = new Random();
        // nbr va prendre en charge les possibilités [0 ... 360) pour la teinte
        int nbr = ran.nextInt(360);

        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        // prend pour chaque pixel ses composantes couleurs R G B
        for (int i = 0; i < height * width; ++i) {
            int p = pixels[i];
            int r = Color.red(p);
            int g = Color.green(p);
            int b = Color.blue(p);

            float[] hsv = new float[3];

            // Changement d'espace
            Color.RGBToHSV(r, g, b, hsv);
            hsv[0] = nbr;
            hsv[1] = 1.0f;

            // Re changement d'espace puis affectatiton de la valeur au pixels i
            pixels[i] = Color.HSVToColor(hsv);
        }
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
        myImageView.setImageBitmap(bmp);
    }

    public void decreaseColorDepth(Bitmap bmp, int bitOffset) {
        // get image size
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        // color information
        int  R, G, B;

        int[] pixels = new int[width*height];
        bmp.getPixels(pixels,0,width,0,0,width,height);

        // scan through all pixels
        for(int i = 0; i < pixels.length; ++i) {
                // get pixel color
                R = Color.red(pixels[i]);
                G = Color.green(pixels[i]);
                B = Color.blue(pixels[i]);

                // round-off color offset
                R = ((R + (bitOffset / 2)) - ((R + (bitOffset / 2)) % bitOffset) - 1);
                if(R < 0) { R = 0; }
                G = ((G + (bitOffset / 2)) - ((G + (bitOffset / 2)) % bitOffset) - 1);
                if(G < 0) { G = 0; }
                B = ((B + (bitOffset / 2)) - ((B + (bitOffset / 2)) % bitOffset) - 1);
                if(B < 0) { B = 0; }

            pixels[i] = Color.rgb(R,G,B);
            }

        bmp.setPixels(pixels,0,width,0,0,width,height);
        myImageView.setImageBitmap(bmp);
    }

    /* Fonction récupérée sur une page GitHub*/

    public void contrast(Bitmap bmp, double value) {
        // Taille de l'image
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        int R, G, B;
        int color;
        // Prend la valeur du contraste
        double contrast = Math.pow((100 + value) / 100, 2);

        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        // Parcourt l'image
        for (int i = 0; i < pixels.length; ++i) {
            // Prend la couleur du pixel
            color = pixels[i];
            // Applique le filtre contraste aux trois canaux de couleurs R, G, B
            R = Color.red(color);
            R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
            if (R < 0) {
                R = 0;
            } else if (R > 255) {
                R = 255;
            }

            G = Color.green(color);
            G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
            if (G < 0) {
                G = 0;
            } else if (G > 255) {
                G = 255;
            }

            B = Color.blue(color);
            B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
            if (B < 0) {
                B = 0;
            } else if (B > 255) {
                B = 255;
            }

            // Applique le changement de couleur à la bitmap
            bmp.setPixels(pixels, 0, width, 0, 0, width, height);
        }
        myImageView.setImageBitmap(bmp);
    }

    public int[] histogram(Bitmap bmp) {
        // Grise bmp
        toGray(bmp);
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] hist = new int[256]; // Crée un tableau de taille 256 pour chaque niveau de gris de bmp
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int color = bmp.getPixel(i, j); // Prend le niveau de gris du pixel
                int R = Color.red(color);
                hist[R] = hist[R] + 1; // Augmente le nombre de pixels ayant le niveau de gris correspondant
            }
        }
        return hist;
    }

    public int[] dynamic(Bitmap bmp) {
        // Calcule les valeurs max et min de l'histogramme de bmp

        int[] hist = histogram(bmp);
        int[] D = new int[2];
        int min = 0;
        int max = 0;
        int maxi = hist[0];
        int mini = hist[0];
        for (int i = 0; i < hist.length; i++) {
            if (hist[i] > maxi) {
                max = i;
            } else if (hist[i] < mini) {
                min = i;
            }
        }
        D[0] = max;
        D[1] = min;
        return D;
    }

    public void contrast(Bitmap bmp) {
        // Image size
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[h * w];

        int[] D = dynamic(bmp);

        bmp.getPixels(pixels, 0, w, 0, 0, w, h);

        // Applique l'extension linéaire de dynamique à l'image

        for (int i = 0; i < pixels.length; ++i) {
            int R = 255 * ((Color.red(pixels[i])) - D[1]) / (D[0] - D[1]);
            int G = 255 * ((Color.green(pixels[i])) - D[1]) / (D[0] - D[1]);
            int B = 255 * ((Color.blue(pixels[i])) - D[1]) / (D[0] - D[1]);
            pixels[i] = Color.rgb(R, G, B);
        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
        myImageView.setImageBitmap(bmp);
    }

    public void egalHistogram(Bitmap bmp) {
        operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        // Taille de l'image
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[h * w];
        // Calcule l'histogramme de l'image
        int[] histo = histogram(bmp);

        int[] C = new int[histo.length];
        C[0] = histo[0];
        // Calcule l'histogramme cumulé de l'image
        for (int i = 1; i < histo.length; i++) {
            C[i] = C[i - 1] + histo[i];
        }

        // Egalise l'histogramme de l'image
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        for (int i = 0; i < pixels.length; i++) {
            int R = Color.red(pixels[i]);
            R = C[R] * 255 / pixels.length;
            int G = Color.green(pixels[i]);
            G = C[G] * 255 / pixels.length;
            int B = Color.blue(pixels[i]);
            B = C[B] * 255 / pixels.length;

            pixels[i] = Color.rgb(R, G, B);
        }
        operation.setPixels(pixels, 0, w, 0, 0, w, h);
        myImageView.setImageBitmap(operation);
    }

    public void egalHistogramBlackAndWhite(Bitmap bmp) {
        // Taille de l'image
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[h * w];
        // Calcule l'histogramme de l'image
        int[] histo = histogram(bmp);

        int[] C = new int[histo.length];
        C[0] = histo[0];
        // Calcule l'histogramme cumulé de l'image
        for (int i = 1; i < histo.length; i++) {
            C[i] = C[i - 1] + histo[i];
        }

        // Egalise l'histogramme de l'image
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        for (int i = 0; i < pixels.length; i++) {
            int R = Color.red(pixels[i]);
            R = C[R] * 255 / pixels.length;
            int G = Color.green(pixels[i]);
            G = C[G] * 255 / pixels.length;
            int B = Color.blue(pixels[i]);
            B = C[B] * 255 / pixels.length;

            pixels[i] = Color.rgb(R, G, B);
        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
        myImageView.setImageBitmap(bmp);
    }


    public void Moyenneur(Bitmap bmp) {

        int SIZE = 3;

        int[][] Matrix = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                Matrix[i][j] = 1;
            }
        }

        int width = bmp.getWidth();
        int height = bmp.getHeight();

        int sumR, sumG, sumB = 0;
        int[] pixels = new int [width*height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int x = 1; x < width - 1; ++x) {
            for (int y = 1; y < height - 1; ++y) {

                sumR = sumG = sumB = 0;

                int index=0;

                for (int u = -1; u <= 1; ++u) {
                    for (int v = -1; v <= 1; ++v) {
                        index = (y+v)*width +(x+u);
                        sumR += Color.red(pixels[index]) * Matrix[u + 1][v + 1];
                        sumG += Color.green(pixels[index]) * Matrix[u + 1][v + 1];
                        sumB += Color.blue(pixels[index]) * Matrix[u + 1][v + 1];
                    }
                }

                sumR = sumR / 9;

                sumG = sumG / 9;

                sumB = sumB / 9;

                bmp.setPixel(x, y, Color.rgb(sumR, sumG, sumB));

            }
        }

        myImageView.setImageBitmap(bmp);
    }

    public void GaussianBlur3x3(Bitmap bmp) {

        int[][] Matrix = new int[][] {
                {1,2,1},
                {2,4,2},
                {1,2,1}
        };

        int width = bmp.getWidth();
        int height = bmp.getHeight();

        int sumR, sumG, sumB = 0;

        int[] pixels = new int [width*height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int x = 1; x < width - 1; ++x) {
            for (int y = 1; y < height - 1; ++y) {

                sumR = sumG = sumB = 0;
                int index=0;


                for (int u = -1; u <= 1; ++u) {
                    for (int v = -1; v <= 1; ++v) {

                        index = (y+v)*width +(x+u);
                        sumR += Color.red(pixels[index]) * Matrix[u + 1][v + 1];
                        sumG += Color.green(pixels[index]) * Matrix[u + 1][v + 1];
                        sumB += Color.blue(pixels[index]) * Matrix[u + 1][v + 1];
                    }
                }


                sumR = sumR / 16;

                sumG = sumG / 16;

                sumB = sumB / 16;


                bmp.setPixel(x, y, Color.rgb(sumR, sumG, sumB));

            }
        }

        myImageView.setImageBitmap(bmp);
    }


    public int[] sort(int[] tab){
        int k= 0;
        for(int i = 0;i<tab.length-1;i++){
            for(int j=i+1;j<tab.length;j++){
                if(tab[j]<tab[i]){
                    tab[i] = k;
                    tab[i] = tab[j];
                    tab[j] = k;
                }
            }
        }
        return tab;
    }
    public void median(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] tab = new int [9];
        int[] pixels = new int[w*h];

        bmp.getPixels(pixels, 0, w, 0, 0, w, h);

        int index = 0;
        // iteration through pixels
        for(int y = 1; y < h-1; ++y) {
            for(int x = 1; x < w-1; ++x) {
                for(int i=-1;i<2;i++){
                    for(int j=-1;j<2;j++){
                        index = (y+j)*w +(x+i);
                        for(int u=0;u<tab.length;u++){
                            tab[u] = pixels[index];
                        }
                        tab = sort(tab);
                        int med = tab[tab.length/2];
                        pixels[index] = med;
                    }
                }
            }
        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
        myImageView.setImageBitmap(bmp);
    }


    // Partie concernant l'implémentation du zoom.
        /* Fonction récupérée sur stackOverFlow */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(event);
        // Handle touch events here...

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG"); // write to LogCat
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                } else if (mode == ZOOM) {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        float F = (x * x + y * y) * (x * x + y * y);
        return F;
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Show an event in the LogCat view, for debugging
     */
    private void dumpEvent(MotionEvent event) {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
    }

}
