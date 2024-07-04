package com.example.apptest.fragment

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dance.materialdialoglib.ConfirmDialogHelper
import com.example.apptest.R
import com.example.apptest.activities.Utils.Companion.encodeBitmap
import com.example.apptest.adapter.DownloadVideoAdapter
import com.example.apptest.cache.Cache
import com.example.apptest.cache.Encryption
import com.example.apptest.databinding.FragmentDownloadsBinding
import com.example.apptest.downloadBackground.TestService
import com.example.apptest.model.downloaded.VideoModel
import com.example.apptest.viewModel.download.DownloadViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DownloadsFragment : Fragment() {
    private val PERMISSION_REQUEST_CODE = 200
    lateinit var downloadList: RecyclerView
    lateinit var file: File
    lateinit var cacheFile: File
    var _down = MutableLiveData<Boolean>()
    val down: LiveData<Boolean>
        get() = _down
    lateinit var downloadViewModel: DownloadViewModel

    init {
        _down.value = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = FragmentDownloadsBinding.inflate(inflater)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
                .visibility = View.VISIBLE
        requireActivity().findViewById<View>(R.id.border)
                .visibility = View.VISIBLE
        requireActivity().registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        requireActivity().registerReceiver(onNotificationClick, IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED))
        downloadList = binding.downloadList
        downloadViewModel = ViewModelProvider(this).get(DownloadViewModel::class.java)
        cacheFile = File(requireContext().getExternalFilesDir(
                Environment.getDataDirectory().absolutePath), "Cache")
        file = File(requireContext().getExternalFilesDir(
                Environment.getDataDirectory().absolutePath), "DStudio")
        binding.lifecycleOwner = this

        Dexter.withContext(requireContext())
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                        Timber.d("permission check")
                        p0?.let {
                            if (p0.isAnyPermissionPermanentlyDenied) {
                                Timber.d("isAnyPermissionPermanentlyDenied")
                                binding.permissionRequired.root.visibility = View.VISIBLE
                                val permissionSetting = ConfirmDialogHelper(requireActivity())
                                permissionSetting.setNegativeClickListener {
                                    permissionSetting.dialog!!.dismiss()
                                }
                                permissionSetting.setPositiveClickListener {
                                    permissionSetting.dialog!!.dismiss()
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri: Uri = Uri.fromParts("package",
                                            requireActivity().packageName, null)
                                    intent.data = uri
                                    startActivity(intent)
                                }
                                permissionSetting.setNegativeClickListener {
                                    findNavController().navigate(DownloadsFragmentDirections.actionDownloadsFragmentToHomeFragment())
                                }
                                permissionSetting.create().show()
                                permissionSetting.setMessage(requireContext().getString(R.string.permission))
                                permissionSetting.setPositiveButtonText("OPEN SETTINGS")
                                permissionSetting.setNegativeButtonText("NOT NOW")

                                permissionSetting.setPositiveButtonTextColor(R.color.primaryLightColor)
                                permissionSetting.setNegativeButtonTextColor(R.color.primaryTextColor)
                                permissionSetting.setMessageTextColor(R.color.primaryTextColor)
                            }
                            if (p0.areAllPermissionsGranted()) {
                                Timber.d("areAllPermissionsGranted")
                                downloadViewModel.noDownFlag.observe(viewLifecycleOwner, {
                                    if (it) {
                                        binding.noDownload.root.visibility = View.GONE
                                    }
                                })
                                if (Cache().getObject(cacheFile).isNullOrEmpty()) {
                                    binding.noDownload.root.visibility = View.VISIBLE
                                } else {
                                    binding.noDownload.root.visibility = View.GONE
                                    downloadList.adapter = DownloadVideoAdapter(Cache().getObject(cacheFile)!!,
                                            DownloadVideoAdapter.OnclickListener {
                                                findNavController().navigate(DownloadsFragmentDirections.actionDownloadsFragmentToPlayDownloadVideoFragment(it!!))
                                            },
                                            DownloadVideoAdapter.DeleteVideo { path: String?, position: Int? ->
                                                deleteVideo(path!!, position!!)
                                            })
                                }
                            } else {
                                binding.permissionRequired.root.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?,
                                                                    p1: PermissionToken?) {
                        Timber.d("onPermissionRationaleShouldBeShown")
                        p1?.continuePermissionRequest()
                    }

                })
                .withErrorListener {
                    Timber.d(it.name)
                }
                .check()

        /*if (Permission().checkPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE))
            Timber.d("downloadsfragment check permission true")
        else {
            Timber.d("downloadsfragment request permission true check false")
            Permission().requestPermission(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), requireActivity())
        }*/
        /*if (!checkAllPermission()){
            Permission().requestPermission(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), requireActivity())
        }
        if (checkAllPermission()) {
            Timber.d("checkAllPermission() true")
            if (Cache().getObject(cacheFile).isNullOrEmpty()) {
                Toast.makeText(requireContext(), "no downloads", Toast.LENGTH_SHORT).show()
            } else {
                downloadList.adapter = DownloadVideoAdapter(Cache().getObject(cacheFile)!!,
                        DownloadVideoAdapter.OnclickListener {
                            this.findNavController().navigate(DownloadsFragmentDirections.actionDownloadsFragmentToPlayDownloadVideoFragment(it!!))
                        },
                        DownloadVideoAdapter.DeleteVideo { path: String?, position: Int? ->
                            deleteVideo(path!!, position!!)
                        })
            }
        } else {
            Timber.d("checkAllPermission() false")
            findNavController().navigate(DownloadsFragmentDirections.actionDownloadsFragmentToHomeFragment())
        }*/
        return binding.root
    }

    fun adapter(list: List<VideoModel>?) {
        if (list != null) {
            Cache().saveObject(list, cacheFile)
        }
        downloadList.adapter = DownloadVideoAdapter(Cache().getObject(cacheFile)!!,
                DownloadVideoAdapter.OnclickListener {
                    this.findNavController().navigate(DownloadsFragmentDirections.actionDownloadsFragmentToPlayDownloadVideoFragment(it!!))
                },
                DownloadVideoAdapter.DeleteVideo { path: String?, position: Int? ->
                    deleteVideo(path!!, position!!)
                })
    }


