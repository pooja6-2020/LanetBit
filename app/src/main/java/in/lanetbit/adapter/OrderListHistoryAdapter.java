package in.lanetbit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.lanetbit.R;
import in.lanetbit.model.AskOrderBookHistory;
import in.lanetbit.model.OrdersHistory;
import in.lanetbit.viewHolder.OrderBookHistoryListViewHolder;
import in.lanetbit.viewHolder.OrderListHistoryViewHolder;

public class OrderListHistoryAdapter extends RecyclerView.Adapter<OrderListHistoryViewHolder>{

    private Context mContext;
    private ArrayList<OrdersHistory> mOrdersHistoryArrayList;
    public OrderListHistoryAdapter(Context context, RecyclerView recyclerViewOrderListHistory, ArrayList<OrdersHistory> ordersHistoryArrayList) {
        this.mOrdersHistoryArrayList = ordersHistoryArrayList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public OrderListHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_history_list_item_view, viewGroup, false);
        return new OrderListHistoryViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull OrderListHistoryViewHolder holder, int position) {
        if (mOrdersHistoryArrayList.get(position) != null) {
            final OrdersHistory ordersHistory = mOrdersHistoryArrayList.get(position);
            holder.textViewAmount.setText(ordersHistory.getAmount());
         //   holder.textViewStatusStatus.setText(ordersHistory.getStatus());
            holder.textViewInstrument.setText(ordersHistory.getInstrument());
            if (ordersHistory.getOrderType().matches("Sell")){
                holder.textViewOrderType.setText(ordersHistory.getOrderType());
                holder.textViewOrderType.setTextColor(mContext.getResources().getColor(R.color.colorDarkRed));
            }else {
                holder.textViewOrderType.setText(ordersHistory.getOrderType());
                holder.textViewOrderType.setTextColor(mContext.getResources().getColor(R.color.colorAccent));


            }
        }

    }

    @Override
    public int getItemCount() {
        if (mOrdersHistoryArrayList != null)
            return mOrdersHistoryArrayList.size();
        else
            return 0;    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }
}
