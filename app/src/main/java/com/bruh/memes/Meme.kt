package com.bruh.memes

import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.tv.TvContract.AUTHORITY
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.util.Log.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.bruh.memes.databinding.FragmentMemeBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import kotlinx.coroutines.NonCancellable.start
import java.io.*
import java.net.URI
import java.util.*

private var _binding: FragmentMemeBinding? = null
private val binding get() = _binding!!
private const val AppAUTHORITY = "${BuildConfig.APPLICATION_ID}.provider"

class Meme : Fragment(R.layout.fragment_meme) {

    var currentUrl: String? = null
    val url = "https://meme-api.herokuapp.com/gimme"
    val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMemeBinding.inflate(inflater, container, false)

        load()

        binding.shareBtn.setOnClickListener {
            share()
        }

        binding.nextBtn.setOnClickListener {
            next()
        }
        return binding.root
    }

    private fun load() {
        val cache = DiskBasedCache(requireContext().cacheDir, 1024 * 1024) // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())

        // Instantiate the RequestQueue with the cache and network. Start the queue.
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        binding.pbar.visibility = View.VISIBLE
        // Instantiate the RequestQueue.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                currentUrl = response.getString("url")

                Glide.with(requireContext()).load(currentUrl).listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.pbar.visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.pbar.visibility = View.GONE
                        return false
                    }
                }).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.memeImg)
            },
            Response.ErrorListener {
                Toast.makeText(requireContext(), "Internet Error", Toast.LENGTH_SHORT).show()
            })
        // Add the request to the RequestQueue.
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)
    }

    fun share() {

        val imageFolder = File(requireContext().cacheDir, "images")
        var uri: Uri? = null
        try {
            imageFolder.mkdirs()
            val file = File(imageFolder, "shared_image.png")
            val image = binding.memeImg.drawable.toBitmap()
            val fostream: FileOutputStream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 85, fostream)
            fostream.flush()
            fostream.close()
//          uri = FileProvider.getUriForFile(activity?.applicationContext!!, AUTHORITY, file)
            uri = FileProvider.getUriForFile(requireContext(), AppAUTHORITY, file)
        } catch (e: IOException) {
            Log.d(ContentValues.TAG, "IOException while trying to write file for sharing: ")
        }

        val intent = Intent(Intent.ACTION_SEND).setType("images/png")
        intent.putExtra(Intent.EXTRA_STREAM, uri)
//      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(intent, "Share Image"))
    }

    fun next() {
        load()
    }
}

//    fun getBitmapFromView(bmp: Bitmap?): Uri? {
//        var bmpUri: Uri? = null
//        try {
//            val file = File(requireContext().cacheDir, System.currentTimeMillis().toString() + ".jpg")
//            val out = FileOutputStream(file)
//            bmp?.compress(Bitmap.CompressFormat.JPEG, 90, out)
//            out.close()
//            bmpUri = Uri.fromFile(file)
//
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        return bmpUri
//        Log.d("UriPath", "$bmpUri")
//    }


//fun loadImageFromStorage(path: String): Bitmap? {
//    try {
//        val file = getFile(path)
//        val bitmap = BitmapFactory.decodeStream(FileInputStream(file))
//        return bitmap
//    } catch (e: Exception) {
//        e.printStackTrace()
//
//        //Returning file from public storage in case the file is stored in public storage
//        return BitmapFactory.decodeStream(FileInputStream(File(path)))
//    }
//
//    return null
//}


//fun getFile(path: String): File? {
//    val cw = ContextWrapper(this)
//    val directory = cw.getDir("image_dir", Context.MODE_PRIVATE)
//    if (!directory.exists())
//        directory.mkdir()
//    try {
//        val fileName = directory.absolutePath + "/" + path.split("/").last()
//        return File(fileName)
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//
//    return null
//}


// DUMP CODE



//        Picasso.get().load(url).into(object : Target {
//             fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
//                val intent = Intent(Intent.ACTION_SEND)
//                intent.type = "image/*"
//                intent.putExtra(Intent.EXTRA_STREAM, getBitmapFromView(bitmap))
//                startActivity(Intent.createChooser(intent, "Share Image"))
//            }
//            fun onPrepareLoad(placeHolderDrawable: Drawable?) { }
//            fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) { }
//        })

//        val intent = Intent(Intent.ACTION_SEND).setType("image/*")
//        val bitmapDrawable = binding.memeImg?.drawable.toBitmap()
//        val uri = getBitmapFromView(bitmapDrawable)
//        intent.putExtra(Intent.EXTRA_STREAM, uri)
//        startActivity(Intent.createChooser(intent, "Share Image"))

//        val cachePath = File(requireContext().externalCacheDir, "my_images/")
//        cachePath.mkdirs()
//        val bitmap = binding.memeImg.drawable.toBitmap()
//        val file = File(cachePath, "cache.png")
//        val fileOutputStream: FileOutputStream
//        try {
//            fileOutputStream = FileOutputStream(file)
//            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
//            fileOutputStream.flush()
//            fileOutputStream.close()
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        val cacheImageUri: Uri = FileProvider.getUriForFile(requireContext(), AUTHORITY , file)
//
//        val intent = Intent(Intent.ACTION_SEND).apply {
//            clipData = ClipData.newRawUri(null, cacheImageUri)
//            putExtra(Intent.EXTRA_STREAM, cacheImageUri)
//            type = "image/ *"
//            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        }
//        startActivity(Intent.createChooser(intent, null))