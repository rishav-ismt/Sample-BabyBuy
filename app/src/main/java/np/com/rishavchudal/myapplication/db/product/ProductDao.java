package np.com.rishavchudal.myapplication.db.product;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Created by Rishav Chudal on 02/01/2023.
 */
@Dao
public interface ProductDao {
    @Insert
    void insertProduct(Product product);

    @Update
    void updateProduct(Product product);

    @Delete
    void deleteProduct(Product product);

    @Query("Select * from product")
    List<Product> getAllProducts();

    @Query("Select * from product where mark_as_purchased = 'true'")
    List<Product> getPurchasedProducts();
}