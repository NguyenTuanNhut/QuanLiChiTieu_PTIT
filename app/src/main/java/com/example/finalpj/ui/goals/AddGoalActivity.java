package com.example.finalpj.ui.goals;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalpj.R;
import com.example.finalpj.data.db.entity.Goal;
import com.example.finalpj.utils.DateUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AddGoalActivity extends AppCompatActivity {
    public static final String EXTRA_GOAL_ID = "extra_goal_id";

    private TextInputEditText etName, etTarget, etCurrent, etDeadline;
    private MaterialButton btnDelete;
    private GoalsViewModel viewModel;
    private long selectedDeadline;
    private int goalId = -1;
    private Goal currentGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        viewModel = new ViewModelProvider(this).get(GoalsViewModel.class);

        etName = findViewById(R.id.et_goal_name);
        etTarget = findViewById(R.id.et_goal_target);
        etCurrent = findViewById(R.id.et_goal_current);
        etDeadline = findViewById(R.id.et_goal_deadline);
        btnDelete = findViewById(R.id.btn_delete_goal);

        etDeadline.setOnClickListener(v -> showDatePicker());

        findViewById(R.id.btn_save_goal).setOnClickListener(v -> saveGoal());
        btnDelete.setOnClickListener(v -> {
            if (currentGoal != null) {
                viewModel.delete(currentGoal);
                finish();
            }
        });

        goalId = getIntent().getIntExtra(EXTRA_GOAL_ID, -1);
        if (goalId != -1) {
            btnDelete.setVisibility(View.VISIBLE);
            // In a real app, we'd observe by ID. For simplicity, we'll wait for the list or query
            viewModel.getGoals().observe(this, goals -> {
                for (Goal g : goals) {
                    if (g.id == goalId) {
                        currentGoal = g;
                        populateGoal(g);
                        break;
                    }
                }
            });
        }
    }

    private void populateGoal(Goal goal) {
        etName.setText(goal.name);
        etTarget.setText(String.valueOf(goal.targetAmount));
        etCurrent.setText(String.valueOf(goal.currentAmount));
        selectedDeadline = goal.deadline;
        etDeadline.setText(DateUtils.formatDate(selectedDeadline));
    }

    private void saveGoal() {
        String name = etName.getText().toString().trim();
        String targetStr = etTarget.getText().toString().trim();
        String currentStr = etCurrent.getText().toString().trim();

        if (name.isEmpty() || targetStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên và số tiền mục tiêu", Toast.LENGTH_SHORT).show();
            return;
        }

        double target = Double.parseDouble(targetStr);
        double current = currentStr.isEmpty() ? 0 : Double.parseDouble(currentStr);

        if (currentGoal == null) {
            currentGoal = new Goal(name, target, current, selectedDeadline, "#2196F3", "ic_goal");
            viewModel.insert(currentGoal);
        } else {
            currentGoal.name = name;
            currentGoal.targetAmount = target;
            currentGoal.currentAmount = current;
            currentGoal.deadline = selectedDeadline;
            viewModel.update(currentGoal);
        }
        finish();
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        if (selectedDeadline > 0) cal.setTimeInMillis(selectedDeadline);
        
        new DatePickerDialog(this, (view, year, month, day) -> {
            cal.set(year, month, day);
            selectedDeadline = cal.getTimeInMillis();
            etDeadline.setText(DateUtils.formatDate(selectedDeadline));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }
}
