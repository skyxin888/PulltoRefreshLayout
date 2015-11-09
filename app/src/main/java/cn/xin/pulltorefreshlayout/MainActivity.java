package cn.xin.pulltorefreshlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import cn.xin.pulltorefreshlibrary.PulltoRefreshLayout;
import cn.xin.pulltorefreshlibrary.PulltorefreshRefreshListener;

public class MainActivity extends BaseActivity {

    PulltoRefreshLayout mRefreshLayout;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView =(RecyclerView)findViewById(R.id.recyclerView);
        mRefreshLayout =(PulltoRefreshLayout)findViewById(R.id.refreshLayout);
        setupRecyclerView(recyclerView);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new SimpleStringAdapter(MainActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mRefreshLayout.setPulltoRefreshListener(new PulltorefreshRefreshListener() {
            @Override
            public void onRefresh(final PulltoRefreshLayout pulltoRefreshLayout) {
                pulltoRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pulltoRefreshLayout.finishRefreshing();

                    }
                }, 3000);
            }

            @Override
            public void onRefreshLoadMore(final PulltoRefreshLayout pulltoRefreshLayout) {
                Toast.makeText(MainActivity.this, "load more", Toast.LENGTH_LONG).show();
                pulltoRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pulltoRefreshLayout.finishRefreshLoadMore();

                    }
                }, 3000);
            }
        });
    }

}
