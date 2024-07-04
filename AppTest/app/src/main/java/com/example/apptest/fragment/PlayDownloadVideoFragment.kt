package com.example.apptest.fragment

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.apptest.cache.Encryption
import com.example.apptest.databinding.FragmentPlayDownloadVideoBinding
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class PlayDownloadVideoFragment : Fragment(){
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var player: SimpleExoPlayer? = null
    lateinit var filePath : String
    private lateinit var playbackStateListener: VideoFragment.PlaybackStateListener
    private lateinit var playerView: PlayerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = FragmentPlayDownloadVideoBinding.inflate(inflater)
        filePath = PlayDownloadVideoFragmentArgs.fromBundle(requireArguments()).filePath
        Timber.d(filePath)
        playbackStateListener = VideoFragment.PlaybackStateListener()
        playerView = binding.mPlayerView
        return binding.root
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
        val uri: Uri = Uri.parse(filePath)
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

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
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