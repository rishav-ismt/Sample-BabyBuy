package np.com.rishavchudal.myapplication.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import np.com.rishavchudal.myapplication.R;
import np.com.rishavchudal.myapplication.db.BabyBuyDatabase;
import np.com.rishavchudal.myapplication.db.product.Product;
import np.com.rishavchudal.myapplication.db.product.ProductDao;
import np.com.rishavchudal.myapplication.utility.BitmapScalar;

public class AddProductActivity extends AppCompatActivity {

    private ImageButton ibBackButton;
    private ImageButton ibAddImage;
    private TextView tvLabelImage;
    private TextInputLayout textInputLayoutProductName;
    private TextInputEditText textInputEditTextProductName;
    private TextInputLayout textInputLayoutProductDescription;
    private TextInputEditText textInputEditTextProductDescription;
    private TextInputLayout textInputLayoutProductPrice;
    private TextInputEditText textInputEditTextProductPrice;
    private TextInputLayout textInputLayoutProductLocation;
    private TextInputEditText textInputEditTextProductLocation;
    private MaterialButton materialButtonAddProduct;

    private BottomSheetDialog pickImageBottomSheetDialog;

    public static final int RESULT_CODE_SUCCESS_ADD_PRODUCT_ACTIVITY = 1010;
    public static final int RESULT_CODE_FAILED_ADD_PRODUCT_ACTIVITY = 1011;

    private static final int REQUEST_CODE_CAMERA_ACTIVITY = 3001;
    private static final int REQUEST_CODE_GALLERY_ACTIVITY = 3002;
    private static final int REQUEST_CODE_MAP_ACTIVITY = 3003;

