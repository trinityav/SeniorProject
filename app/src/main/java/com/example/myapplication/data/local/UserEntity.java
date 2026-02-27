package com.example.myapplication.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String username;
    public String email;
    public String password;
}