package np.com.yourname.babybuy.dashboard.recyclerview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import np.com.yourname.babybuy.R;

public class HomeRecyclerViewHolder
        extends RecyclerView.ViewHolder {
    private ImageView ivProductImage;
    private TextView tvProductTitle;
    private TextView tvProductDescription;
    private ConstraintLayout clProductRootLayout;

    public HomeRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        ivProductImage = itemView.findViewById(R.id.iv_item_image);
        tvProductTitle = itemView.findViewById(R.id.tv_item_title);
        tvProductDescription = itemView.findViewById(R.id.tv_item_description);
        clProductRootLayout = itemView.findViewById(R.id.cl_item_root);
    }

    public ImageView getIvProductImage() {
        return ivProductImage;
    }

    public TextView getTvProductTitle() {
        return tvProductTitle;
    }

    public TextView getTvProductDescription() {
        return tvProductDescription;
    }

    public ConstraintLayout getClProductRootLayout() {
        return clProductRootLayout;
    }
}
