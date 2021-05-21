package com.example.myactivity

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.details.*
import kotlinx.android.synthetic.main.dialog_update.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // KALENDER
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day   = c.get(Calendar.DAY_OF_MONTH)

        //TO SHOW DATEPICKER
        pickdate.setOnClickListener {
            closeKeyBoard()
            val date = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{ view, mYear, mMonth, mDay ->

                pickdate.setText(""+ mDay +"/"+ (mMonth + 1) +"/"+ mYear)
            }, year, month, day)

            date.show()
        }

        picktime.setOnClickListener {
            closeKeyBoard()
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY,hour)
                cal.set(Calendar.MINUTE,minute)

                val myFormat = "HH:mm"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                picktime.setText(sdf.format(cal.time))
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),true).show()
        }

//        picktime.setOnClickListener(object : View.OnClickListener{
//            override fun onClick(view: View){
//             closeKeyBoard()
//                TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), true).updateTime()
//            }
//        })

        setupListofDataIntoRecyclerView()
        btnAdd.setOnClickListener { view ->
            addRecord()
            setupListofDataIntoRecyclerView()
        }
        pickdate.inputType = InputType.TYPE_NULL
        picktime.inputType = InputType.TYPE_NULL
    }

    private fun addRecord() {
        val kegiatan = pickKegiatan.text.toString()
        val tanggal = pickdate.text.toString()
        val waktu = picktime.text.toString()
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        if (!kegiatan.isEmpty() && !tanggal.isEmpty() && !waktu.isEmpty()) {
            val status =
                databaseHandler.addKegiatan(EmpModelClass(0, kegiatan,tanggal, waktu))
            if (status > -1) {
                Toast.makeText(applicationContext, "Record Save", Toast.LENGTH_LONG).show()
                pickKegiatan.text.clear()
                pickdate.text.clear()
                picktime.text.clear()
                closeKeyBoard()
            }
        } else {
            Toast.makeText(applicationContext, "name or email cannot be blank", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun getItemsList(): ArrayList<EmpModelClass>{
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val empList: ArrayList<EmpModelClass> = databaseHandler.viewEmployee()

        return empList
    }

    private fun setupListofDataIntoRecyclerView() {
        if (getItemsList().size > 0) {
            rvItemList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE

            rvItemList.layoutManager = LinearLayoutManager(this)
            val itemAdapter = ItemAdapter(this, getItemsList())
            rvItemList.adapter = itemAdapter
        } else {
            rvItemList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }

    fun deleteRecordAlertDialog(empModelClass: EmpModelClass){
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Delete Record")

        builder.setMessage("Are you sure?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes"){ dialogInterface, which ->
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            val status = databaseHandler.deleteKegiatan(EmpModelClass(empModelClass.id, "", "",""))

            if (status > -1){
                Toast.makeText(applicationContext,"Record deleted successsfully",Toast.LENGTH_LONG).show()
                setupListofDataIntoRecyclerView()
            }
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun detailsRecordDialog(empModelClass: EmpModelClass){
        val details = Dialog(this, R.style.Theme_Dialog)

        details.setCancelable(false)
        details.setContentView(R.layout.details)

        details.detailKegiatan.setText(empModelClass.kegiatan)
        details.detailTanggal.setText(empModelClass.tanggal)
        details.detailWaktu.setText(empModelClass.waktu)

        details.iClose.setOnClickListener(View.OnClickListener {
            details.dismiss()
        })
        details.show()
    }

    fun updateRecordDialog(empModelClass: EmpModelClass){
        val updateDialog = Dialog(this, R.style.Theme_Dialog)

        updateDialog.setCancelable(false)
        updateDialog.setContentView(R.layout.dialog_update)

        updateDialog.etUpdateKegiatan.setText(empModelClass.kegiatan)
        updateDialog.etUpdateTanggal.setText(empModelClass.tanggal)
        updateDialog.etUpdateWaktu.setText(empModelClass.waktu)

        updateDialog.iUpdate.setOnClickListener(View.OnClickListener {
            val kegiatan = updateDialog.etUpdateKegiatan.text.toString()
            val tanggal  = updateDialog.etUpdateTanggal.text.toString()
            val waktu    = updateDialog.etUpdateWaktu.text.toString()

            val databaseHandler: DatabaseHandler = DatabaseHandler(this)

            if(!kegiatan.isEmpty() && !tanggal.isEmpty() && !waktu.isEmpty()){
                val status = databaseHandler.updateKegiatan(EmpModelClass(empModelClass.id,kegiatan,tanggal,waktu))
                if (status > -1){
                    Toast.makeText(applicationContext,"Record Updated", Toast.LENGTH_LONG).show()
                    setupListofDataIntoRecyclerView()
                    updateDialog.dismiss()
                    closeKeyBoard()
                }
            }else{
                Toast.makeText(applicationContext,"Name or Email cannot be blank", Toast.LENGTH_LONG).show()
            }

        })
        updateDialog.iCancel.setOnClickListener(View.OnClickListener {
            updateDialog.dismiss()
        })
        updateDialog.show()
    }

}


