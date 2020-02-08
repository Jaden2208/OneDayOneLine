package com.whalez.onedayoneline.ui.post

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import com.aminography.primecalendar.PrimeCalendar
import com.aminography.primecalendar.common.CalendarFactory
import com.aminography.primecalendar.common.CalendarType
import com.aminography.primedatepicker.PickType
import com.aminography.primedatepicker.fragment.PrimeDatePickerBottomSheet
import com.bumptech.glide.Glide
import com.whalez.onedayoneline.sharedpreference.UserSessionManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.whalez.onedayoneline.R
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_post.progressbar
import java.io.File
import java.util.*

class PostActivity : AppCompatActivity(), PrimeDatePickerBottomSheet.OnDayPickedListener {

    private val TAG = "kkk.PostActivity"

    //    var isStoragePermission = false
    private var imageFile: File? = null

    private lateinit var pickedDay: PrimeCalendar
    private var photoUri: Uri = Uri.EMPTY
    private lateinit var year: String
    private lateinit var month: String
    private lateinit var day: String
    private lateinit var weekday: String

    private val MESSAGE = "message"
    private val IMAGE_URL = "image_url"
    private val DATE = "date"
    private val TIME_STAMP = "timestamp"

    private val TXT_POST_LIMIT_LEN = 126

