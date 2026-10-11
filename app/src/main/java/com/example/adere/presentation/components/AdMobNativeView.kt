package com.example.adere.presentation.components

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.adere.core.ads.AdMobManager
import com.example.adere.core.ads.AdSecurityPolicy
import com.example.adere.core.ads.AdSecurityZone
import com.example.ui.theme.Shapes
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

private const val TAG = "AdMobNativeView"

/**
 * Material 3 styled Jetpack Compose Native Advanced Ad component.
 *
 * Displays native ads cleanly with required "ADVERTISEMENT" badge, headline, body,
 * icon, and call-to-action button.
 * Enforces [AdSecurityPolicy] and automatically cleans up memory on unmount.
 */
@Composable
fun AdMobNativeView(
    modifier: Modifier = Modifier,
    zone: AdSecurityZone = AdSecurityZone.SAFE_ZONE
) {
    if (!AdSecurityPolicy.isAdAllowed(zone)) {
        return
    }

    val context = LocalContext.current
    var loadedNativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var isAdFailed by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val manager = AdMobManager.getInstance(context)
        manager.loadNativeAd(
            context = context,
            zone = zone,
            onNativeAdLoaded = { ad ->
                loadedNativeAd = ad
                isAdFailed = false
            },
            onAdFailedToLoad = { err ->
                Log.w(TAG, "Native ad failed to load: ${err.message}")
                isAdFailed = true
            }
        )

        onDispose {
            loadedNativeAd?.destroy()
            loadedNativeAd = null
        }
    }

    val nativeAd = loadedNativeAd
    if (isAdFailed || nativeAd == null) {
        return
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)
        ) {
            // Header Ad Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "ADVERTISEMENT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                nativeAd.advertiser?.let { advertiser ->
                    Text(
                        text = "· $advertiser",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // AndroidView embedding NativeAdView
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { viewContext ->
                    createNativeAdView(viewContext, nativeAd)
                },
                update = { nativeView ->
                    populateNativeAdView(nativeAd, nativeView)
                }
            )
        }
    }
}

private fun createNativeAdView(context: Context, nativeAd: NativeAd): NativeAdView {
    val nativeAdView = NativeAdView(context)
    val layout = ColumnLayout(context)
    nativeAdView.addView(layout)
    populateNativeAdView(nativeAd, nativeAdView)
    return nativeAdView
}

private fun ColumnLayout(context: Context): android.widget.LinearLayout {
    return android.widget.LinearLayout(context).apply {
        orientation = android.widget.LinearLayout.VERTICAL
        layoutParams = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        )
    }
}

private fun populateNativeAdView(nativeAd: NativeAd, nativeAdView: NativeAdView) {
    val container = nativeAdView.getChildAt(0) as? android.widget.LinearLayout ?: return
    container.removeAllViews()

    val context = nativeAdView.context

    // Row: Icon + Headline & Body
    val contentRow = android.widget.LinearLayout(context).apply {
        orientation = android.widget.LinearLayout.HORIZONTAL
        gravity = android.view.Gravity.CENTER_VERTICAL
    }

    nativeAd.icon?.drawable?.let { iconDrawable ->
        val iconView = ImageView(context).apply {
            setImageDrawable(iconDrawable)
            layoutParams = android.widget.LinearLayout.LayoutParams(120, 120).apply {
                rightMargin = 24
            }
        }
        nativeAdView.iconView = iconView
        contentRow.addView(iconView)
    }

    val textCol = android.widget.LinearLayout(context).apply {
        orientation = android.widget.LinearLayout.VERTICAL
        layoutParams = android.widget.LinearLayout.LayoutParams(
            0,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )
    }

    nativeAd.headline?.let { headline ->
        val headlineView = TextView(context).apply {
            text = headline
            textSize = 15f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(0xFF000000.toInt())
        }
        nativeAdView.headlineView = headlineView
        textCol.addView(headlineView)
    }

    nativeAd.body?.let { body ->
        val bodyView = TextView(context).apply {
            text = body
            textSize = 12f
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END
            setTextColor(0xFF666666.toInt())
        }
        nativeAdView.bodyView = bodyView
        textCol.addView(bodyView)
    }

    contentRow.addView(textCol)
    container.addView(contentRow)

    // Call to Action Button
    nativeAd.callToAction?.let { ctaText ->
        val ctaButton = Button(context).apply {
            text = ctaText
            textSize = 13f
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
            }
        }
        nativeAdView.callToActionView = ctaButton
        container.addView(ctaButton)
    }

    nativeAdView.setNativeAd(nativeAd)
}
