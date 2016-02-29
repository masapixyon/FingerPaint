package my.application.fingerpaint;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;

public class FingerPaintActivity extends Activity implements OnTouchListener{
  Canvas canvas;
  Paint paint;
  Path path;
  Bitmap bitmap;
  float x1,y1;
  int w,h,drow_s,drow_h;
  
  final Integer[] thumbnailds = { R.drawable.itou, R.drawable.natume, R.drawable.noguti };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //- 画面縦固定
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setContentView(R.layout.activity_finger_paint);
    
    //ImageView1を取得
    
    ImageView iv = (ImageView) findViewById(R.id.imageView1);
    
    Intent intent = getIntent();
    int result =  intent.getIntExtra("result",0);
    Bitmap bmp = BitmapFactory.decodeResource(getResources(), thumbnailds[result]);
    
    Display disp=((WindowManager)getSystemService(
        Context.WINDOW_SERVICE)).getDefaultDisplay();
    
    Point size = new Point();
    disp.getSize(size);
    int w = size.x;
    int h = size.y;
    
    bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    
    //キャンバスや場所の作成
    paint = new Paint();
    path = new Path();
    canvas = new Canvas(bitmap);
    
    //線の設定
    paint.setStrokeWidth(10);
    paint.setColor(Color.BLACK);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeJoin(Paint.Join.ROUND);
    paint.setStrokeCap(Paint.Cap.ROUND);
    
    canvas.drawColor(Color.BLACK);
    
    // リサイズ画像の高さ
    drow_h = (w / 2) * 3;
    // 描画始点の高さ
    drow_s = (h - drow_h) / 2;
    
    //リサイズ
    //canvasの背景として設定
    bmp = Bitmap.createScaledBitmap(bmp, w, drow_h , true); 
    canvas.drawBitmap(bmp, 0, drow_s, paint);
    
   //bitmapとTouchをセット
    iv.setImageBitmap(bitmap);
    iv.setOnTouchListener(this);

  }
  
  @Override
  public boolean onTouch(View v, MotionEvent event) {
    float x = event.getX();
    float y = event.getY();
    
    switch (event.getAction()){
    
    //画面をタッチした時
    case MotionEvent.ACTION_DOWN:
      // pathの初期化
      path.reset();
      path.moveTo(x, y);
      x1 = x;
      y1 = y;
      break;
      
    //画面でタッチされた点が移動した時
    case MotionEvent.ACTION_MOVE: 
      path.quadTo(x1, y1, x, y);
      x1 = x;
      y1 = y;
      
      //ACTIOM_DOWM地点(x1,y1)から動いた位置(x,y)までを描画
      //canvasに描画
      canvas.drawPath(path, paint);
      path.reset();
      path.moveTo(x, y);
      break;
      
    //画面から手が離れた時
    case MotionEvent.ACTION_UP:
      //ACTION_MOVEがない時の描画(点)
      if(x == x1 && y == y1)
      y1 = y1 + 1;
      path.quadTo(x1, y1, x, y);
      canvas.drawPath(path,paint);
      path.reset();
      break;
    }
    ImageView iv = (ImageView)this.findViewById(R.id.imageView1);
//    iv.setImageResource(R.drawable.itou);
    iv.setImageBitmap(bitmap);
    return true;
  }
  
  //保存
  void save(){
    //連番を保持
    //getDefaultSharedPreferencesメソッドを使って,SharedPreferencesクラスのインスタンスを取得する
    //設定データの名前とアプリないでののみのアクセス可能なデータの生成
    
    SharedPreferences prefs = getSharedPreferences("Save",MODE_PRIVATE);
    int imageNumber = prefs.getInt("imageNumber", 1);
    File file = null;
      
    //microSDカードの有無のチェック
    //連番のformを作成
    //保存先場所のmypaintファイルをpathに設定
    //ファイルが見当たらなければ作成
    
    if(externalMediaChecker()){
      DecimalFormat form = new DecimalFormat("0000");
      String path = Environment.getExternalStorageDirectory() + "/mypaint/";
      File outDir = new File(path);
      if(!outDir.exists()){  outDir.mkdir(); }     
      
      //ファイルの名前を設定
      //ファイルが存在すれば連番の修正
      //SharedPreferencesクラスのeditメソッドを使ってEditorクラスのインスタンスを取得する
      //EditorクラスのputIntメソッドでキーと値を指定して保存する
      //Editorクラスのcommitメソッドで保存を完了する
      
      //保存時にscanMediaを呼び出すようにする
      
      do{
        file = new File(path + "img" + form.format(imageNumber) + ".png");
        imageNumber++;
      }while(file.exists());
      if(writeImage(file)){
        scanMedia(file.getPath());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("imageNumber", imageNumber);
        editor.commit();
      }
    }
  }
  

  //画像をPNG形式に変換し保存
  boolean writeImage(File file) {
    
    //実際に書き込みするメソッド
    //例外があれば表示
    
    try{
      FileOutputStream fo = new FileOutputStream(file);
      bitmap.compress(CompressFormat.PNG, 100, fo);
      fo.flush();
      fo.close();
    } catch(Exception e){
      System.out.println(e.getLocalizedMessage());
      return false;
    }
    return true;
  }

  //microSDカードの有無のチェック
  boolean externalMediaChecker() {
    //Environment.getExternalStorageState()でSDカードがマウントされていればMEDIA_MOUNTEDを返す
    //なぜかマウントされてなくても、trueを返している
    
    boolean result = false;
    String status = Environment.getExternalStorageState();
    if(status.equals(Environment.MEDIA_MOUNTED)){
      result = true;
    }
    return result;
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater mi = getMenuInflater();
    mi.inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()){
      case R.id.menu_save:
        save();
        break;
    }
    return super.onOptionsItemSelected(item);
  }
  
  
  //ギャラリーに表示するためのスキャン
  MediaScannerConnection mc;
  void scanMedia(final String fp){
    mc = new MediaScannerConnection(this,
        new MediaScannerConnection.MediaScannerConnectionClient(){

      @Override
      public void onScanCompleted(String path, Uri uri) {
        disconnect(); 
      }
      @Override
      public void onMediaScannerConnected() {
        scanFile(fp);    
      }

    });
  }
  
  //メディアスキャンからの切断
  void disconnect() { mc.disconnect(); }
  
  //ファイルのPathとMIMEタイプを指定し、メディアをスキャン
  void scanFile(String fp) { mc.scanFile(fp, "image/png"); }
  
  
  
  
 
}
