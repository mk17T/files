package sat.files

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val noFilesText = findViewById<TextView>(R.id.nofiles_textview)

        val path = intent.getStringExtra("path")
        val root = File(path)
        val filesAndFolders = root.listFiles()

        if (filesAndFolders == null || filesAndFolders.size == 0) {
            noFilesText.visibility = View.VISIBLE
            return
        }

        noFilesText.visibility = View.INVISIBLE

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MyAdapter(applicationContext, filesAndFolders)


    }
}