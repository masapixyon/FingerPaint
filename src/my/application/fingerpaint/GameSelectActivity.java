package my.application.fingerpaint;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;


public class GameSelectActivity extends Activity implements OnClickListener{
  Intent intent;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //- 画面縦固定
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setContentView(R.layout.game_select);
    
    ImageView itouButton = (ImageView)findViewById(R.id.itou);
    itouButton.setOnClickListener(this);
    ImageView natumeButton = (ImageView)findViewById(R.id.natume);
    natumeButton.setOnClickListener(this);
    ImageView nogutiButton = (ImageView)findViewById(R.id.noguti);
    nogutiButton.setOnClickListener(this);
  }
  @Override
  public void onClick(View v) {
    intent = new Intent(GameSelectActivity.this,FingerPaintActivity.class);
    switch (v.getId()){
    case R.id.itou:
       intent.getIntExtra("result", 0 );
       startActivity(intent);
       break;
    
    case R.id.natume:
      intent.putExtra("result", 1 );
      startActivity(intent);
      break;

    case R.id.noguti:
      intent.putExtra("result", 2 );
      startActivity(intent);
      break;
    }
   }
}
