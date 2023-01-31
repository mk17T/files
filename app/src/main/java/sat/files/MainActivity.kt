package sat.files

import android.Manifest
import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.android.material.button.MaterialButton
import sat.files.databinding.ActivityMainBinding
import java.io.File
import java.io.FileWriter
import java.io.IOException

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //WorkManager.initialize(this, Configuration.Builder().build())

        val storageBtn = findViewById<MaterialButton>(R.id.storage_btn)

        storageBtn.setOnClickListener { //
            val intent = Intent(this@MainActivity, FileListActivity::class.java)
            val path = Environment.getExternalStorageDirectory().path
            intent.putExtra("path", path)
            startActivity(intent)
        }

        val saveBtn = findViewById<Button>(R.id.btn_save)
        saveBtn.setOnClickListener { //saveFiles()
        myWork()
        }

    }

    private fun myWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            //.setRequiresDeviceIdle(true)
            .build()

        val myWorkRequest:WorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueue(myWorkRequest)
        Toast.makeText(applicationContext,"dowork",Toast.LENGTH_SHORT).show()
    }


    override fun onStart() {
        super.onStart()
        if (checkPermission()) {
            //permission allowed
            Toast.makeText(applicationContext, "granted", Toast.LENGTH_SHORT).show()
        } else {
            //permission not allowed
            requestPermission()
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return (result == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this@MainActivity,
                "Storage permission is requires,please allow from settings",
                Toast.LENGTH_SHORT
            ).show()
        } else ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            111
        )
    }

    @Throws(IOException::class, InterruptedException::class)
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


