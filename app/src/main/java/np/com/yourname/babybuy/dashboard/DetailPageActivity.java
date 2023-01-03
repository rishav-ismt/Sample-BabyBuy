package np.com.yourname.babybuy.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.checkbox.MaterialCheckBox;

import np.com.yourname.babybuy.R;
import np.com.yourname.babybuy.db.BabyBuyDatabase;
import np.com.yourname.babybuy.db.product.Product;
import np.com.yourname.babybuy.db.product.ProductDao;
import np.com.yourname.babybuy.utility.BitmapScalar;

public class DetailPageActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Product product;
    private ImageButton ibBack;
    private ImageView ivDetailImage;
    private TextView tvProductTitle, tvProductPrice, tvProductDescription;
    private ImageButton ibEdit, ibDelete, ibShare;
    private MaterialCheckBox cbMarkAsPurchased;
    private GoogleMap googleMap;

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
        this.googleMap = googleMap;
        updateMapLocation(product, googleMap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == AddProductActivity.RESULT_CODE_SUCCESS_ADD_PRODUCT_ACTIVITY) {
                Product updatedProduct = (Product) data.getSerializableExtra("updated_product_data");
                if (updatedProduct != null) {
                    product = updatedProduct;
                    assignViews();
                    updateMapLocation(product, googleMap);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    private void initViews() {
        ibBack = findViewById(R.id.ib_back);
        ivDetailImage = findViewById(R.id.iv_product_image);
        tvProductTitle = findViewById(R.id.tv_product_title);
        tvProductDescription = findViewById(R.id.tv_product_description);
        tvProductPrice = findViewById(R.id.tv_product_price);
        cbMarkAsPurchased = findViewById(R.id.cb_mark_as_purchased);
        ibEdit = findViewById(R.id.ib_edit);
        ibDelete = findViewById(R.id.ib_delete);
        ibShare = findViewById(R.id.ib_share);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cbMarkAsPurchased.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                handleCheckChangedForMarkAsPurchased(isChecked);
            }
        });

        ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddProductActivityToUpdate();
            }
        });

        ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertUserBeforeDeleting();
            }
        });
    }

    private void handleCheckChangedForMarkAsPurchased(boolean isChecked) {
        if (isChecked) {
            updateProductWithMarkAsPurchasedTrue();
        } else {
            updateProductWithMarkAsPurchasedFalse();
        }
    }

    private void updateProductWithMarkAsPurchasedTrue() {
        product.markAsPurchased = true;
        updateProductDataInDb(product);
    }

    private void updateProductWithMarkAsPurchasedFalse() {
        product.markAsPurchased = false;
        updateProductDataInDb(product);
    }

    private void updateProductDataInDb(Product product) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BabyBuyDatabase babyBuyDatabase = BabyBuyDatabase
                            .getInstance(getApplicationContext());
                    ProductDao productDao = babyBuyDatabase.getProductDao();
                    productDao.updateProduct(product);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }).start();
    }

    private void startAddProductActivityToUpdate() {
        Intent intent = new Intent(DetailPageActivity.this, AddProductActivity.class);
        intent.putExtra("product_data", product);
        startActivityForResult(intent, 1001);
    }

    private void assignViews() {
        ivDetailImage.post(new Runnable() {
            @Override
            public void run() {
                ivDetailImage.setImageBitmap(getBitmapForImageView(ivDetailImage, product.image));
            }
        });
        tvProductTitle.setText(product.title);
        tvProductPrice.setText("£ " + product.price);
        tvProductDescription.setText(product.description);
        cbMarkAsPurchased.setChecked(product.markAsPurchased);
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
                    view.getWidth(),
                    view.getHeight()
            );
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_gallery);
        }
        return bitmap;
    }

    private void updateMapLocation(Product product, GoogleMap googleMap) {
        LatLng latLng = new LatLng(
                Double.parseDouble(product.latitude),
                Double.parseDouble(product.longitude)
        );
        googleMap.addMarker(new MarkerOptions().position(latLng).title("Product Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private void alertUserBeforeDeleting() {
        AlertDialog alertDialog = new AlertDialog.Builder(DetailPageActivity.this)
                .setTitle("Delete Product")
                .setMessage("Are you sure want to delete this product data?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        deleteProductFromDataBase();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void deleteProductFromDataBase() {
        new Thread(() -> {
            try {
                BabyBuyDatabase babyBuyDatabase = BabyBuyDatabase
                        .getInstance(getApplicationContext());
                ProductDao productDao = babyBuyDatabase.getProductDao();
                productDao.deleteProduct(product);
                runOnUiThread(this::finishActivity);
            } catch (Exception exception) {
                exception.printStackTrace();
                runOnUiThread(() -> Toast.makeText(
                        DetailPageActivity.this,
                        "Error while deleting product...",
                        Toast.LENGTH_SHORT
                ).show());
            }
        }).start();
    }

    private void finishActivity() {
        setResult(RESULT_OK);
        finish();
    }
}