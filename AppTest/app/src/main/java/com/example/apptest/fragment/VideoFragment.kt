package com.example.apptest.fragment

//import com.example.apptest.downloadOffline.DownloadTracker
import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.dance.materialdialoglib.ConfirmDialogHelper
import com.example.apptest.R
import com.example.apptest.activities.Utils
import com.example.apptest.activities.Utils.Companion.name
import com.example.apptest.adapter.RecentVideoAdapter
import com.example.apptest.databinding.FragmentVideoBinding
import com.example.apptest.downloadBackground.TestService
import com.example.apptest.model.categoryHome.Video
import com.example.apptest.model.categorySeeAll.CatWiseVideoData
import com.example.apptest.model.recentVideo.RecentVideoData
import com.example.apptest.viewModel.video.VideoViewModel
import com.example.apptest.viewModel.video.VideoViewModelFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import timber.log.Timber
import java.io.File


class VideoFragment : Fragment()/*, MotionLayout.TransitionListener, DownloadTracker.Listener*/ {
    private var added: Boolean = false
    private lateinit var playerView: PlayerView
    private var islike: Boolean = false
    private var isDislike: Boolean = false
    private var isaddToList: Boolean = false
    private var likeCount: Int? = 0
    private var dislikeCount: Int = 0
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var player: SimpleExoPlayer? = null
    private lateinit var playbackStateListener: PlaybackStateListener
    private var mCurrentPosition = 0
    private val _time = "play_time"

    class PlaybackStateListener : Player.EventListener {
        /*private val progress = progressBar*/
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            val stateString: String
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> {
                    stateString = "ExoPlayer.STATE_IDLE      -"
                    //progress.visibility = VISIBLE
                }

                ExoPlayer.STATE_BUFFERING -> {
                    stateString = "ExoPlayer.STATE_BUFFERING -"
                    //progress.visibility = VISIBLE
                }

                ExoPlayer.STATE_READY -> {
                    stateString = "ExoPlayer.STATE_READY     -"
                    //progress.visibility = GONE
                }

                ExoPlayer.STATE_ENDED ->
                    stateString = "ExoPlayer.STATE_ENDED     -"

                else ->
                    stateString = "UNKNOWN_STATE             -"
            }
            Log.d("exoplayer state -> ", "changed state to " + stateString
                    + " playWhenReady: " + playWhenReady)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val file = File(requireContext().getExternalFilesDir(
                Environment.getDataDirectory().absolutePath), "DStudio")
        if (!file.mkdirs()) {
            Timber.e("Directory not created")
        }


        var (videoName, videoDesc, view, category) = arrayOf("null", "null", "null", "null")
        val binding = FragmentVideoBinding.inflate(inflater)

        /*val fullScreenButton = playerView.findViewById<ImageView>(R.id.exo_fullscreen_icon)
        fullScreenButton.setOnClickListener {
            val params: ConstraintLayout.LayoutParams = playerView.layoutParams as ConstraintLayout.LayoutParams
            if (fullScreen) {
                fullScreenButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.full_screen_open))
                requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE)
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                val scale = requireContext().resources.displayMetrics.density
                val pixels = (300 * scale + 0.5f).toInt()
                binding.topImageContainer.layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                binding.topImageContainer.layoutParams.height = pixels
                params.width = RelativeLayout.LayoutParams.MATCH_PARENT
                params.height = pixels
                playerView.layoutParams = params
                fullScreen = false
            } else {
                binding.topImageContainer.layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                binding.topImageContainer.layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT
                fullScreenButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.fullscreen_exit));
                requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                params.width = RelativeLayout.LayoutParams.MATCH_PARENT
                params.height = RelativeLayout.LayoutParams.MATCH_PARENT
                playerView.layoutParams = params;
                fullScreen = true
            }
        }*/

