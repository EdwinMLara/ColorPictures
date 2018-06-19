package com.example.emlar.colorpictures;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    public static final int PETICION_FOTO = 1;
    public static final int PETICION_VIDEO = 2;
    public static final int PETICION_GALERIA_FOTOS = 3;
    public static final int PETICION_GALERIA_VIDEOS = 4;

    public static final int MEDIA_FOTO = 5;
    public static final int MEDIA_VIDEO = 6;
    private static final int MAX_DURATION = 30;
    private static final String TAG = MainActivity.class.getSimpleName() ;
    private static final int CAMERA_WRITE_PERMISSION = 11;
    private static final int VIDEO_WRITE_PERMISSION = 12;

    private Uri mediaUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){

            if( requestCode == PETICION_FOTO){
                Intent intent   = new Intent(this,ImageActivity.class);
                intent.setData(mediaUri);
                startActivity(intent);
            }

            if (requestCode == PETICION_VIDEO){
                Intent intent = new Intent(Intent.ACTION_VIEW,mediaUri);
                intent.setDataAndType(mediaUri,"video/*");
                startActivity(intent);
            }

            if (requestCode == PETICION_GALERIA_FOTOS){
                Intent intent = new Intent(this,ImageActivity.class);
                intent.setData(data.getData());
                startActivity(intent);
            }

            if (requestCode == PETICION_GALERIA_VIDEOS){
                Intent intent = new Intent(this,VideoActivity.class);
                intent.setData(data.getData());
                startActivity(intent);
            }

        }else{
            Toast.makeText(this, "There is a mistake", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_GRANTED) {

            if (requestCode == CAMERA_WRITE_PERMISSION) {
                crearMedio(MEDIA_FOTO);
            }else if (requestCode == VIDEO_WRITE_PERMISSION){
                crearMedio(MEDIA_VIDEO);
            }
        }

        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void tomarFoto(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    mostrarExplicacion(PETICION_FOTO);
                }else{
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERA_WRITE_PERMISSION);
                }

            }else{
                crearMedio(MEDIA_FOTO);
            }
        }else {
            Log.d(TAG,"Verizion de android menor a 6");
            crearMedio(MEDIA_FOTO);
        }
    }

    private void mostrarExplicacion(final int tipoPeticion){
        new AlertDialog.Builder(this)
                .setTitle("Necesito tu permiso")
                .setMessage("Necesito tu permiso para poder almacenar las fotos y videos")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (tipoPeticion == PETICION_FOTO){
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERA_WRITE_PERMISSION);
                        }else if (tipoPeticion == PETICION_VIDEO){
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},VIDEO_WRITE_PERMISSION);
                        }else{
                            throw new IllegalArgumentException();
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this,"Ouch",Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }

    public void tomarVideo(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    mostrarExplicacion(PETICION_VIDEO);
                }else{
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},VIDEO_WRITE_PERMISSION);
                }

            }else{
                crearMedio(MEDIA_VIDEO);
            }
        }else {
            Log.d(TAG,"Verizion de android menor a 6");
            crearMedio(MEDIA_VIDEO);
        }
    }

    private void crearMedio (int tipoMedio){
        try {
            mediaUri = CrearArchivoMedio(tipoMedio);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mediaUri == null){
            Toast.makeText(this, "Problema al almacenar", Toast.LENGTH_SHORT).show();
        }else{
            if (tipoMedio == MEDIA_FOTO){
                iniciarCamara(mediaUri,PETICION_FOTO);
            }else if (tipoMedio == MEDIA_VIDEO){
                iniciarCamara(mediaUri,PETICION_VIDEO);
            }

        }
    }

    private Uri CrearArchivoMedio(int tipoMedio) throws IOException {

        if(!almacenamientoExternoDisponible())
            return null;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreArchivo ;
        File archivo;

        if (tipoMedio == MEDIA_FOTO){
            nombreArchivo = "IMG_" + timeStamp + "_";
            File directorioAlmacenamiento = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            archivo = File.createTempFile(nombreArchivo,".jpg",directorioAlmacenamiento);

            Log.d("TAG",archivo.getAbsolutePath());
            MediaScannerConnection.scanFile(this, new String[] { archivo.getPath() }, new String[] { "image/jpeg","video/mp4" }, null);

            return FileProvider.getUriForFile(MainActivity.this, "com.example.emlar.colorpictures.fileProvider", archivo);

        }else if (tipoMedio == MEDIA_VIDEO){
            nombreArchivo = "MOV_" + timeStamp + "_";
            File directorioAlmacenamiento = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            archivo = File.createTempFile(nombreArchivo,".mp4",directorioAlmacenamiento);

            Log.d("TAG",archivo.getAbsolutePath());

            return FileProvider.getUriForFile(MainActivity.this, "com.example.emlar.colorpictures.fileProvider", archivo);
        }else{
            return null;
        }
    }

    private void iniciarCamara(Uri mediaUri,int tipoPeticion) {
        Intent intent;

        if (tipoPeticion == PETICION_VIDEO) {
            intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_DURATION);
            startActivityForResult(intent, tipoPeticion);
        } else if (tipoPeticion == PETICION_FOTO) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
            startActivityForResult(intent,tipoPeticion);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void verGaleriaFotos(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,PETICION_GALERIA_FOTOS);
    }

    public void verGaleriaVideo(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent,PETICION_GALERIA_VIDEOS);

    }



    private Boolean almacenamientoExternoDisponible(){
        String estado = Environment.getExternalStorageState();
        if (estado.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }
}
