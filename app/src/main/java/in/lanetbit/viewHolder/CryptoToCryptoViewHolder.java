package in.lanetbit.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import in.lanetbit.R;

/**
 * Created by Abhimanyu Kumar on 02,June,2020
 **/
public class CryptoToCryptoViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewStatus,textViewCurrencyFrom,textViewCurrencyTo,textViewMoneyReceived,textViewMoneySent,textViewTotalFee;
    public CryptoToCryptoViewHolder(@NonNull View itemView) {
        super(itemView);

        textViewStatus = itemView.findViewById(R.id.tv_status);
        textViewCurrencyFrom = itemView.findViewById(R.id.tv_currency_from);
        textViewCurrencyTo = itemView.findViewById(R.id.tv_currency_to);
        textViewMoneyReceived = itemView.findViewById(R.id.tv_money_received);
        textViewMoneySent = itemView.findViewById(R.id.tv_money_sent);
        textViewTotalFee = itemView.findViewById(R.id.tv_total_fee);
    }
}
