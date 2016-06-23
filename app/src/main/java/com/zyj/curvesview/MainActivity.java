package com.zyj.curvesview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CurersView curersView = (CurersView) findViewById(R.id.curersView);
        List<DataEntity> entityList = new ArrayList<>();
        DateTime dateTime = new DateTime(new Date());
        DateTime time = dateTime.minusDays(6);
        String day = time.toString("yyyy-MM-dd");
        DateTime dateTime1 = new DateTime(day);
        DateTime time7 = dateTime.minusDays(0);
        String day7 = time7.toString("yyyy-MM-dd");
        DateTime dateTime7 = new DateTime(day7);

        for (int i = 0; i < 10; i++) {
            DataEntity dataEntity = new DataEntity();
            long millis = (long) (dateTime1.getMillis() + Math.random() * (dateTime7.getMillis() - dateTime1.getMillis()));
            dataEntity.setTime(millis);
            dataEntity.setFloat((float) (Math.random() * 200f));
            entityList.add(dataEntity);
        }
        curersView.setEntityList(entityList);

    }
}