        playbackStateListener = PlaybackStateListener(/*binding.progressbar*/)
        binding.lifecycleOwner = this
        playerView = binding.videoPlayer

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.videoDesc.justificationMode = JUSTIFICATION_MODE_INTER_WORD
        }


        requireActivity().findViewById<AppBarLayout>(R.id.appbar).visibility = GONE
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = GONE
        requireActivity().findViewById<View>(R.id.border).visibility = GONE


        // Set up the media controller widget and attach it to the video view.
        if (savedInstanceState != null) mCurrentPosition = savedInstanceState.getInt(_time)
        val (application, video, data, catData) = arrayOf(requireNotNull(activity).application,
                VideoFragmentArgs.fromBundle(requireArguments()).video,
                VideoFragmentArgs.fromBundle(requireArguments()).data,
                VideoFragmentArgs.fromBundle(requireArguments()).catWiseData)

        val videoViewModelFactory = VideoViewModelFactory(video as Video?,
                data as RecentVideoData?,
                catData as CatWiseVideoData?,
                application as Application)
        val videoviewmodel = ViewModelProvider(this, videoViewModelFactory).get(VideoViewModel::class.java)
        binding.video = videoviewmodel


        videoviewmodel.isOffline.observe(viewLifecycleOwner, {
            if (it) {
                binding.offline.root.visibility = VISIBLE
                binding.videoFragment.visibility = GONE
                requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = VISIBLE
                requireActivity().findViewById<View>(R.id.border).visibility = VISIBLE
            } else {
                binding.offline.root.visibility = GONE
                binding.videoFragment.visibility = VISIBLE

                requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = GONE
                requireActivity().findViewById<View>(R.id.border).visibility = GONE
            }
        })
        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                super.onLost(network)
                videoviewmodel.isOffline()
            }

            override fun onAvailable(network: Network) {
                videoviewmodel.isOnline()
            }
        })
        videoviewmodel.status.observe(viewLifecycleOwner, {
            when (it!!) {
                com.example.apptest.viewModel.video.Status.LOADING -> {
                    binding.status.visibility = VISIBLE
                    binding.status.setImageResource(R.drawable.loading_main_animation)
                }
                com.example.apptest.viewModel.video.Status.DONE -> {
                    binding.status.visibility = GONE
                }
                com.example.apptest.viewModel.video.Status.ERROR -> {
                    binding.status.visibility = GONE
                    binding.offline.root.visibility = VISIBLE
                }
            }
        })

        videoviewmodel.videoName.observe(viewLifecycleOwner, {
            videoName = it
            name = it
            @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            if (file.listFiles().isNotEmpty()) {
                var i = 0
                while (i != file.listFiles().size) {
                    var fileVideoName = file.listFiles()[i].name
                    fileVideoName = fileVideoName.substring(0, fileVideoName.length - 4)
                    if (fileVideoName == videoName) {
                        binding.downloadVideo.setImageResource(R.drawable.no_download)
                        binding.downloadVideo.isClickable = false
                        i = file.listFiles().size
                    } else {
                        i += 1
                    }
                }
            }
        })

        videoviewmodel.videoDesc.observe(viewLifecycleOwner, {
            videoDesc = it
            Utils.desc = it
        })
        videoviewmodel.selectedData.observe(viewLifecycleOwner, {
            Utils.url = it
        })


        binding.like.setOnClickListener {
            binding.like.setColorFilter(ContextCompat.getColor(requireContext(), R.color.likeColor))
            likeCount = likeCount?.plus(1)
            binding.likeCount.text = (likeCount).toString()
            binding.like.isClickable = false
            binding.dislikeIcon.isClickable = false
            /*if (!islike) {
                binding.like.setColorFilter(ContextCompat.getColor(requireContext(), R.color.likeColor))
                likeCount = likeCount?.plus(1)
                binding.likeCount.text = (likeCount).toString()
                islike = true
                if (!isDislike) {
                    binding.dislikeIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.iconColor))
                    dislikeCount = dislikeCount.minus(1)
                    binding.dislikes.text = (dislikeCount).toString()
                }
            } else {
                binding.like.setColorFilter(ContextCompat.getColor(requireContext(), R.color.iconColor))
                likeCount = likeCount?.minus(1)
                binding.likeCount.text = (likeCount).toString()
                islike = false
            }
            videoviewmodel.isLikeChange(islike)*/
        }
        videoviewmodel.videoLike.observe(viewLifecycleOwner, {
            likeCount = it
            binding.likeCount.text = likeCount.toString()
        })
        videoviewmodel.isLike.observe(viewLifecycleOwner, {
            islike = it
            if (islike) {
                binding.like.setColorFilter(ContextCompat.getColor(requireContext(), R.color.likeColor))
            } else {
                binding.like.setColorFilter(ContextCompat.getColor(requireContext(), R.color.iconColor))
            }
        })

        binding.dislikeIcon.setOnClickListener {
            binding.dislikeIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.likeColor))
            dislikeCount = dislikeCount.plus(1)
            binding.dislikes.text = (dislikeCount).toString()
            binding.dislikeIcon.isClickable = false
            binding.like.isClickable = false
            /*if (isDislike) {
                binding.dislikeIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.likeColor))
                dislikeCount = dislikeCount.plus(1)
                binding.dislikes.text = (dislikeCount).toString()
                isDislike = false
                if (islike) {
                    binding.like.setColorFilter(ContextCompat.getColor(requireContext(), R.color.iconColor))
                    likeCount = likeCount?.minus(1)
                    binding.likeCount.text = (likeCount).toString()
                }
            } else {
                binding.dislikeIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.iconColor))
                dislikeCount = dislikeCount.minus(1)
                binding.dislikes.text = (dislikeCount).toString()
                isDislike = true
            }
            videoviewmodel.isLikeChange(isDislike)*/
        }
        videoviewmodel.dislikeCount.observe(viewLifecycleOwner, {
            dislikeCount = it
            binding.dislikes.text = dislikeCount.toString()
        })
        videoviewmodel.isDislike.observe(viewLifecycleOwner, {
            isDislike = it
            if (isDislike) {
                binding.dislikeIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.likeColor))
            } else {
                binding.like.setColorFilter(ContextCompat.getColor(requireContext(), R.color.iconColor))
            }
        })

        binding.addToList.setOnClickListener {
            if (!isaddToList) {
                binding.addToList.setColorFilter(ContextCompat.getColor(requireContext(), R.color.likeColor))
                videoviewmodel.addToList()
                isaddToList = true
            }
        }
        videoviewmodel.addToList.observe(viewLifecycleOwner, {
            if (!added) {
                Snackbar.make(requireActivity().findViewById(R.id.videoFragment), it, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.snack_black))
                        .setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryTextColor)).show()
                added = true
            }

        })
        videoviewmodel.isaddToList.observe(viewLifecycleOwner, {
            isaddToList = it
            if (isaddToList) {
                binding.addToList.setColorFilter(ContextCompat.getColor(requireContext(), R.color.likeColor))
            }
        })

        binding.downloadVideo.setOnClickListener {
            Dexter.withContext(requireContext())
                    .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                            p0?.let {
                                if (p0.areAllPermissionsGranted()) {
                                    binding.downloadVideo.isClickable = false
                                    binding.downloadVideo.setImageResource(R.drawable.no_download)
                                    val intent = Intent(requireContext(), TestService::class.java)
                                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        requireContext().startForegroundService(intent)
                                    } else {
                                        requireContext().startService(intent)
                                    }*/
                                    requireContext().startService(intent)
                                    val shared = requireContext().getSharedPreferences("pref",Context.MODE_PRIVATE)
                                            ?: return
                                    with(shared.edit()) {
                                        putString("isDownloading", "$name.mp4")
                                        apply()
                                    }
                                }
                                if (p0.isAnyPermissionPermanentlyDenied) {
                                    Timber.d("isAnyPermissionPermanentlyDenied")
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
                                    permissionSetting.create().show()
                                    permissionSetting.setMessage(requireContext().getString(R.string.permission))
                                    permissionSetting.setPositiveButtonText("OPEN SETTINGS")
                                    permissionSetting.setNegativeButtonText("NOT NOW")

                                    permissionSetting.setPositiveButtonTextColor(R.color.primaryLightColor)
                                    permissionSetting.setNegativeButtonTextColor(R.color.primaryTextColor)
                                    permissionSetting.setMessageTextColor(R.color.primaryTextColor)
                                }
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?,
                                                                        p1: PermissionToken?) {
                            p1?.continuePermissionRequest()
                        }

                    })
                    .withErrorListener {
                        Timber.d(it.name)
                    }
                    .check()
        }


        videoviewmodel.navigateToSelected.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                videoviewmodel.getVideoDetails()
                this.requireActivity().findNavController(this.id)
                        .navigate(VideoFragmentDirections.actionVideoFragmentSelf(null, it, null))
                videoviewmodel.navComplete()
            }
        })

        binding.recentVideoItems.adapter = RecentVideoAdapter(RecentVideoAdapter.OnclickListener {
            videoviewmodel.openVideo(it)
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerView.useController = true
        playerView.controllerShowTimeoutMs = 1500 * 3
    }

    private fun initializePlayer() {

        if (player == null) {
            val trackSelector = DefaultTrackSelector()
            trackSelector.setParameters(
                    trackSelector.buildUponParameters().setMaxVideoSizeSd()
            )
            player = ExoPlayerFactory.newSimpleInstance(requireContext(), trackSelector)
        }
        playerView.player = player
        val uri: Uri
        val video = VideoFragmentArgs.fromBundle(requireArguments()).video
        val data = VideoFragmentArgs.fromBundle(requireArguments()).data
        val catData = VideoFragmentArgs.fromBundle(requireArguments()).catWiseData

        uri = if (video == null && data == null) {
            Uri.parse(catData!!.VideoUrl)
        } else if (video == null && catData == null) {
            Uri.parse(data!!.VideoUrl)
        } else {
            Uri.parse(video!!.VideoUrl)
        }
        val mediaSource = buildMediaSource(uri)
        player?.playWhenReady = playWhenReady
        player?.seekTo(currentWindow, playbackPosition)
        player?.addListener(playbackStateListener)
        if (mediaSource != null) {
            player?.prepare(mediaSource, false, false)
        }

    }

    private fun buildMediaSource(uri: Uri): MediaSource? {
        val dataSourceFactory: DataSource.Factory =
                DefaultDataSourceFactory(context, "admin")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }


    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24 || player == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            playWhenReady = player!!.playWhenReady
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            player!!.removeListener(playbackStateListener)
            player!!.release()
            player = null
        }
    }

}
