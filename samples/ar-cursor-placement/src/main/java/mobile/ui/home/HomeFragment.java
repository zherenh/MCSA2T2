package mobile.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import io.github.sceneview.sample.arcursorplacement.R;
import io.github.sceneview.sample.arcursorplacement.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    ListAdapter listAdapter;

    ArrayList<ListData> dataArrayList = new ArrayList<>();

    ListData listData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ArrayList<ListData> dataArrayList = new ArrayList<>();
        int[] imageList = new int[]{R.drawable.bed, R.drawable.bed, R.drawable.bed};
        String[] nameList = new String[]{"Bed", "Bed", "Bed"};
        String[] sizeList = new String[]{"30*40*50", "30*40*50", "30*40*50"};

        for (int i = 0; i < imageList.length; i++) {
            listData = new ListData(nameList[i], sizeList[i], imageList[i]);
            dataArrayList.add(listData);
        }
        listAdapter = new ListAdapter(requireContext(), dataArrayList);
        binding.listView.setAdapter(listAdapter);
        binding.listView.setClickable(true);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(packageContext:MainActivity.this, DetailedActivity.class;
//                startActivity(intent);
            }
        });
        makeRequest();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void makeRequest() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = "https://mobiles-2a62216dada4.herokuapp.com/model";
        Log.e("requestTest", "url-set");


        JsonArrayRequest jsonArrayRequest =
                new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    ArrayList<ListData> dataItems = new ArrayList<>();
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject itemObj = response.getJSONObject(i);
                                        String name = itemObj.getString("name");
                                        String imgB64 = itemObj.getString("image");
                                        String glbB64 = itemObj.getString("data");
                                        byte[] decodeImage = Base64.decode(imgB64, Base64.DEFAULT);
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodeImage, 0, decodeImage.length);
                                        byte[] glbData = Base64.decode(glbB64, Base64.DEFAULT);

                                        saveByteArrayToInternalStorage(glbData, "model.glb");

                                        String stringdata =  readStorage();
                                        String first100Characters = stringdata.substring(stringdata.length() - 100);
                                        Log.e("requestTest", "stringdata"+first100Characters);

                                        ListData modelItem = new ListData(name, bitmap);
//                                        ListData modelItem = new ListData(name,"2",0);
                                        dataItems.add(modelItem);
                                        Log.e("requestTest", "pass");
                                    }
                                    Log.e("requestTest", String.valueOf(response.length()));
                                    // Now: all the data items are in the array list, send it to the recycler adapter to create views.
                                    ListAdapter adapter = new ListAdapter(requireContext(), dataItems);
                                    // assign the adapter to the recycler view
                                    binding.listView.setAdapter(adapter);
                                } catch (Exception e) {
                                    Log.d("stock", e.getMessage());
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("stock", error.getMessage());
                    }
                });
        // due to long response time, we need to add a long delay time
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

// Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static String getDataType(byte[] byteArray) {
        // Check for common data type signatures or patterns
        if (byteArray.length > 4) {
            if (byteArray[0] == 0x47 && byteArray[1] == 0x4C && byteArray[2] == 0x54) {
                return "GLB (glTF Binary)";
            }
        }

        return "Unknown"; // Default to "Unknown" if the data type is not recognized
    }

    public void saveByteArrayToFile(byte[] byteArray, String filePath) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(byteArray);
            fos.close();
            Log.d("File", "File saved at " + filePath);
        } catch (IOException e) {
            Log.e("File", "Error saving file: " + e.getMessage());
        }
    }

    private void saveByteArrayToInternalStorage(byte[] data, String filename) {
        try {

            File file = new File(requireContext().getFilesDir(), filename);
//            File file = new File("app/src/main/res/raw", filename);
            Log.e("requestTest", "GLB data saved to internal storage: " + file.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
            Log.e("requestTest", "GLB data saved to internal storage: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e("requestTest", "Error saving GLB data to internal storage: " + e.getMessage());
        }
    }


    private String readStorage() {
        String base64Data=null;
        try {
            File file = new File(requireContext().getFilesDir(), "model.glb"); // 用你的文件名替换 "your_file.glb"
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            base64Data= Base64.encodeToString(data, Base64.DEFAULT);
            // 此时，data 变量包含了文件的内容
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64Data;
    }

}
