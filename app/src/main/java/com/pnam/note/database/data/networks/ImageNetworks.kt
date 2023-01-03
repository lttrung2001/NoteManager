
@Singleton
interface ImageNetworks {
    fun deleteImage(noteId: String, url: String): Single<String>
}