package com.zpf.aaa

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Tracks
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride
import com.zpf.aaa.databinding.ActivityTest3Binding


class Test3Activity : AppCompatActivity() {
    private val binding: ActivityTest3Binding by lazy {
        ActivityTest3Binding.inflate(layoutInflater, null, false)
    }
    private val mediaUrl =
        "https://cdn.tiantiantiaosheng.com/dataupload/tool/2025-04-02/a3f4451a-eb00-4712-ac4a-f6d38fcfeac9/3e7ffec8-93b2-4e28-bf95-f6cd5f08b4be.mp4"
    private val trackSelector by lazy { DefaultTrackSelector(application) }

    private val player: ExoPlayer by lazy {
        val realPlayer = ExoPlayer.Builder(this).setTrackSelector(trackSelector).build()
        realPlayer.playWhenReady = true
        realPlayer.addListener(object : Player.Listener {
            override fun onIsLoadingChanged(isLoading: Boolean) {
                super.onIsLoadingChanged(isLoading)
                if (!isLoading) {
                    player.currentTracks
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    Log.e(
                        "ZPF",
                        "onPlaybackStateChanged==>Player.STATE_READY"
                    )
                }
            }

            override fun onPlayerError(error: PlaybackException) {

            }

            override fun onTracksChanged(tracks: Tracks) {
                super.onTracksChanged(tracks)
                var n = 0
                for (trackGroup in tracks.groups) {
                    // Group level information.
                    val trackType = trackGroup.type
                    if (trackType != C.TRACK_TYPE_AUDIO) {
                        continue
                    }
                    val trackInGroupIsSelected = trackGroup.isSelected
                    val trackInGroupIsSupported = trackGroup.isSupported
                    Log.e(
                        "ZPF",
                        "onTracksChanged==>i=$n;isSupported=$trackInGroupIsSupported;isSelected=$trackInGroupIsSelected;trackType=$trackType"
                    )
                    n++
                    for (i in 0 until trackGroup.length) {
                        // Individual track information.
                        val isSupported = trackGroup.isTrackSupported(i)
                        val isSelected = trackGroup.isTrackSelected(i)
                        val trackFormat = trackGroup.getTrackFormat(i)
                        Log.e(
                            "ZPF",
                            "trackGroup==>i=$i;isSupported=$isSupported;isSelected=$isSelected;trackFormat=$trackFormat"
                        )
                    }
                }
            }
        })
        realPlayer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val albumLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val uri = it.data?.data

                }
            }
//        val pick = Runnable {
//            val permissions = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//                emptyArray()
//            } else {
//                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
//            }
//            PermissionManager.get().checkPermission(this, permissions, PermissionGrantedListener {
//                val albumIntent = Intent(Intent.ACTION_PICK)
//                albumIntent.setDataAndType(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*"
//                )
//                albumLauncher.launch(albumIntent)
//            })
//        }
        binding.run {
            btn01.setOnClickListener {
//                pick.run()
//                changeAudioTrack(0)
                switchAudioTrack("en")
            }
            btn02.setOnClickListener {
//                trackSelector.
//                player.trackSelectionParameters =
//                changeAudioTrack(1)
                switchAudioTrack("und")
            }
            playerView.player = player
        }
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setAllowVideoMixedMimeTypeAdaptiveness(true)
        )
        player.setMediaItem(MediaItem.fromUri(mediaUrl))
        player.playWhenReady = true
        player.prepare()
    }

    override fun onStart() {
        super.onStart()
        binding.playerView.player?.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        binding.playerView.player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.playerView.player?.release()
    }

    private fun changeAudioTrack(index: Int) {
        if (index < 0) {
            return
        }
        val trackInfo = trackSelector.currentMappedTrackInfo ?: return
        if (trackInfo.rendererCount < 1) {
            return
        }
        var audioGroupArray: TrackGroupArray? = null
        for (i in 0 until trackInfo.rendererCount) {
            val type = trackInfo.getRendererType(i)
            if (type == C.TRACK_TYPE_AUDIO) {
                audioGroupArray = trackInfo.getTrackGroups(i)
                break
            }
        }
        if (audioGroupArray == null || index >= audioGroupArray.length) {
            return
        }
        val targetGroup = audioGroupArray.get(index)
        val override = TrackSelectionOverride(targetGroup, 0)
        val params = player.trackSelectionParameters
            .buildUpon()
            .setOverrideForType(override)
            .build()
        player.trackSelectionParameters = params
    }

    private fun switchAudioTrack(language: String) {
        val parametersBuilder = trackSelector.buildUponParameters()
        parametersBuilder.setPreferredAudioLanguage(language)
        trackSelector.setParameters(parametersBuilder)
    }
}