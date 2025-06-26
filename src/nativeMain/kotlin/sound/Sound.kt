@file:OptIn(ExperimentalForeignApi::class)

package sound

import cnames.structs.SDL_AudioStream
import kotlinx.cinterop.*
import sdl.*
import util.sdlError
import kotlin.concurrent.AtomicReference

class Sound(filename: String) {
    companion object {
        const val RESOURCES_PATH = "src/nativeMain/resources/"
        val playerHurt: Sound = Sound("playerhurt.wav")
        val playerDeath: Sound = Sound("death.wav")
        val monsterHurt: Sound =
            Sound("monsterhurt.wav")
        val test: Sound = Sound("test.wav")
        val pickup: Sound = Sound("pickup.wav")
        val bossDeath: Sound = Sound("bossdeath.wav")
        val craft: Sound = Sound("craft.wav")

        fun cleanupAll() {
            playerHurt.cleanup()
            playerDeath.cleanup()
            monsterHurt.cleanup()
            test.cleanup()
            pickup.cleanup()
            bossDeath.cleanup()
            craft.cleanup()
        }
    }

    val path = RESOURCES_PATH + filename
    val audioData: AtomicReference<CPointer<UByteVar>?> = AtomicReference(null)
    val audioLength: AtomicReference<UInt> = AtomicReference(0u)
    val audioSpec: AtomicReference<CPointer<SDL_AudioSpec>?> = AtomicReference(null)
    val audioStream: AtomicReference<CPointer<SDL_AudioStream>?> = AtomicReference(null)

    init {
        loadAudio()
    }
}

fun Sound.loadAudio() = memScoped {
    val spec = alloc<SDL_AudioSpec>()
    val buffer = alloc<CPointerVar<UByteVar>>()
    val length = alloc<UIntVar>()

    if (SDL_LoadWAV(path, spec.ptr, buffer.ptr, length.ptr)) {
        audioData.value = buffer.value
        audioLength.value = length.value
        audioSpec.value = spec.ptr

        // Criar stream de áudio para SDL3
        val stream = SDL_OpenAudioDeviceStream(
            SDL_AUDIO_DEVICE_DEFAULT_PLAYBACK, spec.ptr, null, null
        )
        audioStream.value = stream
    } else {
        println("Failed to load audio file: $path - ${sdlError()}")
    }
}

fun Sound.play() {
    val data = audioData.value
    val length = audioLength.value
    val stream = audioStream.value

    if (data != null && stream != null && length > 0u) {
        SDL_PutAudioStreamData(stream, data, length.toInt())
        SDL_ResumeAudioDevice(SDL_GetAudioStreamDevice(stream))
    }
    println("Playing sound: $path")
}

fun Sound.cleanup() {
    audioStream.value?.let { SDL_DestroyAudioStream(it) }
    audioData.value?.let { SDL_free(it) }
    audioSpec.value?.let { nativeHeap.free(it) }
}
