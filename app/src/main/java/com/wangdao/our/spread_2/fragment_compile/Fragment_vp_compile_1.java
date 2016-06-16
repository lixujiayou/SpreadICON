package com.wangdao.our.spread_2.fragment_compile;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.unionpay.tsmservice.data.Constant;
import com.wangdao.our.spread_2.ExampleApplication;
import com.wangdao.our.spread_2.R;
import com.wangdao.our.spread_2.activity_.Article_info;
import com.wangdao.our.spread_2.activity_.LoginActivity;
import com.wangdao.our.spread_2.activity_.WebViewInfo;
import com.wangdao.our.spread_2.bean.CompileResult;
import com.wangdao.our.spread_2.bean.MyArticle;
import com.wangdao.our.spread_2.bean.RecommendArticle;
import com.wangdao.our.spread_2.slide_widget.AllUrl;
import com.wangdao.our.spread_2.slide_widget.PostObjectRequest;

import com.wangdao.our.spread_2.slide_widget.widget_image.RoundedImageView;
import com.wangdao.our.spread_2.slide_widget.widget_push.ResponseListener;
import com.wangdao.our.spread_2.widget_pull.PullToRefreshBase;
import com.wangdao.our.spread_2.widget_pull.PullToRefreshListView;
import com.wangdao.our.spread_2.widget_pull.PullToRefreshScrollView;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/21 0021.
 */
public class Fragment_vp_compile_1 extends Fragment{

    private PullToRefreshScrollView pull_ScrollView;
    private ListView myListView;
    private View myView;
    private Context myContext;
    private LayoutInflater myInflater;
    private List<RecommendArticle> list_reArticle;
    private AllUrl allurl = new AllUrl();

    private HttpPost httpPost;
    private HttpResponse httpResponse = null;
    private List<NameValuePair> params = new ArrayList<NameValuePair>();
    private FVC_Adapter fAdapter;
    private fcHandler_1 fhandler_1 = new fcHandler_1();
    private TextView tvnull;
    private final String myUrl = "http://wz.ijiaque.com/app/article/articledetail.html";
    private LinearLayout ll_nowifi;

    private NetBroadcast netBroadcast;
    private IntentFilter intentFilter;
    private  Ansy_1 ansy_1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.vp_compile_page1, null);
        myContext = this.getActivity();
        myInflater = inflater;

        initView();
        initListView();

        list_reArticle = new ArrayList<>();

        fAdapter = new FVC_Adapter(list_reArticle);
        myListView.setAdapter(fAdapter);
        setListViewHeightBasedOnChildren(myListView);
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        netBroadcast = new NetBroadcast();
        myContext.registerReceiver(netBroadcast, intentFilter);
        return myView;
    }

    private class NetBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            ConnectivityManager connectionManager =
                    (ConnectivityManager) myContext.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo netinfo = connectionManager.getActiveNetworkInfo();
            if(netinfo!=null&&netinfo.isAvailable()){
                ansy_1 = new Ansy_1();
                startInitData();
             //   initData();
            }else{
            }
        }
    }


    private Dialog dia_wait;
    private ImageView dialog_iv;
    private void startDialog(){

        View dialog_view = myInflater.inflate(R.layout.dialog_wait_2,null);
        dia_wait = new Dialog(myContext,R.style.dialog);
        dia_wait.setContentView(dialog_view);
        dialog_iv  = (ImageView) dialog_view.findViewById(R.id.dialog_wait_2_iv);

        Animation anim = AnimationUtils.loadAnimation(myContext, R.anim.dialog_zhuang);

        LinearInterpolator lir = new LinearInterpolator();
        anim.setInterpolator(lir);

        dialog_iv.startAnimation(anim);

        dia_wait.show();
    }



    /*
       * *Minong 测试数据post网络请求接口
       * @param value 测试数据
       * @param listener 回调接口，包含错误回调和正确的数据回调
       */
