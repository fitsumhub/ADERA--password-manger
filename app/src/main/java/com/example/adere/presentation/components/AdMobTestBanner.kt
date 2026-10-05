package com.example.adere.presentation.components

import android.util.Log
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.key
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.AdView

private const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
private const val TAG = "AdMobTestBanner"

@Composable
fun AdMobTestBanner(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
            context,
            maxWidth.value.toInt()
        )

        key(adSize) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(adSize.height.dp),
                factory = { viewContext ->
                    AdView(viewContext).apply {
                        setAdSize(adSize)
                        adUnitId = TEST_BANNER_AD_UNIT_ID
                        adListener = object : AdListener() {
                            override fun onAdFailedToLoad(error: LoadAdError) {
                                Log.w(TAG, "Test banner failed to load: ${error.message}")
                            }
                        }
                        loadAd(AdRequest.Builder().build())
                    }
                },
                onRelease = AdView::destroy
            )
        }
    }
}
