package np.com.rishavchudal.myapplication.db.user;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import np.com.rishavchudal.myapplication.db.user.User;

@Dao
public interface UserDao {
    @Query("Select * from user_table")
    List<User> getAllUsers();

    @Insert
    void insertUser(User user);

    @Query("Select * from user_table where email_data = :email Limit 1")
    User getUserByEmail(String email);

    @Delete
    void deleteUser(User user);
}