//    override fun onDestroy() {
//        super.onDestroy()
//        requireActivity().unregisterReceiver(onComplete)
//        requireActivity().unregisterReceiver(onNotificationClick)
//    }

    var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context?, intent: Intent?) {
            /*val shared = ctxt!!.getSharedPreferences("pref", Context.MODE_PRIVATE)
            with(shared.edit()) {
                putString("isDownloading", null)
                apply()
            }*/
            saveToCache()
            /*if (!shared.getBoolean("isRun", true)) {
                enc(ctxt)
            }*/
            downloadViewModel.setDownFlag()
            ctxt!!.stopService(Intent(ctxt, TestService::class.java))
        }
    }

    var onNotificationClick: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context?, intent: Intent?) {
        }
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @Throws(IOException::class)
    fun getvideos() {
        Timber.d("#getvideos#")
        var i = 0
        val mVideoList = arrayListOf<VideoModel>()
        while (i != file.listFiles().size) {
            val mVideo = VideoModel()
            mVideo.isSelected = (false)
            mVideo.filePath = file.listFiles()[i].absolutePath
            mVideo.videoName = file.listFiles()[i].name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val a = ThumbnailUtils.createVideoThumbnail(file.listFiles()[i], Size(100, 60), CancellationSignal())
                mVideo.videoThumb = encodeBitmap(a)
            } else {
                val a = ThumbnailUtils.createVideoThumbnail(file.listFiles()[i].absolutePath, MediaStore.Images.Thumbnails.MINI_KIND)
                mVideo.videoThumb = encodeBitmap(a!!)
            }
            mVideoList.add(mVideo)
            i += 1
        }
        adapter(mVideoList)
    }

    private fun deleteVideo(filePath: String, position: Int) {
        val deleteVideo = ConfirmDialogHelper(requireContext())
        deleteVideo.setNegativeClickListener {
            deleteVideo.dialog!!.dismiss()
        }
        deleteVideo.setPositiveClickListener {
            deleteVideo.dialog!!.dismiss()
            var i = 0
            while (i != file.listFiles().size) {
                if (file.listFiles()[i].name == filePath) {
                    file.listFiles()[i].delete()
                    i = file.listFiles().size
                } else
                    i += 1
            }
            val mVideoList = Cache().getObject(cacheFile) as ArrayList<VideoModel>
            mVideoList.removeAt(position)
            adapter(mVideoList)
        }
        deleteVideo.create().show()
        deleteVideo.setMessage("Delete from downloads?")
        deleteVideo.setPositiveButtonText("Yes")

        deleteVideo.setPositiveButtonTextColor(R.color.primaryLightColor)
        deleteVideo.setNegativeButtonTextColor(R.color.primaryTextColor)
        deleteVideo.setMessageTextColor(R.color.primaryTextColor)

        if (file.listFiles().isNullOrEmpty()) {
            requireActivity().findViewById<View>(R.id.no_download).visibility = View.VISIBLE
        }
    }

    @Throws(Exception::class)
    fun enc(ctx: Context) {
        val file = File(ctx.getExternalFilesDir(
                Environment.getDataDirectory().absolutePath), "DStudio")
        if (!file.listFiles().isNullOrEmpty()) {
            val i = file.listFiles().size - 1
            val temp = file.listFiles()[i].name.substring(0, file.listFiles()[i].name.length - 4)
            val file2 = File(ctx.getExternalFilesDir(
                    Environment.getDataDirectory().absolutePath + File.separator + "Cache2"),
                    temp)
            Encryption.enc(
                    "kdjfhAS5D4ASsa3d", "as1d2wS21DASfdg3",
                    FileInputStream(file.listFiles()[i]), FileOutputStream(file2)
            )
            file.listFiles()[i].delete()
        }
    }

    fun dec() {
        val cache2 = File(requireContext().getExternalFilesDir(
                Environment.getDataDirectory().absolutePath),
                "Cache2")
        if (!cache2.listFiles().isNullOrEmpty()) {
            var i = cache2.listFiles().size - 1
            while (i != -1) {
                val file = File(requireContext().getExternalFilesDir(
                        Environment.getDataDirectory().absolutePath),
                        "DStudio/${cache2.listFiles()[i].name}.mp4")
                Encryption.dec("kdjfhAS5D4ASsa3d", "as1d2wS21DASfdg3",
                        FileInputStream(cache2.listFiles()[i]), FileOutputStream(file))
                cache2.listFiles()[i].delete()
                i = cache2.listFiles().size - 1
            }
        }
    }

    /* private void encrypt() throws Exception {
            File file = new File(context.getExternalFilesDir(
                    Environment.getDataDirectory().getAbsolutePath()),
                    "DStudio");
            if (file.listFiles() != null) {
                int i = file.listFiles().length - 1;
                while (i != -1) {
                    String temp = file.listFiles()[i].getName().substring(0, file.listFiles()[i].getName().length() - 4);
                    File file2 = new File(context.getExternalFilesDir(
                            Environment.getDataDirectory().getAbsolutePath() + File.separator + "Cache2"),
                            temp);
                    Encryption.enc(
                            "kdjfhAS5D4ASsa3d", "as1d2wS21DASfdg3",
                            new FileInputStream(file.listFiles()[i]), new FileOutputStream(file2)
                    );
                    file.listFiles()[i].delete();
                    i = file.listFiles().length - 1;
                }
            }
        }

        private void decrypt() throws Exception {
            File cache2 = new File(context.getExternalFilesDir(
                    Environment.getDataDirectory().getAbsolutePath()),
                    "Cache2");
            if (cache2.listFiles() != null) {
                int i = cache2.listFiles().length - 1;
                while (i != -1) {
                    File file = new File(context.getExternalFilesDir(
                            Environment.getDataDirectory().getAbsolutePath()),
                            "DStudio/"+name+".mp4");
                    Encryption.dec("kdjfhAS5D4ASsa3d", "as1d2wS21DASfdg3",
                            new FileInputStream(cache2.listFiles()[i]), new FileOutputStream(file));
                    cache2.listFiles()[i].delete();
                    i = cache2.listFiles().length - 1;
                }
            }
        }*/
    @Throws(IOException::class)
    private fun saveToCache() {
        Timber.d("saveToCache() called")

        val sortedByDate: Array<File> = file.listFiles()!!
        if (sortedByDate.size > 1) {
            Arrays.sort(sortedByDate) { object1, object2 ->
                max(object1!!.lastModified(), object2!!.lastModified()).toInt()
            }
        }

        val list: ArrayList<VideoModel> = if (Cache().getObject(cacheFile).isNullOrEmpty()) {
            ArrayList()
        } else {
            Cache().getObject(cacheFile) as ArrayList<VideoModel>
        }
        val videoModel = VideoModel()
        val i = file.listFiles().size - 1
        videoModel.videoName = file.listFiles()[i].name
        videoModel.filePath = file.listFiles()[i].absolutePath

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val a = ThumbnailUtils.createVideoThumbnail(file.listFiles()[i], Size(100, 60),
                    CancellationSignal())
            videoModel.videoThumb = encodeBitmap(a)
        } else {
            val a = ThumbnailUtils.createVideoThumbnail(file.listFiles()[i].absolutePath, MediaStore.Images.Thumbnails.MINI_KIND)
            videoModel.videoThumb = encodeBitmap(a!!)
        }

        list.add(videoModel)
        adapter(list)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Timber.d("onRequestPermissionsResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        Timber.d("onstart")
        Dexter.withContext(requireContext())
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                        Timber.d("permission check")
                        p0?.let {
                            if (p0.areAllPermissionsGranted()) {
                                Timber.d("areAllPermissionsGranted")
                                requireActivity().findViewById<View>(R.id.permission_required).visibility = View.GONE
                                if (Cache().getObject(cacheFile).isNullOrEmpty()) {
                                    requireActivity().findViewById<View>(R.id.no_download).visibility = View.VISIBLE
                                } else {
                                    requireActivity().findViewById<View>(R.id.no_download).visibility = View.GONE
                                    downloadList.adapter = DownloadVideoAdapter(Cache().getObject(cacheFile)!!,
                                            DownloadVideoAdapter.OnclickListener {
                                                findNavController().navigate(DownloadsFragmentDirections.actionDownloadsFragmentToPlayDownloadVideoFragment(it!!))
                                            },
                                            DownloadVideoAdapter.DeleteVideo { path: String?, position: Int? ->
                                                deleteVideo(path!!, position!!)
                                            })
                                }
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?,
                                                                    p1: PermissionToken?) {
                        Timber.d("onPermissionRationaleShouldBeShown")
                        p1?.continuePermissionRequest()
                    }

                })
                .withErrorListener {
                    Timber.d(it.name)
                }
                .check()
        super.onStart()
    }
}