    private static final int GALLERY_PERMISSION_REQUEST_CODE = 11;
    private String imageUriPathToSave = "";
    private ProductLocation productLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        initViews();
        setViewsClicks();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA_ACTIVITY) {
            handleResultFromCameraActivity(resultCode, data);
        } else if (requestCode == REQUEST_CODE_GALLERY_ACTIVITY) {
            handleResultFromGalleryActivity(resultCode, data);
        } else if (requestCode == REQUEST_CODE_MAP_ACTIVITY) {
            handleResultFromMapActivity(resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            if (allPermissionForGalleryGranted()) {
                startActivityForResultFromGalleryToPickImage();
            } else {
                String message = "Please grant the required permissions for Gallery to open";
                Toast.makeText(this,
                        message,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        showPageExitAlertDialog();
    }

    private void initViews() {
        ibBackButton = findViewById(R.id.ib_back);
        ibAddImage = findViewById(R.id.ib_add_image);
        tvLabelImage = findViewById(R.id.tv_label_image);
        textInputLayoutProductName = findViewById(R.id.til_product_name);
        textInputEditTextProductName = findViewById(R.id.tiet_product_name);
        textInputLayoutProductDescription = findViewById(R.id.til_product_description);
        textInputEditTextProductDescription = findViewById(R.id.tiet_product_description);
        textInputLayoutProductPrice = findViewById(R.id.til_product_price);
        textInputEditTextProductPrice = findViewById(R.id.tiet_product_price);
        textInputLayoutProductLocation = findViewById(R.id.til_product_location);
        textInputEditTextProductLocation = findViewById(R.id.tiet_product_location);
        materialButtonAddProduct = findViewById(R.id.mb_add_product);
    }

    private void setViewsClicks() {
        ibBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPageExitAlertDialog();
            }
        });

        ibAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializePickImageBottomSheetDialog();
            }
        });

        textInputEditTextProductLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMapActivity();
            }
        });

        materialButtonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateProductData();
            }
        });
    }

    private void showPageExitAlertDialog() {
        AlertDialog exitAlertDialog = new AlertDialog.Builder(AddProductActivity.this)
                .setTitle("Exit")
                .setMessage("Are you sure want to exit? All your progress will be lost.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finishWithResult(RESULT_CODE_FAILED_ADD_PRODUCT_ACTIVITY);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        exitAlertDialog.show();
    }

    private void initializePickImageBottomSheetDialog() {
        pickImageBottomSheetDialog = new BottomSheetDialog(AddProductActivity.this);
        pickImageBottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_pick_image);

        LinearLayout linearLayoutPickByCamera = pickImageBottomSheetDialog
                .findViewById(R.id.ll_pick_by_camera);
        LinearLayout linearLayoutPickByGallery = pickImageBottomSheetDialog
                .findViewById(R.id.ll_pick_by_gallery);

        linearLayoutPickByCamera.setOnClickListener(
                view ->  {
                    pickImageBottomSheetDialog.dismiss();
                    startCameraActivity();
                });
        linearLayoutPickByGallery.setOnClickListener(
                view -> {
                    pickImageBottomSheetDialog.dismiss();
                    startGalleryToPickImage();
        });

        pickImageBottomSheetDialog.setCancelable(true);
        pickImageBottomSheetDialog.show();
    }

    private void startCameraActivity() {
        Intent intent = new Intent(AddProductActivity.this, CameraActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CAMERA_ACTIVITY);
    }

    private void startGalleryToPickImage() {
        if (allPermissionForGalleryGranted()) {
            startActivityForResultFromGalleryToPickImage();
        } else {
            requestPermissions(
                    getPermissionsRequiredForCamera().toArray(new String[0]),
                    GALLERY_PERMISSION_REQUEST_CODE
            );
        }
    }

    private boolean allPermissionForGalleryGranted() {
        boolean granted = false;
        for (String permission :
                getPermissionsRequiredForCamera()) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED) {
                granted = true;
            }
        }
        return granted;
    }

    private void startActivityForResultFromGalleryToPickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY_ACTIVITY);
    }

    private List<String> getPermissionsRequiredForCamera() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permissions;
    }

    private void startMapActivity() {
        Intent intent = new Intent(AddProductActivity.this, MapsActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MAP_ACTIVITY);
    }

    private void validateProductData() {
        String productName = textInputEditTextProductName.getText().toString().trim();
        String productDescription = textInputEditTextProductDescription.getText().toString().trim();
        String productPrice = textInputEditTextProductPrice.getText().toString().trim();

        if (productName.isEmpty()) {
            showToastWithMessage("Please enter a name for the product.");
        } else if (productDescription.isEmpty()) {
            showToastWithMessage("Please enter a description for the product.");
        } else if (productPrice.isEmpty()) {
            showToastWithMessage("Please enter a price for the product.");
        } else if (imageUriPathToSave.isEmpty()) {
            showToastWithMessage("Please select an image for the product.");
        } else if (productLocation == null) {
            showToastWithMessage("Please mark a valid location to buy the product.");
        } else {
            addProductToDb(
                    productName,
                    productDescription,
                    productPrice,
                    imageUriPathToSave,
                    productLocation
            );
        }
    }

    private void showToastWithMessage(String message) {
        Toast.makeText(
                AddProductActivity.this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    private void handleResultFromCameraActivity(int resultCode, Intent intentData) {
        if (resultCode == CameraActivity.CAMERA_ACTIVITY_SUCCESS_RESULT_CODE) {
            imageUriPathToSave = intentData.getStringExtra(
                    CameraActivity.CAMERA_ACTIVITY_OUTPUT_FILE_PATH);
            loadThumbnailImage();
        } else {
            imageUriPathToSave = "";
            ibAddImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void loadThumbnailImage() {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                    getContentResolver(),
                    Uri.parse(imageUriPathToSave)
            );
            bitmap = BitmapScalar.stretchToFill(
                    bitmap,
                    ibAddImage.getMeasuredWidth(),
                    ibAddImage.getMeasuredHeight()
            );
            ibAddImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleResultFromGalleryActivity(int resultCode, Intent intentData) {
        if (resultCode == Activity.RESULT_OK) {
            imageUriPathToSave = intentData.getData().toString();
            loadThumbnailImage();
        } else {
            imageUriPathToSave = "";
            ibAddImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void handleResultFromMapActivity(int resultCode, Intent intent) {
        if (resultCode == MapsActivity.MAPS_ACTIVITY_SUCCESS_RESULT_CODE) {
            productLocation = (ProductLocation) intent
                    .getSerializableExtra(MapsActivity.EXTRA_PRODUCT_LOCATION);
            String latLngData = "Lat : " +
                    productLocation.latitude +
                    ", Lng :" +
                    productLocation.longitude;
            textInputEditTextProductLocation.setText(latLngData);
        } else {
            productLocation = null;
            textInputEditTextProductLocation.setText("");
        }
    }

    private void addProductToDb(
            String title,
            String description,
            String price,
            String image,
            ProductLocation productLocation
    ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                insertProductInDb(title, description, price, image, productLocation);
            }
        }).start();
    }

    private void insertProductInDb(
            String title,
            String description,
            String price,
            String image,
            ProductLocation productLocation
    ) {
        try {
            BabyBuyDatabase babyBuyDatabase = BabyBuyDatabase.getInstance(getApplicationContext());
            ProductDao productDao = babyBuyDatabase.getProductDao();
            Product product = new Product();
            product.title = title;
            product.description = description;
            product.price = price;
            product.image = image;
            product.latitude = productLocation.latitude;
            product.longitude = productLocation.longitude;
            productDao.insertProduct(product);
            finishActivityInRunnableThread(RESULT_CODE_SUCCESS_ADD_PRODUCT_ACTIVITY);
        } catch (Exception exception) {
            exception.printStackTrace();
            finishActivityInRunnableThread(RESULT_CODE_FAILED_ADD_PRODUCT_ACTIVITY);
        }
    }

    private void finishActivityInRunnableThread(int resultCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finishWithResult(resultCode);
            }
        });
    }

    private void finishWithResult(int resultCode) {
        setResult(resultCode);
        finish();
    }

}