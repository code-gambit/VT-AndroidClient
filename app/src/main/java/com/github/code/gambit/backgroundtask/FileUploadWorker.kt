package com.github.code.gambit.backgroundtask

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.github.code.gambit.R
import com.github.code.gambit.data.entity.chache.FileCacheEntity
import com.github.code.gambit.data.entity.network.FileNetworkEntity
import com.github.code.gambit.data.local.Database
import com.github.code.gambit.data.mapper.cache.FileCacheMapper
import com.github.code.gambit.data.mapper.network.FileNetworkMapper
import com.github.code.gambit.data.model.FileMetaData
import com.github.code.gambit.data.remote.services.ApiService
import com.github.code.gambit.data.remote.services.file.FileService
import com.github.code.gambit.data.remote.services.file.FileServiceImpl
import com.github.code.gambit.ui.activity.main.MainActivity
import com.github.code.gambit.utility.AppConstant
import com.github.code.gambit.utility.sharedpreference.LastEvaluatedKeyManager
import com.github.code.gambit.utility.sharedpreference.UserManager
import com.google.gson.GsonBuilder
import io.ipfs.kotlin.defaults.InfuraIPFS
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File
import java.lang.Exception
import kotlin.math.pow

class FileUploadWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private val _db =
        Room.databaseBuilder(applicationContext, Database::class.java, AppConstant.Database.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    private val db get() = _db

    private val fileDao get() = db.fileDao()
    private val fileMetaDataDao get() = db.fileMetaDataDao()

    /**
     * @inputData filePath: String is the absolute file path
     * @inputData fileName: String name of the file
     * @inputData fileSize: Int size of the file in Mb
     *
     * @outputData data: Data toString of FileNetworkEntity
     */
    override suspend fun doWork(): Result {
        val fileMetaDataString = inputData.getString(AppConstant.Worker.FILE_META_DATA)!!
        val fmd = FileMetaData.fromString(fileMetaDataString)
        val uid = System.currentTimeMillis().toInt()
        val size = String.format("%.2f", fmd.size.div(10.0.pow(6.0)))
        setForeground(
            makeStatusNotification(
                uid,
                "New file",
                "Uploading file ${fmd.name} of size $size MB"
            )
        )
        /*val test = true
        if (test) {
            delay(60000)
            return Result.success(
                workDataOf(
                    AppConstant.Worker.FILE_OUTPUT_KEY to
                        FileNetworkEntity("", "", "test-hash", fmd.name, fmd.size, ".any").toString()
                )
            )
        }*/
        val file = File(fmd.path)
        val res = InfuraIPFS().add.file(file, fmd.name, fmd.name).Hash
        val fileService = getFileService()
        val fne = FileNetworkEntity("", "", res, fmd.name, fmd.size, fmd.name.split(".")[1])
        val task = fileService.uploadFile(fne)
        fileDao.insertFiles(getFileCacheEntityFromFileNetworkEntity(task))
        val data = workDataOf(AppConstant.Worker.FILE_OUTPUT_KEY to task.toString())
        try {
            fileMetaDataDao.deleteFileMetaData(id.toString())
            Timber.tag("worker").i("deleted file from db")
        } catch (err: Exception) {
            Timber.tag("worker").i(err.localizedMessage)
        }
        return Result.success(data)
    }

    private fun getFileCacheEntityFromFileNetworkEntity(fileNetworkEntity: FileNetworkEntity): FileCacheEntity {
        val networkMapper = FileNetworkMapper()
        val cacheMapper = FileCacheMapper()
        return cacheMapper.mapToEntity(networkMapper.mapFromEntity(fileNetworkEntity))
    }

    private fun getApiService(): ApiService {
        return Retrofit.Builder().baseUrl(AppConstant.BASE_URL)
            .addConverterFactory(
                GsonConverterFactory
                    .create(
                        GsonBuilder()
                            .excludeFieldsWithoutExposeAnnotation().create()
                    )
            ).build().create(ApiService::class.java)
    }

    private fun getFileService(): FileService {
        return FileServiceImpl(
            getApiService(),
            UserManager(applicationContext),
            LastEvaluatedKeyManager(applicationContext)
        )
    }

    /**
     * @param id Notification id
     * @param title notification title
     * @param message Notification message
     */
    private fun makeStatusNotification(id: Int, title: String, message: String): ForegroundInfo {
        val context = applicationContext
        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            createChannel()
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val cancelIntent =
            WorkManager.getInstance(applicationContext).createCancelPendingIntent(getId())
        // Create the notification
        val builder = NotificationCompat.Builder(context, AppConstant.Notification.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setProgress(0, 0, true)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, "Cancel Upload", cancelIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setVibrate(LongArray(0))

        // NotificationManagerCompat.from(applicationContext).notify(1, builder.build())

        return ForegroundInfo(id, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel() {
        val name = AppConstant.Notification.CHANNEL_NAME
        val description = AppConstant.Notification.CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(AppConstant.Notification.CHANNEL_ID, name, importance)
        channel.description = description

        // Add the channel
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }
}
