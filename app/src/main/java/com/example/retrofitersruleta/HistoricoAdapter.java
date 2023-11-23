package com.example.retrofitersruleta;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoricoAdapter extends RecyclerView.Adapter<HistoricoAdapter.ViewHolder> {

    private List<HistoricoItem> historicoList;

    public HistoricoAdapter(List<HistoricoItem> historicoList) {
        this.historicoList = historicoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View historicoView = inflater.inflate(R.layout.activity_historico, parent, false);

        return new ViewHolder(historicoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoricoItem historicoItem = historicoList.get(position);

        holder.textId.setText("Id: " + historicoItem.getId());
        holder.textNombre.setText("Jugador: " + historicoItem.getNombre());
        holder.textMonedero.setText("Monedero: " + historicoItem.getMonedero());
        holder.textTurnos.setText("Turnos: " + historicoItem.getTurnos());
    }

    @Override
    public int getItemCount() {
        return historicoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textId;
        public TextView textNombre;
        public TextView textMonedero;
        public TextView textTurnos;

        public ViewHolder(View itemView) {
            super(itemView);

            textId = itemView.findViewById(R.id.textId);
            textNombre = itemView.findViewById(R.id.textNombre);
            textMonedero = itemView.findViewById(R.id.textMonedero);
            textTurnos = itemView.findViewById(R.id.textTurnos);
        }
    }
}
