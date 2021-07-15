package com.example.roomexp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WordsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordsFragment extends Fragment {

    private WordViewModel wordViewModel;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter1, myAdapter2;
    private FloatingActionButton floatingActionButton;
    private LiveData<List<Word>> filteredWords;
    private static final String VIEW_TYPE_SHARED_PREFERENCES = "view_type_shared_preferences";
    private static final String IS_USING_CARD_VIEW = "is_using_card_view";
    private List<Word> allWords;
    private boolean undoAction;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WordsFragment() {
//      默认上方不显示工具条，需开启
        setHasOptionsMenu(true);
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WordsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WordsFragment newInstance(String param1, String param2) {
        WordsFragment fragment = new WordsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_words, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//      wordViewModel = ViewModelProviders.of(requireActivity()).get(WordViewModel.class);
        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        recyclerView = requireActivity().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public void onAnimationFinished(@NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager != null) {
                    int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                    for (int i = firstPosition; i <= lastPosition; i++) {
                        MyAdapter.MyViewHolder myViewHolder = (MyAdapter.MyViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        if (myViewHolder != null) {
                            myViewHolder.textViewNumber.setText(String.valueOf(i + 1));
                        }
                    }
                }
                super.onAnimationFinished(viewHolder);
            }
        });
        myAdapter1 = new MyAdapter(false, wordViewModel);
        myAdapter2 = new MyAdapter(true, wordViewModel);


        floatingActionButton = requireActivity().findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_wordsFragment_to_addFragment);
        });

//*******************************************************************************************************
//        recyclerView.setAdapter(myAdapter1);
//      在onActivityCreated中处理，onCreateOptionsMenu添加单词后返回onOptionsItemSelected中视图不能记住当前状态
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(VIEW_TYPE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        boolean viewType = sharedPreferences.getBoolean(IS_USING_CARD_VIEW, false);
        if (viewType) {
            recyclerView.setAdapter(myAdapter2);
        } else {
            recyclerView.setAdapter(myAdapter1);
        }

        filteredWords = wordViewModel.getAllWordsLive();
//      视图层面刷新     不能用requireActivity()引用，是错误的owner他会每次切换添加页面和单词页面时重新调用，否则出现列表闪动异常要用LifeCycleOwner
        filteredWords.observe(getViewLifecycleOwner(), words -> {
            int temp = myAdapter1.getItemCount();
            allWords = words;
            if (temp != words.size()) {
                if (temp < words.size() && !undoAction) {
                    recyclerView.smoothScrollBy(0, -200);
                }
                undoAction = false;
                myAdapter1.submitList(words);
                myAdapter2.submitList(words);
            }
        });
//        recyclerView中的ItemTouchHelper工具实现滑动删除
//        *****
//        *bug*无法执行数据库删除操作
//        *****
//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START) {
//            @Override
//            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
////              allWords不用判断是否为空
//                Word wordToDelete = allWords.get(viewHolder.getAdapterPosition());
//                wordViewModel.deleteWords(wordToDelete);
//
//                Snackbar.make(requireActivity().findViewById(R.id.wordsFragmentView), "你删除了这个单词", Snackbar.LENGTH_SHORT)
//                        .setAction("撤销", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                  undoAction = true;
//                                wordViewModel.insertWords(wordToDelete);
//                            }
//                        }).show();
//            }
//        }).attachToRecyclerView(recyclerView);
    }

    //    右上方工具栏：清空数据和切换试图
    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearData:
//              当选择清空数据时，弹出AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("清空数据");
                builder.setPositiveButton("确定", (dialog, which) -> wordViewModel.deleteAllWords());
                builder.setNegativeButton("取消", (dialog, which) -> {
                });
                builder.create();
                builder.show();
                break;
//              当选择切换视图时，改变recyclerView的Adapter
            case R.id.switchViewType:
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(VIEW_TYPE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                boolean viewType = sharedPreferences.getBoolean(IS_USING_CARD_VIEW, false);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (viewType) {
                    recyclerView.setAdapter(myAdapter1);
                    editor.putBoolean(IS_USING_CARD_VIEW, false);
                } else {
                    recyclerView.setAdapter(myAdapter2);
                    editor.putBoolean(IS_USING_CARD_VIEW, true);
                }
                editor.apply();
        }
        return super.onOptionsItemSelected(item);
    }

    //  右上方工具栏：onCreate搜索框
    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setMaxWidth(700);
//      设置监听文字内容变化
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//              模糊查询
                String patten = newText.trim();
//***********************************************************************
//              先移除observers 不然会与onActivityCreated中的filteredWords.observe冲突
                filteredWords.removeObservers(getViewLifecycleOwner());
                filteredWords = wordViewModel.findWordsWithPatten(patten);
//不能用requireActivity()引用，是错误的owner他会每次切换添加页面和单词页面时重新调用，否则出现列表闪动异常要用getLifeCycleOwner
                filteredWords.observe(getViewLifecycleOwner(), words -> {
                    int temp = myAdapter1.getItemCount();
                    allWords = words;
                    if (temp != words.size()) {
//                        改变插入方式，让单词插入不显得突兀
//                        myAdapter1.notifyDataSetChanged();
//                        myAdapter2.notifyDataSetChanged();
//                        改为继承ListAdapter后用submitList()方法提交数据，显得不突兀
                        myAdapter1.submitList(words);
                        myAdapter2.submitList(words);
                    }
                });
                return true;
            }
        });
    }
    //    @Override
//    public void onResume() {
//        强制消失键盘
//        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
//        super.onResume();
//    }
}