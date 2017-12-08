package tashi.beta.myrtc

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException

class MainActivity : AppCompatActivity() {

    internal val uiHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView1.setInitialScale(160)
        webView1.webViewClient = MyWebViewClient()

        BackgroundWorker().execute()

        val refreshSite = View.OnClickListener { view -> webView1.reload()}
        refreshBtn.setOnClickListener(refreshSite)

    }

    @Suppress("OverridingDeprecatedMember")
    private inner class MyWebViewClient : WebViewClient(){

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            progressBar.visibility = View.GONE
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if(url.toString() == "https://my.rtc.bt/") {
                view?.loadUrl(url)
            }else{
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(i)
            }
            return false
        }

        @RequiresApi(21)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            if(request?.url.toString() == "https://my.rtc.bt/") {
                view?.loadUrl(request?.url.toString())
            }else{
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(request?.url.toString()))
                startActivity(i)
            }
            return false
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class BackgroundWorker: AsyncTask<Void, Void, Void>(){

        override fun doInBackground(vararg params: Void?): Void? {
            blogFeatured()
            return null
        }

        fun blogFeatured(){
            try {
                val htmlDocument: Document = Jsoup.connect("https://my.rtc.bt/").get()
                val element: Element? = htmlDocument.select("div.blog-featured").first()

                htmlDocument.head().append("<style>body { background-color: white!important;}\n"+
                        "h2 { font-size: 150%!important; }\n"+
                        "p { font-size: 20px!important; }\n"+
                        "</style>")
                htmlDocument.body().empty().append(element.toString())
                val html: String = htmlDocument.toString()

                uiHandler.post { webView1.loadData(html, "text/html", "UTF-8") }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}