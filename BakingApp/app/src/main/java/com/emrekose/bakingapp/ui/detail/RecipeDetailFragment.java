package com.emrekose.bakingapp.ui.detail;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.emrekose.bakingapp.R;
import com.emrekose.bakingapp.model.RecipeResponse;
import com.emrekose.bakingapp.ui.steps.StepsActivity;
import com.emrekose.bakingapp.ui.steps.StepsFragment;
import com.emrekose.bakingapp.utils.ConfigLayoutSizeUtil;
import com.emrekose.bakingapp.utils.Constants;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import moe.feng.common.stepperview.IStepperAdapter;
import moe.feng.common.stepperview.VerticalStepperItemView;
import moe.feng.common.stepperview.VerticalStepperView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeDetailFragment extends Fragment implements IStepperAdapter {

    @BindView(R.id.recipe_details_ingredients)
    TextView recipeIngredients;

    @BindView(R.id.recipe_steps)
    VerticalStepperView verticalStepperView;

    RecipeResponse response;

    private static final String RECIPE_ARGUMENT = "recipe_arg";

    public RecipeDetailFragment() {
        // Required empty public constructor
    }

    public static RecipeDetailFragment newInstance(RecipeResponse response) {

        Bundle args = new Bundle();
        args.putSerializable(RECIPE_ARGUMENT, response);

        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            response = (RecipeResponse) getArguments().getSerializable(RECIPE_ARGUMENT);
            setIngredients(response);
            verticalStepperView.setStepperAdapter(this);
        }
    }

    private void setIngredients(RecipeResponse response) {
        recipeIngredients.setText("Ingredients\n");

        for (int i = 0; i < response.getIngredients().size(); i++) {
            String ingredient = response.getIngredients().get(i).getIngredient();
            double quantity = response.getIngredients().get(i).getQuantity();
            String measure = response.getIngredients().get(i).getMeasure();
            String formattedValue = (i + 1) + ". " + ingredient + "(" + quantity + " " + measure + ")" + "\n";
            recipeIngredients.append(formattedValue);
        }
    }

    @NonNull
    @Override
    public CharSequence getTitle(int i) {
        return "Step " + (i + 1);
    }

    @Nullable
    @Override
    public CharSequence getSummary(int i) {
        if (i == 0) {
            return Html.fromHtml(String.format("First step", response.getName()));
        } else {
            if (i != size() - 1) {
                return Html.fromHtml(response.getSteps().get(i - 1).getDescription());
            } else {
                return Html.fromHtml(String.format("Last step", response.getName()));
            }
        }
    }

    @Override
    public int size() {
        return response.getIngredients().size();
    }

    @Override
    public View onCreateCustomView(int index, Context context, VerticalStepperItemView parent) {
        View inflateView = LayoutInflater.from(context).inflate(R.layout.vertical_stepper_item, parent, false);

        TextView contentView = inflateView.findViewById(R.id.item_content);
        contentView.setText(response.getSteps().get(index).getDescription());

        Button nextButton = inflateView.findViewById(R.id.button_next);
        nextButton.setOnClickListener(v -> {
            if (verticalStepperView.canNext()) {
                verticalStepperView.nextStep();
            }

            if (ConfigLayoutSizeUtil.isTabletMode(getActivity())){
                if (inflateView.findViewById(R.id.steps_container) == null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.steps_container, StepsFragment.newInstance(response.getSteps().get(index)))
                            .commit();
                }
            } else {
                Intent intent = new Intent(getActivity(), StepsActivity.class);
                intent.putExtra(Constants.STEPS_EXTRA, (Serializable) response.getSteps());
                intent.putExtra(Constants.STEPS_INDEX_EXTRA, index);
                startActivityForResult(intent, Constants.STEPS_REQUEST_CODE);
            }
        });

        Button prevButton = inflateView.findViewById(R.id.button_prev);
        prevButton.setOnClickListener(v -> {
            if (index != 0) {
                verticalStepperView.prevStep();
            } else {
                verticalStepperView.setAnimationEnabled(!verticalStepperView.isAnimationEnabled());
            }
        });

        ImageView videoIcon = inflateView.findViewById(R.id.item_video_icon);
        if (!response.getSteps().get(index).getVideoURL().equals("")) {
            videoIcon.setVisibility(View.VISIBLE);
        } else {
            videoIcon.setVisibility(View.GONE);
        }

        return inflateView;
    }

    @Override
    public void onShow(int i) {

    }

    @Override
    public void onHide(int i) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.STEPS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                int stepIndex = data.getIntExtra(Constants.STEP_INDEX_RESULT, 0);
                verticalStepperView.setCurrentStep(stepIndex);
                verticalStepperView.scrollTo(stepIndex, 0);
            }
        }
    }

}


