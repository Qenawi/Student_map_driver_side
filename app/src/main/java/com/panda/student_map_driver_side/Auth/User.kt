package com.panda.student_map_driver_side.Auth

import com.google.gson.annotations.SerializedName

 class User(@SerializedName("name") var name: String?="",
                @SerializedName("mail") var mail: String?="",
                @SerializedName("password") var password: String?="",
                @SerializedName("driverLicense") var driverLicense: String?="",
                @SerializedName("location") var location: String?="",
                @SerializedName("phone") var phone: String?="",
            @SerializedName("driverID") var driverID: String?=""



 )
 {
     constructor():this("drivername","drivermail","password","driverlicense","location","mPhone")
 }
