package com.bruh.memes

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.tv.TvContract.AUTHORITY
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bruh.memes.databinding.FragmentMemeBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private var _binding: FragmentMemeBinding? = null
private val binding get() = _binding!!

class Meme : Fragment(R.layout.fragment_meme) {

    var currentUrl: String? = null
    val url = "https://meme-api.herokuapp.com/gimme"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMemeBinding.inflate(inflater, container, false)

        load()

        binding.shareBtn.setOnClickListener {
            share()
        }
        return binding.root
    }

    private fun load() {

        binding.pbar.visibility = View.VISIBLE
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(requireContext())
        // We cannot use this as context because this is not an activity, so we call requireContext()

        // Request a string response from the provided URL.
        val stringRequest = JsonObjectRequest(
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
                }).into(binding.memeImg)
            },
            Response.ErrorListener {
                Toast.makeText(requireContext(), "Internet Error", Toast.LENGTH_SHORT).show()
            })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun next() {

    }

    fun share() {

//        val intent = Intent(Intent.ACTION_SEND).setType("image/*")
//        val bitmapDrawable = binding.memeImg.drawable.toBitmap()
//        val path = MediaStore.Images.Media.insertImage(
//                requireContext().contentResolver,
//                bitmapDrawable,
//                "tempimage",
//                "image"
//            )
//        val uri = Uri.parse(path)
//        intent.putExtra(Intent.EXTRA_STREAM, uri)
//        startActivity(Intent.createChooser(intent, "Share Image"))


//        val imageFolder = File(requireContext().externalCacheDir, "images")
//        var uri: Uri? = null
//        try {
//            imageFolder.mkdirs()
//            val file = File(imageFolder, "shared_image.png")
//            val image = binding.memeImg.drawable.toBitmap()
//            val stream: FileOutputStream = FileOutputStream(file)
//            image.compress(Bitmap.CompressFormat.PNG, 85, stream)
//            stream.flush()
//            stream.close()
//            uri = FileProvider.getUriForFile(requireContext(), AUTHORITY, file)
//        } catch (e: IOException) {
//            Log.d(ContentValues.TAG, "IOException while trying to write file for sharing: ")
//        }
//
//        val intent = Intent(Intent.ACTION_SEND).setType("images/png")
//        intent.putExtra(Intent.EXTRA_STREAM, uri)
//        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        startActivity(Intent.createChooser(intent, "Share Image"))
    }
}