//    public static void postObjectMinongApi(String value,ResponseListener listener){
//        Map<String,String> param = new HashMap<String,String>() ;
//        param.put("test",value) ;
//        Request request = new PostObjectRequest(Constant.MinongHost,param,new TypeToken<TestBean>(){}.getType(),listener);
//        VolleyUtil.getRequestQueue().add(request) ;
//    }


    /**
     * 初始化数据
     */

    private int cNum = 1;
    private String queryResult;
    private void initData(){
        startDialog();
        cNum = 1;
        list_reArticle.clear();
        httpPost = new HttpPost(allurl.getWenZhangAll());
        params.add(new BasicNameValuePair("keywordtags","热文"));
        params.add(new BasicNameValuePair("page", "1"));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    httpResponse = new DefaultHttpClient().execute(httpPost);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        String result = EntityUtils.toString(httpResponse.getEntity());








                        Type type = new TypeToken<CompileResult>() {}.getType();
                        Gson gson = new Gson();
                        CompileResult compileResult = gson.fromJson(result, type);
                        Log.i("qqqqqqqq",compileResult.info);

                        for(RecommendArticle temp__:compileResult.data){
                            list_reArticle.add(temp__);
                        }
                        fhandler_1.sendEmptyMessage(1);

//                        Log.i("qqqqqqq1",list_reArticle.get(0).getWriting_title());
//                        Log.i("qqqqqqqq",list_reArticle.get(1).getWriting_title());

//                        JSONObject jo =new JSONObject(result);
//                        queryResult = jo.getString("info");
//                        if(jo.getString("status").equals("1")){
//                            JSONArray ja = jo.getJSONArray("data");
//                            RecommendArticle mWenZ = new RecommendArticle();
//
//                            Gson mgson = new Gson();
//
//                            for(int i = 0;i<ja.length();i++){
//                                JSONObject jo_2 = ja.getJSONObject(i);
//
//                             //   RecommendArticle mWenz_2 = mgson.fromJson(jo_2.toString(),RecommendArticle.class);
//
//                                mWenZ.setTitle(jo_2.getString("writing_title"));
//                                mWenZ.setTryNum(jo_2.getString("writing_use"));
//                                mWenZ.setIconUrl(jo_2.getString("writing_img"));
//                                mWenZ.setaId(jo_2.getString("id"));
//                                mWenZ.setContent_(jo_2.getString("writing_brief"));
//                                list_reArticle.add(mWenZ);
//
//
//                            }
//
//                        }else{
//                            fhandler_1.sendEmptyMessage(2);
//                        }
                    }
                } catch (Exception e) {
                    fhandler_1.sendEmptyMessage(2);
                    e.printStackTrace();
                }
            }
        }).start();
    }


