package cn.xin.pulltorefreshlayout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by guxin on 15/11/6.
 */
public class SimpleStringAdapter extends RecyclerView.Adapter<SimpleStringAdapter.ViewHolder>{
    public SimpleStringAdapter(Context context) {
        super();
    }

    @Override
    public SimpleStringAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_simplestring, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleStringAdapter.ViewHolder holder, int position) {
        switch (position%4){
            case 0:
                holder.mText.setText("nice");
                break;
            case 1:
                holder.mText.setText("pull");
                break;
            case 2:
                holder.mText.setText("to");
                break;
            case 3:
                holder.mText.setText("refresh");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mText;

        public ViewHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.content);
        }
    }
}
