package in.megasoft.workplace;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class volleySingelton {


    private RequestQueue requestQueue;
    private static volleySingelton mInstance;

    private volleySingelton(Context context){
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());

    }
    public static  synchronized volleySingelton getmInstance(Context context){

        if (mInstance == null){
            mInstance = new volleySingelton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){return requestQueue;}
}
