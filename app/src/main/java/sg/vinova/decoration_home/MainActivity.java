package sg.vinova.decoration_home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sg.vinova.decoration_home.FloorChange.FloorChangeActivity;
import sg.vinova.decoration_home.HomeDecoration.HomeDecorateActivity;
import sg.vinova.decoration_home.WallDecoration.WallDecorateActivity;

public class MainActivity extends Activity implements View.OnClickListener {
    @BindView(R.id.decor_home)
    TextView btnDecorHome;
    @BindView(R.id.wall_decor)
    TextView btnDecorWall;
    @BindView(R.id.change_floor)
    TextView btnChangeFloor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        btnDecorWall.setOnClickListener(this);
        btnDecorHome.setOnClickListener(this);
        btnChangeFloor.setOnClickListener(this);
    }

    private void changeActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_floor: {
                changeActivity(FloorChangeActivity.class);
            }
            break;
            case R.id.wall_decor: {
                changeActivity(WallDecorateActivity.class);

            }
            break;
            case R.id.decor_home: {
                changeActivity(HomeDecorateActivity.class);

            }
            break;
        }
    }
}

