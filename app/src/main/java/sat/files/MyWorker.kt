package sat.files

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File
import java.io.FileWriter
import java.io.IOException

class MyWorker(context: Context, workerParameters: WorkerParameters): Worker(context,workerParameters) {

    override fun doWork(): Result {
        saveFiles()
        return Result.success()
    }

    private fun writeFilesAndFolders(directory: File, writer: FileWriter) {
        val children = directory.listFiles()
        if (children != null) {
            for (child in children) {
                if (child.isDirectory) {
                    writeFilesAndFolders(child, writer)
                    Thread.sleep(100)
                } else {
                    writer.append(child.path).append("\n")
                }
            }
        }
    }

    private fun saveFiles() {
        val path = Environment.getExternalStorageDirectory().path
        val root = File(path)
        val filesAndFolders = root.listFiles()
        val downloadDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString()
        val file = File(downloadDirectory, "files_and_folders.txt")
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            val writer = FileWriter(file)
            writeFilesAndFolders(root, writer)
            writer.flush()
            writer.close()

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }


}