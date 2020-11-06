package in.lanetbit.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import in.lanetbit.R;

public class OrderListHistoryViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewAmount,textViewStatusStatus,textViewOrderType,textViewInstrument;

    public OrderListHistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewAmount = itemView.findViewById(R.id.tv_amount);
      //  textViewStatusStatus = itemView.findViewById(R.id.tv_status);
        textViewOrderType = itemView.findViewById(R.id.tv_order_type);
        textViewInstrument = itemView.findViewById(R.id.tv_instrument);

    }
}
