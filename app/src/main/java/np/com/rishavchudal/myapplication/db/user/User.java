package np.com.rishavchudal.myapplication.db.user;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "email_data")
    public String email;

    @ColumnInfo(name = "password_data")
    public String password;
}