    companion object {
        const val PICKER_TAG = "PrimeDatePickerBottomSheet"
        const val PICK_FROM_ALBUM = 1
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        val userSessionManager = UserSessionManager(this)
        val id = userSessionManager.userDetail["ID"].toString()

        var datePicker: PrimeDatePickerBottomSheet
        val today = CalendarFactory.newInstance(CalendarType.CIVIL)
        year = today.year.toString()
        month = (today.month + 1).toString()
        day = today.dayOfMonth.toString()
        weekday = today.weekDayNameShort
        pickedDay = today

        tv_date.text = "${year}년 ${month}월 ${day}일 ${weekday}요일"

        callExternalStoragePermission()

        // back 버튼 클릭
        btn_back.setOnClickListener { finish() }

        // 날짜 변경 버튼 클릭
        btn_change_date.setOnClickListener {
            val pickType = PickType.SINGLE
            datePicker = PrimeDatePickerBottomSheet.newInstance(
                currentDateCalendar = today,
                pickType = pickType,
                pickedSingleDayCalendar = pickedDay // can be null
                // 기타 속성: https://github.com/aminography/PrimeDatePicker#usage
            )
            datePicker.setOnDateSetListener(this)
            datePicker.show(
                supportFragmentManager,
                PICKER_TAG
            )
        }

        // 사진 불러오기 및 변경하기 버튼 클릭
        btn_load_img.setOnClickListener {
            goToAlbum()
        }

        txt_post.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val wordCount = txt_post.length()
                limit_text_len.text = "(${wordCount} / ${TXT_POST_LIMIT_LEN})"
                if(wordCount == TXT_POST_LIMIT_LEN){
                    limit_text_len.setTextColor( ContextCompat.getColor(
                        applicationContext,
                        R.color.colorAccent
                    ))
                } else {
                    limit_text_len.setTextColor( ContextCompat.getColor(
                        applicationContext,
                        R.color.colorPrimaryLight
                    ))
                }
            }

        })

        // 올리기 버튼 클릭
        btn_post.setOnClickListener {
            Log.d(TAG, "버튼 눌림")
            if(thereIsEmptySpace()){
                Toast.makeText(this@PostActivity, "사진과 메시지를 모두 입력하세요.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            postBtnDisabled()
            ifThereIsNoDataInFireStoreThanUploadFile(id)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            photoUri = data.data!!
        }
        if (requestCode == PICK_FROM_ALBUM) {
            Log.d(TAG, "PICK_FROM_ALBUM")
            setImage(photoUri)
            btn_load_img.text = "사진 변경하기"
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onSingleDayPicked(singleDay: PrimeCalendar) {
        year = singleDay.year.toString()
        month = (singleDay.month + 1).toString()
        day = singleDay.dayOfMonth.toString()
        weekday = singleDay.weekDayNameShort
        pickedDay = singleDay
        tv_date.text = "${year}년 ${month}월 ${day}일 ${weekday}요일"
    }

    override fun onMultipleDaysPicked(multipleDays: List<PrimeCalendar>) {}
    override fun onRangeDaysPicked(startDay: PrimeCalendar, endDay: PrimeCalendar) {}

    private fun goToAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "image/*"
        )
        startActivityForResult(
            intent,
            PICK_FROM_ALBUM
        )
    }

    private fun setImage(photoUri: Uri) {
        Glide.with(this@PostActivity).load(photoUri).into(img_post)
        imageFile = null
    }

    private fun callExternalStoragePermission() {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Log.d(TAG, "저장소 접근 권한 주어짐")
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                Log.d(TAG, "저장소 접근 권한 거절")
            }
        }

        TedPermission.with(this@PostActivity)
            .setPermissionListener(permissionListener)
            .setRationaleMessage(resources.getString(R.string.permission_1))
            .setDeniedMessage(resources.getString(R.string.permission_2))
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .check()
    }

    private fun ifThereIsNoDataInFireStoreThanUploadFile(id: String){
        val collectionPath = "users/${id}/posts"
        val dateText = tv_date.text
        FirebaseFirestore.getInstance()
            .collection(collectionPath).whereEqualTo("date", dateText).get()
            .addOnSuccessListener {
                // 파일이 Storage에 존재하지 않으면 데이터 업로드
                if (it.documents.isEmpty()) {
                    val date = "" + year + '_' + month + '_' + day
                    val imageRef = FirebaseStorage.getInstance().reference
                        .child("${id}/${date}.jpg")
                    val uploadTask = imageRef.putFile(photoUri)
                    val dataToSave = mutableMapOf<String, Any>()
                    uploadTask.addOnFailureListener {
                        Log.d(TAG, "파일 업로드 실패: ${it.message}")
                    }.addOnSuccessListener {
                        Log.d(TAG, "파일 업로드 성공")
//                    }.addOnProgressListener {
//                        val progress = (100.0 * it.bytesTransferred / it.totalByteCount)
//                        progressbar.progress = progress.toInt()
                    }.continueWithTask<Uri> { task ->
                        if (!task.isSuccessful) {
                            throw task.exception!!
                        }
                        imageRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val imageUri = task.result
                            dataToSave[MESSAGE] = txt_post.text.toString()
                            dataToSave[IMAGE_URL] = imageUri.toString()
                            dataToSave[DATE] = dateText
                            dataToSave[TIME_STAMP] = pickedDay.timeInMillis.toString()
                            FirebaseFirestore.getInstance()
                                .collection(collectionPath)
                                .document(dataToSave[DATE].toString())
                                .set(dataToSave).addOnSuccessListener {
                                    Log.d(TAG, "InspiringQuote : Document has been saved!")
                                    finish()
                                }.addOnFailureListener { e ->
                                    Log.d(TAG, "InspiringQuote : NO! - " + e.message)
                                }
                        } else {
                            Log.d(TAG, "task fail: " + task.exception)
                        }
                    }
                }
                // 파일이 Storage에 이미 존재 하면,
                else {
                    val builder = AlertDialog.Builder(
                        ContextThemeWrapper(
                            this@PostActivity,
                            R.style.MyAlertDialogStyle
                        )
                    )
                    builder.setMessage("해당 날짜에 이미 기록되어있습니다!")
                        .setPositiveButton("확인") { _, _ ->
                            postBtnEnabled()
                        }
                        .show()
                }
            }
            .addOnFailureListener {
                Log.d(TAG, it.message.toString())
            }
    }

    private fun postBtnDisabled() {
        progressLayout.visibility = View.VISIBLE
        btn_back.isClickable = false
        btn_preview.isClickable = false
        btn_change_date.isClickable = false
        btn_load_img.isClickable = false
        txt_post.isClickable = false
        btn_post.isClickable = false
    }
    private fun postBtnEnabled() {
        btn_back.isClickable = true
        btn_preview.isClickable = true
        btn_change_date.isClickable = true
        btn_load_img.isClickable = true
        txt_post.isClickable = true
        btn_post.isClickable = true
        progressLayout.visibility = View.GONE
    }
    private fun thereIsEmptySpace(): Boolean {
        Log.d(TAG, "thereIsEmptySpace 진입")
        if (photoUri.toString() == "" || txt_post.text.toString() == "") {
            return true
        }
        return false
    }

}
