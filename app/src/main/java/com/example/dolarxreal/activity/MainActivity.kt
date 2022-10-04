package com.example.dolarxreal.activity

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.dolarxreal.API.Endpoint
import com.example.dolarxreal.Constants.Constants
import com.example.dolarxreal.R.drawable.*
import com.example.dolarxreal.data.SharedPreferences
import com.example.dolarxreal.databinding.ActivityMainBinding
import com.example.retrofitapi.util.NetworkUtils
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode



class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var bind : ActivityMainBinding
    private lateinit var progressBar: ProgressBar
    private val coinList = arrayListOf("Dolar X Real","Real x Dolar")
    private lateinit var mShared : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        resourceVisibility(false)

        mShared = SharedPreferences(this)

        bind.btnConverter.setOnClickListener(this)
        progressBar = bind.progress
        setAdapter()

    }


    override fun onClick(p0: View?) {
        when(p0?.id)
        {
            bind.btnConverter.id-> if(validateField()) bind.edittextValue.error = "Informe o valor" else getCurrencies()
        }
    }
    private fun format(value:Float) : Float{
        return value.toBigDecimal().setScale(2,RoundingMode.UP).toFloat()
    }



    private fun resourceVisibility(isVisible: Boolean)
    {
        if (isVisible)
        {
            bind.textDolarVariant.visibility = View.VISIBLE
            bind.imgDolarVariant.visibility = View.VISIBLE
        }else
        {
            bind.textDolarVariant.visibility = View.INVISIBLE
            bind.imgDolarVariant.visibility = View.INVISIBLE
        }

    }

    private fun setAdapter()
    {
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1,coinList)
        bind.spinFrom.setAdapter(adapter)
        bind.spinFrom.setText(coinList[0],false)
    }

    private fun getCurrencies()
    {
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://economia.awesomeapi.com.br/")
        val endpoint = retrofitClient.create(Endpoint::class.java)


        ready(true)
        endpoint.getCurrencies().enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                val data = response.body()?.entrySet()?.find { it.key == "USDBRL"}?.value

                val dolValue = data?.asJsonObject?.entrySet()?.find { it.key=="bid" }?.value?.asFloat.toString()

                ready(false)

                variantInfo(dolValue.toFloat())
                convertCoin(dolValue,bind.edittextValue.text.toString().toFloat())
                saveVariantDol(dolValue.toFloat())

            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                ready(false)
                Snackbar.make(bind.root, "Sem conexão com o servidor", Snackbar.LENGTH_SHORT).show()


            }


        })


    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun variantInfo(actualDol: Float)
    {
        val baseDol = mShared.getDolar(Constants.KEYS.variantDolar)
        var result = ""
        var percent =0f

        if(baseDol > 0f)
        {
           if(format(actualDol)>format(baseDol))
           {
               resourceVisibility(true)
               bind.imgDolarVariant.setImageResource(ic_baseline_north_24)
               result ="Dolar em Alta"
               percent = (actualDol-baseDol)/(baseDol)*100

            }
           else if(format(actualDol)==format(baseDol))
           {
               resourceVisibility(false)
           }
           else
           {
               resourceVisibility(true)
               bind.imgDolarVariant.setImageResource(ic_baseline_south_24)
               result ="Dolar em Baixa"
               percent = (actualDol-baseDol)/(baseDol)*100
           }


            bind.textDolarVariant.text = "${String.format("%.2f",percent)}% $result"


        }
        else{
            resourceVisibility(false)
        }

    }

    private fun validateField() : Boolean = bind.edittextValue.text.isNullOrEmpty()


    private fun saveVariantDol(variantDol : Float)
    {
        if(mShared.getDolar(Constants.KEYS.variantDolar)==0f)
        {
            mShared.storeDolar(Constants.KEYS.variantDolar,variantDol)
        }

        val a = format(mShared.getDolar(Constants.KEYS.variantDolar))
        val b = format(variantDol)
        if(a != b)
        {
            mShared.storeDolar(Constants.KEYS.variantDolar,variantDol)
        }

    }


    @SuppressLint("SetTextI18n")
    private fun convertCoin(dolValue: String, inputValue : Float)
    {

        when {
            bind.spinFrom.text.toString() == coinList[0] -> {
                val result = (inputValue * dolValue.toFloat())
                bind.textResult.text = "R$"+ String.format("%.2f",result)


            }
            else -> {
                val result = (inputValue / dolValue.toFloat())
                bind.textResult.text = "$"+ String.format("%.2f",result)

            }
              }

    }



private fun ready(bol:Boolean)
{
    if(bol)
    {
        var i = 0
        progressBar.visibility = View.VISIBLE

        progressBar.progress = i

        Thread {
            while (i < 100) {
                i += 1

                val mainExecutor = ContextCompat.getMainExecutor(this)

                mainExecutor.execute {
                   progressBar.progress = i
                }

                try {
                    Thread.sleep(20)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                }

        }.start()
    }
    else progressBar.visibility = View.INVISIBLE

}


}


