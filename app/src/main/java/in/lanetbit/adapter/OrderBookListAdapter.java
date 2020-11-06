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
import in.lanetbit.viewHolder.OrderBookHistoryListViewHolder;

public class OrderBookListAdapter extends RecyclerView.Adapter<OrderBookHistoryListViewHolder> {

    private Context mContext;
    private ArrayList<AskOrderBookHistory> mAskOrderBookHistoryArrayList;
    private ArrayList<BidOrderBookHistory> mBidOrderBookHistoryArrayList;

    public OrderBookListAdapter(Context context, RecyclerView recyclerViewOrderBook, ArrayList<AskOrderBookHistory> askOrderBookHistoryArrayList) {
        this.mAskOrderBookHistoryArrayList = askOrderBookHistoryArrayList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public OrderBookHistoryListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_book_history_list_item_view, viewGroup, false);
        return new OrderBookHistoryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderBookHistoryListViewHolder holder, int position) {
        if (mAskOrderBookHistoryArrayList.get(position) != null) {
            final AskOrderBookHistory askOrderBookHistory = mAskOrderBookHistoryArrayList.get(position);
            holder.textViewAmount.setText(askOrderBookHistory.getAmount());
            holder.textViewPrice.setText(askOrderBookHistory.getPrice());
        }
//        if (mBidOrderBookHistoryArrayList.get(position) != null) {
//            final BidOrderBookHistory bidOrderBookHistory = mBidOrderBookHistoryArrayList.get(position);
//
//        }
    }

    @Override
    public int getItemCount() {
        if (mAskOrderBookHistoryArrayList != null)
            return mAskOrderBookHistoryArrayList.size();
        else
            return 0;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

}
