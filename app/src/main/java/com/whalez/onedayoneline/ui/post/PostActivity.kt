package com.whalez.onedayoneline.ui.post

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.aminography.primecalendar.PrimeCalendar
import com.aminography.primecalendar.common.CalendarFactory
import com.aminography.primecalendar.common.CalendarType
import com.aminography.primedatepicker.PickType
import com.aminography.primedatepicker.fragment.PrimeDatePickerBottomSheet
import com.bumptech.glide.Glide
import com.example.indyproject2.UserSessionManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.whalez.onedayoneline.R
import kotlinx.android.synthetic.main.activity_post.*
import java.io.File
import java.util.*

class PostActivity : AppCompatActivity(), PrimeDatePickerBottomSheet.OnDayPickedListener {

    private val TAG = "kkk_PostActivity"

    //    var isStoragePermission = false
    private var imageFile: File? = null

    private lateinit var pickedDay: PrimeCalendar
    private lateinit var photoUri: Uri
    private lateinit var year: String
    private lateinit var month: String
    private lateinit var day: String
    private lateinit var weekday: String

    private val MESSAGE = "message"
    private val IMAGE_URL = "image_url"
    private val DATE = "date"
    private val TIME_STAMP = "timestamp"

    companion object {
        const val PICKER_TAG = "PrimeDatePickerBottomSheet"
        const val PICK_FROM_ALBUM = 1
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        val userSessionManager = UserSessionManager(this)
        val id = userSessionManager.userDetail["ID"]

        var datePicker: PrimeDatePickerBottomSheet
        val today = CalendarFactory.newInstance(CalendarType.CIVIL)
        year = today.year.toString()
        month = (today.month + 1).toString()
        day = today.month.toString()
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
            datePicker.show(supportFragmentManager,
                PICKER_TAG
            )
        }

        // 사진 불러오기 및 변경하기 버튼 클릭
        btn_load_img.setOnClickListener {
            goToAlbum()
        }

        // 올리기 버튼 클릭
        btn_post.setOnClickListener {
            val date = "" + year + '_' + month + '_' + day
            val imageRef: StorageReference = FirebaseStorage.getInstance().reference
                .child("${id}/${date}.jpg")
            val uploadTask = imageRef.putFile(photoUri)
            uploadTask.addOnFailureListener {
                Log.d(TAG, "파일 업로드 실패: ${it.message}")
            }.addOnSuccessListener {
                Log.d(TAG, "파일 업로드 성공")
                finish()
            }

            val postText = txt_post.text.toString()
            val dataToSave = mutableMapOf<String, Any>()
            uploadTask.continueWithTask<Uri> { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    dataToSave[MESSAGE] = postText
                    dataToSave[IMAGE_URL] = downloadUri.toString()
                    dataToSave[DATE] = tv_date.text.toString()
                    dataToSave[TIME_STAMP] = pickedDay.timeInMillis.toString()
                    val mDocRef = FirebaseFirestore.getInstance()
                        .document("users/${id}/posts/${pickedDay.timeInMillis}")
                    mDocRef.set(dataToSave).addOnSuccessListener {
                        Log.d(TAG, "InspiringQuote : Document has been saved!")
//                        mDocRef.collection("users/${id}").orderBy(TIME_STAMP, Query.Direction.DESCENDING)
                    }.addOnFailureListener { e ->
                        Log.d(TAG, "InspiringQuote : NO! - " + e.message)
                    }
                } else {
                    Log.d(TAG, "task fail: " + task.exception)
                }
            }
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
        startActivityForResult(intent,
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
//                isStoragePermission = true
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                Log.d(TAG, "저장소 접근 권한 거절")
//                isStoragePermission = false
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

}
