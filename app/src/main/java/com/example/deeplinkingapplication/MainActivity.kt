package com.example.deeplinkingapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.deeplinkingapplication.databinding.ActivityMainBinding
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this , R.layout.activity_main)

        // to generate deep link
        generateDeepLink()

        // to get response from deep link
        recieveDeepLink()
    }

    private fun recieveDeepLink() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    val pm: PackageManager = applicationContext.packageManager
                    if (!isPackageInstalled("jp.hazuki.yuzubrowser", pm)) {
                        val intent = Intent(Intent.ACTION_VIEW).apply {

                            // code to redirect user to Google Play Store

                            data =
                                Uri.parse("https://play.google.com/store/apps/details?id=jp.hazuki.yuzubrowser")
                            setPackage("com.android.vending")
                        }
                        startActivity(intent)
                    } else {

                        // open the link in app

                        deepLink = pendingDynamicLinkData.link
                        val url = deepLink.toString()?.substring(20)
                        binding.webView.loadUrl(url.toString())
                        binding.webView.settings.javaScriptEnabled = true
                        Log.i("TAG", url.toString())
                    }

                }
            }
            .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }
    }

    private fun generateDeepLink() {
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {

    //            tab.url

            try {
                link = Uri.parse("https://example.com/https://www.appyhigh.com")
                domainUriPrefix = "https://mydeeplinkingprojectss.page.link"
                androidParameters() {}
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        val dynamicLinkUri = dynamicLink.uri // final deep link URI
        Log.i("TAG", dynamicLinkUri.toString())
    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}