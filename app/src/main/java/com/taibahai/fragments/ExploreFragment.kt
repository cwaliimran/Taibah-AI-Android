package com.taibahai.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.network.base.BaseFragment
import com.network.models.ModelSurah
import com.network.models.ModelToday
import com.network.network.NetworkResult
import com.network.utils.AppClass
import com.network.utils.AppConstants
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.activities.QuranChaptersActivity
import com.taibahai.databinding.FragmentExploreBinding
import com.taibahai.hadiths.HadithBooksActivity1
import com.taibahai.utils.showToast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ExploreFragment : BaseFragment() {

    lateinit var binding: FragmentExploreBinding
    val viewModel: MainViewModelAI by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentExploreBinding>(
            inflater, R.layout.fragment_explore, container, false
        )

        return binding.root
    }

    override fun viewCreated() {
        binding.appbar.tvTitle.setText("Explore")
        binding.appbar.ivLeft.setVisibility(View.GONE)
        binding.appbar.ivRight.setVisibility(View.GONE)

    }

    override fun clicks() {
        binding.clQuran.setOnClickListener {
            val intent = Intent(requireContext(), QuranChaptersActivity::class.java)
            startActivity(intent)
        }

        binding.clHadith.setOnClickListener {
            val intent = Intent(requireContext(), HadithBooksActivity1::class.java)
            startActivity(intent)
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.todayLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            activity?.displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    activity?.displayLoading(true)
                }

                is NetworkResult.Success -> {
                    AppClass.sharedPref.storeObject(AppConstants.TODAY, it.data?.data)
                    AppClass.sharedPref.storeDate(AppConstants.CURRENT_DATE, Date())
                    binding.inTodayVerse.tvArbiAyat.text = it.data?.data?.quran?.text
                    binding.inTodayVerse.tvTranslation.text = it.data?.data?.quran?.quran_translation_en
                    binding.inTodayVerse.tvSurah.text = it.data?.data?.quran?.transliteration_en
                    val transliteration = it.data?.data?.quran?.quran_transliteration_en
                    if (transliteration != null) {
                        val spannedText: Spanned =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Html.fromHtml(transliteration, Html.FROM_HTML_MODE_LEGACY)
                            } else {
                                @Suppress("DEPRECATION") HtmlCompat.fromHtml(
                                    transliteration, HtmlCompat.FROM_HTML_MODE_LEGACY
                                )
                            }

                        binding.inTodayVerse.tvEnglishAyat.text = spannedText
                    }


                    binding.inTodayHadith.tvArbiHadith.text = it.data?.data?.hadith?.arabic
                    binding.inTodayHadith.tvTranslation.text =
                        it.data?.data?.hadith?.english_translation
                    val reference = it.data?.data?.hadith?.reference
                    if (reference != null) {
                        val parts = reference.split("\t : ")
                        if (parts.size > 1) {
                            val trimmedReference = parts[1].trim()
                            binding.inTodayHadith.tvHadithFrom.text = trimmedReference
                        }
                    }


                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        val todayDate = Date()
        val savedTodayDate = AppClass.sharedPref.getDate(AppConstants.CURRENT_DATE)

        if (todayDate.toString() != savedTodayDate.toString()) {
            viewModel.today()
        }
        else {
            val savedData =AppClass.sharedPref.getObject(AppConstants.TODAY, ModelToday::class.java)
            if (savedData != null)
            {
                binding.inTodayVerse.tvArbiAyat.text = savedData.data.quran.text
                binding.inTodayVerse.tvTranslation.text = savedData.data.quran.quran_translation_en
                binding.inTodayVerse.tvSurah.text = savedData.data.quran.transliteration_en
                val transliteration = savedData.data.quran.quran_transliteration_en
                if (transliteration != null)

                {
                    val spannedText: Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(transliteration, Html.FROM_HTML_MODE_LEGACY)}

                    else {
                        @Suppress("DEPRECATION") HtmlCompat.fromHtml(transliteration,HtmlCompat.FROM_HTML_MODE_LEGACY)
                    }

                    binding.inTodayVerse.tvEnglishAyat.text = spannedText
                }


                binding.inTodayHadith.tvArbiHadith.text = savedData.data.hadith.arabic
                binding.inTodayHadith.tvTranslation.text = savedData.data.hadith.english_translation
                val reference = savedData.data.hadith.reference
                if (reference != null) {
                    val parts = reference.split("\t : ")
                    if (parts.size > 1) {
                        val trimmedReference = parts[1].trim()
                        binding.inTodayHadith.tvHadithFrom.text = trimmedReference
                    }
                }
            }


        }


    }

    fun isSameDay(date1: Date?, date2: Date?): Boolean {
        if (date1 == null || date2 == null) {
            return false
        }

        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(
            Calendar.MONTH
        ) && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }
}