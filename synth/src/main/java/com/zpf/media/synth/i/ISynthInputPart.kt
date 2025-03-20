package  com.zpf.media.synth.i

import com.zpf.media.synth.model.MediaInputBasicInfo

interface ISynthInputPart {
    fun getBasicInfo(): MediaInputBasicInfo
    fun getTrackEditor(trackId: String): ISynthTrackEditor?
}