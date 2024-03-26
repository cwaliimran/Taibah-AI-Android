package com.taibahai.quran;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.taibahai.R;
import com.taibahai.databinding.AlQuranItemBinding;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Status;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AllSurahListAdapter extends RecyclerView.Adapter<AllSurahListAdapter.HomeListHolder> {
    private final Context context;
    private List<SurahListModel> mData;
    LayoutInflater layoutInflater;
    OnPlayListener listener;
    private String selected = "-1";
//    RoomDatabaseRepository dbRepository;

    public AllSurahListAdapter(Context context) {
        this.context = context;
        mData=new ArrayList<>();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        dbRepository = new RoomDatabaseRepository(context);

    }

    public void updateData( List<SurahListModel> mData){
        this.mData = mData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomeListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeListHolder(AlQuranItemBinding.inflate(layoutInflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeListHolder holder, @SuppressLint("RecyclerView") final int position) {
        // showing data
        final SurahListModel model = mData.get(position);
        if (selected.equals(model.getId())) {
            holder.binding.name.setTextColor(Color.RED);
            holder.binding.meaning.setTextColor(Color.RED);
        } else {
            holder.binding.name.setTextColor(Color.BLACK);
            holder.binding.meaning.setTextColor(Color.BLACK);
        }

        holder.binding.name.setText(model.getTransliterationEn());
        holder.binding.meaning.setText(String.format("%s (%s)", model.getTranslationEn(), model.getTotalVerses()));
        holder.binding.number.setText(model.getNumber());
        holder.binding.checkBox.setSelected(model.isFav());
        loadImageFromDrawable(context, R.drawable.surah, holder.binding.image, R.drawable.surah);


        // handling download statuses
        if (model.getDownload() != null) {
            Status status = model.getDownload().getStatus();
            switch (status) {
                case COMPLETED:
                    String child = StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(model.getAudio());
                    holder.binding.progress.setVisibility(View.GONE);
                    if (isFileExists(child,context)) {
                        holder.binding.play.setImageResource(R.drawable.ic_delete);
                        holder.binding.play.setOnClickListener(v -> listener.onDelete(model));
                        holder.itemView.setOnClickListener(v -> listener.onPlayClick(model));
                    } else {
                        holder.binding.play.setImageResource(R.drawable.ic_download);
                        holder.binding.playLayout.setOnClickListener(v -> listener.onDownload(model));
                        holder.itemView.setOnClickListener(v -> listener.onDownload(model));
                    }
                    break;
                case PAUSED:
                case ADDED:
                    holder.binding.progress.setVisibility(View.GONE);
                    holder.binding.play.setImageResource(R.drawable.ic_download);
                    holder.binding.playLayout.setOnClickListener(v -> listener.onResume(model));
                    holder.itemView.setOnClickListener(v -> listener.onResume(model));
                    break;
                case REMOVED:
                case DELETED:
                    holder.binding.progress.setVisibility(View.GONE);
                    holder.binding.play.setImageResource(R.drawable.ic_download);
                    holder.binding.playLayout.setOnClickListener(v -> listener.onDownload(model));
                    holder.itemView.setOnClickListener(v -> listener.onDownload(model));
                    break;
                case FAILED:
                    holder.binding.progress.setVisibility(View.GONE);
                    holder.binding.play.setImageResource(R.drawable.ic_download);
                    holder.binding.playLayout.setOnClickListener(v -> listener.onRetryDownload(model));
                    holder.itemView.setOnClickListener(v -> listener.onRetryDownload(model));
                    break;
                case DOWNLOADING:
                    holder.binding.progress.setVisibility(View.VISIBLE);
                    holder.binding.progress.setIndeterminateMode(true);
                    holder.binding.play.setImageResource(R.drawable.ic_pin);
                    holder.binding.playLayout.setOnClickListener(v -> listener.onPause(model));
                    break;
                case QUEUED:
                    holder.binding.progress.setVisibility(View.VISIBLE);
                    holder.binding.progress.setIndeterminateMode(false);
                    holder.binding.play.setImageResource(R.drawable.ic_pin);
                    holder.binding.playLayout.setOnClickListener(v -> listener.onPause(model));
//                    long progress = model.getDownload().getDownloaded() / 1000;
//                    long maximum_progress = model.getDownload().getTotal() / 1000;

//                    if (progress == -1) {
//                         Download progress is undermined at the moment.
//                        progress = 0;
//                    }
//                    holder.binding.progress.setIndeterminateMode(true);
//                    holder.binding.progress.setProgressMax(maximum_   progress);
//                    holder.binding.progress.setProgress(progress);
                    break;
                default:
                    break;
            }
        } else {
            holder.binding.progress.setVisibility(View.GONE);
            holder.binding.play.setImageResource(R.drawable.ic_download);
            holder.binding.playLayout.setOnClickListener(v -> listener.onDownload(model));
            holder.itemView.setOnClickListener(v -> listener.onDownload(model));
        }

        //clicks
        holder.binding.checkBox.setOnClickListener(new View.OnClickListener() {
            FavModel favModel;

            @Override
            public void onClick(View v) {
                if (!model.isFav()) {
                    favModel = new FavModel(Long.parseLong(model.getId()));
                   // dbRepository.addToFavSurah(favModel);
                    holder.binding.checkBox.setSelected(true);
                    model.setFav(true);
                    Log.d("response", "park id inserted: " + model.getId());
                } else {
                  //  dbRepository.deleteFromFavSurah(Integer.parseInt(model.getId()));
                    holder.binding.checkBox.setSelected(false);
                    model.setFav(false);
                    Log.d("response", "park id deleted: " + model.getId());
                }
//                notifyItemChanged(position);
            }
        });


    }

    public void setOnItemClickListener(OnPlayListener listener) {
        this.listener = listener;
    }

    public interface OnPlayListener {
        void onDownload(SurahListModel model);

        void onDelete(SurahListModel model);

        void onPause(SurahListModel model);

        void onResume(SurahListModel model);

        void onRetryDownload(SurahListModel model);

        void onPlayClick(SurahListModel model);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    static class HomeListHolder extends RecyclerView.ViewHolder {
        AlQuranItemBinding binding;

        HomeListHolder(@NonNull AlQuranItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }

        public void updateView(Download download) {
            for (int position = 0; position < mData.size(); position++) {
                final SurahListModel downloadData = mData.get(position);
                if (downloadData.getDownloadId(context) == download.getId()) {
                    downloadData.setDownload(download);
                    notifyItemChanged(position);
                    return;
                }
            }
        }

    public void updateView(String id) {
        selected = id;
        notifyDataSetChanged();
    }


    public static void loadImageFromDrawable(Context context, int url, ImageView imageView, int error) {
        try {
            Glide.with(context).load(url)
                    .error(error)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isFileExists(String childPath,Context context) {
        File yourFile = new File(getAudioOutputDirectory(context), childPath);
        return yourFile.exists();
    }



    public static File getAudioOutputDirectory(Context context) {
        File mediaStorageDir = new File(context.getFilesDir() + "/" +
                context.getString(R.string.app_name) + "/Audios");
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        return mediaStorageDir;
    }
}