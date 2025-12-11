package com.example.savebite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PantryFragment extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pantry, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        db = new DatabaseHelper(getContext());

        loadData();
        return view;
    }

    private void loadData() {
        List<PantryItem> list = db.getAllItems();
        PantryAdapter adapter = new PantryAdapter(getContext(), list, new PantryAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int id) {
                db.deleteItem(id);
                loadData();
            }

            @Override
            public void onStatusChange() {
                // 状态改变时（比如勾选Consumed），不需要重载整个列表，
                // 因为 Adapter 内部已经处理了 notifyItemChanged。
                // 如果需要和其他 Fragment 联动，通常在 onResume 里刷新数据即可。
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // 每次回来都刷新
    }
}