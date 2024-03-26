package com.taibahai.quran;

import static com.network.utils.AppClass.sharedPref;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taibahai.R;
import com.taibahai.databinding.ItemQuranChapterDetailBinding;

import java.util.ArrayList;
import java.util.List;

public class SurahAdapter extends RecyclerView.Adapter<SurahAdapter.HomeListHolder> {
    private OnItemClickListener listener;
    private List<SurahModel> mData;
    private final Context context;
    LayoutInflater layoutInflater;


    public SurahAdapter(Context context) {
        this.context = context;
        mData = new ArrayList<>();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateList(List<SurahModel> tasks) {
        this.mData = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomeListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quran_chapter_detail, parent, false);
        return new HomeListHolder(ItemQuranChapterDetailBinding.inflate(layoutInflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeListHolder holder, int position) {
        SurahModel model = mData.get(position);
        if (position == mData.size() - 1) {
            holder.binding.footer.setVisibility(View.VISIBLE);
        } else {
            holder.binding.footer.setVisibility(View.GONE);
        }

        holder.binding.ayatArabicText.setText(model.getArabicText());
        holder.binding.ayatEnglishTranslation.setText(model.getEnglishText());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.binding.ayatEnglishTranslitration.setText(Html.fromHtml(model.getEnglish_translation(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.binding.ayatEnglishTranslitration.setText(Html.fromHtml(model.getEnglish_translation()));
        }
        holder.binding.ayatNumber.setText(String.valueOf(model.getPosition()));


//        holder.binding.shareImage.setOnClickListener(v -> {
//            if (listener != null && position != RecyclerView.NO_POSITION) {
//                listener.onItemClick(holder.itemView, model);
//            }
//        });

        int textSize = sharedPref.getInt(StringUtils.FONT_SIZE);
        if (textSize != 0) {
            holder.binding.ayatArabicText.setTextSize(textSize);
        }

    }

    @Override
    public int getItemCount() {
        if (mData != null)
            return mData.size();
        else return 0;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, SurahModel homeModel);
    }

    public void setOnItemClickListner(OnItemClickListener listener) {
        this.listener = listener;
    }

    static class HomeListHolder extends RecyclerView.ViewHolder {
        ItemQuranChapterDetailBinding binding;

        HomeListHolder(@NonNull ItemQuranChapterDetailBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}