package in.lanetbit.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import in.lanetbit.R;

public class OrderBookHistoryListViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewAmount,textViewPrice;
    public OrderBookHistoryListViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewAmount = itemView.findViewById(R.id.tv_amount);
        textViewPrice = itemView.findViewById(R.id.tv_price);

    }
}
