package com.guido.app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guido.app.model.PlaceType
import com.guido.app.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User?)

    @Query("SELECT * FROM user_table WHERE id =:userId")
    suspend fun getUser(userId : String) : User

    @Query("UPDATE user_table SET profilePicture = :profilePic WHERE id = :userId")
    suspend fun updateProfilePic(userId: String,profilePic: String)


    @Query("SELECT * FROM user_table WHERE id =:userId")
    fun getUserFlow(userId : String) : Flow<User?>


    @Query("DELETE FROM user_table")
    fun deleteUser()

}