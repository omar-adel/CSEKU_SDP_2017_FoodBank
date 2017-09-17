package com.example.bappy.foodbank;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class DisplayRestaurentFoodList extends AppCompatActivity {
    TextView txt;
    String restaurent_name;
    JSONObject jsonObject;
    JSONArray jsonArray;
    RestaurentFoodAdapter restaurentfoodAdapter;
    ListView listView;

    ArrayList<RestaurentFood> addRestaurantFood;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Boolean save_login;

    String name,resname,pass,type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_restaurent_foodlist_layout);
        sharedPreferences=getSharedPreferences(getString(R.string.PREF_FILE), 0);
        editor=sharedPreferences.edit();
        save_login=sharedPreferences.getBoolean(getString(R.string.SAVE_LOGIN),false);

        type=sharedPreferences.getString(getString(R.string.TYPE),"None");
        name=sharedPreferences.getString(getString(R.string.NAME),"None");
        pass=sharedPreferences.getString(getString(R.string.PASSWORD),"None");
        resname=sharedPreferences.getString(getString(R.string.RESTAURANT_NAME),"None");

        restaurent_name = getIntent().getExtras().getString("restaurent_name");

        addRestaurantFood=new ArrayList<>();
        new BackgroundTask().execute(restaurent_name);

        txt = (TextView) findViewById(R.id.txt);
        txt.setText(restaurent_name);
        listView = (ListView) findViewById(R.id.lisview);
        restaurentfoodAdapter = new RestaurentFoodAdapter(this, R.layout.restaurent_food_layout,restaurent_name,addRestaurantFood);
        listView.setAdapter(restaurentfoodAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        if(save_login)
            menuInflater.inflate(R.menu.menu_item_super,menu);
        else
            menuInflater.inflate(R.menu.menu_main2,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Logout:
                editor.clear();
                editor.commit();
                startActivity(new Intent(this, staff_login_resistor.class));
                finish();
                return true;
            case R.id.LogIn:
                startActivity(new Intent(this, staff_login_resistor.class));
                finish();
                return true;
            case R.id.my_profile:
                startActivity(new Intent(this, ShowProfile.class));
                return true;
            case R.id.new_restaurant:
                startActivity(new Intent(this, CreateNewRestaurant.class));
                return true;
            case R.id.edit_profile:
                Intent intent=new Intent(this, EditChangeProfile.class);
                intent.putExtra("op_type","Edit");
                startActivity(intent);
                return true;
            case R.id.delete_profile:
                AlertDialog.Builder alert =new AlertDialog.Builder(this);
                alert.setTitle("Attention");
                alert.setMessage("Are You Sure??");
                alert.setCancelable(true);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new BackgroundTask3().execute("Delete",name,name,resname,type,pass,pass);
                    }
                });
                alert.setNegativeButton("No,later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog al=alert.create();
                al.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class BackgroundTask3 extends AsyncTask<String,Void,Boolean> {
        String json_url;
        String JSON_STRING;
        String newpass, newname, op_type, resul, res_name, role;

        @Override
        protected void onPreExecute() {
            json_url = "http://" + getString(R.string.ip_address) + "/FoodBank/ProfileEditDelete.php";
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                op_type = params[0];
                String oldname = params[1];
                newname = params[2];
                res_name = params[3];
                role = params[4];
                String oldpass = params[5];
                newpass = params[6];
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputstream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedwritter = new BufferedWriter(new OutputStreamWriter(outputstream, "UTF-8"));
                String postdata = URLEncoder.encode("op_type", "UTF-8") + "=" + URLEncoder.encode(op_type, "UTF-8")
                        + "&" + URLEncoder.encode("oldname", "UTF-8") + "=" + URLEncoder.encode(oldname, "UTF-8")
                        + "&" + URLEncoder.encode("newname", "UTF-8") + "=" + URLEncoder.encode(newname, "UTF-8")
                        + "&" + URLEncoder.encode("res_name", "UTF-8") + "=" + URLEncoder.encode(res_name, "UTF-8")
                        + "&" + URLEncoder.encode("role", "UTF-8") + "=" + URLEncoder.encode(role, "UTF-8")
                        + "&" + URLEncoder.encode("oldpass", "UTF-8") + "=" + URLEncoder.encode(oldpass, "UTF-8")
                        + "&" + URLEncoder.encode("newpass", "UTF-8") + "=" + URLEncoder.encode(newpass, "UTF-8");
                bufferedwritter.write(postdata);
                bufferedwritter.flush();
                bufferedwritter.close();
                outputstream.close();
                InputStream inputstream = httpURLConnection.getInputStream();
                BufferedReader bufferdreader = new BufferedReader(new InputStreamReader(inputstream, "iso-8859-1"));
                resul = "";
                String line = "";
                while ((line = bufferdreader.readLine()) != null) {
                    resul += line;
                }
                bufferdreader.close();
                inputstream.close();
                httpURLConnection.disconnect();
                return true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            editor.clear();
            editor.commit();
            Intent intent = new Intent(DisplayRestaurentFoodList.this, staff_login_resistor.class);
            startActivity(intent);
            finish();
        }
    }


    public class RestaurentFoodAdapter extends ArrayAdapter {
        ArrayList<RestaurentFood> list=new ArrayList();
        Context ct;
        String frestarant,ffood,fprice;

        public RestaurentFoodAdapter(@NonNull Context context, @LayoutRes int resource, String s,ArrayList<RestaurentFood> string) {
            super(context, resource);
            ct=context;
            this.frestarant=s;
            list=string;
        }

        @Override
        public void add(@Nullable Object object) {
            super.add(object);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Nullable
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View restaurentfoodview;
            restaurentfoodview=convertView;
            RestaurentfoodHolder restaurentfoodHolder;
            if(restaurentfoodview==null)
            {
                LayoutInflater layoutInflater=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                restaurentfoodview=layoutInflater.inflate(R.layout.restaurent_food_layout,parent,false);
                restaurentfoodHolder=new RestaurentfoodHolder();
                restaurentfoodHolder.name=(TextView)restaurentfoodview.findViewById(R.id.t_name);
                restaurentfoodHolder.type=(TextView)restaurentfoodview.findViewById(R.id.t_type);
                restaurentfoodHolder.price=(TextView)restaurentfoodview.findViewById(R.id.t_price);
                restaurentfoodview.setTag(restaurentfoodHolder);
            }
            else
            {
                restaurentfoodHolder=(RestaurentfoodHolder) restaurentfoodview.getTag();
            }

            final RestaurentFood restaurentfood=(RestaurentFood) this.getItem(position);
            restaurentfoodHolder.name.setText(restaurentfood.getName());
            restaurentfoodHolder.type.setText(restaurentfood.getType());
            restaurentfoodHolder.price.setText(restaurentfood.getPrice());
            restaurentfoodview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ffood=restaurentfood.getName();
                    fprice=restaurentfood.getPrice();
                    AlertDialog.Builder paidbuilder = new AlertDialog.Builder(ct);
                    //setting the alertdialog title
                    paidbuilder.setTitle("Attention");
                    //setting the body message
                    paidbuilder.setMessage("Do You Want To Order It?");
                    //set state for cancelling state
                    paidbuilder.setCancelable(true);

                    //setting activity for positive state button
                    paidbuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!isNetworkAvilabe())
                                nointernet();
                            else {
                                if(save_login) {
                                    Intent intent = new Intent(ct, Order.class);
                                    intent.putExtra("restaurant", frestarant);
                                    intent.putExtra("food", ffood);
                                    intent.putExtra("price", fprice);
                                    intent.putExtra("resfood", "2");
                                    ct.startActivity(intent);
                                    finish();
                                }
                                else
                                    Toast.makeText(ct, "OPPS Sorry,U didn't SIGN IN as User", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    //setting activity for negative state button
                    paidbuilder.setNegativeButton("NO, Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    //alertdialog create
                    AlertDialog mydialog = paidbuilder.create();
                    //for working the alertdialog state
                    mydialog.show();
                }
            });
            return restaurentfoodview;
        }

        class RestaurentfoodHolder
        {
            TextView name,type,price;
        }
    }

    class BackgroundTask extends AsyncTask<String,Void,Boolean>
    {

        String json_url;
        String JSON_STRING;
        String name;

        @Override
        protected void onPreExecute() {
            json_url="http://"+getString(R.string.ip_address)+"/FoodBank/RestaurentFood.php";
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String type2 = params[0];
                name=type2;
                URL url=new URL(json_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputstream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedwritter = new BufferedWriter(new OutputStreamWriter(outputstream, "UTF-8"));
                String postdata = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(type2, "UTF-8");
                bufferedwritter.write(postdata);
                bufferedwritter.flush();
                bufferedwritter.close();
                outputstream.close();
                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder=new StringBuilder();
                while((JSON_STRING=bufferedReader.readLine())!=null){
                    stringBuilder.append(JSON_STRING+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                String restaurent_food_name= stringBuilder.toString().trim();

                jsonObject = new JSONObject(restaurent_food_name);
                jsonArray = jsonObject.getJSONArray("Server_response");

                int count = 0;
                String name, type, price;
                while (count < jsonArray.length()) {
                    JSONObject jo = jsonArray.getJSONObject(count);
                    name = jo.getString("name");
                    type = jo.getString("type");
                    price = jo.getString("foodprice");
                    RestaurentFood restaurentFood = new RestaurentFood(name, type, price);
                    addRestaurantFood.add(restaurentFood);
                    count++;
                }
                return true;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result.equals("false"))
                Toast.makeText(DisplayRestaurentFoodList.this, "can't connect to the database", Toast.LENGTH_SHORT).show();
            restaurentfoodAdapter.notifyDataSetChanged();
        }
    }

    public class RestaurentFood {

        private String name,type,price;

        public RestaurentFood(String name, String type, String price) {
            this.name = name;
            this.type = type;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }
    private boolean isNetworkAvilabe()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void nointernet() {
        //Creating an Alertdialog
        AlertDialog.Builder CheckBuild = new AlertDialog.Builder(DisplayRestaurentFoodList.this);
        CheckBuild.setIcon(R.drawable.no);
        CheckBuild.setTitle("Error!");
        CheckBuild.setMessage("Check Your Internet Connection");

        //Builder Retry Button

        CheckBuild.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                //Restart The Activity
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }

        });
        CheckBuild.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //Exit The Activity
                finish();
            }

        });
        AlertDialog alertDialog = CheckBuild.create();
        alertDialog.show();
    }
    //creating activity for back pressing from phone
    public void onBackPressed() {
        Intent intent=new Intent(DisplayRestaurentFoodList.this,DisplayRestaurentListView.class);
        startActivity(intent);
        finish();
    }
}