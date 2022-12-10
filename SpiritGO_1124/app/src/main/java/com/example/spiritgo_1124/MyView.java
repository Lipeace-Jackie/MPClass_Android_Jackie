package com.example.spiritgo_1124;


import android.content.Context;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.os.Bundle;
        import android.util.AttributeSet;
import android.view.View;
        import android.widget.Button;

class MyView extends View{
    //생성자에 인수를 세개 준다. 생성자를 세개 다 만들어줘야한다. 생성자들앞에 public붙여야함
    public MyView(Context context){
        super(context);
    }
    public MyView(Context context,AttributeSet att){
        super(context,att);
    }
    public MyView(Context context,AttributeSet att, int a){
        super(context,att,a);
    }

    //사각형 좌표값의 값을 변수로 설정!
    int x1=100,y1=100,x2=300,y2=300;
    @Override //부모가 가진 onDraw와 오버라이딩
    public void onDraw(Canvas c){
        Paint paint= new Paint();
        paint.setColor(Color.DKGRAY);
        c.drawRect(x1, y1, x2, y2, paint);
    }
}
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button) findViewById(R.id.button01);
        btn.setOnClickListener(t);
        Button btn2 = (Button) findViewById(R.id.button02);
        btn2.setOnClickListener(t);
    }
    //왼쪽버튼누르면 사각형이 왼쪽으로가고 오른쪽버튼 누르면 사각형이 오른쪽으로 가도록 만들어라.
    Button.OnClickListener t = new Button.OnClickListener() {
        public void onClick(View v) {
            MyView mv = (MyView)findViewById(R.id.mv);
            Button btn= (Button)findViewById(v.getId());
            switch(v.getId()){  //id를 받아와 버튼에 조건을 건다.
                case R.id.button01: //id가 button01인것을 누르면, 왼쪽으로 이동하는 조건
                    int a = 0;
                    a -=5; //왼쪽으로 가므로 마이너스 값을 준다.
                    mv.x1 = mv.x1+a;
                    mv.x2 = mv.x2+a;
                    mv.invalidate(); //데이터갱신
                    break;
                case R.id.button02: //id가 button02인것을 누르면, 오른쪽으로 이동하는 조건
                    int b = 0;
                    b +=5; //오른쪽으로 가므로 플러스값 값을 준다.
                    mv.x1 = mv.x1+b;
                    mv.x2 = mv.x2+b;
                    mv.invalidate();
                    break;
            };
        }
    };
}