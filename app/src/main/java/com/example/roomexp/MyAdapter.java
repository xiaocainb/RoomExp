package com.example.roomexp;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends ListAdapter<Word, MyAdapter.MyViewHolder> {
    boolean useCardView;
    private WordViewModel wordViewModel;

    public MyAdapter(boolean useCardView, WordViewModel wordViewModel) {
//继承自ViewHolderAdapter改为ListAdapter 列表数据的差异化处理在后台 异步进行******************************
        super(new DiffUtil.ItemCallback<Word>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull Word oldItem, @NonNull @NotNull Word newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull @NotNull Word oldItem, @NonNull @NotNull Word newItem) {
                return (oldItem.getWord().equals(newItem.getWord())
                        && oldItem.getChineseMeaning().equals(newItem.getChineseMeaning())
                        && oldItem.isChineseInvisible() == newItem.isChineseInvisible());
            }
        });
        this.wordViewModel = wordViewModel;
        this.useCardView = useCardView;
    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if (useCardView) {
            itemView = layoutInflater.inflate(R.layout.unit_card_2, parent, false);
        } else {
            itemView = layoutInflater.inflate(R.layout.unit_normal_2, parent, false);
        }
//********************************************************************************************************************
//      两个listener从onBindViewHolder中写到onCreateViewHolder提升性能
//      itemView设置点击跳转
        MyViewHolder holder = new MyViewHolder(itemView);
        holder.itemView.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://translate.google.cn/?sl=en&tl=zh-CN&text=" + holder.textViewEnglish.getText() + "&op=translate");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            holder.itemView.getContext().startActivity(intent);
        });

//     设置中文隐藏按钮点击事件
        holder.aSwitchChineseInvisible.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Word word = (Word) holder.itemView.getTag(R.id.word_for_view_holder);
            if (isChecked) {
                holder.textViewChinese.setVisibility(View.GONE);
                word.setChineseInvisible(true);
                wordViewModel.updatetWords(word);
            } else {
                holder.textViewChinese.setVisibility(View.VISIBLE);
                word.setChineseInvisible(false);
                wordViewModel.updatetWords(word);
            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull MyAdapter.MyViewHolder holder, int position) {
        Word word = getItem(position);

//      setTag全局使用word
        holder.itemView.setTag(R.id.word_for_view_holder, word);

        holder.textViewNumber.setText(String.valueOf(word.getId()));
        holder.textViewEnglish.setText(word.getWord());
        holder.textViewChinese.setText(word.getChineseMeaning());
//      setChecked在recyclerView中新的switch会用以前回收的switch，创建新的switch时候会一直调用onCheckedChanged（设置中文隐藏按钮点击事件）
//      先在listener中设置初始值null可以解决 //两个listener已经转移到onCreateViewHolder中，不再需要用这个方法解决性能问题 
//      holder.aSwitchChineseInvisible.setOnCheckedChangeListener(null);

        if (word.isChineseInvisible()) {
            holder.textViewChinese.setVisibility(View.GONE);
            holder.aSwitchChineseInvisible.setChecked(true);
        } else {
            holder.textViewChinese.setVisibility(View.VISIBLE);
            holder.aSwitchChineseInvisible.setChecked(false);
        }
    }


    //      ViewHolder中绑定三个text数据和中文隐藏按钮
    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNumber, textViewEnglish, textViewChinese;
        Switch aSwitchChineseInvisible;

        public MyViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);

            textViewNumber = itemView.findViewById(R.id.textViewNumber);
            textViewEnglish = itemView.findViewById(R.id.textViewEnglish);
            textViewChinese = itemView.findViewById(R.id.textViewChinese);
            aSwitchChineseInvisible = itemView.findViewById(R.id.switchChineseInvisible);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull @NotNull MyAdapter.MyViewHolder holder) {
//      防止屏幕外单词序号错误
        holder.textViewNumber.setText(String.valueOf(holder.getAdapterPosition() + 1));
        super.onViewAttachedToWindow(holder);
    }
}
