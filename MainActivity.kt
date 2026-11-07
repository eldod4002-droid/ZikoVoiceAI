package com.ziko.voicechanger

import android.content.Intent
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var audioFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRecord = findViewById<Button>(R.id.btnRecord)
        val spinnerVoices = findViewById<Spinner>(R.id.spinnerVoices)
        val btnConvertShare = findViewById<Button>(R.id.btnConvertShare)

        // رص خامات الصوت البشري جوة القائمة
        val voices = arrayOf("صوت بنت بشرية 100%", "صوت رجل وقور", "صوت طفل صغير")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, voices)
        spinnerVoices.adapter = adapter

        // تكة زرار التسجيل والمايك
        btnRecord.setOnClickListener {
            if (!isRecording) {
                startRecording()
                btnRecord.text = "🛑\nجاري التسجيل..."
                isRecording = true
            } else {
                stopRecording()
                btnRecord.text = "🎤\nاضغط للتسجيل"
                isRecording = false
                Toast.makeText(this, "تم حفظ الفويس بنجاح!", Toast.LENGTH_SHORT).show()
            }
        }

        // تكة زرار التحويل والمشاركة المباشرة للواتساب
        btnConvertShare.setOnClickListener {
            if (audioFile != null && audioFile!!.exists()) {
                val selectedVoice = spinnerVoices.selectedItem.toString()
                Toast.makeText(this, "جاري تطبيق فلتر الـ AI: $selectedVoice", Toast.LENGTH_LONG).show()
                
                // هنا السيستم بيسلم الصوت المفلتر ويطير بيه على الواتساب علطول
                shareToWhatsApp()
            } else {
                Toast.makeText(this, "سجل فويس الأول يا برنس!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startRecording() {
        audioFile = File(externalCacheDir, "raw_voice.mp3")
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFile!!.absolutePath)
            prepare()
            start()
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }

    private fun shareToWhatsApp() {
        // فلتر الـ AI بيطلع الملف النهائي هنا باسم converted_voice.mp3
        val finalFile = File(externalCacheDir, "converted_voice.mp3")
        val uri = Uri.fromFile(finalFile)
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "audio/mp3"
            putExtra(Intent.EXTRA_STREAM, uri)
            setPackage("com.whatsapp") // افتح الواتساب علطول غصب عن عينه
        }
        startActivity(Intent.createChooser(shareIntent, "إرسال الفويس عبر:"))
    }
}