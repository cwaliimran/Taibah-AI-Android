package com.taibahai.activities


import android.os.AsyncTask
import com.google.zxing.common.StringUtils
import com.network.base.BaseActivity
import com.network.models.ModelSurah
import com.network.models.ModelSurahDetail
import com.taibahai.R
import com.taibahai.adapters.AdapterQuranDetail
import com.taibahai.databinding.ActivityChapterDetailBinding
import com.taibahai.utils.JsonUtils
import com.taibahai.utils.JsonUtilss
import org.json.JSONArray
import org.json.JSONException

class ChapterDetailActivity : BaseActivity() {
    lateinit var binding:ActivityChapterDetailBinding
    val showList=ArrayList<ModelSurahDetail>()
    lateinit var adapter: AdapterQuranDetail
    private val isFling = false
    private var totalVerse = 0
    private  var counter:Int = 0
    var surahId=""
    var mPlayerList: List<ModelSurah>? = null
    var name=""



    override fun onCreate() {
        binding=ActivityChapterDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter= AdapterQuranDetail(showList)
      /*  showList.add(ModelQuranDetail(1, "قُلْ هُوَ ٱللَّهُ أَحَدٌ ١","Qul huwa Allahu Ahad ","Soy, “O Prophet,”He is Allah_One"))
        showList.add(ModelQuranDetail(2, "ٱللَّهُ ٱلصَّمَدُ ","Allahu As-Samad","Allah—the Sustainer ˹needed by all˺"))
        showList.add(ModelQuranDetail(3, "لَمْ يَلِدْ وَلَمْ يُولَدْ ٣","Allahu As-Samad","Allah—the Sustainer ˹needed by all˺"))
        adapter.setDate(showList)*/
        binding.rvQuranDetail.adapter=adapter

    }


    private fun showAyatList() {
        class loadAyats :
            AsyncTask<Void?, Void?, List<ModelSurahDetail>>() {
            override fun doInBackground(vararg params: Void?): List<ModelSurahDetail> {
                try {
                    val jsonArr = JSONArray(JsonUtilss.loadQuranJson(context, surahId))
                    //                    Log.d("response jsonArr: ", jsonArr.toString());
                    for (i in 0 until jsonArr.length()) {
                        val surahModel = ModelSurahDetail()
                        if (surahId == jsonArr.getJSONObject(i).getString("surah_number")) {
                            surahModel.position = jsonArr.getJSONObject(i).getString("verse_number")
                            surahModel.arabic = jsonArr.getJSONObject(i).getString("text")
                            surahModel.english_translation =
                                jsonArr.getJSONObject(i).getString("translation_en")
                            surahModel.english_transliteration =
                                jsonArr.getJSONObject(i).getString("transliteration_en")
                            showList.add(surahModel)
                            counter++
                            if (counter == totalVerse) {
                                break
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                return showList
            }

            override fun onPreExecute() {
                super.onPreExecute()
                showList.clear()
                adapter.setDate(showList)

            }


            override fun onPostExecute(tasks: List<ModelSurahDetail>) {
                super.onPostExecute(tasks)
                counter = 0
                adapter.setDate(tasks)

              //  playSurah()
               // Handler().postDelayed({ this@ChapterDetailActivity.startScroll() }, 1000)
            }
        }
        loadAyats().execute()
    }


    private fun loadJson() {
        showList.clear()
        val intent = intent
        surahId = intent.getStringExtra("ayat_id")!!
        val verse = intent.getStringExtra("ayat_verse")
        totalVerse = verse!!.toInt()
        name = intent.getStringExtra("ayat_name")!!
        val type = intent.getStringExtra("ayat_type")

    }


    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.setText("Quran")
        binding.appbar.ivLeft.setImageDrawable(resources.getDrawable(R.drawable.arrow_back_24))
        binding.appbar.ivRight.setImageDrawable(resources.getDrawable(R.drawable.heartt))
        loadJson()
        showAyatList()
    }
}