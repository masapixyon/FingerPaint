package my.application.fingerpaint;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends Activity implements OnClickListener{

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //- 画面縦固定
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setContentView(R.layout.activity_main);
    
    ImageButton aboutButton = (ImageButton)findViewById(R.id.game_start_button);
    aboutButton.setOnClickListener(this);
    ImageButton exitBotton = (ImageButton)findViewById(R.id.exit_button);
    exitBotton.setOnClickListener(this);
  }
      @Override
      public void onClick(View v) {
        switch(v.getId()){
        case R.id.game_start_button:
          Intent intent = new Intent(MainActivity.this, GameSelectActivity.class);
          startActivity(intent);
          break;     
        case R.id.exit_button:
          MainActivity.this.finish();
          break; 
      }   
        
  }
}