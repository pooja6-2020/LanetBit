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
import in.lanetbit.model.BidOrderBookHistory;
import in.lanetbit.viewHolder.OrderBookHistoryBidListViewHolder;
import in.lanetbit.viewHolder.OrderBookHistoryListViewHolder;

public class OrderBookBidListAdapter  extends RecyclerView.Adapter<OrderBookHistoryBidListViewHolder> {
    private ArrayList<BidOrderBookHistory> mBidOrderBookHistoryArrayList;
    private Context mContext;

    public OrderBookBidListAdapter(Context context, RecyclerView recyclerViewOrderBook, ArrayList<BidOrderBookHistory> bidOrderBookHistoryArrayList) {
        this.mBidOrderBookHistoryArrayList = bidOrderBookHistoryArrayList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public OrderBookHistoryBidListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_book_history_bid_list_item_view, viewGroup, false);
        return new OrderBookHistoryBidListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderBookHistoryBidListViewHolder holder, int position) {
        if (mBidOrderBookHistoryArrayList.get(position) != null) {
            final BidOrderBookHistory bidOrderBookHistory = mBidOrderBookHistoryArrayList.get(position);
            holder.textViewAmount.setText(bidOrderBookHistory.getAmount());
            holder.textViewPrice.setText(bidOrderBookHistory.getPrice());
        }
    }

    @Override
    public int getItemCount() {
        if (mBidOrderBookHistoryArrayList != null)
            return mBidOrderBookHistoryArrayList.size();
        else
            return 0;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

}
