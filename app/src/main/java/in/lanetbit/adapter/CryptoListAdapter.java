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
import in.lanetbit.model.CryptoToCryptoList;
import in.lanetbit.model.OrdersHistory;
import in.lanetbit.viewHolder.CryptoToCryptoViewHolder;
import in.lanetbit.viewHolder.OrderListHistoryViewHolder;

public class CryptoListAdapter extends RecyclerView.Adapter<CryptoToCryptoViewHolder>{
    private Context mContext;
    private ArrayList<CryptoToCryptoList> mCryptoToCryptoLists;

    public CryptoListAdapter(Context context, RecyclerView recyclerViewCryptoList, ArrayList<CryptoToCryptoList> cryptoToCryptoLists) {

        this.mCryptoToCryptoLists = cryptoToCryptoLists;
        this.mContext = context;
    }

    @NonNull
    @Override
    public CryptoToCryptoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.crypto_to_crypto_list_item_view, viewGroup, false);
        return new CryptoToCryptoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CryptoToCryptoViewHolder holder, int position) {

        if (mCryptoToCryptoLists.get(position) != null) {

            final CryptoToCryptoList cryptoToCryptoList = mCryptoToCryptoLists.get(position);

            holder.textViewStatus.setText(cryptoToCryptoList.getStatus());
            holder.textViewCurrencyFrom.setText(cryptoToCryptoList.getCurrencyFrom());
            holder.textViewCurrencyTo.setText(cryptoToCryptoList.getCurrencyTo());
            holder.textViewMoneyReceived.setText(cryptoToCryptoList.getMoneyReceived());
            holder.textViewMoneySent.setText(cryptoToCryptoList.getMoneySent());
            holder.textViewTotalFee.setText(cryptoToCryptoList.getTotalFee());
        }

    }

    @Override
    public int getItemCount() {
        if (mCryptoToCryptoLists != null)
            return mCryptoToCryptoLists.size();
        else
            return 0;
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }
}
