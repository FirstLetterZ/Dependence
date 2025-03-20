package com.zpf.media.synth.model

import com.zpf.media.synth.i.ISynthInputPart
import com.zpf.media.synth.i.ISynthTrackEditor

data class MediaSynthPartInfo(
    val mediaInfo: MediaInputBasicInfo,
    val trackEditorList: List<ISynthTrackEditor>
) : ISynthInputPart {

    override fun getBasicInfo(): MediaInputBasicInfo {
        return mediaInfo
    }

    override fun getTrackEditor(trackId: String): ISynthTrackEditor? {
        return trackEditorList.find { it.trackId() == trackId }
    }

}