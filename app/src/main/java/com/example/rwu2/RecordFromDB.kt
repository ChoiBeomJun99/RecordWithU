package com.example.rwu2

data class RecordFromDB(
    val title:String,
    val imgURL: String,
    val lat:Double, val lng:Double,
    val text:String,
    val date: String
    ){
    constructor() : this("","",0.0,0.0,"","")
}
