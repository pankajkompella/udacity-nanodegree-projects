package com.emrekose.bakingapp.ui.recipes;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.emrekose.bakingapp.R;
import com.emrekose.bakingapp.base.BaseFragment;
import com.emrekose.bakingapp.model.RecipeResponse;
import com.emrekose.bakingapp.ui.detail.RecipeDetailActivity;
import com.emrekose.bakingapp.utils.ColumnUtils;
import com.emrekose.bakingapp.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeListFragment extends BaseFragment implements RecipesMvpView, RecipesRecyclerAdapter.RecipeClickListener {

    @BindView(R.id.recipeListProgressbar)
    ProgressBar progressBar;

    @BindView(R.id.recipeListRecyclerView)
    RecyclerView recyclerView;

    @Inject
    RecipesPresenter presenter;

    RecipesRecyclerAdapter adapter;

    public RecipeListFragment() {
        // Required empty public constructor
    }

    public static RecipeListFragment newInstance() {

        Bundle args = new Bundle();

        RecipeListFragment fragment = new RecipeListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        ButterKnife.bind(this, view);

        presenter.setView(this);

        if (savedInstanceState != null) {
            // TODO: 10.04.2018 handle screen rotation
        } else {
            presenter.getRecipes();
        }

        return view;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_recipe_list;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void renderRecipes(List<RecipeResponse> recipesList) {
        adapter = new RecipesRecyclerAdapter(recipesList, getActivity(), this);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), ColumnUtils.numberOfColumns(getActivity())));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showErrorMessage() {
        Snackbar.make(getActivity().findViewById(R.id.main_layout), "Something went wrong.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showNetworkConnectionError() {
        Snackbar.make(getActivity().findViewById(R.id.main_layout), "Network connection error.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showServerError() {
        Snackbar.make(getActivity().findViewById(R.id.main_layout), "Something went wrong. Server not found", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRecipeClick(RecipeResponse recipeResponse) {
        Intent i = new Intent(getActivity(), RecipeDetailActivity.class);
        i.putExtra(Constants.RECIPES_EXTRA, recipeResponse);
        startActivity(i);
    }
}