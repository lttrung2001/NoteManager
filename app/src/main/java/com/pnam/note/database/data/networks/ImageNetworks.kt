import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ImageNetworks {
    fun deleteImage(noteId: String, url: String): Single<String>
}