private void startInitData(){

    startDialog();
    cNum = 1;
    list_reArticle.clear();
    httpPost = new HttpPost(allurl.getWenZhangAll());
    params.add(new BasicNameValuePair("keywordtags","热文"));
    params.add(new BasicNameValuePair("page", "1"));
    try {
        httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    }
    ansy_1 = new Ansy_1();
    ansy_1.execute("");

}

    class Ansy_1 extends AsyncTask<String,String,List<RecommendArticle>>{

        public Ansy_1(){

        }

        @Override
        protected List<RecommendArticle> doInBackground(String... params) {

            try {
                httpResponse = new DefaultHttpClient().execute(httpPost);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {

                    String result = EntityUtils.toString(httpResponse.getEntity());

                    Type type = new TypeToken<CompileResult>() {}.getType();
                    Gson gson = new Gson();
                    CompileResult compileResult = gson.fromJson(result, type);
                    Log.i("qqqqqqqq",compileResult.info);
                    queryResult = compileResult.info;

                    for(RecommendArticle temp__:compileResult.data){
                        list_reArticle.add(temp__);
                    }

                    fhandler_1.sendEmptyMessage(1);

                }
            } catch (Exception e) {
                fhandler_1.sendEmptyMessage(2);
                e.printStackTrace();
            }


            return list_reArticle;
        }


        @Override
        protected void onPostExecute(List<RecommendArticle> recommendArticles) {
            super.onPostExecute(recommendArticles);

            fAdapter.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(myListView);
            pull_ScrollView.onRefreshComplete();
            tvnull.setVisibility(View.GONE);
            myListView.setVisibility(View.VISIBLE);
            dia_wait.dismiss();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


    }

    /**
     * 压缩图片
     */
    private Bitmap yaSuoImage(Bitmap image){


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if(image == null){
            Bitmap bmm = BitmapFactory.decodeResource(this.getResources(), R.drawable.icon_2);
            return bmm;
        }

        image.compress(Bitmap.CompressFormat.JPEG, 85, out);

        float zoom = (float)Math.sqrt(10 * 1024 / (float)out.toByteArray().length);

        Matrix matrix = new Matrix();
        matrix.setScale(zoom, zoom);

        Bitmap result = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);

        out.reset();
        result.compress(Bitmap.CompressFormat.JPEG, 85, out);

        while(out.toByteArray().length > 10 * 1024){
            System.out.println(out.toByteArray().length);
            matrix.setScale(0.9f, 0.9f);
            result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
            out.reset();
            result.compress(Bitmap.CompressFormat.JPEG, 85, out);
        }
        return result;
    }
    private String result_2;
    private void initData_2(){

        startDialog();
    //    list_reArticle.clear();
        cNum += 1;
        httpPost = new HttpPost(allurl.getWenZhangAll());
        params.add(new BasicNameValuePair("keywordtags","热文"));
        params.add(new BasicNameValuePair("page", cNum+""));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    httpResponse = new DefaultHttpClient().execute(httpPost);

                    if (httpResponse.getStatusLine().getStatusCode() == 200) {

                        HttpEntity entity = httpResponse.getEntity();

                        if(entity != null){
                            InputStream is = entity.getContent();

                            result_2 = IOUtils.toString(is);
                            //result_2 = convertStreamToString(is);
                           // result_2 = result_2.replace("\n", "");
                        }

                        //String result = EntityUtils.toString(httpResponse.getEntity());

                        Type type = new TypeToken<CompileResult>() {}.getType();
                        Gson gson = new Gson();
                        CompileResult compileResult = gson.fromJson(result_2, type);
                        Log.i("qqqqqqqq",compileResult.info);
                        queryResult = compileResult.info;
                        for(RecommendArticle temp__:compileResult.data){
                            list_reArticle.add(temp__);
                        }
                        fhandler_1.sendEmptyMessage(1);


                        JSONObject jo =new JSONObject(result_2);
                       // queryResult = jo.getString("info");
//                        if(jo.getString("status").equals("1")){
//                            JSONArray ja = jo.getJSONArray("data");
//                            for(int i = 0;i<ja.length();i++){
//
//                                JSONObject jo_2 = ja.getJSONObject(i);
//                                RecommendArticle mWenZ = new RecommendArticle();
//                                mWenZ.setTitle(jo_2.getString("writing_title"));
//                                mWenZ.setTryNum(jo_2.getString("writing_use"));
//                                mWenZ.setIconUrl(jo_2.getString("writing_img"));
//                                mWenZ.setaId(jo_2.getString("id"));
//                                mWenZ.setContent_(jo_2.getString("writing_brief"));
//
//                                list_reArticle.add(mWenZ);
//                            }
//                            fhandler_1.sendEmptyMessage(1);
//                        }else{
//                            fhandler_1.sendEmptyMessage(2);
//                        }
                    }
                } catch (Exception e) {
                    fhandler_1.sendEmptyMessage(2);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String convertStreamToString(InputStream inputStream)
    {
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();
        String line;
        try {
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total.toString();
    }

class fcHandler_1 extends Handler{
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case 1:
                fAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(myListView);
                pull_ScrollView.onRefreshComplete();
                tvnull.setVisibility(View.GONE);
                myListView.setVisibility(View.VISIBLE);
                dia_wait.dismiss();
                break;
            case 2:
                Log.i("qqqqqq","queryResult"+queryResult);
                Toast.makeText(myContext,queryResult,Toast.LENGTH_SHORT).show();
                pull_ScrollView.onRefreshComplete();
               // tvnull.setText(queryResult);
               // tvnull.setVisibility(View.VISIBLE);
                dia_wait.dismiss();
                break;
        }
    }
}

        private void initView(){
            ll_nowifi = (LinearLayout) myView.findViewById(R.id.vp_compile_page1_ll_nowifi);
            pull_ScrollView = (PullToRefreshScrollView) myView.findViewById(R.id.vp_compile_page1_scrollview);
            myListView = (ListView) myView.findViewById(R.id.vp_compile_page1_pull);
            tvnull = (TextView) myView.findViewById(R.id.vp_compile_page1_tvnull);
        }
          private void initListView(){
              pull_ScrollView.setMode(PullToRefreshBase.Mode.BOTH);
              pull_ScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
                  @Override
                  public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                   //   initData();
                      startInitData();
                  }
                  @Override
                  public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                    //  initData();

                      initData_2();

                  }
              });
          }

    private String auid;
    class FVC_Adapter extends BaseAdapter{
        FVC_ViewHolder fvc_viewHolder;
        List<RecommendArticle> reArticles;

        public FVC_Adapter(List<RecommendArticle> reArticles){
            this.reArticles = reArticles;
        }
        @Override
        public int getCount() {
            return reArticles.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = myInflater.inflate(R.layout.item_compile,null);
                fvc_viewHolder = new FVC_ViewHolder();
                fvc_viewHolder.iv_icon = (RoundedImageView) convertView.findViewById(R.id.item_compile_iv_icon);
//                RoundingParams roundingParams = RoundingParams.fromCornersRadius(7f);
//                roundingParams.setOverlayColor(R.color.colorPrimary);


                fvc_viewHolder.tv_title = (TextView) convertView.findViewById(R.id.item_compile_tv_title);
                fvc_viewHolder.tv_num = (TextView) convertView.findViewById(R.id.item_compile_tv_num);
                fvc_viewHolder.bt_compile = (Button) convertView.findViewById(R.id.item_compile_bt);
                convertView.setTag(fvc_viewHolder);
            }else{
                fvc_viewHolder = (FVC_ViewHolder) convertView.getTag();
            }

            SharedPreferences sharedPreferences = myContext.getSharedPreferences("user", myContext.MODE_PRIVATE);
            auid = sharedPreferences.getString("uid", "");


            fvc_viewHolder.bt_compile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(myContext, Article_info.class);
                    myIntent.putExtra("url",myUrl+"?writing_id="+reArticles.get(position).getId()+"&uid="+auid);
                    myIntent.putExtra("uid", reArticles.get(position).getId());
                    myIntent.putExtra("title", reArticles.get(position).getWriting_title());
                    myIntent.putExtra("img",reArticles.get(position).getWriting_img());
                    myIntent.putExtra("content",reArticles.get(position).getWriting_brief());
                    startActivity(myIntent);
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(myContext, Article_info.class);
                    myIntent.putExtra("url",myUrl+"?writing_id="+reArticles.get(position).getId()+"&uid="+auid);
                    myIntent.putExtra("uid", reArticles.get(position).getId());
                    myIntent.putExtra("title", reArticles.get(position).getWriting_title());
                    myIntent.putExtra("img",reArticles.get(position).getWriting_img());
                    myIntent.putExtra("content",reArticles.get(position).getWriting_brief());
                    startActivity(myIntent);
                }
            });
            fvc_viewHolder.tv_title.setText(reArticles.get(position).getWriting_title());
            fvc_viewHolder.tv_num.setText("使用次数：\t"+reArticles.get(position).getWriting_use());


            ImageLoader.getInstance().displayImage(reArticles.get(position).getWriting_img() ==null ? "": reArticles.get(position).getWriting_img(),fvc_viewHolder.iv_icon,
                    ExampleApplication.getInstance().getOptions(R.drawable.moren)

            );

         //   asynImageLoader.showImageAsyn(fvc_viewHolder.iv_icon, reArticles.get(position).getIconUrl(), R.drawable.nopic);
            return convertView;
        }
    }
    class FVC_ViewHolder{
        RoundedImageView iv_icon;
        TextView tv_title;
        TextView tv_num;
        Button bt_compile;
    }
    /***
     * 动态设置listview的高度 item 总布局必须是linearLayout
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1))
                + 15;
        listView.setLayoutParams(params);
    }
}
