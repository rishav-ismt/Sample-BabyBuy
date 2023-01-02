package np.com.rishavchudal.myapplication.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import np.com.rishavchudal.myapplication.R;
import np.com.rishavchudal.myapplication.db.product.Product;
import np.com.rishavchudal.myapplication.utility.BitmapScalar;

public class DetailPageActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Product product;
    private ImageButton ibBack;
    private ImageView ivDetailImage;
    private TextView tvProductTitle, tvProductPrice, tvProductDescription;
    private ImageButton ibEdit, ibDelete, ibShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        //Receive the data from intent
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product_data");
        //use this product data to set on the detail page
        initViews();
        assignViews();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (product == null) {
            return;
        }
        LatLng latLng = new LatLng(
                Double.parseDouble(product.latitude),
                Double.parseDouble(product.longitude)
        );
        googleMap.addMarker(new MarkerOptions().position(latLng).title("Product Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private void initViews() {
        ibBack = findViewById(R.id.ib_back);
        ivDetailImage = findViewById(R.id.iv_product_image);
        tvProductTitle = findViewById(R.id.tv_product_title);
        tvProductDescription = findViewById(R.id.tv_product_description);
        tvProductPrice = findViewById(R.id.tv_product_price);
        ibEdit = findViewById(R.id.ib_edit);
        ibDelete = findViewById(R.id.ib_delete);
        ibShare = findViewById(R.id.ib_share);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);
    }

    private void assignViews() {
        ivDetailImage.setImageBitmap(getBitmapForImageView(ivDetailImage, product.image));
        tvProductTitle.setText(product.title);
        tvProductPrice.setText("£ " + product.price);
        tvProductDescription.setText(product.description);
    }

    private Bitmap getBitmapForImageView(View view, String imageUriPath) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                    getContentResolver(),
                    Uri.parse(imageUriPath)
            );
            bitmap = BitmapScalar.stretchToFill(
                    bitmap,
                    100,
                    100
            );
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_gallery);
        }
        return bitmap;
    }
}