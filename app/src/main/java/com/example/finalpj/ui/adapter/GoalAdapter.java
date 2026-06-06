package com.example.finalpj.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalpj.R;
import com.example.finalpj.data.db.entity.Goal;
import com.example.finalpj.utils.CurrencyUtils;
import java.util.ArrayList;
import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {
    private List<Goal> goals = new ArrayList<>();
    private OnGoalClickListener listener;

    public interface OnGoalClickListener {
        void onGoalClick(Goal goal);
    }

    public void setOnGoalClickListener(OnGoalClickListener listener) {
        this.listener = listener;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        holder.bind(goals.get(position));
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvProgressText, tvAmount;
        ProgressBar progressBar;
        View viewColor;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_goal_name);
            tvProgressText = itemView.findViewById(R.id.tv_goal_progress_text);
            tvAmount = itemView.findViewById(R.id.tv_goal_amount);
            progressBar = itemView.findViewById(R.id.pb_goal);
            viewColor = itemView.findViewById(R.id.view_goal_color);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onGoalClick(goals.get(pos));
                }
            });
        }

        public void bind(Goal goal) {
            tvName.setText(goal.name);
            int progress = goal.getProgress();
            tvProgressText.setText(progress + "%");
            tvAmount.setText(CurrencyUtils.format(goal.currentAmount) + " / " + CurrencyUtils.format(goal.targetAmount));
            progressBar.setProgress(progress);

            // Dynamic color based on status
            long now = System.currentTimeMillis();
            int statusColor;
            if (progress >= 100) {
                statusColor = Color.parseColor("#4CAF50"); // Green
            } else if (goal.deadline > 0 && goal.deadline < now) {
                statusColor = Color.parseColor("#F44336"); // Red
            } else {
                statusColor = Color.parseColor("#2196F3"); // Blue
            }

            viewColor.setBackgroundColor(statusColor);
            tvProgressText.setTextColor(statusColor);
            progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(statusColor));
        }
    }
}
