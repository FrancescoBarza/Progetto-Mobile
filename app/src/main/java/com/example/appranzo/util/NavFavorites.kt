package com.example.appranzo.util

import android.content.Context
import android.content.Intent
import com.example.appranzo.PlaceDetailActivity
import com.example.appranzo.data.models.Place

fun navigateToPlaceDetail(ctx: Context, place: Place) {
    val intent = Intent(ctx, PlaceDetailActivity::class.java)
    intent.putExtra("EXTRA_PRODUCT_ID", place.id)
    ctx.startActivity(intent)
}
